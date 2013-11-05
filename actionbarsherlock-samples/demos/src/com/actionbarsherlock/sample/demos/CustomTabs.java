package com.actionbarsherlock.sample.demos;

import java.lang.reflect.Field;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class CustomTabs extends SherlockActivity implements TabListener {
	boolean isLight;
	TextView mSelected;
	int type = -1;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			addTabs(1);
			return false;
		case 1:
			addTabs(2);
			return false;
		case 2:
			addTabs(3);
			return false;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar
		isLight = SampleList.THEME == R.style.Theme_Sherlock_Light;
		menu.add(0, 0, 0, "Custom").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		menu.add(0, 1, 0, "Icons & text").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		menu.add(0, 2, 0, "Icons").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(SampleList.THEME); // Used for theme switching in samples
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_tab_navigation);

		mSelected = (TextView) findViewById(R.id.text);

		addTabs(0);

		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * 
	 * Creates different tabs depending on the value of <b>type</b>
	 * 
	 * @param type
	 *            shows what type of Tab navigation should be created.
	 *            <ol>
	 *            <li>type = 0 creates tabs with text only</li>
	 *            <li>type = 1 creates tabs with EditText inside it</li>
	 *            <li>type = 2 creates tabs with icons and text</li>
	 *            <li>type = 3 creates tabs with icons only</li>
	 *            </ol>
	 */
	private void addTabs(int type) {
		getSupportActionBar().removeAllTabs();
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		this.type = type;
		switch (type) {
		case 0:
			for (int i = 1; i <= 3; i++) {
				ActionBar.Tab tab = getSupportActionBar().newTab();
				tab.setText("Tab " + i);
				tab.setTabListener(this);
				getSupportActionBar().addTab(tab);
			}
			break;
		case 1:
			for (int i = 1; i <= 3; i++) {
				ActionBar.Tab tab = getSupportActionBar().newTab();
				tab.setCustomView(R.layout.custom_tab_view);
				tab.setTabListener(this);
				EditText mEditText = (EditText) tab.getCustomView()
						.findViewById(R.id.inputfield);

				mEditText.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						mSelected.setText("Text in selected tab's EditText: "
								+ s);
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {

					}

					@Override
					public void afterTextChanged(Editable s) {

					}
				});
				getSupportActionBar().addTab(tab);
			}
			break;
		case 2:
			for (int i = 1; i <= 3; i++) {
				ActionBar.Tab tab = getSupportActionBar().newTab();
				tab.setText("Tab " + i);
				tab.setIcon(isLight ? R.drawable.ic_search
						: R.drawable.ic_search_inverse);
				tab.setTabListener(this);
				getSupportActionBar().addTab(tab);
			}
			break;
		case 3:
			for (int i = 1; i <= 3; i++) {
				ActionBar.Tab tab = getSupportActionBar().newTab();
				tab.setIcon(isLight ? R.drawable.ic_search
						: R.drawable.ic_search_inverse);
				tab.setTabListener(this);
				getSupportActionBar().addTab(tab);
			}
			break;
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (type == 1) {
			EditText mEditText = (EditText) tab.getCustomView().findViewById(
					R.id.inputfield);

			mSelected.setText("Text in selected tab's EditText: "
					+ mEditText.getText());

		} else
			mSelected.setText("Selected: " + tab.getText());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}

}
