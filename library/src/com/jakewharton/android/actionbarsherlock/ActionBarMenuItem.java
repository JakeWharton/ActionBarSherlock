/*
 * Copyright 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jakewharton.android.actionbarsherlock;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

/**
 * An implementation of the {@link MenuItem} interface for use in inflating menu
 * XML resources to be added to a third-party action bar. 
 * 
 * @author Jake Wharton <jakewharton@gmail.com>
 * @see {@link com.android.internal.view.MenuItemImpl}
 */
public final class ActionBarMenuItem implements MenuItem {
	/**
	 * Context used for resolving any resources.
	 */
	private final Context mContext;
	
	private Intent mIntent;
	private int mIconId;
	private int mItemId;
	private int mGroupId;
	private int mOrder;
	private CharSequence mTitle;
	private CharSequence mTitleCondensed;
	private ActionBarSubMenu mSubMenu;
	private boolean mIsCheckable;
	private boolean mIsChecked;
	private boolean mIsEnabled;
	private boolean mIsVisible;
	private char mNumericalShortcut;
	private char mAlphabeticalShortcut;
	
	
	/**
	 * Create a new action bar menu item.
	 * 
	 * @param context Context used if resource resolution is required.
	 * @param itemId A unique ID. Used in the activity callback.
	 * @param groupId Group ID. Currently unused.
	 * @param order Item order. Currently unused.
	 * @param title Title of the item.
	 */
	public ActionBarMenuItem(Context context, int itemId, int groupId, int order, CharSequence title) {
		this.mContext = context;
		this.mIsCheckable = false;
		this.mIsChecked = false;
		this.mIsEnabled = true;
		this.mIsVisible = true;
		this.mItemId = itemId;
		this.mGroupId = groupId;
		this.mOrder = order;
		this.mTitle = title;
	}
	
	
	@Override
	public Intent getIntent() {
		return this.mIntent;
	}
	
	/**
	 * Get the current icon resource ID.
	 * 
	 * @return Icon resource ID.
	 */
	public int getIconId() {
		return this.mIconId;
	}

	@Override
	public int getItemId() {
		return this.mItemId;
	}

	@Override
	public CharSequence getTitle() {
		return this.mTitle;
	}

	@Override
	public boolean isEnabled() {
		return this.mIsEnabled;
	}

	@Override
	public boolean isVisible() {
		return this.mIsVisible;
	}

	@Override
	public ActionBarMenuItem setEnabled(boolean enabled) {
		this.mIsEnabled = enabled;
		return this;
	}

	@Override
	public ActionBarMenuItem setIcon(int iconResourceId) {
		this.mIconId = iconResourceId;
		return this;
	}

	@Override
	public ActionBarMenuItem setIntent(Intent intent) {
		this.mIntent = intent;
		return this;
	}

	@Override
	public ActionBarMenuItem setTitle(CharSequence title) {
		this.mTitle = title;
		return this;
	}

	@Override
	public ActionBarMenuItem setTitle(int titleResourceId) {
		return this.setTitle(this.mContext.getResources().getString(titleResourceId));
	}

	@Override
	public ActionBarMenuItem setVisible(boolean visible) {
		this.mIsVisible = visible;
		return this;
	}

	@Override
	public boolean isChecked() {
		return this.mIsChecked;
	}

	@Override
	public ActionBarMenuItem setChecked(boolean checked) {
		this.mIsChecked = checked;
		return this;
	}

	@Override
	public boolean isCheckable() {
		return this.mIsCheckable;
	}

	@Override
	public ActionBarMenuItem setCheckable(boolean checkable) {
		this.mIsCheckable = checkable;
		return this;
	}

	@Override
	public CharSequence getTitleCondensed() {
		return this.mTitleCondensed;
	}

	@Override
	public ActionBarMenuItem setTitleCondensed(CharSequence title) {
		this.mTitleCondensed = title;
		return this;
	}

	@Override
	public int getGroupId() {
		return this.mGroupId;
	}

	@Override
	public int getOrder() {
		return this.mOrder;
	}

	@Override
	public ActionBarSubMenu getSubMenu() {
		return this.mSubMenu;
	}
	
	/**
	 * Set the sub-menu of this item.
	 * 
	 * @param subMenu Sub-menu instance.
	 * @return This Item so additional setters can be called. 
	 */
	public ActionBarMenuItem setSubMenu(ActionBarSubMenu subMenu) {
		this.mSubMenu = subMenu;
		return this;
	}

	@Override
	public boolean hasSubMenu() {
		return (this.mSubMenu != null) && (this.mSubMenu.size() > 0);
	}

	@Override
	public char getAlphabeticShortcut() {
		return this.mAlphabeticalShortcut;
	}

	@Override
	public char getNumericShortcut() {
		return this.mNumericalShortcut;
	}

	@Override
	public ActionBarMenuItem setAlphabeticShortcut(char alphaChar) {
		this.mAlphabeticalShortcut = Character.toLowerCase(alphaChar);
		return this;
	}

	@Override
	public ActionBarMenuItem setNumericShortcut(char numericChar) {
		this.mNumericalShortcut = numericChar;
		return this;
	}

	@Override
	public ActionBarMenuItem setShortcut(char numericChar, char alphaChar) {
		return this.setNumericShortcut(numericChar).setAlphabeticShortcut(alphaChar);
	}
	
	
	@Override
	public View getActionView() {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public Drawable getIcon() {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public ContextMenuInfo getMenuInfo() {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public ActionBarMenuItem setActionView(View view) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public ActionBarMenuItem setActionView(int resId) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public ActionBarMenuItem setIcon(Drawable icon) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public ActionBarMenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public void setShowAsAction(int actionEnum) {
		throw new RuntimeException("Method not supported.");
	}
}