package com.actionbarsherlock.internal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActionBar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.actionbarsherlock.R;

public final class ActionBarWatson extends RelativeLayout {
	/** Default display options if none are defined in the theme. */
	private static final int DEFAULT_DISPLAY_OPTIONS = ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME;
	
	/** Default navigation mode if one is not defined in the theme. */
	private static final int DEFAULT_NAVIGATION_MODE = ActionBar.NAVIGATION_MODE_STANDARD;
	
	
	
	/** Home logo and icon action item. */
	private final HomeItem mHome;
	
	/** Title view. */
	private final TextView mTitle;
	
	/** Subtitle view. */
	private final TextView mSubtitle;
	
	/** List view. */
	private final Spinner mListView;
	
	/** Custom view parent. */
	private final FrameLayout mCustomView;
	
	/** Container for all action items. */
	private final LinearLayout mActionsView;

	/** Container for all tab items. */
	private final LinearLayout mTabsView;
	
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
	
	/** Whether text is shown on action items regardless of display params. */
	private boolean mIsActionItemTextEnabled = false;
	
	

	public ActionBarWatson(Context context) {
		this(context, null);
	}
	
	public ActionBarWatson(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.actionBarStyle);
	}
	
	public ActionBarWatson(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.actionbarwatson, this, true);
		
		final TypedArray attrsActionBar = context.obtainStyledAttributes(attrs, R.styleable.SherlockActionBar, defStyle, 0);
		final TypedArray attrsTheme = context.obtainStyledAttributes(attrs, R.styleable.SherlockTheme, defStyle, 0);
		
		
		/// HOME ////
		
		mHome = (HomeItem)findViewById(R.id.actionbarwatson_home);
		
		
		//Load the up indicator
		final Drawable homeAsUpIndicator = attrsTheme.getDrawable(R.styleable.SherlockTheme_homeAsUpIndicator);
		mHome.setUpIndicator(homeAsUpIndicator);

		//Try to load the logo from the theme
		final Drawable homeLogo = attrsActionBar.getDrawable(R.styleable.SherlockActionBar_logo);
		if (homeLogo != null) {
			mHome.setLogo(homeLogo);
		}
		
		//Try to load the icon from the theme
		final Drawable homeIcon = attrsActionBar.getDrawable(R.styleable.SherlockActionBar_icon);
		mHome.setIcon(homeIcon);

		
		//// TITLE ////
		
		mTitle = (TextView)findViewById(R.id.actionbarwatson_title);
		
		//Try to load title style from the theme
		final int titleTextStyle = attrsActionBar.getResourceId(R.styleable.SherlockActionBar_titleTextStyle, 0);
		if (titleTextStyle != 0) {
			mTitle.setTextAppearance(context, titleTextStyle);
		}
		
		//Try to load title from the theme
		final CharSequence title = attrsActionBar.getString(R.styleable.SherlockActionBar_title);
		if (title != null) {
			setTitle(title);
		}
		
		
		//// SUBTITLE ////
		
		mSubtitle = (TextView)findViewById(R.id.actionbarwatson_subtitle);
		
		//Try to load subtitle style from the theme
		final int subtitleTextStyle = attrsActionBar.getResourceId(R.styleable.SherlockActionBar_subtitleTextStyle, 0);
		if (subtitleTextStyle != 0) {
			mSubtitle.setTextAppearance(context, subtitleTextStyle);
		}
		
		//Try to load subtitle from theme
		final CharSequence subtitle = attrsActionBar.getString(R.styleable.SherlockActionBar_subtitle);
		if (subtitle != null) {
			setSubtitle(subtitle);
		}
		
		
		//// NAVIGATION ////
		
		mListView = (Spinner)findViewById(R.id.actionbarwatson_nav_list);
		mTabsView = (LinearLayout)findViewById(R.id.actionbarwatson_nav_tabs);
		
		
		//// CUSTOM VIEW ////
		
		mCustomView = (FrameLayout)findViewById(R.id.actionbarwatson_custom);
		
		//Try to load a custom view from the theme. This will NOT automatically
		//trigger the visibility of the custom layout, however.
		final int customViewResourceId = attrsActionBar.getResourceId(R.styleable.SherlockActionBar_customNavigationLayout, 0);
		if (customViewResourceId != 0) {
			setCustomView(customViewResourceId);
		}
		
		
		mActionsView = (LinearLayout)findViewById(R.id.actionbarwatson_actions);
		
		
		//Try to get the display options defined in the theme, or fall back to
		//displaying the title and home icon
		setDisplayOptions(attrsActionBar.getInteger(R.styleable.SherlockActionBar_displayOptions, DEFAULT_DISPLAY_OPTIONS));
		
		//Try to get the navigation defined in the theme, or, fall back to
		//use standard navigation by default (this will call reloadDisplay)
		setNavigationMode(attrsActionBar.getInteger(R.styleable.SherlockActionBar_navigationMode, DEFAULT_NAVIGATION_MODE));
		
		
		//Reduce, Reuse, Recycle!
		attrsActionBar.recycle();
		attrsTheme.recycle();
	}
	
	
	
	// ------------------------------------------------------------------------
	// HELPER METHODS
	// ------------------------------------------------------------------------
	
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
		final boolean hasSubtitle = (mSubtitle.getText() != null) && !mSubtitle.getText().equals(""); 
		final boolean displayHome = getDisplayOptionValue(ActionBar.DISPLAY_SHOW_HOME);
		final boolean displayHomeAsUp = getDisplayOptionValue(ActionBar.DISPLAY_HOME_AS_UP);
		final boolean displayTitle = getDisplayOptionValue(ActionBar.DISPLAY_SHOW_TITLE);
		final boolean displayCustom = getDisplayOptionValue(ActionBar.DISPLAY_SHOW_CUSTOM);
		final boolean displayLogo = getDisplayOptionValue(ActionBar.DISPLAY_USE_LOGO);
		
		mHome.setVisibility(displayHome ? View.VISIBLE : View.GONE);
		if (displayHome) {
			mHome.setUpIndicatorVisibility(displayHomeAsUp ? View.VISIBLE : View.GONE);
			mHome.setLogoVisibility(displayLogo ? View.VISIBLE : View.GONE);
			mHome.setIconVisibility(displayLogo ? View.GONE : View.VISIBLE);
		} else {
			mHome.setUpIndicatorVisibility(View.GONE);
			mHome.setLogoVisibility(View.GONE);
			mHome.setIconVisibility(View.GONE);
		}
		
		//Only show list if we are in list navigation and there are list items
		mListView.setVisibility(isList ? View.VISIBLE : View.GONE);

		// Show tabs if in tabs navigation mode.
		mTabsView.setVisibility(isTab ? View.VISIBLE : View.GONE);
		
		//Show title view if we are not in list navigation, not showing custom
		//view, and the show title flag is true
		mTitle.setVisibility(isStandard && !displayCustom && displayTitle ? View.VISIBLE : View.GONE);
		//Show subtitle view if we are not in list navigation, not showing
		//custom view, show title flag is true, and a subtitle is set
		mSubtitle.setVisibility(isStandard && !displayCustom && displayTitle && hasSubtitle ? View.VISIBLE : View.GONE);
		//Show custom view if we are not in list navigation and showing custom
		//flag is set
		mCustomView.setVisibility(isStandard && displayCustom ? View.VISIBLE : View.GONE);
	}
	
	// ------------------------------------------------------------------------
	// ACTION BAR API
	// ------------------------------------------------------------------------

	public void addTab(ActionBar.Tab tab) {
		final int tabCount = getTabCount();
		addTab(tab, tabCount, tabCount == 0);
	}
	
	public void addTab(ActionBar.Tab tab, boolean setSelected) {
		addTab(tab, getTabCount(), setSelected);
	}
	
	public void addTab(ActionBar.Tab tab, int position) {
		addTab(tab, position, getTabCount() == 0);
	}
	
	public void addTab(ActionBar.Tab tab, int position, boolean setSelected) {
		mTabsView.addView(((TabImpl)tab).mView, position);
		if (setSelected) {
			tab.select();
		}
	}
	
	public View getCustomView() {
		return mCustomView.getChildAt(0);
	}
	
	public int getDisplayOptions() {
		return mFlags;
	}

	//public int getHeight();

	public int getNavigationItemCount() {
		if (mNavigationMode == ActionBar.NAVIGATION_MODE_LIST) {
			return mListView.getCount();
		}
		if (mNavigationMode == ActionBar.NAVIGATION_MODE_TABS) {
			return mTabsView.getChildCount();
		}
		return 0;
	}

	public int getNavigationMode() {
		return mNavigationMode;
	}

	public int getSelectedNavigationIndex() {
		if (mNavigationMode == ActionBar.NAVIGATION_MODE_LIST) {
			return mListView.getSelectedItemPosition();
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

	public CharSequence getSubtitle() {
		if ((mNavigationMode == ActionBar.NAVIGATION_MODE_STANDARD) && !mSubtitle.getText().equals("")) {
			return mSubtitle.getText();
		} else {
			return null;
		}
	}

	public TabImpl getTabAt(int index) {
		View view = mTabsView.getChildAt(index);
		return (view != null) ? (TabImpl)view.getTag() : null;
	}

	public int getTabCount() {
		return mTabsView.getChildCount();
	}

	public CharSequence getTitle() {
		if ((mNavigationMode == ActionBar.NAVIGATION_MODE_STANDARD) && !mTitle.getText().equals("")) {
			return mTitle.getText();
		} else {
			return null;
		}
	}

	public void hide() {
		setVisibility(View.GONE);
	}

	public boolean isShowing() {
		return getVisibility() == View.VISIBLE;
	}
	
	public TabImpl newTab() {
		return new TabImpl(this);
	}

	public void removeAllTabs() {
		TabImpl selected = getSelectedTab();
		if (selected != null) {
			selected.unselect();
		}
		mTabsView.removeAllViews();
	}

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

	//public void setBackgroundDrawable(Drawable d);

	public void setCustomView(int resId) {
		mCustomView.removeAllViews();
		LayoutInflater.from(getContext()).inflate(resId, mCustomView, true);
	}

	public void setCustomView(View view) {
		mCustomView.removeAllViews();
		mCustomView.addView(view);
	}
	
	public void setCustomView(View view, ActionBar.LayoutParams layoutParams) {
		view.setLayoutParams(layoutParams);
		setCustomView(view);
	}
	
	public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
		setDisplayOptions(showHomeAsUp ? ActionBar.DISPLAY_HOME_AS_UP : 0, ActionBar.DISPLAY_HOME_AS_UP);
	}

	public void setDisplayOptions(int options, int mask) {
		mFlags = (mFlags & ~mask) | options;
		reloadDisplay();
	}

	public void setDisplayOptions(int options) {
		mFlags = options;
		reloadDisplay();
	}
	
	public void setDisplayShowCustomEnabled(boolean showCustom) {
		setDisplayOptions(showCustom ? ActionBar.DISPLAY_SHOW_CUSTOM : 0, ActionBar.DISPLAY_SHOW_CUSTOM);
	}
	
	public void setDisplayShowHomeEnabled(boolean showHome) {
		setDisplayOptions(showHome ? ActionBar.DISPLAY_SHOW_HOME : 0, ActionBar.DISPLAY_SHOW_HOME);
	}
	
	public void setDisplayShowTitleEnabled(boolean showTitle) {
		setDisplayOptions(showTitle ? ActionBar.DISPLAY_SHOW_TITLE : 0, ActionBar.DISPLAY_SHOW_TITLE);
	}

	public void setDisplayUseLogoEnabled(boolean useLogo) {
		setDisplayOptions(useLogo ? ActionBar.DISPLAY_USE_LOGO : 0, ActionBar.DISPLAY_USE_LOGO);
	}

	public void setListNavigationCallbacks(SpinnerAdapter adapter, final ActionBar.OnNavigationListener callback) {
		mListView.setAdapter(adapter);
		mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long itemId) {
				if (callback != null) {
					callback.onNavigationItemSelected(position, itemId);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		reloadDisplay();
	}

	public void setNavigationMode(int mode) {
		if ((mode != ActionBar.NAVIGATION_MODE_STANDARD) && (mode != ActionBar.NAVIGATION_MODE_LIST)
				&& (mode != ActionBar.NAVIGATION_MODE_TABS)) {
			throw new IllegalArgumentException("Unknown navigation mode value " + Integer.toString(mode));
		}
		
		if (mode != mNavigationMode) {
			mNavigationMode = mode;
			reloadDisplay();
		}
	}

	public void setSelectedNavigationItem(int position) {
		if (mNavigationMode == ActionBar.NAVIGATION_MODE_TABS) {
			ActionBar.Tab tab = getTabAt(position);
			if (tab != null) {
				tab.select();
			}
		} else if (mNavigationMode == ActionBar.NAVIGATION_MODE_LIST) {
			mListView.setSelection(position);
		}
	}

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

	public void setSubtitle(CharSequence subtitle) {
		mSubtitle.setText((subtitle == null) ? "" : subtitle);
		reloadDisplay();
	}
	
	public void setSubtitle(int resId) {
		mSubtitle.setText(resId);
		reloadDisplay();
	}

	public void setTitle(CharSequence title) {
		mTitle.setText((title == null) ? "" : title);
	}
	
	public void setTitle(int resId) {
		mTitle.setText(resId);
	}

	public void show() {
		setVisibility(View.VISIBLE);
	}
	
	// ------------------------------------------------------------------------
	// ACTION ITEMS SUPPORT
	// ------------------------------------------------------------------------
	
	public ActionBarWatson.Item getHomeItem() {
		return mHome;
	}
	
	public ActionBarWatson.Item newItem() {
		ActionItem item = (ActionItem)LayoutInflater.from(getContext()).inflate(R.layout.actionbarwatson_item, mActionsView, false);
		item.setActionBar(this);
		return item;
	}
	
	public void addItem(ActionBarWatson.Item item) {
		if (item instanceof HomeItem) {
			throw new IllegalStateException("Cannot add home item as an action item.");
		}
		mActionsView.addView(item);
	}
	
	public void addItem(ActionBarWatson.Item item, int position) {
		if (item instanceof HomeItem) {
			throw new IllegalStateException("Cannot add home item as an action item.");
		}
		mActionsView.addView(item, position);
	}
	
	public void removeAllItems() {
		mActionsView.removeAllViews();
	}
	
	public void setIsActionItemTextEnabled(boolean isActionItemTextEnabled) {
		if (isActionItemTextEnabled != mIsActionItemTextEnabled) {
			mIsActionItemTextEnabled = isActionItemTextEnabled;
			final int count = mActionsView.getChildCount();
			for (int i = count - 1; i >= 0; i--) {
				View view = mActionsView.getChildAt(i);
				if (view instanceof ActionItem) {
					((ActionItem)view).reloadDisplay();
				}
			}
		}
	}
	
	// ------------------------------------------------------------------------
	// HELPER INTERFACES AND HELPER CLASSES
	// ------------------------------------------------------------------------
	
	public static abstract class Item extends RelativeLayout {
		public Item(Context context) {
			super(context);
		}
		public Item(Context context, AttributeSet attrs) {
			super(context, attrs);
		}
		public Item(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}
		
		public abstract View getCustomView();
		public abstract Item setCustomView(int resId);
		public abstract Item setCustomView(View view);
		
		public abstract Drawable getIcon();
		public abstract Item setIcon(int resId);
		public abstract Item setIcon(Drawable icon);
		
		public abstract Drawable getLogo();
		public abstract Item setLogo(int resId);
		public abstract Item setLogo(Drawable logo);
		
		public abstract CharSequence getTitle();
		public abstract Item setTitle(int resId);
		public abstract Item setTitle(CharSequence title);
	}
	
	public static final class ActionItem extends Item {
		ActionBarWatson mActionBar;
		ImageView mIconView;
		TextView mTextView;
		FrameLayout mCustomView;


		public ActionItem(Context context) {
			this(context, null);
		}
		public ActionItem(Context context, AttributeSet attrs) {
			this(context, attrs, R.attr.actionButtonStyle);
		}
		public ActionItem(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}
		
		
		@Override
		protected void onFinishInflate() {
			super.onFinishInflate();

			mIconView = (ImageView)findViewById(R.id.actionbarwatson_item_icon);
			mTextView = (TextView)findViewById(R.id.actionbarwatson_item_text);
			mCustomView = (FrameLayout)findViewById(R.id.actionbarwatson_item_custom);
		}
		
		void reloadDisplay() {
			final boolean hasCustomView = mCustomView.getChildCount() > 0;
			final boolean hasText = (mTextView.getText() != null) && !mTextView.getText().equals("");
			
			mIconView.setVisibility(!hasCustomView ? View.VISIBLE : View.GONE);
			mTextView.setVisibility(!hasCustomView && hasText && mActionBar.mIsActionItemTextEnabled ? View.VISIBLE : View.GONE);
			mCustomView.setVisibility(hasCustomView ? View.VISIBLE : View.GONE);
		}
		
		void setActionBar(ActionBarWatson actionBar) {
			mActionBar = actionBar;
		}
		
		@Override
		public View getCustomView() {
			return mCustomView;
		}
		
		@Override
		public ActionItem setCustomView(int resId) {
			mCustomView.removeAllViews();
			LayoutInflater.from(getContext()).inflate(resId, mCustomView, true);
			reloadDisplay();
			return this;
		}
		
		@Override
		public ActionItem setCustomView(View view) {
			mCustomView.removeAllViews();
			if (view != null) {
				mCustomView.addView(view);
			}
			reloadDisplay();
			return this;
		}
		
		@Override
		public Drawable getIcon() {
			return mIconView.getDrawable();
		}
		
		@Override
		public ActionItem setIcon(int resId) {
			if (resId != View.NO_ID) {
				mIconView.setImageResource(resId);
			}
			return this;
		}
		
		@Override
		public ActionItem setIcon(Drawable icon) {
			mIconView.setImageDrawable(icon);
			return this;
		}
		
		@Override
		public Drawable getLogo() {
			//Not implemented
			return null;
		}
		
		@Override
		public ActionItem setLogo(int resId) {
			//Not implemented
			return this;
		}
		
		@Override
		public ActionItem setLogo(Drawable logo) {
			//Not implemented
			return this;
		}

		@Override
		public CharSequence getTitle() {
			return mTextView.getText();
		}
		
		@Override
		public ActionItem setTitle(int resId) {
			mTextView.setText(resId);
			reloadDisplay();
			return this;
		}
		
		@Override
		public ActionItem setTitle(CharSequence title) {
			mTextView.setText(title);
			reloadDisplay();
			return this;
		}
	}
	
	public static final class HomeItem extends Item {
		/** Home logo. */
		private final ImageView mLogo;
		
		/** Home icon. */
		private final ImageView mIcon;
		
		/** Home button up indicator. */
		private final View mUpIndicator;
		
		
		public HomeItem(Context context) {
			this(context, null);
		}
		
		public HomeItem(Context context, AttributeSet attrs) {
			this(context, attrs, R.attr.actionHomeButtonStyle);
		}
		
		public HomeItem(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			LayoutInflater.from(context).inflate(R.layout.actionbarwatson_item_home, this, true);
			
			mLogo = (ImageView)findViewById(R.id.actionbarwatson_home_logo);
			mIcon = (ImageView)findViewById(R.id.actionbarwatson_home_icon);
			mUpIndicator = findViewById(R.id.actionbarwatson_home_as_up_indicator);
		}
		
		
		void setUpIndicator(Drawable homeAsUpIndicator) {
			mUpIndicator.setBackgroundDrawable(homeAsUpIndicator);
		}
		
		void setIconVisibility(int visibility) {
			mIcon.setVisibility(visibility);
		}
		
		void setLogoVisibility(int visibility) {
			mLogo.setVisibility(visibility);
		}
		
		void setUpIndicatorVisibility(int visibility) {
			mUpIndicator.setVisibility(visibility);
		}
		
		@Override
		public View getCustomView() {
			//Not implemented
			return null;
		}
		
		@Override
		public Item setCustomView(int resId) {
			//Not implemented
			return this;
		}
		
		@Override
		public Item setCustomView(View view) {
			//Not implemented
			return this;
		}
		
		@Override
		public Drawable getIcon() {
			return mIcon.getDrawable();
		}
		
		@Override
		public HomeItem setIcon(int resId) {
			mIcon.setImageResource(resId);
			return this;
		}
		
		@Override
		public HomeItem setIcon(Drawable icon) {
			mIcon.setImageDrawable(icon);
			return this;
		}
		
		@Override
		public Drawable getLogo() {
			return mLogo.getDrawable();
		}
		
		@Override
		public HomeItem setLogo(int resId) {
			mLogo.setImageResource(resId);
			return this;
		}
		
		@Override
		public HomeItem setLogo(Drawable logo) {
			mLogo.setImageDrawable(logo);
			return this;
		}
		
		@Override
		public CharSequence getTitle() {
			//Not implemented
			return null;
		}
		
		@Override
		public HomeItem setTitle(int resId) {
			//Not implemented
			return this;
		}
		
		@Override
		public HomeItem setTitle(CharSequence title) {
			//Not implemented
			return this;
		}
	}
	
	private static class TabImpl implements ActionBar.Tab {
		private static final View.OnClickListener clickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((TabImpl)v.getTag()).select();
			}
		};
		
		final ActionBarWatson mActionBar;
		final View mView;
		final ImageView mIconView;
		final TextView mTextView;
		final FrameLayout mCustomView;
		
		ActionBar.TabListener mListener;
		Object mTag;
		
		
		TabImpl(ActionBarWatson actionBar) {
			mActionBar = actionBar;
			mView = LayoutInflater.from(mActionBar.getContext()).inflate(R.layout.actionbarwatson_tab, actionBar.mTabsView, false);
			mView.setTag(this);
			mView.setOnClickListener(clickListener);
			
			mIconView = (ImageView)mView.findViewById(R.id.actionbarwatson_tab_icon);
			mTextView = (TextView)mView.findViewById(R.id.actionbarwatson_tab);
			mCustomView = (FrameLayout)mView.findViewById(R.id.actionbarwatson_tab_custom);
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
		public ActionBar.TabListener getTabListener() {
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
			LayoutInflater.from(mActionBar.getContext()).inflate(layoutResId, mCustomView, true);
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
		public TabImpl setTabListener(ActionBar.TabListener listener) {
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
	}
}
