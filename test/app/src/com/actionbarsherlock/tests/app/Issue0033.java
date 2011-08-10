package com.actionbarsherlock.tests.app;

import java.util.concurrent.CountDownLatch;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;

public final class Issue0033 extends FragmentActivity {
    public static final int ID_VISIBLE = 1;
    public static final int ID_HIDDEN = 2;
    public static final String TEXT_VISIBLE = "Test1";
    public static final String TEXT_HIDDEN = "Test2";

    public MenuItem menuItemNativeVisible;
    public MenuItem menuItemNativeXmlHidden;
    public MenuItem menuItemNativeCodeHidden;
    public MenuItem menuItemActionVisible;
    public MenuItem menuItemActionXmlHidden;
    public MenuItem menuItemActionCodeHidden;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuItemNativeVisible = menu.add(0, ID_VISIBLE, 0, TEXT_VISIBLE);
        menuItemNativeVisible.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        menuItemActionVisible = menu.add(0, 0, 0, TEXT_VISIBLE);
        menuItemActionVisible.setIcon(R.drawable.ic_menu_star_holo_light);
        menuItemActionVisible.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        getMenuInflater().inflate(R.menu.issue0033, menu);
        menuItemActionXmlHidden = menu.findItem(R.id.issue0033_action);
        menuItemNativeXmlHidden = menu.findItem(R.id.issue0033_native);

        menuItemNativeCodeHidden = menu.add(0, ID_HIDDEN, 0, TEXT_HIDDEN);
        menuItemNativeCodeHidden.setVisible(false);
        menuItemActionCodeHidden = menu.add(0, 0, 0, TEXT_HIDDEN);
        menuItemActionCodeHidden.setIcon(R.drawable.ic_menu_star_holo_light);
        menuItemActionCodeHidden.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItemActionCodeHidden.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ID_VISIBLE:
            case ID_HIDDEN:
            case R.id.issue0033_native:
                return item.isVisible();
        }
        return super.onOptionsItemSelected(item);
    }

    public String getXmlHiddenText() {
        return getString(R.string.issue0033_test);
    }

    public void hideVisibleMenuItems() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                menuItemActionVisible.setVisible(false);
                menuItemNativeVisible.setVisible(false);
                latch.countDown();
            }
        });
        latch.await();
    }
}
