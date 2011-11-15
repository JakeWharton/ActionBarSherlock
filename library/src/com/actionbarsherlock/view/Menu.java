package com.actionbarsherlock.view;

import android.content.ComponentName;
import android.content.Intent;
import android.view.KeyEvent;

public interface Menu {
	public static final int CATEGORY_ALTERNATIVE = android.view.Menu.CATEGORY_ALTERNATIVE;
	
	public static final int CATEGORY_CONTAINER = android.view.Menu.CATEGORY_CONTAINER;
	
	public static final int CATEGORY_SECONDARY = android.view.Menu.CATEGORY_SECONDARY;
	
	public static final int CATEGORY_SYSTEM = android.view.Menu.CATEGORY_SYSTEM;
	
	public static final int FIRST = android.view.Menu.FIRST;
	
	public static final int FLAG_ALWAYS_PERFORM_CLOSE = android.view.Menu.FLAG_ALWAYS_PERFORM_CLOSE;
	
	public static final int FLAG_APPEND_TO_GROUP = android.view.Menu.FLAG_APPEND_TO_GROUP;
	
	public static final int FLAG_PERFORM_NO_CLOSE = android.view.Menu.FLAG_PERFORM_NO_CLOSE;
	
	public static final int NONE = android.view.Menu.NONE;
	
	
    MenuItem add(CharSequence title);

    MenuItem add(int groupId, int itemId, int order, int titleRes);

    MenuItem add(int titleRes);

    MenuItem add(int groupId, int itemId, int order, CharSequence title);
    
    int addIntentOptions(int groupId, int itemId, int order, ComponentName caller, Intent[] specifics, Intent intent, int flags, MenuItem[] outSpecificItems);

    SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title);

    SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes);

    SubMenu addSubMenu(CharSequence title);

    SubMenu addSubMenu(int titleRes);
    
    void clear();
    
    void close();

    MenuItem findItem(int id);

    MenuItem getItem(int index);
    
    boolean hasVisibleItems();
    
    boolean isShortcutKey(int keyCode, KeyEvent event);
    
    boolean performIdentifierAction(int id, int flags);
    
    boolean performShortcut(int keyCode, KeyEvent event, int flags);
    
    void removeGroup(int groupId);
    
    void removeItem(int id);
    
    void setGroupCheckable(int group, boolean checkable, boolean exclusive);
    
    void setGroupEnabled(int group, boolean enabled);
    
    void setGroupVisible(int group, boolean visible);
    
    void setQwertyMode(boolean isQwerty);
    
    int size();
}
