package com.actionbarsherlock.internal.view.menu;

import java.util.WeakHashMap;
import android.content.ComponentName;
import android.content.Intent;
import android.view.KeyEvent;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

/**
 * 
 * MenuWrapper class is used
 * 
 */
public class MenuWrapper implements Menu {
	private final android.view.Menu mNativeMenu;

	private final WeakHashMap<android.view.MenuItem, MenuItem> mNativeMap = new WeakHashMap<android.view.MenuItem, MenuItem>();

	public MenuWrapper(android.view.Menu nativeMenu) {
		mNativeMenu = nativeMenu;
	}

	public android.view.Menu unwrap() {
		return mNativeMenu;
	}

	/**
	 * Creates a new {@code MenuItemWrapper} using the given {@code nativeItem}
	 * and puts the new {@code MenuItemWrapper} to {@link WeakHashMap} with
	 * {@code mNativeMap} as key.
	 * 
	 * @param nativeItem
	 * @return new {@code MenuItem} with {@code nativeItem} inside it.
	 */
	private MenuItem addInternal(android.view.MenuItem nativeItem) {
		MenuItem item = new MenuItemWrapper(nativeItem);
		mNativeMap.put(nativeItem, item);
		return item;
	}

	@Override
	public MenuItem add(CharSequence title) {
		return addInternal(mNativeMenu.add(title));
	}

	@Override
	public MenuItem add(int titleRes) {
		return addInternal(mNativeMenu.add(titleRes));
	}

	@Override
	public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
		return addInternal(mNativeMenu.add(groupId, itemId, order, title));
	}

	@Override
	public MenuItem add(int groupId, int itemId, int order, int titleRes) {
		return addInternal(mNativeMenu.add(groupId, itemId, order, titleRes));
	}

	/**
	 * Creates a new {@code SubMenu} using the given {@code nativeSubMenu}. Then
	 * gets the {@code MenuItem} from this new {@code Submenu} and and puts the
	 * new {@code MenuItem} to {@link WeakHashMap} with {@code mNativeMap} as
	 * key.
	 * 
	 * @param nativeSubMenu
	 *            see {@link android.view.SubMenu}
	 * @return the {@code SubMenu} created using the passedF
	 *         {@code nativeSubMenu}
	 */
	private SubMenu addInternal(android.view.SubMenu nativeSubMenu) {
		SubMenu subMenu = new SubMenuWrapper(nativeSubMenu);
		android.view.MenuItem nativeItem = nativeSubMenu.getItem();
		MenuItem item = subMenu.getItem();
		mNativeMap.put(nativeItem, item);
		return subMenu;
	}

	@Override
	public SubMenu addSubMenu(CharSequence title) {
		return addInternal(mNativeMenu.addSubMenu(title));
	}

	@Override
	public SubMenu addSubMenu(int titleRes) {
		return addInternal(mNativeMenu.addSubMenu(titleRes));
	}

	@Override
	public SubMenu addSubMenu(int groupId, int itemId, int order,
			CharSequence title) {
		return addInternal(mNativeMenu
				.addSubMenu(groupId, itemId, order, title));
	}

	@Override
	public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
		return addInternal(mNativeMenu.addSubMenu(groupId, itemId, order,
				titleRes));
	}

	@Override
	public int addIntentOptions(int groupId, int itemId, int order,
			ComponentName caller, Intent[] specifics, Intent intent, int flags,
			MenuItem[] outSpecificItems) {
		int result;
		if (outSpecificItems != null) {
			android.view.MenuItem[] nativeOutItems = new android.view.MenuItem[outSpecificItems.length];
			result = mNativeMenu.addIntentOptions(groupId, itemId, order,
					caller, specifics, intent, flags, nativeOutItems);
			for (int i = 0, length = outSpecificItems.length; i < length; i++) {
				outSpecificItems[i] = new MenuItemWrapper(nativeOutItems[i]);
			}
		} else {
			result = mNativeMenu.addIntentOptions(groupId, itemId, order,
					caller, specifics, intent, flags, null);
		}
		return result;
	}

	@Override
	public void removeItem(int id) {
		mNativeMap.remove(mNativeMenu.findItem(id));
		mNativeMenu.removeItem(id);
	}

	@Override
	public void removeGroup(int groupId) {
		for (int i = 0; i < mNativeMenu.size(); i++) {
			final android.view.MenuItem item = mNativeMenu.getItem(i);
			if (item.getGroupId() == groupId) {
				mNativeMap.remove(item);
			}
		}
		mNativeMenu.removeGroup(groupId);
	}

	@Override
	public void clear() {
		mNativeMap.clear();
		mNativeMenu.clear();
	}

	/**
	 * if {@code mNativeMap} is not empty all of its items are put to
	 * {@code WeakHashMap} and after clearing {@code mNativeMap} put as its
	 * values
	 * 
	 */
	public void invalidate() {
		if (mNativeMap.isEmpty())
			return;

		final WeakHashMap<android.view.MenuItem, MenuItem> menuMapCopy = new WeakHashMap<android.view.MenuItem, MenuItem>(
				mNativeMap.size());

		for (int i = 0; i < mNativeMenu.size(); i++) {
			final android.view.MenuItem item = mNativeMenu.getItem(i);
			menuMapCopy.put(item, mNativeMap.get(item));
		}

		mNativeMap.clear();
		mNativeMap.putAll(menuMapCopy);
	}

	@Override
	public void setGroupCheckable(int group, boolean checkable,
			boolean exclusive) {
		mNativeMenu.setGroupCheckable(group, checkable, exclusive);
	}

	@Override
	public void setGroupVisible(int group, boolean visible) {
		mNativeMenu.setGroupVisible(group, visible);
	}

	@Override
	public void setGroupEnabled(int group, boolean enabled) {
		mNativeMenu.setGroupEnabled(group, enabled);
	}

	@Override
	public boolean hasVisibleItems() {
		return mNativeMenu.hasVisibleItems();
	}

	@Override
	public MenuItem findItem(int id) {
		android.view.MenuItem nativeItem = mNativeMenu.findItem(id);
		return findItem(nativeItem);
	}

	/**
	 * Checks if the given {@code nativeItem} is not null tries to get
	 * the value from {@code mNativeMap} using {@code nativeItem} as a
	 * key.
	 * 
	 * @param nativeItem
	 * @return new {@code MenuItem} with {@code nativeItem} inside it.
	 */
	public MenuItem findItem(android.view.MenuItem nativeItem) {
		if (nativeItem == null) {
			return null;
		}

		MenuItem wrapped = mNativeMap.get(nativeItem);
		if (wrapped != null) {
			return wrapped;
		}

		return addInternal(nativeItem);
	}

	@Override
	public int size() {
		return mNativeMenu.size();
	}

	@Override
	public MenuItem getItem(int index) {
		android.view.MenuItem nativeItem = mNativeMenu.getItem(index);
		return findItem(nativeItem);
	}

	@Override
	public void close() {
		mNativeMenu.close();
	}

	@Override
	public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
		return mNativeMenu.performShortcut(keyCode, event, flags);
	}

	@Override
	public boolean isShortcutKey(int keyCode, KeyEvent event) {
		return mNativeMenu.isShortcutKey(keyCode, event);
	}

	@Override
	public boolean performIdentifierAction(int id, int flags) {
		return mNativeMenu.performIdentifierAction(id, flags);
	}

	@Override
	public void setQwertyMode(boolean isQwerty) {
		mNativeMenu.setQwertyMode(isQwerty);
	}
}
