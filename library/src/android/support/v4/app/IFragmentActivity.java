/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (C) 2011 Jake Wharton <jakewharton@gmail.com>
 * Copyright (C) 2011 Felix Bechstein <f@ub0r.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package android.support.v4.app;

import java.io.FileDescriptor;
import java.io.PrintWriter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ActionMode;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.Window;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.actionbarsherlock.internal.view.menu.MenuItemImpl;

/**
 * {@link IFragmentActivity} is holding all methods of {@link FragmentActivity}.
 * This way you can reference the interface in stead of the class in all the other classes.
 * 
 * Copy {@link FragmentActivity}'s code to your custom Activity like FragementMapActivity.
 * 
 * @author Felix Bechstein <f@ub0r.de>
 */
public interface IFragmentActivity {
	// ------------------------------------------------------------------------
	// Getter / Setter
	// ------------------------------------------------------------------------
	public Handler getHandler();
	
	public FragmentManagerImpl getFragments();
	
	// ------------------------------------------------------------------------
	// Prior hidden methods
	// ------------------------------------------------------------------------
	public void ensureSupportActionBarAttached();
	public void invalidateSupportFragmentIndex(int index);
	public LoaderManagerImpl getLoaderManager(int index, boolean started, boolean create);
	
	// ------------------------------------------------------------------------
	// HOOKS INTO ACTIVITY
	// ------------------------------------------------------------------------

	/**
	 * Enable extended window features.
	 * 
	 * @param featureId
	 *            The desired feature as defined in
	 *            {@link android.support.v4.view.Window}.
	 * @return Returns {@code true} if the requested feature is supported and
	 *         now enabled.
	 */
	public boolean requestWindowFeature(long featureId);

	public android.view.MenuInflater getMenuInflater();

	public void setContentView(int layoutResId);

	public void setContentView(View view, LayoutParams params);

	public void setContentView(View view);

	public void setTitle(CharSequence title);

	public void setTitle(int titleId);

	/**
	 * Take care of popping the fragment back stack or finishing the activity as
	 * appropriate.
	 */
	public void onBackPressed();

	/**
	 * Dispatch configuration change to all fragments.
	 */
	public void onConfigurationChanged(Configuration newConfig);

	/**
	 * <p>
	 * Initialize the contents of the Activity's standard options menu. You
	 * should place your menu items in to menu.
	 * </p>
	 * <p>
	 * The default implementation populates the menu with standard system menu
	 * items. These are placed in the {@link Menu.CATEGORY_SYSTEM} group so that
	 * they will be correctly ordered with application-defined menu items.
	 * Deriving classes should always call through to the base implementation.
	 * </p>
	 * <p>
	 * You can safely hold on to menu (and any items created from it), making
	 * modifications to it as desired, until the next time {@code
	 * onCreateOptionsMenu()} is called.
	 * </p>
	 * <p>
	 * When you add items to the menu, you can implement the Activity's
	 * {@link #onOptionsItemSelected(MenuItem)} method to handle them there.
	 * </p>
	 * 
	 * @param menu
	 *            The options menu in which you place your items.
	 * @return You must return true for the menu to be displayed; if you return
	 *         false it will not be shown.
	 */
	public boolean onCreateOptionsMenu(Menu menu);

	public boolean onCreateOptionsMenu(android.view.Menu menu);

	/**
	 * Add support for inflating the &lt;fragment> tag.
	 */
	public View onCreateView(String name, Context context, AttributeSet attrs);

	public void invalidateOptionsMenu();

	/**
	 * Take care of calling onBackPressed() for pre-Eclair platforms.
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event);

	/**
	 * Dispatch onLowMemory() to all fragments.
	 */
	public void onLowMemory();

