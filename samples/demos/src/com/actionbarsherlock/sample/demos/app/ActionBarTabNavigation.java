package com.actionbarsherlock.sample.demos.app;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;

public class ActionBarTabNavigation extends SherlockActivity implements ActionBar.TabListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, CountingFragment.newInstance(0))
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
			.replace(android.R.id.content, CountingFragment.newInstance(tab.getPosition()))
			.commit();
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
}
