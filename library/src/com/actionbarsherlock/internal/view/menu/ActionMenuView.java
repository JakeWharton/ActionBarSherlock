package com.actionbarsherlock.internal.view.menu;

import java.util.List;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.actionbarsherlock.R;

public class ActionMenuView extends LinearLayout implements MenuView, MenuBuilder.ItemInvoker {
	private int mMaxItems;
	private MenuBuilder mMenu;
	private int mWidthLimit;
	
	public ActionMenuView(Context context) {
		this(context, null);
	}
	public ActionMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mMaxItems = getMaxActionButtons();
		mWidthLimit = getResources().getDisplayMetrics().widthPixels / 2;
		
		setBaselineAligned(false);
	}
	
	/* XXX UNUSED?
	private boolean addItemView(boolean something, ActionMenuItemView itemView) {
		itemView.setItemInvoker(this);
		final boolean hasText = itemView.hasText();
		if (hasText && something) {
			addView(makeDividerView(), makeDividerLayoutParams());
		}
		addView(itemView);
		return hasText;
	}
	*/
	
	private int getMaxActionButtons() {
		return getResources().getInteger(R.integer.max_action_buttons);
	}
	
	private LinearLayout.LayoutParams makeActionViewLayoutParams(View view) {
		return generateLayoutParams(view.getLayoutParams());
	}
	
	private boolean removeChildrenUntil(int startAt, View paramView) {
		//TODO
		final int count = getChildCount();
		int index = startAt;
		while (index < count) {
			break;
		}
		
		return false;
	}
	
	@Override
	protected LinearLayout.LayoutParams generateDefaultLayoutParams() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.FILL_PARENT,
			LinearLayout.LayoutParams.FILL_PARENT
		);
		params.gravity = Gravity.CENTER_VERTICAL;
		return params;
	}
	
	@Override
	protected LinearLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams params) {
		if (params instanceof LinearLayout.LayoutParams) {
			LinearLayout.LayoutParams newParams = new LinearLayout.LayoutParams((LinearLayout.LayoutParams)params);
			if (newParams.gravity <= 0) {
				newParams.gravity = Gravity.CENTER_VERTICAL;
			}
			return newParams;
		}
		return generateDefaultLayoutParams();
	}

	@Override
	public int getWindowAnimations() {
		return 0;
	}

	@Override
	public void initialize(MenuBuilder menu, int menuType) {
		Log.d("ActionMenuView", "Initializing with " + menu.toString());
		menu.setActionWidthLimit(mWidthLimit);
		menu.setMaxActionItems(mMaxItems);
		if (mMenu != menu) {
			mMenu = menu;
			updateChildren(true);
		} else {
			updateChildren(false);
		}
	}
	
	@Override
	public boolean invokeItem(MenuItemImpl item) {
		return mMenu.performItemAction(item, 0);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		mMaxItems = getMaxActionButtons();
		mWidthLimit = getResources().getDisplayMetrics().widthPixels / 2;
		
		if (mMenu != null) {
			mMenu.setMaxActionItems(mMaxItems);
			updateChildren(false);
		}
	}

	@Override
	public void updateChildren(boolean cleared) {
		Log.d("ActionMenuView", "Updating children (" + Boolean.toString(cleared) + ")");
		List<MenuItemImpl> menuItems = mMenu.getActionItems(false);
		final int menuItemCount = menuItems.size();
		Log.d("ActionMenuView", "Got " + menuItemCount + " items out of " + mMenu.size());
		int viewIndex = 0;
		int menuItemIndex = 0;
		while (menuItemIndex < menuItemCount) {
			final MenuItemImpl menuItem = menuItems.get(menuItemIndex);

			View menuItemView = null;
			if (menuItem.getActionView() != null) {
				menuItemView = menuItem.getActionView();
				menuItemView.setLayoutParams(makeActionViewLayoutParams(menuItemView));
			} else {
				ActionMenuItemView actionMenuItem = (ActionMenuItemView)menuItem.getItemView(MenuBuilder.TYPE_ACTION_BAR, this);
				actionMenuItem.setItemInvoker(this);
				menuItemView = actionMenuItem;
			}
			
			removeChildrenUntil(viewIndex, menuItemView);
			if (getChildAt(viewIndex) != menuItemView) {
				addView(menuItemView, viewIndex);
			}
			viewIndex += 1;
			menuItemIndex += 1;
		}
	}
}