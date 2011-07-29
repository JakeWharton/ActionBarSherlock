package com.actionbarsherlock.tests.app;

import java.util.concurrent.CountDownLatch;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public final class Issue0042 extends FragmentActivity {
	public static final String TITLE = "Hey, This Is A Test!";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank);
    }
	
	public void setTitleString() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				setTitle(TITLE);
				latch.countDown();
			}
		});
		latch.await();
	}
	
	public String setTitleResource() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				setTitle(R.string.issue0042_title);
				latch.countDown();
			}
		});
		latch.await();
		return getString(R.string.issue0042_title);
	}
	
	private CharSequence supportActionBarTitle = null;
	public CharSequence getSupportActionBarTitle() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				supportActionBarTitle = getSupportActionBar().getTitle();
				latch.countDown();
			}
		});
		latch.await();
		return supportActionBarTitle;
	}
}