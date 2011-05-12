/*
 * Copyright 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jakewharton.android.actionbarsherlock;

import java.util.LinkedList;
import java.util.List;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.SpinnerAdapter;

//NOTE: Unqualified references to Activity in this file are to the inner-class!

/**
 * <p>Helper for implementing the action bar design pattern across all versions
 * of Android.</p>
 * 
 * <p>This class will automatically use the native
 * {@link android.app.ActionBar} implementation on Android 3.0 or later. For
 * previous versions which do not include a native action bar, an optional
 * custom handler can be provided to initialize a third-party action bar
 * implementation of your choice.</p>
 * 
 * <p>Further interaction with these action bars can be handled through
 * extending the default handlers as inner-classes of your activity. The
 * classes will allow for overriding various methods to handle the creation
 * of and interaction with each type of action bar.</p>
 * 
 * <p>Examples of third-party implementations can be found in the
 * <code>samples</code> folder of the repository, or by visiting the
 * <a href="https://github.com/JakeWharton/ActionBarSherlock/">GitHub project
 * page</a>.</p>
 * 
 * @author Jake Wharton <jakewharton@gmail.com>
 * @version 2.1.0
 */
public final class ActionBarSherlock {
	/**
	 * <p>Whether or not the current classloader has access to Android's native
	 * {@link android.app.ActionBar} class.</p>
	 * 
	 * <p>This can be used anywhere in your application to determine whether
	 * the native {@link android.app.ActionBar} or a custom action bar will
	 * be displayed and act accordingly.</p>
	 */
	public static final boolean HAS_NATIVE_ACTION_BAR;
	
	static {
		boolean hasNativeActionBar = false;
		try {
			Class.forName("android.app.ActionBar");
			hasNativeActionBar = true;
		} catch (NoClassDefFoundError e) {
		} catch (ClassNotFoundException e) {
		} finally {
			HAS_NATIVE_ACTION_BAR = hasNativeActionBar;
		}
	}
	
	
	private static final String ERROR_ACTIVITY_NULL = "Activity must not be null.";
	private static final String ERROR_ACTIVITY_FRAGMENT = "Activity must extend from android.support.v4.app.Fragment.";
	private static final String ERROR_ATTACHED = "Sherlock has already been attached to the activity.";
	private static final String ERROR_BUNDLE = "A Bundle has already been specified.";
	private static final String ERROR_HANDLER_CUSTOM = "A custom handler has already been specified.";
	private static final String ERROR_HANDLER_CUSTOM_NULL = "Custom handler must not be null.";
	private static final String ERROR_HANDLER_NATIVE = "A native handler has already been specified.";
	private static final String ERROR_HANDLER_NATIVE_NULL = "Native handler must not be null.";
	private static final String ERROR_LAYOUT_FRAGMENT = "A layout fragment has already been specified.";
	private static final String ERROR_LAYOUT_ID = "A layout ID has already been specified.";
	private static final String ERROR_LAYOUT_NULL = "Layout must not be null.";
	private static final String ERROR_LAYOUT_VIEW = "A layout view has already been specified.";
	private static final String ERROR_LAYOUT_ZERO = "Layout ID must not be zero.";
	private static final String ERROR_LAYOUTS_NULL = "At least one type of layout must be specified.";
	private static final String ERROR_LOGO_MISSING = "Neither the activity nor the application entry in the manifest contains a logo.";
	
	
	/**
	 * Whether or not this instance has been attached to the
	 * {@link android.app.Activity} yet.
	 */
	private boolean mAttached;
	
	/**
	 * The {@link android.app.Activity} on which we are binding.
	 */
	private final android.app.Activity mActivity;
	
	/**
	 * A persisted instance to forward to the implementing onCreate method.
	 */
	private Bundle mSavedInstanceState;
	
	/**
	 * Resource ID of the layout to use for the activitiy's content.
	 */
	private Integer mLayoutResourceId;
	
	/**
	 * View instance to use for the activity's content.
	 */
	private View mView;
	
	/**
	 * Fragment to load as activity's content.
	 */
	private Fragment mFragment;
	
	/**
	 * The class which will handle the native action bar.
	 */
	private Class<? extends NativeActionBarHandler> mNativeHandler;
	
	/**
	 * The class which will handle a custom action bar.
	 */
	private Class<? extends ActionBarHandler<?>> mCustomHandler;
	
	

	/**
	 * Create a new instance of the class and associate it to an activity.
	 * 
	 * @param activity Activity instance.
	 * @return ActionBarSherlock instance for builder pattern.
	 */
	public static ActionBarSherlock from(android.app.Activity activity) {
		assert activity != null : ERROR_ACTIVITY_NULL;
		
		return new ActionBarSherlock(activity);
	}
	
	
	
	/**
	 * Internal-only constructor to initialize this class for the builder
	 * pattern. Implementing activities should use the
	 * {@link #from(android.app.Activity)} method to create a new instance.
	 * 
	 * @param activity Activity on which to bind.
	 */
	private ActionBarSherlock(android.app.Activity activity) {
		this.mActivity = activity;
		
		//Defaults
		this.mAttached = false;
	}
	
	
	
	/**
	 * Include a saved state to pass to the appropriate handler's onCreate
	 * method.
	 * 
	 * @param savedInstanceState Saved instance.
	 * @return Current instance for builder pattern.
	 */
	public ActionBarSherlock with(Bundle savedInstanceState) {
		assert this.mAttached == false : ERROR_ATTACHED;
		assert this.mSavedInstanceState == null : ERROR_BUNDLE;
		
		this.mSavedInstanceState = savedInstanceState;
		return this;
	}
	
