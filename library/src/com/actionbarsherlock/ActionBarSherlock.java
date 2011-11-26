package com.actionbarsherlock;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.accessibility.AccessibilityEvent;
import android.widget.PopupWindow;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.internal.app.ActionBarImpl;
import com.actionbarsherlock.internal.view.StandaloneActionMode;
import com.actionbarsherlock.internal.view.menu.MenuBuilder;
import com.actionbarsherlock.internal.view.menu.MenuPresenter;
import com.actionbarsherlock.internal.widget.ActionBarContainer;
import com.actionbarsherlock.internal.widget.ActionBarContextView;
import com.actionbarsherlock.internal.widget.ActionBarView;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * Helper class which manages interaction with the custom ICS action bar
 * implementation.
 * 
 * @author Jake Wharton <jakewharton@gmail.com>
 * @version 4.0.0
 */
public final class ActionBarSherlock {
	private static final String TAG = "ActionBarSherlock";
	private static final boolean DEBUG = true;

    protected static final int DEFAULT_FEATURES = (1 << Window.FEATURE_ACTION_BAR);
    
    
    
    public interface OnCreateOptionsMenuListener {
    	public boolean onCreateOptionsMenu(Menu menu);
    }
    public interface OnOptionsItemSelectedListener {
    	public boolean onOptionsItemSelected(MenuItem item);
    }
    public interface OnPrepareOptionsMenuListener {
    	public boolean onPrepareOptionsMenu(Menu menu);
    }
    public interface OnActionModeFinishedListener {
    	public void onActionModeFinished(ActionMode mode);
    }
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
    
    
    
    private final Activity mActivity;
    private final boolean mIsDelegate;
    private final boolean mHasMenuKey;
    
    private ViewGroup mDecor;
    private ViewGroup mContentParent;
    
    private ActionBarImpl mActionBar;
    private ActionBarView mActionBarView;
    private int mFeatures = DEFAULT_FEATURES;
    private int mUiOptions = 0;
    
    private ActionMode mActionMode;
    private ActionBarContextView mActionModeView;
    private PopupWindow mActionModePopup;
    private Runnable mShowActionModePopup;
    
    private boolean mIsTitleReady = false;

