package com.actionbarsherlock.internal.policy.impl;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.Window.Callback;

import com.actionbarsherlock.R;
import com.actionbarsherlock.internal.widget.ActionBarContainer;
import com.actionbarsherlock.internal.widget.ActionBarContextView;
import com.actionbarsherlock.internal.widget.ActionBarView;
import com.actionbarsherlock.view.MenuItem;

public class SherlockWindow {
    private static final String TAG = "SherlockWindow";
    
    protected static final int DEFAULT_FEATURES = (1 << Window.FEATURE_ACTION_BAR);
    
    private final Window mRealWindow;
    
    private com.actionbarsherlock.view.Window.Callback mCallback;
    
    private ViewGroup mDecor;
    private ViewGroup mContentParent;
    
    private ActionBarView mActionBar;

    private CharSequence mTitle = null;
    
    private int mFeatures = DEFAULT_FEATURES;
    private int mUiOptions = 0;
    
    public SherlockWindow(Window realWindow) {
        mRealWindow = realWindow;
    }


    public void setUiOptions(int uiOptions) {
        mUiOptions = uiOptions;
    }

    public void setUiOptions(int uiOptions, int mask) {
        mUiOptions = (mUiOptions & ~mask) | (uiOptions & mask);
    }
    
    public com.actionbarsherlock.view.Window.Callback getCallback() {
    	if (mCallback == null) {
    		mCallback = new com.actionbarsherlock.view.Window.Callback() {
    			@Override
    			public boolean onMenuItemSelected(int featureId, MenuItem item) {
    				return false; //Default impl
    			}
    		};
    	}
    	return mCallback;
    }
    
    public void setCallback(com.actionbarsherlock.view.Window.Callback callback) {
    	mCallback = callback;
        if (mActionBar != null) {
            mActionBar.setWindowCallback(mCallback);
        }
    }

    /**
     * Enable extended screen features.  This must be called before
     * setContentView().  May be called as many times as desired as long as it
     * is before setContentView().  If not called, no extended features
     * will be available.  You can not turn off a feature once it is requested.
     * You cannot use other title features with {@link #FEATURE_CUSTOM_TITLE}.
     *
     * @param featureId The desired features, defined as constants by Window.
     * @return The features that are now set.
     */
    public boolean requestFeature(int featureId) {
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
                
            case Window.FEATURE_LEFT_ICON:
            case Window.FEATURE_RIGHT_ICON:
                return false;
        }
        
