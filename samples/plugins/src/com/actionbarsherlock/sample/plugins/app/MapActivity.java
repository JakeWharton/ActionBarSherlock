package com.actionbarsherlock.sample.plugins.app;

import com.actionbarsherlock.sample.plugins.R;
import com.google.android.maps.MapView;
import android.os.Bundle;
import android.support.v4.app.FragmentMapActivity;
import android.widget.Toast;

public class MapActivity extends FragmentMapActivity {
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
    protected boolean isRouteDisplayed() {
        return false;
    }
}
