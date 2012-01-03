package com.actionbarsherlock;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import java.util.ArrayList;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyCharacterMap;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.internal.app.ActionBarImpl;
import com.actionbarsherlock.internal.view.StandaloneActionMode;
import com.actionbarsherlock.internal.view.menu.ActionMenuPresenter;
import com.actionbarsherlock.internal.view.menu.MenuBuilder;
import com.actionbarsherlock.internal.view.menu.MenuItemImpl;
import com.actionbarsherlock.internal.view.menu.MenuPresenter;
import com.actionbarsherlock.internal.widget.ActionBarContainer;
import com.actionbarsherlock.internal.widget.ActionBarContextView;
import com.actionbarsherlock.internal.widget.ActionBarView;
import com.actionbarsherlock.internal.widget.IcsProgressBar;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * <p>Helper for implementing the action bar design pattern across all versions
 * of Android.</p>
 *
 * <p>This class will manage interaction with a custom action bar based on the
 * Android 4.0 source code. The exposed API mirrors that of its native
 * counterpart and you should refer to its documentation for instruction.</p>
 *
 * @author Jake Wharton <jakewharton@gmail.com>
 * @version 4.0.0
 */
public final class ActionBarSherlock {
    private static final String TAG = "ActionBarSherlock";
    private static final boolean DEBUG = true;

    /** Window features which are enabled by default. */
    protected static final int DEFAULT_FEATURES = (1 << Window.FEATURE_ACTION_BAR);


    /** Activity interface for menu creation callback. */
    public interface OnCreatePanelMenuListener {
        public boolean onCreatePanelMenu(int featureId, Menu menu);
    }
    /** Activity interface for menu item selection callback. */
    public interface OnMenuItemSelectedListener {
        public boolean onMenuItemSelected(int featureId, MenuItem item);
    }
    /** Activity interface for menu preparation callback. */
    public interface OnPreparePanelListener {
        public boolean onPreparePanel(int featureId, View view, Menu menu);
    }
    /** Activity interface for action mode finished callback. */
    public interface OnActionModeFinishedListener {
        public void onActionModeFinished(ActionMode mode);
    }
    /** Activity interface for action mode started callback. */
    public interface OnActionModeStartedListener {
        public void onActionModeStarted(ActionMode mode);
    }



    /**
     * Wrap an existing activity with a custom action bar implementation.
     *
     * @param activity Activity to wrap.
     * @return Instance to interact with the action bar.
     */
    public static ActionBarSherlock wrap(Activity activity) {
        return new ActionBarSherlock(activity, false);
    }

    /**
     * Act as a delegate for another class which is providing the services
     * of an action bar along with its normal responsibility.
     *
     * @param activity Owning activity.
     * @return Instance to interact with the action bar.
     */
    public static ActionBarSherlock asDelegateFor(Activity activity) {
        return new ActionBarSherlock(activity, true);
    }



    /** Activity which is displaying the action bar. Also used for context. */
    private final Activity mActivity;
    /** Whether delegating actions for the activity or managing ourselves. */
    private final boolean mIsDelegate;

    /** Whether or not the device has a dedicated menu key button. */
    private boolean mReserveOverflow;
    /** Lazy-load indicator for {@link #mReserveOverflow}. */
    private boolean mReserveOverflowSet = false;

    /** Parent view of the window decoration (action bar, mode, etc.). */
    private ViewGroup mDecor;
    /** Parent view of the activity content. */
    private ViewGroup mContentParent;

    /** Implementation which backs the action bar interface API. */
    private ActionBarImpl mActionBar;
    /** Main action bar view which displays the core content. */
    private ActionBarView mActionBarView;
    /** Relevant window and action bar features flags. */
    private int mFeatures = DEFAULT_FEATURES;
    /** Relevant user interface option flags. */
    private int mUiOptions = 0;

    /** Decor indeterminate progress indicator. */
    private IcsProgressBar mCircularProgressBar;
    /** Decor progress indicator. */
    private IcsProgressBar mHorizontalProgressBar;

    /** Current displayed context action bar, if any. */
    private ActionMode mActionMode;
    /** Parent view in which the context action bar is displayed. */
    private ActionBarContextView mActionModeView;

    /** Whether or not the title is stable and can be displayed. */
    private boolean mIsTitleReady = false;

    /** Reference to our custom menu inflater which supports action items. */
    private MenuInflater mMenuInflater;
    /** Current menu instance for managing action items. */
    private MenuBuilder mMenu;
    /** Map between native options items and sherlock items (pre-3.0 only). */
    private HashMap<android.view.MenuItem, MenuItemImpl> mNativeItemMap;
    /** Result of the last dispatch of menu creation. */
    private boolean mLastCreateResult;
    /** Result of the last dispatch of menu preparation. */
    private boolean mLastPrepareResult;