        return mRealWindow.requestFeature(featureId);
    }

    /**
     * Return the feature bits that are enabled.  This is the set of features
     * that were given to requestFeature(), and are being handled by this
     * Window itself or its container.  That is, it is the set of
     * requested features that you can actually use.
     *
     * <p>To do: add a public version of this API that allows you to check for
     * features by their feature ID.
     *
     * @return int The feature bits.
     */
    protected final int getFeatures() {
        return mFeatures;
    }
    
    /**
     * Query for the availability of a certain feature.
     * 
     * @param feature The feature ID to check
     * @return true if the feature is enabled, false otherwise.
     */
    public boolean hasFeature(int feature) {
        return (getFeatures() & (1 << feature)) != 0;
    }
    
    public View getDecorView() {
        if (mDecor == null) {
            installDecor();
        }
        return mDecor;
    }
    
    public View peekDecorView() {
        return mDecor;
    }
    
    public void setContentView(int layoutResId) {
        if (mContentParent == null) {
            installDecor();
        } else {
            mContentParent.removeAllViews();
        }
        mRealWindow.getLayoutInflater().inflate(layoutResId, mContentParent);
        
        final Callback cb = mRealWindow.getCallback();
        if (cb != null) {
            cb.onContentChanged();
        }
    }
    
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (mContentParent == null) {
            installDecor();
        } else {
            mContentParent.removeAllViews();
        }
        mContentParent.addView(view, params);
        
        final Callback cb = mRealWindow.getCallback();
        if (cb != null) {
            cb.onContentChanged();
        }
    }
    
    public void setContentView(View view) {
        if (mContentParent == null) {
            installDecor();
        } else {
            mContentParent.removeAllViews();
        }
        mContentParent.addView(view);
        
        final Callback cb = mRealWindow.getCallback();
        if (cb != null) {
            cb.onContentChanged();
        }
    }
    
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        if (mContentParent == null) {
            installDecor();
        }
        mContentParent.addView(view, params);
        
        final Callback cb = mRealWindow.getCallback();
        if (cb != null) {
            cb.onContentChanged();
        }
    }

    public void setTitle(CharSequence title) {
        if (mActionBar != null) {
            mActionBar.setWindowTitle(title);
        }
        mTitle = title;
    }
    
    private void installDecor() {
        if (mDecor == null) {
            mDecor = (ViewGroup)mRealWindow.getDecorView().findViewById(android.R.id.content);
        }
        if (mContentParent == null) {
            mContentParent = generateLayout(mDecor);
            mActionBar = (ActionBarView) mDecor.findViewById(R.id.abs__action_bar);
            if (mActionBar != null) {
                mActionBar.setWindowCallback(getCallback());
                if (mActionBar.getTitle() == null) {
                    mActionBar.setWindowTitle(mTitle);
                }
                if (hasFeature(Window.FEATURE_PROGRESS)) {
                    mActionBar.initProgress();
                }
                if (hasFeature(Window.FEATURE_INDETERMINATE_PROGRESS)) {
                    mActionBar.initIndeterminateProgress();
                }

                boolean splitActionBar = false;
                final boolean splitWhenNarrow =
                        (mUiOptions & ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW) != 0;
                if (splitWhenNarrow) {
                    splitActionBar = mRealWindow.getContext().getResources().getBoolean(
                            R.bool.abs__split_action_bar_is_narrow);
                } else {
                    splitActionBar = mRealWindow.getContext().getTheme()
                    		.obtainStyledAttributes(R.styleable.SherlockTheme)
                    		.getBoolean(R.styleable.SherlockTheme_windowSplitActionBar, false);
                }
                final ActionBarContainer splitView = (ActionBarContainer) mDecor.findViewById(
                        R.id.abs__split_action_bar);
                if (splitView != null) {
                    mActionBar.setSplitView(splitView);
                    mActionBar.setSplitActionBar(splitActionBar);
                    mActionBar.setSplitWhenNarrow(splitWhenNarrow);

                    final ActionBarContextView cab = (ActionBarContextView) mDecor.findViewById(
                            R.id.abs__action_context_bar);
                    cab.setSplitView(splitView);
                    cab.setSplitActionBar(splitActionBar);
                    cab.setSplitWhenNarrow(splitWhenNarrow);
                } else if (splitActionBar) {
                    Log.e(TAG, "Requested split action bar with " +
                            "incompatible window decor! Ignoring request.");
                }

                // Post the panel invalidate for later; avoid application onCreateOptionsMenu
                // being called in the middle of onCreate or similar.
                mDecor.post(new Runnable() {
                    public void run() {
                        // Invalidate if the panel menu hasn't been created before this.
                        /* TODO PanelFeatureState st = getPanelState(Window.FEATURE_OPTIONS_PANEL, false);
                        if (!isDestroyed() && (st == null || st.menu == null)) {
                            invalidatePanelMenu(Window.FEATURE_ACTION_BAR);
                        }*/
                    }
                });
            }
        }
    }
    
    private ViewGroup generateLayout(ViewGroup decor) {
        // Apply data from current theme.

        TypedArray a = mRealWindow.getContext().getTheme().obtainStyledAttributes(R.styleable.SherlockTheme);

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
        
        View in = mRealWindow.getLayoutInflater().inflate(layoutResource, null);
        decor.addView(in, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        
        ViewGroup contentParent = (ViewGroup)decor.findViewById(R.id.abs__content);
        if (contentParent == null) {
            throw new RuntimeException("SherlockWindow couldn't find content container view");
        }
        
        //Make our new child the true content view (for fragments). VERY VOLATILE!
        mDecor.setId(View.NO_ID);
        contentParent.setId(android.R.id.content);

        /* TODO if (hasFeature(Window.FEATURE_INDETERMINATE_PROGRESS)) {
            ProgressBar progress = getCircularProgressBar(false);
            if (progress != null) {
                progress.setIndeterminate(true);
            }
        }*/

        if (mTitle != null) {
            setTitle(mTitle);
        }
        
        return contentParent;
    }
}
