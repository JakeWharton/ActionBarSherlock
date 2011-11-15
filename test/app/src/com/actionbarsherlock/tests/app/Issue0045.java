package com.actionbarsherlock.tests.app;

import java.util.concurrent.CountDownLatch;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;

public final class Issue0045 extends SherlockActivity {
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