package com.actionbarsherlock.internal;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.internal.app.ActionBarWrapper;
import com.actionbarsherlock.internal.view.menu.MenuPresenter.Callback;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

public class ActionBarSherlockNative extends ActionBarSherlock {
    private final Window mWindow;
    
    public ActionBarSherlockNative(Activity activity, boolean isDelegate) {
        super(activity, isDelegate);

        mWindow = activity.getWindow();
    }

    @Override
    protected void initActionBar() {
        mActionBarPublic = new ActionBarWrapper(mActivity);
    }

    @Override
    protected void setMenu(Menu menu, Callback cb) {
        //TODO invalidate panel options menu
        //TODO set boolean that we triggered the invalidation
        //TODO bind in onPreparePanelmenu
    }

    @Override
    public boolean hasFeature(int feature) {
        return mWindow.hasFeature(feature);
    }

    @Override
    public boolean requestFeature(int featureId) {
        return mWindow.requestFeature(featureId);
    }

    @Override
    public void setUiOptions(int uiOptions) {
        mWindow.setUiOptions(uiOptions);
    }

    @Override
    public void setUiOptions(int uiOptions, int mask) {
        mWindow.setUiOptions(uiOptions, mask);
    }

    @Override
    public void setContentView(int layoutResId) {
        mWindow.setContentView(layoutResId);
        initActionBar();
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        mWindow.setContentView(view, params);
        initActionBar();
    }

    @Override
    public void addContentView(View view, LayoutParams params) {
        mWindow.addContentView(view, params);
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
    protected Context getThemedContext() {
        //TODO read theme attribute
        return mActivity;
    }

    @Override
    public ActionMode startActionMode(com.actionbarsherlock.view.ActionMode.Callback callback) {
        // TODO Auto-generated method stub
        return null;
    }
}
