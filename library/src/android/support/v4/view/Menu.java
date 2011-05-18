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

package android.support.v4.view;

import android.content.ComponentName;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SubMenu;

/**
 * <p>Interface for managing the items in a menu.</p>
 * 
 * <p>By default, every Activity supports an options menu of actions or options.
 * You can add items to this menu and handle clicks on your additions. The
 * easiest way of adding menu items is inflating an XML file into the
 * {@link Menu} via {@link android.view.MenuInflater}. The easiest way of
 * attaching code to clicks is via
 * {@link android.support.v4.app.Activity#onOptionsItemSelected(MenuItem)}
 * and
 * {@link android.support.v4.app.Activity#onContextItemSelected(MenuItem)}.
 * </p>
 * 
 * <p>Different menu types support different features:
 * <ol>
 *  <li><b>Context menus</b>: Do not support item shortcuts and item icons.</li>
 *  <li><b>Options menus</b>: The <b>icon menus</b> do not support item check
 *  marks and only show the item's condensed title. The <b>expanded menus</b>
 *  (only available if six or more menu items are visible, reached via the
 *  'More' item in the icon menu) do not show item icons, and item check marks
 *  are discouraged.</li>
 *  <li><b>Sub menus</b>: Do not support item icons, or nested sub menus.</li>
 * </ol>
 * </p>
 * 
 * <p>This class is actually a wrapper around {@link android.view.Menu} which
 * provides an additional method: {@link #getOverflowMenu()}. This should be
 * used when adding menu items programmatically where the items should be placed
 * on either the native action bar overflow menu (on 3.0+) or on the activity's
 * context menu (on pre-3.0).</p>
 */
public final class Menu implements android.view.Menu {
	private final android.view.Menu mMenu;
	
	public Menu(android.view.Menu menu) {
		this.mMenu = menu;
	}
	
	/**
	 * Get the overflow menu. This will either be the same instance as we are
	 * wrapping or the context menu of the activity.
	 * 
	 * @return Overflow {@link android.view.Menu}.
	 */
	public android.view.Menu getOverflowMenu() {
		return this.mMenu instanceof MenuBuilder
			? ((MenuBuilder)this.mMenu).getNativeMenu()
			: this;
	}

	@Override
	public MenuItem add(CharSequence title) {
		return this.mMenu.add(title);
	}

	@Override
	public MenuItem add(int titleRes) {
		return this.mMenu.add(titleRes);
	}

	@Override
	public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
		return this.mMenu.add(groupId, itemId, order, title);
	}

	@Override
	public MenuItem add(int groupId, int itemId, int order, int titleRes) {
		return this.mMenu.add(groupId, itemId, order, titleRes);
	}

	@Override
	public int addIntentOptions(int groupId, int itemId, int order, ComponentName caller, Intent[] specifics, Intent intent, int flags, MenuItem[] outSpecificItems) {
		return this.mMenu.addIntentOptions(groupId, itemId, order, caller, specifics, intent, flags, outSpecificItems);
	}

	@Override
	public SubMenu addSubMenu(CharSequence title) {
		return this.mMenu.addSubMenu(title);
	}

	@Override
	public SubMenu addSubMenu(int titleRes) {
		return this.mMenu.addSubMenu(titleRes);
	}

	@Override
	public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
		return this.mMenu.addSubMenu(groupId, itemId, order, title);
	}

	@Override
	public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
		return this.mMenu.addSubMenu(groupId, itemId, order, titleRes);
	}

	@Override
	public void clear() {
		this.mMenu.clear();
	}

	@Override
	public void close() {
		this.mMenu.close();
	}

	@Override
	public MenuItem findItem(int id) {
		return this.mMenu.findItem(id);
	}

	@Override
	public MenuItem getItem(int index) {
		return this.mMenu.getItem(index);
	}

	@Override
	public boolean hasVisibleItems() {
		return this.mMenu.hasVisibleItems();
	}

	@Override
	public boolean isShortcutKey(int keyCode, KeyEvent event) {
		return this.mMenu.isShortcutKey(keyCode, event);
	}

	@Override
	public boolean performIdentifierAction(int id, int flags) {
		return this.mMenu.performIdentifierAction(id, flags);
	}

	@Override
	public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
		return this.mMenu.performShortcut(keyCode, event, flags);
	}

	@Override
	public void removeGroup(int groupId) {
		this.mMenu.removeGroup(groupId);
	}

	@Override
	public void removeItem(int id) {
		this.mMenu.removeItem(id);
	}

	@Override
	public void setGroupCheckable(int group, boolean checkable, boolean exclusive) {
		this.mMenu.setGroupCheckable(group, checkable, exclusive);
	}

	@Override
	public void setGroupEnabled(int group, boolean enabled) {
		this.mMenu.setGroupEnabled(group, enabled);
	}

	@Override
	public void setGroupVisible(int group, boolean visible) {
		this.mMenu.setGroupVisible(group, visible);
	}

	@Override
	public void setQwertyMode(boolean isQwerty) {
		this.mMenu.setQwertyMode(isQwerty);
	}

	@Override
	public int size() {
		return this.mMenu.size();
	}
}
