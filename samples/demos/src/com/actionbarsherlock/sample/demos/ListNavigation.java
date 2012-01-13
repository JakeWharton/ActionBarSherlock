package com.actionbarsherlock.sample.demos;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

public class ListNavigation extends SherlockActivity implements ActionBar.OnNavigationListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(this, R.array.locations, android.R.layout.simple_dropdown_item_1line);
        list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getSupportActionBar().setListNavigationCallbacks(list, this);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        ((TextView)findViewById(R.id.text)).setText("Selected: " + itemPosition);
        return true;
    }
}