	/**
	 * Dispatch context and options menu to fragments.
	 */
	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item);

	public boolean onMenuItemSelected(int featureId, MenuItem item);

	public boolean onOptionsItemSelected(MenuItem item);

	public boolean onOptionsItemSelected(android.view.MenuItem item);

	/**
	 * Call onOptionsMenuClosed() on fragments.
	 */
	public void onPanelClosed(int featureId, android.view.Menu menu);

	public boolean onPrepareOptionsMenu(Menu menu);

	public boolean onPrepareOptionsMenu(android.view.Menu menu);

	/**
	 * Cause this Activity to be recreated with a new instance. This results in
	 * essentially the same flow as when the Activity is created due to a
	 * configuration change -- the current instance will go through its
	 * lifecycle to onDestroy() and a new instance then created after it.
	 */
	public void recreate();

	/**
	 * Retain all appropriate fragment and loader state. You can NOT override
	 * this yourself!
	 */
	public Object onRetainNonConfigurationInstance();

	/**
	 * <p>
	 * Sets the visibility of the indeterminate progress bar in the title.
	 * </p>
	 * <p>
	 * In order for the progress bar to be shown, the feature must be requested
	 * via {@link #requestWindowFeature(long)}.
	 * </p>
	 * <p>
	 * <strong>This method must be used instead of
	 * {@link #setProgressBarIndeterminateVisibility(boolean)} for
	 * ActionBarSherlock.</strong> Pass {@link Boolean.TRUE} or
	 * {@link Boolean.FALSE} to ensure the appropriate one is called.
	 * </p>
	 * 
	 * @param visible
	 *            Whether to show the progress bars in the title.
	 */
	public void setProgressBarIndeterminateVisibility(Boolean visible);

	// ------------------------------------------------------------------------
	// NEW METHODS
	// ------------------------------------------------------------------------

	/**
	 * Print the Activity's state into the given stream. This gets invoked if
	 * you run "adb shell dumpsys activity <activity_component_name>".
	 * 
	 * @param prefix
	 *            Desired prefix to prepend at each line of output.
	 * @param fd
	 *            The raw file descriptor that the dump is being sent to.
	 * @param writer
	 *            The PrintWriter to which you should dump your state. This will
	 *            be closed for you after you return.
	 * @param args
	 *            additional arguments to the dump request.
	 */
	public void dump(String prefix, FileDescriptor fd, PrintWriter writer,
			String[] args);

	// void doReallyStop(boolean retaining);

	/**
	 * Pre-HC, we didn't have a way to determine whether an activity was being
	 * stopped for a config change or not until we saw
	 * onRetainNonConfigurationInstance() called after onStop(). However we need
	 * to know this, to know whether to retain fragments. This will tell us what
	 * we need to know.
	 */
	// void onReallyStop(boolean retaining);

	// ------------------------------------------------------------------------
	// ACTION BAR AND ACTION MODE SUPPORT
	// ------------------------------------------------------------------------

	/**
	 * Retrieve a reference to this activity's action bar handler.
	 * 
	 * @return The handler for the appropriate action bar, or null.
	 */
	public ActionBar getSupportActionBar();

	/**
	 * Notifies the activity that an action mode has finished. Activity
	 * subclasses overriding this method should call the superclass
	 * implementation.
	 * 
	 * @param mode
	 *            The action mode that just finished.
	 */
	public void onActionModeFinished(ActionMode mode);

	/**
	 * Notifies the Activity that an action mode has been started. Activity
	 * subclasses overriding this method should call the superclass
	 * implementation.
	 * 
	 * @param mode
	 *            The new action mode.
	 */
	public void onActionModeStarted(ActionMode mode);

	/**
	 * <p>
	 * Give the Activity a chance to control the UI for an action mode requested
	 * by the system.
	 * </p>
	 * <p>
	 * Note: If you are looking for a notification callback that an action mode
	 * has been started for this activity, see
	 * {@link #onActionModeStarted(ActionMode)}.
	 * </p>
	 * 
	 * @param callback
	 *            The callback that should control the new action mode
	 * @return The new action mode, or null if the activity does not want to
	 *         provide special handling for this action mode. (It will be
	 *         handled by the system.)
	 */
	public ActionMode onWindowStartingActionMode(ActionMode.Callback callback);

	/**
	 * Start an action mode.
	 * 
	 * @param callback
	 *            Callback that will manage lifecycle events for this context
	 *            mode
	 * @return The ContextMode that was started, or null if it was cancelled
	 * @see android.support.v4.view.ActionMode
	 */
	public ActionMode startActionMode(final ActionMode.Callback callback);

	/**
	 * Get a special instance of {@link MenuItemImpl} which denotes the home
	 * item and should be invoked when the custom home button is clicked.
	 * 
	 * @return Menu item instance.
	 */
	public MenuItemImpl getHomeMenuItem();

	// ------------------------------------------------------------------------
	// FRAGMENT SUPPORT
	// ------------------------------------------------------------------------

	/**
	 * Called when a fragment is attached to the activity.
	 */
	public void onAttachFragment(Fragment fragment);

	/**
	 * Return the FragmentManager for interacting with fragments associated with
	 * this activity.
	 */
	public FragmentManager getSupportFragmentManager();

	/**
	 * Modifies the standard behavior to allow results to be delivered to
	 * fragments. This imposes a restriction that requestCode be <= 0xffff.
	 */
	public void startActivityForResult(Intent intent, int requestCode);

	/**
	 * Called by Fragment.startActivityForResult() to implement its behavior.
	 */
	public void startActivityFromFragment(Fragment fragment, Intent intent,
			int requestCode);

	// void invalidateSupportFragmentIndex(int index);

	// ------------------------------------------------------------------------
	// LOADER SUPPORT
	// ------------------------------------------------------------------------

	/**
	 * Return the LoaderManager for this fragment, creating it if needed.
	 */
	public LoaderManager getSupportLoaderManager();
}
