package com.actionbarsherlock.view;

import android.graphics.drawable.Drawable;
import android.view.View;

public interface SubMenu extends Menu {
	void clearHeader();
	
    MenuItem getItem();

    SubMenu setHeaderIcon(Drawable icon);

    SubMenu setHeaderIcon(int iconRes);

    SubMenu setHeaderTitle(CharSequence title);

    SubMenu setHeaderTitle(int titleRes);

    SubMenu setHeaderView(View view);

    SubMenu setIcon(Drawable icon);

    SubMenu setIcon(int iconRes);
}
