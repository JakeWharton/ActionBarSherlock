package com.actionbarsherlock.sample.demos.app;

import com.actionbarsherlock.sample.demos.R;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.widget.ArrayAdapter;

public class ActionBarListNavigation extends FragmentActivity implements ActionBar.OnNavigationListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(this, R.array.locations, R.layout.abs__simple_spinner_item);
        list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		getSupportActionBar().setListNavigationCallbacks(list, this);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		getSupportFragmentManager()
			.beginTransaction()
			.replace(android.R.id.content, FragmentStackSupport.CountingFragment.newInstance(itemPosition))
			.commit();
		return true;
	}
}
