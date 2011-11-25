package com.actionbarsherlock.sample.plugins.app;

import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.sample.plugins.R;
import com.google.android.maps.MapView;
import android.os.Bundle;
import android.widget.Toast;

public class MapSimple extends SherlockMapActivity {
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
