package com.jakewharton.android.actionbarsherlock.sample;

import greendroid.widget.GDActionBarItem;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

public class HelloActionBarActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBarSherlock.newInstance()
			.setActivity(this, savedInstanceState)
			.setLayout(R.layout.activity_hello)
			.setTitle("Hello, ActionBar!")
			.setHoneycombHandler(HelloHoneycombActionBarHandler.class)
			.setPreHoneycombHandler(HelloPreHoneycombActionBarHandler.class);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.HONEYCOMB) {
			return super.onCreateOptionsMenu(menu);
		} else {
			this.getMenuInflater().inflate(R.menu.hello_honeycomb, menu);
			return true;
		}
	}

	public static final class HelloHoneycombActionBarHandler extends ActionBarSherlock.HoneycombActionBarHandler {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			Toast.makeText(
					this.getActivity(),
					"Hello, Honeycomb ActionBar!",
					Toast.LENGTH_SHORT
			).show();
		}
	}

	public static final class HelloPreHoneycombActionBarHandler extends ActionBarSherlock.PreHoneycombActionBarHandler {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			Toast.makeText(
					this.getActivity(),
					"Hello, Pre-Honeycomb ActionBar!",
					Toast.LENGTH_SHORT
			).show();
			
			this.getActionBar().addItem(GDActionBarItem.Type.Compose);
			this.getActionBar().addItem(GDActionBarItem.Type.Search);
			this.getActionBar().addItem(GDActionBarItem.Type.Refresh);
		}
	}
}
