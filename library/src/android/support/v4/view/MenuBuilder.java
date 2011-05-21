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

import java.util.ArrayList;
import java.util.List;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

/**
 * An implementation of the {@link android.view.Menu} interface for use in
 * inflating menu XML resources to be added to a third-party action bar. 
 * 
 * @author Jake Wharton <jakewharton@gmail.com>
 * @see <a href="http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob;f=core/java/com/android/internal/view/menu/MenuBuilder.java">com.android.internal.view.menu.MenuBuilder</a>
 */
//TODO make extends ArrayList<MenuItemImpl>
public class MenuBuilder implements Menu {
	private static final int DEFAULT_ITEM_ID = 0;
	private static final int DEFAULT_GROUP_ID = 0;
	private static final int DEFAULT_ORDER = 0;
	
	
	
	/**
	 * Context used for resolving any resources.
	 */
	private final Context mContext;
	
	/**
	 * Child {@link ActionBarMenuItem} items.
	 */
	private final List<MenuItemImpl> mItems;
	
	
	
	/**
	 * Create a new action bar menu.
	 * 
	 * @param context Context used if resource resolution is required.
	 */
	public MenuBuilder(Context context) {
		this.mContext = context;
		this.mItems = new ArrayList<MenuItemImpl>();
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
		String title = this.mContext.getResources().getString(titleResourceId);
		return this.add(groupId, itemId, order, title);
	}

	@Override
	public MenuItemImpl add(int groupId, int itemId, int order, CharSequence title) {
		MenuItemImpl item = new MenuItemImpl(this.mContext, itemId, groupId, order, title);
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
		throw new IndexOutOfBoundsException("No item with id " + itemId);
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
		throw new IndexOutOfBoundsException("No item with id " + itemId);
	}

	@Override
	public int size() {
		return this.mItems.size();
	}
	
	@Override
	public int addIntentOptions(int groupId, int itemId, int order, ComponentName caller, Intent[] specifics, Intent intent, int flags, android.view.MenuItem[] outSpecificItems) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public boolean isShortcutKey(int keyCode, KeyEvent event) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public boolean performIdentifierAction(int id, int flags) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public void removeGroup(int groupId) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public void setGroupCheckable(int groupId, boolean checkable, boolean exclusive) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public void setGroupEnabled(int groupId, boolean enabled) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public void setGroupVisible(int groupId, boolean visible) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public void setQwertyMode(boolean isQwerty) {
		throw new RuntimeException("Method not supported.");
	}
}