package org.mearnag.est;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class Training extends Activity implements OnItemSelectedListener {
	private static final int MANAGE_LOCATIONS_RESULT = 1;

	final private String TAG = "Training";

	private String currentLocation = "Home";
	private Spinner loc_spinner;
	//private List<String> locations;// = Arrays.asList("Home","Work","In Transit","Metro Market","Josh's","Ian's","Parents","Downtown Books","Mai Thai's");
	private String[] locations;
	
	private Locations ldb;
	
	public int locationPosition(String location) {
		//Iterator<String> i = locations.listIterator();
		int l = locations.length;
		String s;
		int r = 0;
		//while (i.hasNext()) {
		for (; r < l; r++) {
			s = locations[r];//i.next();
			//Log.v(TAG,"locationPosition("+location+") == "+s+": "+location.equals(s));
			if (location.equals(s))
				break;
			//r++;
		}
		Log.v(TAG,"locationPosition("+location+") returning:"+r);
		return r;
	}
	@Override
	public void onRestart() {
		super.onRestart();
		Log.v(TAG,"onRestart()");
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(TAG,"onActivityResult("+requestCode+","+resultCode+",_)");
		super.onActivityResult(requestCode, resultCode, data);
		resetLocationSpinner();
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Log.v(TAG,"onCreate("+savedInstanceState+")");
        if(savedInstanceState != null) {
        	currentLocation = savedInstanceState.getString("currentLocation");
        	Log.v(TAG,"currentLocation:"+currentLocation);
        } else {
        	SharedPreferences settings = getPreferences(0); // MODE_PRIVATE
    		currentLocation = settings.getString("currentLocation", "Home");
        }
        ldb = new Locations(this);
        resetLocationSpinner();
    }
	private void resetLocationSpinner() {
		locations = ldb.locations();
        
        loc_spinner = (Spinner)findViewById(R.id.loc_spinner);
        Log.v(TAG,"loc_spinner:"+loc_spinner+" locations:"+locations);
        ArrayAdapter<String> spin_adapter = new ArrayAdapter<String>(this,R.layout.row_location,locations);
        Log.v(TAG,"spin_adapter:"+spin_adapter);
	    spin_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    Log.v(TAG,"loc_spinner:"+loc_spinner+" spin_adapter:"+spin_adapter);
	    loc_spinner.setAdapter(spin_adapter);
	    Log.v(TAG,"setSelection("+locationPosition(currentLocation));
	    loc_spinner.setSelection(locationPosition(currentLocation));
        loc_spinner.setOnItemSelectedListener(this);
	}
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentLocation", currentLocation);
        Log.v(TAG,"onSaveInstanceState outState:"+outState+" cl:"+currentLocation);
    }

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		currentLocation = locations[arg2];//locations.get(arg2);
		Log.v(TAG,"currentLocation:"+currentLocation);
		Intent intent = new Intent(this, TrainingService.class);
        intent.putExtra("location",currentLocation);
        intent.putExtra("locations", locations);//(String[])locations.toArray());
        startService(intent);
        SharedPreferences settings = getPreferences(0); // MODE_PRIVATE
        Editor e = settings.edit();
        e.putString("currentLocation", currentLocation);
        e.commit();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.settings:
				showSettings();
				return true;
			case R.id.manage_locations:
				showManageLocations();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	private void showManageLocations() {
		Log.v(TAG,"showManageLocations");
		Intent manageLocationsActivity = new Intent(getBaseContext(),ManageLocations.class);
		////Log.v(TAG,"got bookmarksActivity intent");
		startActivityForResult(manageLocationsActivity, MANAGE_LOCATIONS_RESULT);
	}
	private void showSettings() {
		Log.v(TAG,"showSettings");
	}
}