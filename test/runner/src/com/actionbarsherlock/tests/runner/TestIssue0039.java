package com.actionbarsherlock.tests.runner;

import com.actionbarsherlock.internal.view.menu.MenuBuilder;
import android.content.Context;
import android.test.AndroidTestCase;
import android.view.Menu;

public class TestIssue0039 extends AndroidTestCase {
    private static final int ITEM_A_ID = 1;
    private static final int ITEM_B_ID = 2;
    private static final int ITEM_C_ID = 3;
    private static final int ITEM_D_ID = 4;
    private static final int ITEM_E_ID = 5;

    public void testMenuInflationOrderAndCategory() throws Exception {
        Menu supportMenu = new MenuBuilder(getContext());
        addMenuItems(supportMenu);
        
        Class<?> nativeClass = Class.forName("com.android.internal.view.menu.MenuBuilder");
        Menu nativeMenu = (Menu)nativeClass.getConstructor(Context.class).newInstance(getContext());
        addMenuItems(nativeMenu);
        
        assertEquals(nativeMenu.size(), supportMenu.size());
        assertEquals(5, supportMenu.size());
        assertEquals(nativeMenu.getItem(0).getItemId(), supportMenu.getItem(0).getItemId());
        assertEquals(nativeMenu.getItem(1).getItemId(), supportMenu.getItem(1).getItemId());
        assertEquals(nativeMenu.getItem(2).getItemId(), supportMenu.getItem(2).getItemId());
        assertEquals(nativeMenu.getItem(3).getItemId(), supportMenu.getItem(3).getItemId());
        assertEquals(nativeMenu.getItem(4).getItemId(), supportMenu.getItem(4).getItemId());
    }
    
    private static void addMenuItems(Menu target) { 
        target.add(Menu.CATEGORY_SECONDARY, ITEM_A_ID, 0, "A");
        target.add(Menu.NONE              , ITEM_B_ID, 0, "B");
        target.add(Menu.NONE              , ITEM_C_ID, 0, "C");
        target.add(Menu.CATEGORY_SECONDARY, ITEM_D_ID, 1, "D");
        target.add(Menu.CATEGORY_SECONDARY, ITEM_E_ID, 0, "E");
    }
}