	/**
	 * Set layout resource to use for the activity's content.
	 * 
	 * @param layoutResourceId Layout resource.
	 * @return Current instance for builder pattern.
	 */
	public ActionBarSherlock layout(int layoutResourceId) {
		assert this.mAttached == false : ERROR_ATTACHED;
		assert this.mLayoutResourceId == null : ERROR_LAYOUT_ID;
		assert this.mView == null : ERROR_LAYOUT_VIEW;
		assert this.mFragment == null : ERROR_LAYOUT_FRAGMENT;
		assert layoutResourceId != 0 : ERROR_LAYOUT_ZERO;
		
		this.mLayoutResourceId = layoutResourceId;
		return this;
	}
	
	/**
	 * Set view to use for the activity's content.
	 * 
	 * @param view Content view instance.
	 * @return Current instance for builder pattern.
	 */
	public ActionBarSherlock layout(View view) {
		assert this.mAttached == false : ERROR_ATTACHED;
		assert this.mLayoutResourceId == null : ERROR_LAYOUT_ID;
		assert this.mView == null : ERROR_LAYOUT_VIEW;
		assert this.mFragment == null : ERROR_LAYOUT_FRAGMENT;
		assert view != null : ERROR_LAYOUT_NULL;
		
		this.mView = view;
		return this;
	}
	
	/**
	 * Set fragment to use for the activity's content.
	 * 
	 * @param fragment Fragment instance.
	 * @return Current instance for builder pattern.
	 */
	public ActionBarSherlock layout(Fragment fragment) {
		assert this.mAttached == false : ERROR_ATTACHED;
		assert this.mActivity instanceof android.support.v4.app.FragmentActivity : ERROR_ACTIVITY_FRAGMENT;
		assert this.mLayoutResourceId == null : ERROR_LAYOUT_ID;
		assert this.mView == null : ERROR_LAYOUT_VIEW;
		assert this.mFragment == null : ERROR_LAYOUT_FRAGMENT;
		assert fragment != null : ERROR_LAYOUT_NULL;
		
		this.mFragment = fragment;
		return this;
	}
	
	/**
	 * Class to use for handling the native action bar creation.
	 * 
	 * @param handler Class which extends {@link NativeActionBarHandler}. If
	 *                you are doing no native handling directly you may omit
	 *                this declaration
	 * @return Current instance for builder pattern.
	 */
	public ActionBarSherlock handleNative(Class<? extends NativeActionBarHandler> handler) {
		assert this.mAttached == false : ERROR_ATTACHED;
		assert this.mNativeHandler == null : ERROR_HANDLER_NATIVE;
		assert handler != null : ERROR_HANDLER_NATIVE_NULL;
		
		this.mNativeHandler = handler;
		return this;
	}
	
	/**
	 * Class to use for handling the custom action bar creation.
	 * 
	 * @param handler Class which extends {@link ActionBarHandler}. If you
	 *                do not want an action bar on devices which do not have
	 *                the native version you may omit this for a normal
	 *                activity.
	 * @return Current instance for builder pattern.
	 */
	public ActionBarSherlock handleCustom(Class<? extends ActionBarHandler<?>> handler) {
		assert this.mAttached == false : ERROR_ATTACHED;
		assert this.mCustomHandler == null : ERROR_HANDLER_CUSTOM;
		assert handler != null : ERROR_HANDLER_CUSTOM_NULL;
		
		this.mCustomHandler = handler;
		return this;
	}
	
