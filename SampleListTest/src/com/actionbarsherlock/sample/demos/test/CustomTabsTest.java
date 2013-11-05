package com.actionbarsherlock.sample.demos.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.actionbarsherlock.sample.demos.CustomTabs;

public class CustomTabsTest extends
		ActivityInstrumentationTestCase2<CustomTabs> {

	private Activity mActivity;
	private TextView mTextView;

	public CustomTabsTest() {
		super("com.actionbarsherlock.sample.demos", CustomTabs.class);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		mActivity = this.getActivity();
		mTextView = (TextView) mActivity
				.findViewById(com.actionbarsherlock.sample.demos.R.id.textView1);

	}

}
