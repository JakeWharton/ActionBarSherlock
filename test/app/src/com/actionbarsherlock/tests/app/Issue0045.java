package com.actionbarsherlock.tests.app;

import java.util.concurrent.CountDownLatch;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;

public final class Issue0045 extends FragmentActivity {
	Object mMenuItemSubMenu = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mMenuItemSubMenu = menu.addSubMenu("test").getItem().getSubMenu();
		return false;
	}
	
	public Object getMenuItemSubMenu() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				invalidateOptionsMenu();
				latch.countDown();
			}
		});
		latch.await();
		return mMenuItemSubMenu;
	}
}