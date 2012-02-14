package com.actionbarsherlock.app;

import android.app.ListActivity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.ActionBarSherlock.OnActionModeFinishedListener;
import com.actionbarsherlock.ActionBarSherlock.OnActionModeStartedListener;
import com.actionbarsherlock.ActionBarSherlock.OnCreatePanelMenuListener;
import com.actionbarsherlock.ActionBarSherlock.OnMenuItemSelectedListener;
import com.actionbarsherlock.ActionBarSherlock.OnPreparePanelListener;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SherlockListActivity extends ListActivity implements OnCreatePanelMenuListener, OnPreparePanelListener, OnMenuItemSelectedListener, OnActionModeStartedListener, OnActionModeFinishedListener {
    private final ActionBarSherlock mSherlock;


    public SherlockListActivity() {
        this(ActionBarSherlock.FLAG_DELEGATE);
    }

    public SherlockListActivity(int flags) {
        mSherlock = ActionBarSherlock.wrap(this, flags);
    }

    public <T extends ActionBarSherlock> SherlockListActivity(int flags, Class<T> implementation) {
        mSherlock = ActionBarSherlock.wrap(this, flags, implementation);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Action bar and mode
    ///////////////////////////////////////////////////////////////////////////

    public ActionBar getSupportActionBar() {
        return mSherlock.getActionBar();
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        return mSherlock.startActionMode(callback);
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {}

    @Override
    public void onActionModeFinished(ActionMode mode) {}


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
    protected void onPause() {
        mSherlock.dispatchPause();
        super.onPause();
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mSherlock.dispatchKeyEvent(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Native menu handling
    ///////////////////////////////////////////////////////////////////////////

    public MenuInflater getSupportMenuInflater() {
        return mSherlock.getMenuInflater();
    }

    public void invalidateOptionsMenu() {
        mSherlock.dispatchInvalidateOptionsMenu();
    }

    public void supportInvalidateOptionsMenu() {
        invalidateOptionsMenu();
    }

    @Override
    public final boolean onCreateOptionsMenu(android.view.Menu menu) {
        return mSherlock.dispatchCreateOptionsMenu(menu);
    }

    @Override
    public final boolean onPrepareOptionsMenu(android.view.Menu menu) {
        return mSherlock.dispatchPrepareOptionsMenu(menu);
    }

    @Override
    public final boolean onOptionsItemSelected(android.view.MenuItem item) {
        return mSherlock.dispatchOptionsItemSelected(item);
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
    // Sherlock menu handling
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_OPTIONS_PANEL) {
            return onCreateOptionsMenu(menu);
        }
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        if (featureId == Window.FEATURE_OPTIONS_PANEL) {
            return onPrepareOptionsMenu(menu);
        }
        return false;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (featureId == Window.FEATURE_OPTIONS_PANEL) {
            return onOptionsItemSelected(item);
        }
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
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

    public void requestWindowFeature(long featureId) {
        mSherlock.requestFeature((int)featureId);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Progress Indication
    ///////////////////////////////////////////////////////////////////////////

    public void setSupportProgress(int progress) {
        mSherlock.setProgress(progress);
    }

    public void setSupportProgressBarIndeterminate(boolean indeterminate) {
        mSherlock.setProgressBarIndeterminate(indeterminate);
    }

    public void setSupportProgressBarIndeterminateVisibility(boolean visible) {
        mSherlock.setProgressBarIndeterminateVisibility(visible);
    }

    public void setSupportProgressBarVisibility(boolean visible) {
        mSherlock.setProgressBarVisibility(visible);
    }

    public void setSupportSecondaryProgress(int secondaryProgress) {
        mSherlock.setSecondaryProgress(secondaryProgress);
    }
}
