/*
 * Copyright (C) 2011 The Android Open Source Project
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.ActionBarSherlock.OnActionModeFinishedListener;
import com.actionbarsherlock.ActionBarSherlock.OnActionModeStartedListener;
import com.actionbarsherlock.ActionBarSherlock.OnCreatePanelMenuListener;
import com.actionbarsherlock.ActionBarSherlock.OnMenuItemSelectedListener;
import com.actionbarsherlock.ActionBarSherlock.OnPreparePanelListener;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * Base class for activities that want to use the support-based
 * {@link android.support.v4.app.Fragment} and
 * {@link android.support.v4.content.Loader} APIs.
 *
 * <p>When using this class as opposed to new platform's built-in fragment
 * and loader support, you must use the {@link #getSupportFragmentManager()}
 * and {@link #getSupportLoaderManager()} methods respectively to access
 * those features.
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
 * This means that in some cases if the activity is restored from its last saved
 * state, this may be a snapshot slightly before what the user last saw.</p>
 * </ul>
 */
public class FragmentActivity extends Activity implements OnCreatePanelMenuListener, OnPreparePanelListener, OnMenuItemSelectedListener, OnActionModeStartedListener, OnActionModeFinishedListener {
    private static final String TAG = "FragmentActivity";

    private static final String FRAGMENTS_TAG = "android:support:fragments";

    // This is the SDK API version of Honeycomb (3.0).
    private static final int HONEYCOMB = 11;

