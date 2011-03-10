package com.jakewharton.android.actionbarsherlock.sample.greendroid;

import com.jakewharton.android.actionbarsherlock.ActionBarSherlock;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

/**
 * Simple activity to demonstrate using GreenDroid as a third-party action bar.
 * 
 * @author Jake Wharton <jakewharton@gmail.com>
 */
public class HelloActionBarActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Attach sherlock and set up the action bar.
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
		//Only inflate the menu if we have a native ActionBar. This would then
		//be applied to that action bar to become the buttons.
		if (ActionBarSherlock.HAS_NATIVE_ACTION_BAR) {
			this.getMenuInflater().inflate(R.menu.hello_honeycomb, menu);
			return true;
		} else {
			return super.onCreateOptionsMenu(menu);
		}
	}


	/**
	 * Extension of the native action bar handler which allows us to display a
	 * {@link android.widget.Toast} upon successful attachment.
	 */
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
	

	/**
	 * Extension of the GreenDroid action bar handler which allows us to
	 * display a {@link android.widget.Toast} upon successful attachment.
	 */
	public static final class HelloGreenDroidActionBarHandler extends GreenDroidActionBar.Handler {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			//TODO: this is where you would set up the buttons
			
			Toast.makeText(
					this.getActivity(),
					"Hello, GreenDroid ActionBar!",
					Toast.LENGTH_SHORT
			).show();
		}
	}
}
