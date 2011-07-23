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

import java.util.ArrayList;
import java.util.List;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.KeyEvent;

/**
 * An implementation of the {@link android.view.Menu} interface for use in
 * inflating menu XML resources to be added to a third-party action bar. 
 * 
 * @author Jake Wharton <jakewharton@gmail.com>
 * @see <a href="http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob;f=core/java/com/android/internal/view/menu/MenuBuilder.java">com.android.internal.view.menu.MenuBuilder</a>
 */
public class MenuBuilder implements Menu {
	private static final int DEFAULT_ITEM_ID = 0;
	private static final int DEFAULT_GROUP_ID = 0;
	private static final int DEFAULT_ORDER = 0;
	
	public static final int NUM_TYPES = 2;
	public static final int TYPE_WATSON = 0;
	public static final int TYPE_ACTION_BAR = 1;
	
	
	
	public interface Callback {
		public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item);
	}
	
	
	
	/** Context used for resolving any resources. */
	private final Context mContext;
	
	/** Child {@link ActionBarMenuItem} items. */
	private final List<MenuItemImpl> mItems;
	
	/** Menu callback that will receive various events. */
	private Callback mCallback;
	
	
	
	/**
	 * Create a new action bar menu.
	 * 
	 * @param context Context used if resource resolution is required.
	 */
	public MenuBuilder(Context context) {
		this.mContext = context;
		this.mItems = new ArrayList<MenuItemImpl>();
	}

	
	
	public void setCallback(Callback callback) {
		mCallback = callback;
	}
	
	public Callback getCallback() {
		return mCallback;
	}
	
	/**
	 * Gets the root menu (if this is a submenu, find its root menu).
	 * 
	 * @return The root menu.
	 */
	public MenuBuilder getRootMenu() {
		return this;
	}
	
	/**
	 * Special method to create a detached child item of this menu with the
	 * specified ID. This should ONLY be used internally for the creation
	 * of the home item.
	 * 
	 * @param itemId ID of detached item.
	 * @return Item instance.
	 */
	public MenuItemImpl addDetached(int itemId) {
		return new MenuItemImpl(this, itemId, -1, -1, null);
	}
	
	/**
	 * Get a list of the items contained in this menu.
	 * 
	 * @return List of {@link MenuItemImpl}s.
	 */
	public final List<MenuItemImpl> getItems() {
		return this.mItems;
	}
	
	final MenuItemImpl remove(int index) {
		return this.mItems.remove(index);
	}
	
	final Context getContext() {
		return this.mContext;
	}

	void setExclusiveItemChecked(MenuItem item) {
		final int group = item.getGroupId();
		
		final int N = mItems.size();
		for (int i = 0; i < N; i++) {
			MenuItemImpl curItem = mItems.get(i);
			if (curItem.getGroupId() == group) {
				if (!curItem.isExclusiveCheckable()) continue;
				if (!curItem.isCheckable()) continue;
				
				// Check the item meant to be checked, uncheck the others (that are in the group)
				curItem.setCheckedInt(curItem == item);
			}
		}
	}
	
	// ** Menu Methods ** \\
	
	@Override
	public MenuItemImpl add(CharSequence title) {
		return this.add(DEFAULT_ITEM_ID, DEFAULT_GROUP_ID, DEFAULT_ORDER, title);
	}

	@Override
	public MenuItemImpl add(int titleResourceId) {
		return this.add(DEFAULT_GROUP_ID, DEFAULT_ITEM_ID, DEFAULT_ORDER, titleResourceId);
	}

	@Override
	public MenuItemImpl add(int groupId, int itemId, int order, int titleResourceId) {
		String title = null;
		if (titleResourceId != 0) {
			title = this.mContext.getResources().getString(titleResourceId);
		}
		return this.add(groupId, itemId, order, title);
	}

	@Override
	public MenuItemImpl add(int groupId, int itemId, int order, CharSequence title) {
		MenuItemImpl item = new MenuItemImpl(this, itemId, groupId, order, title);
		this.mItems.add(item);
		return item;
	}

	@Override
	public SubMenuBuilder addSubMenu(CharSequence title) {
		return this.addSubMenu(DEFAULT_GROUP_ID, DEFAULT_ITEM_ID, DEFAULT_ORDER, title);
	}

	@Override
	public SubMenuBuilder addSubMenu(int titleResourceId) {
		return this.addSubMenu(DEFAULT_GROUP_ID, DEFAULT_ITEM_ID, DEFAULT_ORDER, titleResourceId);
	}

	@Override
	public SubMenuBuilder addSubMenu(int groupId, int itemId, int order, int titleResourceId) {
		String title = this.mContext.getResources().getString(titleResourceId);
		return this.addSubMenu(groupId, itemId, order, title);
	}

	@Override
	public SubMenuBuilder addSubMenu(int groupId, int itemId, int order, CharSequence title) {
		MenuItemImpl item = (MenuItemImpl)this.add(groupId, itemId, order, title);
		SubMenuBuilder subMenu = new SubMenuBuilder(this.mContext, this, item);
		item.setSubMenu(subMenu);
		return subMenu;
	}

	@Override
	public void clear() {
		this.mItems.clear();
	}

	@Override
	public void close() {}

	@Override
	public MenuItemImpl findItem(int itemId) {
		for (MenuItemImpl item : this.mItems) {
			if (item.getItemId() == itemId) {
				return item;
			}
		}
		return null;
	}

	@Override
	public MenuItemImpl getItem(int index) {
		return this.mItems.get(index);
	}

	@Override
	public boolean hasVisibleItems() {
		for (MenuItem item : this.mItems) {
			if (item.isVisible()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void removeItem(int itemId) {
		final int size = this.mItems.size();
		for (int i = 0; i < size; i++) {
			if (this.mItems.get(i).getItemId() == itemId) {
				this.mItems.remove(i);
				return;
			}
		}
	}

	@Override
	public int size() {
		return this.mItems.size();
	}
	
	@Override
	public int addIntentOptions(int groupId, int itemId, int order, ComponentName caller, Intent[] specifics, Intent intent, int flags, android.view.MenuItem[] outSpecificItems) {
		PackageManager pm = mContext.getPackageManager();
		final List<ResolveInfo> lri =
				pm.queryIntentActivityOptions(caller, specifics, intent, 0);
		final int N = lri != null ? lri.size() : 0;

		if ((flags & FLAG_APPEND_TO_GROUP) == 0) {
			removeGroup(groupId);
		}

		for (int i=0; i<N; i++) {
			final ResolveInfo ri = lri.get(i);
			Intent rintent = new Intent(
				ri.specificIndex < 0 ? intent : specifics[ri.specificIndex]);
			rintent.setComponent(new ComponentName(
					ri.activityInfo.applicationInfo.packageName,
					ri.activityInfo.name));
			final MenuItem item = add(groupId, itemId, order, ri.loadLabel(pm))
					.setIcon(ri.loadIcon(pm))
					.setIntent(rintent);
			if (outSpecificItems != null && ri.specificIndex >= 0) {
				outSpecificItems[ri.specificIndex] = item;
			}
		}

		return N;
	}

	@Override
	public boolean isShortcutKey(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean performIdentifierAction(int id, int flags) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
		return false;
	}

	@Override
	public void removeGroup(int groupId) {
		final int size = this.mItems.size();
		for (int i = 0; i < size; i++) {
			if (this.mItems.get(i).getGroupId() == groupId) {
				this.mItems.remove(i);
			}
		}
	}

	@Override
	public void setGroupCheckable(int groupId, boolean checkable, boolean exclusive) {
		final int N = mItems.size();
		for (int i = 0; i < N; i++) {
			MenuItemImpl item = mItems.get(i);
			if (item.getGroupId() == groupId) {
				item.setExclusiveCheckable(exclusive);
				item.setCheckable(checkable);
			}
		}
	}

	@Override
	public void setGroupEnabled(int groupId, boolean enabled) {
		final int size = this.mItems.size();
		for (int i = 0; i < size; i++) {
			MenuItemImpl item = mItems.get(i);
			if (item.getGroupId() == groupId) {
				item.setEnabled(enabled);
			}
		}
	}

	@Override
	public void setGroupVisible(int groupId, boolean visible) {
		final int size = this.mItems.size();
		for (int i = 0; i < size; i++) {
			MenuItemImpl item = mItems.get(i);
			if (item.getGroupId() == groupId) {
				item.setVisible(visible);
			}
		}
	}

	@Override
	public void setQwertyMode(boolean isQwerty) {
		throw new RuntimeException("Method not supported.");
	}
}