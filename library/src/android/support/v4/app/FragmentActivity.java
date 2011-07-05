/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (C) 2011 Jake Wharton <jakewharton@gmail.com>
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

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import com.actionbarsherlock.internal.app.ActionBarHandlerNative;
import com.actionbarsherlock.internal.app.ActionBarHandlerWatson;
import com.actionbarsherlock.internal.view.menu.MenuBuilder;
import com.actionbarsherlock.internal.view.menu.MenuInflaterWrapper;
import com.actionbarsherlock.internal.view.menu.MenuItemImpl;
import com.actionbarsherlock.internal.view.menu.MenuItemWrapper;
import com.actionbarsherlock.internal.view.menu.MenuWrapper;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.ActionMode;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuInflater;
import android.support.v4.view.MenuItem;
import android.support.v4.view.Window;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

/**
 * Base class for activities that want to use the support-based ActionBar,
 * Fragment, and Loader APIs.
 *
 * <p>Known limitations:</p>
 * <ul>
 * <li> <p>When using the &lt;fragment> tag, this implementation can not
 * use the parent view's ID as the new fragment's ID.  You must explicitly
 * specify an ID (or tag) in the &lt;fragment>.</p>
 * <li> <p>Prior to Honeycomb (3.0), an activity's state was saved before pausing.
 * Fragments are a significant amount of new state, and dynamic enough that one
 * often wants them to change between pausing and stopping.  These classes
 * throw an exception if you try to change the fragment state after it has been
 * saved, to avoid accidental loss of UI state.  However this is too restrictive
 * prior to Honeycomb, where the state is saved before pausing.  To address this,
 * when running on platforms prior to Honeycomb an exception will not be thrown
 * if you change fragments between the state save and the activity being stopped.
 * This means that is some cases if the activity is restored from its last saved
 * state, this may be a snapshot slightly before what the user last saw.</p>
 * </ul>
 */
public class FragmentActivity extends Activity {
	private static final String TAG = "FragmentActivity";
	private static final boolean DEBUG = false;
	
	private static final String FRAGMENTS_TAG = "android:support:fragments";
	
	private static final boolean IS_HONEYCOMB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

	static final int MSG_REALLY_STOPPED = 1;

