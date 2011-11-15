package com.actionbarsherlock.tests.app;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public final class FeatureEnableActionItemText extends SherlockActivity {
	public static final String MENU_ITEM_TEXT = "Item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blank);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(MENU_ITEM_TEXT).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}
}
