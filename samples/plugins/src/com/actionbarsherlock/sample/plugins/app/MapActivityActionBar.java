package com.actionbarsherlock.sample.plugins.app;

import com.actionbarsherlock.sample.plugins.R;
import com.google.android.maps.MapView;
import android.os.Bundle;
import android.support.v4.app.FragmentMapActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.widget.Toast;

public class MapActivityActionBar extends FragmentMapActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //MapView for the lose :(
        Toast.makeText(this, R.string.map_warning, Toast.LENGTH_LONG).show();

        setContentView(R.layout.map_simple);
        MapView mapView = (MapView)findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Save")
		    .setIcon(R.drawable.ic_compose)
		    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		menu.add("Search")
	        .setIcon(R.drawable.ic_search)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		menu.add("Refresh")
	        .setIcon(R.drawable.ic_refresh)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		return super.onCreateOptionsMenu(menu);
	}

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}
