package com.actionbarsherlock.sample.featuredemo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.View;

public class ActionItemFeature extends FragmentActivity {
	private MenuItem mFirst;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.action_item_feature_activity);
		
		findViewById(R.id.display_item_1_show).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFirst.setVisible(true);
			}
		});
		findViewById(R.id.display_item_1_hide).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFirst.setVisible(false);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		mFirst = menu.add("First");
		mFirst.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		return true;
	}

}
