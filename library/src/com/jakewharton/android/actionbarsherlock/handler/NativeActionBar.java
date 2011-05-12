package com.jakewharton.android.actionbarsherlock.handler;

import java.util.HashMap;
import android.app.ActionBar;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.SpinnerAdapter;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock;
import com.jakewharton.android.actionbarsherlock.ActionBarTab;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.ActionBarHandler;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasBackgroundDrawable;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasCustomView;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasHome;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasListNavigation;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasLogo;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasMenu;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasNavigationState;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasSubtitle;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasTabNavigation;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasTitle;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasVisibility;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.LayoutParams;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.OnMenuVisibilityListener;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.OnNavigationListener;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.SherlockActivity;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.TabListener;

public final class NativeActionBar {
	//No instances
	private NativeActionBar() {}
	
	/**
	 * <p>Handler for Android's native {@link android.app.ActionBar}.</p>
	 * 
	 * <p>All of the implementations of the interfaces are marked as final to
	 * prevent tampering with the functionality. Most methods simple marshal
	 * the method call down to the native action bar method anyways.</p>
	 * 
	 * <p>Extending the native action bar should be done by implementing custom
	 * interfaces so the new-style interaction as described in the
	 * {@link ActionBarSherlock#attach()} method.</p>
	 */
	public static class Handler extends ActionBarHandler<ActionBar> implements HasTitle, HasSubtitle, HasHome, HasLogo, HasListNavigation, HasTabNavigation, HasMenu, HasVisibility, HasNavigationState, HasCustomView, HasBackgroundDrawable, ActionBar.TabListener {
		/** Maps listener wrappers to native listeners for removal. */
		private final HashMap<OnMenuVisibilityListener, ActionBar.OnMenuVisibilityListener> mMenuListener = new HashMap<OnMenuVisibilityListener, ActionBar.OnMenuVisibilityListener>();
		
		@Override
		public final ActionBar initialize(int layoutResourceId) {
			this.getActivity().setContentView(layoutResourceId);
			return this.getActivity().getActionBar();
		}
		
		@Override
		public final ActionBar initialize(View view) {
			this.getActivity().setContentView(view);
			return this.getActivity().getActionBar();
		}
		
		@Override
		public final ActionBar initialize(Fragment fragment, FragmentManager manager) {
			manager.beginTransaction()
			       .add(android.R.id.content, fragment)
			       .commit();
			
			return this.getActivity().getActionBar();
		}

		@Override
		public final CharSequence getTitle() {
			return this.getActionBar().getTitle();
		}
		
		@Override
		public final void setTitle(CharSequence title) {
			this.getActionBar().setTitle(title);
		}

		@Override
		public final void setTitle(int resourceId) {
			this.getActionBar().setTitle(resourceId);
		}
		
		@Override
		public final void setShowTitle(boolean value) {
			this.getActionBar().setDisplayShowTitleEnabled(value);
		}

		@Override
		public final CharSequence getSubtitle() {
			return this.getActionBar().getSubtitle();
		}

		@Override
		public final void setSubtitle(CharSequence subtitle) {
			this.getActionBar().setSubtitle(subtitle);
		}

		@Override
		public final void setSubtitle(int resourceId) {
			this.getActionBar().setSubtitle(resourceId);
		}
		
		@Override
		public final void setHomeAsUp(boolean displayHomeAsUp) {
			this.getActionBar().setDisplayHomeAsUpEnabled(displayHomeAsUp);
		}

		public final void setShowHome(boolean showHome) {
			this.getActionBar().setDisplayShowHomeEnabled(showHome);
		}
		
		@Override
		public final void setUseLogo(boolean useLogo) {
			this.getActionBar().setDisplayUseLogoEnabled(useLogo);
		}

		@Override
		public final int getItemCount() {
			return this.getActionBar().getNavigationItemCount();
		}

		@Override
		public final int getSelectedItemIndex() {
			return this.getActionBar().getSelectedNavigationIndex();
		}

		@Override
		public final void selectItem(int position) {
			this.getActionBar().setSelectedNavigationItem(position);
		}

		@Override
		public final void setList(SpinnerAdapter adapter, final OnNavigationListener listener) {
			//Set the navigation mode to a list
			this.getActionBar().setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_LIST);
			this.getActionBar().setListNavigationCallbacks(adapter, new ActionBar.OnNavigationListener() {
				@Override
				public boolean onNavigationItemSelected(int itemPosition, long itemId) {
					return listener.onNavigationItemSelected(itemPosition, itemId);
				}
			});
		}

		@Override
		public final void setMenuResourceId(int menuResourceId) {
			//FYI: instanceof SherlockActivity was checked in ActionBarSherlock#menu(int)
			SherlockActivity activity = (SherlockActivity)this.getActivity();
			//Delegate inflation to the activity for native implementation
			activity.setActionBarMenu(menuResourceId);
		}

		@Override
		public void addMenuVisiblityListener(final OnMenuVisibilityListener listener) {
			//Create a native listener and store the mapping from the wrapper
			ActionBar.OnMenuVisibilityListener nativeListener = new ActionBar.OnMenuVisibilityListener() {
				@Override
				public void onMenuVisibilityChanged(boolean isVisible) {
					listener.onMenuVisibilityChanged(isVisible);
				}
			};
			this.mMenuListener.put(listener, nativeListener);
			
			this.getActionBar().addOnMenuVisibilityListener(nativeListener);
		}

