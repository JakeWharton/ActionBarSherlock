package com.actionbarsherlock.app;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActionBarDrawerToggle;
import com.actionbarsherlock.R;

public class SherlockActionBarDrawerToggleDelegate implements ActionBarDrawerToggle.Delegate {

    private static final int[] THEME_ATTRS = new int[]{
            R.attr.homeAsUpIndicator
    };

    private Activity mActivity;
    private final ActionBar mActionBar;

    public SherlockActionBarDrawerToggleDelegate(Activity activity) {
        if (!(activity instanceof SupportActivity)) {
            throw new IllegalArgumentException("Activity must be a Sherlock activity.");
        }

        if (!(activity instanceof ActionBarDrawerToggle.DelegateProvider)) {
            throw new IllegalArgumentException("Activity must implement ActionBarDrawerToggle.DelegateProvider.");
        }

        mActivity = activity;
        mActionBar = ((SupportActivity) activity).getSupportActionBar();
    }

    @Override
    public void setActionBarUpIndicator(Drawable drawable, int contentDescRes) {
        mActionBar.setHomeAsUpIndicator(drawable);
        mActionBar.setHomeActionContentDescription(contentDescRes);
    }

    @Override
    public void setActionBarDescription(int contentDescRes) {
        mActionBar.setHomeActionContentDescription(contentDescRes);
    }

    @Override
    public Drawable getThemeUpIndicator() {
        final TypedArray a = mActivity.obtainStyledAttributes(THEME_ATTRS);
        final Drawable result = a.getDrawable(0);
        a.recycle();
        return result;
    }
}
