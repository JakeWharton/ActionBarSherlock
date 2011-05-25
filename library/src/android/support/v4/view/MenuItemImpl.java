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
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * An implementation of the {@link android.view.MenuItem} interface for use in
 * inflating menu XML resources to be added to a third-party action bar. 
 * 
 * @author Jake Wharton <jakewharton@gmail.com>
 * @see <a href="http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob;f=core/java/com/android/internal/view/menu/MenuItemImpl.java">com.android.internal.view.menu.MenuItemImpl</a>
 */
public final class MenuItemImpl implements MenuItem {
	final MenuBuilder mMenu;
	final LayoutInflater mInflater;
	
	final View mView;
	final ImageView mIcon;
	final FrameLayout mCustomView;
	
	final int mItemId;
	final int mGroupId;
	final int mOrder;
	
	Intent mIntent;
	CharSequence mTitle;
	CharSequence mTitleCondensed;
	SubMenuBuilder mSubMenu;
	char mNumericalShortcut;
	char mAlphabeticalShortcut;
	int mShowAsAction;
	OnMenuItemClickListener mListener;

	int mFlags = ENABLED;
	private static final int CHECKABLE = 0x01;
	private static final int CHECKED   = 0x02;
	private static final int EXCLUSIVE = 0x04;
	private static final int HIDDEN    = 0x08;
	private static final int ENABLED   = 0x10;
	
	boolean mIsShownOnActionBar;
	
	
	/**
	 * Create a new action bar menu item.
	 * 
	 * @param context Context used if resource resolution is required.
	 * @param itemId A unique ID. Used in the activity callback.
	 * @param groupId Group ID. Currently unused.
	 * @param order Item order. Currently unused.
	 * @param title Title of the item.
	 */
	MenuItemImpl(MenuBuilder menu, int itemId, int groupId, int order, CharSequence title) {
		this.mMenu = menu;
		this.mInflater = (LayoutInflater)menu.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mView = this.mInflater.inflate(R.layout.actionbar_item, null, false);
		mView.setTag(this);
		
		mIcon = (ImageView)mView.findViewById(R.id.actionbar_item_icon);
		mCustomView = (FrameLayout)mView.findViewById(R.id.actionbar_item_custom);
		
		this.mItemId = itemId;
		this.mGroupId = groupId;
		this.mOrder = order;
		this.mTitle = title;
		
		this.mIsShownOnActionBar = false;
	}
	
	
	public void addTo(android.view.Menu menu) {
		if (this.mSubMenu != null) {
			android.view.SubMenu subMenu = menu.addSubMenu(this.mGroupId, this.mItemId, this.mOrder, this.mTitle);
			subMenu.setIcon(this.mIcon.getDrawable());
			
			for (MenuItemImpl item : this.mSubMenu.getItems()) {
				item.addTo(subMenu);
			}
		} else {
			menu.add(this.mGroupId, this.mItemId, this.mOrder, this.mTitle)
				.setCheckable(this.isCheckable())
				.setChecked(this.isChecked())
				.setAlphabeticShortcut(this.mAlphabeticalShortcut)
				.setNumericShortcut(this.mNumericalShortcut)
				.setEnabled(this.isEnabled())
				.setVisible(this.isVisible())
				.setIntent(this.mIntent)
				.setOnMenuItemClickListener(this.mListener)
				.setIcon(this.mIcon.getDrawable());

			if (this.isExclusiveCheckable()) {
				menu.setGroupCheckable(this.mGroupId, true, true);
			}
		}
	}
	
	public View getActionBarView() {
		return this.mView;
	}
	
	/**
	 * Get whether or not this item is being shown on the action bar.
	 * 
	 * @return {@code true} if shown, {@code false} otherwise.
	 */
	public boolean isShownOnActionBar() {
		return this.mIsShownOnActionBar;
	}
	
	/**
	 * Denote whether or not this menu item is being shown on the action bar.
	 * 
	 * @param isShownOnActionBar {@code true} if shown or {@code false}.
	 */
	public void setIsShownOnActionBar(boolean isShownOnActionBar) {
		this.mIsShownOnActionBar = isShownOnActionBar;
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
		return (mFlags & ENABLED) != 0;
	}

	@Override
	public boolean isVisible() {
		return (mFlags & HIDDEN) == 0;
	}

	@Override
	public MenuItem setEnabled(boolean enabled) {
		mFlags = (mFlags & ~ENABLED) | (enabled ? ENABLED : 0);
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
		mTitle = this.mMenu.getContext().getResources().getString(titleResourceId);
		return this;
	}

	@Override
	public MenuItem setVisible(boolean visible) {
		final int oldFlags = mFlags;
		mFlags = (mFlags & ~HIDDEN) | (visible ? 0 : HIDDEN);
		if (oldFlags != mFlags) {
			mView.setVisibility(visible ? View.VISIBLE : View.GONE);
		}
		return this;
	}

	@Override
	public boolean isChecked() {
		return (mFlags & CHECKED) == CHECKED;
	}

	@Override
	public MenuItem setChecked(boolean checked) {
		if ((mFlags & EXCLUSIVE) == EXCLUSIVE) {
			// Call the method on the Menu since it knows about the others in this
			// exclusive checkable group
			mMenu.setExclusiveItemChecked(this);
		} else {
			setCheckedInt(checked);
		}
		
		return this;
	}

	void setCheckedInt(boolean checked) {
		final int oldFlags = mFlags;
		mFlags = (mFlags & ~CHECKED) | (checked ? CHECKED : 0);
		if (oldFlags != mFlags) {
			//TODO update view as checked or not
		}
	}

	@Override
	public boolean isCheckable() {
		return (mFlags & CHECKABLE) == CHECKABLE;
	}

	@Override
	public MenuItem setCheckable(boolean checkable) {
		final int oldFlags = mFlags;
		mFlags = (mFlags & ~CHECKABLE) | (checkable ? CHECKABLE : 0);
		if (oldFlags != mFlags) {
			//TODO update view as checkable or not
		}
		
		return this;
	}

	public void setExclusiveCheckable(boolean exclusive) {
		mFlags = (mFlags & ~EXCLUSIVE) | (exclusive ? EXCLUSIVE : 0);
	}

	public boolean isExclusiveCheckable() {
		return (mFlags & EXCLUSIVE) != 0;
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
		return this.mCustomView.getChildAt(0);
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
		this.mCustomView.removeAllViews();
		if (view != null) {
			this.mCustomView.addView(view);
		}
		this.mIcon.setVisibility((view != null) ? View.GONE : View.VISIBLE);
		return this;
	}

	@Override
	public MenuItem setActionView(int resId) {
		this.mCustomView.removeAllViews();
		this.mInflater.inflate(resId, this.mCustomView, true);
		this.mIcon.setVisibility(View.GONE);
		return this;
	}

	@Override
	public MenuItem setIcon(Drawable icon) {
		this.mIcon.setImageDrawable(icon);
		return this;
	}
	
	@Override
	public android.view.MenuItem setOnMenuItemClickListener(final android.view.MenuItem.OnMenuItemClickListener menuItemClickListener) {
		return this.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				return menuItemClickListener.onMenuItemClick(new MenuItemHoneycombWrapper(item));
			}
		});
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