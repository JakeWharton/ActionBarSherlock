/*
 * Copyright (C) 2006 The Android Open Source Project
 *               2011 Jake Wharton
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

package android.support.v4.view;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

/**
 * <p>Interface for direct access to a previously created menu item.</p>
 * 
 * <p>An Item is returned by calling one of the {@link Menu#add(int)}
 * methods.</p>
 * 
 * <p>For a feature set of specific menu types, see {@link Menu}.</p>
 */
public class MenuItem implements android.view.MenuItem {
	private static final class HoneycombMenuItem {
		static View getActionView(android.view.MenuItem item) {
			return item.getActionView();
		}
		
		static void setActionView(android.view.MenuItem item, int resId) {
			item.setActionView(resId);
		}
		
		static void setActionView(android.view.MenuItem item, View view) {
			item.setActionView(view);
		}
		
		static void setShowAsAction(android.view.MenuItem item, int actionEnum) {
			item.setShowAsAction(actionEnum);
		}
	}
	
	
	/**
	 * Always show this item as a button in an Action Bar. Use sparingly! If too
	 * many items are set to always show in the Action Bar it can crowd the
	 * Action Bar and degrade the user experience on devices with smaller
	 * screens. A good rule of thumb is to have no more than 2 items set to
	 * always show at a time.
	 */
	public static final int SHOW_AS_ACTION_ALWAYS = android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;
	
	/**
	 * Show this item as a button in an Action Bar if the system decides there
	 * is room for it.
	 */
	public static final int SHOW_AS_ACTION_IF_ROOM = android.view.MenuItem.SHOW_AS_ACTION_IF_ROOM;
	
	/**
	 * Never show this item as a button in an Action Bar.
	 */
	public static final int SHOW_AS_ACTION_NEVER = android.view.MenuItem.SHOW_AS_ACTION_NEVER;
	
	/**
	 * When this item is in the action bar, always show it with a text label
	 * even if it also has an icon specified.
	 */
	public static final int SHOW_AS_ACTION_WITH_TEXT = android.view.MenuItem.SHOW_AS_ACTION_WITH_TEXT;
	
	
	/** Native {@link android.view.MenuItem} whose methods are wrapped. */
	private final android.view.MenuItem mMenuItem;
	
	protected MenuItem() {
		this.mMenuItem = null;
	}
	
	/**
	 * Constructor used to create a wrapper to a native
	 * {@link android.view.MenuItem} so we can return the same type for native
	 * and {@link MenuItemImpl} instances, the latter of which will override
	 * all the methods defined in this base class.
	 * 
	 * @param menuItem Native instance.
	 */
	public MenuItem(android.view.MenuItem menuItem) {
		this.mMenuItem = menuItem;
	}
	
	
	/**
	 * Returns the currently set action view for this menu item.
	 * 
	 * @return The item's action view
	 * @see #setActionView(int)
	 * @see #setActionView(View)
	 * @see #setShowAsAction(int)
	 */
	public View getActionView() {
		if (this.mMenuItem != null) {
			return HoneycombMenuItem.getActionView(this.mMenuItem);
		} else {
			return null;
		}
	}
	
	/**
	 * Set an action view for this menu item. An action view will be displayed
	 * in place of an automatically generated menu item element in the UI when
	 * this item is shown as an action within a parent.
	 * 
	 * @param resId Layout resource to use for presenting this item to the user.
	 * @return This Item so additional setters can be called.
	 * @see #setActionView(View)
	 */
	public MenuItem setActionView(int resId) {
		if (this.mMenuItem != null) {
			HoneycombMenuItem.setActionView(this.mMenuItem, resId);
		}
		return this;
	}
	
	/**
	 * Set an action view for this menu item. An action view will be displayed
	 * in place of an automatically generated menu item element in the UI when
	 * this item is shown as an action within a parent.
	 * 
	 * @param view View to use for presenting this item to the user.
	 * @return This Item so additional setters can be called.
	 * @see #setActionView(int)
	 */
	public MenuItem setActionView(View view) {
		if (this.mMenuItem != null) {
			HoneycombMenuItem.setActionView(this.mMenuItem, view);
		}
		return this;
	}
	
