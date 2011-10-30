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
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.support.v4.view.ActionMode;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuInflater;
import android.support.v4.view.MenuItem;
import android.support.v4.view.Window;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import com.actionbarsherlock.R;
import com.actionbarsherlock.internal.app.ActionBarImpl;
import com.actionbarsherlock.internal.app.ActionBarWrapper;
import com.actionbarsherlock.internal.view.menu.MenuBuilder;
import com.actionbarsherlock.internal.view.menu.MenuInflaterWrapper;
import com.actionbarsherlock.internal.view.menu.MenuItemImpl;
import com.actionbarsherlock.internal.view.menu.MenuItemWrapper;
import com.actionbarsherlock.internal.view.menu.MenuWrapper;

/**
 * Base class for activities that want to use the support-based ActionBar and
 * Preference APIs.
 */
public class SherlockPreferenceActivity extends PreferenceActivity implements SupportActivity {
    private static final String TAG = "SherlockPreferenceActivity";
    private static final boolean DEBUG = false;

    static final boolean IS_HONEYCOMB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

    private static final int WINDOW_FLAG_ACTION_BAR = 1 << Window.FEATURE_ACTION_BAR;
    private static final int WINDOW_FLAG_ACTION_BAR_ITEM_TEXT = 1 << Window.FEATURE_ACTION_BAR_ITEM_TEXT;
    private static final int WINDOW_FLAG_ACTION_BAR_OVERLAY = 1 << Window.FEATURE_ACTION_BAR_OVERLAY;
    private static final int WINDOW_FLAG_ACTION_MODE_OVERLAY = 1 << Window.FEATURE_ACTION_MODE_OVERLAY;
    private static final int WINDOW_FLAG_INDETERMINANTE_PROGRESS = 1 << Window.FEATURE_INDETERMINATE_PROGRESS;

    final SupportActivity.InternalCallbacks mInternalCallbacks = new SupportActivity.InternalCallbacks() {
        @Override
        void invalidateSupportFragmentIndex(int index) {
            //No op
        }

        @Override
        LoaderManagerImpl getLoaderManager(int index, boolean started, boolean create) {
            return null;
        }

        @Override
        Handler getHandler() {
            return null;
        }

        @Override
        FragmentManagerImpl getFragments() {
            return null;
        }

        @Override
        void ensureSupportActionBarAttached() {
            SherlockPreferenceActivity.this.ensureSupportActionBarAttached();
        }

        @Override
        boolean getRetaining() {
            return false;
        }
    };

    ActionBar mActionBar;
    boolean mIsActionBarImplAttached;
    long mWindowFlags = 0;

