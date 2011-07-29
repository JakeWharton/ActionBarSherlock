package com.actionbarsherlock.tests.app;

import java.util.concurrent.CountDownLatch;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;

public final class Issue0030 extends FragmentActivity {
	private boolean performFindItem = false;
	private boolean performRemoveItem = false;
	private MenuItem findItemResult;
	
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	if (performFindItem) {
    		findItemResult = menu.findItem(1);
    	} else if (performRemoveItem) {
    		menu.removeItem(1);
    	}
		return false;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank);
    }
    
    public MenuItem performFindItem() throws InterruptedException {
    	performFindItem = true;
    	final CountDownLatch latch = new CountDownLatch(1);
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				invalidateOptionsMenu();
				latch.countDown();
			}
		});
    	latch.await();
    	performFindItem = false;
    	return findItemResult;
    }
    
    public void performRemoveItem() throws InterruptedException {
    	performRemoveItem = true;
    	final CountDownLatch latch = new CountDownLatch(1);
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				invalidateOptionsMenu();
				latch.countDown();
			}
		});
    	latch.await();
    	performRemoveItem = false;
    }
}