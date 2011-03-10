package com.jakewharton.android.actionbarsherlock.sample.greendroid;

import com.jakewharton.android.actionbarsherlock.ActionBarSherlock;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

public class HelloActionBarActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBarSherlock.from(this)
			.with(savedInstanceState)
			.layout(R.layout.activity_hello)
		    .title("Hello, ActionBar!")
			.handleNative(HelloNativeActionBarHandler.class)
		    .handleCustom(GreenDroidActionBar.Handler.class)
			.attach();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (ActionBarSherlock.HAS_NATIVE_ACTION_BAR) {
			this.getMenuInflater().inflate(R.menu.hello_honeycomb, menu);
			return true;
		} else {
			return super.onCreateOptionsMenu(menu);
		}
	}

	
	public static final class HelloNativeActionBarHandler extends ActionBarSherlock.NativeActionBarHandler {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			Toast.makeText(
					this.getActivity(),
					"Hello, Native ActionBar!",
					Toast.LENGTH_SHORT
			).show();
		}
	}
}
