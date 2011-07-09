/*
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2011 Jake Wharton
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

package com.actionbarsherlock.internal.view.menu;

import java.lang.ref.WeakReference;
import com.actionbarsherlock.R;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MenuItem;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;

public final class MenuItemImpl implements MenuItem {
	private static final String TAG = "MenuItemImpl";
	
	private final MenuBuilder mMenu;
	
	private final int mItemId;
	private final int mGroupId;
	private final int mCategoryOrder;
	private final int mOrdering;
	
	private Intent mIntent;
	private CharSequence mTitle;
	private CharSequence mTitleCondensed;
	private char mNumericalShortcut;
	private char mAlphabeticalShortcut;
	private int mShowAsAction;
	private SubMenuBuilder mSubMenu;
	private Runnable mItemCallback;
	private OnMenuItemClickListener mClickListener;
	private Drawable mIcon;
	private int mIconRes = View.NO_ID;
	private View mActionView;
	private int mActionViewRes = View.NO_ID;

	int mFlags = ENABLED;
	static final int CHECKABLE = 0x01;
	static final int CHECKED   = 0x02;
	static final int EXCLUSIVE = 0x04;
	static final int HIDDEN    = 0x08;
	static final int ENABLED   = 0x10;
	static final int IS_ACTION = 0x20;
	
	private final WeakReference<MenuView.ItemView>[] mItemViews;
	
	/*
	private final DialogInterface.OnClickListener subMenuClick = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int index) {
			dialog.dismiss();
			((MenuItemImpl)mSubMenu.getItem(index)).invoke();
		}
	};
	private final DialogInterface.OnMultiChoiceClickListener subMenuMultiClick = new DialogInterface.OnMultiChoiceClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int index, boolean isChecked) {
			dialog.dismiss();
			mSubMenu.getItem(index).setChecked(isChecked);
		}
	};
	*/
	
	
	@SuppressWarnings("unchecked")
	MenuItemImpl(MenuBuilder menu, int groupId, int itemId, int order, int ordering, CharSequence title, int showAsAction) {
		mMenu = menu;
		
		mItemId = itemId;
		mGroupId = groupId;
		mCategoryOrder = order;
		mOrdering = ordering;
		mTitle = title;
		mShowAsAction = showAsAction;
		
		mItemViews = new WeakReference[MenuBuilder.NUM_TYPES];
	}
	

	
	private MenuView.ItemView createItemView(int menuType, ViewGroup parent) {
		MenuView.ItemView view = null;
		switch (menuType) {
			case MenuBuilder.TYPE_NATIVE:
				view = new NativeItemView();
				break;
				
			case MenuBuilder.TYPE_ACTION_BAR:
				view = (MenuView.ItemView)getLayoutInflater(menuType).inflate(R.layout.action_menu_item_layout, parent, false);
				break;
		}
		if (view != null) {
			view.initialize(this, menuType);
		}
		return view;
	}
	
	private boolean hasItemView(int menuType) {
		return mItemViews[menuType] != null && mItemViews[menuType].get() != null;
	}
	
	private void setIconOnViews(Drawable icon) {
		for (int i = MenuBuilder.NUM_TYPES - 1; i >= 0; i--) {
			if (hasItemView(i)) {
				mItemViews[i].get().setIcon(icon);
			}
		}
	}
	
	@Override
	public View getActionView() {
		if (mActionView != null) {
			return mActionView;
		}
		if (mActionViewRes != View.NO_ID) {
			return LayoutInflater.from(mMenu.getContext()).inflate(mActionViewRes, null, false);
		}
		return null;
	}

	@Override
	public char getAlphabeticShortcut() {
		return mAlphabeticalShortcut;
	}

	@Override
	public int getGroupId() {
		return mGroupId;
	}

	@Override
	public Drawable getIcon() {
		if (mIcon != null) {
			return mIcon;
		}
		if (mIconRes != View.NO_ID) {
			return mMenu.getContext().getResources().getDrawable(mIconRes);
		}
		return null;
	}
	
	@Override
	public Intent getIntent() {
		return this.mIntent;
	}

	@Override
	public int getItemId() {
		return this.mItemId;
	}
	
	public MenuView.ItemView getItemView(int menuType, ViewGroup parent) {
		if (!hasItemView(menuType)) {
			MenuView.ItemView view = createItemView(menuType, parent);
			mItemViews[menuType] = new WeakReference<MenuView.ItemView>(view);
		}
		return mItemViews[menuType].get();
	}
	
	public LayoutInflater getLayoutInflater(int menuType) {
		return mMenu.getMenuType(menuType).getInflater();
	}

	@Override
	public ContextMenuInfo getMenuInfo() {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public char getNumericShortcut() {
		return mNumericalShortcut;
	}

	@Override
	public int getOrder() {
		return mCategoryOrder;
	}
	
	public int getOrdering() {
		return mOrdering;
	}

	@Override
	public SubMenuBuilder getSubMenu() {
		return mSubMenu;
	}

	@Override
	public CharSequence getTitle() {
		return this.mTitle;
	}

	@Override
	public CharSequence getTitleCondensed() {
		return mTitleCondensed;
	}
	
	public CharSequence getTitleForItemView(MenuView.ItemView itemView) {
		return itemView.prefersCondensedTitle() ? getTitleCondensed() : getTitle();
	}

	@Override
	public boolean hasSubMenu() {
		return (mSubMenu != null) && (mSubMenu.size() > 0);
	}
	
	public boolean invoke() {
		/*
		if (hasSubMenu()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mMenu.getContext());
			builder.setTitle(getTitle());
			
			final boolean isExclusive = (mSubMenu.size() > 0) && mSubMenu.getItem(0).isExclusiveCheckable();
			final boolean isCheckable = (mSubMenu.size() > 0) && mSubMenu.getItem(0).isCheckable();
			if (isExclusive) {
				builder.setSingleChoiceItems(getSubMenuTitles(), getSubMenuSelected(), subMenuClick);
			} else if (isCheckable) {
				builder.setMultiChoiceItems(getSubMenuTitles(), getSubMenuChecked(), subMenuMultiClick);
			} else {
				builder.setItems(getSubMenuTitles(), subMenuClick);
			}
			
			builder.show();
			return true;
		}
		*/
		
		if (mClickListener != null &&
		    mClickListener.onMenuItemClick(this)) {
			return true;
		}
		
		MenuBuilder.Callback callback = mMenu.getRootMenu().getCallback();
		if (callback != null &&
			callback.onMenuItemSelected(mMenu.getRootMenu(), this)) {
			return true;
		}
		
		if (mItemCallback != null) {
			mItemCallback.run();
			return true;
		}
		
		if (mIntent != null) {
			try {
				mMenu.getContext().startActivity(mIntent);
				return true;
			} catch (ActivityNotFoundException e) {
				Log.e(TAG, "Can't find activity to handle intent; ignoring", e);
			}
		}
		
		return false;
	}
	
	public boolean isActionButton() {
		return ((mFlags & IS_ACTION) == IS_ACTION) && requiresActionButton();
	}

	@Override
	public boolean isCheckable() {
		return (mFlags & CHECKABLE) == CHECKABLE;
	}

	@Override
	public boolean isChecked() {
		return (mFlags & CHECKED) == CHECKED;
	}

	@Override
	public boolean isEnabled() {
		return (mFlags & ENABLED) == ENABLED;
	}

	public boolean isExclusiveCheckable() {
		return (mFlags & EXCLUSIVE) == EXCLUSIVE;
	}

	@Override
	public boolean isVisible() {
		return (mFlags & HIDDEN) == 0;
	}
	
	public boolean requestsActionButton() {
		return (mShowAsAction & MenuItem.SHOW_AS_ACTION_IF_ROOM) == MenuItem.SHOW_AS_ACTION_IF_ROOM;
	}
	
	public boolean requiresActionButton() {
		return (mShowAsAction & MenuItem.SHOW_AS_ACTION_ALWAYS) == MenuItem.SHOW_AS_ACTION_ALWAYS;
	}

	@Override
	public MenuItem setActionView(int resId) {
		ViewGroup viewGroup = (ViewGroup)mMenu.getMenuView(MenuBuilder.TYPE_NATIVE, null);
		View view = LayoutInflater.from(mMenu.getContext()).inflate(resId, viewGroup, false);
		return setActionView(view);
	}

	@Override
	public MenuItem setActionView(View view) {
		mActionView = view;
		mMenu.onItemActionRequestChanged(this);
		return this;
	}

	@Override
	public MenuItem setAlphabeticShortcut(char alphaChar) {
		mAlphabeticalShortcut = Character.toLowerCase(alphaChar);
		return this;
	}

	@Override
	public MenuItem setCheckable(boolean checkable) {
		final boolean oldValue = isCheckable();
		mFlags = (mFlags & ~CHECKABLE) | (checkable ? CHECKABLE : 0);
		if (oldValue != checkable) {
			for (int i = MenuBuilder.NUM_TYPES - 1; i >= 0; i--) {
				if (hasItemView(i)) {
					mItemViews[i].get().setCheckable(checkable);
				}
			}
		}
		
		return this;
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
		final boolean oldValue = isChecked();
		mFlags = (mFlags & ~CHECKED) | (checked ? CHECKED : 0);
		if (oldValue != checked) {
			for (int i = MenuBuilder.NUM_TYPES - 1; i >= 0; i--) {
				if (hasItemView(i)) {
					mItemViews[i].get().setChecked(checked);
				}
			}
		}
	}

	@Override
	public MenuItem setEnabled(boolean enabled) {
		final boolean oldValue = isEnabled();
		mFlags = (mFlags & ~ENABLED) | (enabled ? ENABLED : 0);
		
		if (oldValue != enabled) {
			for (int i = MenuBuilder.NUM_TYPES - 1; i >= 0; i--) {
				if (hasItemView(i)) {
					mItemViews[i].get().setEnabled(enabled);
				}
			}
		}
		
		return this;
	}

	public void setExclusiveCheckable(boolean exclusive) {
		mFlags = (mFlags & ~EXCLUSIVE) | (exclusive ? EXCLUSIVE : 0);
	}

	@Override
	public MenuItem setIcon(int iconResourceId) {
		mIcon = null;
		mIconRes = iconResourceId;
		
		if (mIconRes != View.NO_ID) {
			setIconOnViews(mMenu.getContext().getResources().getDrawable(mIconRes));
		}
		
		return this;
	}

	@Override
	public MenuItem setIcon(Drawable icon) {
		mIconRes = View.NO_ID;
		mIcon = icon;
		setIconOnViews(icon);
		return this;
	}

	@Override
	public MenuItem setIntent(Intent intent) {
		mIntent = intent;
		return this;
	}
	
	public void setIsActionButton(boolean isAction) {
		mFlags = (mFlags & ~IS_ACTION) | (isAction ? IS_ACTION : 0);
	}
	
	public void setItemView(int type, MenuView.ItemView itemView) {
		mItemViews[type] = new WeakReference<MenuView.ItemView>(itemView);
	}

	@Override
	public MenuItem setNumericShortcut(char numericChar) {
		mNumericalShortcut = numericChar;
		return this;
	}

	@Override
	public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
		mClickListener = menuItemClickListener;
		return this;
	}
	
	@Override
	public android.view.MenuItem setOnMenuItemClickListener(final android.view.MenuItem.OnMenuItemClickListener menuItemClickListener) {
		return this.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				return menuItemClickListener.onMenuItemClick(new MenuItemWrapper(item));
			}
		});
	}

	@Override
	public MenuItem setShortcut(char numericChar, char alphaChar) {
		setNumericShortcut(numericChar);
		setAlphabeticShortcut(alphaChar);
		return this;
	}

	@Override
	public void setShowAsAction(int actionEnum) {
		mShowAsAction = actionEnum;
	}
	
	void setSubMenu(SubMenuBuilder subMenu) {
		if ((mMenu != null) && (mMenu instanceof SubMenu)) {
			throw new UnsupportedOperationException("Attempt to add sub-menu to a sub-menu");
		}
		mSubMenu = subMenu;
		mSubMenu.setHeaderTitle(getTitle());
	}

	@Override
	public MenuItem setTitle(int titleResourceId) {
		return setTitle(mMenu.getContext().getString(titleResourceId));
	}

	@Override
	public MenuItem setTitle(CharSequence title) {
		mTitle = title;
		for (int i = MenuBuilder.NUM_TYPES; i >= 0; i--) {
			if (hasItemView(i)) {
				MenuView.ItemView itemView = mItemViews[i].get();
				if (!itemView.prefersCondensedTitle() || (mTitleCondensed == null)) {
					itemView.setTitle(mTitle);
				}
			}
		}
		return this;
	}

	@Override
	public MenuItem setTitleCondensed(CharSequence title) {
		mTitleCondensed = title;
		for (int i = MenuBuilder.NUM_TYPES; i >= 0; i--) {
			if (hasItemView(i)) {
				MenuView.ItemView itemView = mItemViews[i].get();
				if (itemView.prefersCondensedTitle()) {
					itemView.setTitle(mTitleCondensed);
				}
			}
		}
		return this;
	}

	@Override
	public MenuItem setVisible(boolean visible) {
		if (setVisibleInt(visible)) {
			mMenu.onItemVisibleChanged(this);
		}
		return this;
	}
	
	boolean setVisibleInt(boolean visible) {
		final boolean oldValue = isVisible();
		mFlags = (mFlags & ~HIDDEN) | (visible ? 0 : HIDDEN);
		return oldValue != visible;
	}
	
	public boolean showsTextAsAction() {
		return (mShowAsAction & MenuItem.SHOW_AS_ACTION_WITH_TEXT) == MenuItem.SHOW_AS_ACTION_WITH_TEXT;
	}
	
	@Override
	public String toString() {
		return mTitle.toString();
	}
	
	
	
	
	public static final class NativeItemView implements MenuView.ItemView {
		private MenuItemImpl mItemData;
		private android.view.MenuItem mNativeMenuItem;
		
		
		public void attach(android.view.Menu menu) {
			//TODO add item to menu
			//TODO store in mNativeMenuItem
			//TODO initialize everything else
		}

		@Override
		public MenuItemImpl getItemData() {
			return mItemData;
		}

		@Override
		public void initialize(MenuItemImpl itemData, int menuType) {
			mItemData = itemData;
			//Initialization is deferred until we attach to a native menu
		}

		@Override
		public boolean prefersCondensedTitle() {
			return true;
		}

		@Override
		public void setCheckable(boolean checkable) {
			if (mNativeMenuItem != null) {
				mNativeMenuItem.setCheckable(checkable);
			}
		}

		@Override
		public void setChecked(boolean checked) {
			if (mNativeMenuItem != null) {
				mNativeMenuItem.setChecked(checked);
			}
		}

		@Override
		public void setEnabled(boolean enabled) {
			if (mNativeMenuItem != null) {
				mNativeMenuItem.setEnabled(enabled);
			}
		}

		@Override
		public void setIcon(Drawable icon) {
			if (mNativeMenuItem != null) {
				mNativeMenuItem.setIcon(icon);
			}
		}

		@Override
		public void setShortcut(boolean showShortcut, char shortcutKey) {
			if (mNativeMenuItem != null) {
				mNativeMenuItem.setAlphabeticShortcut(shortcutKey);
			}
		}

		@Override
		public void setTitle(CharSequence title) {
			if (mNativeMenuItem != null) {
				mNativeMenuItem.setTitle(title);
			}
		}

		@Override
		public boolean showsIcon() {
			return true;
		}
	}
}
