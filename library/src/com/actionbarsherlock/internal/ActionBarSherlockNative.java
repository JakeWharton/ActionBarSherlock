package com.actionbarsherlock.internal;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.internal.app.ActionBarWrapper;
import com.actionbarsherlock.internal.view.menu.MenuPresenter.Callback;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class ActionBarSherlockNative extends ActionBarSherlock {
    private ActionBarWrapper mActionBar;

    public ActionBarSherlockNative(Activity activity, boolean isDelegate) {
        super(activity, isDelegate);
    }


    @Override
    public ActionBar getActionBar() {
        if (DEBUG) Log.d(TAG, "[getActionBar]");

        initActionBar();
        return mActionBar;
    }

    private void initActionBar() {
        mActionBar = new ActionBarWrapper(mActivity);
    }

    @Override
    protected void setMenu(Menu menu, Callback cb) {
        //TODO invalidate panel options menu
        //TODO set boolean that we triggered the invalidation
        //TODO bind in onPreparePanelmenu
    }

    @Override
    public boolean dispatchOpenOptionsMenu() {
        //By returning false the superclass implementation will be called
        //which will trigger the native menu to open.
        return false;
    }

    @Override
    public boolean dispatchCloseOptionsMenu() {
        //By returning false the superclass implementation will be called
        //which will trigger the native menu to close.
        return false;
    }

    @Override
    public void dispatchPostCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.d(TAG, "[dispatchOnPostCreate]");

        if (mIsDelegate) {
            mIsTitleReady = true;
        }
    }

    @Override
    public boolean dispatchKeyUp(int keyCode, KeyEvent event) {
        //By returning false the superclass implementation will be called
        //which will allow the window to handle the menu key natively.
        return false;
    }

    @Override
    public void dispatchInvalidateOptionsMenu() {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean dispatchPrepareOptionsMenu(android.view.Menu menu) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean dispatchOptionsItemSelected(android.view.MenuItem item) {
        return false; //TODO
    }

    @Override
    public boolean dispatchMenuOpened(int featureId, android.view.Menu menu) {
        //By returning false the superclass implementation will be called
        //which will allow the window
        return false;
    }

    @Override
    public boolean hasFeature(int feature) {
        return mActivity.getWindow().hasFeature(feature);
    }

    @Override
    public boolean requestFeature(int featureId) {
        return mActivity.getWindow().requestFeature(featureId);
    }

    @Override
    public void setUiOptions(int uiOptions) {
        mActivity.getWindow().setUiOptions(uiOptions);
    }

    @Override
    public void setUiOptions(int uiOptions, int mask) {
        mActivity.getWindow().setUiOptions(uiOptions, mask);
    }

    @Override
    public void setContentView(int layoutResId) {
        mActivity.getWindow().setContentView(layoutResId);
        initActionBar();
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        mActivity.getWindow().setContentView(view, params);
        initActionBar();
    }

    @Override
    public void addContentView(View view, LayoutParams params) {
        mActivity.getWindow().addContentView(view, params);
        initActionBar();
    }

    @Override
    public void setProgressBarVisibility(boolean visible) {
        mActivity.setProgressBarVisibility(visible);
    }

    @Override
    public void setProgressBarIndeterminateVisibility(boolean visible) {
        mActivity.setProgressBarIndeterminateVisibility(visible);
    }

    @Override
    public void setProgressBarIndeterminate(boolean indeterminate) {
        mActivity.setProgressBarIndeterminate(indeterminate);
    }

    @Override
    public void setProgress(int progress) {
        mActivity.setProgress(progress);
    }

    @Override
    public void setSecondaryProgress(int secondaryProgress) {
        mActivity.setSecondaryProgress(secondaryProgress);
    }

    @Override
    public MenuInflater getMenuInflater() {
        if (DEBUG) Log.d(TAG, "[getMenuInflater]");

        // Make sure that action views can get an appropriate theme.
        if (mMenuInflater == null) {
            initActionBar();
            if (mActionBar != null) {
                //TODO read theme attribute
                mMenuInflater = new MenuInflater(mActivity);
            } else {
                mMenuInflater = new MenuInflater(mActivity);
            }
        }
        return mMenuInflater;
    }

    @Override
    public ActionMode startActionMode(com.actionbarsherlock.view.ActionMode.Callback callback) {
        // TODO Auto-generated method stub
        return null;
    }
}
