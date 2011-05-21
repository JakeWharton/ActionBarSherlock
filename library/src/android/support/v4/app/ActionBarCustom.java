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
import com.jakewharton.android.actionbarsherlock.R;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.MenuBuilder;
import android.support.v4.view.MenuItem;
import android.support.v4.view.MenuItemImpl;
import android.support.v4.view.Window;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

final class ActionBarCustom extends ActionBar {
	static final class LogoLoader {
		static Drawable loadLogo(FragmentActivity activity) {
			Drawable logo = null;
			try {
				//Try to load the logo from the Activity's manifest entry
				logo = activity.getPackageManager().getActivityLogo(activity.getComponentName());
			} catch (NameNotFoundException e) {}
			
			if (logo == null) {
				//Try to load the logo from the Application's manifest entry
				logo = activity.getApplicationInfo().loadLogo(activity.getPackageManager());
			}
			
			return logo;
		}
	}
	
	static final class Dropdown extends PopupWindow implements View.OnClickListener {
		private final LayoutInflater mInflater;
		private SpinnerAdapter mAdapter;
		private OnClickListener mListener;
		private View mParent;
		
		Dropdown(Context context) {
			super(context);
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			setFocusable(true);
			setWindowLayoutMode(0, LayoutParams.WRAP_CONTENT);
		}
		
		public Dropdown setAdapter(SpinnerAdapter adapter, OnClickListener listener) {
			this.mAdapter = adapter;
			this.mListener = listener;
			return this;
		}
		
		public Dropdown setParent(View parent) {
			this.mParent = parent;
			return this;
		}
		
		public void show() {
			View contentView = this.mInflater.inflate(R.layout.actionbar_list_dropdown, null, false);
			LinearLayout list = (LinearLayout) contentView.findViewById(R.id.actionbar_list_dropdown);
			for (int i = 0; i < this.mAdapter.getCount(); i++) {
				View item = this.mAdapter.getDropDownView(i, null, list);
				item.setFocusable(true);
				item.setTag(new Integer(i));
				item.setOnClickListener(this);
				list.addView(item);
			}

			setContentView(contentView);
			setWidth(this.mParent.getWidth());
			showAsDropDown(this.mParent);
		}

		@Override
		public void onClick(View view) {
			dismiss();
			this.mListener.onClick(null, (Integer)view.getTag());
		}
	}

	

	//TODO make this dynamic based on width of the action bar
	private static final int MAX_ACTION_BAR_ITEMS = 3;
	
	
	/** Action bar view. */
	private View mBarView;
	
	/** Activity content view. */
	private FrameLayout mContentView;
	
	/** Home logo. */
	private ImageView mHomeLogo;
	
	/** Home icon. */
	private ImageView mHomeIcon;
	
	/** Home button up indicator. */
	private View mHomeAsUp;
	
	/** Title view. */
	private TextView mTitleView;
	
	/** Subtitle view. */
	private TextView mSubtitleView;
	
	/** List view. */
	private FrameLayout mListView;
	
	/** List dropdown indicator. */
	private View mListIndicator;
	
	/** Custom view parent. */
	private FrameLayout mCustomView;
	
	/** Container for all action items. */
	private LinearLayout mActionsView;

	/** Container for all tab items. */
	private LinearLayout mTabsView;
	
	/**
	 * Display state flags.
	 * 
	 * @see #getDisplayOptions()
	 * @see #getDisplayOptionValue(int)
	 * @see #setDisplayOptions(int)
	 * @see #setDisplayOptions(int, int)
	 * @see #setDisplayOption(int, boolean)
	 * @see #reloadDisplay()
	 */
	private int mFlags;
	
	/**
	 * Current navigation mode
	 * 
	 * @see #getNavigationMode()
	 * @see #setNavigationMode(int)
	 */
	private int mNavigationMode;
	
	/**
	 * Current selected index of either the list or tab navigation.
	 */
	private int mSelectedIndex;
	
	/**
	 * Adapter for the list navigation contents.
	 */
	private SpinnerAdapter mListAdapter;
	