    final MenuBuilder mSupportMenu;
    final MenuBuilder.Callback mSupportMenuCallback = new MenuBuilder.Callback() {
        @Override
        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
            return SherlockPreferenceActivity.this.onMenuItemSelected(Window.FEATURE_OPTIONS_PANEL, item);
        }
    };

    boolean mOptionsMenuInvalidated;
    boolean mOptionsMenuCreateResult;


    public SherlockPreferenceActivity() {
        super();

        if (IS_HONEYCOMB) {
            mActionBar = ActionBarWrapper.createFor(this);
            mSupportMenu = null; //Everything should be done natively
        } else {
            mSupportMenu = new MenuBuilder(this);
            mSupportMenu.setCallback(mSupportMenuCallback);
        }
    }

    @Override
    public SupportActivity.InternalCallbacks getInternalCallbacks() {
        return mInternalCallbacks;
    }

    @Override
    public Activity asActivity() {
        return this;
    }

    protected void ensureSupportActionBarAttached() {
        if (IS_HONEYCOMB) {
            return;
        }
        if (!mIsActionBarImplAttached) {
            if (isChild()) {
                //Do not allow an action bar if we have a parent activity
                mWindowFlags &= ~WINDOW_FLAG_ACTION_BAR;
            }

            final ListView contentView = new ListView(this);
            contentView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            contentView.setId(android.R.id.list);

            if ((mWindowFlags & WINDOW_FLAG_ACTION_BAR) == WINDOW_FLAG_ACTION_BAR) {
                if ((mWindowFlags & WINDOW_FLAG_ACTION_BAR_OVERLAY) == WINDOW_FLAG_ACTION_BAR_OVERLAY) {
                    View view = getLayoutInflater().inflate(R.layout.abs__screen_action_bar_overlay, null);
                    ((ViewGroup)view.findViewById(R.id.abs__content)).addView(contentView);
                    super.setContentView(view);
                } else {
                    View view = getLayoutInflater().inflate(R.layout.abs__screen_action_bar, null);
                    ((ViewGroup)view.findViewById(R.id.abs__content)).addView(contentView);
                    super.setContentView(view);
                }

                mActionBar = new ActionBarImpl(this);
                ((ActionBarImpl)mActionBar).init();

                final boolean textEnabled = ((mWindowFlags & WINDOW_FLAG_ACTION_BAR_ITEM_TEXT) == WINDOW_FLAG_ACTION_BAR_ITEM_TEXT);
                mSupportMenu.setShowsActionItemText(textEnabled);

                if ((mWindowFlags & WINDOW_FLAG_INDETERMINANTE_PROGRESS) == WINDOW_FLAG_INDETERMINANTE_PROGRESS) {
                    ((ActionBarImpl)mActionBar).setProgressBarIndeterminateVisibility(false);
                }

                //TODO set other flags
            } else {
                if ((mWindowFlags & WINDOW_FLAG_INDETERMINANTE_PROGRESS) == WINDOW_FLAG_INDETERMINANTE_PROGRESS) {
                    super.requestWindowFeature((int)Window.FEATURE_INDETERMINATE_PROGRESS);
                }
                View view = getLayoutInflater().inflate(R.layout.abs__screen_simple, null);
                ((ViewGroup)view.findViewById(R.id.abs__content)).addView(contentView);
                super.setContentView(view);
            }

            invalidateOptionsMenu();
            mIsActionBarImplAttached = true;
        }
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
    @Override
    public boolean requestWindowFeature(long featureId) {
        if (!IS_HONEYCOMB) {
            switch ((int)featureId) {
                case (int)Window.FEATURE_ACTION_BAR:
                case (int)Window.FEATURE_ACTION_BAR_ITEM_TEXT:
                case (int)Window.FEATURE_ACTION_BAR_OVERLAY:
                case (int)Window.FEATURE_ACTION_MODE_OVERLAY:
                case (int)Window.FEATURE_INDETERMINATE_PROGRESS:
                    mWindowFlags |= (1 << featureId);
                return true;
            }
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
        return new MenuInflater(this, super.getMenuInflater());
    }

    public void setTitle(CharSequence title) {
        if (IS_HONEYCOMB || (getSupportActionBar() == null)) {
            super.setTitle(title);
        } else {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void setTitle(int titleId) {
        if (IS_HONEYCOMB || (getSupportActionBar() == null)) {
            super.setTitle(titleId);
        } else {
            getSupportActionBar().setTitle(titleId);
        }
    }

    @Override
    protected void onApplyThemeResource(Theme theme, int resid, boolean first) {
        TypedArray attrs = theme.obtainStyledAttributes(resid, R.styleable.SherlockTheme);

        final boolean actionBar = attrs.getBoolean(R.styleable.SherlockTheme_windowActionBar, false);
        mWindowFlags |= actionBar ? WINDOW_FLAG_ACTION_BAR : 0;

        final boolean actionModeOverlay = attrs.getBoolean(R.styleable.SherlockTheme_windowActionModeOverlay, false);
        mWindowFlags |= actionModeOverlay ? WINDOW_FLAG_ACTION_MODE_OVERLAY : 0;

        attrs.recycle();
        super.onApplyThemeResource(theme, resid, first);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ensureSupportActionBarAttached();
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
    @Override
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
            result = onCreateOptionsMenu(wrapped);
        }

        if (DEBUG) Log.d(TAG, "onCreateOptionsMenu(android.view.Menu): Returning " + result);
        return result;
    }

    @Override
    public void invalidateOptionsMenu() {
        if (DEBUG) Log.d(TAG, "supportInvalidateOptionsMenu(): Invalidating menu.");

        if (IS_HONEYCOMB) {
            HoneycombInvalidateOptionsMenu.invoke(this);
        } else {
            mSupportMenu.clear();

            mOptionsMenuCreateResult = onCreateOptionsMenu(mSupportMenu);

            if (getSupportActionBar() != null) {
                onPrepareOptionsMenu(mSupportMenu);

                //Since we now know we are using a custom action bar, perform the
                //inflation callback to allow it to display any items it wants.
                ((ActionBarImpl)mActionBar).onMenuInflated(mSupportMenu);
            }

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

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
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
                if (!IS_HONEYCOMB && (getSupportActionBar() != null)) {
                    if (DEBUG) Log.d(TAG, "onPanelClosed(int, android.view.Menu): Dispatch menu visibility false to custom action bar.");
                    ((ActionBarImpl)mActionBar).onMenuVisibilityChanged(false);
                }
                break;
        }
        super.onPanelClosed(featureId, menu);
    }

    @Override
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
                prepareResult = onPrepareOptionsMenu(mSupportMenu);
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
                    for (MenuItemImpl item : mSupportMenu.getItems()) {
                        if (!item.isShownOnActionBar()) {
                            item.addTo(menu);
                        }
                    }
                }
            }

            if (mOptionsMenuCreateResult && prepareResult && menu.hasVisibleItems()) {
                if (getSupportActionBar() != null) {
                    if (DEBUG) Log.d(TAG, "onPrepareOptionsMenu(android.view.Menu): Dispatch menu visibility true to custom action bar.");
                    ((ActionBarImpl)mActionBar).onMenuVisibilityChanged(true);
                }
                result = true;
            }
        } else {
            if (DEBUG) Log.d(TAG, "onPrepareOptionsMenu(android.view.Menu): Calling support method with wrapped native menu.");
            final MenuWrapper wrappedMenu = new MenuWrapper(menu);
            result = onPrepareOptionsMenu(wrappedMenu);
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
    @Override
    public void recreate() {
        //This SUCKS! Figure out a way to call the super method and support Android 1.6
        /*
        if (IS_HONEYCOMB) {
            super.recreate();
        } else {
        */
            final Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            startActivity(intent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                OverridePendingTransition.invoke(this);
            }

            finish();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
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
     * Retain all appropriate fragment and loader state.  You can NOT
     * override this yourself!  Use {@link #onRetainCustomNonConfigurationInstance()}
     * if you want to retain your own state.
     */
    @Override
    public final Object onRetainNonConfigurationInstance() {
        Object custom = onRetainCustomNonConfigurationInstance();
        if (custom == null) {
            return null;
        }
        return custom;
    }

    /**
     * <p>Sets the visibility of the indeterminate progress bar in the
     * title.</p>
     *
     * <p>In order for the progress bar to be shown, the feature must be
     * requested via {@link #requestWindowFeature(long)}.</p>
     *
     * <p><strong>This method must be used instead of
     * {@link #setProgressBarIndeterminateVisibility(boolean)} for
     * ActionBarSherlock.</strong> Pass {@link Boolean.TRUE} or
     * {@link Boolean.FALSE} to ensure the appropriate one is called.</p>
     *
     * @param visible Whether to show the progress bars in the title.
     */
    @Override
    public void setProgressBarIndeterminateVisibility(Boolean visible) {
        if (IS_HONEYCOMB || (getSupportActionBar() == null)) {
            super.setProgressBarIndeterminateVisibility(visible);
        } else if ((mWindowFlags & WINDOW_FLAG_INDETERMINANTE_PROGRESS) == WINDOW_FLAG_INDETERMINANTE_PROGRESS) {
            ((ActionBarImpl)mActionBar).setProgressBarIndeterminateVisibility(visible);
        }
    }

    // ------------------------------------------------------------------------
    // NEW METHODS
    // ------------------------------------------------------------------------

    /**
     * Use this instead of {@link #onRetainNonConfigurationInstance()}.
     * Retrieve later with {@link #getLastCustomNonConfigurationInstance()}.
     */
    public Object onRetainCustomNonConfigurationInstance() {
        return null;
    }

    /**
     * Return the value previously returned from
     * {@link #onRetainCustomNonConfigurationInstance()}.
     */
    public Object getLastCustomNonConfigurationInstance() {
        return getLastNonConfigurationInstance();
    }

    /**
     * @deprecated Use {@link invalidateOptionsMenu}.
     */
    @Deprecated
    void supportInvalidateOptionsMenu() {
        invalidateOptionsMenu();
    }

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
    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        if (IS_HONEYCOMB) {
            //This can only work if we can call the super-class impl. :/
            //ActivityCompatHoneycomb.dump(this, prefix, fd, writer, args);
        }
        writer.print(prefix); writer.print("Local FragmentActivity ");
                writer.print(Integer.toHexString(System.identityHashCode(this)));
    }

    // ------------------------------------------------------------------------
    // ACTION BAR AND ACTION MODE SUPPORT
    // ------------------------------------------------------------------------

    /**
     * Retrieve a reference to this activity's action bar handler.
     *
     * @return The handler for the appropriate action bar, or null.
     */
    @Override
    public ActionBar getSupportActionBar() {
        return (mActionBar != null) ? mActionBar.getPublicInstance() : null;
    }

    /**
     * Notifies the activity that an action mode has finished. Activity
     * subclasses overriding this method should call the superclass
     * implementation.
     *
     * @param mode The action mode that just finished.
     */
    @Override
    public void onActionModeFinished(ActionMode mode) {
    }

    /**
     * Notifies the Activity that an action mode has been started. Activity
     * subclasses overriding this method should call the superclass
     * implementation.
     *
     * @param mode The new action mode.
     */
    @Override
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
    @Override
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
    @Override
    public final ActionMode startActionMode(final ActionMode.Callback callback) {
        //Give the activity override a chance to handle the action mode
        ActionMode actionMode = onWindowStartingActionMode(callback);

        if (actionMode == null) {
            //If the activity did not handle, send to action bar for platform-
            //specific implementation
            actionMode = mActionBar.startActionMode(callback);
        }
        if (actionMode != null) {
            //Send the activity callback that our action mode was started
            onActionModeStarted(actionMode);
        }

        //Return to the caller
        return actionMode;
    }

    // ------------------------------------------------------------------------
    // FRAGMENT SUPPORT
    // ------------------------------------------------------------------------

    /**
     * Called when a fragment is attached to the activity.
     */
    @Override
    public void onAttachFragment(Fragment fragment) {
    }

    /**
     * Return the FragmentManager for interacting with fragments associated
     * with this activity.
     */
    @Override
    public FragmentManager getSupportFragmentManager() {
        return null;
    }

    /**
     * Called by Fragment.startActivityForResult() to implement its behavior.
     */
    @Override
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
    }

    // ------------------------------------------------------------------------
    // LOADER SUPPORT
    // ------------------------------------------------------------------------

    /**
     * Return the LoaderManager for this fragment, creating it if needed.
     */
    @Override
    public LoaderManager getSupportLoaderManager() {
        return null;
    }
}
