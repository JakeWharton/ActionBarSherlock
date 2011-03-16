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

import java.util.ArrayList;
import java.util.List;
import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

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
 * extending the default handler in an activity as an inner-classes. The
 * classes will allow for overriding various methods to handle the creation
 * of and interaction with each type of action bar.</p>
 * 
 * <p>Examples of third-party implementations can be found in the
 * <code>samples</code> folder of this repository, or by visiting the
 * <a href="https://github.com/JakeWharton/ActionBarSherlock/">GitHub project
 * page</a>.</p>
 * 
 * @author Jake Wharton <jakewharton@gmail.com>
 * @verion 2.0.0
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
	 * Title to automatically set on whichever type of action bar is selected.
	 */
	private CharSequence mTitle;
	
	/**
	 * Resource ID of the menu to inflate to the action bar.
	 */
	private Integer mMenuResourceId;
	
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
		return new ActionBarSherlock(activity);
	}
	
	
	
	/**
	 * Internal-only constructor to initialize this class for the builder
	 * pattern. Implementing activities should use the
	 * {@link from(android.app.Activity)} method to create a new instance.
	 * 
	 * @param activity Activity on which to bind.
	 */
	private ActionBarSherlock(android.app.Activity activity) {
		this.mAttached = false;
		this.mActivity = activity;
	}
	
	
	
	/**
	 * Include a saved state to pass to the appropriate handler's onCreate
	 * method.
	 * 
	 * @param savedInstanceState Saved instance.
	 * @return Current instance for builder pattern.
	 */
	public ActionBarSherlock with(Bundle savedInstanceState) {
		assert this.mAttached == false;
		assert this.mSavedInstanceState == null;
		
		this.mSavedInstanceState = savedInstanceState;
		return this;
	}
	
	/**
	 * Layout resource to use for the activity's content.
	 * 
	 * @param layoutResource Layout resource.
	 * @return Current instance for builder pattern.
	 */
	public ActionBarSherlock layout(int layoutResource) {
		assert this.mAttached == false;
		assert this.mLayoutResourceId == null;
		assert this.mView == null;
		
		this.mLayoutResourceId = layoutResource;
		return this;
	}
	
	/**
	 * View to use for the activity's content.
	 * 
	 * @param view Content view instance.
	 * @return Current instance for builder pattern.
	 */
	public ActionBarSherlock layout(View view) {
		assert this.mAttached == false;
		assert this.mLayoutResourceId == null;
		assert this.mView == null;
		
		this.mView = view;
		
		return this;
	}
	
	/**
	 * Initial string resource to use for setting the title of the action bar.
	 * 
	 * @param stringResourceId String resource ID.
	 * @return Current instance for builder pattern.
	 */
	public ActionBarSherlock title(int stringResourceId) {
		assert this.mAttached == false;
		return this.title(this.mActivity.getResources().getString(stringResourceId));
	}
	
	/**
	 * String to use for setting the title of the action bar.
	 * 
	 * @param title Title string.
	 * @return Current instance for builder pattern.
	 */
	public ActionBarSherlock title(CharSequence title) {
		assert this.mAttached == false;
		assert this.mTitle == null;
		
		this.mTitle = title;
		return this;
	}
	
	/**
	 * Resource ID of a menu to inflate as buttons onto the action bar. This
	 * will fall back to 
	 * 
	 * @param menuResourceId
	 * @return
	 */
	public ActionBarSherlock menu(int menuResourceId) {
		assert this.mAttached == false;
		assert this.mMenuResourceId == null;
		
		this.mMenuResourceId = menuResourceId;
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
		assert this.mAttached == false;
		assert this.mNativeHandler == null;
		
		this.mNativeHandler = handler;
		return this;
	}
	
	/**
	 * Class to use for handling the custom action bar creation.
	 * 
	 * @param handler Class which extends {@link ActionBarHandler<?>}. If you
	 *                do not want an action bar on devices which do not have
	 *                the native version you may omit this for a normal
	 *                activity.
	 * @return Current instance for builder pattern.
	 */
	public ActionBarSherlock handleCustom(Class<? extends ActionBarHandler<?>> handler) {
		assert this.mAttached == false;
		assert this.mCustomHandler == null;
		
		this.mCustomHandler = handler;
		return this;
	}
	
	/**
	 * Perform the attachment to the activity and execute the appropriate
	 * onCreate callback to a handler.
	 */
	public void attach() {
		assert this.mAttached == false;
		assert (this.mLayoutResourceId != null)
			|| (this.mView != null);
		
		if (this.mNativeHandler == null) {
			this.mNativeHandler = NativeActionBarHandler.class;
		}
		
		ActionBarHandler<?> handler;
		try {
			if (HAS_NATIVE_ACTION_BAR) {
				handler = this.mNativeHandler.newInstance();
			} else if (this.mCustomHandler != null) {
				handler = this.mCustomHandler.newInstance();
			} else {
				//No custom handler so pass the view directly to the activity
				if (this.mLayoutResourceId != null) {
					this.mActivity.setContentView(this.mLayoutResourceId);
				} else {
					this.mActivity.setContentView(this.mView);
				}
				return;
			}
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		
		handler.setActivity(this.mActivity);
		
		if (this.mLayoutResourceId != null) {
			handler.setLayout(this.mLayoutResourceId);
		} else {
			handler.setLayout(this.mView);
		}
		
		if ((this.mActivity instanceof ActionBarSherlock.Activity) && (this.mMenuResourceId != null)) {
			((ActionBarSherlock.Activity)this.mActivity).setActionBarMenu(this.mMenuResourceId, handler);
		}
		
		if (this.mTitle != null) {
			handler.setTitle(this.mTitle);
		}
		
		handler.onCreate(this.mSavedInstanceState);
	}
	
	
	/**
	 * Base class for handling an action bar that has been created by a
	 * {@link ActionBarSherlock} attachment.
	 *
	 * @param <T> Action bar class.
	 */
	public static abstract class ActionBarHandler<T> {
		private android.app.Activity mActivity;
		private T mActionBar;

		/**
		 * Get the activity to which the action bar is bound.
		 * 
		 * @return Activity instance.
		 */
		public final android.app.Activity getActivity() {
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
		public T getActionBar() {
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
		 *  <p>This method will assign the current action bar instance with the
		 *  return value of a call to the extending class' initialize
		 *  method.</p>
		 * 
		 * @param layoutResourceId Layout resource ID.
		 */
		private void setLayout(int layoutResourceId) {
			this.setActionBar(this.initialize(layoutResourceId));
		}
		
		/**
		 * <p>Initialize the activity's layout.</p>
		 * 
		 *  <p>This method will assign the current action bar instance with the
		 *  return value of a call to the extending class' initialize
		 *  method.</p>
		 *  
		 * @param view View instance.
		 */
		private void setLayout(View view) {
			this.setActionBar(this.initialize(view));
		}
		
		/**
		 * Initialize the activity's layout using a layout resource.
		 * 
		 * @param layoutResourceId Layout resource ID.
		 * @return Action bar instance.
		 */
		public abstract T initialize(int layoutResourceId);
		
		/**
		 * Initialize the activity's layout using an existing view.
		 * 
		 * @param view View instance.
		 * @return Action bar instance.
		 */
		public abstract T initialize(View view);
		
		/**
		 * Set the title of the action bar.
		 * 
		 * @param title Title string.
		 */
		public abstract void setTitle(CharSequence title);
		
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
		public void onCreate(Bundle savedInstanceState) {
			//Grumble, grumble... OVERRIDE ME!
		}
	}
	
	/**
	 * Minimal handler for Android's native {@link android.app.ActionBar}.
	 */
	public static class NativeActionBarHandler extends ActionBarHandler<ActionBar> {
		@Override
		public ActionBar initialize(int layoutResourceId) {
			//For native action bars assigning a layout is all that is required
			this.getActivity().setContentView(layoutResourceId);
			
			return this.getActivity().getActionBar();
		}
		
		@Override
		public ActionBar initialize(View view) {
			//For native action bars assigning a layout is all that is required
			this.getActivity().setContentView(view);
			
			return this.getActivity().getActionBar();
		}

		@Override
		public void setTitle(CharSequence title) {
			this.getActionBar().setTitle(title);
		}
	}
	
	public static interface ActionBarMenuHandler {
		public void addItem(ActionBarMenuItem item);
	}

	/**
	 * Special activity wrapper which will allow for unifying common
	 * functionality via the {@link ActionBarSherlock} activity API.
	 */
	public static abstract class Activity extends android.app.Activity {
		private Integer mMenuResourceId;
		private boolean mHasMenuHandler;
		
		public void setActionBarMenu(int menuResourceId, ActionBarHandler<?> handler) {
			this.mMenuResourceId = menuResourceId;
			this.mHasMenuHandler = handler instanceof ActionBarMenuHandler;
			
			if (!ActionBarSherlock.HAS_NATIVE_ACTION_BAR && this.mHasMenuHandler) {
				//Has menu, not native, handler handles menu
				ActionBarMenuHandler menuHandler = (ActionBarMenuHandler)handler;
				
				ActionBarMenu menu = new ActionBarMenu(handler.getActivity());
				this.getMenuInflater().inflate(this.mMenuResourceId, menu);
				
				//Delegate to the handler for addition to the action bar
				for (ActionBarMenuItem item : menu.getItems()) {
					menuHandler.addItem(item);
				}
			}
		}

		@Override
		public final boolean onCreateOptionsMenu(Menu menu) {
			if ((this.mMenuResourceId != null) && (ActionBarSherlock.HAS_NATIVE_ACTION_BAR || !this.mHasMenuHandler)) {
				//Inflate to native action bar or native menu if no handler
				this.getMenuInflater().inflate(this.mMenuResourceId, menu);
				return true;
			} else {
				//No applicable inflation targets
				return false;
			}
		}
	}
	
	/*
	 * See: com.android.internal.view.menu.MenuBuilder
	 */
	private static class ActionBarMenu implements Menu {
		private static final int DEFAULT_ITEM_ID = 0;
		private static final int DEFAULT_GROUP_ID = 0;
		private static final int DEFAULT_ORDER = 0;
		
		private final Context mContext;
		private final List<ActionBarMenuItem> mItems;
		
		private ActionBarMenu(Context context) {
			this.mContext = context;
			this.mItems = new ArrayList<ActionBarMenuItem>();
		}
		
		
		@Override
		public MenuItem add(CharSequence title) {
			ActionBarMenuItem item = new ActionBarMenuItem(this.mContext, DEFAULT_ITEM_ID, DEFAULT_GROUP_ID, DEFAULT_ORDER, title);
			this.mItems.add(item);
			return item;
		}

		@Override
		public MenuItem add(int titleResourceId) {
			ActionBarMenuItem item = new ActionBarMenuItem(this.mContext, 0, 0, 0, titleResourceId);
			this.mItems.add(item);
			return item;
		}

		@Override
		public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
			ActionBarMenuItem item = new ActionBarMenuItem(this.mContext, itemId, groupId, order, title);
			this.mItems.add(item);
			return item;
		}

		@Override
		public MenuItem add(int groupId, int itemId, int order, int titleResourceId) {
			ActionBarMenuItem item = new ActionBarMenuItem(this.mContext, itemId, groupId, order, titleResourceId);
			this.mItems.add(item);
			return item;
		}

		@Override
		public void clear() {
			this.mItems.clear();
		}

		@Override
		public void close() {}

		@Override
		public MenuItem findItem(int itemId) {
			for (MenuItem item : this.mItems) {
				if (item.getItemId() == itemId) {
					return item;
				}
			}
			throw new IndexOutOfBoundsException("No item with id " + itemId);
		}

		@Override
		public MenuItem getItem(int index) {
			return this.mItems.get(index);
		}

		@Override
		public boolean hasVisibleItems() {
			for (MenuItem item : this.mItems) {
				if (item.isVisible()) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void removeItem(int itemId) {
			final int size = this.mItems.size();
			for (int i = 0; i < size; i++) {
				if (this.mItems.get(i).getItemId() == itemId) {
					this.mItems.remove(i);
					return;
				}
			}
			throw new IndexOutOfBoundsException("No item with id " + itemId);
		}

		@Override
		public int size() {
			return this.mItems.size();
		}
		
		public List<ActionBarMenuItem> getItems() {
			return this.mItems;
		}

		@Override
		public int addIntentOptions(int groupId, int itemId, int order, ComponentName caller, Intent[] specifics, Intent intent, int flags, MenuItem[] outSpecificItems) {
			throw new RuntimeException("Method not supported.");
		}

		@Override
		public SubMenu addSubMenu(CharSequence title) {
			throw new RuntimeException("Method not supported.");
		}

		@Override
		public SubMenu addSubMenu(int titleResourceId) {
			throw new RuntimeException("Method not supported.");
		}

		@Override
		public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
			throw new RuntimeException("Method not supported.");
		}

		@Override
		public SubMenu addSubMenu(int groupId, int itemId, int order, int titleResourceId) {
			throw new RuntimeException("Method not supported.");
		}

		@Override
		public boolean isShortcutKey(int keyCode, KeyEvent event) {
			throw new RuntimeException("Method not supported.");
		}

		@Override
		public boolean performIdentifierAction(int id, int flags) {
			throw new RuntimeException("Method not supported.");
		}

		@Override
		public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
			throw new RuntimeException("Method not supported.");
		}

		@Override
		public void removeGroup(int groupId) {
			throw new RuntimeException("Method not supported.");
		}

		@Override
		public void setGroupCheckable(int groupId, boolean checkable, boolean exclusive) {
			throw new RuntimeException("Method not supported.");
		}

		@Override
		public void setGroupEnabled(int groupId, boolean enabled) {
			throw new RuntimeException("Method not supported.");
		}

		@Override
		public void setGroupVisible(int groupId, boolean visible) {
			throw new RuntimeException("Method not supported.");
		}

		@Override
		public void setQwertyMode(boolean isQwerty) {
			throw new RuntimeException("Method not supported.");
		}
	}
	
	/*
	 * See: com.android.internal.view.menu.MenuItemItmpl
	 */
	public static final class ActionBarMenuItem implements MenuItem {
		private final Context mContext;
		
		private Intent mIntent;
		private int mIconId;
		private int mItemId;
		private int mGroupId;
		private int mOrder;
		private CharSequence mTitle;
		private CharSequence mTitleCondensed;
		private SubMenu mSubMenu;
		private boolean mIsCheckable;
		private boolean mIsChecked;
		private boolean mIsEnabled;
		private boolean mIsVisible;
		private char mNumericalShortcut;
		private char mAlphabeticalShortcut;

		private ActionBarMenuItem(Context context, int itemId, int groupId, int order, int titleResourceId) {
			this(context, itemId, groupId, order, context.getResources().getString(titleResourceId));
		}
		private ActionBarMenuItem(Context context, int itemId, int groupId, int order, CharSequence title) {
			this.mContext = context;
			
			this.mIsCheckable = false;
			this.mIsChecked = false;
			this.mIsEnabled = true;
			this.mIsVisible = true;
			this.mItemId = itemId;
			this.mGroupId = groupId;
			this.mOrder = order;
			this.mTitle = title;
		}
		
		@Override
		public Intent getIntent() {
			return this.mIntent;
		}
		
		public int getIconId() {
			return this.mIconId;
		}

		@Override
		public int getItemId() {
			return this.mItemId;
		}

		@Override
		public CharSequence getTitle() {
			return this.mTitle;
		}

		@Override
		public boolean isEnabled() {
			return this.mIsEnabled;
		}

		@Override
		public boolean isVisible() {
			return this.mIsVisible;
		}

		@Override
		public MenuItem setEnabled(boolean enabled) {
			this.mIsEnabled = enabled;
			return this;
		}

		@Override
		public MenuItem setIcon(int iconResourceId) {
			this.mIconId = iconResourceId;
			return this;
		}

		@Override
		public MenuItem setIntent(Intent intent) {
			this.mIntent = intent;
			return this;
		}

		@Override
		public MenuItem setTitle(CharSequence title) {
			this.mTitle = title;
			return this;
		}

		@Override
		public MenuItem setTitle(int titleResourceId) {
			return this.setTitle(this.mContext.getResources().getString(titleResourceId));
		}

		@Override
		public MenuItem setVisible(boolean visible) {
			this.mIsVisible = visible;
			return this;
		}

		@Override
		public boolean isChecked() {
			return this.mIsChecked;
		}

		@Override
		public MenuItem setChecked(boolean checked) {
			this.mIsChecked = checked;
			return this;
		}

		@Override
		public boolean isCheckable() {
			return this.mIsCheckable;
		}

		@Override
		public MenuItem setCheckable(boolean checkable) {
			this.mIsCheckable = checkable;
			return this;
		}

		@Override
		public CharSequence getTitleCondensed() {
			return this.mTitleCondensed;
		}

		@Override
		public MenuItem setTitleCondensed(CharSequence title) {
			this.mTitleCondensed = title;
			return this;
		}

		@Override
		public int getGroupId() {
			return this.mGroupId;
		}

		@Override
		public int getOrder() {
			return this.mOrder;
		}

		@Override
		public SubMenu getSubMenu() {
			return this.mSubMenu;
		}

		@Override
		public boolean hasSubMenu() {
			return this.mSubMenu != null;
		}

		@Override
		public char getAlphabeticShortcut() {
			return this.mAlphabeticalShortcut;
		}

		@Override
		public char getNumericShortcut() {
			return this.mNumericalShortcut;
		}

		@Override
		public MenuItem setAlphabeticShortcut(char alphaChar) {
			this.mAlphabeticalShortcut = Character.toLowerCase(alphaChar);
			return this;
		}

		@Override
		public MenuItem setNumericShortcut(char numericChar) {
			this.mNumericalShortcut = numericChar;
			return this;
		}

		@Override
		public MenuItem setShortcut(char numericChar, char alphaChar) {
			return this.setNumericShortcut(numericChar).setAlphabeticShortcut(alphaChar);
		}
		
		
		@Override
		public View getActionView() {
			throw new RuntimeException("Method not supported.");
		}

		@Override
		public Drawable getIcon() {
			throw new RuntimeException("Method not supported.");
		}

		@Override
		public ContextMenuInfo getMenuInfo() {
			throw new RuntimeException("Method not supported.");
		}

		@Override
		public MenuItem setActionView(View view) {
			throw new RuntimeException("Method not supported.");
		}

		@Override
		public MenuItem setActionView(int resId) {
			throw new RuntimeException("Method not supported.");
		}

		@Override
		public MenuItem setIcon(Drawable icon) {
			throw new RuntimeException("Method not supported.");
		}

		@Override
		public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
			throw new RuntimeException("Method not supported.");
		}

		@Override
		public void setShowAsAction(int actionEnum) {
			throw new RuntimeException("Method not supported.");
		}
	}
}
