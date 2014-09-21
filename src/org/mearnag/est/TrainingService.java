package org.mearnag.est;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class TrainingService extends Service implements Listener, LocationListener, SensorEventListener {

	private static final int ONGOING_NOTIFICATION = 1;
	private LocationManager locman;
	final private static String TAG = "TrainingService";
	private GpsStatus status;
	private double lat;
	private double lon;
	private double dlat;
	private double dlon;
	private float acc;
	private String currentLocation = "Unknown";
	private TrainingDB locationDB;
	private EventDB eventDB;
	private String ssid;
	private int sstrength;
    private int isPluggedIn = 0;
	private int level = 0;
	private String[] locationLookup = {"Home","In Transit","Metro Market"};
	
	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
		@Override
	    public void onReceive(Context arg0, Intent intent) {
			level = intent.getIntExtra("level", 0);
			isPluggedIn = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
	    }
	};
	private WekaWrapper ww;
	private LocationClassifier lc;
	private int guessedLocation;
	private int nsats;
	private String locsource;
	private String loctype;
	private int lastEventID;
	private int lastEventType;
	private AudioManager audiomanager;
	private boolean hphones_wired;
	private boolean hphones_bt;
	private int audio_mode;
	private int ringer_mode;
	private SensorManager sensor_manager;
	private Sensor sensor_accel;
	private List<Sensor> sensor_list;
	private Sensor sensor_mag;
	private float mag_x;
	private float mag_y;
	private float mag_z;
	private String lastExplicitLocation;
	private ActivityManager activitymanager;
	private String[] task_history_packages = new String[TASK_HISTORY_DEPTH];
	private int[] task_history_flags = new int[TASK_HISTORY_DEPTH];
	
	private final static int LOC_INTERVAL = 30000; // milliseconds
	private static final int DECLARE_LOCATION = 0;
	private static final int DECLARE_EVENT = 1;
	public static final int TASK_HISTORY_DEPTH = 5;

	public void init() {
		Log.v(TAG,"init()");
		//showNotification();
		
        locationDB = new TrainingDB(this);
        currentLocation = locationDB.lastLocation();
        eventDB = new EventDB(this);
        
		locman = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//locman.addGpsStatusListener(this);
		requestLocUpdates();
		 
		this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		audiomanager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		
		activitymanager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		
		sensor_manager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor_accel = sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //sensor_manager.registerListener(this, sensor_accel, SensorManager.SENSOR_DELAY_NORMAL);
        sensor_mag = sensor_manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensor_manager.registerListener(this, sensor_mag, SensorManager.SENSOR_DELAY_NORMAL);
        
        sensor_list = sensor_manager.getSensorList(Sensor.TYPE_ALL);
        Log.v(TAG,"sensors:"+sensor_list);
        Iterator<Sensor> i = sensor_list.iterator();
        Sensor s;
        while (i.hasNext()) {
        	s = i.next();
        	Log.v(TAG,"sensor:"+s+" name:"+s.getName()+" type:"+s.getType());
        }
//		ww = new WekaWrapper();
//		Instance i;
//		i.
//		ww.classifyInstance(i);
		//lc = new LocationClassifier();
	}

	private void showNotification() {
		Notification notification = new Notification(R.drawable.icon, "Est thinks you're @"+locationLookup[guessedLocation]+" last-explicit:"+currentLocation, System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
		Intent notificationIntent = new Intent(this, Training.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, "Est: the meta-discipline", "(I:"+locationLookup[guessedLocation]+")"
		                                               +" (E:"+currentLocation+") (LE:"+lastExplicitLocation+")",
		                                pendingIntent);
		startForeground(ONGOING_NOTIFICATION, notification);
	}

    @Override
	public void onGpsStatusChanged(int event) {
		switch (event) {
		case(GpsStatus.GPS_EVENT_STARTED):
			Log.v(TAG,"GPS_EVENT_STARTED");
			break;
		case(GpsStatus.GPS_EVENT_STOPPED):
			Log.v(TAG,"GPS_EVENT_STOPPED");
			break;
		case(GpsStatus.GPS_EVENT_FIRST_FIX):
			Log.v(TAG,"GPS_EVENT_FIRST_FIX");
			break;
		case(GpsStatus.GPS_EVENT_SATELLITE_STATUS):
			Log.v(TAG,"GPS_EVENT_SATELLITE_STATUS");
			break;
		}
		if (status == null) {
			status = locman.getGpsStatus(status);
			//Log.v(TAG,"status:"+status.hashCode());
		} else {
			locman.getGpsStatus(status);
			//Log.v(TAG,"status:"+status.hashCode());
		}
		Iterable<GpsSatellite> satellites = status.getSatellites();
		Iterator<GpsSatellite> i = satellites.iterator();
		GpsSatellite sat;
		nsats = 0;
		while (i.hasNext()) {
			sat = i.next();
			//sat.
			nsats++;
		}
	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Log.v(TAG,"onLocationChanged");
		Double newlat = location.getLatitude();
		Double newlon = location.getLongitude();
		dlat = lat - newlat;
		dlon = lon - newlon;
		lat = newlat;
		lon = newlon;
		acc = location.getAccuracy();
		Bundle b = location.getExtras();
		Set<String> keys = b.keySet();
		Iterator<String> i = keys.iterator();
		String key;
		while(i.hasNext()) {
			key = i.next();
			Log.v(TAG,"key:"+key+" val:"+b.get(key));
		}
		//nsats = b.getInt("satellites");
		locsource = b.getString("networkLocationSource");
		loctype = b.getString("networkLocationType");
		Log.v(TAG,"locsource:"+locsource+" loctype:"+loctype+" nsats:"+nsats);
		
		getWifiInfo();
		getAudioInfo();
		getTaskInfo();
		
		int newGuess = (int)LocationClassifier.classify(lat,lon,acc,ssid,sstrength,isPluggedIn,level);
		locationDB.insert(lat,lon,dlat,dlon,acc,ssid,sstrength,isPluggedIn,level,nsats,
							locsource,loctype,hphones_wired,hphones_bt,audio_mode,ringer_mode,
							mag_x,mag_y,mag_z,lastExplicitLocation,currentLocation,guessedLocation,
							task_history_packages,task_history_flags);
		if (newGuess != guessedLocation) {
			guessedLocation = newGuess;
			showNotification();
		}
	}
	private void getAudioInfo() {
		hphones_wired = audiomanager.isWiredHeadsetOn();
		hphones_bt = audiomanager.isBluetoothA2dpOn();
		audio_mode = audiomanager.getMode();
		ringer_mode = audiomanager.getRingerMode();
		Log.v(TAG,"getAudioInfo() isMusicActive:"+audiomanager.isMusicActive());
	}
	private void getTaskInfo() {
		List<RecentTaskInfo> l = activitymanager.getRecentTasks(TASK_HISTORY_DEPTH, ActivityManager.RECENT_WITH_EXCLUDED);
		RecentTaskInfo ti;
		Intent bi;
		int ti_contents;
		String ti_package;
		Iterator<RecentTaskInfo> i = l.iterator();
		int task_id = 0;
		while (i.hasNext()) {
			ti = i.next();
			ti_contents = ti.describeContents();
			bi = ti.baseIntent;
			ti_package = bi.getPackage();
			task_history_packages[task_id] = ti_package;
			task_history_flags[task_id] = ti_contents;
			task_id++;
		}
	}
	private void getWifiInfo() {
		Log.v(TAG,"onLocationChanged lat:"+lat+" lon:"+lon+" acc:"+acc);
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        ssid = wifiInfo.getSSID();
        sstrength = wifiInfo.getRssi();
	}
	
	@Override
	public void onProviderDisabled(String provider) {
		Log.v(TAG,"onProviderDisabled: "+provider);
		requestLocUpdates();
	}
	@Override
	public void onProviderEnabled(String provider) {
		Log.v(TAG,"onProviderEnabled: "+provider);
		requestLocUpdates();
	}
	private void requestLocUpdates() {
		nsats = 0;
//		List<String> providers = locman.getAllProviders();
//		locman.removeGpsStatusListener(this);
//		locman.removeUpdates(this);
//		locman.addGpsStatusListener(this);
//		String provider;
//		Iterator<String> i = providers.iterator();
//		while (i.hasNext()) {
//			provider = i.next();
//			Log.v(TAG,"requesting updates from: "+provider);
//			locman.requestLocationUpdates(provider, LOC_INTERVAL, 0, this);
//		}
		String provider = locman.getBestProvider(new Criteria(), true);
		Log.v(TAG,"requestLocUpdates() provider:"+provider);
		if (provider != null) {
			locman.requestLocationUpdates(provider, LOC_INTERVAL, 0, this);
		}
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.v(TAG,"onStatusChanged: "+provider+" status:"+status);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG,"onHandleIntent");
		if (intent == null)
			return START_STICKY;
		Bundle e = intent.getExtras();
		if (e != null) {
			Log.v(TAG,"onHandleIntent got extras");
			switch (e.getInt("action")) {
			case(DECLARE_LOCATION):
				Log.v(TAG,"getting locationLookup");
				locationLookup = e.getStringArray("locations");
				String newLoc = e.getString("location");
				if (newLoc.equals(currentLocation)) {
					Log.v(TAG,"explicit loc:"+newLoc+" is where the service already thinks it is!");
				} else {
					lastExplicitLocation = currentLocation;
					currentLocation = newLoc;
					showNotification();
				}
				break;
			case(DECLARE_EVENT):
				lastEventID = e.getInt("event_id");
				lastEventType = e.getInt("event_type");
				eventDB.insert(lastEventID, lastEventType);
				break;
			}
		}
		if (locationDB == null) {
			init();
		}
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Log.v(TAG,"onAccuracyChanged sensor:"+sensor+" acc:"+accuracy);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		//Log.v(TAG,"onSensorChanged sensor:"+event.sensor+" acc:"+event.accuracy+" vals:"+event.values);
		if (event.sensor == sensor_mag) {
			//Log.v(TAG,"onSensorChanged magnetic field: x:"+event.values[0]+" y:"+event.values[1]+" z:"+event.values[2]);
			mag_x = event.values[0];
			mag_y = event.values[1];
			mag_z = event.values[2];
		}
	}
}
