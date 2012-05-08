package android.support.v4.app;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
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

import java.util.ArrayList;

public abstract class _ActionBarSherlockTrojanHorse extends FragmentActivity implements OnCreatePanelMenuListener, OnPreparePanelListener, OnMenuItemSelectedListener, OnActionModeStartedListener, OnActionModeFinishedListener {
    static final boolean DEBUG = false;
    private static final String TAG = "SherlockFragmentActivity";

    /** Fragment interface for menu creation callback. */
    public interface OnCreateOptionsMenuListener {
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater);
    }
    /** Fragment interface for menu preparation callback. */
    public interface OnPrepareOptionsMenuListener {
        public void onPrepareOptionsMenu(Menu menu);
    }
    /** Fragment interface for menu item selection callback. */
    public interface OnOptionsItemSelectedListener {
      public boolean onOptionsItemSelected(MenuItem item);
    }

    private ActionBarSherlock mSherlock;
    private boolean mIgnoreNativeCreate = false;
    private boolean mIgnoreNativePrepare = false;
    private boolean mIgnoreNativeSelected = false;

    private ArrayList<Fragment> mCreatedMenus;

    protected final ActionBarSherlock getSherlock() {
        if (mSherlock == null) {
            mSherlock = ActionBarSherlock.wrap(this, ActionBarSherlock.FLAG_DELEGATE);
        }
        return mSherlock;
    }


    ///////////////////////////////////////////////////////////////////////////
    // Action bar and mode
    ///////////////////////////////////////////////////////////////////////////

    public ActionBar getSupportActionBar() {
        return getSherlock().getActionBar();
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        return getSherlock().startActionMode(callback);
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {}

    @Override
    public void onActionModeFinished(ActionMode mode) {}


    ///////////////////////////////////////////////////////////////////////////
    // General lifecycle/callback dispatching
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getSherlock().dispatchConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getSherlock().dispatchPostResume();
    }

    @Override
    protected void onPause() {
        getSherlock().dispatchPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        getSherlock().dispatchStop();
        super.onStop();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        getSherlock().dispatchPostCreate(savedInstanceState);
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        getSherlock().dispatchTitleChanged(title, color);
        super.onTitleChanged(title, color);
    }

    @Override
    public final boolean onMenuOpened(int featureId, android.view.Menu menu) {
        if (getSherlock().dispatchMenuOpened(featureId, menu)) {
            return true;
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public void onPanelClosed(int featureId, android.view.Menu menu) {
        getSherlock().dispatchPanelClosed(featureId, menu);
        super.onPanelClosed(featureId, menu);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (getSherlock().dispatchKeyEvent(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Native menu handling
    ///////////////////////////////////////////////////////////////////////////

    public MenuInflater getSupportMenuInflater() {
        if (DEBUG) Log.d(TAG, "[getSupportMenuInflater]");

        return getSherlock().getMenuInflater();
    }

    public void invalidateOptionsMenu() {
        if (DEBUG) Log.d(TAG, "[invalidateOptionsMenu]");

        getSherlock().dispatchInvalidateOptionsMenu();
    }

    public void supportInvalidateOptionsMenu() {
        if (DEBUG) Log.d(TAG, "[supportInvalidateOptionsMenu]");

        invalidateOptionsMenu();
    }

    @Override
    public final boolean onCreatePanelMenu(int featureId, android.view.Menu menu) {
        if (DEBUG) Log.d(TAG, "[onCreatePanelMenu] featureId: " + featureId + ", menu: " + menu);

        if (featureId == Window.FEATURE_OPTIONS_PANEL && !mIgnoreNativeCreate) {
            mIgnoreNativeCreate = true;
            boolean result = getSherlock().dispatchCreateOptionsMenu(menu);
            mIgnoreNativeCreate = false;

            if (DEBUG) Log.d(TAG, "[onCreatePanelMenu] returning " + result);
            return result;
        }
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public final boolean onCreateOptionsMenu(android.view.Menu menu) {
        return true;
    }

    @Override
    public final boolean onPreparePanel(int featureId, View view, android.view.Menu menu) {
        if (DEBUG) Log.d(TAG, "[onPreparePanel] featureId: " + featureId + ", view: " + view + ", menu: " + menu);

        if (featureId == Window.FEATURE_OPTIONS_PANEL && !mIgnoreNativePrepare) {
            mIgnoreNativePrepare = true;
            boolean result = getSherlock().dispatchPrepareOptionsMenu(menu);
            mIgnoreNativePrepare = false;

            if (DEBUG) Log.d(TAG, "[onPreparePanel] returning " + result);
            return result;
        }
        return super.onPreparePanel(featureId, view, menu);
    }

    @Override
    public final boolean onPrepareOptionsMenu(android.view.Menu menu) {
        return true;
    }

    @Override
    public final boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
        if (DEBUG) Log.d(TAG, "[onMenuItemSelected] featureId: " + featureId + ", item: " + item);

        if (featureId == Window.FEATURE_OPTIONS_PANEL && !mIgnoreNativeSelected) {
            mIgnoreNativeSelected = true;
            boolean result = getSherlock().dispatchOptionsItemSelected(item);
            mIgnoreNativeSelected = false;

            if (DEBUG) Log.d(TAG, "[onMenuItemSelected] returning " + result);
            return result;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public final boolean onOptionsItemSelected(android.view.MenuItem item) {
        return false;
    }

    @Override
    public void openOptionsMenu() {
        if (!getSherlock().dispatchOpenOptionsMenu()) {
            super.openOptionsMenu();
        }
    }

    @Override
    public void closeOptionsMenu() {
        if (!getSherlock().dispatchCloseOptionsMenu()) {
            super.closeOptionsMenu();
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // Sherlock menu handling
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (DEBUG) Log.d(TAG, "[onCreatePanelMenu] featureId: " + featureId + ", menu: " + menu);

        if (featureId == Window.FEATURE_OPTIONS_PANEL) {
            boolean result = onCreateOptionsMenu(menu);
            if (DEBUG) Log.d(TAG, "[onCreatePanelMenu] activity create result: " + result);

            MenuInflater inflater = getSupportMenuInflater();
            boolean show = false;
            ArrayList<Fragment> newMenus = null;
            if (mFragments.mActive != null) {
                for (int i = 0; i < mFragments.mAdded.size(); i++) {
                    Fragment f = mFragments.mAdded.get(i);
                    if (f != null && !f.mHidden && f.mHasMenu && f.mMenuVisible && f instanceof OnCreateOptionsMenuListener) {
                        show = true;
                        ((OnCreateOptionsMenuListener)f).onCreateOptionsMenu(menu, inflater);
                        if (newMenus == null) {
                            newMenus = new ArrayList<Fragment>();
                        }
                        newMenus.add(f);
                    }
                }
            }

            if (mCreatedMenus != null) {
                for (int i = 0; i < mCreatedMenus.size(); i++) {
                    Fragment f = mCreatedMenus.get(i);
                    if (newMenus == null || !newMenus.contains(f)) {
                        f.onDestroyOptionsMenu();
                    }
                }
            }

            mCreatedMenus = newMenus;

            if (DEBUG) Log.d(TAG, "[onCreatePanelMenu] fragments create result: " + show);
            result |= show;

            if (DEBUG) Log.d(TAG, "[onCreatePanelMenu] returning " + result);
            return result;
        }
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        if (DEBUG) Log.d(TAG, "[onPreparePanel] featureId: " + featureId + ", view: " + view + " menu: " + menu);

        if (featureId == Window.FEATURE_OPTIONS_PANEL) {
            boolean result = onPrepareOptionsMenu(menu);
            if (DEBUG) Log.d(TAG, "[onPreparePanel] activity prepare result: " + result);

            boolean show = false;
            if (mFragments.mActive != null) {
                for (int i = 0; i < mFragments.mAdded.size(); i++) {
                    Fragment f = mFragments.mAdded.get(i);
                    if (f != null && !f.mHidden && f.mHasMenu && f.mMenuVisible && f instanceof OnPrepareOptionsMenuListener) {
                        show = true;
                        ((OnPrepareOptionsMenuListener)f).onPrepareOptionsMenu(menu);
                    }
                }
            }

            if (DEBUG) Log.d(TAG, "[onPreparePanel] fragments prepare result: " + show);
            result |= show;

            result &= menu.hasVisibleItems();
            if (DEBUG) Log.d(TAG, "[onPreparePanel] returning " + result);
            return result;
        }
        return false;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (DEBUG) Log.d(TAG, "[onMenuItemSelected] featureId: " + featureId + ", item: " + item);

        if (featureId == Window.FEATURE_OPTIONS_PANEL) {
            if (onOptionsItemSelected(item)) {
                return true;
            }

            if (mFragments.mActive != null) {
                for (int i = 0; i < mFragments.mAdded.size(); i++) {
                    Fragment f = mFragments.mAdded.get(i);
                    if (f != null && !f.mHidden && f.mHasMenu && f.mMenuVisible && f instanceof OnOptionsItemSelectedListener) {
                        if (((OnOptionsItemSelectedListener)f).onOptionsItemSelected(item)) {
                            return true;
                        }
                    }
                }
            }
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
        getSherlock().addContentView(view, params);
    }

    @Override
    public void setContentView(int layoutResId) {
        getSherlock().setContentView(layoutResId);
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        getSherlock().setContentView(view, params);
    }

    @Override
    public void setContentView(View view) {
        getSherlock().setContentView(view);
    }

    public void requestWindowFeature(long featureId) {
        getSherlock().requestFeature((int)featureId);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Progress Indication
    ///////////////////////////////////////////////////////////////////////////

    public void setSupportProgress(int progress) {
        getSherlock().setProgress(progress);
    }

    public void setSupportProgressBarIndeterminate(boolean indeterminate) {
        getSherlock().setProgressBarIndeterminate(indeterminate);
    }

    public void setSupportProgressBarIndeterminateVisibility(boolean visible) {
        getSherlock().setProgressBarIndeterminateVisibility(visible);
    }

    public void setSupportProgressBarVisibility(boolean visible) {
        getSherlock().setProgressBarVisibility(visible);
    }

    public void setSupportSecondaryProgress(int secondaryProgress) {
        getSherlock().setSecondaryProgress(secondaryProgress);
    }
}