    /** Action bar menu-related callbacks. */
    private final MenuPresenter.Callback mMenuPresenterCallback = new MenuPresenter.Callback() {
        @Override
        public boolean onOpenSubMenu(MenuBuilder subMenu) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
            // TODO Auto-generated method stub
        }
    };

    /** Menu callbacks triggered with actions on our items. */
    private final MenuBuilder.Callback mMenuBuilderCallback = new MenuBuilder.Callback() {
        @Override
        public void onMenuModeChange(MenuBuilder menu) {
            // TODO Auto-generated method stub
        }

        @Override
        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
            return dispatchOptionsItemSelected(item);
        }
    };

    /** Native menu item callback which proxies to our callback. */
    private final android.view.MenuItem.OnMenuItemClickListener mNativeItemListener = new android.view.MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(android.view.MenuItem item) {
            if (DEBUG) Log.d(TAG, "[mNativeItemListener.onMenuItemClick] item: " + item);

            final MenuItemImpl sherlockItem = mNativeItemMap.get(item);
            if (sherlockItem != null) {
                sherlockItem.invoke();
            } else {
                Log.e(TAG, "Options item \"" + item + "\" not found in mapping");
            }

            return true; //Do not allow continuation of native handling
        }
    };

    /** Window callback for the home action item. */
    private final com.actionbarsherlock.view.Window.Callback mWindowCallback = new com.actionbarsherlock.view.Window.Callback() {
        @Override
        public boolean onMenuItemSelected(int featureId, MenuItem item) {
            return dispatchOptionsItemSelected(item);
        }
    };



    private ActionBarSherlock(Activity activity, boolean isDelegateOnly) {
        if (DEBUG) Log.d(TAG, "[<ctor>] activity: " + activity + ", isDelegateOnly: " + isDelegateOnly);

        mActivity = activity;
        mIsDelegate = isDelegateOnly;
    }


    /**
     * Determine whether or not the device has a dedicated menu key.
     *
     * @return {@code true} if native menu key is present.
     */
    private boolean isReservingOverflow() {
        if (!mReserveOverflowSet) {
            mReserveOverflow = ActionMenuPresenter.reserveOverflow(mActivity);
            mReserveOverflowSet = true;
        }
        return mReserveOverflow;
    }

    /**
     * Get the current action bar instance.
     *
     * @return Action bar instance.
     */
    public ActionBar getActionBar() {
        if (DEBUG) Log.d(TAG, "[getActionBar]");

        initActionBar();
        return mActionBar;
    }

    private void initActionBar() {
        if (DEBUG) Log.d(TAG, "[initActionBar]");

        // Initializing the window decor can change window feature flags.
        // Make sure that we have the correct set before performing the test below.
        if (mDecor == null) {
            installDecor();
        }

        if ((mActionBar != null) || !hasFeature(Window.FEATURE_ACTION_BAR) || mActivity.isChild()) {
            return;
        }

        mActionBar = new ActionBarImpl(mActivity, mFeatures);

        if (!mIsDelegate) {
            //We may never get another chance to set the title
            mActionBar.setTitle(mActivity.getTitle());
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle and interaction callbacks when delegating
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Notify action bar of a configuration change event. Should be dispatched
     * after the call to the superclass implementation.
     *
     * <blockquote><pre>
     * @Override
     * public void onConfigurationChanged(Configuration newConfig) {
     *     super.onConfigurationChanged(newConfig);
     *     mSherlock.dispatchConfigurationChanged(newConfig);
     * }
     * </pre></blockquote>
     *
     * @param newConfig The new device configuration.
     */
    public void dispatchConfigurationChanged(Configuration newConfig) {
        if (DEBUG) Log.d(TAG, "[dispatchConfigurationChanged] newConfig: " + newConfig);

        if (mActionBar != null) {
            mActionBar.onConfigurationChanged(newConfig);
        }
    }

    /**
     * Notify the action bar that the activity has finished its resume process.
     * Should be dispatched after the class to the superclass implementation.
     *
     * <blockquote><pre>
     * @Override
     * protected void onPostResume() {
     *     super.onPostResume();
     *     mSherlock.dispatchPostResume();
     * }
     * </pre></blockquote>
     */
    public void dispatchPostResume() {
        if (DEBUG) Log.d(TAG, "[dispatchPostResume]");

        if (mActionBar != null) {
            mActionBar.setShowHideAnimationEnabled(true);
        }
    }

    /**
     * Notify the action bar that the activity is stopping. This should be
     * called before the superclass implementation.
     *
     * <blockquote><p>
     * @Override
     * protected void onStop() {
     *     mSherlock.dispatchStop();
     *     super.onStop();
     * }
     * </p></blockquote>
     */
    public void dispatchStop() {
        if (DEBUG) Log.d(TAG, "[dispatchStop]");

        if (mActionBar != null) {
            mActionBar.setShowHideAnimationEnabled(false);
        }
    }

    /**
     * Indicate that the menu should be recreated by calling
     * {@link OnCreateOptionsMenuListener#onCreateOptionsMenu(com.actionbarsherlock.view.Menu)}.
     */
    public void dispatchInvalidateOptionsMenu() {
        if (DEBUG) Log.d(TAG, "[dispatchInvalidateOptionsMenu]");

        if (mMenu == null) {
            Context context = mActivity;
            if (mActionBar != null) {
                TypedValue outValue = new TypedValue();
                mActivity.getTheme().resolveAttribute(R.attr.actionBarWidgetTheme, outValue, true);
                if (outValue.resourceId != 0) {
                    //We are unable to test if this is the same as our current theme
                    //so we just wrap it and hope that if the attribute was specified
                    //then the user is intentionally specifying an alternate theme.
                    context = new ContextThemeWrapper(context, outValue.resourceId);
                }
            }
            mMenu = new MenuBuilder(context);
            mMenu.setCallback(mMenuBuilderCallback);
        }

        mMenu.stopDispatchingItemsChanged();
        mMenu.clear();

        if (!dispatchCreateOptionsMenu()) {
            if (mActionBar != null) {
                mActionBar.setMenu(null, mMenuPresenterCallback);
            }
            return;
        }

        if (!dispatchPrepareOptionsMenu()) {
            if (mActionBar != null) {
                mActionBar.setMenu(null, mMenuPresenterCallback);
            }
            mMenu.startDispatchingItemsChanged();
            return;
        }

        //TODO figure out KeyEvent? See PhoneWindow#preparePanel
        KeyCharacterMap kmap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);
        mMenu.setQwertyMode(kmap.getKeyboardType() != KeyCharacterMap.NUMERIC);
        mMenu.startDispatchingItemsChanged();

        mActionBar.setMenu(mMenu, mMenuPresenterCallback);
    }

    /**
     * Notify the action bar that it should display its overflow menu if it is
     * appropriate for the device. The implementation should conditionally
     * call the superclass method only if this method returns {@code false}.
     *
     * <blockquote><p>
     * @Override
     * public void openOptionsMenu() {
     *     if (!mSherlock.dispatchOpenOptionsMenu()) {
     *         super.openOptionsMenu();
     *     }
     * }
     * </p></blockquote>
     *
     * @return {@code true} if the opening of the menu was handled internally.
     */
    public boolean dispatchOpenOptionsMenu() {
        if (DEBUG) Log.d(TAG, "[dispatchOpenOptionsMenu]");

        if (!isReservingOverflow()) {
            return false;
        }

        return mActionBarView.showOverflowMenu();
    }

    /**
     * Notify the action bar that it should close its overflow menu if it is
     * appropriate for the device. This implementation should conditionally
     * call the superclass method only if this method returns {@code false}.
     *
     * <blockquote><pre>
     * @Override
     * public void closeOptionsMenu() {
     *     if (!mSherlock.dispatchCloseOptionsMenu()) {
     *         super.closeOptionsMenu();
     *     }
     * }
     * </pre></blockquote>
     *
     * @return {@code true} if the closing of the menu was handled internally.
     */
    public boolean dispatchCloseOptionsMenu() {
        if (DEBUG) Log.d(TAG, "[dispatchCloseOptionsMenu]");

        if (!isReservingOverflow()) {
            return false;
        }

        return mActionBarView.hideOverflowMenu();
    }

    /**
     * Notify the class that the activity has finished its creation. This
     * should be called after the superclass implementation.
     *
     * <blockquote><pre>
     * @Override
     * protected void onPostCreate(Bundle savedInstanceState) {
     *     mSherlock.dispatchPostCreate(savedInstanceState);
     *     super.onPostCreate(savedInstanceState);
     * }
     * </pre></blockquote>
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle
     *                           contains the data it most recently supplied in
     *                           {@link Activity#}onSaveInstanceState(Bundle)}.
     *                           <strong>Note: Otherwise it is null.</strong>
     */
    public void dispatchPostCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.d(TAG, "[dispatchOnPostCreate]");

        if (mIsDelegate) {
            mIsTitleReady = true;
        }
    }

    /**
     * Notify the action bar that the title has changed and the action bar
     * should be updated to reflect the change. This should be called before
     * the superclass implementation.
     *
     * <blockquote><pre>
     *  @Override
     *  protected void onTitleChanged(CharSequence title, int color) {
     *      mSherlock.dispatchTitleChanged(title, color);
     *      super.onTitleChanged(title, color);
     *  }
     * </pre></blockquote>
     *
     * @param title New activity title.
     * @param color New activity color.
     */
    public void dispatchTitleChanged(CharSequence title, int color) {
        if (DEBUG) Log.d(TAG, "[dispatchTitleChanged] title: " + title + ", color: " + color);

        if (mIsDelegate && !mIsTitleReady) {
            return;
        }
        if (mActionBar != null) {
            mActionBar.setTitle(title);
        }
    }

    /**
     * Internal method to trigger the menu creation process.
     *
     * @return {@code true} if menu creation should proceed.
     */
    private boolean dispatchCreateOptionsMenu() {
        if (DEBUG) Log.d(TAG, "[dispatchCreateOptionsMenu]");

        mLastCreateResult = false;
        if (mActivity instanceof OnCreatePanelMenuListener) {
            OnCreatePanelMenuListener listener = (OnCreatePanelMenuListener)mActivity;
            mLastCreateResult = listener.onCreatePanelMenu(Window.FEATURE_OPTIONS_PANEL, mMenu);
        }
        return mLastCreateResult;
    }

    /**
     * Internal method to trigger the menu preparation process.
     *
     * @return {@code true} if menu preparation should proceed.
     */
    private boolean dispatchPrepareOptionsMenu() {
        if (DEBUG) Log.d(TAG, "[dispatchPrepareOptionsMenu]");

        mLastPrepareResult = false;
        if (mActivity instanceof OnPreparePanelListener) {
            OnPreparePanelListener listener = (OnPreparePanelListener)mActivity;
            mLastPrepareResult = listener.onPreparePanel(Window.FEATURE_OPTIONS_PANEL, null, mMenu);
        }
        return mLastPrepareResult;
    }

    /**
     * Notify the action bar that the Activity has triggered a menu preparation
     * which usually means that the user has requested the overflow menu via a
     * hardware menu key. You should return the result of this method call and
     * not call the superclass implementation.
     *
     * <blockquote><p>
     * @Override
     * public final boolean onPrepareOptionsMenu(android.view.Menu menu) {
     *     return mSherlock.dispatchPrepareOptionsMenu(menu);
     * }
     * </p></blockquote>
     *
     * @param menu Activity native menu
     * @return {@code true} if menu display should proceed.
     */
    public boolean dispatchPrepareOptionsMenu(android.view.Menu menu) {
        if (DEBUG) Log.d(TAG, "[dispatchPrepareOptionsMenu] android.view.Menu: " + menu);

        if (isReservingOverflow()) {
            mActionBarView.showOverflowMenu();
            return false;
        }

        if (!dispatchPrepareOptionsMenu()) {
            return false;
        }

        final ArrayList<MenuItemImpl> nonActionItems = mMenu.getNonActionItems();
        if (nonActionItems == null || nonActionItems.size() == 0) {
            return false;
        }

        if (mNativeItemMap == null) {
            mNativeItemMap = new HashMap<android.view.MenuItem, MenuItemImpl>();
        } else {
            mNativeItemMap.clear();
        }

        menu.clear();
        boolean visible = false;
        for (MenuItemImpl nonActionItem : nonActionItems) {
            if (nonActionItem.isVisible()) {
                visible = true;
                //TODO move this binding "inward" to internal so we have access to more raw data
                android.view.MenuItem nativeItem = menu.add(nonActionItem.getGroupId(), nonActionItem.getItemId(),
                        nonActionItem.getOrder(), nonActionItem.getTitle());
                nativeItem.setIcon(nonActionItem.getIcon());
                nativeItem.setOnMenuItemClickListener(mNativeItemListener);

                mNativeItemMap.put(nativeItem, nonActionItem);
            }
        }

        return visible;
    }

    /**
     * Internal method for dispatching options menu selection to the owning
     * activity callback.
     *
     * @param item Selected options menu item.
     * @return {@code true} if the item selection was handled in the callback.
     */
    private boolean dispatchOptionsItemSelected(MenuItem item) {
        if (DEBUG) Log.d(TAG, "[dispatchOptionsItemSelected] item: " + item);

        if (mActivity instanceof OnMenuItemSelectedListener) {
            OnMenuItemSelectedListener listener = (OnMenuItemSelectedListener)mActivity;
            return listener.onMenuItemSelected(Window.FEATURE_OPTIONS_PANEL, item);
        }
        return false;
    }

    /**
     * Notify the action bar that the overflow menu has been opened. The
     * implementation should conditionally return {@code true} if this method
     * returns {@code true}, otherwise return the result of the superclass
     * method.
     *
     * <blockquote><p>
     * @Override
     * public final boolean onMenuOpened(int featureId, android.view.Menu menu) {
     *     if (mSherlock.dispatchMenuOpened(featureId, menu)) {
     *         return true;
     *     }
     *     return super.onMenuOpened(featureId, menu);
     * }
     * </p></blockquote>
     *
     * @param featureId Window feature which triggered the event.
     * @param menu Activity native menu.
     * @return {@code true} if the event was handled by this method.
     */
    public boolean dispatchMenuOpened(int featureId, android.view.Menu menu) {
        if (DEBUG) Log.d(TAG, "[dispatchMenuOpened] featureId: " + featureId + ", menu: " + menu);

        if (featureId == Window.FEATURE_ACTION_BAR || featureId == Window.FEATURE_OPTIONS_PANEL) {
            if (mActionBar != null) {
                mActionBar.dispatchMenuVisibilityChanged(true);
            }
            return true;
        }

        return false;
    }

    /**
     * Notify the action bar that the overflow menu has been closed. This
     * method should be called before the superclass implementation.
     *
     * <blockquote><p>
     * @Override
     * public void onPanelClosed(int featureId, android.view.Menu menu) {
     *     mSherlock.dispatchPanelClosed(featureId, menu);
     *     super.onPanelClosed(featureId, menu);
     * }
     * </p></blockquote>
     *
     * @param featureId
     * @param menu
     */
    public void dispatchPanelClosed(int featureId, android.view.Menu menu){
        if (DEBUG) Log.d(TAG, "[dispatchPanelClosed] featureId: " + featureId + ", menu: " + menu);

        if (featureId == Window.FEATURE_ACTION_BAR || featureId == Window.FEATURE_OPTIONS_PANEL) {
            if (mActionBar != null) {
                mActionBar.dispatchMenuVisibilityChanged(false);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////


    /**
     * Return the feature bits that are enabled. This is the set of features
     * that were given to requestFeature(), and are being handled by this
     * Window itself or its container. That is, it is the set of requested
     * features that you can actually use.
     *
     * @return The feature bits.
     */
    public int getFeatures() {
        if (DEBUG) Log.d(TAG, "[getFeatures]");

        return mFeatures;
    }

    /**
     * Query for the availability of a certain feature.
     *
     * @param featureId The feature ID to check.
     * @return {@code true} if feature is enabled, {@code false} otherwise.
     */
    public boolean hasFeature(int featureId) {
        if (DEBUG) Log.d(TAG, "[hasFeature] featureId: " + featureId);

        return (mFeatures & (1 << featureId)) != 0;
    }

    /**
     * Enable extended screen features. This must be called before
     * {@code setContentView()}. May be called as many times as desired as long
     * as it is before {@code setContentView()}. If not called, no extended
     * features will be available. You can not turn off a feature once it is
     * requested.
     *
     * @param featureId The desired features, defined as constants by Window.
     * @return Returns true if the requested feature is supported and now
     * enabled.
     */
    public boolean requestFeature(int featureId) {
        if (DEBUG) Log.d(TAG, "[requestFeature] featureId: " + featureId);

        if (mContentParent != null) {
            throw new AndroidRuntimeException("requestFeature() must be called before adding content");
        }

        switch (featureId) {
            case Window.FEATURE_ACTION_BAR:
            case Window.FEATURE_ACTION_BAR_OVERLAY:
            case Window.FEATURE_ACTION_MODE_OVERLAY:
            case Window.FEATURE_INDETERMINATE_PROGRESS:
            case Window.FEATURE_NO_TITLE:
            case Window.FEATURE_PROGRESS:
                mFeatures |= (1 << featureId);
                return true;

            default:
                return false;
        }
    }

    /**
     * Set extra options that will influence the UI for this window.
     *
     * @param uiOptions Flags specifying extra options for this window.
     */
    public void setUiOptions(int uiOptions) {
        if (DEBUG) Log.d(TAG, "[setUiOptions] uiOptions: " + uiOptions);

        mUiOptions = uiOptions;
    }

    /**
     * Set extra options that will influence the UI for this window. Only the
     * bits filtered by mask will be modified.
     *
     * @param uiOptions Flags specifying extra options for this window.
     * @param mask Flags specifying which options should be modified. Others
     *             will remain unchanged.
     */
    public void setUiOptions(int uiOptions, int mask) {
        if (DEBUG) Log.d(TAG, "[setUiOptions] uiOptions: " + uiOptions + ", mask: " + mask);

        mUiOptions = (mUiOptions & ~mask) | (uiOptions & mask);
    }

    /**
     * Set the content of the activity inside the action bar.
     *
     * @param layoutResId Layout resource ID.
     */
    public void setContentView(int layoutResId) {
        if (DEBUG) Log.d(TAG, "[setContentView] layoutResId: " + layoutResId);

        if (mContentParent == null) {
            installDecor();
        } else {
            mContentParent.removeAllViews();
        }
        mActivity.getLayoutInflater().inflate(layoutResId, mContentParent);

        android.view.Window.Callback callback = mActivity.getWindow().getCallback();
        if (callback != null) {
            callback.onContentChanged();
        }

        initActionBar();
    }

    /**
     * Set the content of the activity inside the action bar.
     *
     * @param view The desired content to display.
     */
    public void setContentView(View view) {
        if (DEBUG) Log.d(TAG, "[setContentView] view: " + view);

        setContentView(view, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
    }

    /**
     * Set the content of the activity inside the action bar.
     *
     * @param view The desired content to display.
     * @param params Layout parameters to apply to the view.
     */
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (DEBUG) Log.d(TAG, "[setContentView] view: " + view + ", params: " + params);

        if (mContentParent == null) {
            installDecor();
        } else {
            mContentParent.removeAllViews();
        }
        mContentParent.addView(view, params);

        android.view.Window.Callback callback = mActivity.getWindow().getCallback();
        if (callback != null) {
            callback.onContentChanged();
        }

        initActionBar();
    }

    /**
     * Variation on {@link #setContentView(android.view.View, android.view.ViewGroup.LayoutParams)}
     * to add an additional content view to the screen. Added after any
     * existing ones on the screen -- existing views are NOT removed.
     *
     * @param view The desired content to display.
     * @param params Layout parameters for the view.
     */
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        if (DEBUG) Log.d(TAG, "[addContentView] view: " + view + ", params: " + params);

        if (mContentParent == null) {
            installDecor();
        }
        mContentParent.addView(view, params);

        initActionBar();
    }

    private void installDecor() {
        if (DEBUG) Log.d(TAG, "[installDecor]");

        if (mDecor == null) {
            mDecor = (ViewGroup)mActivity.getWindow().getDecorView().findViewById(android.R.id.content);
        }
        if (mContentParent == null) {
            mContentParent = generateLayout();
            mActionBarView = (ActionBarView)mDecor.findViewById(R.id.abs__action_bar);
            if (mActionBarView != null) {
                mActionBarView.setWindowCallback(mWindowCallback);
                if (mActionBarView.getTitle() == null) {
                    mActionBarView.setWindowTitle(mActivity.getTitle());
                }
                if (hasFeature(Window.FEATURE_PROGRESS)) {
                    mActionBarView.initProgress();
                }
                if (hasFeature(Window.FEATURE_INDETERMINATE_PROGRESS)) {
                    mActionBarView.initIndeterminateProgress();
                }

                //Since we don't require onCreate dispatching, parse for uiOptions here
                mUiOptions = loadUiOptionsFromManifest(mActivity);

                boolean splitActionBar = false;
                final boolean splitWhenNarrow = (mUiOptions & ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW) != 0;
                if (splitWhenNarrow) {
                    splitActionBar = mActivity.getResources().getBoolean(R.bool.abs__split_action_bar_is_narrow);
                } else {
                    splitActionBar = mActivity.getTheme()
                            .obtainStyledAttributes(R.styleable.SherlockTheme)
                            .getBoolean(R.styleable.SherlockTheme_windowSplitActionBar, false);
                }
                final ActionBarContainer splitView = (ActionBarContainer)mDecor.findViewById(R.id.abs__split_action_bar);
                if (splitView != null) {
                    mActionBarView.setSplitView(splitView);
                    mActionBarView.setSplitActionBar(splitActionBar);
                    mActionBarView.setSplitWhenNarrow(splitWhenNarrow);

                    mActionModeView = (ActionBarContextView)mDecor.findViewById(R.id.abs__action_context_bar);
                    mActionModeView.setSplitView(splitView);
                    mActionModeView.setSplitActionBar(splitActionBar);
                    mActionModeView.setSplitWhenNarrow(splitWhenNarrow);
                } else if (splitActionBar) {
                    Log.e(TAG, "Requested split action bar with incompatible window decor! Ignoring request.");
                }

                // Post the panel invalidate for later; avoid application onCreateOptionsMenu
                // being called in the middle of onCreate or similar.
                mDecor.post(new Runnable() {
                    @Override
                    public void run() {
                        //Invalidate if the panel menu hasn't been created before this.
                        if (mMenu == null) {
                            dispatchInvalidateOptionsMenu();
                        }
                    }
                });
            }
        }
    }

    private static int loadUiOptionsFromManifest(Activity activity) {
        int uiOptions = 0;
        try {
            final String thisPackage = activity.getClass().getName();
            if (DEBUG) Log.i(TAG, "Parsing AndroidManifest.xml for " + thisPackage);

            final String packageName = activity.getApplicationInfo().packageName;
            final AssetManager am = activity.createPackageContext(packageName, 0).getAssets();
            final XmlResourceParser xml = am.openXmlResourceParser("AndroidManifest.xml");

            int eventType = xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String name = xml.getName();

                    if ("application".equals(name)) {
                        //Check if the <application> has the attribute
                        if (DEBUG) Log.d(TAG, "Got <application>");

                        for (int i = xml.getAttributeCount() - 1; i >= 0; i--) {
                            if (DEBUG) Log.d(TAG, xml.getAttributeName(i) + ": " + xml.getAttributeValue(i));

                            if ("uiOptions".equals(xml.getAttributeName(i))) {
                                uiOptions = xml.getAttributeIntValue(i, 0);
                                break; //out of for loop
                            }
                        }
                    } else if ("activity".equals(name)) {
                        //Check if the <activity> is us and has the attribute
                        if (DEBUG) Log.d(TAG, "Got <activity>");
                        Integer activityUiOptions = null;
                        String activityPackage = null;
                        boolean isOurActivity = false;

                        for (int i = xml.getAttributeCount() - 1; i >= 0; i--) {
                            if (DEBUG) Log.d(TAG, xml.getAttributeName(i) + ": " + xml.getAttributeValue(i));

                            //We need both uiOptions and name attributes
                            String attrName = xml.getAttributeName(i);
                            if ("uiOptions".equals(attrName)) {
                                activityUiOptions = xml.getAttributeIntValue(i, 0);
                            } else if ("name".equals(attrName)) {
                                activityPackage = xml.getAttributeValue(i);
                                //Handle FQCN or relative
                                if (!activityPackage.startsWith(packageName) && activityPackage.startsWith(".")) {
                                    activityPackage = packageName + activityPackage;
                                }
                                if (!thisPackage.equals(activityPackage)) {
                                    break; //out of for loop
                                }
                                isOurActivity = true;
                            }

                            //Make sure we have both attributes before processing
                            if ((activityUiOptions != null) && (activityPackage != null)) {
                                //Our activity, uiOptions specified, override with our value
                                uiOptions = activityUiOptions.intValue();
                            }
                        }
                        if (isOurActivity) {
                            //If we matched our activity but it had no logo don't
                            //do any more processing of the manifest
                            break;
                        }
                    }
                }
                eventType = xml.nextToken();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DEBUG) Log.i(TAG, "Returning " + Integer.toHexString(uiOptions));
        return uiOptions;
    }

    private ViewGroup generateLayout() {
        if (DEBUG) Log.d(TAG, "[generateLayout]");

        // Apply data from current theme.

        TypedArray a = mActivity.getTheme().obtainStyledAttributes(R.styleable.SherlockTheme);

        if (a.getBoolean(R.styleable.SherlockTheme_windowNoTitle, false)) {
            requestFeature(Window.FEATURE_NO_TITLE);
        } else if (a.getBoolean(R.styleable.SherlockTheme_windowActionBar, true)) {
            // Don't allow an action bar if there is no title.
            requestFeature(Window.FEATURE_ACTION_BAR);
        }

        if (a.getBoolean(R.styleable.SherlockTheme_windowActionBarOverlay, false)) {
            requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        }

        if (a.getBoolean(R.styleable.SherlockTheme_windowActionModeOverlay, false)) {
            requestFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        }

        a.recycle();

        int layoutResource;
        if (hasFeature(Window.FEATURE_ACTION_BAR)) {
            if (hasFeature(Window.FEATURE_ACTION_BAR_OVERLAY)) {
                layoutResource = R.layout.abs__screen_action_bar_overlay;
            } else {
                layoutResource = R.layout.abs__screen_action_bar;
            }
        } else if (hasFeature(Window.FEATURE_ACTION_MODE_OVERLAY)) {
            layoutResource = R.layout.abs__screen_simple_overlay_action_mode;
        } else {
            layoutResource = R.layout.abs__screen_simple;
        }

        View in = mActivity.getLayoutInflater().inflate(layoutResource, null);
        mDecor.addView(in, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));

        ViewGroup contentParent = (ViewGroup)mDecor.findViewById(R.id.abs__content);
        if (contentParent == null) {
            throw new RuntimeException("Couldn't find content container view");
        }

        //Make our new child the true content view (for fragments). VERY VOLATILE!
        mDecor.setId(View.NO_ID);
        contentParent.setId(android.R.id.content);

        if (hasFeature(Window.FEATURE_INDETERMINATE_PROGRESS)) {
            IcsProgressBar progress = getCircularProgressBar(false);
            if (progress != null) {
                progress.setIndeterminate(true);
            }
        }

        return contentParent;
    }

    /**
     * Change the title associated with this activity.
     */
    public void setTitle(CharSequence title) {
        if (DEBUG) Log.d(TAG, "[setTitle] title: " + title);

        dispatchTitleChanged(title, 0);
    }

    /**
     * Change the title associated with this activity.
     */
    public void setTitle(int resId) {
        if (DEBUG) Log.d(TAG, "[setTitle] resId: " + resId);

        setTitle(mActivity.getString(resId));
    }

    /**
     * Sets the visibility of the progress bar in the title.
     * <p>
     * In order for the progress bar to be shown, the feature must be requested
     * via {@link #requestWindowFeature(int)}.
     *
     * @param visible Whether to show the progress bars in the title.
     */
    public void setProgressBarVisibility(boolean visible) {
        setFeatureInt(Window.FEATURE_PROGRESS, visible ? Window.PROGRESS_VISIBILITY_ON :
            Window.PROGRESS_VISIBILITY_OFF);
    }

    /**
     * Sets the visibility of the indeterminate progress bar in the title.
     * <p>
     * In order for the progress bar to be shown, the feature must be requested
     * via {@link #requestWindowFeature(int)}.
     *
     * @param visible Whether to show the progress bars in the title.
     */
    public void setProgressBarIndeterminateVisibility(boolean visible) {
        setFeatureInt(Window.FEATURE_INDETERMINATE_PROGRESS,
                visible ? Window.PROGRESS_VISIBILITY_ON : Window.PROGRESS_VISIBILITY_OFF);
    }

    /**
     * Sets whether the horizontal progress bar in the title should be indeterminate (the circular
     * is always indeterminate).
     * <p>
     * In order for the progress bar to be shown, the feature must be requested
     * via {@link #requestWindowFeature(int)}.
     *
     * @param indeterminate Whether the horizontal progress bar should be indeterminate.
     */
    public void setProgressBarIndeterminate(boolean indeterminate) {
        setFeatureInt(Window.FEATURE_PROGRESS,
                indeterminate ? Window.PROGRESS_INDETERMINATE_ON : Window.PROGRESS_INDETERMINATE_OFF);
    }

    /**
     * Sets the progress for the progress bars in the title.
     * <p>
     * In order for the progress bar to be shown, the feature must be requested
     * via {@link #requestWindowFeature(int)}.
     *
     * @param progress The progress for the progress bar. Valid ranges are from
     *            0 to 10000 (both inclusive). If 10000 is given, the progress
     *            bar will be completely filled and will fade out.
     */
    public void setProgress(int progress) {
        setFeatureInt(Window.FEATURE_PROGRESS, progress + Window.PROGRESS_START);
    }

    /**
     * Sets the secondary progress for the progress bar in the title. This
     * progress is drawn between the primary progress (set via
     * {@link #setProgress(int)} and the background. It can be ideal for media
     * scenarios such as showing the buffering progress while the default
     * progress shows the play progress.
     * <p>
     * In order for the progress bar to be shown, the feature must be requested
     * via {@link #requestWindowFeature(int)}.
     *
     * @param secondaryProgress The secondary progress for the progress bar. Valid ranges are from
     *            0 to 10000 (both inclusive).
     */
    public void setSecondaryProgress(int secondaryProgress) {
        setFeatureInt(Window.FEATURE_PROGRESS,
                secondaryProgress + Window.PROGRESS_SECONDARY_START);
    }

    private void setFeatureInt(int featureId, int value) {
        updateInt(featureId, value, false);
    }

    private void updateInt(int featureId, int value, boolean fromResume) {
        // Do nothing if the decor is not yet installed... an update will
        // need to be forced when we eventually become active.
        if (mContentParent == null) {
            return;
        }

        final int featureMask = 1 << featureId;

        if ((getFeatures() & featureMask) == 0 && !fromResume) {
            return;
        }

        onIntChanged(featureId, value);
    }

    /**
     * Called when an int feature changes, for the window to update its
     * graphics.
     *
     * @param featureId The feature being changed.
     * @param value The new integer value.
     */
    private void onIntChanged(int featureId, int value) {
        if (featureId == Window.FEATURE_PROGRESS || featureId == Window.FEATURE_INDETERMINATE_PROGRESS) {
            updateProgressBars(value);
        }
    }

    /**
     * Updates the progress bars that are shown in the title bar.
     *
     * @param value Can be one of {@link Window#PROGRESS_VISIBILITY_ON},
     *            {@link Window#PROGRESS_VISIBILITY_OFF},
     *            {@link Window#PROGRESS_INDETERMINATE_ON},
     *            {@link Window#PROGRESS_INDETERMINATE_OFF}, or a value
     *            starting at {@link Window#PROGRESS_START} through
     *            {@link Window#PROGRESS_END} for setting the default
     *            progress (if {@link Window#PROGRESS_END} is given,
     *            the progress bar widgets in the title will be hidden after an
     *            animation), a value between
     *            {@link Window#PROGRESS_SECONDARY_START} -
     *            {@link Window#PROGRESS_SECONDARY_END} for the
     *            secondary progress (if
     *            {@link Window#PROGRESS_SECONDARY_END} is given, the
     *            progress bar widgets will still be shown with the secondary
     *            progress bar will be completely filled in.)
     */
    private void updateProgressBars(int value) {
        IcsProgressBar circularProgressBar = getCircularProgressBar(true);
        IcsProgressBar horizontalProgressBar = getHorizontalProgressBar(true);

        final int features = mFeatures;//getLocalFeatures();
        if (value == Window.PROGRESS_VISIBILITY_ON) {
            if ((features & (1 << Window.FEATURE_PROGRESS)) != 0) {
                int level = horizontalProgressBar.getProgress();
                int visibility = (horizontalProgressBar.isIndeterminate() || level < 10000) ?
                        View.VISIBLE : View.INVISIBLE;
                horizontalProgressBar.setVisibility(visibility);
            }
            if ((features & (1 << Window.FEATURE_INDETERMINATE_PROGRESS)) != 0) {
                circularProgressBar.setVisibility(View.VISIBLE);
            }
        } else if (value == Window.PROGRESS_VISIBILITY_OFF) {
            if ((features & (1 << Window.FEATURE_PROGRESS)) != 0) {
                horizontalProgressBar.setVisibility(View.GONE);
            }
            if ((features & (1 << Window.FEATURE_INDETERMINATE_PROGRESS)) != 0) {
                circularProgressBar.setVisibility(View.GONE);
            }
        } else if (value == Window.PROGRESS_INDETERMINATE_ON) {
            horizontalProgressBar.setIndeterminate(true);
        } else if (value == Window.PROGRESS_INDETERMINATE_OFF) {
            horizontalProgressBar.setIndeterminate(false);
        } else if (Window.PROGRESS_START <= value && value <= Window.PROGRESS_END) {
            // We want to set the progress value before testing for visibility
            // so that when the progress bar becomes visible again, it has the
            // correct level.
            horizontalProgressBar.setProgress(value - Window.PROGRESS_START);

            if (value < Window.PROGRESS_END) {
                showProgressBars(horizontalProgressBar, circularProgressBar);
            } else {
                hideProgressBars(horizontalProgressBar, circularProgressBar);
            }
        } else if (Window.PROGRESS_SECONDARY_START <= value && value <= Window.PROGRESS_SECONDARY_END) {
            horizontalProgressBar.setSecondaryProgress(value - Window.PROGRESS_SECONDARY_START);

            showProgressBars(horizontalProgressBar, circularProgressBar);
        }
    }

    private void showProgressBars(IcsProgressBar horizontalProgressBar, IcsProgressBar spinnyProgressBar) {
        final int features = mFeatures;//getLocalFeatures();
        if ((features & (1 << Window.FEATURE_INDETERMINATE_PROGRESS)) != 0 &&
                spinnyProgressBar.getVisibility() == View.INVISIBLE) {
            spinnyProgressBar.setVisibility(View.VISIBLE);
        }
        // Only show the progress bars if the primary progress is not complete
        if ((features & (1 << Window.FEATURE_PROGRESS)) != 0 &&
                horizontalProgressBar.getProgress() < 10000) {
            horizontalProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBars(IcsProgressBar horizontalProgressBar, IcsProgressBar spinnyProgressBar) {
        final int features = mFeatures;//getLocalFeatures();
        Animation anim = AnimationUtils.loadAnimation(mActivity, android.R.anim.fade_out);
        anim.setDuration(1000);
        if ((features & (1 << Window.FEATURE_INDETERMINATE_PROGRESS)) != 0 &&
                spinnyProgressBar.getVisibility() == View.VISIBLE) {
            spinnyProgressBar.startAnimation(anim);
            spinnyProgressBar.setVisibility(View.INVISIBLE);
        }
        if ((features & (1 << Window.FEATURE_PROGRESS)) != 0 &&
                horizontalProgressBar.getVisibility() == View.VISIBLE) {
            horizontalProgressBar.startAnimation(anim);
            horizontalProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private IcsProgressBar getCircularProgressBar(boolean shouldInstallDecor) {
        if (mCircularProgressBar != null) {
            return mCircularProgressBar;
        }
        if (mContentParent == null && shouldInstallDecor) {
            installDecor();
        }
        mCircularProgressBar = (IcsProgressBar)mDecor.findViewById(R.id.abs__progress_circular);
        if (mCircularProgressBar != null) {
            mCircularProgressBar.setVisibility(View.INVISIBLE);
        }
        return mCircularProgressBar;
    }

    private IcsProgressBar getHorizontalProgressBar(boolean shouldInstallDecor) {
        if (mHorizontalProgressBar != null) {
            return mHorizontalProgressBar;
        }
        if (mContentParent == null && shouldInstallDecor) {
            installDecor();
        }
        mHorizontalProgressBar = (IcsProgressBar)mDecor.findViewById(R.id.abs__progress_horizontal);
        if (mHorizontalProgressBar != null) {
            mHorizontalProgressBar.setVisibility(View.INVISIBLE);
        }
        return mHorizontalProgressBar;
    }

    /**
     * Get a menu inflater instance which supports the newer menu attributes.
     *
     * @return Menu inflater instance.
     */
    public MenuInflater getMenuInflater() {
        if (DEBUG) Log.d(TAG, "[getMenuInflater]");

        // Make sure that action views can get an appropriate theme.
        if (mMenuInflater == null) {
            initActionBar();
            if (mActionBar != null) {
                mMenuInflater = new MenuInflater(mActionBar.getThemedContext());
            } else {
                mMenuInflater = new MenuInflater(mActivity);
            }
        }
        return mMenuInflater;
    }

    /**
     * Start an action mode.
     *
     * @param callback Callback that will manage lifecycle events for this
     *                 context mode.
     * @return The ContextMode that was started, or null if it was canceled.
     * @see ActionMode
     */
    public ActionMode startActionMode(ActionMode.Callback callback) {
        if (mActionMode != null) {
            mActionMode.finish();
        }

        final ActionMode.Callback wrappedCallback = new ActionModeCallbackWrapper(callback);
        ActionMode mode = null;

        if (mActionModeView == null) {
            ViewStub stub = (ViewStub)mDecor.findViewById(R.id.abs__action_mode_bar_stub);
            if (stub != null) {
                mActionModeView = (ActionBarContextView)stub.inflate();
            }
        }
        if (mActionModeView != null) {
            mActionModeView.killMode();
            mode = new StandaloneActionMode(mActivity, mActionModeView, wrappedCallback, true);
            if (callback.onCreateActionMode(mode, mode.getMenu())) {
                mode.invalidate();
                mActionModeView.initForMode(mode);
                mActionModeView.setVisibility(View.VISIBLE);
                mActionMode = mode;
                mActionModeView.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
            } else {
                mActionMode = null;
            }
        }
        if (mActionMode != null && mActivity instanceof OnActionModeStartedListener) {
            ((OnActionModeStartedListener)mActivity).onActionModeStarted(mActionMode);
        }
        return mActionMode;
    }

    /**
     * Clears out internal reference when the action mode is destroyed.
     */
    private class ActionModeCallbackWrapper implements ActionMode.Callback {
        private final ActionMode.Callback mWrapped;

        public ActionModeCallbackWrapper(ActionMode.Callback wrapped) {
            mWrapped = wrapped;
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return mWrapped.onCreateActionMode(mode, menu);
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return mWrapped.onPrepareActionMode(mode, menu);
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return mWrapped.onActionItemClicked(mode, item);
        }

        public void onDestroyActionMode(ActionMode mode) {
            mWrapped.onDestroyActionMode(mode);
            if (mActionModeView != null) {
                mActionModeView.setVisibility(View.GONE);
                mActionModeView.removeAllViews();
            }
            if (mActivity instanceof OnActionModeFinishedListener) {
                ((OnActionModeFinishedListener)mActivity).onActionModeFinished(mActionMode);
            }
            mActionMode = null;
        }
    }
}
