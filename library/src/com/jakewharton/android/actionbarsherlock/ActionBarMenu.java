package com.jakewharton.android.actionbarsherlock;

import java.util.ArrayList;
import java.util.List;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

/*
 * See: com.android.internal.view.menu.MenuBuilder
 */
public final class ActionBarMenu implements Menu {
	private static final int DEFAULT_ITEM_ID = 0;
	private static final int DEFAULT_GROUP_ID = 0;
	private static final int DEFAULT_ORDER = 0;
	
	private final Context mContext;
	private final List<ActionBarMenuItem> mItems;
	
	public ActionBarMenu(Context context) {
		this.mContext = context;
		this.mItems = new ArrayList<ActionBarMenuItem>();
	}
	
	
	@Override
	public MenuItem add(CharSequence title) {
		ActionBarMenuItem item = new ActionBarMenuItem(this.mContext, DEFAULT_ITEM_ID, DEFAULT_GROUP_ID, DEFAULT_ORDER, title);
		this.mItems.add(item);
		return item;
	}

	@Override
	public MenuItem add(int titleResourceId) {
		ActionBarMenuItem item = new ActionBarMenuItem(this.mContext, 0, 0, 0, titleResourceId);
		this.mItems.add(item);
		return item;
	}

	@Override
	public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
		ActionBarMenuItem item = new ActionBarMenuItem(this.mContext, itemId, groupId, order, title);
		this.mItems.add(item);
		return item;
	}

	@Override
	public MenuItem add(int groupId, int itemId, int order, int titleResourceId) {
		ActionBarMenuItem item = new ActionBarMenuItem(this.mContext, itemId, groupId, order, titleResourceId);
		this.mItems.add(item);
		return item;
	}

	@Override
	public void clear() {
		this.mItems.clear();
	}

	@Override
	public void close() {}

	@Override
	public MenuItem findItem(int itemId) {
		for (MenuItem item : this.mItems) {
			if (item.getItemId() == itemId) {
				return item;
			}
		}
		throw new IndexOutOfBoundsException("No item with id " + itemId);
	}

	@Override
	public MenuItem getItem(int index) {
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
	
	public List<ActionBarMenuItem> getItems() {
		return this.mItems;
	}

	@Override
	public int addIntentOptions(int groupId, int itemId, int order, ComponentName caller, Intent[] specifics, Intent intent, int flags, MenuItem[] outSpecificItems) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public SubMenu addSubMenu(CharSequence title) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public SubMenu addSubMenu(int titleResourceId) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public SubMenu addSubMenu(int groupId, int itemId, int order, int titleResourceId) {
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