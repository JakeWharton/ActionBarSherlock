package com.jakewharton.android.actionbarsherlock.sample.android_actionbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.ActionBarHandler;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasHomeAsUp;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasMenu;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasTitle;
import com.jakewharton.android.actionbarsherlock.handler.Android_ActionBar;

public class HelloActionBarActivity extends ActionBarSherlock.Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBarHandler<?> handler = ActionBarSherlock.from(this)
			.with(savedInstanceState)
			.layout(R.layout.activity_hello)
			.handleCustom(Android_ActionBar.Handler.class)
			.attach();
		
		if (handler instanceof HasTitle) {
			((HasTitle)handler).setTitle(R.string.hello);
		}
		if (handler instanceof HasMenu) {
			((HasMenu)handler).setMenuResourceId(R.menu.hello);
		}
		if (handler instanceof HasHomeAsUp) {
			((HasHomeAsUp)handler).useHomeAsUp(true);
		}
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
