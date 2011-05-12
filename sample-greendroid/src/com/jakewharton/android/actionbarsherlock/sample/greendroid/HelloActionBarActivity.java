package com.jakewharton.android.actionbarsherlock.sample.greendroid;

import greendroid.widget.GDActionBarItem;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.ActionBarHandler;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasTitle;
import com.jakewharton.android.actionbarsherlock.handler.GreenDroid;
import com.jakewharton.android.actionbarsherlock.handler.NativeActionBar;
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
		ActionBarHandler<?> handler = ActionBarSherlock.from(this)
			.with(savedInstanceState)
			.layout(R.layout.activity_hello)
			.handleNative(HelloNativeActionBarHandler.class)
			.handleCustom(HelloGreenDroidActionBarHandler.class)
			.attach();
		
		if (handler instanceof HasTitle) {
			((HasTitle)handler).setTitle("Hello, Action Bar!");
		}
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
	public static final class HelloNativeActionBarHandler extends NativeActionBar.Handler {
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
	public static final class HelloGreenDroidActionBarHandler extends GreenDroid.Handler {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			this.getActionBar().addItem(GDActionBarItem.Type.Compose);
			this.getActionBar().addItem(GDActionBarItem.Type.Search);
			this.getActionBar().addItem(GDActionBarItem.Type.Refresh);
			
			Toast.makeText(
					this.getActivity(),
					"Hello, GreenDroid ActionBar!",
					Toast.LENGTH_SHORT
			).show();
		}
	}
}
