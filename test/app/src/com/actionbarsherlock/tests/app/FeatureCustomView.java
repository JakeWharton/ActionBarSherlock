package com.actionbarsherlock.tests.app;

import java.util.concurrent.CountDownLatch;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;

public final class FeatureCustomView extends SherlockActivity {
	public TextView customView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blank);
	}

	public void setCustomView() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				getSupportActionBar().setDisplayShowCustomEnabled(false);
				customView = new TextView(FeatureCustomView.this);
				customView.setText("Custom View!");
				customView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				getSupportActionBar().setCustomView(customView);
				latch.countDown();
			}
		});
		latch.await();
	}
	
	public void enableCustomView() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				getSupportActionBar().setDisplayShowCustomEnabled(true);
				latch.countDown();
			}
		});
		latch.await();
	}
}
