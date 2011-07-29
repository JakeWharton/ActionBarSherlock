package com.actionbarsherlock.tests.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;

public final class Issue0035 extends FragmentActivity {
	boolean mIsActivityCreating = false;
	boolean mWasMenuCreatedOnActivityCreation = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		mIsActivityCreating = true;
        super.onCreate(savedInstanceState);
        mIsActivityCreating = false;
        setContentView(R.layout.blank);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (mIsActivityCreating) {
			mWasMenuCreatedOnActivityCreation = true;
		}
		return false;
	}
	
	public boolean getWasMenuCreatedOnActivityCreation() {
		return mWasMenuCreatedOnActivityCreation;
	}
}