	/**
	 * Sets how this item should display in the presence of an Action Bar. The
	 * parameter actionEnum is a flag set. One of
	 * {@link #SHOW_AS_ACTION_ALWAYS}, {@link #SHOW_AS_ACTION_IF_ROOM}, or
	 * {@link #SHOW_AS_ACTION_NEVER} should be used, and you may optionally OR
	 * the value with {@link #SHOW_AS_ACTION_WITH_TEXT}.
	 * {@link #SHOW_AS_ACTION_WITH_TEXT} requests that when the item is shown as
	 * an action, it should be shown with a text label.
	 * 
	 * @param actionEnum How the item should display. One of
	 * {@link #SHOW_AS_ACTION_ALWAYS}, {@link #SHOW_AS_ACTION_IF_ROOM}, or
	 * {@link #SHOW_AS_ACTION_NEVER}. {@link #SHOW_AS_ACTION_NEVER} is the
	 * default.
	 */
	public void setShowAsAction(int actionEnum) {
		if (this.mMenuItem != null) {
			HoneycombMenuItem.setShowAsAction(this.mMenuItem, actionEnum);
		}
	}
	
	// ---------------------------------------------------------------------
	// MENU ITEM SUPPORT
	// ---------------------------------------------------------------------

	@Override
	public char getAlphabeticShortcut() {
		return this.mMenuItem.getAlphabeticShortcut();
	}

	@Override
	public int getGroupId() {
		return this.mMenuItem.getGroupId();
	}

	@Override
	public Drawable getIcon() {
		return this.mMenuItem.getIcon();
	}

	@Override
	public Intent getIntent() {
		return this.mMenuItem.getIntent();
	}

	@Override
	public int getItemId() {
		return this.mMenuItem.getItemId();
	}

	@Override
	public ContextMenuInfo getMenuInfo() {
		return this.mMenuItem.getMenuInfo();
	}

	@Override
	public char getNumericShortcut() {
		return this.mMenuItem.getNumericShortcut();
	}

	@Override
	public int getOrder() {
		return this.mMenuItem.getOrder();
	}

	@Override
	public android.view.SubMenu getSubMenu() {
		return this.mMenuItem.getSubMenu();
	}

	@Override
	public CharSequence getTitle() {
		return this.mMenuItem.getTitle();
	}

	@Override
	public CharSequence getTitleCondensed() {
		return this.mMenuItem.getTitleCondensed();
	}

	@Override
	public boolean hasSubMenu() {
		return this.mMenuItem.hasSubMenu();
	}

	@Override
	public boolean isCheckable() {
		return this.mMenuItem.isCheckable();
	}

	@Override
	public boolean isChecked() {
		return this.mMenuItem.isChecked();
	}

	@Override
	public boolean isEnabled() {
		return this.mMenuItem.isEnabled();
	}

	@Override
	public boolean isVisible() {
		return this.mMenuItem.isVisible();
	}

	@Override
	public MenuItem setAlphabeticShortcut(char alphaChar) {
		this.mMenuItem.setAlphabeticShortcut(alphaChar);
		return this;
	}

	@Override
	public MenuItem setCheckable(boolean checkable) {
		this.mMenuItem.setCheckable(checkable);
		return this;
	}

	@Override
	public MenuItem setChecked(boolean checked) {
		this.mMenuItem.setChecked(checked);
		return this;
	}

	@Override
	public MenuItem setEnabled(boolean enabled) {
		this.mMenuItem.setEnabled(enabled);
		return this;
	}

	@Override
	public MenuItem setIcon(Drawable icon) {
		this.mMenuItem.setIcon(icon);
		return this;
	}

	@Override
	public MenuItem setIcon(int iconRes) {
		this.mMenuItem.setIcon(iconRes);
		return this;
	}

	@Override
	public MenuItem setIntent(Intent intent) {
		this.mMenuItem.setIntent(intent);
		return this;
	}

	@Override
	public MenuItem setNumericShortcut(char numericChar) {
		this.mMenuItem.setNumericShortcut(numericChar);
		return this;
	}

	@Override
	public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
		this.mMenuItem.setOnMenuItemClickListener(menuItemClickListener);
		return this;
	}

	@Override
	public MenuItem setShortcut(char numericChar, char alphaChar) {
		this.mMenuItem.setShortcut(numericChar, alphaChar);
		return this;
	}

	@Override
	public MenuItem setTitle(CharSequence title) {
		this.mMenuItem.setTitle(title);
		return this;
	}

	@Override
	public MenuItem setTitle(int title) {
		this.mMenuItem.setTitle(title);
		return this;
	}

	@Override
	public MenuItem setTitleCondensed(CharSequence title) {
		this.mMenuItem.setTitleCondensed(title);
		return this;
	}

	@Override
	public MenuItem setVisible(boolean visible) {
		this.mMenuItem.setVisible(visible);
		return this;
	}
}
