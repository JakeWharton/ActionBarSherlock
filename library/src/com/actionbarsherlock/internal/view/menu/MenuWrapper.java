package com.actionbarsherlock.internal.view.menu;

import java.util.WeakHashMap;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

public class MenuWrapper implements Menu {
    private final android.view.Menu mNativeMenu;

    private final WeakHashMap<android.view.MenuItem, MenuItem> mNativeMap =
            new WeakHashMap<android.view.MenuItem, MenuItem>();


    public MenuWrapper(android.view.Menu nativeMenu) {
        mNativeMenu = nativeMenu;
    }


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
    public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
        return addInternal(mNativeMenu.addSubMenu(groupId, itemId, order, title));
    }

    @Override
    public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
        return addInternal(mNativeMenu.addSubMenu(groupId, itemId, order, titleRes));
    }

    @Override
    public int addIntentOptions(int groupId, int itemId, int order, ComponentName caller, Intent[] specifics, Intent intent, int flags, MenuItem[] outSpecificItems) {
        android.view.MenuItem[] nativeOutItems = new android.view.MenuItem[outSpecificItems.length];
        int result = mNativeMenu.addIntentOptions(groupId, itemId, order, caller, specifics, intent, flags, nativeOutItems);
        for (int i = 0, length = outSpecificItems.length; i < length; i++) {
            outSpecificItems[i] = new MenuItemWrapper(nativeOutItems[i]);
        }
        return result;
    }

    @Override
    public void removeItem(int id) {
        mNativeMenu.removeItem(id);
    }

    @Override
    public void removeGroup(int groupId) {
        mNativeMenu.removeGroup(groupId);
    }

    @Override
    public void clear() {
        mNativeMap.clear();
        mNativeMenu.clear();
    }

    @Override
    public void setGroupCheckable(int group, boolean checkable, boolean exclusive) {
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

    public MenuItem findItem(android.view.MenuItem nativeItem) {
    	if (nativeItem == null) {
    		return null;
    	}
    	MenuItem found = mNativeMap.get(nativeItem);
    	if (found != null) {
    		return found;
    	}

    	// special handling for the home item
    	// because it is not a part of mNativeMenu
    	// but still must be handled.
    	// Also check findItem(id), just to make this
    	// code bullet proof.
    	android.view.MenuItem nativeItem2 = mNativeMenu.findItem(nativeItem.getItemId());
    	if (nativeItem2 != nativeItem) {
    		Log.e("TEST", "findItem(nativeItem) - instances mismatch");
    		if (nativeItem.getItemId() == android.R.id.home && nativeItem2 == null) {
    			Log.e("TEST", "findItem(home)");
    			return new MenuItemWrapper(nativeItem);
    		}
    		return findItem(nativeItem2);
    	}

    	// this menuItem is a part of the native menu
    	// but not of us. This should never happen!
    	// But at least we know how to repair it and
    	// not cause an NPE.
    	if (nativeItem2 != null) {
    		return addInternal(nativeItem2);
    	}
        return null;
    }

    @Override
    public int size() {
        return mNativeMenu.size();
    }

    @Override
    public MenuItem getItem(int index) {
        android.view.MenuItem nativeItem = mNativeMenu.getItem(index);
        return (nativeItem != null) ? mNativeMap.get(nativeItem) : null;
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
