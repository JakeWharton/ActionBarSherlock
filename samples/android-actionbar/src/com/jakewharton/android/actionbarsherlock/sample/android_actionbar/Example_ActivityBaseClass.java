package com.jakewharton.android.actionbarsherlock.sample.android_actionbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock;
import com.markupartist.android.widget.ActionBar.IntentAction;

public class Example_ActivityBaseClass extends ActionBarSherlock.Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBarSherlock.from(this)
			.with(savedInstanceState)
			.layout(R.layout.activity_hello)
			.menu(R.menu.hello)
			.title("Hello, ActionBar!")
			.handleCustom(HelloActionBarForAndroidActionBarHandler.class)
			.attach();
	}

	public static final class HelloActionBarForAndroidActionBarHandler extends ActionBarForAndroidActionBar.Handler {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			//Home button won't show unless we partially set it up.
			this.getActionBar().setHomeAction(new IntentAction(this.getActivity(), new Intent(), R.drawable.ic_title_home_default));
			
			Toast.makeText(
					this.getActivity(),
					"Hello, Android-ActionBar ActionBar!",
					Toast.LENGTH_SHORT
			).show();
		}
	}
}