	/**
	 * Callback for the list navigation event.
	 */
	private ActionBar.OnNavigationListener mListCallback;
	
	/**
	 * List of listeners to the menu visibility.
	 */
	private final List<OnMenuVisibilityListener> mMenuListeners = new ArrayList<OnMenuVisibilityListener>();

	/**
	 * Listener for list title click. Will display a list dialog of all the
	 * options provided and execute the specified {@link OnNavigationListener}.
	 */
	private final View.OnClickListener mListClicked = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mListAdapter != null) {
				new Dropdown(v.getContext())
						.setAdapter(mListAdapter, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int position) {
								//Execute call back, if exists
								if (mListCallback != null) {
									mListCallback.onNavigationItemSelected(position, mListAdapter.getItemId(position));
								}
								
								if (position != mSelectedIndex) {
									mSelectedIndex = position;
									reloadDisplay();
								}
							}
						})
						.setParent(mListView)
						.show();
			}
		}
	};

	/**
	 * Listener for action item click.
	 */
	private final View.OnClickListener mActionClicked = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			final MenuItemImpl item = (MenuItemImpl)view.getTag();
			if (item.isCheckable()) {
				item.setChecked(!item.isChecked());
			}
			if (item.getOnMenuItemClickListener() != null) {
				item.getOnMenuItemClickListener().onMenuItemClick(item);
			}
			if (item.getIntent() != null) {
				getActivity().startActivity(item.getIntent());
			}
		}
	};

	
	
	// ------------------------------------------------------------------------
	// PRIVATE HELPER METHODS
	// ------------------------------------------------------------------------
	
	/**
	 * Helper to set a flag to a new value.
	 * 
	 * @param flag Flag to update.
	 * @param enabled New value.
	 */
	private void setDisplayOption(int flag, boolean enabled) {
		//Remove current value and OR with new value
		this.mFlags = (this.mFlags & ~flag) | (enabled ? flag : 0);
	}
	
	/**
	 * Helper to get a boolean value for a specific flag.
	 * 
	 * @param flag Target flag.
	 * @return Value.
	 */
	private boolean getDisplayOptionValue(int flag) {
		return (this.mFlags & flag) != 0;
	}
	
	/**
	 * Reload the current action bar display state.
	 */
	private void reloadDisplay() {
		final boolean isList = this.mNavigationMode == ActionBar.NAVIGATION_MODE_LIST;
		final boolean isTab = this.mNavigationMode == NAVIGATION_MODE_TABS;
		final boolean hasList = (this.mListAdapter != null) && (this.mListAdapter.getCount() > 0);
		final boolean showingTitle = getDisplayOptionValue(ActionBar.DISPLAY_SHOW_TITLE);
		final boolean showingCustom = getDisplayOptionValue(ActionBar.DISPLAY_SHOW_CUSTOM);
		final boolean usingLogo = getDisplayOptionValue(ActionBar.DISPLAY_USE_LOGO);
		final boolean hasSubtitle = (this.mSubtitleView.getText() != null) && !this.mSubtitleView.getText().equals(""); 
		
		if (getDisplayOptionValue(ActionBar.DISPLAY_SHOW_HOME)) {
			this.mHomeAsUp.setVisibility(getDisplayOptionValue(ActionBar.DISPLAY_HOME_AS_UP) ? View.VISIBLE : View.GONE);
			this.mHomeLogo.setVisibility(usingLogo ? View.VISIBLE : View.GONE);
			this.mHomeIcon.setVisibility(usingLogo ? View.GONE : View.VISIBLE);
		} else {
			this.mHomeAsUp.setVisibility(View.GONE);
			this.mHomeLogo.setVisibility(View.GONE);
			this.mHomeIcon.setVisibility(View.GONE);
		}
		
		//If we are a list, set the list view to the currently selected item
		if (isList) {
			View oldView = this.mListView.getChildAt(0);
			this.mListView.removeAllViews();
			if (hasList) {
				this.mListView.addView(this.mListAdapter.getView(this.mSelectedIndex, oldView, this.mListView));
				this.mListView.getChildAt(0).setOnClickListener(this.mListClicked);
			}
		}
		
		//Only show list if we are in list navigation and there are list items
		this.mListView.setVisibility(isList && hasList ? View.VISIBLE : View.GONE);
		this.mListIndicator.setVisibility(isList && hasList ? View.VISIBLE : View.GONE);

		// Show tabs if in tabs navigation mode.
		this.mTabsView.setVisibility(isTab ? View.VISIBLE : View.GONE);
		
		//Show title view if we are not in list navigation, not showing custom
		//view, and the show title flag is true
		this.mTitleView.setVisibility(!isList && !isTab && !showingCustom && showingTitle ? View.VISIBLE : View.GONE);
		//Show subtitle view if we are not in list navigation, not showing
		//custom view, show title flag is true, and a subtitle is set
		this.mSubtitleView.setVisibility(!isList && !isTab && !showingCustom && showingTitle && hasSubtitle ? View.VISIBLE : View.GONE);
		//Show custom view if we are not in list navigation and showing custom
		//flag is set
		this.mCustomView.setVisibility(!isList && !isTab && showingCustom ? View.VISIBLE : View.GONE);
	}
	
	// ------------------------------------------------------------------------
	// ACTION BAR SHERLOCK SUPPORT
	// ------------------------------------------------------------------------

	@Override
	void performAttach() {
		LinearLayout contentView = new LinearLayout(this.getActivity());
		contentView.setOrientation(LinearLayout.VERTICAL);
		contentView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT
		));

		this.getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getActivity().setSuperContentView(contentView);
		
		this.mBarView = this.getActivity().getLayoutInflater().inflate(R.layout.actionbar, contentView, false);
		contentView.addView(mBarView);
		
		this.mContentView = new FrameLayout(this.getActivity());
		this.mContentView.setId(R.id.actionbar_content);
		this.mContentView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT
		));
		contentView.addView(this.mContentView);
		
		
		this.mHomeLogo = (ImageView)this.mBarView.findViewById(R.id.actionbar_home_logo);
		this.mHomeIcon = (ImageView)this.mBarView.findViewById(R.id.actionbar_home_icon);
		this.mHomeAsUp = this.mBarView.findViewById(R.id.actionbar_home_is_back);

		this.mTitleView = (TextView)this.mBarView.findViewById(R.id.actionbar_title);
		this.mSubtitleView = (TextView)this.mBarView.findViewById(R.id.actionbar_subtitle);
		
		this.mListView = (FrameLayout)this.mBarView.findViewById(R.id.actionbar_list);
		this.mListIndicator = this.mBarView.findViewById(R.id.actionbar_list_indicator);
		
		this.mCustomView = (FrameLayout)this.mBarView.findViewById(R.id.actionbar_custom);
		this.mActionsView = (LinearLayout)this.mBarView.findViewById(R.id.actionbar_actions);
		this.mTabsView = (LinearLayout)this.mBarView.findViewById(R.id.actionbar_tabs);

		ComponentName componentName = this.getActivity().getComponentName();
		PackageManager packageManager = this.getActivity().getPackageManager();
		
		//Try to load title from the Activity's manifest entry
		CharSequence title;
		try {
			title = packageManager.getActivityInfo(componentName, PackageManager.GET_ACTIVITIES).loadLabel(packageManager);
		} catch (NameNotFoundException e) {
			//Can't load/find activity title. Set a default.
			title = this.getActivity().getApplicationInfo().loadLabel(packageManager);
		}
		if ((title == null) || (title.equals(""))) {
			//Still no title? Fall back to activity class name
			title = this.getActivity().getClass().getSimpleName();
		}
		this.setTitle(title);
		
		//Load icon from the Activity's manifest entry
		Drawable icon;
		try {
			icon = packageManager.getActivityIcon(componentName);
		} catch (NameNotFoundException e) {
			//Can't load/find activity icon. Get application icon or default.
			icon = packageManager.getApplicationIcon(this.getActivity().getApplicationInfo());
		}
		this.mHomeIcon.setImageDrawable(icon);
		
		//Must be >= gingerbread to look for a logo
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			Drawable logo = LogoLoader.loadLogo(this.getActivity());
			if (logo != null) {
				this.setHomeLogo(logo);
			}
		}
		
		//Show the title and home icon by default
		this.setDisplayOption(ActionBar.DISPLAY_SHOW_TITLE, true);
		this.setDisplayOption(ActionBar.DISPLAY_SHOW_HOME, true);
		//Use standard navigation by default (this will call reloadDisplay)
		this.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}

	@Override
	public void onMenuInflated(MenuBuilder menu) {
		//Iterate and grab as many actions as we can up to MAX_ACTION_BAR_ITEMS
		//honoring their showAsAction values
		
		int ifItems = 0;
		final int count = menu.size();
		List<MenuItemImpl> keep = new ArrayList<MenuItemImpl>();
		for (int i = 0; i < count; i++) {
			MenuItemImpl item = menu.getItem(i);
			if ((item.getShowAsAction() & MenuItem.SHOW_AS_ACTION_ALWAYS) != 0) {
				//Show always therefore add to keep list
				keep.add(item);
				
				if ((keep.size() > MAX_ACTION_BAR_ITEMS) && (ifItems > 0)) {
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
					&& (keep.size() < MAX_ACTION_BAR_ITEMS)) {
				//"ifRoom" items are added if we have not exceeded the max.
				keep.add(item);
				ifItems += 1;
			}
		}
		
		//Update action bar display
		this.mActionsView.removeAllViews();
		for (MenuItemImpl item : keep) {
			//Make sure this item isn't displayed on the options menu
			item.setIsShownOnActionBar(true);
			//Add the item view to our action bar view
			View view = item.getActionBarView();
			view.setOnClickListener(this.mActionClicked);
			this.mActionsView.addView(view);
		}
	}

	@Override
	protected void setContentView(int layoutResId) {
		this.getActivity().getLayoutInflater().inflate(layoutResId, this.mContentView, true);
	}

	@Override
	protected void setContentView(View view) {
		this.mContentView.addView(view);
	}

	@Override
	protected void setContentView(View view, ViewGroup.LayoutParams params) {
		this.mContentView.addView(view, params);
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
	public void onMenuVisibilityChanged(boolean isVisible) {
		//Marshal to all listeners
		for (OnMenuVisibilityListener listener : this.mMenuListeners) {
			listener.onMenuVisibilityChanged(isVisible);
		}
	}
	
	// ------------------------------------------------------------------------
	// ACTION BAR METHODS
	// ------------------------------------------------------------------------

	@Override
	public void addOnMenuVisibilityListener(OnMenuVisibilityListener listener) {
		if (!this.mMenuListeners.contains(listener)) {
			this.mMenuListeners.add(listener);
		}
	}

	@Override
	public void addTab(ActionBar.Tab tab, int position, boolean setSelected) {
		this.mTabsView.addView(((TabImpl)tab).mView, position);
		if (setSelected) {
			tab.select();
		}
	}
	
	@Override
	public View getCustomView() {
		return this.mCustomView.getChildAt(0);
	}
	
	@Override
	public int getDisplayOptions() {
		return this.mFlags;
	}

	@Override
	public int getHeight() {
		return this.mBarView.getHeight();
	}

	@Override
	public int getNavigationItemCount() {
		if (this.mNavigationMode == ActionBar.NAVIGATION_MODE_LIST) {
			return this.mListAdapter.getCount();
		}
		if (this.mNavigationMode == ActionBar.NAVIGATION_MODE_TABS) {
			return this.mTabsView.getChildCount();
		}
		return 0;
	}

	@Override
	public int getNavigationMode() {
		return this.mNavigationMode;
	}

	@Override
	public int getSelectedNavigationIndex() {
		if (this.mNavigationMode == ActionBar.NAVIGATION_MODE_LIST) {
			return this.mSelectedIndex;
		}
		if (this.mNavigationMode == ActionBar.NAVIGATION_MODE_TABS) {
			final int count = this.mTabsView.getChildCount();
			for (int i = 0; i < count; i++) {
				if (((TabImpl)this.mTabsView.getChildAt(i).getTag()).mView.isSelected()) {
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public TabImpl getSelectedTab() {
		final int count = this.mTabsView.getChildCount();
		for (int i = 0; i < count; i++) {
			TabImpl tab = (TabImpl)this.mTabsView.getChildAt(i).getTag();
			if (tab.mView.isSelected()) {
				return tab;
			}
		}
		return null;
	}

	@Override
	public CharSequence getSubtitle() {
		if ((this.mNavigationMode == ActionBar.NAVIGATION_MODE_STANDARD) && !this.mSubtitleView.getText().equals("")) {
			return this.mSubtitleView.getText();
		} else {
			return null;
		}
	}

	@Override
	public TabImpl getTabAt(int index) {
		View view = this.mTabsView.getChildAt(index);
		return (view != null) ? (TabImpl)view.getTag() : null;
	}

	@Override
	public int getTabCount() {
		return this.mTabsView.getChildCount();
	}

	@Override
	public CharSequence getTitle() {
		if ((this.mNavigationMode == ActionBar.NAVIGATION_MODE_STANDARD) && !this.mTitleView.getText().equals("")) {
			return this.mTitleView.getText();
		} else {
			return null;
		}
	}

	@Override
	public void hide() {
		this.mBarView.setVisibility(View.GONE);
	}

	@Override
	public boolean isShowing() {
		return this.mBarView.getVisibility() == View.VISIBLE;
	}
	
	@Override
	public TabImpl newTab() {
		return new TabImpl(this);
	}

	@Override
	public void removeAllTabs() {
		TabImpl selected = this.getSelectedTab();
		if (selected != null) {
			selected.unselect();
		}
		this.mTabsView.removeAllViews();
	}

	@Override
	public void removeOnMenuVisibilityListener(OnMenuVisibilityListener listener) {
		this.mMenuListeners.remove(listener);
	}

	@Override
	public void removeTab(ActionBar.Tab tab) {
		final int count = this.mTabsView.getChildCount();
		for (int i = 0; i < count; i++) {
			TabImpl existingTab = (TabImpl)this.mTabsView.getChildAt(i).getTag();
			if (existingTab.equals(tab)) {
				this.removeTabAt(i);
				break;
			}
		}
	}

	@Override
	public void removeTabAt(int position) {
		TabImpl tab = (TabImpl)this.getTabAt(position);
		if (tab != null) {
			tab.unselect();
			this.mTabsView.removeViewAt(position);
		
			if (position > 0) {
				//Select previous tab
				((TabImpl)this.mTabsView.getChildAt(position - 1).getTag()).select();
			} else if (this.mTabsView.getChildCount() > 0) {
				//Select first tab
				((TabImpl)this.mTabsView.getChildAt(0).getTag()).select();
			}
		}
	}

	@Override
	public void setBackgroundDrawable(Drawable d) {
		this.mBarView.setBackgroundDrawable(d);
	}

	@Override
	public void setCustomView(int resId) {
		this.mCustomView.removeAllViews();
		this.getActivity().getLayoutInflater().inflate(resId, this.mCustomView, true);
		this.setDisplayShowCustomEnabled(true);
	}

	@Override
	public void setCustomView(View view) {
		this.mCustomView.removeAllViews();
		this.mCustomView.addView(view);
		this.setDisplayShowCustomEnabled(true);
	}
	
	@Override
	public void setCustomView(View view, LayoutParams layoutParams) {
		this.mCustomView.removeAllViews();
		this.mCustomView.addView(view, layoutParams);
		this.setDisplayShowCustomEnabled(true);
	}

	@Override
	public void setDisplayOptions(int options, int mask) {
		this.mFlags = (this.mFlags & ~mask) | options;
		this.reloadDisplay();
	}

	@Override
	public void setDisplayOptions(int options) {
		this.mFlags = options;
		this.reloadDisplay();
	}

	@Override
	public void setListNavigationCallbacks(SpinnerAdapter adapter, ActionBar.OnNavigationListener callback) {
		//Reset selected item
		this.mSelectedIndex = 0;
		//Save adapter and callback
		this.mListAdapter = adapter;
		this.mListCallback = callback;
		
		this.reloadDisplay();
	}

	@Override
	public void setNavigationMode(int mode) {
		if ((mode != ActionBar.NAVIGATION_MODE_STANDARD) && (mode != ActionBar.NAVIGATION_MODE_LIST)
				&& (mode != ActionBar.NAVIGATION_MODE_TABS)) {
			throw new IllegalArgumentException("Unknown navigation mode value " + Integer.toString(mode));
		}
		
		if (mode != this.mNavigationMode) {
			this.mNavigationMode = mode;
			this.mSelectedIndex = (mode == ActionBar.NAVIGATION_MODE_STANDARD) ? -1 : 0;
			this.reloadDisplay();
		}
	}

	@Override
	public void setSelectedNavigationItem(int position) {
		if ((this.mNavigationMode != ActionBar.NAVIGATION_MODE_STANDARD) && (position != this.mSelectedIndex)) {
			this.mSelectedIndex = position;
			this.reloadDisplay();
		}
	}

	@Override
	public void selectTab(ActionBar.Tab tab) {
		final int count = this.mTabsView.getChildCount();
		for (int i = 0; i < count; i++) {
			TabImpl existingTab = (TabImpl)this.mTabsView.getChildAt(i).getTag();
			if (existingTab.equals(tab)) {
				existingTab.select();
				break;
			}
		}
	}

	@Override
	public void setSubtitle(CharSequence subtitle) {
		this.mSubtitleView.setText((subtitle == null) ? "" : subtitle);
		this.reloadDisplay();
	}

	@Override
	public void setTitle(CharSequence title) {
		this.mTitleView.setText((title == null) ? "" : title);
	}

	@Override
	public void show() {
		this.mBarView.setVisibility(View.VISIBLE);
	}
	
	// ------------------------------------------------------------------------
	// LEGACY AND DEPRECATED METHODS
	// ------------------------------------------------------------------------

	/**
	 * <p>Set a logo for the action bar.</p>
	 * 
	 * <p>You must call {@link #setDisplayUseLogoEnabled(boolean)} or
	 * either {@link #setDisplayOptions(int)} or
	 * {@link #setDisplayOptions(int, int)} with the {@link #DISPLAY_USE_LOGO}
	 * flag.</p>
	 * 
	 * <p><em>Note:</em> For forward compatibility you should also specify your
	 * logo in the {@code android:logo} attribute of the entry for the activity
	 * and/or the application in the manifest.</p>
	 * 
	 * @param resId Resource ID of the logo.
	 * 
	 * @see #setHomeLogo(Drawable)
	 */
	public void setHomeLogo(int resId) {
		this.mHomeLogo.setImageResource(resId);
	}

	/**
	 * <p>Set a logo for the action bar.</p>
	 * 
	 * <p>You must call {@link #setDisplayUseLogoEnabled(boolean)} or
	 * either {@link #setDisplayOptions(int)} or
	 * {@link #setDisplayOptions(int, int)} with the {@link #DISPLAY_USE_LOGO}
	 * flag.</p>
	 * 
	 * <p><em>Note:</em> For forward compatibility you should also specify your
	 * logo in the {@code android:logo} attribute of the entry for the activity
	 * and/or the application in the manifest.</p>
	 * 
	 * @param logo Drawable logo.
	 * 
	 * @see #setHomeLogo(int)
	 */
	public void setHomeLogo(Drawable logo) {
		this.mHomeLogo.setImageDrawable(logo);
	}
	
	// ------------------------------------------------------------------------
	// HELPER INTERFACES AND HELPER CLASSES
	// ------------------------------------------------------------------------
	
	private static class TabImpl extends ActionBar.Tab implements View.OnClickListener {
		final ActionBarCustom mActionBar;
		final View mView;
		final ImageView mIconView;
		final TextView mTextView;
		final FrameLayout mCustomView;
		
		ActionBar.TabListener mListener;
		Object mTag;
		
		
		TabImpl(ActionBarCustom actionBar) {
			this.mActionBar = actionBar;
			this.mView = actionBar.getActivity().getLayoutInflater().inflate(R.layout.actionbar_tab, actionBar.mTabsView, false);
			this.mView.setTag(this);
			this.mView.setOnClickListener(this);
			
			this.mIconView = (ImageView)this.mView.findViewById(R.id.actionbar_tab_icon);
			this.mTextView = (TextView)this.mView.findViewById(R.id.actionbar_tab);
			this.mCustomView = (FrameLayout)this.mView.findViewById(R.id.actionbar_tab_custom);
		}
		
		/**
		 * Update display to reflect current property state.
		 */
		void reloadDisplay() {
			boolean hasCustom = this.mCustomView.getChildCount() > 0;
			this.mIconView.setVisibility(hasCustom ? View.GONE : View.VISIBLE);
			this.mTextView.setVisibility(hasCustom ? View.GONE : View.VISIBLE);
			this.mCustomView.setVisibility(hasCustom ? View.VISIBLE : View.GONE);
		}

		@Override
		public View getCustomView() {
			return this.mCustomView.getChildAt(0);
		}

		@Override
		public Drawable getIcon() {
			return this.mIconView.getDrawable();
		}

		@Override
		public int getPosition() {
			final int count = this.mActionBar.mTabsView.getChildCount();
			for (int i = 0; i < count; i++) {
				if (this.mActionBar.mTabsView.getChildAt(i).getTag().equals(this)) {
					return i;
				}
			}
			return -1;
		}

		@Override
		public TabListener getTabListener() {
			return this.mListener;
		}

		@Override
		public Object getTag() {
			return this.mTag;
		}

		@Override
		public CharSequence getText() {
			return mTextView.getText();
		}

		@Override
		public TabImpl setCustomView(int layoutResId) {
			this.mCustomView.removeAllViews();
			this.mActionBar.getActivity().getLayoutInflater().inflate(layoutResId, this.mCustomView, true);
			this.reloadDisplay();
			return this;
		}

		@Override
		public TabImpl setCustomView(View view) {
			this.mCustomView.removeAllViews();
			if (view != null) {
				this.mCustomView.addView(view);
			}
			this.reloadDisplay();
			return this;
		}

		@Override
		public TabImpl setIcon(Drawable icon) {
			this.mIconView.setImageDrawable(icon);
			return this;
		}

		@Override
		public TabImpl setIcon(int resId) {
			this.mIconView.setImageResource(resId);
			return this;
		}

		@Override
		public TabImpl setTabListener(TabListener listener) {
			this.mListener = listener;
			return this;
		}

		@Override
		public TabImpl setTag(Object obj) {
			this.mTag = obj;
			return this;
		}

		@Override
		public TabImpl setText(int resId) {
			this.mTextView.setText(resId);
			return this;
		}

		@Override
		public TabImpl setText(CharSequence text) {
			this.mTextView.setText(text);
			return this;
		}

		@Override
		public void select() {
			if (this.mView.isSelected()) {
				if (this.mListener != null) {
					this.mListener.onTabReselected(this, null);
				}
				return;
			}
			
			TabImpl current = this.mActionBar.getSelectedTab();
			if (current != null) {
				current.unselect();
			}
			
			this.mView.setSelected(true);
			if (this.mListener != null) {
				this.mListener.onTabSelected(this, null);
			}
		}

		/**
		 * Unselect this tab. Only valid if the tab has been added to the
		 * action bar and was previously selected.
		 */
		void unselect() {
			if (this.mView.isSelected()) {
				this.mView.setSelected(false);

				if (this.mListener != null) {
					this.mListener.onTabUnselected(this, null);
				}
			}
		}

		@Override
		public void onClick(View v) {
			this.select();
		}
	}
}
