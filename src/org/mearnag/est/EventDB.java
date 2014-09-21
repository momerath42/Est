package org.mearnag.est;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class EventDB extends SQLiteOpenHelper {
	public EventDB(Context context) {
		super(context, "est.db", null, 1);
	}
	public static final String TAG = "EventDB";
	public static final String TABLE = "est_event";
	public static final String TS = "ts";
	public static final String EVENT_ID = "event_id";
	public static final String EVENT_TYPE = "event_type";
	public static final String[] COLUMNS = {TS,EVENT_ID,EVENT_TYPE};
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table est_event ( " + BaseColumns._ID
		+ " integer primary key autoincrement, ts integer key, event_id integer key, event_type integer key);";
		
		Log.v(TAG, "onCreate: executing:" + sql);
		db.execSQL(sql);
	}
	public void insert(int event, int event_type) {
		Log.v(TAG,"insert("+event+")");
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("ts", System.currentTimeMillis());
		values.put("event_id", event);
		values.put("event_type", event_type);
		db.insert(TABLE, null, values);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Log.v(TAG, "onUpgrade!? oldVersion:" + oldVersion + " newVersion:" + newVersion);
	}
//	public String lastLocation() {
//		SQLiteDatabase db = getReadableDatabase();
//		Cursor cursor = db.query(TABLE, COLUMNS, null, null, null, null, TS+" DESC");
//		String loc;
//		if (cursor.getCount() == 0) {
//			loc = "Home";
//		} else {
//			cursor.moveToFirst();
//			int location_i = cursor.getColumnIndex(LOCATION);
//			loc = cursor.getString(location_i);
//			Log.v(TAG,"loc:"+loc+" count:"+cursor.getCount());
//		}
//    	cursor.close();
//    	db.close();
//    	return loc;
//	}
}
