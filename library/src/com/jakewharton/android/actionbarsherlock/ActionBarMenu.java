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

import java.util.ArrayList;
import java.util.List;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

/**
 * An implementation of the {@link android.view.Menu} interface for use in
 * inflating menu XML resources to be added to a third-party action bar. 
 * 
 * @author Jake Wharton <jakewharton@gmail.com>
 * @see {@link com.android.internal.view.MenuBuilder}
 */
public class ActionBarMenu implements Menu {
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
	private final List<ActionBarMenuItem> mItems;
	
	
	/**
	 * Create a new action bar menu.
	 * 
	 * @param context Context used if resource resolution is required.
	 */
	public ActionBarMenu(Context context) {
		this.mContext = context;
		this.mItems = new ArrayList<ActionBarMenuItem>();
	}

	
	/**
	 * Get a list of the items contained in this menu.
	 * 
	 * @return List of {@link ActionBarMenuItem}s.
	 */
	public List<ActionBarMenuItem> getItems() {
		return this.mItems;
	}
	
	/**
	 * Get a list of the titles contained in the menu.
	 * 
	 * @return String list.
	 */
	public String[] getTitles() {
		String[] result = new String[this.mItems.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = this.mItems.get(i).getTitle().toString();
		}
		return result;
	}
	
	// ** Menu Methods ** \\
	
	@Override
	public ActionBarMenuItem add(CharSequence title) {
		return this.add(DEFAULT_ITEM_ID, DEFAULT_GROUP_ID, DEFAULT_ORDER, title);
	}

	@Override
	public ActionBarMenuItem add(int titleResourceId) {
		return this.add(DEFAULT_GROUP_ID, DEFAULT_ITEM_ID, DEFAULT_ORDER, titleResourceId);
	}

	@Override
	public ActionBarMenuItem add(int groupId, int itemId, int order, int titleResourceId) {
		String title = this.mContext.getResources().getString(titleResourceId);
		return this.add(groupId, itemId, order, title);
	}

	@Override
	public ActionBarMenuItem add(int groupId, int itemId, int order, CharSequence title) {
		ActionBarMenuItem item = new ActionBarMenuItem(this.mContext, itemId, groupId, order, title);
		this.mItems.add(item);
		return item;
	}

	@Override
	public ActionBarSubMenu addSubMenu(CharSequence title) {
		return this.addSubMenu(DEFAULT_GROUP_ID, DEFAULT_ITEM_ID, DEFAULT_ORDER, title);
	}

	@Override
	public ActionBarSubMenu addSubMenu(int titleResourceId) {
		return this.addSubMenu(DEFAULT_GROUP_ID, DEFAULT_ITEM_ID, DEFAULT_ORDER, titleResourceId);
	}

	@Override
	public ActionBarSubMenu addSubMenu(int groupId, int itemId, int order, int titleResourceId) {
		String title = this.mContext.getResources().getString(titleResourceId);
		return this.addSubMenu(groupId, itemId, order, title);
	}

	@Override
	public ActionBarSubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
		ActionBarMenuItem item = this.add(groupId, itemId, order, title);
		ActionBarSubMenu subMenu = new ActionBarSubMenu(this.mContext, this, item);
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
	public ActionBarMenuItem findItem(int itemId) {
		for (ActionBarMenuItem item : this.mItems) {
			if (item.getItemId() == itemId) {
				return item;
			}
		}
		throw new IndexOutOfBoundsException("No item with id " + itemId);
	}

	@Override
	public ActionBarMenuItem getItem(int index) {
		return this.mItems.get(index);
	}

	@Override
	public boolean hasVisibleItems() {
		for (ActionBarMenuItem item : this.mItems) {
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
	public int addIntentOptions(int groupId, int itemId, int order, ComponentName caller, Intent[] specifics, Intent intent, int flags, MenuItem[] outSpecificItems) {
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