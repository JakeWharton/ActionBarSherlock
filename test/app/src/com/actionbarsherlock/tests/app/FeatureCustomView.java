package com.actionbarsherlock.tests.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public final class FeatureCustomView extends FragmentActivity {
	public TextView customView;
	
	private final Handler setCustomViewHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			getSupportActionBar().setDisplayShowCustomEnabled(false);
			customView = new TextView(FeatureCustomView.this);
			customView.setText("Custom View!");
			customView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			getSupportActionBar().setCustomView(customView);
		}
	};
	
	private final Handler enabledCustomViewHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			getSupportActionBar().setDisplayShowCustomEnabled(true);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blank);
	}

	public void setCustomView() {
		setCustomViewHandler.sendEmptyMessage(0);
	}
	
	public void enableCustomView() {
		enabledCustomViewHandler.sendEmptyMessage(0);
	}
}