    static final int MSG_REALLY_STOPPED = 1;
    static final int MSG_RESUME_PENDING = 2;

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REALLY_STOPPED:
                    if (mStopped) {
                        doReallyStop(false);
                    }
                    break;
                case MSG_RESUME_PENDING:
                    mFragments.dispatchResume();
                    mFragments.execPendingActions();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

    };
    final FragmentManagerImpl mFragments = new FragmentManagerImpl();

    boolean mCreated;
    boolean mResumed;
    boolean mStopped;
    boolean mReallyStopped;
    boolean mRetaining;

    //boolean mOptionsMenuInvalidated;

    boolean mCheckedForLoaderManager;
    boolean mLoadersStarted;
    HCSparseArray<LoaderManagerImpl> mAllLoaderManagers;
    LoaderManagerImpl mLoaderManager;

    static final class NonConfigurationInstances {
        Object activity;
        Object custom;
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

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    private final ActionBarSherlock mSherlock;


    public FragmentActivity() {
        this(ActionBarSherlock.FLAG_DELEGATE);
    }

    public FragmentActivity(int flags) {
        mSherlock = ActionBarSherlock.wrap(this, flags);
    }

    public <T extends ActionBarSherlock> FragmentActivity(int flags, Class<T> implementation) {
        mSherlock = ActionBarSherlock.wrap(this, flags, implementation);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Action bar and mode
    ///////////////////////////////////////////////////////////////////////////

    public ActionBar getSupportActionBar() {
        return mSherlock.getActionBar();
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        return mSherlock.startActionMode(callback);
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {}

    @Override
    public void onActionModeFinished(ActionMode mode) {}


    ///////////////////////////////////////////////////////////////////////////
    // General lifecycle/callback dispatching
    ///////////////////////////////////////////////////////////////////////////

    /* Implementation added to existing override
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mSherlock.dispatchConfigurationChanged(newConfig);
    }
    */

    /* Implementation added to existing override
    @Override
    protected void onPostResume() {
        super.onPostResume();
        mSherlock.dispatchPostResume();
    }
    */

    /* Implementation added to existing override
    @Override
    protected void onPause() {
        mSherlock.dispatchPause();
        super.onPause();
    }
    */

    /* Implementation added to existing override
    @Override
    protected void onStop() {
        mSherlock.dispatchStop();
        super.onStop();
    }
    */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        mSherlock.dispatchPostCreate(savedInstanceState);
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        mSherlock.dispatchTitleChanged(title, color);
        super.onTitleChanged(title, color);
    }

    @Override
    public final boolean onMenuOpened(int featureId, android.view.Menu menu) {
        if (mSherlock.dispatchMenuOpened(featureId, menu)) {
            return true;
        }
        return super.onMenuOpened(featureId, menu);
    }

    /* Implementation added to existing override
    @Override
    public void onPanelClosed(int featureId, android.view.Menu menu) {
        mSherlock.dispatchPanelClosed(featureId, menu);
        super.onPanelClosed(featureId, menu);
    }
    */

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mSherlock.dispatchKeyEvent(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Native menu handling
    ///////////////////////////////////////////////////////////////////////////

    public MenuInflater getSupportMenuInflater() {
        return mSherlock.getMenuInflater();
    }

    public void invalidateOptionsMenu() {
        mSherlock.dispatchInvalidateOptionsMenu();
    }

    public void supportInvalidateOptionsMenu() {
        invalidateOptionsMenu();
    }

    @Override
    public final boolean onCreateOptionsMenu(android.view.Menu menu) {
        return mSherlock.dispatchCreateOptionsMenu(menu);
    }

    @Override
    public final boolean onPrepareOptionsMenu(android.view.Menu menu) {
        return mSherlock.dispatchPrepareOptionsMenu(menu);
    }

    @Override
    public final boolean onOptionsItemSelected(android.view.MenuItem item) {
        return mSherlock.dispatchOptionsItemSelected(item);
    }

    @Override
    public void openOptionsMenu() {
        if (!mSherlock.dispatchOpenOptionsMenu()) {
            super.openOptionsMenu();
        }
    }

    @Override
    public void closeOptionsMenu() {
        if (!mSherlock.dispatchCloseOptionsMenu()) {
            super.closeOptionsMenu();
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // Sherlock menu handling
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_OPTIONS_PANEL) {
            boolean show = onCreateOptionsMenu(menu);
            show |= mFragments.dispatchCreateOptionsMenu(menu, getSupportMenuInflater());
            return show;
        }
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        if (featureId == Window.FEATURE_OPTIONS_PANEL) {
            boolean goforit = onPrepareOptionsMenu(menu);
            goforit |= mFragments.dispatchPrepareOptionsMenu(menu);
            return goforit && menu.hasVisibleItems();
        }
        return false;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (featureId == Window.FEATURE_OPTIONS_PANEL) {
            if (onOptionsItemSelected(item)) {
                return true;
            }
            return mFragments.dispatchOptionsItemSelected(item);
        }
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }


    ///////////////////////////////////////////////////////////////////////////
    // Content
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void addContentView(View view, LayoutParams params) {
        mSherlock.addContentView(view, params);
    }

    @Override
    public void setContentView(int layoutResId) {
        mSherlock.setContentView(layoutResId);
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        mSherlock.setContentView(view, params);
    }

    @Override
    public void setContentView(View view) {
        mSherlock.setContentView(view);
    }

    public void requestWindowFeature(long featureId) {
        mSherlock.requestFeature((int)featureId);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Progress Indication
    ///////////////////////////////////////////////////////////////////////////

    public void setSupportProgress(int progress) {
        mSherlock.setProgress(progress);
    }

    public void setSupportProgressBarIndeterminate(boolean indeterminate) {
        mSherlock.setProgressBarIndeterminate(indeterminate);
    }

    public void setSupportProgressBarIndeterminateVisibility(boolean visible) {
        mSherlock.setProgressBarIndeterminateVisibility(visible);
    }

    public void setSupportProgressBarVisibility(boolean visible) {
        mSherlock.setProgressBarVisibility(visible);
    }

    public void setSupportSecondaryProgress(int secondaryProgress) {
        mSherlock.setSecondaryProgress(secondaryProgress);
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    // ------------------------------------------------------------------------
    // HOOKS INTO ACTIVITY
    // ------------------------------------------------------------------------

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
        mSherlock.dispatchConfigurationChanged(newConfig);
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
    }

    /**
     * Dispatch to Fragment.onCreateOptionsMenu().
     */
    /* Implementation provided by ActionBarSherlock
    @Override
    public boolean onCreatePanelMenu(int featureId, android.view.Menu menu) {
        if (featureId == Window.FEATURE_OPTIONS_PANEL) {
            boolean show = super.onCreatePanelMenu(featureId, menu);
            show |= mFragments.dispatchCreateOptionsMenu(menu, getMenuInflater());
            if (android.os.Build.VERSION.SDK_INT >= HONEYCOMB) {
                return show;
            }
            // Prior to Honeycomb, the framework can't invalidate the options
            // menu, so we must always say we have one in case the app later
            // invalidates it and needs to have it shown.
            return true;
        }
        return super.onCreatePanelMenu(featureId, menu);
    }
    */

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
    public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
        if (super.onMenuItemSelected(featureId, item)) {
            return true;
        }

        switch (featureId) {
            //Implementation provided by ActionBarSherlock
            //case Window.FEATURE_OPTIONS_PANEL:
            //    return mFragments.dispatchOptionsItemSelected(item);

            case Window.FEATURE_CONTEXT_MENU:
                return mFragments.dispatchContextItemSelected(item);

            default:
                return false;
        }
    }

    /**
     * Call onOptionsMenuClosed() on fragments.
     */
    @Override
    public void onPanelClosed(int featureId, android.view.Menu menu) {
        mSherlock.dispatchPanelClosed(featureId, menu);
        switch (featureId) {
            case Window.FEATURE_OPTIONS_PANEL:
                //TODO mFragments.dispatchOptionsMenuClosed(menu);
                break;
        }
        super.onPanelClosed(featureId, menu);
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        mSherlock.dispatchPause();
        super.onPause();
        mResumed = false;
        if (mHandler.hasMessages(MSG_RESUME_PENDING)) {
            mHandler.removeMessages(MSG_RESUME_PENDING);
            mFragments.dispatchResume();
        }
        mFragments.dispatchPause();
    }

    /**
     * Dispatch onResume() to fragments.
     */
    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessage(MSG_RESUME_PENDING);
        mResumed = true;
        mFragments.execPendingActions();
    }

    /**
     * Dispatch onResume() to fragments.
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        mSherlock.dispatchPostResume();
        mHandler.removeMessages(MSG_RESUME_PENDING);
        mFragments.dispatchResume();
        mFragments.execPendingActions();
    }

    /**
     * Dispatch onPrepareOptionsMenu() to fragments.
     */
    /* Implementation provided by ActionBarSherlock
    @Override
    public boolean onPreparePanel(int featureId, View view, android.view.Menu menu) {
        if (featureId == Window.FEATURE_OPTIONS_PANEL && menu != null) {
            if (mOptionsMenuInvalidated) {
                mOptionsMenuInvalidated = false;
                menu.clear();
                onCreatePanelMenu(featureId, menu);
            }
            boolean goforit = super.onPreparePanel(featureId, view, menu);
            goforit |= mFragments.dispatchPrepareOptionsMenu(menu);
            return goforit && menu.hasVisibleItems();
        }
        return super.onPreparePanel(featureId, view, menu);
    }
    */

    /**
     * Retain all appropriate fragment and loader state.  You can NOT
     * override this yourself!  Use {@link #onRetainCustomNonConfigurationInstance()}
     * if you want to retain your own state.
     */
    @Override
    public final Object onRetainNonConfigurationInstance() {
        if (mStopped) {
            doReallyStop(true);
        }

        Object custom = onRetainCustomNonConfigurationInstance();

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
        if (fragments == null && !retainLoaders && custom == null) {
            return null;
        }

        NonConfigurationInstances nci = new NonConfigurationInstances();
        nci.activity = null;
        nci.custom = custom;
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
        mReallyStopped = false;
        mHandler.removeMessages(MSG_REALLY_STOPPED);

        if (!mCreated) {
            mCreated = true;
            mFragments.dispatchActivityCreated();
        }

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
                LoaderManagerImpl lm = mAllLoaderManagers.valueAt(i);
                lm.finishRetain();
                lm.doReportStart();
            }
        }
    }

    /**
     * Dispatch onStop() to all fragments.  Ensure all loaders are stopped.
     */
    @Override
    protected void onStop() {
        mSherlock.dispatchStop();
        super.onStop();

        mStopped = true;
        mHandler.sendEmptyMessage(MSG_REALLY_STOPPED);

        mFragments.dispatchStop();
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
        NonConfigurationInstances nc = (NonConfigurationInstances)
                getLastNonConfigurationInstance();
        return nc != null ? nc.custom : null;
    }

    /* Implementation provided by ActionBarSherlock
    void supportInvalidateOptionsMenu() {
        if (android.os.Build.VERSION.SDK_INT >= HONEYCOMB) {
            // If we are running on HC or greater, we can use the framework
            // API to invalidate the options menu.
            ActivityCompatHoneycomb.invalidateOptionsMenu(this);
            return;
        }

        // Whoops, older platform...  we'll use a hack, to manually rebuild
        // the options menu the next time it is prepared.
        mOptionsMenuInvalidated = true;
    }
    */

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
        if (android.os.Build.VERSION.SDK_INT >= HONEYCOMB) {
            // XXX This can only work if we can call the super-class impl. :/
            //ActivityCompatHoneycomb.dump(this, prefix, fd, writer, args);
        }
        writer.print(prefix); writer.print("Local FragmentActivity ");
                writer.print(Integer.toHexString(System.identityHashCode(this)));
                writer.println(" State:");
        String innerPrefix = prefix + "  ";
        writer.print(innerPrefix); writer.print("mCreated=");
                writer.print(mCreated); writer.print("mResumed=");
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
            mRetaining = retaining;
            mHandler.removeMessages(MSG_REALLY_STOPPED);
            onReallyStop();
        }
    }

    /**
     * Pre-HC, we didn't have a way to determine whether an activity was
     * being stopped for a config change or not until we saw
     * onRetainNonConfigurationInstance() called after onStop().  However
     * we need to know this, to know whether to retain fragments.  This will
     * tell us what we need to know.
     */
    void onReallyStop() {
        if (mLoadersStarted) {
            mLoadersStarted = false;
            if (mLoaderManager != null) {
                if (!mRetaining) {
                    mLoaderManager.doStop();
                } else {
                    mLoaderManager.doRetain();
                }
            }
        }

        mFragments.dispatchReallyStop();
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
            if (lm != null && !lm.mRetaining) {
                lm.doDestroy();
                mAllLoaderManagers.remove(index);
            }
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
