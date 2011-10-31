package android.support.v4.app;

import android.app.Activity;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.ActionMode;
import android.support.v4.view.Window;

import com.actionbarsherlock.R;
import com.actionbarsherlock.internal.app.ActionBarImpl;
import com.actionbarsherlock.internal.app.ActionBarWrapper;
import com.actionbarsherlock.internal.view.menu.MenuBuilder;

/**
 * This is a base class for ActionBar to work with Activity and SupportActivity.
 * @author Jonathan Steele
 */
public class ActionBarBaseClass
{
    public static final boolean IS_HONEYCOMB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

    public static final int WINDOW_FLAG_ACTION_BAR = 1 << Window.FEATURE_ACTION_BAR;
    public static final int WINDOW_FLAG_ACTION_BAR_ITEM_TEXT = 1 << Window.FEATURE_ACTION_BAR_ITEM_TEXT;
    public static final int WINDOW_FLAG_ACTION_BAR_OVERLAY = 1 << Window.FEATURE_ACTION_BAR_OVERLAY;
    public static final int WINDOW_FLAG_ACTION_MODE_OVERLAY = 1 << Window.FEATURE_ACTION_MODE_OVERLAY;
    public static final int WINDOW_FLAG_INDETERMINANTE_PROGRESS = 1 << Window.FEATURE_INDETERMINATE_PROGRESS;

    private SupportActivity mActivity;

    final MenuBuilder mSupportMenu;

    ActionBar mActionBar;
    int mWindowFlags;

    public <T extends Activity & SupportActivity> ActionBarBaseClass(T activity, MenuBuilder.Callback callback)
    {
    	mActivity = activity;

    	if (IS_HONEYCOMB) {
            mActionBar = ActionBarWrapper.createFor(activity);
            mSupportMenu = null; //Everything should be done natively
        }
        else {
            mSupportMenu = new MenuBuilder(activity);
            mSupportMenu.setCallback(callback);
        }
    }

    public ActionBar getActionBarInstance()
    {
    	return (mActionBar != null) ? mActionBar.getPublicInstance() : null;
    }

    public ActionBarImpl getActionBarImpl()
    {
        return (ActionBarImpl) mActionBar;
    }

    public void onApplyThemeResource(Theme theme, int resid)
    {
        final TypedArray attrs = theme.obtainStyledAttributes(resid, R.styleable.SherlockTheme);

        final boolean actionBar = attrs.getBoolean(R.styleable.SherlockTheme_windowActionBar, false);
        mWindowFlags |= actionBar ? WINDOW_FLAG_ACTION_BAR : 0;

        final boolean actionModeOverlay = attrs.getBoolean(R.styleable.SherlockTheme_windowActionModeOverlay, false);
        mWindowFlags |= actionModeOverlay ? WINDOW_FLAG_ACTION_MODE_OVERLAY : 0;

        attrs.recycle();
    }

    public boolean requestWindowFeature(int featureId)
    {
        switch (featureId) {
        case (int) Window.FEATURE_ACTION_BAR:
        case (int) Window.FEATURE_ACTION_BAR_ITEM_TEXT:
        case (int) Window.FEATURE_ACTION_BAR_OVERLAY:
        case (int) Window.FEATURE_ACTION_MODE_OVERLAY:
        case (int) Window.FEATURE_INDETERMINATE_PROGRESS:
            mWindowFlags |= 1 << featureId;
            return true;
        default:
            return false;
        }
    }

    public ActionMode startActionMode(ActionMode.Callback callback)
    {
        return mActionBar.startActionMode(callback);
    }

    public void setProgressBarIndeterminateVisibility(Boolean visible)
    {
        if (IS_HONEYCOMB || (getActionBarInstance() == null)) {
            mActivity.setProgressBarIndeterminateVisibility(visible);
        }
        else if (isWindowsFeatureEnabled(WINDOW_FLAG_INDETERMINANTE_PROGRESS)) {
            getActionBarImpl().setProgressBarIndeterminateVisibility(visible);
        }
    }

    public boolean isWindowsFeatureEnabled(int windowFlag)
    {
        return (mWindowFlags & windowFlag) == windowFlag;
    }
}