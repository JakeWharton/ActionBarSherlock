package com.actionbarsherlock.tests.runner;

import com.actionbarsherlock.internal.view.menu.MenuBuilder;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.test.AndroidTestCase;

public class TestIssue0039 extends AndroidTestCase {
    private static final int ITEM_A_ID = 1;
    private static final int ITEM_B_ID = 2;
    private static final int ITEM_C_ID = 3;
    private static final int ITEM_D_ID = 4;
    private static final int ITEM_E_ID = 5;
    
    private MenuBuilder mMenu;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mMenu = new MenuBuilder(getContext());

        // Category SECONDARY, SHOW_AS_ACTION_ALWAYS 
        mMenu.add( Menu.CATEGORY_SECONDARY, ITEM_A_ID, 0, "ItemA" )
                .setShowAsAction( MenuItem.SHOW_AS_ACTION_ALWAYS );

        // Category NONE, SHOW_AS_ACTION_ALWAYS 
        mMenu.add( Menu.NONE, ITEM_B_ID, 0, "ItemB" )
                .setShowAsAction( MenuItem.SHOW_AS_ACTION_ALWAYS );

        // Category NONE, SHOW_AS_ACTION_IF_ROOM
        mMenu.add( Menu.NONE, ITEM_C_ID, 0, "ItemC" )
                .setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );

        // Category SECONDARY, SHOW_AS_ACTION_IF_ROOM, Order 1
        mMenu.add( Menu.CATEGORY_SECONDARY, ITEM_D_ID, 1, "ItemD" )
                .setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );

        // Category SECONDARY, SHOW_AS_ACTION_IF_ROOM, Order 0
        mMenu.add( Menu.CATEGORY_SECONDARY, ITEM_E_ID, 0, "ItemE" )
                .setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
    }

    public void testMenuInflationNotHonoringCategory() {
        /*
        ItemB <- SHOW_AS_ACTION_ALWAYS , Category NONE
        ItemC <- SHOW_AS_ACTION_IF_ROOM, Category NONE
        ItemA <- SHOW_AS_ACTION_ALWAYS , Category SECONDARY, Order 0
        ItemE <- SHOW_AS_ACTION_IF_ROOM, Category SECONDARY, Order 0 but added later
        ItemD <- SHOW_AS_ACTION_IF_ROOM, Category SECONDARY, Order 1
        */
        
        assertEquals(ITEM_B_ID, mMenu.getItem(0).getItemId());
        assertEquals(ITEM_C_ID, mMenu.getItem(0).getItemId());
        assertEquals(ITEM_A_ID, mMenu.getItem(0).getItemId());
        assertEquals(ITEM_E_ID, mMenu.getItem(0).getItemId());
        assertEquals(ITEM_D_ID, mMenu.getItem(0).getItemId());
    }
}
