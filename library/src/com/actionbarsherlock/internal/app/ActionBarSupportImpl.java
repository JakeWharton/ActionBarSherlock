/*
 * Copyright (C) 2011 Jake Wharton <jakewharton@gmail.com>
 * Copyright (C) 2010 Johan Nilsson <http://markupartist.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.actionbarsherlock.internal.app;

import java.util.ArrayList;
import java.util.List;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ActionMode;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.Window;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SpinnerAdapter;
import com.actionbarsherlock.R;
import com.actionbarsherlock.internal.view.menu.MenuBuilder;
import com.actionbarsherlock.internal.view.menu.MenuItemImpl;
import com.actionbarsherlock.internal.view.menu.MenuView;
import com.actionbarsherlock.internal.widget.ActionBarWatson;

public final class ActionBarSupportImpl extends ActionBar {
	/**
	 * Abstraction to get an instance of our implementing class.
	 * 
	 * @param activity Parent activity.
	 * @return {@code ActionBar} instance.
	 */
	public static ActionBar createFor(FragmentActivity activity) {
		return new ActionBarSupportImpl(activity);
	}
	
	/** Maximum action bar items in portrait mode. */
	private static final int MAX_ACTION_BAR_ITEMS_PORTRAIT = 3;
	
	/** Maximum action bar items in landscape mode. */
	private static final int MAX_ACTION_BAR_ITEMS_LANDSCAPE = 4;
	
	
	
	/** Action bar view. */
	private ActionBarWatson mActionBar;
	
	/** Activity content view. */
	private FrameLayout mContentView;
	
	/** List of listeners to the menu visibility. */
	private final List<OnMenuVisibilityListener> mMenuListeners = new ArrayList<OnMenuVisibilityListener>();
	
	/** Whether text will be enabled on action items. */
	private boolean mIsActionItemTextEnabled = false;
	
	/** Whether display of the indeterminate progress is allowed. */
	private boolean mHasIndeterminateProgress = false;
	
	
	
	private ActionBarSupportImpl(FragmentActivity activity) {
		super(activity);
	}
	
	
	// ------------------------------------------------------------------------
	// ACTION BAR SHERLOCK SUPPORT
	// ------------------------------------------------------------------------

	public void performAttach() {
		mActionBar = (ActionBarWatson)getActivity().findViewById(R.id.actionbarwatson);
		mContentView = (FrameLayout)getActivity().findViewById(R.id.actionbarsherlock_content);
		
		final MenuItemImpl homeMenuItem = getHomeMenuItem();
		final ActionBarWatson.Item homeItem = mActionBar.getHomeItem();
		final WatsonItemViewWrapper homeWrapper = new WatsonItemViewWrapper(homeItem);
		homeWrapper.initialize(homeMenuItem, MenuBuilder.TYPE_WATSON);
		homeMenuItem.setItemView(MenuBuilder.TYPE_WATSON, homeWrapper);

		final PackageManager pm = getActivity().getPackageManager();
		final ApplicationInfo appInfo = getActivity().getApplicationInfo();
		ActivityInfo actInfo = null;
		try {
			actInfo = pm.getActivityInfo(getActivity().getComponentName(), PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {}

		
		if ((actInfo != null) && (actInfo.labelRes != 0)) {
			//Load label string resource from the activity entry
			mActionBar.setTitle(actInfo.labelRes);
		} else if (mActionBar.getTitle() == null) {
			//No activity label string resource and none in theme
			mActionBar.setTitle(actInfo.loadLabel(pm));
		}
		
		if ((actInfo != null) && (actInfo.icon != 0)) {
			//Load the icon from the activity entry
			homeItem.setIcon(actInfo.icon);
		} else if (homeItem.getIcon() == null) {
			//No activity icon and none in theme
			homeItem.setIcon(pm.getApplicationIcon(appInfo));
		}
		
		//XXX LOGO LOADING DOES NOT WORK
		//XXX SEE: http://stackoverflow.com/questions/6105504/load-activity-and-or-application-logo-programmatically-from-manifest
		//XXX SEE: https://groups.google.com/forum/#!topic/android-developers/UFR4l0ZwJWc
		//if ((actInfo != null) && (actInfo.logo != 0)) {
		//	//Load the logo from the activity entry
		//	homeItem.setLogo(actInfo.logo);
		//} else if ((homeItem.getLogo() == null) && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)) {
		//	//No activity logo and none in theme
		//	homeItem.setLogo(appInfo.logo);
		//}
	}
	
	public void onMenuInflated(Menu menu) {
		int maxItems = MAX_ACTION_BAR_ITEMS_PORTRAIT;
		if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			maxItems = MAX_ACTION_BAR_ITEMS_LANDSCAPE;
		}
		
		//Iterate and grab as many actions as we can up to maxItems honoring
		//their showAsAction values
		int ifItems = 0;
		final int count = menu.size();
		List<MenuItemImpl> keep = new ArrayList<MenuItemImpl>();
		for (int i = 0; i < count; i++) {
			MenuItemImpl item = (MenuItemImpl)menu.getItem(i);
			
			//Items without an icon or items without a title when the title
			//is enabled are forced into the normal options menu
			if (!mIsActionItemTextEnabled && (item.getIcon() == null)) {
				continue;
			} else if (mIsActionItemTextEnabled && ((item.getTitle() == null) || item.getTitle().equals(""))) {
				continue;
			}
			
			if ((item.getShowAsAction() & MenuItem.SHOW_AS_ACTION_ALWAYS) != 0) {
				//Show always therefore add to keep list
				keep.add(item);
				
				if ((keep.size() > maxItems) && (ifItems > 0)) {
					//If we have exceeded the max and there are "ifRoom" items
					//then iterate backwards to remove one and add it to the
					//head of the classic items list.
					for (int j = keep.size() - 1; j >= 0; j--) {
						if ((keep.get(j).getShowAsAction() & MenuItem.SHOW_AS_ACTION_IF_ROOM) != 0) {
							keep.remove(j);
							ifItems -= 1;
							break;
						}
					}
				}
			} else if (((item.getShowAsAction() & MenuItem.SHOW_AS_ACTION_IF_ROOM) != 0)
					&& (keep.size() < maxItems)) {
				//"ifRoom" items are added if we have not exceeded the max.
				keep.add(item);
				ifItems += 1;
			}
		}
		
		//Mark items that will be shown on the action bar as such so they do
		//not show up on the activity options menu
		mActionBar.removeAllItems();
		mActionBar.setIsActionItemTextEnabled(mIsActionItemTextEnabled);
		for (MenuItemImpl item : keep) {
			item.setIsShownOnActionBar(true);
			
			//Get a new item for this menu item
			ActionBarWatson.Item watsonItem = mActionBar.newItem();
			
			//Create and initialize a watson itemview wrapper
			WatsonItemViewWrapper watsonWrapper = new WatsonItemViewWrapper(watsonItem);
			watsonWrapper.initialize(item, MenuBuilder.TYPE_WATSON);
			
			//Associate the itemview with the item so changes will be reflected
			item.setItemView(MenuBuilder.TYPE_WATSON, watsonWrapper);
			
			//Add to the action bar for display
			mActionBar.addItem(watsonItem);
		}
	}

	public void setContentView(int layoutResId) {
		getActivity().getLayoutInflater().inflate(layoutResId, mContentView, true);
	}

	public void setContentView(View view) {
		mContentView.addView(view);
	}

	public void setContentView(View view, ViewGroup.LayoutParams params) {
		mContentView.addView(view, params);
	}

	public boolean requestWindowFeature(long featureId) {
		if (featureId == Window.FEATURE_ACTION_BAR_OVERLAY) {
			// TODO Make action bar partially transparent
			return true;
		}
		if (featureId == Window.FEATURE_ACTION_MODE_OVERLAY) {
			// TODO Make action modes partially transparent
			return true;
		}
		if (featureId == Window.FEATURE_ENABLE_ACTION_BAR_WATSON_TEXT) {
			mIsActionItemTextEnabled = true;
			return true;
		}
		if (featureId == Window.FEATURE_INDETERMINATE_PROGRESS) {
			mHasIndeterminateProgress = true;
			return true;
		}
		return false;
	}
	
	public void onMenuVisibilityChanged(boolean isVisible) {
		//Marshal to all listeners
		for (OnMenuVisibilityListener listener : mMenuListeners) {
			listener.onMenuVisibilityChanged(isVisible);
		}
	}
	
	public void setProgressBarIndeterminateVisibility(boolean visible) {
		if (mHasIndeterminateProgress) {
			mActionBar.setProgressBarIndeterminateVisibility(visible);
		}
	}
	
	// ------------------------------------------------------------------------
	// ACTION MODE METHODS
	// ------------------------------------------------------------------------

	@Override
	protected ActionMode startActionMode(ActionMode.Callback callback) {
		throw new RuntimeException("Not implemented.");
	}
	
	// ------------------------------------------------------------------------
	// ACTION BAR METHODS
	// ------------------------------------------------------------------------

	@Override
	public void addOnMenuVisibilityListener(OnMenuVisibilityListener listener) {
		if (!mMenuListeners.contains(listener)) {
			mMenuListeners.add(listener);
		}
	}

	@Override
	public void addTab(Tab tab) {
		mActionBar.addTab(tab);
	}

	@Override
	public void addTab(Tab tab, boolean setSelected) {
		mActionBar.addTab(tab, setSelected);
	}

	@Override
	public void addTab(Tab tab, int position) {
		mActionBar.addTab(tab, position);
	}

	@Override
	public void addTab(ActionBar.Tab tab, int position, boolean setSelected) {
		mActionBar.addTab(tab, position, setSelected);
	}
	
	@Override
	public View getCustomView() {
		return mActionBar.getCustomView();
	}
	
	@Override
	public int getDisplayOptions() {
		return mActionBar.getDisplayOptions();
	}

	@Override
	public int getHeight() {
		return mActionBar.getHeight();
	}

	@Override
	public int getNavigationItemCount() {
		return mActionBar.getNavigationItemCount();
	}

	@Override
	public int getNavigationMode() {
		return mActionBar.getNavigationMode();
	}

	@Override
	public int getSelectedNavigationIndex() {
		return mActionBar.getSelectedNavigationIndex();
	}

	@Override
	public ActionBar.Tab getSelectedTab() {
		return mActionBar.getSelectedTab();
	}

	@Override
	public CharSequence getSubtitle() {
		return mActionBar.getSubtitle();
	}

	@Override
	public ActionBar.Tab getTabAt(int index) {
		return mActionBar.getTabAt(index);
	}

	@Override
	public int getTabCount() {
		return mActionBar.getTabCount();
	}

	@Override
	public CharSequence getTitle() {
		return mActionBar.getTitle();
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
	public ActionBar.Tab newTab() {
		return mActionBar.newTab();
	}

	@Override
	public void removeAllTabs() {
		mActionBar.removeAllTabs();
	}

	@Override
	public void removeOnMenuVisibilityListener(OnMenuVisibilityListener listener) {
		mMenuListeners.remove(listener);
	}

	@Override
	public void removeTab(ActionBar.Tab tab) {
		mActionBar.removeTab(tab);
	}

	@Override
	public void removeTabAt(int position) {
		mActionBar.removeTabAt(position);
	}

	@Override
	public void setBackgroundDrawable(Drawable d) {
		mActionBar.setBackgroundDrawable(d);
	}

	@Override
	public void setCustomView(int resId) {
		mActionBar.setCustomView(resId);
	}

	@Override
	public void setCustomView(View view) {
		mActionBar.setCustomView(view);
	}
	
	@Override
	public void setCustomView(View view, ActionBar.LayoutParams layoutParams) {
		mActionBar.setCustomView(view, layoutParams);
	}

	@Override
	public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
		mActionBar.setDisplayHomeAsUpEnabled(showHomeAsUp);
	}

	@Override
	public void setDisplayOptions(int options, int mask) {
		mActionBar.setDisplayOptions(options, mask);
	}

	@Override
	public void setDisplayOptions(int options) {
		mActionBar.setDisplayOptions(options);
	}

	@Override
	public void setDisplayShowCustomEnabled(boolean showCustom) {
		mActionBar.setDisplayShowCustomEnabled(showCustom);
	}

	@Override
	public void setDisplayShowHomeEnabled(boolean showHome) {
		mActionBar.setDisplayShowHomeEnabled(showHome);
	}

	@Override
	public void setDisplayShowTitleEnabled(boolean showTitle) {
		mActionBar.setDisplayShowTitleEnabled(showTitle);
	}

	@Override
	public void setDisplayUseLogoEnabled(boolean useLogo) {
		mActionBar.setDisplayUseLogoEnabled(useLogo);
	}

	@Override
	public void setListNavigationCallbacks(SpinnerAdapter adapter, ActionBar.OnNavigationListener callback) {
		mActionBar.setListNavigationCallbacks(adapter, callback);
	}

	@Override
	public void setNavigationMode(int mode) {
		mActionBar.setNavigationMode(mode);
	}

	@Override
	public void setSelectedNavigationItem(int position) {
		mActionBar.setSelectedNavigationItem(position);
	}

	@Override
	public void selectTab(ActionBar.Tab tab) {
		mActionBar.selectTab(tab);
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
	public void setTitle(CharSequence title) {
		mActionBar.setTitle(title);
	}
	@Override
	public void setTitle(int resId) {
		mActionBar.setTitle(resId);
	}

	@Override
	public void show() {
		mActionBar.show();
	}
	
	/////
	
	private static final class WatsonItemViewWrapper implements MenuView.ItemView, View.OnClickListener {
		private final ActionBarWatson.Item mWatsonItem;
		private MenuItemImpl mMenuItem;

		public WatsonItemViewWrapper(ActionBarWatson.Item item) {
			mWatsonItem = item;
			mWatsonItem.setOnClickListener(this);
		}

		@Override
		public MenuItemImpl getItemData() {
			return mMenuItem;
		}

		@Override
		public void initialize(MenuItemImpl itemData, int menuType) {
			mMenuItem = itemData;

			//Only load menu item data if we are not the HomeItem
			if (!(mWatsonItem instanceof ActionBarWatson.HomeItem)) {
				setIcon(itemData.getIcon());
				setTitle(itemData.getTitle());
				setEnabled(itemData.isEnabled());
				setCheckable(itemData.isCheckable());
				setChecked(itemData.isChecked());
				setActionView(itemData.getActionView());
			}
		}

		@Override
		public boolean prefersCondensedTitle() {
			return true;
		}

		@Override
		public void setCheckable(boolean checkable) {
			//TODO mItem.setCheckable(checkable);
		}

		@Override
		public void setChecked(boolean checked) {
			//TODO mItem.setChecked(checked);
		}

		@Override
		public void setEnabled(boolean enabled) {
			mWatsonItem.setEnabled(enabled);
		}

		@Override
		public void setIcon(Drawable icon) {
			mWatsonItem.setIcon(icon);
		}

		@Override
		public void setShortcut(boolean showShortcut, char shortcutKey) {
			//Not supported
		}

		@Override
		public void setTitle(CharSequence title) {
			mWatsonItem.setTitle(title);
		}

		@Override
		public boolean showsIcon() {
			return true;
		}

		@Override
		public void setActionView(View actionView) {
			mWatsonItem.setCustomView(actionView);
		}

		@Override
		public void onClick(View view) {
			if (mMenuItem != null) {
				mMenuItem.invoke();
			}
		}
	}
}