		@Override
		public void removeMenuVisiblityListener(OnMenuVisibilityListener listener) {
			this.getActionBar().removeOnMenuVisibilityListener(this.mMenuListener.get(listener));
		}

		@Override
		public final void addTab(ActionBarTab tab) {
			this.getActionBar().addTab(
				this.getActionBar().newTab()
						.setTabListener(this)
						.setIcon(tab.getIcon())
						.setText(tab.getText())
						.setTag(tab) //Unwrapped in callbacks below
			);
		}

		@Override
		public final ActionBarTab getSelectedTab() {
			return (ActionBarTab)this.getActionBar().getSelectedTab().getTag();
		}

		@Override
		public final int getSelectedTabIndex() {
			return this.getActionBar().getSelectedNavigationIndex();
		}

		@Override
		public final ActionBarTab getTabAt(int position) {
			return (ActionBarTab)this.getActionBar().getTabAt(position).getTag();
		}

		@Override
		public final int getTabCount() {
			return this.getActionBar().getTabCount();
		}

		@Override
		public final void removeAllTabs() {
			this.getActionBar().removeAllTabs();
		}

		@Override
		public final void removeTab(ActionBarTab tab) {
			//Iterate until we match and remove by index
			final int tabCount = this.getTabCount();
			for (int i = 0; i < tabCount; i++) {
				if (this.getTabAt(i).equals(tab)) {
					this.removeTabAt(i);
				}
			}
		}

		@Override
		public final void removeTabAt(int position) {
			this.getActionBar().removeTabAt(position);
		}

		@Override
		public final void selectTab(int position) {
			this.getActionBar().setSelectedNavigationItem(position);
		}

		@Override
		public final void selectTab(ActionBarTab tab) {
			//Iterate until we match and select by index
			final int tabCount = this.getTabCount();
			for (int i = 0; i < tabCount; i++) {
				if (this.getTabAt(i).equals(tab)) {
					this.selectTab(i);
				}
			}
		}
		
		@Override
		public final void hide() {
			this.getActionBar().hide();
		}

		@Override
		public final boolean isShowing() {
			return this.getActionBar().isShowing();
		}
		
		@Override
		public int getHeight() {
			return this.getActionBar().getHeight();
		}

		@Override
		public final void show() {
			this.getActionBar().show();
		}
		
		@Override
		public final int getNavigationMode() {
			return this.getActionBar().getNavigationMode();
		}
		
		@Override
		public final View getCustomView() {
			return this.getActionBar().getCustomView();
		}

		@Override
		public final void setCustomView(int resourceId) {
			this.getActionBar().setCustomView(resourceId);
		}

		@Override
		public final void setCustomView(View view) {
			this.getActionBar().setCustomView(view);
		}

		@Override
		public final void setCustomView(View view, LayoutParams layoutParameters) {
			//Copy custom LayoutParams into native version
			ActionBar.LayoutParams nativeParams = new ActionBar.LayoutParams(layoutParameters.width, layoutParameters.height, layoutParameters.gravity);
			nativeParams.bottomMargin = layoutParameters.bottomMargin;
			nativeParams.layoutAnimationParameters = layoutParameters.layoutAnimationParameters;
			nativeParams.leftMargin = layoutParameters.leftMargin;
			nativeParams.rightMargin = layoutParameters.rightMargin;
			nativeParams.topMargin = layoutParameters.topMargin;
			
			this.getActionBar().setCustomView(view, nativeParams);
		}
		
		@Override
		public final void setShowCustomView(boolean showCustomView) {
			this.getActionBar().setDisplayShowCustomEnabled(showCustomView);
		}

		@Override
		public final void setBackgroundDrawable(Drawable drawable) {
			this.getActionBar().setBackgroundDrawable(drawable);
		}
		
		@Override
		public final void setNavigationMode(int navigationMode) {
			this.getActionBar().setNavigationMode(navigationMode);
		}
		
		@Override
		public final void onTabReselected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction transaction) {
			//FYI: instanceof TabListener was checked in ActionBarSherlock#tab(ActionBarTab)
			TabListener activity = (TabListener)this.getActivity();
			//Delegate tab reselection handling to our common API
			activity.onTabReselected((ActionBarTab)tab.getTag());
		}

		@Override
		public final void onTabSelected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction transaction) {
			//FYI: instanceof TabListener was checked in ActionBarSherlock#tab(ActionBarTab)
			TabListener activity = (TabListener)this.getActivity();
			//Delegate tab selection handling to our common API
			activity.onTabSelected((ActionBarTab)tab.getTag());
		}

		@Override
		public final void onTabUnselected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction transaction) {
			//FYI: instanceof TabListener was checked in ActionBarSherlock#tab(ActionBarTab)
			TabListener activity = (TabListener)this.getActivity();
			//Delegate tab unselection handling to our common API
			activity.onTabUnselected((ActionBarTab)tab.getTag());
		}
	}
}
