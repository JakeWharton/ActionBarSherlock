package com.actionbarsherlock.internal;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.internal.view.menu.MenuPresenter.Callback;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class ActionBarSherlockNative extends ActionBarSherlock {
    public ActionBarSherlockNative(Activity activity, boolean isDelegate) {
        super(activity, isDelegate);
    }

    @Override
    protected void initActionBar() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void setMenu(Menu menu, Callback cb) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getFeatures() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean hasFeature(int featureId) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean requestFeature(int featureId) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setUiOptions(int uiOptions) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setUiOptions(int uiOptions, int mask) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setContentView(int layoutResId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addContentView(View view, LayoutParams params) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setProgressBarVisibility(boolean visible) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setProgressBarIndeterminateVisibility(boolean visible) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setProgressBarIndeterminate(boolean indeterminate) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setProgress(int progress) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setSecondaryProgress(int secondaryProgress) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected Context getThemedContext() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ActionMode startActionMode(
            com.actionbarsherlock.view.ActionMode.Callback callback) {
        // TODO Auto-generated method stub
        return null;
    }
}