    private MenuInflater mMenuInflater;
    private MenuBuilder mMenu;
    private HashMap<android.view.MenuItem, MenuItem> mNativeItemMap;
    private boolean mLastCreateResult;
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
			final MenuItem sherlockItem = mNativeItemMap.get(item);
			if (sherlockItem != null) {
				return mMenuBuilderCallback.onMenuItemSelected(mMenu, sherlockItem);
			}
			return false;
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
        
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        	mHasMenuKey = true;
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        	mHasMenuKey = false;
        } else {
        	mHasMenuKey = HasPermanentMenuKey.invoke(mActivity);
        }
    }
    
    /* For Android 1.6 */
    private static final class HasPermanentMenuKey {
    	public static boolean invoke(Context context) {
    		return ViewConfiguration.get(context).hasPermanentMenuKey();
    	}
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
        
        mActionBar = new ActionBarImpl(mActivity);
        
        if (!mIsDelegate) {
        	//We may never get another chance to set the title
        	mActionBar.setTitle(mActivity.getTitle());
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle and interaction callbacks
    ///////////////////////////////////////////////////////////////////////////
    
    public void dispatchConfigurationChanged(Configuration newConfig) {
    	if (DEBUG) Log.d(TAG, "[dispatchConfigurationChanged] newConfig: " + newConfig);
    	
    	if (mActionBar != null) {
    		mActionBar.onConfigurationChanged(newConfig);
    	}
    }
    
    public void dispatchPostResume() {
    	if (DEBUG) Log.d(TAG, "[dispatchPostResume]");
    	
    	if (mActionBar != null) {
    		mActionBar.setShowHideAnimationEnabled(true);
    	}
    }
    
    public void dispatchStop() {
    	if (DEBUG) Log.d(TAG, "[dispatchStop]");
    	
    	if (mActionBar != null) {
    		mActionBar.setShowHideAnimationEnabled(false);
    	}
    }
    
    public void dispatchInvalidateOptionsMenu() {
    	if (DEBUG) Log.d(TAG, "[dispatchInvalidateOptionsMenu]");
    	
    	if (mMenu == null) {
    		//TODO honor actionBarWidgetTheme attribute
    		mMenu = new MenuBuilder(mActivity);
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
    
    public boolean dispatchOpenOptionsMenu() {
    	if (DEBUG) Log.d(TAG, "[dispatchOpenOptionsMenu]");
    	
    	if (mHasMenuKey) {
    		return false;
    	}
    	
    	return mActionBarView.showOverflowMenu();
    }
    
    public boolean dispatchCloseOptionsMenu() {
    	if (DEBUG) Log.d(TAG, "[dispatchCloseOptionsMenu]");
    	
    	if (mHasMenuKey) {
    		return false;
    	}
    	
    	return mActionBarView.hideOverflowMenu();
    }
    
    public void dispatchPostCreate(Bundle savedInstanceState) {
    	if (DEBUG) Log.d(TAG, "[dispatchOnPostCreate]");
    	
    	if (mIsDelegate) {
    		mIsTitleReady = true;
    	}
    }
    
    public void dispatchTitleChanged(CharSequence title, int color) {
    	if (DEBUG) Log.d(TAG, "[dispatchTitleChanged] title: " + title + ", color: " + color);
    	
    	if (mIsDelegate && !mIsTitleReady) {
    		return;
    	}
    	if (mActionBar != null) {
    		mActionBar.setTitle(title);
    	}
    }
    
    private boolean dispatchCreateOptionsMenu() {
    	if (DEBUG) Log.d(TAG, "[dispatchCreateOptionsMenu]");
    	
    	mLastCreateResult = false;
    	if (mActivity instanceof OnCreateOptionsMenuListener) {
    		OnCreateOptionsMenuListener listener = (OnCreateOptionsMenuListener)mActivity;
    		mLastCreateResult = listener.onCreateOptionsMenu(mMenu);
    	}
    	return mLastCreateResult;
    }
    
    private boolean dispatchPrepareOptionsMenu() {
    	if (DEBUG) Log.d(TAG, "[dispatchPrepareOptionsMenu]");
    	
    	mLastPrepareResult = false;
    	if (mActivity instanceof OnPrepareOptionsMenuListener) {
    		OnPrepareOptionsMenuListener listener = (OnPrepareOptionsMenuListener)mActivity;
    		mLastPrepareResult = listener.onPrepareOptionsMenu(mMenu);
    	}
    	return mLastPrepareResult;
    }
    
    public boolean dispatchPrepareOptionsMenu(android.view.Menu menu) {
    	if (DEBUG) Log.d(TAG, "[dispatchPrepareOptionsMenu] android.view.Menu: " + menu);
    	
    	if (!mHasMenuKey) {
    		return true;
    	}
    	//TODO bind to native menu as overflow
    	return false;
    }
    
    private boolean dispatchOptionsItemSelected(MenuItem item) {
    	if (DEBUG) Log.d(TAG, "[dispatchOptionsItemSelected] item: " + item);
    	
    	if (mActivity instanceof OnOptionsItemSelectedListener) {
    		OnOptionsItemSelectedListener listener = (OnOptionsItemSelectedListener)mActivity;
    		return listener.onOptionsItemSelected(item);
    	}
    	return false;
    }
    
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
    
    public void dispatchPanelClosed(int featureId, android.view.Menu menu){
    	if (DEBUG) Log.d(TAG, "[dispatchPanelClosed] featureId: " + featureId + ", menu: " + menu);
    	
    	if (featureId == Window.FEATURE_ACTION_BAR || featureId == Window.FEATURE_OPTIONS_PANEL) {
    		if (mActionBar != null) {
    			mActionBar.dispatchMenuVisibilityChanged(false);
    		}
    	}
    }
    
    
    
    public int getFeatures() {
    	if (DEBUG) Log.d(TAG, "[getFeatures]");
    	
        return mFeatures;
    }
    
    public boolean hasFeature(int featureId) {
    	if (DEBUG) Log.d(TAG, "[hasFeature] featureId: " + featureId);
    	
        return (mFeatures & (1 << featureId)) != 0;
    }
    
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

    public void setUiOptions(int uiOptions) {
    	if (DEBUG) Log.d(TAG, "[setUiOptions] uiOptions: " + uiOptions);
    	
        mUiOptions = uiOptions;
    }
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
     * @param params Layout paramaters to apply to the view.
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
        		
        		boolean splitActionBar = false;
        		final boolean splitWhenNattow = (mUiOptions & ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW) != 0;
        		if (splitWhenNattow) {
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
        			mActionBarView.setSplitWhenNarrow(splitWhenNattow);
        			
        			final ActionBarContextView cab = (ActionBarContextView)mDecor.findViewById(R.id.abs__action_context_bar);
        			cab.setSplitView(splitView);
        			cab.setSplitActionBar(splitActionBar);
        			cab.setSplitWhenNarrow(splitWhenNattow);
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

        /* TODO if (hasFeature(Window.FEATURE_INDETERMINATE_PROGRESS)) {
            ProgressBar progress = getCircularProgressBar(false);
            if (progress != null) {
                progress.setIndeterminate(true);
            }
		*/
        
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
    
    public ActionMode startActionMode(ActionMode.Callback callback) {
    	if (mActionMode != null) {
    		mActionMode.finish();
    	}

        final ActionMode.Callback wrappedCallback = new ActionModeCallbackWrapper(callback);
        ActionMode mode = null;
        
    	if (mActionModeView == null) {
    		ViewStub stub = (ViewStub)mActivity.findViewById(R.id.abs__action_mode_bar_stub);
    		if (stub != null) {
    			mActionModeView = (ActionBarContextView)stub.inflate();
    		}
    	}
    	if (mActionModeView != null) {
    		mActionModeView.killMode();
    		mode = new StandaloneActionMode(mActivity, mActionModeView, wrappedCallback, mActionModePopup == null);
            if (callback.onCreateActionMode(mode, mode.getMenu())) {
                mode.invalidate();
                mActionModeView.initForMode(mode);
                mActionModeView.setVisibility(View.VISIBLE);
                mActionMode = mode;
                if (mActionModePopup != null) {
                    mDecor.post(mShowActionModePopup);
                }
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
            if (mActionModePopup != null) {
                mDecor.removeCallbacks(mShowActionModePopup);
                mActionModePopup.dismiss();
            } else if (mActionModeView != null) {
                mActionModeView.setVisibility(View.GONE);
            }
            if (mActionModeView != null) {
                mActionModeView.removeAllViews();
            }
            if (mActivity instanceof OnActionModeFinishedListener) {
            	((OnActionModeFinishedListener)mActivity).onActionModeFinished(mActionMode);
            }
            mActionMode = null;
        }
    }
}