	/**
	 * <p>Perform the attachment to the activity and execute the appropriate
	 * onCreate callback to a handler.</p>
	 * 
	 * <p>The class returned exposes no methods itself. Instead, you must test
	 * its feature set by calling <code>instanceof HasXXX</code> where
	 * <code>HasXXX</code> any possible interface (either defined in the
	 * {@link ActionBarSherlock} class or custom implemeneted. You should then
	 * cast this instance to the interface type and interact using its
	 * methods.</p>
	 * 
	 * @return Appropriate handler instance based on the Android version.
	 */
	public ActionBarHandler<?> attach() {
		assert this.mAttached == false : ERROR_ATTACHED;
		assert (this.mLayoutResourceId != null)
			|| (this.mView != null)
			|| (this.mFragment != null) : ERROR_LAYOUTS_NULL;
		
		this.mAttached = true;
		
		//Instantiate the appropriate handler
		ActionBarHandler<?> handler;
		try {
			if (HAS_NATIVE_ACTION_BAR) {
				//If no extended native handler, just use the default one
				if (this.mNativeHandler == null) {
					handler = (ActionBarHandler<?>)Class.forName("com.jakewharton.android.actionbarsherlock.ActionBarSherlock.NativeActionBarHandler").newInstance();
				} else {
					handler = this.mNativeHandler.newInstance();
				}
			} else if (this.mCustomHandler != null) {
				handler = this.mCustomHandler.newInstance();
			} else {
				this.attachDirectly();
				return null;
			}
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		//First, assign the activity to which the handler is attached
		handler.setActivity(this.mActivity);
		
		//Set up the layout of the activity
		if (this.mLayoutResourceId != null) {
			handler.setLayout(this.mLayoutResourceId);
		} else if (this.mFragment != null) {
			//Already checked activity was FragmentActivity in layout(Fragment)
			FragmentManager manager = ((android.support.v4.app.FragmentActivity)this.mActivity).getSupportFragmentManager();
			handler.setLayout(this.mFragment, manager);
		} else {
			handler.setLayout(this.mView);
		}
		
		//Execute the onCreate callback for any additional setup
		handler.onCreate(this.mSavedInstanceState);
		
		//Return handler instance to allow direct (yet abstracted) interaction
		return handler;
	}
	
	/**
	 * Attach the specified layout directly to the activity.
	 */
	private void attachDirectly() {
		//No custom handler so pass the view directly to the activity
		if (this.mLayoutResourceId != null) {
			this.mActivity.setContentView(this.mLayoutResourceId);
		} else if (this.mFragment != null) {
			//Already instanceof FragmentActivity in layout(Fragment)
			FragmentManager manager = ((android.support.v4.app.FragmentActivity)this.mActivity).getSupportFragmentManager();
			manager.beginTransaction()
			       .add(android.R.id.content, this.mFragment)
			       .commit();
		} else {
			this.mActivity.setContentView(this.mView);
		}
	}
	
	
	/**
	 * Base class for handling an action bar that has been created by a
	 * {@link ActionBarSherlock} attachment.
	 *
	 * @param <T> Action bar class.
	 */
	public static abstract class ActionBarHandler<T> {
		/**
		 * Activity to which we are attached.
		 */
		private android.app.Activity mActivity;
		
		/**
		 * Activity's action bar instance.
		 */
		private T mActionBar;


		/**
		 * Get the activity to which the action bar is bound.
		 * 
		 * @return Activity instance.
		 */
		protected final android.app.Activity getActivity() {
			return this.mActivity;
		}
		
		/**
		 * <p>Set the activity to which the action bar is bound.</p>
		 * 
		 * <p><em>This should only be called internally within
		 * {@link ActionBarSherlock}</em></p>
		 * 
		 * @param activity Activity instance.
		 */
		private void setActivity(android.app.Activity activity) {
			this.mActivity = activity;
		}
		
		/**
		 * Get the action bar instance.
		 * 
		 * @return Action bar instance.
		 */
		protected final T getActionBar() {
			return this.mActionBar;
		}
		
		/**
		 * <p>Set the action bar instance.</p>
		 * 
		 * <p><em>This should only be called internally within
		 * {@link ActionBarSherlock}</em></p>
		 * 
		 * @param actionBar Action bar instance.
		 */
		private void setActionBar(T actionBar) {
			this.mActionBar = actionBar;
		}
		
		/**
		 * <p>Initialize the activity's layout.</p>
		 * 
		 * <p>This method will assign the current action bar instance with the
		 * return value of a call to the extending class' initialize
		 * method.</p>
		 * 
		 * @param layoutResourceId Layout resource ID.
		 */
		private void setLayout(int layoutResourceId) {
			this.setActionBar(this.initialize(layoutResourceId));
		}
		
		/**
		 * <p>Initialize the activity's layout.</p>
		 * 
		 * <p>This method will assign the current action bar instance with the
		 * return value of a call to the extending class' initialize
		 * method.</p>
		 *  
		 * @param view View instance.
		 */
		private void setLayout(View view) {
			this.setActionBar(this.initialize(view));
		}
		
		/**
		 * <p>Initialize the activity's layout.</p>
		 * 
		 * <p>This method will assign the current action bar instance with the
		 * return value of a call to the extending class' initialize
		 * method.</p>
		 * 
		 * @param fragment Fragment instance.
		 * @param manager Activity's fragment manager.
		 */
		private void setLayout(Fragment fragment, FragmentManager manager) {
			this.setActionBar(this.initialize(fragment, manager));
		}
		
		/**
		 * Initialize the activity's layout using a layout resource.
		 * 
		 * @param layoutResourceId Layout resource ID.
		 * @return Action bar instance.
		 */
		protected abstract T initialize(int layoutResourceId);
		
		/**
		 * Initialize the activity's layout using an existing view.
		 * 
		 * @param view View instance.
		 * @return Action bar instance.
		 */
		protected abstract T initialize(View view);
		
		/**
		 * Initialize the activity's layout using a {@link Fragment}.
		 * 
		 * @param fragment Fragment instance.
		 * @param manager Activity's fragment manager.
		 * @return Action bar instance.
		 */
		protected abstract T initialize(Fragment fragment, FragmentManager manager);
		
		/**
		 * <p>Callback method for when the attachment is complete and the
		 * handler may perform any additional actions in setting up the action
		 * bar.</p>
		 * 
		 * <p>This is implemented as an empty method so that overriding in
		 * a custom action bar implementation handler and/or an extension of
		 * a custom handler within an activity is optional.</p>
		 * 
		 * @param savedInstanceState Saved activity instance.
		 */
		protected void onCreate(Bundle savedInstanceState) {
			//Grumble, grumble... OVERRIDE ME!
		}
		
		/**
		 * This should be called when an action bar button is clicked. This
		 * method will automatically pass the call on to the parent activity.
		 * 
		 * @param item Clicked MenuItem.
		 */
		protected final void clicked(MenuItem item) {
			this.getActivity().onOptionsItemSelected(item);
		}
		
		/**
		 * Attempt to fetch the logo for the specified activity. If no logo has
		 * been set for the activity, attempt to fetch the application logo. If
		 * that too cannot be found then a
		 * {@link java.lang.IllegalStateException} will be thrown.
		 * 
		 * @return Drawable resource ID of logo.
		 */
		protected final int getActivityLogo() {
			Integer logoResourceId = null;

			//Attempt to obtain the logo from the activity's entry in its manifest
			try {
				logoResourceId = this.getActivity().getPackageManager().getActivityInfo(this.getActivity().getComponentName(), 0).icon;
			} catch (NameNotFoundException e) {}
			
			//If no activity logo was found, try to get the application's logo
			if (logoResourceId == null) {
				logoResourceId = this.getActivity().getApplicationInfo().icon;
			}
			
			if (logoResourceId == null) {
				throw new IllegalStateException(ERROR_LOGO_MISSING);
			}
			
			return logoResourceId;
		}
		
		/**
		 * Inflate an XML menu into an {@link ActionBarMenu}.
		 * @param menuResourceId Resource ID of XML menu.
		 * @return Inflated menu.
		 */
		protected final ActionBarMenu inflateMenu(int menuResourceId) {
			ActionBarMenu menu = new ActionBarMenu(this.getActivity());
			ActionBarMenuInflater menuInflater = new ActionBarMenuInflater(this.getActivity());
			menuInflater.inflate(menuResourceId, menu);
			return menu;
		}
		
		/**
		 * Inflate an XML menu and get only the items which are to be displayed
		 * on the action bar itself. Any remaining items will automatically be
		 * attached to the activity context menu.
		 * 
		 * @param menuResourceId Resource ID of XML menu.
		 * @param maxActionBarItems Maximum number of action bar items.
		 * @return Items which are to be displayed on the action bar.
		 */
		protected final List<ActionBarMenuItem> parseMenu(int menuResourceId, int maxActionBarItems) {
			//Inflate the menu to two categories of items
			List<ActionBarMenuItem> actionBarItems = new LinkedList<ActionBarMenuItem>();
			List<ActionBarMenuItem> overflowItems = new LinkedList<ActionBarMenuItem>();
			this.parseMenu(menuResourceId, maxActionBarItems, actionBarItems, overflowItems);
			
			//Automatically set the remaining items as the activity context menu
			((SherlockActivity)this.getActivity()).setActivityMenu(overflowItems);
			
			//Return the action bar items to the caller
			return actionBarItems;
		}
		
		/**
		 * Inflate an XML menu and get the items which are to be displayed on
		 * the action bar itself and the items which should overflow into a
		 * secondary menu.
		 * 
		 * @param menuResourceId Resource ID of XML menu.
		 * @param maxActionBarItems Maximum number of action bar items.
		 * @param actionBarItems Output parameter of action bar items. You must
		 * pass in an instance to which the items will be added.
		 * @param overflowItems Output parameter of overflow items. You must
		 * pass in an instance to which the items will be added.
		 */
		protected final void parseMenu(int menuResourceId, int maxActionBarItems, List<ActionBarMenuItem> actionBarItems, List<ActionBarMenuItem> overflowItems) {
			assert actionBarItems != null;
			assert overflowItems != null;
			assert maxActionBarItems > 0;
			
			ActionBarMenu menu = this.inflateMenu(menuResourceId);
			int ifItems = 0;
			for (ActionBarMenuItem item : menu.getItems()) {
				if ((item.getShowAsAction() & ActionBarMenuItem.SHOW_AS_ACTION_ALWAYS) != 0) {
					actionBarItems.add(item);
					
					if ((actionBarItems.size() > maxActionBarItems) && (ifItems > 0)) {
						//If we have exceeded the max and there are "ifRoom" items
						//then iterate backwards to remove one and add it to the
						//head of the classic items list.
						for (int i = actionBarItems.size() - 1; i >= 0; i--) {
							if ((actionBarItems.get(i).getShowAsAction() & ActionBarMenuItem.SHOW_AS_ACTION_IF_ROOM) != 0) {
								overflowItems.add(0, actionBarItems.get(i));
								actionBarItems.remove(i);
								ifItems -= 1;
								break;
							}
						}
					}
				} else if (((item.getShowAsAction() & ActionBarMenuItem.SHOW_AS_ACTION_IF_ROOM) != 0)
						&& (actionBarItems.size() < maxActionBarItems)) {
					//"ifRoom" items are added if we have not exceeded the max.
					actionBarItems.add(item);
					ifItems += 1;
				} else {
					//"never" items and "ifRoom" items (when max is exceeded)
					//get added to the overflow list.
					overflowItems.add(item);
				}
			}
		}
	}
	
	
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
	public static class NativeActionBarHandler extends ActionBarHandler<android.app.ActionBar> implements HasTitle, HasSubtitle, HasHomeAsUp, HasLogo, HasListNavigation, HasTabNavigation, HasMenu, HasVisibility, HasNavigationState, HasCustomView, android.app.ActionBar.TabListener {
		@Override
		public final android.app.ActionBar initialize(int layoutResourceId) {
			this.getActivity().setContentView(layoutResourceId);
			return this.getActivity().getActionBar();
		}
		
		@Override
		public final android.app.ActionBar initialize(View view) {
			this.getActivity().setContentView(view);
			return this.getActivity().getActionBar();
		}
		
		@Override
		public final android.app.ActionBar initialize(Fragment fragment, FragmentManager manager) {
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
		public final void showTitle(boolean value) {
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
		public final void useHomeAsUp(boolean showHomeAsUp) {
			this.getActionBar().setDisplayHomeAsUpEnabled(showHomeAsUp);
		}

		@Override
		public final void useLogo(boolean useLogo) {
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
			this.getActionBar().setListNavigationCallbacks(adapter, new android.app.ActionBar.OnNavigationListener() {
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
		public final void setMenuVisiblityListener(final OnMenuVisibilityListener listener) {
			this.getActionBar().addOnMenuVisibilityListener(new android.app.ActionBar.OnMenuVisibilityListener() {
				@Override
				public void onMenuVisibilityChanged(boolean isVisible) {
					listener.onMenuVisibilityChanged(isVisible);
				}
			});
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
		public final void show() {
			this.getActionBar().show();
		}
		
		@Override
		public final int getNavigationMode() {
			return this.getActionBar().getNavigationMode();
		}
		
		@Override
		public View getCustomView() {
			return this.getActionBar().getCustomView();
		}

		@Override
		public void setCustomView(int resourceId) {
			this.getActionBar().setCustomView(resourceId);
		}

		@Override
		public void setCustomView(View view) {
			this.getActionBar().setCustomView(view);
		}

		@Override
		public void setCustomView(View view, LayoutParams layoutParameters) {
			//Copy custom LayoutParams into native version
			android.app.ActionBar.LayoutParams nativeParams = new android.app.ActionBar.LayoutParams(layoutParameters.width, layoutParameters.height, layoutParameters.gravity);
			nativeParams.bottomMargin = layoutParameters.bottomMargin;
			nativeParams.layoutAnimationParameters = layoutParameters.layoutAnimationParameters;
			nativeParams.leftMargin = layoutParameters.leftMargin;
			nativeParams.rightMargin = layoutParameters.rightMargin;
			nativeParams.topMargin = layoutParameters.topMargin;
			
			this.getActionBar().setCustomView(view, nativeParams);
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
	
	
	/**************************************************************************\
	 * HasXXX Interface    *  Native ActionBar methods                        *
	 **************************************************************************
	 * HasMenu             *  addOnMenuVisibilityListener(ActionBar.OnMenuVisibilityListener listener)
	 * HasTabNavigation    *  addTab(ActionBar.Tab tab)
	 * HasTabNavigation    *  addTab(ActionBar.Tab tab, boolean setSelected)
	 * HasTabNavigation    *  addTab(ActionBar.Tab tab, int position)
	 * HasTabNavigation    *  addTab(ActionBar.Tab tab, int position, boolean setSelected)
	 * HasCustomView       *  getCustomView()
	 *                     *  getDisplayOptions()
	 *                     *  getHeight()
	 * List & Tab          *  getNavigationItemCount()
	 * HasNavigationState  *  getNavigationMode()
	 * List & Tab          *  getSelectedNavigationIndex()
	 * HasTabNavigation    *  getSelectedTab()
	 * HasSubtitle         *  getSubtitle()
	 * HasTabNavigation    *  getTabAt(int index)
	 * HasTabNavigation    *  getTabCount()
	 * HasTitle            *  getTitle()
	 * HasVisibility       *  hide()
	 * HasVisibility       *  isShowing()
	 * HasTabNavigation    *  newTab()
	 * HasTabNavigation    *  removeAllTabs()
	 *                     *  removeOnMenuVisibilityListener(ActionBar.OnMenuVisibilityListener listener)
	 * HasTabNavigation    *  removeTab(ActionBar.Tab tab)
	 * HasTabNavigation    *  removeTabAt(int position)
	 * HasTabNavigation    *  selectTab(ActionBar.Tab tab)
	 *                     *  setBackgroundDrawable(Drawable d)
	 * HasCustomView       *  setCustomView(int resId)
	 * HasCustomView       *  setCustomView(View view)
	 * HasCustomView       *  setCustomView(View view, ActionBar.LayoutParams layoutParams)
	 * HasHomeAsUp         *  setDisplayHomeAsUpEnabled(boolean showHomeAsUp)
	 *                     *  setDisplayOptions(int options, int mask)
	 *                     *  setDisplayOptions(int options)
	 *                     *  setDisplayShowCustomEnabled(boolean showCustom)
	 *                     *  setDisplayShowHomeEnabled(boolean showHome)
	 * HasTitle            *  setDisplayShowTitleEnabled(boolean showTitle)
	 * HasLogo             *  setDisplayUseLogoEnabled(boolean useLogo)
	 * HasListNavigation   *  setListNavigationCallbacks(SpinnerAdapter adapter, ActionBar.OnNavigationListener callback)
	 * HasNavigationState  *  setNavigationMode(int mode)
	 * List & Tab          *  setSelectedNavigationItem(int position)
	 * HasSubtitle         *  setSubtitle(int resId)
	 * HasSubtitle         *  setSubtitle(CharSequence subtitle)
	 * HasTitle            *  setTitle(CharSequence title)
	 * HasTitle            *  setTitle(int resId)
	 * HasVisibility       *  show()
	\**************************************************************************/
	
	/**
	 * Interface which denotes a third-party action bar handler implementation
	 * supports populating the action bar from an XML menu.
	 */
	public static interface HasMenu {
		/**
		 * <p>Populate the action bar with items from an XML menu.</p>
		 * 
		 * <p>A convenience method, {@link ActionBarHandler#inflateMenu(int)},
		 * is available which will perform the inflation and return an
		 * {@link ActionBarMenu}.</p>
		 * 
		 * @param menuResourceId Resource ID of menu XML.
		 */
		public void setMenuResourceId(int menuResourceId);
		
		/**
		 * Add a listener that will respond to menu visibility change events.
		 * 
		 * @param listener The new listener to add.
		 */
		public void setMenuVisiblityListener(OnMenuVisibilityListener listener);
	}
	
	/**
	 * Interface which denotes a third-party action bar handler implementation
	 * supports using the activity logo rather than just the home icon and a
	 * title.
	 */
	public static interface HasLogo {
		/**
		 * <p>Set whether to display the activity logo rather than the activity
		 * icon. A logo is often a wider, more detailed image.</p>
		 * 
		 * <p>For handlers: A convenience method,
		 * {@link ActionBarHandler#getActivityLogo()}, is available which will
		 * automatically fetch and return a drawable resource ID.</p>
		 * 
		 * @param useLogo Value.
		 */
		public void useLogo(boolean useLogo);
	}
	
	/**
	 * Interface which denotes a handler supports using a drop-down list for
	 * navigation.
	 */
	public static interface HasListNavigation {
		/**
		 * Use drop-down navigation.
		 * 
		 * @param adapter List of drop-down items.
		 * @param listener Callback listener for when an item is clicked.
		 */
		public void setList(SpinnerAdapter adapter, OnNavigationListener listener);
		
		/**
		 * Set the selected list item.
		 * 
		 * @param position Item position.
		 */
		public void selectItem(int position);

		/**
		 * Get the position of the selected item.
		 * 
		 * @return Selected item index.
		 */
		public int getSelectedItemIndex();

		/**
		 * Returns the number of items currently registered with the list.
		 * 
		 * @return Item count.
		 */
		public int getItemCount();
	}
	
	/**
	 * Interface which denotes a handler supports setting the home action item
	 * as an "up" affordance.
	 */
	public static interface HasHomeAsUp {
		/**
		 * Set whether home should be displayed as an "up" affordance. Set this
		 * to true if selecting "home" returns up by a single level in your UI
		 * rather than back to the top level or front page.
		 * 
		 * @param showHomeAsUp True to show the user that selecting home will
		 * return one level up rather than to the top level of the application.
		 */
		public void useHomeAsUp(boolean showHomeAsUp);
	}
	
	/**
	 * Interface which denotes a handler supports the adding of tabs to its
	 * action bar.
	 */
	public static interface HasTabNavigation {
		/**
		 * Remove all tabs from the action bar and deselect the current tab.
		 */
		public void removeAllTabs();
		
		/**
		 * Remove a tab from the action bar. If the removed tab was selected it
		 * will be deselected and another tab will be selected if present.
		 * 
		 * @param tab The tab to remove.
		 */
		public void removeTab(ActionBarTab tab);
		
		/**
		 * Remove a tab from the action bar. If the removed tab was selected it
		 * will be deselected and another tab will be selected if present.
		 * 
		 * @param position Position of tab to remove.
		 */
		public void removeTabAt(int position);

		/**
		 * Set the selected tab position.
		 * 
		 * @param position Tab position.
		 */
		public void selectTab(int position);
		
		/**
		 * <p>Select the specified tab. If it is not a child of this action bar
		 * it will be added.</p>
		 * 
		 * <p>Note: if you want to select a tab by index use
		 * {@link #selectTab(int)}.</p>
		 * 
		 * @param tab Tab to select.
		 */
		public void selectTab(ActionBarTab tab);
		
		/**
		 * Add a tab for use in tabbed navigation mode. The tab will be added
		 * at the end of the list. If this is the first tab to be added it will
		 * become the selected tab.
		 * 
		 * @param tab Tab to add.
		 */
		public void addTab(ActionBarTab tab);
		
		/**
		 * Returns the number of tabs currently registered with the action bar.
		 * 
		 * @return Tab count.
		 */
		public int getTabCount();
		
		/**
		 * Returns the tab at the specified index.
		 * 
		 * @param position Index value in the range 0-get.
		 * @return Tab instance.
		 */
		public ActionBarTab getTabAt(int position);
		
		/**
		 * Returns the currently selected tab if in tabbed navigation mode and
		 * there is at least one tab present.
		 * 
		 * @return The currently selected tab or null.
		 */
		public ActionBarTab getSelectedTab();
		
		/**
		 * Get the position of the selected tab.
		 * 
		 * @return Selected tab index.
		 */
		public int getSelectedTabIndex();
	}
	
	/**
	 * Interface which denotes a handler supports a title.
	 */
	public static interface HasTitle {
		/**
		 * Returns the current action bar title in standard mode.
		 * 
		 * @return The current action bar title or null.
		 */
		public CharSequence getTitle();
		
		/**
		 * Set the action bar's title.
		 * 
		 * @param title Title to set.
		 */
		public void setTitle(CharSequence title);
		
		/**
		 * Set the action bar's title.
		 * 
		 * @param resourceId Resource ID of title string to set.
		 */
		public void setTitle(int resourceId);

		/**
		 * Set whether an activity title/subtitle should be displayed.
		 * 
		 * @param value True to display a title/subtitle if present.
		 */
		public void showTitle(boolean value);
	}
	
	/**
	 * Interface which denotes a handler supports a subtitle.
	 */
	public static interface HasSubtitle {
		/**
		 * Returns the current ActionBar subtitle in standard mode.
		 * 
		 * @return The current ActionBar subtitle or null.
		 */
		public CharSequence getSubtitle();
		
		/**
		 * Set the action bar's subtitle.
		 * 
		 * @param subtitle Title to set.
		 */
		public void setSubtitle(CharSequence subtitle);
		
		/**
		 * Set the action bar's subtitle.
		 * 
		 * @param resourceId Resource ID of title string to set.
		 */
		public void setSubtitle(int resourceId);
	}
	
	/**
	 * Interface which denotes a handler supports changing action bar
	 * visibility.
	 */
	public static interface HasVisibility {
		/**
		 * Hide the ActionBar if it is not currently showing.
		 */
		public void hide();
		
		/**
		 * Get action bar visibility state.
		 * 
		 * @return True if the ActionBar is showing, false otherwise.
		 */
		public boolean isShowing();
		
		/**
		 * Show the ActionBar if it is not currently showing.
		 */
		public void show();
	}
	
	/**
	 * Interface which denotes a handler supports maintaining navigation state.
	 */
	public static interface HasNavigationState {
		/**
		 * Returns the current navigation mode. The result will be one of:
		 * <ul>
		 *   <li>{@link #MODE_STANDARD}</li>
		 *   <li>{@link #MODE_LIST}</li>
		 *   <li>{@link #MODE_TABS}</li>
		 * </ul>
		 * 
		 * @return The current navigation mode.
		 */
		public int getNavigationMode();
		
		/**
		 * Set the current navigation mode.
		 * 
		 * @param navigationMode The new mode to set.
		 * 
		 * @see #MODE_STANDARD
		 * @see #MODE_LIST
		 * @see #MODE_TABS
		 */
		public void setNavigationMode(int navigationMode);
		
		
		//NOTE: The following values will be inlined by the compiler so it does
		//      not matter that we reference android.app.ActionBar directly.
		
		/**
		 * List navigation mode. Instead of static title text this mode
		 * presents a list menu for navigation within the activity. e.g. this
		 * might be presented to the user as a drop-down list.
		 */
		public static final int MODE_LIST = android.app.ActionBar.NAVIGATION_MODE_LIST;
		
		/**
		 * Standard navigation mode. Consists of either a logo or icon and
		 * title text with an optional subtitle. Clicking any of these
		 * elements will dispatch
		 * {@link OnNavigationListener#onNavigationItemSelected(int, long)}
		 * to the host activity with a menu item with item ID
		 * <code>android.R.id.home</code>.
		 */
		public static final int MODE_STANDARD = android.app.ActionBar.NAVIGATION_MODE_STANDARD;
		
		/**
		 * Tab navigation mode. Instead of static title text this mode presents
		 * a series of tabs for navigation within the activity.
		 */
		public static final int MODE_TABS = android.app.ActionBar.NAVIGATION_MODE_TABS;
	}
	
	/**
	 * Interface which denotes a handler supports displaying a custom view.
	 */
	public static interface HasCustomView {
		/**
		 * @return The current custom view.
		 */
		public View getCustomView();
		
		/**
		 * <p>Set the action bar into custom navigation mode, supplying a view
		 * for custom navigation.</p>
		 * 
		 * <p>Custom navigation views appear between the application icon and
		 * any action buttons and may use any space available there. Common use
		 * cases for custom navigation views might include an auto-suggesting
		 * address bar for a browser or other navigation mechanisms that do not
		 * translate well to provided navigation modes.</p>
		 * 
		 * @param resourceId Resource ID of a layout to inflate.
		 */
		public void setCustomView(int resourceId);
		
		/**
		 * <p>Set the action bar into custom navigation mode, supplying a view
		 * for custom navigation.</p>
		 * 
		 * <p>Custom navigation views appear between the application icon and
		 * any action buttons and may use any space available there. Common use
		 * cases for custom navigation views might include an auto-suggesting
		 * address bar for a browser or other navigation mechanisms that do not
		 * translate well to provided navigation modes.</p>
		 * 
		 * @param view Custom navigation view.
		 */
		public void setCustomView(View view);
		
		/**
		 * <p>Set the action bar into custom navigation mode, supplying a view
		 * for custom navigation.</p>
		 * 
		 * <p>Custom navigation views appear between the application icon and
		 * any action buttons and may use any space available there. Common use
		 * cases for custom navigation views might include an auto-suggesting
		 * address bar for a browser or other navigation mechanisms that do not
		 * translate well to provided navigation modes.</p>
		 * 
		 * @param view Custom navigation view.
		 * @param layoutParameters How this custom view should layout.
		 */
		public void setCustomView(View view, LayoutParams layoutParameters);
	}
	
	
	/**
	 * <p>Listener interface for ActionBar navigation events.</p>
	 * 
	 * <p>Emulates {@link android.app.ActionBar.OnNavigationListener}.</p>
	 */
	public static interface OnNavigationListener {
		/**
		 * This method is called whenever a navigation item in your action bar is selected.
		 * 
		 * @param itemPosition Position of the item clicked.
		 * @param itemId ID of the item clicked.
		 * @return True if the event was handled, false otherwise.
		 */
		boolean onNavigationItemSelected(int itemPosition, long itemId);
	}
	
	/**
	 * Listener for receiving events when action bar menus are shown or hidden.
	 * 
	 * <p>Emulates {@link android.app.ActionBar.OnMenuVisibilityListener}.</p>
	 */
	public static interface OnMenuVisibilityListener {
		/**
		 * Called when an action bar menu is shown or hidden. Applications may
		 * want to use this to tune auto-hiding behavior for the action bar or
		 * pause/resume video playback, gameplay, or other activity within the
		 * main content area.
		 * 
		 * @param isVisible True if an action bar menu is now visible, false
		 * if no action bar menus are visible.
		 */
		void onMenuVisibilityChanged(boolean isVisible);
	}
	
	/**
	 * <p>Callback interface invoked when a tab is focused, unfocused, added, or
	 * removed.</p>
	 * 
	 * <p>Emulates {@link android.app.ActionBar.TabListener}.</p>
	 */
	public static interface TabListener {
		/**
		 * Called when a tab that is already selected is chosen again by the
		 * user. Some applications may use this action to return to the top
		 * level of a category.
		 * 
		 * @param tab The tab that was reselected.
		 */
		public void onTabReselected(ActionBarTab tab);
		
		/**
		 * Called when a tab enters the selected state.
		 * 
		 * @param tab The tab that was selected.
		 */
		public void onTabSelected(ActionBarTab tab);
		
		/**
		 * Called when a tab exits the selected state.
		 * 
		 * @param tab The tab that was unselected.
		 */
		public void onTabUnselected(ActionBarTab tab);
	}
	
	/**
	 * <p>Per-child layout information associated with action bar custom views.</p>
	 * 
	 * <p>Emulates {@link android.app.ActionBar.LayoutParams}.</p>
	 */
	public static class LayoutParams extends MarginLayoutParams {
		/**
		 * Gravity for the view associated with these LayoutParams.
		 * 
		 * @see android.view.Gravity
		 **/
		public int gravity;
		
		
		public LayoutParams(Context context, AttributeSet attributeSet) {
			super(context, attributeSet);
		}
		
		public LayoutParams(int width, int height) {
			super(width, height);
		}
		
		public LayoutParams(int width, int height, int gravity) {
			super(width, height);
			this.gravity = gravity;
		}
		
		/*public LayoutParams(int gravity) {
			super(???);
			this.gravity = gravity;
		}*/
		
		public LayoutParams(LayoutParams source) {
			super(source);
			this.gravity = source.gravity;
		}
		
		public LayoutParams(ViewGroup.LayoutParams source) {
			this(source.width, source.height);
			this.layoutAnimationParameters = source.layoutAnimationParameters;
		}
	}
	
	
	/**
	 * Interface of helper methods implemented by all helper classes.
	 */
	static interface SherlockActivity {
		/**
		 * Set the menu XML resource ID for inflation to the native action bar.
		 * If a third-party action bar is being used it will be automatically
		 * inflated in the {@link ActionBarSherlock#attach()} method.
		 * 
		 * @param menuResourceId Resource ID of menu XML.
		 */
		void setActionBarMenu(int menuResourceId);
		
		/**
		 * Set additional items which should be added to the activity context
		 * menu. <strong>This should ONLY be called when the native action bar
		 * is not being utilized.</strong>
		 * 
		 * @param items Menu items.
		 * @throws RuntimeException If called on an activity which has access to
		 * the native action bar.
		 */
		void setActivityMenu(List<ActionBarMenuItem> menuItems);
		
		/**
		 * Callback for after the menu has been successfully loaded.
		 */
		void onOptionsMenuCreated(Menu menu);
	}
	
	
	/**
	 * Special {@link android.app.Activity} wrapper which will allow for
	 * unifying common functionality via the {@link ActionBarSherlock} API.
	 */
	public static abstract class Activity extends android.app.Activity implements SherlockActivity {
		/**
		 * Resource ID of menu XML.
		 */
		private Integer mMenuResourceId;
		
		/**
		 * List of menu items to add to the context menu.
		 */
		private List<ActionBarMenuItem> mMenuItems;
		
		
		@Override
		public final void setActionBarMenu(int menuResourceId) {
			this.mMenuResourceId = menuResourceId;
		}
		
		@Override
		public final void setActivityMenu(List<ActionBarMenuItem> menuItems) {
			this.mMenuItems = menuItems;
		}

		@Override
		public final boolean onCreateOptionsMenu(Menu menu) {
			if (this.mMenuResourceId != null) {
				this.getMenuInflater().inflate(this.mMenuResourceId, menu);
				this.onOptionsMenuCreated(menu);
				return true;
			} else if ((this.mMenuItems != null) && (this.mMenuItems.size() > 0)) {
				for (ActionBarMenuItem menuItem : this.mMenuItems) {
					menuItem.addTo(menu);
				}
				this.onOptionsMenuCreated(menu);
				return true;
			}
			return false;
		}

		@Override
		public void onOptionsMenuCreated(Menu menu) {
			//Grumble, grumble... OVERRIDE ME!
		}
	}
	
	/**
	 * Special {@link android.app.ListActivity} wrapper which will allow for
	 * unifying common functionality via the {@link ActionBarSherlock} API.
	 * 
	 * @deprecated The use of {@link android.support.v4.app.Fragment}s and
	 * {@link android.support.v4.app.ListFragment} is suggested for working
	 * with lists. It provides a much nicer experience when scaled up to the
	 * large tablet-sized screens.
	 */
	public static abstract class ListActivity extends android.app.ListActivity implements SherlockActivity {
		/**
		 * Resource ID of menu XML.
		 */
		private Integer mMenuResourceId;
		
		/**
		 * List of menu items to add to the context menu.
		 */
		private List<ActionBarMenuItem> mMenuItems;
		
		
		@Override
		public void setActionBarMenu(int menuResourceId) {
			this.mMenuResourceId = menuResourceId;
		}
		
		@Override
		public final void setActivityMenu(List<ActionBarMenuItem> menuItems) {
			this.mMenuItems = menuItems;
		}

		@Override
		public final boolean onCreateOptionsMenu(Menu menu) {
			if (this.mMenuResourceId != null) {
				this.getMenuInflater().inflate(this.mMenuResourceId, menu);
				this.onOptionsMenuCreated(menu);
				return true;
			} else if ((this.mMenuItems != null) && (this.mMenuItems.size() > 0)) {
				for (ActionBarMenuItem menuItem : this.mMenuItems) {
					menuItem.addTo(menu);
				}
				this.onOptionsMenuCreated(menu);
				return true;
			}
			return false;
		}

		@Override
		public void onOptionsMenuCreated(Menu menu) {
			//Grumble, grumble... OVERRIDE ME!
		}
	}
	
	/**
	 * Special {@link android.support.v4.app.FragmentActivity} wrapper which
	 * will allow for unifying common functionality via the
	 * {@link ActionBarSherlock} API.
	 */
	public static abstract class FragmentActivity extends android.support.v4.app.FragmentActivity implements SherlockActivity {
		/**
		 * Resource ID of menu XML.
		 */
		private Integer mMenuResourceId;
		
		/**
		 * List of menu items to add to the context menu.
		 */
		private List<ActionBarMenuItem> mMenuItems;
		
		
		@Override
		public void setActionBarMenu(int menuResourceId) {
			this.mMenuResourceId = menuResourceId;
		}
		
		@Override
		public final void setActivityMenu(List<ActionBarMenuItem> menuItems) {
			this.mMenuItems = menuItems;
		}

		@Override
		public final boolean onCreateOptionsMenu(Menu menu) {
			if (this.mMenuResourceId != null) {
				this.getMenuInflater().inflate(this.mMenuResourceId, menu);
				this.onOptionsMenuCreated(menu);
				return true;
			} else if ((this.mMenuItems != null) && (this.mMenuItems.size() > 0)) {
				for (ActionBarMenuItem menuItem : this.mMenuItems) {
					menuItem.addTo(menu);
				}
				this.onOptionsMenuCreated(menu);
				return true;
			}
			return false;
		}

		@Override
		public void onOptionsMenuCreated(Menu menu) {
			//Grumble, grumble... OVERRIDE ME!
		}
	}
}