	final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_REALLY_STOPPED:
					if (mStopped) {
						doReallyStop(false);
					}
					break;
				default:
					super.handleMessage(msg);
			}
		}
	};
	final FragmentManagerImpl mFragments = new FragmentManagerImpl();
	
	final ActionBar mActionBar;
	final MenuBuilder mActionBarMenu;
	final MenuBuilder.Callback mMenuCallback = new MenuBuilder.Callback() {
		@Override
		public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
			return FragmentActivity.this.onMenuItemSelected(Window.FEATURE_OPTIONS_PANEL, item);
		}
	};
	
	boolean mResumed;
	boolean mStopped;
	boolean mReallyStopped;

	boolean mOptionsMenuInvalidated;
	boolean mOptionsMenuCreateResult;

	boolean mCheckedForLoaderManager;
	boolean mLoadersStarted;
	HCSparseArray<LoaderManagerImpl> mAllLoaderManagers;
	LoaderManagerImpl mLoaderManager;
	
	static final class NonConfigurationInstances {
		Object activity;
		HashMap<String, Object> children;
		ArrayList<Fragment> fragments;
		HCSparseArray<LoaderManagerImpl> loaders;
	}
	
	static class FragmentTag {
		public static final int[] Fragment = {
			0x01010003, 0x010100d0, 0x010100d1
		};
		public static final int Fragment_id = 1;
		public static final int Fragment_name = 0;
		public static final int Fragment_tag = 2;
	}
	

	
	public FragmentActivity() {
		super();
		
		if (DEBUG) Log.d(TAG, "<ctor>(): IS_HONEYCOMB = " + IS_HONEYCOMB);
		
		//Load the appropriate action bar handler and menu
		Class<? extends ActionBar> handler = null;
		if (IS_HONEYCOMB) {
			handler = ActionBarHandlerNative.get();
			
			//No menu, everything should be done natively
			mActionBarMenu = null;
		} else {
			if (ActionBar.HANDLER_CUSTOM != null) {
				handler = ActionBar.HANDLER_CUSTOM;
			}
			
			mActionBarMenu = new MenuBuilder(this);
			mActionBarMenu.setCallback(mMenuCallback);
		}
		
		if (handler != null) {
			try {
				mActionBar = handler.newInstance();
				mActionBar.setActivity(this);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} else {
			mActionBar = null;
		}
		
		if (DEBUG) Log.d(TAG, "<ctor>(): mActionBarMenu = " + mActionBarMenu);
		if (DEBUG) Log.d(TAG, "<ctor>(): mActionBar = " + mActionBar);
	}
	
	// ------------------------------------------------------------------------
	// HOOKS INTO ACTIVITY
	// ------------------------------------------------------------------------
	
	/**
	 * Enable extended window features.
	 * 
	 * @param featureId The desired feature as defined in
	 * {@link android.support.v4.view.Window}.
	 * @return Returns {@code true} if the requested feature is supported and
	 * now enabled.
	 */
	public boolean requestWindowFeature(long featureId) {
		if (!IS_HONEYCOMB && ((ActionBarHandlerWatson)mActionBar).requestWindowFeature(featureId)) {
			return true;
		}
		return super.requestWindowFeature((int)featureId);
	}
	
	@Override
	public android.view.MenuInflater getMenuInflater() {
		if (IS_HONEYCOMB) {
			if (DEBUG) Log.d(TAG, "getMenuInflater(): Wrapping native inflater.");
			
			//Wrap the native inflater so it can unwrap the native menu first
			return new MenuInflaterWrapper(this, super.getMenuInflater());
		}
		
		if (DEBUG) Log.d(TAG, "getMenuInflater(): Returning support inflater.");
		
		//Use our custom menu inflater
		return new MenuInflater(this);
	}
	
	@Override
	public void setContentView(int layoutResId) {
		if ((mActionBar != null) && !IS_HONEYCOMB) {
			((ActionBarHandlerWatson)mActionBar).setContentView(layoutResId);
		} else {
			super.setContentView(layoutResId);
		}
	}
	
	@Override
	public void setContentView(View view, LayoutParams params) {
		if ((mActionBar != null) && !IS_HONEYCOMB) {
			((ActionBarHandlerWatson)mActionBar).setContentView(view, params);
		} else {
			super.setContentView(view, params);
		}
	}
	
	@Override
	public void setContentView(View view) {
		if ((mActionBar != null) && !IS_HONEYCOMB) {
			((ActionBarHandlerWatson)mActionBar).setContentView(view);
		} else {
			super.setContentView(view);
		}
	}
	
	/**
	 * Hook into the superclass's setContentView implementation.
	 * 
	 * @param view Content view.
	 */
	final void setSuperContentView(View view) {
		super.setContentView(view);
	}

	/**
	 * Hook into the superclass's setContentView implementation.
	 * 
	 * @param layoutResId Resource ID of layout.
	 */
	final void setSuperContentView(int layoutResId) {
		super.setContentView(layoutResId);
	}
	
	/**
	 * Dispatch incoming result to the correct fragment.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		int index = requestCode>>16;
		if (index != 0) {
			index--;
			if (mFragments.mActive == null || index < 0 || index >= mFragments.mActive.size()) {
				Log.w(TAG, "Activity result fragment index out of range: 0x"
						+ Integer.toHexString(requestCode));
				return;
			}
			Fragment frag = mFragments.mActive.get(index);
			if (frag == null) {
				Log.w(TAG, "Activity result no fragment exists for index: 0x"
						+ Integer.toHexString(requestCode));
			}
			frag.onActivityResult(requestCode&0xffff, resultCode, data);
			return;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Take care of popping the fragment back stack or finishing the activity
	 * as appropriate.
	 */
	public void onBackPressed() {
		if (!mFragments.popBackStackImmediate()) {
			finish();
		}
	}

	/**
	 * Dispatch configuration change to all fragments.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mFragments.dispatchConfigurationChanged(newConfig);
	}

	/**
	 * Perform initialization of all fragments and loaders.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mFragments.attachActivity(this);
		// Old versions of the platform didn't do this!
		if (getLayoutInflater().getFactory() == null) {
			getLayoutInflater().setFactory(this);
		}
		
		super.onCreate(savedInstanceState);
		if (!IS_HONEYCOMB) {
			((ActionBarHandlerWatson)mActionBar).performAttach();
		}
		
		NonConfigurationInstances nc = (NonConfigurationInstances)
				getLastNonConfigurationInstance();
		if (nc != null) {
			mAllLoaderManagers = nc.loaders;
		}
		if (savedInstanceState != null) {
			Parcelable p = savedInstanceState.getParcelable(FRAGMENTS_TAG);
			mFragments.restoreAllState(p, nc != null ? nc.fragments : null);
		}
		mFragments.dispatchCreate();
		
		if (!IS_HONEYCOMB) {
			//Trigger menu inflation
			invalidateOptionsMenu();
		}
	}
	
	/**
	 * <p>Initialize the contents of the Activity's standard options menu. You
	 * should place your menu items in to menu.</p>
	 * 
	 * <p>The default implementation populates the menu with standard system
	 * menu items. These are placed in the {@link Menu.CATEGORY_SYSTEM} group
	 * so that they will be correctly ordered with application-defined menu
	 * items. Deriving classes should always call through to the base
	 * implementation.</p>
	 * 
	 * <p>You can safely hold on to menu (and any items created from it),
	 * making modifications to it as desired, until the next time
	 * {@code onCreateOptionsMenu()} is called.</p>
	 * 
	 * <p>When you add items to the menu, you can implement the Activity's
	 * {@link #onOptionsItemSelected(MenuItem)} method to handle them
	 * there.</p>
	 * 
	 * @param menu The options menu in which you place your items.
	 * @return You must return true for the menu to be displayed; if you return
	 * false it will not be shown.
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		if (DEBUG) Log.d(TAG, "onCreateOptionsMenu(Menu): Returning " + menu.hasVisibleItems());
		return menu.hasVisibleItems();
	}
	
	@Override
	public final boolean onCreateOptionsMenu(android.view.Menu menu) {
		// Prior to Honeycomb, the framework can't invalidate the options
		// menu, so we must always say we have one in case the app later
		// invalidates it and needs to have it shown.
		boolean result = true;
		
		if (IS_HONEYCOMB) {
			if (DEBUG) Log.d(TAG, "onCreateOptionsMenu(android.view.Menu): Calling support method with wrapped native menu.");
			MenuWrapper wrapped = new MenuWrapper(menu);
			result  = onCreateOptionsMenu(wrapped);
			result |= mFragments.dispatchCreateOptionsMenu(wrapped, getMenuInflater());
		}
		
		if (DEBUG) Log.d(TAG, "onCreateOptionsMenu(android.view.Menu): Returning " + result);
		return result;
	}
	
	/**
	 * Add support for inflating the &lt;fragment> tag.
	 */
	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		if (!"fragment".equals(name)) {
			return super.onCreateView(name, context, attrs);
		}
		
		String fname = attrs.getAttributeValue(null, "class");
		TypedArray a =  context.obtainStyledAttributes(attrs, FragmentTag.Fragment);
		if (fname == null) {
			fname = a.getString(FragmentTag.Fragment_name);
		}
		int id = a.getResourceId(FragmentTag.Fragment_id, View.NO_ID);
		String tag = a.getString(FragmentTag.Fragment_tag);
		a.recycle();
		
		View parent = null; // NOTE: no way to get parent pre-Honeycomb.
		int containerId = parent != null ? parent.getId() : 0;
		if (containerId == View.NO_ID && id == View.NO_ID && tag == null) {
			throw new IllegalArgumentException(attrs.getPositionDescription()
					+ ": Must specify unique android:id, android:tag, or have a parent with an id for " + fname);
		}

		// If we restored from a previous state, we may already have
		// instantiated this fragment from the state and should use
		// that instance instead of making a new one.
		Fragment fragment = id != View.NO_ID ? mFragments.findFragmentById(id) : null;
		if (fragment == null && tag != null) {
			fragment = mFragments.findFragmentByTag(tag);
		}
		if (fragment == null && containerId != View.NO_ID) {
			fragment = mFragments.findFragmentById(containerId);
		}

		if (FragmentManagerImpl.DEBUG) Log.v(TAG, "onCreateView: id=0x"
				+ Integer.toHexString(id) + " fname=" + fname
				+ " existing=" + fragment);
		if (fragment == null) {
			fragment = Fragment.instantiate(this, fname);
			fragment.mFromLayout = true;
			fragment.mFragmentId = id != 0 ? id : containerId;
			fragment.mContainerId = containerId;
			fragment.mTag = tag;
			fragment.mInLayout = true;
			fragment.mImmediateActivity = this;
			fragment.mFragmentManager = mFragments;
			fragment.onInflate(this, attrs, fragment.mSavedFragmentState);
			mFragments.addFragment(fragment, true);

		} else if (fragment.mInLayout) {
			// A fragment already exists and it is not one we restored from
			// previous state.
			throw new IllegalArgumentException(attrs.getPositionDescription()
					+ ": Duplicate id 0x" + Integer.toHexString(id)
					+ ", tag " + tag + ", or parent id 0x" + Integer.toHexString(containerId)
					+ " with another fragment for " + fname);
		} else {
			// This fragment was retained from a previous instance; get it
			// going now.
			fragment.mInLayout = true;
			fragment.mImmediateActivity = this;
			// If this fragment is newly instantiated (either right now, or
			// from last saved state), then give it the attributes to
			// initialize itself.
			if (!fragment.mRetaining) {
				fragment.onInflate(this, attrs, fragment.mSavedFragmentState);
			}
			mFragments.moveToState(fragment);
		}

		if (fragment.mView == null) {
			throw new IllegalStateException("Fragment " + fname
					+ " did not create a view.");
		}
		if (id != 0) {
			fragment.mView.setId(id);
		}
		if (fragment.mView.getTag() == null) {
			fragment.mView.setTag(tag);
		}
		return fragment.mView;
	}

	public void invalidateOptionsMenu() {
		if (DEBUG) Log.d(TAG, "supportInvalidateOptionsMenu(): Invalidating menu.");
		
		if (IS_HONEYCOMB) {
			HoneycombInvalidateOptionsMenu.invoke(this);
		} else {
			mActionBarMenu.clear();
			
			mOptionsMenuCreateResult  = onCreateOptionsMenu(mActionBarMenu);
			mOptionsMenuCreateResult |= mFragments.dispatchCreateOptionsMenu(mActionBarMenu, getMenuInflater());
			
			//Since we now know we are using a custom action bar, perform the
			//inflation callback to allow it to display any items it wants.
			//Any items that were displayed will have a boolean toggled so that we
			//do not display them on the options menu.
			((ActionBarHandlerWatson)mActionBar).onMenuInflated(mActionBarMenu);
			
			// Whoops, older platform...  we'll use a hack, to manually rebuild
			// the options menu the next time it is prepared.
			mOptionsMenuInvalidated = true;
		}
	}
	
	private static final class HoneycombInvalidateOptionsMenu {
		static void invoke(Activity activity) {
			activity.getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
		}
	}
	
	/**
	 * Destroy all fragments and loaders.
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();

		doReallyStop(false);

		mFragments.dispatchDestroy();
		if (mLoaderManager != null) {
			mLoaderManager.doDestroy();
		}
	}

	/**
	 * Take care of calling onBackPressed() for pre-Eclair platforms.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (android.os.Build.VERSION.SDK_INT < 5 /* ECLAIR */
				&& keyCode == KeyEvent.KEYCODE_BACK
				&& event.getRepeatCount() == 0) {
			// Take care of calling this method on earlier versions of
			// the platform where it doesn't exist.
			onBackPressed();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Dispatch onLowMemory() to all fragments.
	 */
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mFragments.dispatchLowMemory();
	}

	/**
	 * Dispatch context and options menu to fragments.
	 */
	@Override
	public final boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
		if (super.onMenuItemSelected(featureId, item)) {
			return true;
		}
		
		switch (featureId) {
			case Window.FEATURE_OPTIONS_PANEL:
				return mFragments.dispatchOptionsItemSelected(new MenuItemWrapper(item));
				
			case Window.FEATURE_CONTEXT_MENU:
				return mFragments.dispatchContextItemSelected(new MenuItemWrapper(item));

			default:
				return false;
		}
	}
	
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (onOptionsItemSelected(item)) {
			return true;
		}
		
		switch (featureId) {
			case Window.FEATURE_OPTIONS_PANEL:
				return mFragments.dispatchOptionsItemSelected(item);
				
			case Window.FEATURE_CONTEXT_MENU:
				return mFragments.dispatchContextItemSelected(item);
			
			default:
				return false;
		}
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public final boolean onOptionsItemSelected(android.view.MenuItem item) {
		return onOptionsItemSelected(new MenuItemWrapper(item));
	}

	/**
	 * Call onOptionsMenuClosed() on fragments.
	 */
	@Override
	public void onPanelClosed(int featureId, android.view.Menu menu) {
		switch (featureId) {
			case Window.FEATURE_OPTIONS_PANEL:
				mFragments.dispatchOptionsMenuClosed(menu);
				
				if (!IS_HONEYCOMB) {
					if (DEBUG) Log.d(TAG, "onPanelClosed(int, android.view.Menu): Dispatch menu visibility false to custom action bar.");
					((ActionBarHandlerWatson)mActionBar).onMenuVisibilityChanged(false);
				}
				break;
		}
		super.onPanelClosed(featureId, menu);
	}
	
	/**
	 * Dispatch onPause() to fragments.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mResumed = false;
		mFragments.dispatchPause();
	}

	/**
	 * Dispatch onActivityCreated() on fragments.
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mFragments.dispatchActivityCreated();
	}

	/**
	 * Dispatch onResume() to fragments.
	 */
	@Override
	protected void onPostResume() {
		super.onPostResume();
		mFragments.dispatchResume();
		mFragments.execPendingActions();
	}
	
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean result = menu.hasVisibleItems();
		if (DEBUG) Log.d(TAG, "onPrepareOptionsMenu(Menu): Returning " + result);
		return result;
	}
	
	@Override
	public final boolean onPrepareOptionsMenu(android.view.Menu menu) {
		boolean result = super.onPrepareOptionsMenu(menu);
		
		if (!IS_HONEYCOMB) {
			if (DEBUG) {
				Log.d(TAG, "onPrepareOptionsMenu(android.view.Menu): mOptionsMenuCreateResult = " + mOptionsMenuCreateResult);
				Log.d(TAG, "onPrepareOptionsMenu(android.view.Menu): mOptionsMenuInvalidated = " + mOptionsMenuInvalidated);
			}
			
			boolean prepareResult = true;
			if (mOptionsMenuCreateResult) {
				if (DEBUG) Log.d(TAG, "onPrepareOptionsMenu(android.view.Menu): Calling support method with custom menu.");
				prepareResult = onPrepareOptionsMenu(mActionBarMenu);
				if (DEBUG) Log.d(TAG, "onPrepareOptionsMenu(android.view.Menu): Support method result returned " + prepareResult);
			}
			
			if (mOptionsMenuInvalidated) {
				if (DEBUG) Log.d(TAG, "onPrepareOptionsMenu(android.view.Menu): Clearing existing options menu.");
				menu.clear();
				mOptionsMenuInvalidated = false;
				
				if (mOptionsMenuCreateResult && prepareResult) {
					if (DEBUG) Log.d(TAG, "onPrepareOptionsMenu(android.view.Menu): Adding any action items that are not displayed on the action bar.");
					//Only add items that have not already been added to our custom
					//action bar implementation
					for (MenuItemImpl item : mActionBarMenu.getItems()) {
						if (!item.isShownOnActionBar()) {
							item.addTo(menu);
						}
					}
				}
			}
			
			if (mOptionsMenuCreateResult && prepareResult && menu.hasVisibleItems()) {
				if (DEBUG) Log.d(TAG, "onPrepareOptionsMenu(android.view.Menu): Dispatch menu visibility true to custom action bar.");
				((ActionBarHandlerWatson)mActionBar).onMenuVisibilityChanged(true);
				result = true;
			}
		} else {
			if (DEBUG) Log.d(TAG, "onPrepareOptionsMenu(android.view.Menu): Calling support method with custom menu.");
			result = onPrepareOptionsMenu(new MenuWrapper(menu));
		}

		if (DEBUG) Log.d(TAG, "onPrepareOptionsMenu(android.view.Menu): Returning " + result);
		return result;
	}
	
	/**
	 * Cause this Activity to be recreated with a new instance. This results in
	 * essentially the same flow as when the Activity is created due to a
	 * configuration change -- the current instance will go through its
	 * lifecycle to onDestroy() and a new instance then created after it. 
	 */
	public void recreate() {
		//XXX This SUCKS! Figure out a way to call the super method and support Android 1.6
		/*
		if (IS_HONEYCOMB) {
			super.recreate();
		} else {
		*/
			final Intent intent = getIntent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			
			finish();
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR) {
				OverridePendingTransition.invoke(this);
			}
			
			startActivity(intent);
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR) {
				OverridePendingTransition.invoke(this);
			}
		/*
		}
		*/
	}
	
	private static final class OverridePendingTransition {
		static void invoke(Activity activity) {
			activity.overridePendingTransition(0, 0);
		}
	}

	/**
	 * Ensure any outstanding fragment transactions have been committed.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mResumed = true;
		mFragments.execPendingActions();
	}

	/**
	 * Retain all appropriate fragment and loader state.  You can NOT
	 * override this yourself!
	 */
	@Override
	public final Object onRetainNonConfigurationInstance() {
		if (mStopped) {
			doReallyStop(true);
		}

		ArrayList<Fragment> fragments = mFragments.retainNonConfig();
		boolean retainLoaders = false;
		if (mAllLoaderManagers != null) {
			// prune out any loader managers that were already stopped and so
			// have nothing useful to retain.
			for (int i=mAllLoaderManagers.size()-1; i>=0; i--) {
				LoaderManagerImpl lm = mAllLoaderManagers.valueAt(i);
				if (lm.mRetaining) {
					retainLoaders = true;
				} else {
					lm.doDestroy();
					mAllLoaderManagers.removeAt(i);
				}
			}
		}
		if (fragments == null && !retainLoaders) {
			return null;
		}
		
		NonConfigurationInstances nci = new NonConfigurationInstances();
		nci.activity = null;
		nci.children = null;
		nci.fragments = fragments;
		nci.loaders = mAllLoaderManagers;
		return nci;
	}

	/**
	 * Save all appropriate fragment state.
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Parcelable p = mFragments.saveAllState();
		if (p != null) {
			outState.putParcelable(FRAGMENTS_TAG, p);
		}
	}

	/**
	 * Dispatch onStart() to all fragments.  Ensure any created loaders are
	 * now started.
	 */
	@Override
	protected void onStart() {
		super.onStart();

		mStopped = false;
		mHandler.removeMessages(MSG_REALLY_STOPPED);

		mFragments.noteStateNotSaved();
		mFragments.execPendingActions();
		
		
		if (!mLoadersStarted) {
			mLoadersStarted = true;
			if (mLoaderManager != null) {
				mLoaderManager.doStart();
			} else if (!mCheckedForLoaderManager) {
				mLoaderManager = getLoaderManager(-1, mLoadersStarted, false);
			}
			mCheckedForLoaderManager = true;
		}
		// NOTE: HC onStart goes here.
		
		mFragments.dispatchStart();
		if (mAllLoaderManagers != null) {
			for (int i=mAllLoaderManagers.size()-1; i>=0; i--) {
				mAllLoaderManagers.valueAt(i).finishRetain();
			}
		}
	}

	/**
	 * Dispatch onStop() to all fragments.  Ensure all loaders are stopped.
	 */
	@Override
	protected void onStop() {
		super.onStop();

		mStopped = true;
		mHandler.sendEmptyMessage(MSG_REALLY_STOPPED);
		
		mFragments.dispatchStop();
	}

	// ------------------------------------------------------------------------
	// NEW METHODS
	// ------------------------------------------------------------------------
	
	/**
	 * Print the Activity's state into the given stream.  This gets invoked if
	 * you run "adb shell dumpsys activity <activity_component_name>".
	 *
	 * @param prefix Desired prefix to prepend at each line of output.
	 * @param fd The raw file descriptor that the dump is being sent to.
	 * @param writer The PrintWriter to which you should dump your state.  This will be
	 * closed for you after you return.
	 * @param args additional arguments to the dump request.
	 */
	public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
		if (IS_HONEYCOMB) {
			//This can only work if we can call the super-class impl. :/
			//ActivityCompatHoneycomb.dump(this, prefix, fd, writer, args);
		}
		writer.print(prefix); writer.print("Local FragmentActivity ");
				writer.print(Integer.toHexString(System.identityHashCode(this)));
				writer.println(" State:");
		String innerPrefix = prefix + "  ";
		writer.print(innerPrefix); writer.print("mResumed=");
				writer.print(mResumed); writer.print(" mStopped=");
				writer.print(mStopped); writer.print(" mReallyStopped=");
				writer.println(mReallyStopped);
		writer.print(innerPrefix); writer.print("mLoadersStarted=");
				writer.println(mLoadersStarted);
		if (mLoaderManager != null) {
			writer.print(prefix); writer.print("Loader Manager ");
					writer.print(Integer.toHexString(System.identityHashCode(mLoaderManager)));
					writer.println(":");
			mLoaderManager.dump(prefix + "  ", fd, writer, args);
		}
		mFragments.dump(prefix, fd, writer, args);
	}

	void doReallyStop(boolean retaining) {
		if (!mReallyStopped) {
			mReallyStopped = true;
			mHandler.removeMessages(MSG_REALLY_STOPPED);
			onReallyStop(retaining);
		}
	}

	/**
	 * Pre-HC, we didn't have a way to determine whether an activity was
	 * being stopped for a config change or not until we saw
	 * onRetainNonConfigurationInstance() called after onStop().  However
	 * we need to know this, to know whether to retain fragments.  This will
	 * tell us what we need to know.
	 */
	void onReallyStop(boolean retaining) {
		if (mLoadersStarted) {
			mLoadersStarted = false;
			if (mLoaderManager != null) {
				if (!retaining) {
					mLoaderManager.doStop();
				} else {
					mLoaderManager.doRetain();
				}
			}
		}

		mFragments.dispatchReallyStop(retaining);
	}
	
	// ------------------------------------------------------------------------
	// ACTION BAR AND ACTION MODE SUPPORT
	// ------------------------------------------------------------------------
	
	/**
	 * Retrieve a reference to this activity's action bar handler.
	 * 
	 * @return The handler for the appropriate action bar, or null.
	 */
	public ActionBar getSupportActionBar() {
		return mActionBar;
	}

	/**
	 * Notifies the activity that an action mode has finished. Activity
	 * subclasses overriding this method should call the superclass
	 * implementation.
	 * 
	 * @param mode The action mode that just finished.
	 */
	public void onActionModeFinished(ActionMode mode) {
	}
	
	/**
	 * Notifies the Activity that an action mode has been started. Activity
	 * subclasses overriding this method should call the superclass
	 * implementation.
	 * 
	 * @param mode The new action mode.
	 */
	public void onActionModeStarted(ActionMode mode) {
	}
	
	/**
	 * <p>Give the Activity a chance to control the UI for an action mode
	 * requested by the system.</p>
	 * 
	 * <p>Note: If you are looking for a notification callback that an action
	 * mode has been started for this activity, see
	 * {@link #onActionModeStarted(ActionMode)}.</p>
	 * 
	 * @param callback The callback that should control the new action mode
	 * @return The new action mode, or null if the activity does not want to
	 * provide special handling for this action mode. (It will be handled by the
	 * system.)
	 */
	public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
		return null;
	}
	
	/**
	 * Start an action mode.
	 * 
	 * @param callback Callback that will manage lifecycle events for this
	 * context mode
	 * @return The ContextMode that was started, or null if it was cancelled
	 * @see android.support.v4.view.ActionMode
	 */
	public final ActionMode startActionMode(final ActionMode.Callback callback) {
		//Give the activity override a chance to handle the action mode
		ActionMode actionMode = onWindowStartingActionMode(callback);
		
		if (actionMode == null) {
			//If the activity did not handle, send to action bar for platform-
			//specific implementation
			actionMode = mActionBar.startActionMode(callback);
		}
		
		//Send the activity callback that our action mode was started
		onActionModeStarted(actionMode);
		
		//Return to the caller
		return actionMode;
	}
	
	/**
	 * Get a special instance of {@link MenuItemImpl} which denotes the home
	 * item and should be invoked when the custom home button is clicked.
	 *  
	 * @return Menu item instance.
	 */
	final MenuItemImpl getHomeMenuItem() {
		return mActionBarMenu.addDetached(android.R.id.home);
	}

	// ------------------------------------------------------------------------
	// FRAGMENT SUPPORT
	// ------------------------------------------------------------------------
	
	/**
	 * Called when a fragment is attached to the activity.
	 */
	public void onAttachFragment(Fragment fragment) {
	}
	
	/**
	 * Return the FragmentManager for interacting with fragments associated
	 * with this activity.
	 */
	public FragmentManager getSupportFragmentManager() {
		return mFragments;
	}

	/**
	 * Modifies the standard behavior to allow results to be delivered to fragments.
	 * This imposes a restriction that requestCode be <= 0xffff.
	 */
	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		if (requestCode != -1 && (requestCode&0xffff0000) != 0) {
			throw new IllegalArgumentException("Can only use lower 16 bits for requestCode");
		}
		super.startActivityForResult(intent, requestCode);
	}

	/**
	 * Called by Fragment.startActivityForResult() to implement its behavior.
	 */
	public void startActivityFromFragment(Fragment fragment, Intent intent, 
			int requestCode) {
		if (requestCode == -1) {
			super.startActivityForResult(intent, -1);
			return;
		}
		if ((requestCode&0xffff0000) != 0) {
			throw new IllegalArgumentException("Can only use lower 16 bits for requestCode");
		}
		super.startActivityForResult(intent, ((fragment.mIndex+1)<<16) + (requestCode&0xffff));
	}
	
	void invalidateSupportFragmentIndex(int index) {
		//Log.v(TAG, "invalidateFragmentIndex: index=" + index);
		if (mAllLoaderManagers != null) {
			LoaderManagerImpl lm = mAllLoaderManagers.get(index);
			if (lm != null) {
				lm.doDestroy();
			}
			mAllLoaderManagers.remove(index);
		}
	}
	
	// ------------------------------------------------------------------------
	// LOADER SUPPORT
	// ------------------------------------------------------------------------
	
	/**
	 * Return the LoaderManager for this fragment, creating it if needed.
	 */
	public LoaderManager getSupportLoaderManager() {
		if (mLoaderManager != null) {
			return mLoaderManager;
		}
		mCheckedForLoaderManager = true;
		mLoaderManager = getLoaderManager(-1, mLoadersStarted, true);
		return mLoaderManager;
	}
	
	LoaderManagerImpl getLoaderManager(int index, boolean started, boolean create) {
		if (mAllLoaderManagers == null) {
			mAllLoaderManagers = new HCSparseArray<LoaderManagerImpl>();
		}
		LoaderManagerImpl lm = mAllLoaderManagers.get(index);
		if (lm == null) {
			if (create) {
				lm = new LoaderManagerImpl(this, started);
				mAllLoaderManagers.put(index, lm);
			}
		} else {
			lm.updateActivity(this);
		}
		return lm;
	}
}
