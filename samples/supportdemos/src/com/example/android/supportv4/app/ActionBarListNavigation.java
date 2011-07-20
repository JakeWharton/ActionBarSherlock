package com.example.android.supportv4.app;

import com.example.android.supportv4.R;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.widget.ArrayAdapter;

public class ActionBarListNavigation extends FragmentActivity implements ActionBar.OnNavigationListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//WARNING: This should normally not be needed as calling setContentView
		//or attaching a fragment to android.R.id.content will call this. In
		//this case, however, we call it manually since initializing the list
		//navigation will trigger a navigation changed callback and thus attach
		//the default fragment as the content.
		ensureSupportActionBarAttached();
		
		
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(this, R.array.locations, R.layout.simple_spinner_item);
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
