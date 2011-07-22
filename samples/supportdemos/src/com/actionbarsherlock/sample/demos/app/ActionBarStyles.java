package com.actionbarsherlock.sample.demos.app;

import com.actionbarsherlock.sample.demos.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActionBarStyles extends FragmentActivity {
	private static int THEME = R.style.Theme_Sherlock;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Save")
		    .setIcon(R.drawable.ic_compose)
		    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		menu.add("Search")
	        .setIcon(R.drawable.ic_search)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		menu.add("Refresh")
	        .setIcon(R.drawable.ic_refresh)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(THEME);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actionbar_styles);
		
		((Button)findViewById(R.id.theme_dark)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				THEME = R.style.Theme_Sherlock;
				recreate();
			}
		});
		((Button)findViewById(R.id.theme_light)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				THEME = R.style.Theme_Sherlock_Light;
				recreate();
			}
		});
		((Button)findViewById(R.id.theme_custom)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				THEME = R.style.Theme_SherlockCustom;
				recreate();
			}
		});
	}
}
