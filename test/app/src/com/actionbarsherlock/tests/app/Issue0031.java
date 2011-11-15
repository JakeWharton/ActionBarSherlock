package com.actionbarsherlock.tests.app;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public final class Issue0031 extends SherlockActivity {
	private MenuItem mResourceMenuItem;
	private MenuItem mStringMenuItem;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.issue0031, menu);
		mResourceMenuItem = menu.findItem(R.id.issue0031_resource_item);
		mStringMenuItem = menu.findItem(R.id.issue0031_string_item);
		return false;
	}
	
	public String getResourceTitle() {
		return getString(R.string.issue0031_test);
	}
	public String getStringTitle() {
		//Make sure this matches EXATCLY with the menu.xml value.
		return "String title test.";
	}
	public MenuItem getResourceMenuItem() {
		return mResourceMenuItem;
	}
	public MenuItem getStringMenuItem() {
		return mStringMenuItem;
	}
}