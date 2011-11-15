package com.actionbarsherlock.tests.app;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;

public final class Issue0035 extends SherlockActivity {
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