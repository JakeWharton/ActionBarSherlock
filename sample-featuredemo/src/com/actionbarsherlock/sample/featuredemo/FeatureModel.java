package com.actionbarsherlock.sample.featuredemo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class FeatureModel extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new LinearLayout(this), new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Save")
		    .setIcon(R.drawable.ic_compose)
		    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		
		menu.add("Search")
		    .setIcon(R.drawable.ic_search)
		    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		
		menu.add("Refresh")
		    .setIcon(R.drawable.ic_refresh)
		    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		
		return true;
	}
}
