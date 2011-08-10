package com.actionbarsherlock.tests.app;

import java.util.concurrent.CountDownLatch;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;

public final class Issue0033 extends FragmentActivity {
    public static final int ITEM_ID = 1;
    public static final String ITEM_TEXT = "Test";
    
    private MenuItem mMenuItemNative;
    private MenuItem mMenuItemAction;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank);
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    mMenuItemNative = menu.add(0, ITEM_ID, 0, ITEM_TEXT);
	    mMenuItemNative.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
	    
	    mMenuItemAction = menu.add(0, ITEM_ID, 0, ITEM_TEXT);
	    mMenuItemAction.setIcon(R.drawable.ic_menu_star_holo_light);
	    mMenuItemAction.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	    
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == ITEM_ID) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public MenuItem getMenuItemAction() {
        return mMenuItemAction;
    }
	
	public MenuItem getMenuItemNative() {
	    return mMenuItemNative;
	}
    
    public void hideMenuItemAction() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMenuItemAction.setVisible(false);
                latch.countDown();
            }
        });
        latch.await();
    }
	
    public void hideMenuItemNative() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMenuItemNative.setVisible(false);
                latch.countDown();
            }
        });
        latch.await();
    }
}