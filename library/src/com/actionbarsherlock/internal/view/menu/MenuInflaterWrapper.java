package com.actionbarsherlock.internal.view.menu;

import android.content.Context;
import android.view.ContextMenu;

public final class MenuInflaterWrapper extends android.view.MenuInflater {
    private final android.view.MenuInflater mMenuInflater;

    public MenuInflaterWrapper(Context context, android.view.MenuInflater menuInflater) {
        super(context);
        mMenuInflater = menuInflater;
    }

    @Override
    public void inflate(int menuRes, android.view.Menu menu) {
        if (menu instanceof ContextMenu) {
            mMenuInflater.inflate(menuRes, menu);
        } else {
            mMenuInflater.inflate(menuRes, ((MenuWrapper)menu).unwrap());
        }
    }
}
