package org.mearnag.est;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class Training extends Activity implements OnItemSelectedListener {
	final private String TAG = "Training";

	private String currentLocation = "Home";
	private Spinner loc_spinner;
	private List<String> locations = Arrays.asList("Home","Work","In Transit","Metro Market","Parents","Josh's","Ian's");
	
	public int locationPosition(String location) {
		Iterator<String> i = locations.listIterator();
		String s;
		int r = 0;
		while (i.hasNext()) {
			s = i.next();
			if (location.equals(s))
				break;
			r++;
		}
		return r;
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        if(savedInstanceState != null) {
        	currentLocation = savedInstanceState.getString("currentLocation");
        }
        
        loc_spinner = (Spinner)this.findViewById(R.id.loc_spinner);
        ArrayAdapter<String> spin_adapter = new ArrayAdapter<String>(this,R.layout.row_location,locations);
	    spin_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    Log.v(TAG,"loc_spinner:"+loc_spinner+" spin_adapter:"+spin_adapter);
	    loc_spinner.setAdapter(spin_adapter);
	    loc_spinner.setSelection(locationPosition(currentLocation));
        loc_spinner.setOnItemSelectedListener(this);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentLocation", currentLocation);
    }

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		currentLocation = locations.get(arg2);
		Log.v(TAG,"currentLocation:"+currentLocation);
		Intent intent = new Intent(this, TrainingService.class);
        intent.putExtra("location",currentLocation);
        startService(intent);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}