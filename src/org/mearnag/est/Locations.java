package org.mearnag.est;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class Locations extends SQLiteOpenHelper {
	public Locations(Context context) {
		super(context, "est_meta.db", null, 1);
	}
	public static final String TAG = "Locations";
	public static final String TABLE = "est_locations";
	public static final String TS = "ts";
	public static final String NAME = "name";
	public static final String TYPE = "location_type";
	public static final String ICON = "icon";
	public static final String[] COLUMNS = {TS,NAME,TYPE};
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table est_locations ( " + BaseColumns._ID
		+ " integer primary key autoincrement, ts integer key, name varchar(100), location_type varchar(100), icon varchar(150));";
		
		Log.v(TAG, "onCreate: executing:" + sql);
		db.execSQL(sql);
		//db.close();
		insertDefault(db,"Home","room","home");
		insertDefault(db,"Home (leaving)","room","home");
		insertDefault(db,"In Transit","non-place","transit");
		insertDefault(db,"Work","room","work");
		insertDefault(db,"Grocery Store","building","store");

	}
	public void insertDefault(SQLiteDatabase db, String name, String type, String icon) {
		Log.v(TAG,"insert("+name+","+type+","+icon+")");
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("location_type", type);
		values.put("icon", icon);
		db.insert(TABLE, null, values);
	}
	public void insert(String name, String type, String icon) {
		Log.v(TAG,"insert("+name+","+type+","+icon+")");
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("location_type", type);
		values.put("icon", icon);
		db.insert(TABLE, null, values);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Log.v(TAG, "onUpgrade!? oldVersion:" + oldVersion + " newVersion:" + newVersion);
	}
	//public List<String> locations() {
	public String[] locations() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE, COLUMNS, null, null, null, null, null);
		String loc;
		//List<String> locs = new ArrayList<String>();
		String[] locs = new String[cursor.getCount()];
		if (cursor.getCount() == 0) {
			Log.e(TAG,"locations found nothing!");
		} else {
			cursor.moveToFirst();
			int location_i = cursor.getColumnIndex(NAME);
			loc = cursor.getString(location_i);
			Log.v(TAG,"loc:"+loc+" count:"+cursor.getCount());
			//locs.add(loc);
			locs[cursor.getPosition()] = loc;
			while (cursor.moveToNext()) {
				loc = cursor.getString(location_i);
				Log.v(TAG,"loc:"+loc+" count:"+cursor.getCount());
				locs[cursor.getPosition()] = loc;
				//locs.add(loc);
			}
		}
    	cursor.close();
    	db.close();
    	return locs;
	}
}
