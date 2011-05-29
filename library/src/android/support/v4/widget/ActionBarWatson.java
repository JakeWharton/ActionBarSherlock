package android.support.v4.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActionBar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.jakewharton.android.actionbarsherlock.R;

public final class ActionBarWatson extends RelativeLayout {
	static final class Dropdown extends PopupWindow implements View.OnClickListener {
		private final LayoutInflater mInflater;
		private SpinnerAdapter mAdapter;
		private DialogInterface.OnClickListener mListener;
		private View mParent;
		
		Dropdown(Context context) {
			super(context, null, R.styleable.SherlockTheme_dropDownListViewStyle);
			
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			setFocusable(true);
			setWindowLayoutMode(0, LayoutParams.WRAP_CONTENT);
		}
		
		public Dropdown setAdapter(SpinnerAdapter adapter, DialogInterface.OnClickListener listener) {
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

	
	/** Default display options if none are defined in the theme. */
	private static final int DEFAULT_DISPLAY_OPTIONS = ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME;
	
	/** Default navigation mode if one is not defined in the theme. */
	private static final int DEFAULT_NAVIGATION_MODE = ActionBar.NAVIGATION_MODE_STANDARD;
	
	
	
	/** Home logo and icon action item. */
	private View mHome;
	
	/** Home logo. */
	private ImageView mHomeLogo;
	
	/** Home icon. */
	private ImageView mHomeIcon;
	
	/** Home button up indicator. */
	private View mHomeAsUpIndicator;
	
	/** Title view. */
	private TextView mTitle;
	
	/** Subtitle view. */
	private TextView mSubtitle;
	
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
								if (position != mSelectedIndex) {
									mSelectedIndex = position;
									reloadDisplay();
								}
								
								//Execute call back, if exists
								if (mListCallback != null) {
									mListCallback.onNavigationItemSelected(position, mListAdapter.getItemId(position));
								}
							}
						})
						.setParent(mListView)
						.show();
			}
		}
	};

	
	

	public ActionBarWatson(Context context) {
		this(context, null);
	}
	
	public ActionBarWatson(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.actionBarStyle);
	}
	
	public ActionBarWatson(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.actionbar, this, true);
		
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SherlockActionBar, defStyle, 0);
		
		/// HOME ////
		
		mHome = findViewById(R.id.actionbar_home);
		//TODO set up home click
		
		mHomeLogo = (ImageView)findViewById(R.id.actionbar_home_logo);
		mHomeIcon = (ImageView)findViewById(R.id.actionbar_home_icon);
		mHomeAsUpIndicator = findViewById(R.id.actionbar_home_as_up_indicator);
		
		//Load the up indicator
		Drawable homeAsUpIndicator = a.getDrawable(R.styleable.SherlockActionBar_homeAsUpIndicator);
		//if (homeAsUpIndicator == null) {
		//	homeAsUpIndicator = getActivity().getResources().getDrawable(R.drawable.actionbar_home_as_up_indicator);
		//}
		mHomeAsUpIndicator.setBackgroundDrawable(homeAsUpIndicator);

		//Try to load the logo from the theme
		Drawable homeLogo = a.getDrawable(R.styleable.SherlockActionBar_logo);
		if (homeLogo != null) {
			mHomeLogo.setImageDrawable(homeLogo);
		}
		
		//Try to load the icon from the theme
		Drawable homeIcon = a.getDrawable(R.styleable.SherlockActionBar_icon);
		mHomeIcon.setImageDrawable(homeIcon);
		

		
		//// TITLE ////
		
		mTitle = (TextView)findViewById(R.id.actionbar_title);
		
		//Try to load title style from the theme
		int titleTextStyle = a.getResourceId(R.styleable.SherlockActionBar_titleTextStyle, 0);
		if (titleTextStyle != 0) {
			mTitle.setTextAppearance(context, titleTextStyle);
		}
		
		//Try to load title from the theme
		CharSequence title = a.getString(R.styleable.SherlockActionBar_title);
		if (title != null) {
			setTitle(title);
		}
		
		
		//// SUBTITLE ////
		
		mSubtitle = (TextView)findViewById(R.id.actionbar_subtitle);
		
		//Try to load subtitle style from the theme
		int subtitleTextStyle = a.getResourceId(R.styleable.SherlockActionBar_subtitleTextStyle, 0);
		if (subtitleTextStyle != 0) {
			mSubtitle.setTextAppearance(context, subtitleTextStyle);
		}
		
		//Try to load subtitle from theme
		CharSequence subtitle = a.getString(R.styleable.SherlockActionBar_subtitle);
		if (subtitle != null) {
			setSubtitle(subtitle);
		}
		
		
		//// LIST NAVIGATION ////
		
		mListView = (FrameLayout)findViewById(R.id.actionbar_list);
		mListIndicator = findViewById(R.id.actionbar_list_indicator);
		
		Drawable listIndicator = a.getDrawable(R.styleable.SherlockActionBar_listIndicator);
		if (listIndicator == null) {
			listIndicator = context.getResources().getDrawable(R.drawable.actionbar_list_indicator);
		}
		mListIndicator.setBackgroundDrawable(listIndicator);
		
		
		//// CUSTOM VIEW ////
		
		mCustomView = (FrameLayout)findViewById(R.id.actionbar_custom);
		
		//Try to load a custom view from the theme. This will NOT automatically
		//trigger the visibility of the custom layout, however.
		int customViewResourceId = a.getResourceId(R.styleable.SherlockActionBar_customNavigationLayout, 0);
		if (customViewResourceId != 0) {
			setCustomView(customViewResourceId);
		}
		
		
		mActionsView = (LinearLayout)findViewById(R.id.actionbar_actions);
		mTabsView = (LinearLayout)findViewById(R.id.actionbar_tabs);
		
		
		//Try to get the display options defined in the theme, or fall back to
		//displaying the title and home icon
		setDisplayOptions(a.getInteger(R.styleable.SherlockActionBar_displayOptions, DEFAULT_DISPLAY_OPTIONS));
		
		//Try to get the navigation defined in the theme, or, fall back to
		//use standard navigation by default (this will call reloadDisplay)
		setNavigationMode(a.getInteger(R.styleable.SherlockActionBar_navigationMode, DEFAULT_NAVIGATION_MODE));
		
		a.recycle();
	}
	
	
	
	
	
	// ------------------------------------------------------------------------
	// HELPER METHODS
	// ------------------------------------------------------------------------
	
	public Drawable getHomeIcon() {
		return mHomeIcon.getDrawable();
	}
	
	public Drawable getHomeLogo() {
		return mHomeLogo.getDrawable();
	}
	
	public void setHomeIcon(Drawable icon) {
		mHomeIcon.setImageDrawable(icon);
	}
	
	public void setHomeLogo(Drawable logo) {
		mHomeLogo.setImageDrawable(logo);
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
		final boolean hasSubtitle = (mSubtitle.getText() != null) && !mSubtitle.getText().equals(""); 
		final boolean displayHome = getDisplayOptionValue(ActionBar.DISPLAY_SHOW_HOME);
		final boolean displayHomeAsUp = getDisplayOptionValue(ActionBar.DISPLAY_HOME_AS_UP);
		final boolean displayTitle = getDisplayOptionValue(ActionBar.DISPLAY_SHOW_TITLE);
		final boolean displayCustom = getDisplayOptionValue(ActionBar.DISPLAY_SHOW_CUSTOM);
		final boolean displayLogo = getDisplayOptionValue(ActionBar.DISPLAY_USE_LOGO);
		
		mHome.setVisibility(displayHome ? View.VISIBLE : View.GONE);
		if (displayHome) {
			mHomeAsUpIndicator.setVisibility(displayHomeAsUp ? View.VISIBLE : View.GONE);
			mHomeLogo.setVisibility(displayLogo ? View.VISIBLE : View.GONE);
			mHomeIcon.setVisibility(displayLogo ? View.GONE : View.VISIBLE);
		} else {
			mHomeAsUpIndicator.setVisibility(View.GONE);
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
		addTab(tab, getTabCount(), false);
	}
	
	public void addTab(ActionBar.Tab tab, boolean setSelected) {
		addTab(tab, getTabCount(), setSelected);
	}
	
	public void addTab(ActionBar.Tab tab, int position) {
		addTab(tab, position, false);
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
			return mListAdapter.getCount();
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
		setDisplayShowCustomEnabled(true);
	}

	public void setCustomView(View view) {
		mCustomView.removeAllViews();
		mCustomView.addView(view);
		setDisplayShowCustomEnabled(true);
	}
	
	public void setCustomView(View view, ActionBar.LayoutParams layoutParams) {
		mCustomView.removeAllViews();
		mCustomView.addView(view, layoutParams);
		setDisplayShowCustomEnabled(true);
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

	public void setListNavigationCallbacks(SpinnerAdapter adapter, ActionBar.OnNavigationListener callback) {
		//Reset selected item
		mSelectedIndex = 0;
		//Save adapter and callback
		mListAdapter = adapter;
		mListCallback = callback;
		
		reloadDisplay();
	}

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

	public void setSelectedNavigationItem(int position) {
		if ((mNavigationMode != ActionBar.NAVIGATION_MODE_STANDARD) && (position != mSelectedIndex)) {
			mSelectedIndex = position;
			reloadDisplay();
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
	
	public ActionBarWatson.Item newItem() {
		Item item = (Item)LayoutInflater.from(getContext()).inflate(R.layout.actionbar_item, mActionsView, false);
		item.setActionBar(this);
		return item;
	}
	
	public void addItem(ActionBarWatson.Item item) {
		mActionsView.addView(item);
	}
	
	public void addItem(ActionBarWatson.Item item, int position) {
		mActionsView.addView(item, position);
	}
	
	public void removeAllItems() {
		mActionsView.removeAllViews();
	}
	
	// ------------------------------------------------------------------------
	// HELPER INTERFACES AND HELPER CLASSES
	// ------------------------------------------------------------------------
	
	public static final class Item extends RelativeLayout {
		ActionBarWatson mActionBar;
		ImageView mIconView;
		FrameLayout mCustomView;
		
		Intent mIntent;
		int mFlags;

		

		public Item(Context context) {
			this(context, null);
		}
		public Item(Context context, AttributeSet attrs) {
			this(context, attrs, R.attr.actionButtonStyle);
		}
		public Item(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}
		
		@Override
		protected void onFinishInflate() {
			super.onFinishInflate();

			mIconView = (ImageView)findViewById(R.id.actionbar_item_icon);
			mCustomView = (FrameLayout)findViewById(R.id.actionbar_item_custom);
		}
		
		void reloadDisplay() {
			final boolean hasCustomView = mCustomView.getChildCount() > 0;
			
			mCustomView.setVisibility(hasCustomView ? View.VISIBLE : View.INVISIBLE);
		}
		
		void setActionBar(ActionBarWatson actionBar) {
			mActionBar = actionBar;
		}
		
		public View getCustomView() {
			return mCustomView;
		}
		
		public int getDisplayOptions() {
			return mFlags;
		}
		
		public Drawable getIcon() {
			return mIconView.getDrawable();
		}
		
		public Intent getIntent() {
			return mIntent;
		}
		
		public void invoke() {
			//TODO
		}
		
		public Item setCustomView(int resId) {
			mCustomView.removeAllViews();
			LayoutInflater.from(getContext()).inflate(resId, mCustomView, true);
			reloadDisplay();
			return this;
		}
		
		public Item setCustomView(View view) {
			mCustomView.removeAllViews();
			if (view != null) {
				mCustomView.addView(view);
			}
			reloadDisplay();
			return this;
		}
		
		public Item setDisplayOptions(int flags) {
			mFlags = flags;
			reloadDisplay();
			return this;
		}
		
		public Item setIcon(Drawable icon) {
			mIconView.setImageDrawable(icon);
			return this;
		}
		
		public Item setIcon(int resId) {
			mIconView.setImageResource(resId);
			return this;
		}
		
		public Item setIntent(Intent intent) {
			mIntent = intent;
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
			mView = LayoutInflater.from(mActionBar.getContext()).inflate(R.layout.actionbar_tab, actionBar.mTabsView, false);
			mView.setTag(this);
			mView.setOnClickListener(clickListener);
			
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
