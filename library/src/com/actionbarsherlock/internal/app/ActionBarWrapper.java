package com.actionbarsherlock.internal.app;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.SpinnerAdapter;

import com.actionbarsherlock.app.ActionBar;

public class ActionBarWrapper extends ActionBar implements android.app.ActionBar.OnNavigationListener {
    private final android.app.ActionBar mActionBar;
    
    private ActionBar.OnNavigationListener mNavigationListener;
    
    public ActionBarWrapper(Activity activity) {
        mActionBar = activity.getActionBar();
    }

    
	@Override
	public void setCustomView(View view) {
		mActionBar.setCustomView(view);
	}

	@Override
	public void setCustomView(View view, LayoutParams layoutParams) {
	    //TODO copy our params to native
        mActionBar.setCustomView(view, null);
	}

	@Override
	public void setCustomView(int resId) {
        mActionBar.setCustomView(resId);
	}

	@Override
	public void setIcon(int resId) {
		mActionBar.setIcon(resId);
	}

	@Override
	public void setIcon(Drawable icon) {
		mActionBar.setIcon(icon);
	}

	@Override
	public void setLogo(int resId) {
		mActionBar.setLogo(resId);
	}

	@Override
	public void setLogo(Drawable logo) {
		mActionBar.setLogo(logo);
	}

	@Override
	public void setListNavigationCallbacks(SpinnerAdapter adapter, OnNavigationListener callback) {
		mNavigationListener = callback;
		mActionBar.setListNavigationCallbacks(adapter, (callback != null) ? this : null);
	}
	
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        //This should never be a NullPointerException since we only set
        //ourselves as the listener when the callback is not null.
        return mNavigationListener.onNavigationItemSelected(itemPosition, itemId);
    }

	@Override
	public void setSelectedNavigationItem(int position) {
		mActionBar.setSelectedNavigationItem(position);
	}

	@Override
	public int getSelectedNavigationIndex() {
		return mActionBar.getSelectedNavigationIndex();
	}

	@Override
	public int getNavigationItemCount() {
		return mActionBar.getNavigationItemCount();
	}

	@Override
	public void setTitle(CharSequence title) {
		mActionBar.setTitle(title);
	}

	@Override
	public void setTitle(int resId) {
		mActionBar.setTitle(resId);
	}

	@Override
	public void setSubtitle(CharSequence subtitle) {
		mActionBar.setSubtitle(subtitle);
	}

	@Override
	public void setSubtitle(int resId) {
		mActionBar.setSubtitle(resId);
	}

	@Override
	public void setDisplayOptions(int options) {
		mActionBar.setDisplayOptions(options);
	}

	@Override
	public void setDisplayOptions(int options, int mask) {
		mActionBar.setDisplayOptions(options, mask);
	}

	@Override
	public void setDisplayUseLogoEnabled(boolean useLogo) {
		mActionBar.setDisplayUseLogoEnabled(useLogo);
	}

	@Override
	public void setDisplayShowHomeEnabled(boolean showHome) {
		mActionBar.setDisplayShowHomeEnabled(showHome);
	}

	@Override
	public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
		mActionBar.setDisplayHomeAsUpEnabled(showHomeAsUp);
	}

	@Override
	public void setDisplayShowTitleEnabled(boolean showTitle) {
		mActionBar.setDisplayShowTitleEnabled(showTitle);
	}

	@Override
	public void setDisplayShowCustomEnabled(boolean showCustom) {
		mActionBar.setDisplayShowCustomEnabled(showCustom);
	}

	@Override
	public void setBackgroundDrawable(Drawable d) {
		mActionBar.setBackgroundDrawable(d);
	}

	@Override
	public View getCustomView() {
		return mActionBar.getCustomView();
	}

	@Override
	public CharSequence getTitle() {
		return mActionBar.getTitle();
	}

	@Override
	public CharSequence getSubtitle() {
		return mActionBar.getSubtitle();
	}

	@Override
	public int getNavigationMode() {
		return mActionBar.getNavigationMode();
	}

	@Override
	public void setNavigationMode(int mode) {
		mActionBar.setNavigationMode(mode);
	}

	@Override
	public int getDisplayOptions() {
		return mActionBar.getDisplayOptions();
	}

	@Override
	public Tab newTab() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addTab(Tab tab) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addTab(Tab tab, boolean setSelected) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addTab(Tab tab, int position) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addTab(Tab tab, int position, boolean setSelected) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTab(Tab tab) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTabAt(int position) {
		mActionBar.removeTabAt(position);
	}

	@Override
	public void removeAllTabs() {
		mActionBar.removeAllTabs();
	}

	@Override
	public void selectTab(Tab tab) {
		// TODO Auto-generated method stub

	}

	@Override
	public Tab getSelectedTab() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tab getTabAt(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTabCount() {
		return mActionBar.getTabCount();
	}

	@Override
	public int getHeight() {
		return mActionBar.getHeight();
	}

	@Override
	public void show() {
		mActionBar.show();
	}

	@Override
	public void hide() {
		mActionBar.hide();
	}

	@Override
	public boolean isShowing() {
		return mActionBar.isShowing();
	}

	@Override
	public void addOnMenuVisibilityListener(OnMenuVisibilityListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeOnMenuVisibilityListener(OnMenuVisibilityListener listener) {
		// TODO Auto-generated method stub

	}

}
