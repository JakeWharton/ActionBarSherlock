package com.jakewharton.android.actionbarsherlock.sample.android_actionbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock;

public class Example_ActivityBaseClass extends ActionBarSherlock.Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBarSherlock.from(this)
			.with(savedInstanceState)
			.layout(R.layout.activity_hello)
			.menu(R.menu.hello)
			.homeAsUp(true)
			.title(R.string.hello)
			.handleCustom(ActionBarForAndroidActionBar.Handler.class)
			.attach();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Check it out, this works for both 3.0 and pre-3.0!!
		switch (item.getItemId()) {
			case android.R.id.home:
				Toast.makeText(this, "Home Clicked!", Toast.LENGTH_SHORT).show();
				return true;
			case R.id.menu_compose:
				Toast.makeText(this, "Compose Clicked!", Toast.LENGTH_SHORT).show();
				return true;
			case R.id.menu_refresh:
				Toast.makeText(this, "Refresh Clicked!", Toast.LENGTH_SHORT).show();
				return true;
			case R.id.menu_search:
				Toast.makeText(this, "Search Clicked!", Toast.LENGTH_SHORT).show();
				return true;
			default:
				return false;
		}
	}
}
