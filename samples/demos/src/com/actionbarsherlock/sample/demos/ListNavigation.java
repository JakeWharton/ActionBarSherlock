package com.actionbarsherlock.sample.demos;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

public class ListNavigation extends SherlockActivity implements ActionBar.OnNavigationListener {
    private TextView mSelected;
    private String[] mLocations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_navigation);
        mSelected = (TextView)findViewById(R.id.text);

        mLocations = getResources().getStringArray(R.array.locations);

        // It is very important that you use 'sherlock_spinner_item' when using
        // list navigation because before Android 3.0 the built-in layout did
        // not take into account different spinner backgrounds.
        int layoutRes = R.layout.sherlock_spinner_item;
        int dropRes = android.R.layout.simple_spinner_dropdown_item;

        if (SampleList.THEME == R.style.Theme_Sherlock_Light_DarkActionBar) {
             // If you are using a light theme with a dark action bar an additional
             // layout, 'sherlock_spinner_item_light_dark', is provided.
            layoutRes = R.layout.sherlock_spinner_item_light_dark;
            dropRes = R.layout.sherlock_spinner_dropdown_item_light_dark;
        }

        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(this, R.array.locations, layoutRes);
        list.setDropDownViewResource(dropRes);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(list, this);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        mSelected.setText("Selected: " + mLocations[itemPosition]);
        return true;
    }
}
