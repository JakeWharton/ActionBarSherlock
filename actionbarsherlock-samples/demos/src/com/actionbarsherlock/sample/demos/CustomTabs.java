package com.actionbarsherlock.sample.demos;

import java.lang.reflect.Field;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class CustomTabs extends SherlockActivity implements TabListener {
	boolean isLight;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			addTabs(1);
			return false;
		case 1:
			addTabs(2);
			return false;
		case 2:
			addTabs(3);
			return false;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar
		isLight = SampleList.THEME == R.style.Theme_Sherlock_Light;
		menu.add(0, 0, 0, "Custom").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		menu.add(0, 1, 0, "Icons & text").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		menu.add(0, 2, 0, "Icons").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(SampleList.THEME); // Used for theme switching in samples
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text);
		setContent((TextView) findViewById(R.id.text));

		addTabs(0);

		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ex) {
			// Ignore
		}

	}

	private void addTabs(int type) {
		getSupportActionBar().removeAllTabs();
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		switch (type) {
		case 0:
			for (int i = 1; i <= 3; i++) {
				ActionBar.Tab tab = getSupportActionBar().newTab();
				tab.setText("Tab " + i);
				tab.setTabListener(this);
				getSupportActionBar().addTab(tab);
			}
			break;
		case 1:
			for (int i = 1; i <= 3; i++) {
				ActionBar.Tab tab = getSupportActionBar().newTab();
				tab.setCustomView(R.layout.custom_tab);
				tab.setTabListener(this);
				getSupportActionBar().addTab(tab);
			}
			break;
		case 2:
			for (int i = 1; i <= 3; i++) {
				ActionBar.Tab tab = getSupportActionBar().newTab();
				tab.setText("Tab " + i);
				tab.setIcon(isLight ? R.drawable.ic_search
						: R.drawable.ic_search_inverse);
				tab.setTabListener(this);
				getSupportActionBar().addTab(tab);
			}
			break;
		case 3:
			for (int i = 1; i <= 3; i++) {
				ActionBar.Tab tab = getSupportActionBar().newTab();
				tab.setIcon(isLight ? R.drawable.ic_search
						: R.drawable.ic_search_inverse);
				tab.setTabListener(this);
				getSupportActionBar().addTab(tab);
			}
		}
	}

	protected void setContent(TextView view) {
		view.setText(R.string.action_items_content);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// tab.setText("Clicked");
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}

}
