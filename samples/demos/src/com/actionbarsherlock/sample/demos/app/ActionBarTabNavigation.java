package com.actionbarsherlock.sample.demos.app;

import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ActionBar.Tab;

public class ActionBarTabNavigation extends FragmentActivity implements ActionBar.TabListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, FragmentStackSupport.CountingFragment.newInstance(0))
			.commit();
		
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		for (int i = 0; i < 3; i++) {
			ActionBar.Tab tab = getSupportActionBar().newTab();
			tab.setText("Tab " + i);
			tab.setTabListener(this);
			getSupportActionBar().addTab(tab);
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		getSupportFragmentManager()
			.beginTransaction()
			.replace(android.R.id.content, FragmentStackSupport.CountingFragment.newInstance(tab.getPosition()))
			.commit();
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
}
