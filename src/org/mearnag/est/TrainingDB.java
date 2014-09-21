package org.mearnag.est;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class TrainingDB extends SQLiteOpenHelper {
	public TrainingDB(Context context) {
		super(context, "est.db", null, 1);
	}
	public static final String TAG = "TrainingDB";
	public static final String TABLE = "est_loc";
	public static final String TS = "ts";
	public static final String LAT = "lat";
	public static final String LON = "lon";
	public static final String DLAT = "dlat";
	public static final String DLON = "dlon";
	public static final String ACC = "ACC";
	public static final String SSID = "ssid";
	public static final String SSTRENGTH = "sstrength";
	public static final String ISPLUGGEDIN = "isPluggedIn";
	public static final String BATLEVEL = "batLevel";
	public static final String NSATS = "nsats";
	public static final String LOCSOURCE = "locsource"; 
	public static final String LOCTYPE = "loctype"; 
	public static final String HPHONES_WIRED = "hphones_wired";
	public static final String HPHONES_BT = "hphones_bt";
	public static final String AUDIO_MODE = "audio_mode";
	public static final String RINGER_MODE = "ringer_mode";
	public static final String MAG_X = "mag_x";
	public static final String MAG_Y = "mag_y";
	public static final String MAG_Z = "mag_z";
	public static final String LAST_EXPLICIT_LOCATION = "lastExplicitLocation";
	public static final String EXPLICIT_LOCATION = "explicitLocation";
	public static final String INFERRED_LOCATION = "inferredLocation";	
	public static final String[] MOST_COLUMNS = {TS,LAT,LON,DLAT,DLON,ACC,SSID,SSTRENGTH,ISPLUGGEDIN,
		BATLEVEL,NSATS,LOCSOURCE,LOCTYPE,HPHONES_WIRED,HPHONES_BT,AUDIO_MODE,RINGER_MODE,MAG_X,MAG_Y,MAG_Z,LAST_EXPLICIT_LOCATION,EXPLICIT_LOCATION,INFERRED_LOCATION};
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String task_history_create_string = "";
		for(int i=0;i<TrainingService.TASK_HISTORY_DEPTH;i++) {
			task_history_create_string = task_history_create_string
				+", thp_"+i+" varchar(100), thf_"+i+" integer ";
		}
		String sql = "create table est_loc ( " + BaseColumns._ID
		+ " integer primary key autoincrement, ts integer key, lat double, lon double, dlat double, dlon double, "
		+ " acc real, ssid varchar(100), sstrength integer, "
		+ " isPluggedIn integer, batLevel integer, "
		+ " nsats integer, locsource varchar(100), loctype varchar(100), "
		+ " hphones_wired boolean, hphones_bt boolean, "
		+ " audio_mode integer, ringer_mode integer, "
		+ " mag_x real, mag_y real, mag_z real, "
		+ " lastExplicitLocation varchar(100), explicitLocation varchar(100), "
		+ " inferredLocation integer "
		+ task_history_create_string
		+ ")";
		Log.v(TAG, "onCreate: executing:" + sql);
		db.execSQL(sql);
	}
	public void insert(Double lat, Double lon, Double dlat, Double dlon, float acc, String ssid, int sstrength, int isPluggedIn,
			int batLevel, int nsats, String locsource, String loctype, Boolean hphones_wired, Boolean hphones_bt, int audio_mode, int ringer_mode,
			float mag_x, float mag_y, float mag_z, String lastExplicitLocation, String explicitLocation, int guessedLocation,
			String[] task_history_packages, int[] task_history_flags) {
		Log.v(TAG,"insert("+lat+","+lon+","+dlat+","+dlon+","+acc+","
				+ssid+","+sstrength+","+isPluggedIn+","
				+batLevel+","+nsats+","+locsource+","+loctype+","
				+hphones_wired+","+hphones_bt+","+audio_mode+","+ringer_mode+","
				+mag_x+","+mag_y+","+mag_z+","
				+lastExplicitLocation+","+explicitLocation+","+guessedLocation+","
				+task_history_packages+","+task_history_flags+")");
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("ts", System.currentTimeMillis());
		values.put("lon", lon);
		values.put("lat", lat);
		values.put("dlat", dlat);
		values.put("dlon", dlon);
		values.put("acc", acc);
		values.put("ssid", ssid);
		values.put("sstrength", sstrength);
		values.put("isPluggedIn", isPluggedIn);
		values.put("batLevel", batLevel);
		values.put("nsats", nsats);
		values.put("locsource",locsource);
		values.put("loctype", loctype);
		values.put("hphones_wired", hphones_wired);
		values.put("hphones_bt",hphones_bt);
		values.put("audio_mode", audio_mode);
		values.put("ringer_mode", ringer_mode);
		values.put("mag_x", mag_x);
		values.put("mag_y", mag_y);
		values.put("mag_z", mag_z);
		values.put("lastExplicitLocation", lastExplicitLocation);
		values.put("explicitLocation", explicitLocation);
		values.put("inferredLocation", guessedLocation);
		for(int i=0;i<TrainingService.TASK_HISTORY_DEPTH;i++) {
			values.put("thp_"+i,task_history_packages[i]);
			values.put("thf_"+i,task_history_flags[i]);
		}
		db.insert(TABLE, null, values);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Log.v(TAG, "onUpgrade!? oldVersion:" + oldVersion + " newVersion:" + newVersion);
	}
	public String lastLocation() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE, MOST_COLUMNS, null, null, null, null, TS+" DESC");
		String loc;
		if (cursor.getCount() == 0) {
			loc = "Home";
		} else {
			cursor.moveToFirst();
			int location_i = cursor.getColumnIndex(EXPLICIT_LOCATION);
			loc = cursor.getString(location_i);
			Log.v(TAG,"loc:"+loc+" count:"+cursor.getCount());
		}
    	cursor.close();
    	db.close();
    	return loc;
	}
}
