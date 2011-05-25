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
import android.support.v4.view.ActionMode;
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
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			setFocusable(true);
			setWindowLayoutMode(0, LayoutParams.WRAP_CONTENT);
		}
		
		public Dropdown setAdapter(SpinnerAdapter adapter, OnClickListener listener) {
			mAdapter = adapter;
			mListener = listener;
			return this;
		}
		
		public Dropdown setParent(View parent) {
			mParent = parent;
			return this;
		}
		
		public void show() {
			View contentView = mInflater.inflate(R.layout.actionbar_list_dropdown, null, false);
			LinearLayout list = (LinearLayout) contentView.findViewById(R.id.actionbar_list_dropdown);
			for (int i = 0; i < mAdapter.getCount(); i++) {
				View item = mAdapter.getDropDownView(i, null, list);
				item.setFocusable(true);
				item.setTag(new Integer(i));
				item.setOnClickListener(this);
				list.addView(item);
			}

			setContentView(contentView);
			setWidth(mParent.getWidth());
			showAsDropDown(mParent);
		}

		@Override
		public void onClick(View view) {
			dismiss();
			mListener.onClick(null, (Integer)view.getTag());
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
	private int mNavigationMode = -1;
	
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
			boolean handled = false;
			if (item.getOnMenuItemClickListener() != null) {
				handled = item.getOnMenuItemClickListener().onMenuItemClick(item);
			}
			if (item.getIntent() != null) {
				getActivity().startActivity(item.getIntent());
			}
			if (!handled) {
				getActivity().onOptionsItemSelected((MenuItemImpl)view.getTag());
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
		mFlags = (mFlags & ~flag) | (enabled ? flag : 0);
	}
	
	/**
	 * Helper to get a boolean value for a specific flag.
	 * 
	 * @param flag Target flag.
	 * @return Value.
	 */
	private boolean getDisplayOptionValue(int flag) {
		return (mFlags & flag) == flag;
	}
	
	/**
	 * Reload the current action bar display state.
	 */
	private void reloadDisplay() {
		final boolean isStandard = mNavigationMode == ActionBar.NAVIGATION_MODE_STANDARD;
		final boolean isList = mNavigationMode == ActionBar.NAVIGATION_MODE_LIST;
		final boolean isTab = mNavigationMode == ActionBar.NAVIGATION_MODE_TABS;
		final boolean hasList = (mListAdapter != null) && (mListAdapter.getCount() > 0);
		final boolean showingTitle = getDisplayOptionValue(ActionBar.DISPLAY_SHOW_TITLE);
		final boolean showingCustom = getDisplayOptionValue(ActionBar.DISPLAY_SHOW_CUSTOM);
		final boolean usingLogo = getDisplayOptionValue(ActionBar.DISPLAY_USE_LOGO);
		final boolean hasSubtitle = (mSubtitleView.getText() != null) && !mSubtitleView.getText().equals(""); 
		
		if (getDisplayOptionValue(ActionBar.DISPLAY_SHOW_HOME)) {
			mHomeAsUp.setVisibility(getDisplayOptionValue(ActionBar.DISPLAY_HOME_AS_UP) ? View.VISIBLE : View.GONE);
			mHomeLogo.setVisibility(usingLogo ? View.VISIBLE : View.GONE);
			mHomeIcon.setVisibility(usingLogo ? View.GONE : View.VISIBLE);
		} else {
			mHomeAsUp.setVisibility(View.GONE);
			mHomeLogo.setVisibility(View.GONE);
			mHomeIcon.setVisibility(View.GONE);
		}
		
		//If we are a list, set the list view to the currently selected item
		if (isList) {
			View oldView = mListView.getChildAt(0);
			mListView.removeAllViews();
			if (hasList) {
				mListView.addView(mListAdapter.getView(mSelectedIndex, oldView, mListView));
				mListView.getChildAt(0).setOnClickListener(mListClicked);
			}
		}
		
		//Only show list if we are in list navigation and there are list items
		mListView.setVisibility(isList && hasList ? View.VISIBLE : View.GONE);
		mListIndicator.setVisibility(isList && hasList ? View.VISIBLE : View.GONE);

		// Show tabs if in tabs navigation mode.
		mTabsView.setVisibility(isTab ? View.VISIBLE : View.GONE);
		
		//Show title view if we are not in list navigation, not showing custom
		//view, and the show title flag is true
		mTitleView.setVisibility(isStandard && !showingCustom && showingTitle ? View.VISIBLE : View.GONE);
		//Show subtitle view if we are not in list navigation, not showing
		//custom view, show title flag is true, and a subtitle is set
		mSubtitleView.setVisibility(isStandard && !showingCustom && showingTitle && hasSubtitle ? View.VISIBLE : View.GONE);
		//Show custom view if we are not in list navigation and showing custom
		//flag is set
		mCustomView.setVisibility(isStandard && showingCustom ? View.VISIBLE : View.GONE);
	}
	
	// ------------------------------------------------------------------------
	// ACTION BAR SHERLOCK SUPPORT
	// ------------------------------------------------------------------------

	@Override
	void performAttach() {
		LinearLayout contentView = new LinearLayout(getActivity());
		contentView.setOrientation(LinearLayout.VERTICAL);
		contentView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT
		));

		getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getActivity().setSuperContentView(contentView);
		
		mBarView = getActivity().getLayoutInflater().inflate(R.layout.actionbar, contentView, false);
		contentView.addView(mBarView);
		
		mContentView = new FrameLayout(getActivity());
		mContentView.setId(R.id.actionbar_content);
		mContentView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT
		));
		contentView.addView(mContentView);
		
		
		mHomeLogo = (ImageView)mBarView.findViewById(R.id.actionbar_home_logo);
		mHomeIcon = (ImageView)mBarView.findViewById(R.id.actionbar_home_icon);
		mHomeAsUp = mBarView.findViewById(R.id.actionbar_home_is_back);

		mTitleView = (TextView)mBarView.findViewById(R.id.actionbar_title);
		mSubtitleView = (TextView)mBarView.findViewById(R.id.actionbar_subtitle);
		
		mListView = (FrameLayout)mBarView.findViewById(R.id.actionbar_list);
		mListIndicator = mBarView.findViewById(R.id.actionbar_list_indicator);
		
		mCustomView = (FrameLayout)mBarView.findViewById(R.id.actionbar_custom);
		mActionsView = (LinearLayout)mBarView.findViewById(R.id.actionbar_actions);
		mTabsView = (LinearLayout)mBarView.findViewById(R.id.actionbar_tabs);

		ComponentName componentName = getActivity().getComponentName();
		PackageManager packageManager = getActivity().getPackageManager();
		
		//Try to load title from the Activity's manifest entry
		CharSequence title;
		try {
			title = packageManager.getActivityInfo(componentName, PackageManager.GET_ACTIVITIES).loadLabel(packageManager);
		} catch (NameNotFoundException e) {
			//Can't load/find activity title. Set a default.
			title = getActivity().getApplicationInfo().loadLabel(packageManager);
		}
		if ((title == null) || (title.equals(""))) {
			//Still no title? Fall back to activity class name
			title = getActivity().getClass().getSimpleName();
		}
		setTitle(title);
		
		//Load icon from the Activity's manifest entry
		Drawable icon;
		try {
			icon = packageManager.getActivityIcon(componentName);
		} catch (NameNotFoundException e) {
			//Can't load/find activity icon. Get application icon or default.
			icon = packageManager.getApplicationIcon(getActivity().getApplicationInfo());
		}
		mHomeIcon.setImageDrawable(icon);
		
		//Must be >= gingerbread to look for a logo
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			Drawable logo = LogoLoader.loadLogo(getActivity());
			if (logo != null) {
				setHomeLogo(logo);
			}
		}
		
		//Show the title and home icon by default
		setDisplayOption(ActionBar.DISPLAY_SHOW_TITLE, true);
		setDisplayOption(ActionBar.DISPLAY_SHOW_HOME, true);
		//Use standard navigation by default (this will call reloadDisplay)
		setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
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
		mActionsView.removeAllViews();
		for (MenuItemImpl item : keep) {
			//Make sure this item isn't displayed on the options menu
			item.setIsShownOnActionBar(true);
			//Add the item view to our action bar view
			View view = item.getActionBarView();
			view.setOnClickListener(mActionClicked);
			mActionsView.addView(view);
		}
	}

	@Override
	protected void setContentView(int layoutResId) {
		getActivity().getLayoutInflater().inflate(layoutResId, mContentView, true);
	}

	@Override
	protected void setContentView(View view) {
		mContentView.addView(view);
	}

	@Override
	protected void setContentView(View view, ViewGroup.LayoutParams params) {
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
	public void onMenuVisibilityChanged(boolean isVisible) {
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
	public void addTab(ActionBar.Tab tab, int position, boolean setSelected) {
		mTabsView.addView(((TabImpl)tab).mView, position);
		if (setSelected) {
			tab.select();
		}
	}
	
	@Override
	public View getCustomView() {
		return mCustomView.getChildAt(0);
	}
	
	@Override
	public int getDisplayOptions() {
		return mFlags;
	}

	@Override
	public int getHeight() {
		return mBarView.getHeight();
	}

	@Override
	public int getNavigationItemCount() {
		if (mNavigationMode == ActionBar.NAVIGATION_MODE_LIST) {
			return mListAdapter.getCount();
		}
		if (mNavigationMode == ActionBar.NAVIGATION_MODE_TABS) {
			return mTabsView.getChildCount();
		}
		return 0;
	}

	@Override
	public int getNavigationMode() {
		return mNavigationMode;
	}

	@Override
	public int getSelectedNavigationIndex() {
		if (mNavigationMode == ActionBar.NAVIGATION_MODE_LIST) {
			return mSelectedIndex;
		}
		if (mNavigationMode == ActionBar.NAVIGATION_MODE_TABS) {
			final int count = mTabsView.getChildCount();
			for (int i = 0; i < count; i++) {
				if (((TabImpl)mTabsView.getChildAt(i).getTag()).mView.isSelected()) {
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public TabImpl getSelectedTab() {
		final int count = mTabsView.getChildCount();
		for (int i = 0; i < count; i++) {
			TabImpl tab = (TabImpl)mTabsView.getChildAt(i).getTag();
			if (tab.mView.isSelected()) {
				return tab;
			}
		}
		return null;
	}

	@Override
	public CharSequence getSubtitle() {
		if ((mNavigationMode == ActionBar.NAVIGATION_MODE_STANDARD) && !mSubtitleView.getText().equals("")) {
			return mSubtitleView.getText();
		} else {
			return null;
		}
	}

	@Override
	public TabImpl getTabAt(int index) {
		View view = mTabsView.getChildAt(index);
		return (view != null) ? (TabImpl)view.getTag() : null;
	}

	@Override
	public int getTabCount() {
		return mTabsView.getChildCount();
	}

	@Override
	public CharSequence getTitle() {
		if ((mNavigationMode == ActionBar.NAVIGATION_MODE_STANDARD) && !mTitleView.getText().equals("")) {
			return mTitleView.getText();
		} else {
			return null;
		}
	}

	@Override
	public void hide() {
		mBarView.setVisibility(View.GONE);
	}

	@Override
	public boolean isShowing() {
		return mBarView.getVisibility() == View.VISIBLE;
	}
	
	@Override
	public TabImpl newTab() {
		return new TabImpl(this);
	}

	@Override
	public void removeAllTabs() {
		TabImpl selected = getSelectedTab();
		if (selected != null) {
			selected.unselect();
		}
		mTabsView.removeAllViews();
	}

	@Override
	public void removeOnMenuVisibilityListener(OnMenuVisibilityListener listener) {
		mMenuListeners.remove(listener);
	}

	@Override
	public void removeTab(ActionBar.Tab tab) {
		final int count = mTabsView.getChildCount();
		for (int i = 0; i < count; i++) {
			TabImpl existingTab = (TabImpl)mTabsView.getChildAt(i).getTag();
			if (existingTab.equals(tab)) {
				removeTabAt(i);
				break;
			}
		}
	}

	@Override
	public void removeTabAt(int position) {
		TabImpl tab = (TabImpl)getTabAt(position);
		if (tab != null) {
			tab.unselect();
			mTabsView.removeViewAt(position);
		
			if (position > 0) {
				//Select previous tab
				((TabImpl)mTabsView.getChildAt(position - 1).getTag()).select();
			} else if (mTabsView.getChildCount() > 0) {
				//Select first tab
				((TabImpl)mTabsView.getChildAt(0).getTag()).select();
			}
		}
	}

	@Override
	public void setBackgroundDrawable(Drawable d) {
		mBarView.setBackgroundDrawable(d);
	}

	@Override
	public void setCustomView(int resId) {
		mCustomView.removeAllViews();
		getActivity().getLayoutInflater().inflate(resId, mCustomView, true);
		setDisplayShowCustomEnabled(true);
	}

	@Override
	public void setCustomView(View view) {
		mCustomView.removeAllViews();
		mCustomView.addView(view);
		setDisplayShowCustomEnabled(true);
	}
	
	@Override
	public void setCustomView(View view, LayoutParams layoutParams) {
		mCustomView.removeAllViews();
		mCustomView.addView(view, layoutParams);
		setDisplayShowCustomEnabled(true);
	}

	@Override
	public void setDisplayOptions(int options, int mask) {
		mFlags = (mFlags & ~mask) | options;
		reloadDisplay();
	}

	@Override
	public void setDisplayOptions(int options) {
		mFlags = options;
		reloadDisplay();
	}

	@Override
	public void setListNavigationCallbacks(SpinnerAdapter adapter, ActionBar.OnNavigationListener callback) {
		//Reset selected item
		mSelectedIndex = 0;
		//Save adapter and callback
		mListAdapter = adapter;
		mListCallback = callback;
		
		reloadDisplay();
	}

	@Override
	public void setNavigationMode(int mode) {
		if ((mode != ActionBar.NAVIGATION_MODE_STANDARD) && (mode != ActionBar.NAVIGATION_MODE_LIST)
				&& (mode != ActionBar.NAVIGATION_MODE_TABS)) {
			throw new IllegalArgumentException("Unknown navigation mode value " + Integer.toString(mode));
		}
		
		if (mode != mNavigationMode) {
			mNavigationMode = mode;
			mSelectedIndex = (mode == ActionBar.NAVIGATION_MODE_STANDARD) ? -1 : 0;
			reloadDisplay();
		}
	}

	@Override
	public void setSelectedNavigationItem(int position) {
		if ((mNavigationMode != ActionBar.NAVIGATION_MODE_STANDARD) && (position != mSelectedIndex)) {
			mSelectedIndex = position;
			reloadDisplay();
		}
	}

	@Override
	public void selectTab(ActionBar.Tab tab) {
		final int count = mTabsView.getChildCount();
		for (int i = 0; i < count; i++) {
			TabImpl existingTab = (TabImpl)mTabsView.getChildAt(i).getTag();
			if (existingTab.equals(tab)) {
				existingTab.select();
				break;
			}
		}
	}

	@Override
	public void setSubtitle(CharSequence subtitle) {
		mSubtitleView.setText((subtitle == null) ? "" : subtitle);
		reloadDisplay();
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitleView.setText((title == null) ? "" : title);
	}

	@Override
	public void show() {
		mBarView.setVisibility(View.VISIBLE);
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
		mHomeLogo.setImageResource(resId);
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
		mHomeLogo.setImageDrawable(logo);
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
			mActionBar = actionBar;
			mView = actionBar.getActivity().getLayoutInflater().inflate(R.layout.actionbar_tab, actionBar.mTabsView, false);
			mView.setTag(this);
			mView.setOnClickListener(this);
			
			mIconView = (ImageView)mView.findViewById(R.id.actionbar_tab_icon);
			mTextView = (TextView)mView.findViewById(R.id.actionbar_tab);
			mCustomView = (FrameLayout)mView.findViewById(R.id.actionbar_tab_custom);
		}
		
		/**
		 * Update display to reflect current property state.
		 */
		void reloadDisplay() {
			boolean hasCustom = mCustomView.getChildCount() > 0;
			mIconView.setVisibility(hasCustom ? View.GONE : View.VISIBLE);
			mTextView.setVisibility(hasCustom ? View.GONE : View.VISIBLE);
			mCustomView.setVisibility(hasCustom ? View.VISIBLE : View.GONE);
		}

		@Override
		public View getCustomView() {
			return mCustomView.getChildAt(0);
		}

		@Override
		public Drawable getIcon() {
			return mIconView.getDrawable();
		}

		@Override
		public int getPosition() {
			final int count = mActionBar.mTabsView.getChildCount();
			for (int i = 0; i < count; i++) {
				if (mActionBar.mTabsView.getChildAt(i).getTag().equals(this)) {
					return i;
				}
			}
			return -1;
		}

		@Override
		public TabListener getTabListener() {
			return mListener;
		}

		@Override
		public Object getTag() {
			return mTag;
		}

		@Override
		public CharSequence getText() {
			return mTextView.getText();
		}

		@Override
		public TabImpl setCustomView(int layoutResId) {
			mCustomView.removeAllViews();
			mActionBar.getActivity().getLayoutInflater().inflate(layoutResId, mCustomView, true);
			reloadDisplay();
			return this;
		}

		@Override
		public TabImpl setCustomView(View view) {
			mCustomView.removeAllViews();
			if (view != null) {
				mCustomView.addView(view);
			}
			reloadDisplay();
			return this;
		}

		@Override
		public TabImpl setIcon(Drawable icon) {
			mIconView.setImageDrawable(icon);
			return this;
		}

		@Override
		public TabImpl setIcon(int resId) {
			mIconView.setImageResource(resId);
			return this;
		}

		@Override
		public TabImpl setTabListener(TabListener listener) {
			mListener = listener;
			return this;
		}

		@Override
		public TabImpl setTag(Object obj) {
			mTag = obj;
			return this;
		}

		@Override
		public TabImpl setText(int resId) {
			mTextView.setText(resId);
			return this;
		}

		@Override
		public TabImpl setText(CharSequence text) {
			mTextView.setText(text);
			return this;
		}

		@Override
		public void select() {
			if (mView.isSelected()) {
				if (mListener != null) {
					mListener.onTabReselected(this, null);
				}
				return;
			}
			
			TabImpl current = mActionBar.getSelectedTab();
			if (current != null) {
				current.unselect();
			}
			
			mView.setSelected(true);
			if (mListener != null) {
				mListener.onTabSelected(this, null);
			}
		}

		/**
		 * Unselect this tab. Only valid if the tab has been added to the
		 * action bar and was previously selected.
		 */
		void unselect() {
			if (mView.isSelected()) {
				mView.setSelected(false);

				if (mListener != null) {
					mListener.onTabUnselected(this, null);
				}
			}
		}

		@Override
		public void onClick(View v) {
			select();
		}
	}
}
