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

package android.support.v4.app;

import java.util.ArrayList;
import java.util.List;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ActionMode;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.Window;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SpinnerAdapter;
import com.jakewharton.android.actionbarsherlock.R;
import com.jakewharton.android.actionbarsherlock.internal.view.MenuBuilder;
import com.jakewharton.android.actionbarsherlock.internal.view.MenuItemImpl;
import com.jakewharton.android.actionbarsherlock.internal.view.MenuView;
import com.jakewharton.android.actionbarsherlock.widget.ActionBarWatson;

final class ActionBarHandlerWatson extends ActionBar {
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
	
	
	
	// ------------------------------------------------------------------------
	// ACTION BAR SHERLOCK SUPPORT
	// ------------------------------------------------------------------------

	@Override
	void performAttach() {
		getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getActivity().setSuperContentView(R.layout.actionbarwatson_wrapper);
		
		mActionBar = (ActionBarWatson)getActivity().findViewById(R.id.actionbarwatson);
		mContentView = (FrameLayout)getActivity().findViewById(R.id.actionbarwatson_content_view);
		
		final ActionBarWatson.Item homeItem = mActionBar.getHomeItem();
		final WatsonItemViewWrapper homeWrapper = new WatsonItemViewWrapper(homeItem);
		getActivity().getHomeMenuItem().setItemView(MenuBuilder.TYPE_WATSON, homeWrapper);

		final PackageManager pm = getActivity().getPackageManager();
		final ApplicationInfo appInfo = getActivity().getApplicationInfo();
		ActivityInfo actInfo = null;
		try {
			actInfo = pm.getActivityInfo(getActivity().getComponentName(), PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {}

		
		if (mActionBar.getTitle() == null) {
			if (actInfo != null) {
				//Try to load title from Activity's manifest entry
				mActionBar.setTitle(actInfo.loadLabel(pm));
			} else {
				//Can't load activity title. Set a default.
				mActionBar.setTitle(appInfo.loadLabel(pm));
			}
		}
		
		if (homeItem.getIcon() == null) {
			if (actInfo != null) {
				//Load icon from the Activity's manifest entry
				homeItem.setIcon(actInfo.loadIcon(pm));
			} else {
				//Can't load activity icon. Get application icon or default.
				homeItem.setIcon(appInfo.loadIcon(pm));
			}
		}
		
		if (homeItem.getLogo() == null) {
			//TODO http://stackoverflow.com/questions/6105504/load-activity-and-or-application-logo-programmatically-from-manifest
			
			//Must be >= gingerbread to look for a logo in the manifest
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				if (actInfo != null) {
					//Try to load the logo from the Activity's manifest entry
					homeItem.setLogo(actInfo.loadLogo(pm));
				} else {
					//Try to load the logo from the Application's manifest entry
					homeItem.setLogo(appInfo.loadLogo(pm));
				}
			}
		}
	}

	@Override
	void onMenuInflated(Menu menu) {
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

	@Override
	void setContentView(int layoutResId) {
		getActivity().getLayoutInflater().inflate(layoutResId, mContentView, true);
	}

	@Override
	void setContentView(View view) {
		mContentView.addView(view);
	}

	@Override
	void setContentView(View view, ViewGroup.LayoutParams params) {
		mContentView.addView(view, params);
	}

	@Override
	boolean requestWindowFeature(int featureId) {
		if (featureId == Window.FEATURE_ACTION_BAR_OVERLAY) {
			// TODO Make action bar partially transparent
			return true;
		}
		if (featureId == Window.FEATURE_ACTION_MODE_OVERLAY) {
			// TODO Make action modes partially transparent
			return true;
		}
		return false;
	}
	
	@Override
	void onMenuVisibilityChanged(boolean isVisible) {
		//Marshal to all listeners
		for (OnMenuVisibilityListener listener : mMenuListeners) {
			listener.onMenuVisibilityChanged(isVisible);
		}
	}
	
	// ------------------------------------------------------------------------
	// ACTION MODE METHODS
	// ------------------------------------------------------------------------

	@Override
	ActionMode startActionMode(ActionMode.Callback callback) {
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
			
			setIcon(itemData.getIcon());
			setTitle(itemData.getTitle());
			setEnabled(itemData.isEnabled());
			setCheckable(itemData.isCheckable());
			setChecked(itemData.isChecked());
			setActionView(itemData.getActionView());
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
			//TODO mItem.setTitle(title);
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
