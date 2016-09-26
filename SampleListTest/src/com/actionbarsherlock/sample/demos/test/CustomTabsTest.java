package com.actionbarsherlock.sample.demos.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.TextView;

import com.actionbarsherlock.sample.demos.AnimatedActionItem;
import com.actionbarsherlock.sample.demos.CustomTabs;
import com.jayway.android.robotium.solo.Solo;

public class CustomTabsTest extends
		ActivityInstrumentationTestCase2<CustomTabs> {

	private Activity mActivity;
	private TextView mDescription, mSelected;
	String mTabsDescription, mTabsDescription0, mTabsDescription1,
			mTabsDescription2;
	private Solo solo;

	public CustomTabsTest() {
		super("com.actionbarsherlock.sample.demos", CustomTabs.class);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		
		mActivity = this.getActivity();
		mDescription = (TextView) mActivity
				.findViewById(com.actionbarsherlock.sample.demos.R.id.tab_navigation_description);
		mSelected = (TextView) mActivity
				.findViewById(com.actionbarsherlock.sample.demos.R.id.text);
		mTabsDescription = mActivity
				.getString(com.actionbarsherlock.sample.demos.R.string.tab_navigation_content);
		mTabsDescription0 = mActivity
				.getString(com.actionbarsherlock.sample.demos.R.string.custom_view_content);
		mTabsDescription1 = mTabsDescription2 = mActivity
				.getString(com.actionbarsherlock.sample.demos.R.string.icon_tab_content);
	}

	@SmallTest
	public void testViews() {
		assertNotNull(mActivity);
		assertNotNull(mDescription);

	}

	// checking that the widgets are on screen
	@SmallTest
	public void testVisibility() {
		ViewAsserts.assertOnScreen(mActivity.getWindow().getDecorView(),
				mDescription);
		ViewAsserts.assertOnScreen(mActivity.getWindow().getDecorView(),
				mSelected);

	}

	@SmallTest
	public void testInitialValues() {
		assertEquals(mTabsDescription, (String) mDescription.getText());
		assertEquals("Selected: Tab 1", (String) mSelected.getText());
		solo.assertCurrentActivity("wrong activity", CustomTabs.class);
	}

	@SmallTest
	public void testDescriptionText() {
		solo.clickOnActionBarItem(0);
		assertEquals(mTabsDescription0, (String) mDescription.getText());
		solo.clickOnActionBarItem(1);
		assertEquals(mTabsDescription1, (String) mDescription.getText());
		solo.clickOnActionBarItem(2);
		assertEquals(mTabsDescription2, (String) mDescription.getText());

	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}
}
