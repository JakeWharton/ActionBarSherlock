package com.actionbarsherlock;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.internal.app.ActionBarImpl;
import com.actionbarsherlock.internal.policy.impl.SherlockWindow;
import com.actionbarsherlock.internal.view.menu.MenuBuilder;
import com.actionbarsherlock.internal.view.menu.MenuPresenter;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * Helper class to wrap any activity with a custom action bar that provides
 * the full Honeycomb action bar API but can be used on Android 1.6 and up.
 */
public final class ActionBarSherlock {
    /**
     * Wrap an existing activity with a custom action bar implementation.
     * 
     * @param activity Activity to wrap.
     * @return Instance to interact with the action bar.
     */
    public static ActionBarSherlock wrap(Activity activity) {
        return new ActionBarSherlock(activity);
    }
    

    private final Activity mActivity;
    private final SherlockWindow mFakeWindow;
    private ActionBarImpl mActionBar;
    private MenuInflater mMenuInflater;
    
    private final MenuPresenter.Callback mMenuCallback = new MenuPresenter.Callback() {
		@Override
		public boolean onOpenSubMenu(MenuBuilder subMenu) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
			// TODO Auto-generated method stub
		}
	};
    
    
    private ActionBarSherlock(Activity activity) {
        mActivity = activity;
        mFakeWindow = new SherlockWindow(activity.getWindow());
        mFakeWindow.setCallback(new com.actionbarsherlock.view.Window.Callback() {
			@Override
			public boolean onMenuItemSelected(int featureId, MenuItem item) {
				return false; //TODO theeeees!
			}
		});
    }
    
    
    /**
     * Get the current action bar instance.
     * 
     * @return Action bar instance.
     */
    public ActionBar getActionBar() {
        return mActionBar;
    }
    
    private void initActionBar() {
        // Initializing the window decor can change window feature flags.
        // Make sure that we have the correct set before performing the test below.
    	mFakeWindow.getDecorView();

        if (mActivity.isChild() || !hasFeature(Window.FEATURE_ACTION_BAR) || (mActionBar != null)) {
            return;
        }
        
        mActionBar = new ActionBarImpl(mActivity);
        mActionBar.setTitle(mActivity.getTitle());
    }
    
    public boolean hasFeature(int featureId) {
    	return mFakeWindow.hasFeature(featureId);
    }
    
    public boolean requestWindowFeature(int featureId) {
    	return mFakeWindow.requestFeature(featureId);
    }

    public void setUiOptions(int uiOptions) {
    	mFakeWindow.setUiOptions(uiOptions);
    }
    public void setUiOptions(int uiOptions, int mask) {
    	mFakeWindow.setUiOptions(uiOptions, mask);
    }
    
    /**
     * Set the content of the activity inside the action bar.
     * 
     * @param layoutResId Layout resource ID.
     */
    public void setContentView(int layoutResId) {
    	mFakeWindow.setContentView(layoutResId);
    	initActionBar();
    }
    
    /**
     * Set the content of the activity inside the action bar.
     * 
     * @param view The desired content to display.
     */
    public void setContentView(View view) {
    	mFakeWindow.setContentView(view);
    	initActionBar();
    }
    
    /**
     * Set the content of the activity inside the action bar.
     * 
     * @param view The desired content to display.
     * @param params Layout paramaters to apply to the view.
     */
    public void setContentView(View view, ViewGroup.LayoutParams params) {
    	mFakeWindow.setContentView(view, params);
    	initActionBar();
    }
    
    public void setTitle(CharSequence title) {
    	mActionBar.setTitle(title);
    }
    
    public void setTitle(int resId) {
    	mActionBar.setTitle(resId);
    }
    
    /**
     * Get a menu inflater instance which supports the newer menu attributes.
     * 
     * @return Menu inflater instance.
     */
    public MenuInflater getMenuInflater() {
        // Make sure that action views can get an appropriate theme.
        if (mMenuInflater == null) {
            initActionBar();
            if (mActionBar != null) {
                mMenuInflater = new MenuInflater(mActionBar.getThemedContext());
            } else {
                mMenuInflater = new MenuInflater(mActivity);
            }
        }
        return mMenuInflater;
    }
    
    /**
     * Create a new menu to create or update the action items. Call
     * {@link #setMenu(Menu)} when you want to update the action bar.
     * 
     * @return Menu instance.
     */
    public Menu newMenu() {
        return new MenuBuilder(mActivity);
    }
    
    /**
     * Set the menu used for the action items.
     * 
     * @param menu Menu instance.
     */
    public void setMenu(Menu menu) {
        if (!(menu instanceof MenuBuilder)) {
            throw new RuntimeException("Menu must be of type com.actionbarsherlock.internal.view.menu.MenuBuilder");
        }
        mActionBar.setMenu(menu, mMenuCallback);
    }
}
