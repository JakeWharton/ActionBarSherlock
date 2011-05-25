package android.support.v4.view;

import android.content.ComponentName;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.SubMenu;

public final class MenuHoneycombWrapper implements Menu {
	private final android.view.Menu mMenu;
	
	public MenuHoneycombWrapper(android.view.Menu menu) {
		this.mMenu = menu;
	}
	
	android.view.Menu unwrap() {
		return mMenu;
	}
	
	@Override
	public MenuItem add(CharSequence title) {
		return new MenuItemHoneycombWrapper(mMenu.add(title));
	}

	@Override
	public MenuItem add(int groupId, int itemId, int order, int titleRes) {
		return new MenuItemHoneycombWrapper(mMenu.add(groupId, itemId, order, titleRes));
	}
	
	@Override
	public MenuItem add(int titleRes) {
		return new MenuItemHoneycombWrapper(mMenu.add(titleRes));
	}

	@Override
	public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
		return new MenuItemHoneycombWrapper(mMenu.add(groupId, itemId, order, title));
	}

	@Override
	public int addIntentOptions(int groupId, int itemId, int order, ComponentName caller, Intent[] specifics, Intent intent, int flags, android.view.MenuItem[] outSpecificItems) {
		return mMenu.addIntentOptions(groupId, itemId, order, caller, specifics, intent, flags, outSpecificItems);
	}

	@Override
	public SubMenu addSubMenu(CharSequence title) {
		return mMenu.addSubMenu(title);
	}

	@Override
	public SubMenu addSubMenu(int titleRes) {
		return mMenu.addSubMenu(titleRes);
	}

	@Override
	public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
		return mMenu.addSubMenu(groupId, itemId, order, title);
	}

	@Override
	public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
		return mMenu.addSubMenu(groupId, itemId, order, titleRes);
	}

	@Override
	public void clear() {
		mMenu.clear();
	}

	@Override
	public void close() {
		mMenu.close();
	}

	@Override
	public MenuItem findItem(int id) {
		return new MenuItemHoneycombWrapper(mMenu.findItem(id));
	}

	@Override
	public MenuItem getItem(int index) {
		return new MenuItemHoneycombWrapper(mMenu.getItem(index));
	}

	@Override
	public boolean hasVisibleItems() {
		return mMenu.hasVisibleItems();
	}

	@Override
	public boolean isShortcutKey(int keyCode, KeyEvent event) {
		return mMenu.isShortcutKey(keyCode, event);
	}

	@Override
	public boolean performIdentifierAction(int id, int flags) {
		return mMenu.performIdentifierAction(id, flags);
	}

	@Override
	public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
		return mMenu.performShortcut(keyCode, event, flags);
	}

	@Override
	public void removeGroup(int groupId) {
		mMenu.removeGroup(groupId);
	}

	@Override
	public void removeItem(int id) {
		mMenu.removeItem(id);
	}

	@Override
	public void setGroupCheckable(int group, boolean checkable, boolean exclusive) {
		mMenu.setGroupCheckable(group, checkable, exclusive);
	}

	@Override
	public void setGroupEnabled(int group, boolean enabled) {
		mMenu.setGroupEnabled(group, enabled);
	}

	@Override
	public void setGroupVisible(int group, boolean visible) {
		mMenu.setGroupVisible(group, visible);
	}

	@Override
	public void setQwertyMode(boolean isQwerty) {
		mMenu.setQwertyMode(isQwerty);
	}

	@Override
	public int size() {
		return mMenu.size();
	}
}
