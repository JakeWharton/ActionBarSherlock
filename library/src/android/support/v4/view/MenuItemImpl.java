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

import com.jakewharton.android.actionbarsherlock.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ImageView;

/**
 * An implementation of the {@link android.view.MenuItem} interface for use in
 * inflating menu XML resources to be added to a third-party action bar. 
 * 
 * @author Jake Wharton <jakewharton@gmail.com>
 * @see <a href="http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob;f=core/java/com/android/internal/view/menu/MenuItemImpl.java">com.android.internal.view.menu.MenuItemImpl</a>
 */
public final class MenuItemImpl extends MenuItem {
	final Context mContext;
	final View mView;
	final ImageView mIcon;
	final int mItemId;
	final int mGroupId;
	final int mOrder;
	
	Intent mIntent;
	CharSequence mTitle;
	CharSequence mTitleCondensed;
	SubMenuBuilder mSubMenu;
	boolean mIsCheckable;
	boolean mIsChecked;
	boolean mIsEnabled;
	char mNumericalShortcut;
	char mAlphabeticalShortcut;
	int mShowAsAction;
	OnMenuItemClickListener mListener;
	
	
	/**
	 * Create a new action bar menu item.
	 * 
	 * @param context Context used if resource resolution is required.
	 * @param itemId A unique ID. Used in the activity callback.
	 * @param groupId Group ID. Currently unused.
	 * @param order Item order. Currently unused.
	 * @param title Title of the item.
	 */
	MenuItemImpl(Context context, int itemId, int groupId, int order, CharSequence title) {
		this.mContext = context;
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = inflater.inflate(R.layout.actionbar_item, null, false);
		mView.setTag(this);
		
		mIcon = (ImageView)mView; //Same for now. TODO: Create real action item view with text and custom layout support
		
		this.mIsCheckable = false;
		this.mIsChecked = false;
		this.mIsEnabled = true;
		this.mItemId = itemId;
		this.mGroupId = groupId;
		this.mOrder = order;
		this.mTitle = title;
	}
	
	
	void addTo(android.view.Menu menu) {
		if (this.mSubMenu != null) {
			android.view.SubMenu subMenu = menu.addSubMenu(this.mGroupId, this.mItemId, this.mOrder, this.mTitle);
			for (MenuItemImpl item : this.mSubMenu.getItems()) {
				android.view.MenuItem newItem = subMenu.add(item.getGroupId(), item.getItemId(), item.getOrder(), item.getTitle());
				newItem.setCheckable(item.mIsCheckable);
				newItem.setChecked(item.mIsChecked);
				newItem.setAlphabeticShortcut(item.mAlphabeticalShortcut);
				newItem.setNumericShortcut(item.mNumericalShortcut);
				newItem.setEnabled(item.mIsEnabled);
				newItem.setVisible(item.isVisible());
				newItem.setIntent(item.mIntent);
				newItem.setOnMenuItemClickListener(item.mListener);
				newItem.setIcon(item.mIcon.getDrawable());
			}
		} else {
			menu.add(this.mGroupId, this.mItemId, this.mOrder, this.mTitle);
		}
	}
	
	public View getActionBarView() {
		return this.mView;
	}
	
	@Override
	public Intent getIntent() {
		return this.mIntent;
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
		return this.mView.getVisibility() == View.VISIBLE;
	}

	@Override
	public MenuItem setEnabled(boolean enabled) {
		this.mIsEnabled = enabled;
		return this;
	}

	@Override
	public MenuItem setIcon(int iconResourceId) {
		this.mIcon.setImageResource(iconResourceId);
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
		mTitle = this.mContext.getResources().getString(titleResourceId);
		return this;
	}

	@Override
	public MenuItem setVisible(boolean visible) {
		this.mView.setVisibility(visible ? View.VISIBLE : View.GONE);
		return this;
	}

	@Override
	public boolean isChecked() {
		return this.mIsCheckable && this.mIsChecked;
	}

	@Override
	public MenuItem setChecked(boolean checked) {
		if (mIsCheckable) {
			this.mIsChecked = checked;
		}
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
	public SubMenuBuilder getSubMenu() {
		return this.mSubMenu;
	}
	
	/**
	 * Set the sub-menu of this item.
	 * 
	 * @param subMenu Sub-menu instance.
	 * @return This Item so additional setters can be called. 
	 */
	MenuItem setSubMenu(SubMenuBuilder subMenu) {
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
		this.setNumericShortcut(numericChar);
		this.setAlphabeticShortcut(alphaChar);
		return this;
	}

	@Override
	public void setShowAsAction(int actionEnum) {
		this.mShowAsAction = actionEnum;
	}
	
	public int getShowAsAction() {
		return this.mShowAsAction;
	}
	
	@Override
	public View getActionView() {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public Drawable getIcon() {
		return this.mIcon.getDrawable();
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
		this.mIcon.setImageDrawable(icon);
		return this;
	}

	@Override
	public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
		this.mListener = menuItemClickListener;
		return this;
	}
	
	/**
	 * Returns the currently set menu click listener for this item.
	 * 
	 * @return Click listener or {@code null}.
	 */
	public OnMenuItemClickListener getOnMenuItemClickListener() {
		return this.mListener;
	}
}