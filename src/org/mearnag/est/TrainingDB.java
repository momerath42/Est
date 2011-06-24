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
	public static final String ACC = "ACC";
	public static final String LOCATION = "location";
	public static final String[] COLUMNS = {TS,LAT,LON,ACC,LOCATION};
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table est_loc ( " + BaseColumns._ID
		+ " integer primary key autoincrement, ts integer key, lat double, lon double, acc real, location varchar(100));";
		
		Log.v(TAG, "onCreate: executing:" + sql);
		db.execSQL(sql);
	}
	public void insert(Double lon, Double lat, float acc, String explicitLocation) {
		Log.v(TAG,"insert("+lon+","+lat+","+acc+","+explicitLocation+")");
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("ts", System.currentTimeMillis());
		values.put("lon", lon);
		values.put("lat", lat);
		values.put("acc", acc);
		values.put("location", explicitLocation);
		db.insert(TABLE, null, values);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Log.v(TAG, "onUpgrade!? oldVersion:" + oldVersion + " newVersion:" + newVersion);
	}
	public String lastLocation() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE, COLUMNS, null, null, null, null, TS+" DESC");
		String loc;
		if (cursor.getCount() == 0) {
			loc = "Home";
		} else {
			cursor.moveToFirst();
			int location_i = cursor.getColumnIndex(LOCATION);
			loc = cursor.getString(location_i);
			Log.v(TAG,"loc:"+loc+" count:"+cursor.getCount());
		}
    	cursor.close();
    	db.close();
    	return loc;
	}
}
