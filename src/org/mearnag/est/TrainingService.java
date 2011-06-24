package org.mearnag.est;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class TrainingService extends Service implements Listener, LocationListener {

	private static final int ONGOING_NOTIFICATION = 1;
	private LocationManager locman;
	final private static String TAG = "TrainingService";
	private GpsStatus status;
	private double lat;
	private double lon;
	private float acc;
	private String currentLocation = "Unknown";
	private TrainingDB dbh;
	
	public void init() {
		Log.v(TAG,"init()");
		//showNotification();
		
        dbh = new TrainingDB(this);
        currentLocation = dbh.lastLocation();
        
		locman = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locman.addGpsStatusListener(this);
		locman.requestLocationUpdates(locman.getBestProvider(new Criteria(), true), 300000, 0, this);
	}

	private void showNotification() {
		Notification notification = new Notification(R.drawable.icon, "Est thinks you're @"+currentLocation, System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
		Intent notificationIntent = new Intent(this, Training.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, "Est", "the metadiscipline", pendingIntent);
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
		locman.getGpsStatus(status);
		Log.v(TAG,"status:"+status.hashCode());
	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Log.v(TAG,"onLocationChanged");
		lat = location.getLatitude();
		lon = location.getLongitude();
		acc = location.getAccuracy();
		Log.v(TAG,"onLocationChanged lat:"+lat+" lon:"+lon+" acc:"+acc);		
		dbh.insert(lat,lon,acc,currentLocation);
		//showNotification();
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Log.v(TAG,"onProviderDisabled");
	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Log.v(TAG,"onProviderEnabled");
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		Log.v(TAG,"onStatusChanged");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG,"onHandleIntent");
		Bundle e = intent.getExtras();
		if (e != null) {
			Log.v(TAG,"onHandleIntent got extras");
			currentLocation = e.getString("location");
			showNotification();
		}
		if (dbh == null) {
			init();
		}
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
