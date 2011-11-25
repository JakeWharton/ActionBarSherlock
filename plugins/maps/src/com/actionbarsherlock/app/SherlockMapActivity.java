package com.actionbarsherlock.app;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.ActionBarSherlock.OnCreateOptionsMenuListener;
import com.actionbarsherlock.ActionBarSherlock.OnOptionsItemSelectedListener;
import com.actionbarsherlock.ActionBarSherlock.OnPrepareOptionsMenuListener;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.MapActivity;

public abstract class SherlockMapActivity extends MapActivity implements OnCreateOptionsMenuListener, OnPrepareOptionsMenuListener, OnOptionsItemSelectedListener {
	final ActionBarSherlock mSherlock = ActionBarSherlock.asDelegateFor(this);

    public ActionBar getSupportActionBar() {
        return mSherlock.getActionBar();
    }

    
    ///////////////////////////////////////////////////////////////////////////
    // General lifecycle/callback dispatching
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mSherlock.dispatchConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mSherlock.dispatchPostResume();
    }
    
    @Override
    protected void onStop() {
        mSherlock.dispatchStop();
        super.onStop();
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
    	mSherlock.dispatchPostCreate(savedInstanceState);
    	super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        mSherlock.dispatchTitleChanged(title, color);
        super.onTitleChanged(title, color);
    }
    
    @Override
    public final boolean onMenuOpened(int featureId, android.view.Menu menu) {
        if (mSherlock.dispatchMenuOpened(featureId, menu)) {
        	return true;
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public void onPanelClosed(int featureId, android.view.Menu menu) {
        mSherlock.dispatchPanelClosed(featureId, menu);
        super.onPanelClosed(featureId, menu);
    }
    
    
    ///////////////////////////////////////////////////////////////////////////
    // Menu handling
    ///////////////////////////////////////////////////////////////////////////
    
    public MenuInflater getSupportMenuInflater() {
        return mSherlock.getMenuInflater();
    }

    @Override
    public final boolean onCreateOptionsMenu(android.view.Menu menu) {
    	return true;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public final boolean onPrepareOptionsMenu(android.view.Menu menu) {
        return mSherlock.dispatchPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public final boolean onOptionsItemSelected(android.view.MenuItem item) {
        throw new RuntimeException("This should never be called. Create reproducible test case and report!");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
    
    public void invalidateOptionsMenu() {
    	mSherlock.dispatchInvalidateOptionsMenu();
    }
    
    /** @deprecated Use {@link #invalidateOptionsMenu()}. */
    @Deprecated
    public void supportInvalidateOptionsMenu() {
        invalidateOptionsMenu();
    }

    @Override
    public void openOptionsMenu() {
    	if (!mSherlock.dispatchOpenOptionsMenu()) {
    		super.openOptionsMenu();
    	}
    }

    @Override
    public void closeOptionsMenu() {
    	if (!mSherlock.dispatchCloseOptionsMenu()) {
    		super.closeOptionsMenu();
    	}
    }
    
    
    ///////////////////////////////////////////////////////////////////////////
    // Content
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void addContentView(View view, LayoutParams params) {
        mSherlock.addContentView(view, params);
    }

    @Override
    public void setContentView(int layoutResId) {
        mSherlock.setContentView(layoutResId);
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        mSherlock.setContentView(view, params);
    }

    @Override
    public void setContentView(View view) {
        mSherlock.setContentView(view);
    }
}
