package org.mearnag.est;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ManageLocations extends Activity implements OnItemLongClickListener, OnClickListener {
    private ListView lv_locations;
	private Locations ldb;
	//private List<String> locations;
	private String[] locations;
	private EditText et_new_loc;
	private static final String TAG = "ManageLocations";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_locations);
        lv_locations = (ListView)findViewById(R.id.lv_locations);
        ldb = new Locations(this);
        setLocations();
        lv_locations.setOnItemLongClickListener(this);
        
        Button b = (Button)findViewById(R.id.b_add_location);
        b.setOnClickListener(this);
        
        et_new_loc = (EditText)findViewById(R.id.et_new_location); 
    }

	private void setLocations() {
		locations = ldb.locations();
        ArrayAdapter<String> loc_adapter = new ArrayAdapter<String>(this,R.layout.row_location,locations);
        lv_locations.setAdapter(loc_adapter);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		Toast.makeText(getBaseContext(),
				"ToDo",
				Toast.LENGTH_LONG).show();
		Log.v(TAG,"onItemLongClick(_,"+arg1+","+arg2+")");
		return false;
	}

	@Override
	public void onClick(View v) {
		Toast.makeText(getBaseContext(),
				"Adding Location",
				Toast.LENGTH_LONG).show();
		ldb.insert(et_new_loc.getText().toString(), "todo", "todo");
		setLocations();		
	}
}
