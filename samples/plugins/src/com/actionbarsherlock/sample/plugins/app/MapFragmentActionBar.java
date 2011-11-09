package com.actionbarsherlock.sample.plugins.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentMapActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.actionbarsherlock.sample.plugins.R;
import com.google.android.maps.MapView;

public class MapFragmentActionBar extends FragmentMapActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //MapView for the lose :(
        Toast.makeText(this, R.string.map_warning, Toast.LENGTH_LONG).show();

        getSupportFragmentManager()
        	.beginTransaction()
        	.add(android.R.id.content, new MyMapFragment())
        	.commit();
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
    
    public static final class MyMapFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.map_simple, container, false);
			MapView mapView = (MapView)view.findViewById(R.id.mapview);
			mapView.setBuiltInZoomControls(true);
			return view;
		}
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}
