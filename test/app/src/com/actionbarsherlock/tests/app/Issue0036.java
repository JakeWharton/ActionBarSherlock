package com.actionbarsherlock.tests.app;

import java.util.concurrent.CountDownLatch;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;

public final class Issue0036 extends FragmentActivity {
	Object subMenuItemParent = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		subMenuItemParent = menu.addSubMenu("test").getItem();
		return false;
	}
	
	public Object getSubMenuItemParent() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				invalidateOptionsMenu();
				latch.countDown();
			}
		});
		latch.await();
		return subMenuItemParent;
	}
}