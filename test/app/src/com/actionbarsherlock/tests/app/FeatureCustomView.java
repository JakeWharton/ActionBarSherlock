package com.actionbarsherlock.tests.app;

import java.util.concurrent.CountDownLatch;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public final class FeatureCustomView extends FragmentActivity {
	private final CountDownLatch setCustomViewLatch = new CountDownLatch(1);
	private final CountDownLatch enableCustomViewLatch = new CountDownLatch(1);
	
	public TextView customView;
	
	private final Runnable setCustomViewRunnable = new Runnable() {
		@Override
		public void run() {
			getSupportActionBar().setDisplayShowCustomEnabled(false);
			customView = new TextView(FeatureCustomView.this);
			customView.setText("Custom View!");
			customView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			getSupportActionBar().setCustomView(customView);
			setCustomViewLatch.countDown();
		}
	};
	private final Runnable enabledCustomViewRunnable = new Runnable() {
		@Override
		public void run() {
			getSupportActionBar().setDisplayShowCustomEnabled(true);
			enableCustomViewLatch.countDown();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blank);
	}

	public void setCustomView() throws InterruptedException {
		runOnUiThread(setCustomViewRunnable);
		setCustomViewLatch.await();
	}
	
	public void enableCustomView() throws InterruptedException {
		runOnUiThread(enabledCustomViewRunnable);
		enableCustomViewLatch.await();
	}
}
