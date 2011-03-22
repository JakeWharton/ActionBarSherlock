package com.jakewharton.android.actionbarsherlock.sample.android_actionbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock;

public class HelloActionBarActivity extends ActionBarSherlock.Activity {
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
				this.toast("Home");
				return true;
			
			case R.id.menu_refresh:
			case R.id.menu_search:
			case R.id.menu_compose_sms:
			case R.id.menu_compose_mms:
			case R.id.menu_compose_email:
			case R.id.menu_compose_gmail:
				this.toast(item.getTitle());
				return true;
			
			default:
				return false;
		}
	}
	
	private void toast(CharSequence title) {
		Toast.makeText(this, "\"" + title + "\" Clicked!", Toast.LENGTH_SHORT).show();
	}
}
