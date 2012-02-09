package com.actionbarsherlock.internal;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.R;
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
import com.actionbarsherlock.view.MenuItem;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class ActionBarSherlockCompat extends ActionBarSherlock {
    /** Window features which are enabled by default. */
    protected static final int DEFAULT_FEATURES = 0;


    public ActionBarSherlockCompat(Activity activity, int flags) {
        super(activity, flags);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Properties
    ///////////////////////////////////////////////////////////////////////////

    /** Whether or not the device has a dedicated menu key button. */
    private boolean mReserveOverflow;
    /** Lazy-load indicator for {@link #mReserveOverflow}. */
    private boolean mReserveOverflowSet = false;

    /** Current menu instance for managing action items. */
    private MenuBuilder mMenu;
    /** Map between native options items and sherlock items. */
    protected HashMap<android.view.MenuItem, MenuItemImpl> mNativeItemMap;

    /** Parent view of the window decoration (action bar, mode, etc.). */
    private ViewGroup mDecor;
    /** Parent view of the activity content. */
    private ViewGroup mContentParent;

    /** Whether or not the title is stable and can be displayed. */
    private boolean mIsTitleReady = false;

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


    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle and interaction callbacks when delegating
    ///////////////////////////////////////////////////////////////////////////

    /** Window callback for the home action item. */
    private final com.actionbarsherlock.view.Window.Callback mWindowCallback = new com.actionbarsherlock.view.Window.Callback() {
        @Override
        public boolean onMenuItemSelected(int featureId, MenuItem item) {
            return callbackOptionsItemSelected(item);
        }
    };

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

    /** Native menu item callback which proxies to our callback. */
    protected final android.view.MenuItem.OnMenuItemClickListener mNativeItemListener = new android.view.MenuItem.OnMenuItemClickListener() {
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

    /** Menu callbacks triggered with actions on our items. */
    protected final MenuBuilder.Callback mMenuBuilderCallback = new MenuBuilder.Callback() {
        @Override
        public void onMenuModeChange(MenuBuilder menu) {
            reopenMenu(true);
        }

        @Override
        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
            return callbackOptionsItemSelected(item);
        }
    };


    ///////////////////////////////////////////////////////////////////////////
    // Instance methods
    ///////////////////////////////////////////////////////////////////////////

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

    @Override
    public ActionBar getActionBar() {
        if (DEBUG) Log.d(TAG, "[getActionBar]");

        initActionBar();
        return mActionBar;
    }

    protected void initActionBar() {
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
            mActionBarView.setWindowTitle(mActivity.getTitle());
        }
    }

    @Override
    protected Context getThemedContext() {
        return mActionBar.getThemedContext();
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        if (mActionMode != null) {
            mActionMode.finish();
        }

        final ActionMode.Callback wrappedCallback = new ActionModeCallbackWrapper(callback);
        ActionMode mode = null;

        //Emulate Activity's onWindowStartingActionMode:
        initActionBar();
        if (mActionBar != null) {
            mode = mActionBar.startActionMode(wrappedCallback);
        }

        if (mode != null) {
            mActionMode = mode;
        } else {
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
        }
        if (mActionMode != null && mActivity instanceof OnActionModeStartedListener) {
            ((OnActionModeStartedListener)mActivity).onActionModeStarted(mActionMode);
        }
        return mActionMode;
    }


    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle and interaction callbacks when delegating
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void dispatchConfigurationChanged(Configuration newConfig) {
        if (DEBUG) Log.d(TAG, "[dispatchConfigurationChanged] newConfig: " + newConfig);

        if (mActionBar != null) {
            mActionBar.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void dispatchPostResume() {
        if (DEBUG) Log.d(TAG, "[dispatchPostResume]");

        if (mActionBar != null) {
            mActionBar.setShowHideAnimationEnabled(true);
        }
    }

    @Override
    public void dispatchPause() {
        if (DEBUG) Log.d(TAG, "[dispatchPause]");

        if (mActionBarView != null && mActionBarView.isOverflowMenuShowing()) {
            mActionBarView.hideOverflowMenu();
        }
    }

    @Override
    public void dispatchStop() {
        if (DEBUG) Log.d(TAG, "[dispatchStop]");

        if (mActionBar != null) {
            mActionBar.setShowHideAnimationEnabled(false);
        }
    }

    @Override
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

        if (!callbackCreateOptionsMenu(mMenu)) {
            mMenu = null;
            if (mActionBar != null) {
                if (DEBUG) Log.d(TAG, "[dispatchInvalidateOptionsMenu] setting action bar menu to null");
                mActionBar.setMenu(null, mMenuPresenterCallback);
            }
            return;
        }

        if (!callbackPrepareOptionsMenu(mMenu)) {
            if (mActionBar != null) {
                if (DEBUG) Log.d(TAG, "[dispatchInvalidateOptionsMenu] setting action bar menu to null");
                mActionBar.setMenu(null, mMenuPresenterCallback);
            }
            mMenu.startDispatchingItemsChanged();
            return;
        }

        //TODO figure out KeyEvent? See PhoneWindow#preparePanel
        KeyCharacterMap kmap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);
        mMenu.setQwertyMode(kmap.getKeyboardType() != KeyCharacterMap.NUMERIC);
        mMenu.startDispatchingItemsChanged();

        if (DEBUG) Log.d(TAG, "[dispatchInvalidateOptionsMenu] setting action bar menu to " + mMenu);
        mActionBar.setMenu(mMenu, mMenuPresenterCallback);
    }

    @Override
    public boolean dispatchOpenOptionsMenu() {
        if (DEBUG) Log.d(TAG, "[dispatchOpenOptionsMenu]");

        if (!isReservingOverflow()) {
            return false;
        }

        return mActionBarView.showOverflowMenu();
    }

    @Override
    public boolean dispatchCloseOptionsMenu() {
        if (DEBUG) Log.d(TAG, "[dispatchCloseOptionsMenu]");

        if (!isReservingOverflow()) {
            return false;
        }

        return mActionBarView.hideOverflowMenu();
    }

    @Override
    public void dispatchPostCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.d(TAG, "[dispatchOnPostCreate]");

        if (mIsDelegate) {
            mIsTitleReady = true;
        }

        if (mDecor == null) {
            initActionBar();
        }
    }

    @Override
    public boolean dispatchCreateOptionsMenu(android.view.Menu menu) {
        return true;
    }

    @Override
    public boolean dispatchPrepareOptionsMenu(android.view.Menu menu) {
        if (DEBUG) Log.d(TAG, "[dispatchPrepareOptionsMenu] android.view.Menu: " + menu);

        if (!callbackPrepareOptionsMenu(mMenu)) {
            return false;
        }

        if (isReservingOverflow()) {
            return false;
        }

        if (mNativeItemMap == null) {
            mNativeItemMap = new HashMap<android.view.MenuItem, MenuItemImpl>();
        } else {
            mNativeItemMap.clear();
        }

        if (mMenu == null) {
            return false;
        }

        return mMenu.bindNativeOverflow(menu, mNativeItemListener, mNativeItemMap);
    }

    @Override
    public boolean dispatchOptionsItemSelected(android.view.MenuItem item) {
        throw new IllegalStateException("Native callback invoked. Create a test case and report!");
    }

    @Override
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

    @Override
    public void dispatchPanelClosed(int featureId, android.view.Menu menu){
        if (DEBUG) Log.d(TAG, "[dispatchPanelClosed] featureId: " + featureId + ", menu: " + menu);

        if (featureId == Window.FEATURE_ACTION_BAR || featureId == Window.FEATURE_OPTIONS_PANEL) {
            if (mActionBar != null) {
                mActionBar.dispatchMenuVisibilityChanged(false);
            }
        }
    }

    @Override
    public void dispatchTitleChanged(CharSequence title, int color) {
        if (DEBUG) Log.d(TAG, "[dispatchTitleChanged] title: " + title + ", color: " + color);

        if (mIsDelegate && !mIsTitleReady) {
            return;
        }
        if (mActionBarView != null) {
            mActionBarView.setWindowTitle(title);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (DEBUG) Log.d(TAG, "[dispatchKeyEvent] event: " + event);

        final int keyCode = event.getKeyCode();

        // Not handled by the view hierarchy, does the action bar want it
        // to cancel out of something special?
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            final int action = event.getAction();
            // Back cancels action modes first.
            if (mActionMode != null) {
                if (action == KeyEvent.ACTION_UP) {
                    mActionMode.finish();
                }
                if (DEBUG) Log.d(TAG, "[dispatchKeyEvent] returning true");
                return true;
            }

            // Next collapse any expanded action views.
            if (mActionBar != null && mActionBarView.hasExpandedActionView()) {
                if (action == KeyEvent.ACTION_UP) {
                    mActionBarView.collapseActionView();
                }
                if (DEBUG) Log.d(TAG, "[dispatchKeyEvent] returning true");
                return true;
            }
        }

        if (keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP && isReservingOverflow()) {
            if (mActionBarView.isOverflowMenuShowing()) {
                mActionBarView.hideOverflowMenu();
            } else {
                mActionBarView.showOverflowMenu();
            }
            if (DEBUG) Log.d(TAG, "[dispatchKeyEvent] returning true");
            return true;
        }

        if (DEBUG) Log.d(TAG, "[dispatchKeyEvent] returning false");
        return false;
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
                    splitActionBar = getResources_getBoolean(mActivity, R.bool.abs__split_action_bar_is_narrow);
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


    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void setProgressBarVisibility(boolean visible) {
        setFeatureInt(Window.FEATURE_PROGRESS, visible ? Window.PROGRESS_VISIBILITY_ON :
            Window.PROGRESS_VISIBILITY_OFF);
    }

    @Override
    public void setProgressBarIndeterminateVisibility(boolean visible) {
        setFeatureInt(Window.FEATURE_INDETERMINATE_PROGRESS,
                visible ? Window.PROGRESS_VISIBILITY_ON : Window.PROGRESS_VISIBILITY_OFF);
    }

    @Override
    public void setProgressBarIndeterminate(boolean indeterminate) {
        setFeatureInt(Window.FEATURE_PROGRESS,
                indeterminate ? Window.PROGRESS_INDETERMINATE_ON : Window.PROGRESS_INDETERMINATE_OFF);
    }

    @Override
    public void setProgress(int progress) {
        setFeatureInt(Window.FEATURE_PROGRESS, progress + Window.PROGRESS_START);
    }

    @Override
    public void setSecondaryProgress(int secondaryProgress) {
        setFeatureInt(Window.FEATURE_PROGRESS,
                secondaryProgress + Window.PROGRESS_SECONDARY_START);
    }

    ///////////////////////////////////////////////////////////////////////////
    // XXX
    ///////////////////////////////////////////////////////////////////////////

    private int getFeatures() {
        if (DEBUG) Log.d(TAG, "[getFeatures]");

        return mFeatures;
    }

    @Override
    public boolean hasFeature(int featureId) {
        if (DEBUG) Log.d(TAG, "[hasFeature] featureId: " + featureId);

        return (mFeatures & (1 << featureId)) != 0;
    }

    @Override
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

    @Override
    public void setUiOptions(int uiOptions) {
        if (DEBUG) Log.d(TAG, "[setUiOptions] uiOptions: " + uiOptions);

        mUiOptions = uiOptions;
    }

    @Override
    public void setUiOptions(int uiOptions, int mask) {
        if (DEBUG) Log.d(TAG, "[setUiOptions] uiOptions: " + uiOptions + ", mask: " + mask);

        mUiOptions = (mUiOptions & ~mask) | (uiOptions & mask);
    }

    @Override
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

    @Override
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

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        if (DEBUG) Log.d(TAG, "[addContentView] view: " + view + ", params: " + params);

        if (mContentParent == null) {
            installDecor();
        }
        mContentParent.addView(view, params);

        initActionBar();
    }

    @Override
    public void setTitle(CharSequence title) {
        if (DEBUG) Log.d(TAG, "[setTitle] title: " + title);

        dispatchTitleChanged(title, 0);
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

    private void onIntChanged(int featureId, int value) {
        if (featureId == Window.FEATURE_PROGRESS || featureId == Window.FEATURE_INDETERMINATE_PROGRESS) {
            updateProgressBars(value);
        }
    }

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
                                activityPackage = cleanActivityName(packageName, xml.getAttributeValue(i));
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

    public static String cleanActivityName(String manifestPackage, String activityName) {
        if (activityName.charAt(0) == '.') {
            //Relative activity name (e.g., android:name=".ui.SomeClass")
            return manifestPackage + activityName;
        }
        if (activityName.indexOf('.', 1) == -1) {
            //Unqualified activity name (e.g., android:name="SomeClass")
            return manifestPackage + "." + activityName;
        }
        //Fully-qualified activity name (e.g., "com.my.package.SomeClass")
        return activityName;
    }

    private ViewGroup generateLayout() {
        if (DEBUG) Log.d(TAG, "[generateLayout]");

        // Apply data from current theme.

        TypedArray a = mActivity.getTheme().obtainStyledAttributes(R.styleable.SherlockTheme);
        if (!a.hasValue(R.styleable.SherlockTheme_windowActionBar)) {
            throw new IllegalStateException("You must use Theme.Sherlock, Theme.Sherlock.Light, Theme.Sherlock.Light.DarkActionBar, or a derivative.");
        }

        if (a.getBoolean(R.styleable.SherlockTheme_windowNoTitle, false)) {
            requestFeature(Window.FEATURE_NO_TITLE);
        } else if (a.getBoolean(R.styleable.SherlockTheme_windowActionBar, false)) {
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

    private void reopenMenu(boolean toggleMenuMode) {
        if (mActionBarView != null && mActionBarView.isOverflowReserved()) {
            if (!mActionBarView.isOverflowMenuShowing() || !toggleMenuMode) {
                if (mActionBarView.getVisibility() == View.VISIBLE) {
                    if (callbackPrepareOptionsMenu(mMenu)) {
                        mActionBarView.showOverflowMenu();
                    }
                }
            } else {
                mActionBarView.hideOverflowMenu();
            }
            return;
        }
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

    /**
     * Support implementation of {@code getResources().getBoolean()} that we
     * can use to simulate filtering based on width and smallest width
     * qualifiers on pre-3.2.
     *
     * @param context Context to load booleans from on 3.2+ and to fetch the
     * display metrics.
     * @param id Id of boolean to load.
     * @return Associated boolean value as reflected by the current display
     * metrics.
     */
    public static boolean getResources_getBoolean(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            return context.getResources().getBoolean(id);
        }

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float widthDp = metrics.widthPixels / metrics.density;
        float heightDp = metrics.heightPixels / metrics.density;
        float smallestWidthDp = (widthDp < heightDp) ? widthDp : heightDp;

        if (id == R.bool.abs__action_bar_embed_tabs) {
            if (widthDp >= 480) {
                return true; //values-w480dp
            }
            return false; //values
        }
        if (id == R.bool.abs__split_action_bar_is_narrow) {
            if (widthDp >= 480) {
                return false; //values-w480dp
            }
            return true; //values
        }
        if (id == R.bool.abs__action_bar_expanded_action_views_exclusive) {
            if (smallestWidthDp >= 600) {
                return false; //values-sw600dp
            }
            return true; //values
        }
        if (id == R.bool.abs__config_allowActionMenuItemTextWithIcon) {
            if (widthDp >= 480) {
                return true; //values-w480dp
            }
            return false; //values
        }

        throw new IllegalArgumentException("Unknown boolean resource ID " + id);
    }

    /**
     * Support implementation of {@code getResources().getInteger()} that we
     * can use to simulate filtering based on width qualifiers on pre-3.2.
     *
     * @param context Context to load integers from on 3.2+ and to fetch the
     * display metrics.
     * @param id Id of integer to load.
     * @return Associated integer value as reflected by the current display
     * metrics.
     */
    public static int getResources_getInteger(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            return context.getResources().getInteger(id);
        }

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float widthDp = metrics.widthPixels / metrics.density;

        if (id == R.integer.abs__max_action_buttons) {
            if (widthDp >= 600) {
                return 5; //values-w600dp
            }
            if (widthDp >= 500) {
                return 4; //values-w500dp
            }
            if (widthDp >= 360) {
                return 3; //values-w360dp
            }
            return 2; //values
        }

        throw new IllegalArgumentException("Unknown integer resource ID " + id);
    }
}
