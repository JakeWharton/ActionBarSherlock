package com.jakewharton.android.actionbarsherlock;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

/*
 * See: com.android.internal.view.menu.MenuItemImpl
 */
public final class ActionBarMenuItem implements MenuItem {
	private final Context mContext;
	
	private Intent mIntent;
	private int mIconId;
	private int mItemId;
	private int mGroupId;
	private int mOrder;
	private CharSequence mTitle;
	private CharSequence mTitleCondensed;
	private SubMenu mSubMenu;
	private boolean mIsCheckable;
	private boolean mIsChecked;
	private boolean mIsEnabled;
	private boolean mIsVisible;
	private char mNumericalShortcut;
	private char mAlphabeticalShortcut;
	
	
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
	public MenuItem setEnabled(boolean enabled) {
		this.mIsEnabled = enabled;
		return this;
	}

	@Override
	public MenuItem setIcon(int iconResourceId) {
		this.mIconId = iconResourceId;
		return this;
	}

	@Override
	public MenuItem setIntent(Intent intent) {
		this.mIntent = intent;
		return this;
	}

	@Override
	public MenuItem setTitle(CharSequence title) {
		this.mTitle = title;
		return this;
	}

	@Override
	public MenuItem setTitle(int titleResourceId) {
		return this.setTitle(this.mContext.getResources().getString(titleResourceId));
	}

	@Override
	public MenuItem setVisible(boolean visible) {
		this.mIsVisible = visible;
		return this;
	}

	@Override
	public boolean isChecked() {
		return this.mIsChecked;
	}

	@Override
	public MenuItem setChecked(boolean checked) {
		this.mIsChecked = checked;
		return this;
	}

	@Override
	public boolean isCheckable() {
		return this.mIsCheckable;
	}

	@Override
	public MenuItem setCheckable(boolean checkable) {
		this.mIsCheckable = checkable;
		return this;
	}

	@Override
	public CharSequence getTitleCondensed() {
		return this.mTitleCondensed;
	}

	@Override
	public MenuItem setTitleCondensed(CharSequence title) {
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
	public SubMenu getSubMenu() {
		return this.mSubMenu;
	}

	@Override
	public boolean hasSubMenu() {
		return this.mSubMenu != null;
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
	public MenuItem setAlphabeticShortcut(char alphaChar) {
		this.mAlphabeticalShortcut = Character.toLowerCase(alphaChar);
		return this;
	}

	@Override
	public MenuItem setNumericShortcut(char numericChar) {
		this.mNumericalShortcut = numericChar;
		return this;
	}

	@Override
	public MenuItem setShortcut(char numericChar, char alphaChar) {
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
	public MenuItem setActionView(View view) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public MenuItem setActionView(int resId) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public MenuItem setIcon(Drawable icon) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public void setShowAsAction(int actionEnum) {
		throw new RuntimeException("Method not supported.");
	}
}