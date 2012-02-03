package com.actionbarsherlock.internal;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.internal.app.ActionBarWrapper;
import com.actionbarsherlock.view.ActionMode;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
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
    public void dispatchInvalidateOptionsMenu() {
        if (DEBUG) Log.d(TAG, "[dispatchInvalidateOptionsMenu]");

        mActivity.getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
    }

    @Override
    public boolean dispatchCreateOptionsMenu(android.view.Menu menu) {
        if (DEBUG) Log.d(TAG, "[dispatchCreateOptionsMenu] menu: " + menu);

        if (mMenu == null) {
            //TODO create menu
        }

        final boolean result = callbackCreateOptionsMenu();
        if (DEBUG) Log.d(TAG, "[dispatchCreateOptionsMenu] returning " + result);
        return result;
    }

    @Override
    public boolean dispatchPrepareOptionsMenu(android.view.Menu menu) {
        if (DEBUG) Log.d(TAG, "[dispatchPrepareOptionsMenu] menu: " + menu);

        final boolean result = callbackPrepareOptionsMenu();
        //TODO bind to native
        if (DEBUG) Log.d(TAG, "[dispatchPrepareOptionsMenu] returning " + result);
        return result;
    }

    @Override
    public boolean dispatchOptionsItemSelected(android.view.MenuItem item) {
        if (DEBUG) Log.d(TAG, "[dispatchOptionsItemSelected] item: " + item);

        final boolean result = callbackOptionsItemSelected(mNativeItemMap.get(item));
        if (DEBUG) Log.d(TAG, "[dispatchOptionsItemSelected] returning " + result);
        return result;
    }

    @Override
    public boolean hasFeature(int feature) {
        if (DEBUG) Log.d(TAG, "[hasFeature] feature: " + feature);

        final boolean result = mActivity.getWindow().hasFeature(feature);
        if (DEBUG) Log.d(TAG, "[hasFeature] returning " + result);
        return result;
    }

    @Override
    public boolean requestFeature(int featureId) {
        if (DEBUG) Log.d(TAG, "[requestFeature] featureId: " + featureId);

        final boolean result = mActivity.getWindow().requestFeature(featureId);
        if (DEBUG) Log.d(TAG, "[requestFeature] returning " + result);
        return result;
    }

    @Override
    public void setUiOptions(int uiOptions) {
        if (DEBUG) Log.d(TAG, "[setUiOptions] uiOptions: " + uiOptions);

        mActivity.getWindow().setUiOptions(uiOptions);
    }

    @Override
    public void setUiOptions(int uiOptions, int mask) {
        if (DEBUG) Log.d(TAG, "[setUiOptions] uiOptions: " + uiOptions + ", mask: " + mask);

        mActivity.getWindow().setUiOptions(uiOptions, mask);
    }

    @Override
    public void setContentView(int layoutResId) {
        if (DEBUG) Log.d(TAG, "[setContentView] layoutResId: " + layoutResId);

        mActivity.getWindow().setContentView(layoutResId);
        initActionBar();
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        if (DEBUG) Log.d(TAG, "[setContentView] view: " + view + ", params: " + params);

        mActivity.getWindow().setContentView(view, params);
        initActionBar();
    }

    @Override
    public void addContentView(View view, LayoutParams params) {
        if (DEBUG) Log.d(TAG, "[addContentView] view: " + view + ", params: " + params);

        mActivity.getWindow().addContentView(view, params);
        initActionBar();
    }

    @Override
    public void setTitle(CharSequence title) {
        if (DEBUG) Log.d(TAG, "[setTitle] title: " + title);

        mActivity.getWindow().setTitle(title);
    }

    @Override
    public void setProgressBarVisibility(boolean visible) {
        if (DEBUG) Log.d(TAG, "[setProgressBarVisibility] visible: " + visible);

        mActivity.setProgressBarVisibility(visible);
    }

    @Override
    public void setProgressBarIndeterminateVisibility(boolean visible) {
        if (DEBUG) Log.d(TAG, "[setProgressBarIndeterminateVisibility] visible: " + visible);

        mActivity.setProgressBarIndeterminateVisibility(visible);
    }

    @Override
    public void setProgressBarIndeterminate(boolean indeterminate) {
        if (DEBUG) Log.d(TAG, "[setProgressBarIndeterminate] indeterminate: " + indeterminate);

        mActivity.setProgressBarIndeterminate(indeterminate);
    }

    @Override
    public void setProgress(int progress) {
        if (DEBUG) Log.d(TAG, "[setProgress] progress: " + progress);

        mActivity.setProgress(progress);
    }

    @Override
    public void setSecondaryProgress(int secondaryProgress) {
        if (DEBUG) Log.d(TAG, "[setSecondaryProgress] secondaryProgress: " + secondaryProgress);

        mActivity.setSecondaryProgress(secondaryProgress);
    }

    @Override
    protected Context getThemedContext() {
        Context context = mActivity;
        TypedValue outValue = new TypedValue();
        mActivity.getTheme().resolveAttribute(android.R.attr.actionBarWidgetTheme, outValue, true);
        if (outValue.resourceId != 0) {
            //We are unable to test if this is the same as our current theme
            //so we just wrap it and hope that if the attribute was specified
            //then the user is intentionally specifying an alternate theme.
            context = new ContextThemeWrapper(context, outValue.resourceId);
        }
        return context;
    }

    @Override
    public ActionMode startActionMode(com.actionbarsherlock.view.ActionMode.Callback callback) {
        if (DEBUG) Log.d(TAG, "[startActionMode] callback: " + callback);

        // TODO Auto-generated method stub
        return null;
    }
}
