package com.actionbarsherlock.app;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window.Callback;

import com.actionbarsherlock.R;
import com.actionbarsherlock.internal.app.ActionBarImpl;
import com.actionbarsherlock.internal.view.menu.MenuBuilder;
import com.actionbarsherlock.internal.view.menu.MenuItemImpl;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class SherlockActivity extends FragmentActivity {
    private static final String TAG = "SherlockActivity";
    private static final boolean DEBUG = true;

    private static final int WINDOW_FLAG_ACTION_BAR = 1 << Window.FEATURE_ACTION_BAR;
    private static final int WINDOW_FLAG_ACTION_BAR_OVERLAY = 1 << Window.FEATURE_ACTION_BAR_OVERLAY;
    private static final int WINDOW_FLAG_ACTION_MODE_OVERLAY = 1 << Window.FEATURE_ACTION_MODE_OVERLAY;
    private static final int WINDOW_FLAG_INDETERMINANTE_PROGRESS = 1 << Window.FEATURE_INDETERMINATE_PROGRESS;
    private static final int WINDOW_FLAG_SPLIT_ACTION_BAR = 1 << Window.FEATURE_SPLIT_ACTION_BAR_WHEN_NARROW;
    
    private static final int MANIFEST_UI_OPTION_SPLIT_ACTION_BAR = 0x01;
    
    
    ViewGroup mContentParent;
    
    ActionBarImpl mActionBar;
    boolean mIsActionBarImplAttached;
    long mWindowFlags;

    MenuBuilder mMenu;
    final MenuBuilder.Callback mMenuCallback = new MenuBuilder.Callback() {
        @Override
        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
            return SherlockActivity.this.onMenuItemSelected(Window.FEATURE_OPTIONS_PANEL, item);
        }
    };

    boolean mOptionsMenuInvalidated;
    boolean mOptionsMenuCreateResult;
    
    final List<WeakReference<Fragment>> mSherlockFragments = new ArrayList<WeakReference<Fragment>>();
    


	///////////////////////////////////////////////////////////////////////////
	// Handle menu item creation
	///////////////////////////////////////////////////////////////////////////
    
    /**
     * TODO this
     * 
     * @return
     */
    public MenuInflater getSupportMenuInflater() {
        //Use our custom menu inflater
        return new MenuInflater(this);
    }

    /**
     * @deprecated Use {@link invalidateOptionsMenu}.
     */
    @Deprecated
    void supportInvalidateOptionsMenu() {
        invalidateOptionsMenu();
    }
    
    public void invalidateOptionsMenu() {
        if (DEBUG) Log.d(TAG, "supportInvalidateOptionsMenu(): Invalidating menu.");

        mMenu = new MenuBuilder(this);
        mMenu.setCallback(mMenuCallback);

        mOptionsMenuCreateResult = dispatchCreateOptionsMenu(mMenu);

        if (mActionBar != null) {
            dispatchPrepareOptionsMenu(mMenu);

            //Since we now know we are using a custom action bar, perform the
            //inflation callback to allow it to display any items it wants.
            mActionBar.onMenuInflated(mMenu);
        }

        // Whoops, older platform...  we'll use a hack, to manually rebuild
        // the options menu the next time it is prepared.
        mOptionsMenuInvalidated = true;
    }
    
    private boolean dispatchCreateOptionsMenu(Menu menu) {
    	if (!onCreateOptionsMenu(menu)) {
    		return false;
    	}
    	final MenuInflater inflater = getSupportMenuInflater();
    	for (WeakReference<Fragment> fragment : mSherlockFragments) {
    		Fragment sherlockFragment = fragment.get();
    		if ((sherlockFragment != null)
    				&& (sherlockFragment instanceof OnCreateOptionsMenuListener)) {
    		    ((OnCreateOptionsMenuListener)sherlockFragment).onCreateOptionsMenu(menu, inflater);
    		}
    	}
    	return true;
    }
    
    @Override
    public final boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Prior to Honeycomb, the framework can't invalidate the options
        // menu, so we must always say we have one in case the app later
        // invalidates it and needs to have it shown.
    	return true;
    }

	/**
	 * TODO this
	 * 
	 * @param item
	 * @return
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
	    return true;
	}
    
    private boolean dispatchPrepareOptionsMenu(Menu menu) {
    	if (!onPrepareOptionsMenu(menu)) {
    	    return false;
    	}
	    for (WeakReference<Fragment> fragment : mSherlockFragments) {
	    	Fragment sherlockFragment = fragment.get();
	    	if ((sherlockFragment != null)
	    			&& (sherlockFragment instanceof OnPrepareOptionsMenuListener)) {
	    		((OnPrepareOptionsMenuListener)sherlockFragment).onPrepareOptionsMenu(menu);
	    	}
	    }
	    return true;
    }
	
	@Override
	public final boolean onPrepareOptionsMenu(android.view.Menu menu) {
        if (DEBUG) {
            Log.d(TAG, "onPrepareOptionsMenu(android.view.Menu): mOptionsMenuCreateResult = " + mOptionsMenuCreateResult);
            Log.d(TAG, "onPrepareOptionsMenu(android.view.Menu): mOptionsMenuInvalidated = " + mOptionsMenuInvalidated);
        }

        boolean prepareResult = true;
        if (mOptionsMenuCreateResult) {
            if (DEBUG) Log.d(TAG, "onPrepareOptionsMenu(android.view.Menu): Calling support method with custom menu.");
            prepareResult = dispatchPrepareOptionsMenu(mMenu);
        }

        if (mOptionsMenuInvalidated) {
            if (DEBUG) Log.d(TAG, "onPrepareOptionsMenu(android.view.Menu): Clearing existing options menu.");
            menu.clear();
            mOptionsMenuInvalidated = false;

            if (mOptionsMenuCreateResult && prepareResult) {
                if (DEBUG) Log.d(TAG, "onPrepareOptionsMenu(android.view.Menu): Adding any action items that are not displayed on the action bar.");
                //Only add items that have not already been added to our custom
                //action bar implementation
                //TODO change to overflow adapter
                for (MenuItemImpl item : mMenu.getItems()) {
                    if (!item.isShownOnActionBar()) {
                        item.addTo(menu);
                    }
                }
            }
        }

        return mOptionsMenuCreateResult && prepareResult && menu.hasVisibleItems();
	}

	/**
	 * TODO this
	 * 
	 * @param item
	 * @return
	 */
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////
	// Handle menu item selection
	///////////////////////////////////////////////////////////////////////////

    /*
     * Called when a native menu item has been selected. If it was the options
     * menu then dispatch to the normal action item callback since it was then
     * part of the overflow menu.
     */
	@Override
	public final boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
		if (featureId == Window.FEATURE_OPTIONS_PANEL) {
			return onMenuItemSelected(featureId, mMenu.findItem(item.getItemId()));
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	/*
	 * Dispatch menu item selection to activity and then fragments.
	 * 
	 * This is called directly when an action item has been selected and should
	 * also be called when a native-menu overflow item has been selected.
	 */
    public final boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (featureId == Window.FEATURE_OPTIONS_PANEL) {
		    return dispatchOptionsItemSelected(item);
		}
		throw new RuntimeException("Unhandled feature id with support menu " + featureId);
	}
	
	/**
	 * Dispatch a menu item selection to the activity and its fragments.
	 * 
	 * @param item Selected menu item.
	 * @return Boolean indicating whether the selection was handled.
	 */
    private boolean dispatchOptionsItemSelected(MenuItem item) {
    	if (onOptionsItemSelected(item)) {
    		return true;
    	}
    	for (WeakReference<Fragment> fragment : mSherlockFragments) {
    		Fragment sherlockFragment = fragment.get();
    		if ((sherlockFragment != null)
    				&& (sherlockFragment instanceof OnOptionsItemSelectedListener)
    				&& ((OnOptionsItemSelectedListener)sherlockFragment).onOptionsItemSelected(item)) {
    			return true;
    		}
    	}
    	return false;
    }
	
	/*
	 * This should never be called since we capture any option item selection
	 * in the native onMenuItemSelected callback.
	 */
	@Override
	public final boolean onOptionsItemSelected(android.view.MenuItem item) {
		throw new RuntimeException("How did you get here?");
	}

	/**
	 * TODO this
	 * 
	 * @param item
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

	

	///////////////////////////////////////////////////////////////////////////
	// Action Bar
	///////////////////////////////////////////////////////////////////////////

    /**
     * Retrieve a reference to this activity's action bar handler.
     *
     * @return The handler for the appropriate action bar, or null.
     */
    public ActionBar getSupportActionBar() {
        return mActionBar;
    }
    
    private void ensureSupportActionBarAttached() {
        if (mIsActionBarImplAttached) {
            return;
        }
        if (!isChild()) {
        	final boolean hasActionBar = ((mWindowFlags & WINDOW_FLAG_ACTION_BAR) != 0);
            if (hasActionBar) {
            	final boolean hasSplitActionBar = hasSplitActionBar();
            	final boolean hasActionBarOverlay = ((mWindowFlags & WINDOW_FLAG_ACTION_BAR_OVERLAY) != 0);
            	
                if (hasActionBarOverlay) {
                    super.setContentView(R.layout.abs__screen_action_bar_overlay);
                } else {
                    super.setContentView(R.layout.abs__screen_action_bar);
                }
                
                //Flip the IDs. Very volatile!
                final View oldContent = getWindow().getDecorView().findViewById(android.R.id.content);
                mContentParent = (ViewGroup)findViewById(R.id.abs__content);
                oldContent.setId(R.id.abs__content);
                mContentParent.setId(android.R.id.content);

                mActionBar = new ActionBarImpl(this);
                mActionBar.init();
            } else {
            	final boolean hasIndeterminateProgress = ((mWindowFlags & WINDOW_FLAG_INDETERMINANTE_PROGRESS) != 0);
                if (hasIndeterminateProgress) {
                    super.requestWindowFeature((int)Window.FEATURE_INDETERMINATE_PROGRESS);
                }
                super.setContentView(R.layout.abs__screen_simple);
            }
        }

        invalidateOptionsMenu();
        mIsActionBarImplAttached = true;
    }
    
	private boolean hasSplitActionBar() {
		//Check if it was set via the window flags
		if ((mWindowFlags & WINDOW_FLAG_SPLIT_ACTION_BAR) != 0) {
			//If so, GTFO!
			return true;
		}
		
		//Try parsing the AndroidManifest.xml for the value
		try {
			final String thisPackage = getClass().getName();
			if (DEBUG) Log.i(TAG, "Parsing AndroidManifest.xml for " + thisPackage);
			
			final String packageName = getApplicationInfo().packageName;
			final AssetManager am = createPackageContext(packageName, 0).getAssets();
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
								//Found the attribute, return its value
								boolean result = (xml.getAttributeUnsignedIntValue(i, 0) == MANIFEST_UI_OPTION_SPLIT_ACTION_BAR);
								if (DEBUG) Log.i(TAG, "Returning " + result);
								return result;
							}
						}
					} else if ("activity".equals(name)) {
						//Check if the <activity> is us and has the attribute
						if (DEBUG) Log.d(TAG, "Got <activity>");
						Integer uiOptions = null;
						String activityPackage = null;
						
						for (int i = xml.getAttributeCount() - 1; i >= 0; i--) {
							if (DEBUG) Log.d(TAG, xml.getAttributeName(i) + ": " + xml.getAttributeValue(i));
							
							//We need both uiOptions and name attributes
							String attrName = xml.getAttributeName(i);
							if ("uiOptions".equals(attrName)) {
								uiOptions = xml.getAttributeUnsignedIntValue(i, 0);
							} else if ("name".equals(attrName)) {
								activityPackage = packageName + xml.getAttributeValue(i);
							}
							
							//If we have both attributes before processing
							if ((uiOptions != null) && (activityPackage != null)) {
								//Make sure we're the right entry for this class
								if (thisPackage.equals(activityPackage)) {
									boolean result = (uiOptions.intValue() == MANIFEST_UI_OPTION_SPLIT_ACTION_BAR);
									if (DEBUG) Log.i(TAG, "Returning " + result);
									return result;
								}
								
								//Skip to next
								break;
							}
						}
					}
				}
				eventType = xml.nextToken();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (DEBUG) Log.i(TAG, "Returning false");
		return false;
	}

    @Override
    public void onPanelClosed(int featureId, android.view.Menu menu) {
        super.onPanelClosed(featureId, menu);
        
        if ((featureId == Window.FEATURE_OPTIONS_PANEL) && (mActionBar != null)) {
            if (DEBUG) Log.d(TAG, "onPanelClosed(int, android.view.Menu): Dispatch menu visibility false to custom action bar.");
            mActionBar.onMenuVisibilityChanged(false);
        }
    }

    @Override
	public boolean onMenuOpened(int featureId, android.view.Menu menu) {
    	if ((featureId == Window.FEATURE_OPTIONS_PANEL) && (mActionBar != null)) {
            if (DEBUG) Log.d(TAG, "onMenuOpened(int, android.view.Menu): Dispatch menu visibility true to custom action bar.");
    		mActionBar.onMenuVisibilityChanged(true);
    	}
    	
		return super.onMenuOpened(featureId, menu);
	}
    
    

	

	///////////////////////////////////////////////////////////////////////////
	// Action Mode
	///////////////////////////////////////////////////////////////////////////

    /**
     * Start an action mode.
     *
     * @param callback Callback that will manage lifecycle events for this
     * context mode
     * @return The ContextMode that was started, or null if it was cancelled
     * @see com.actionbarsherlock.view.ActionMode
     */
    public final ActionMode startActionMode(final ActionMode.Callback callback) {
        //Give the activity override a chance to handle the action mode
        ActionMode actionMode = onWindowStartingActionMode(callback);

        if (actionMode == null) {
            //If the activity did not handle, send to action bar for platform-
            //specific implementation
            //TODO
        	throw new RuntimeException("Not implemented");
        }
        if (actionMode != null) {
            //Send the activity callback that our action mode was started
            onActionModeStarted(actionMode);
        }

        //Return to the caller
        return actionMode;
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
     * Notifies the activity that an action mode has finished. Activity
     * subclasses overriding this method should call the superclass
     * implementation.
     *
     * @param mode The action mode that just finished.
     */
    public void onActionModeFinished(ActionMode mode) {
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
     * Enable extended window features.
     *
     * @param featureId The desired feature as defined in
     * {@link com.actionbarsherlock.view.Window}.
     * @return Returns {@code true} if the requested feature is supported and
     * now enabled.
     */
    public boolean requestWindowFeature(long featureId) {
        switch ((int)featureId) {
            case (int)Window.FEATURE_ACTION_BAR:
            case (int)Window.FEATURE_ACTION_BAR_OVERLAY:
            case (int)Window.FEATURE_ACTION_MODE_OVERLAY:
            case (int)Window.FEATURE_INDETERMINATE_PROGRESS:
            case (int)Window.FEATURE_PROGRESS:
            case (int)Window.FEATURE_SPLIT_ACTION_BAR_WHEN_NARROW:
                mWindowFlags |= (1 << featureId);
                super.requestWindowFeature((int)featureId);
                return true;
        }
        return super.requestWindowFeature((int)featureId);
    }
    


    
    
    
    

    @Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);
		mSherlockFragments.add(new WeakReference<Fragment>(fragment));
	}

    @Override
    public void setContentView(int layoutResId) {
    	if (mContentParent == null) {
    		ensureSupportActionBarAttached();
    	} else {
    		mContentParent.removeAllViews();
    	}
    	getLayoutInflater().inflate(layoutResId, mContentParent);
    	
    	Callback cb = getWindow().getCallback();
    	if (cb != null) {
    		cb.onContentChanged();
    	}
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
    	if (mContentParent == null) {
    		ensureSupportActionBarAttached();
    	} else {
    		mContentParent.removeAllViews();
    	}
    	mContentParent.addView(view, params);
    	
    	Callback cb = getWindow().getCallback();
    	if (cb != null) {
    		cb.onContentChanged();
    	}
    }

    @Override
    public void setContentView(View view) {
    	if (mContentParent == null) {
    		ensureSupportActionBarAttached();
    	} else {
    		mContentParent.removeAllViews();
    	}
    	mContentParent.addView(view);
    	
    	Callback cb = getWindow().getCallback();
    	if (cb != null) {
    		cb.onContentChanged();
    	}
    }

    @Override
    public void setTitle(CharSequence title) {
        if (mActionBar != null) {
            mActionBar.setTitle(title);
        }
        super.setTitle(title);
    }

    @Override
    public void setTitle(int titleId) {
        if (mActionBar != null) {
            mActionBar.setTitle(titleId);
        }
        super.setTitle(titleId);
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
    

    @Override
    protected void onApplyThemeResource(Theme theme, int resid, boolean first) {
        super.onApplyThemeResource(theme, resid, first);
        TypedArray attrs = theme.obtainStyledAttributes(resid, R.styleable.SherlockTheme);

        final boolean actionBar = attrs.getBoolean(R.styleable.SherlockTheme_windowActionBar, false);
        mWindowFlags |= actionBar ? WINDOW_FLAG_ACTION_BAR : 0;

        final boolean actionModeOverlay = attrs.getBoolean(R.styleable.SherlockTheme_windowActionModeOverlay, false);
        mWindowFlags |= actionModeOverlay ? WINDOW_FLAG_ACTION_MODE_OVERLAY : 0;

        attrs.recycle();
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
    public void setProgressBarIndeterminateVisibility(Boolean visible) {
        if (mActionBar == null) {
            super.setProgressBarIndeterminateVisibility(visible);
        } else {
            mActionBar.setProgressBarIndeterminateVisibility(visible);
        }
    }
    



}
