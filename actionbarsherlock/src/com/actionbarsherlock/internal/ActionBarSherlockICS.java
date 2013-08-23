package com.actionbarsherlock.internal;

import android.app.Activity;
import android.util.Log;
import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.internal.app.ActionBarWrapperICS;

@ActionBarSherlock.Implementation(api = 14)
public class ActionBarSherlockICS extends ActionBarSherlockNative {
    private ActionBarWrapperICS mActionBar;

    public ActionBarSherlockICS(Activity activity, int flags) {
        super(activity, flags);
    }

    @Override
    protected ActionBar initActionBar() {
        if (ActionBarSherlock.DEBUG) Log.d(TAG, "[initActionBar]");

        if (mActionBar == null && mActivity.getActionBar() != null) {
            mActionBar = new ActionBarWrapperICS(mActivity);
        }
        return mActionBar;
    }
}
