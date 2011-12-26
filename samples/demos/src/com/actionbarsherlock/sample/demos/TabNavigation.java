package com.actionbarsherlock.sample.demos;

import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.sample.demos.R;

public class TabNavigation extends SherlockActivity implements ActionBar.TabListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text);
		
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		for (int i = 0; i < 3; i++) {
			ActionBar.Tab tab = getSupportActionBar().newTab();
			tab.setText("Tab " + i);
			tab.setTabListener(this);
			getSupportActionBar().addTab(tab);
		}
	}

	@Override
	public void onTabReselected(Tab tab) {
	}

	@Override
	public void onTabSelected(Tab tab) {
		((TextView)findViewById(R.id.text)).setText("Selected: " + tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab) {
	}
}
