package com.actionbarsherlock.tests.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.Window;

public final class FeatureEnableActionItemText extends FragmentActivity {
	public static final String MENU_ITEM_TEXT = "Item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ENABLE_ACTION_BAR_WATSON_TEXT);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blank);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(MENU_ITEM_TEXT).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}
}
