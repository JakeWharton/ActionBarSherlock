package com.actionbarsherlock.sample.demos.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.actionbarsherlock.sample.demos.AnimatedActionItem;
import com.actionbarsherlock.view.MenuItem;
import com.jayway.android.robotium.solo.Solo;

public class AnimatedActionItemTest extends
		ActivityInstrumentationTestCase2<AnimatedActionItem> {
	private Solo solo;
	private Activity mActivity;
	private RadioGroup mRadioGroup;
	private TextView mTextView;
	private String introText, mRadioText0, mRadioText1, mRadioText2,
			mRadioText3, mToastText0, mToastText1, mToastText2, mToastText3;
	private RadioButton mRadioButton0, mRadioButton1, mRadioButton2,
			mRadioButton3;

	public AnimatedActionItemTest() {
		super("com.actionbarsherlock.sample.demos", AnimatedActionItem.class);

	}

	// initialising views and other variables for testing for testing
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		mActivity = this.getActivity();
		mRadioGroup = (RadioGroup) mActivity
				.findViewById(com.actionbarsherlock.sample.demos.R.id.radioGroup1);
		mTextView = (TextView) mActivity
				.findViewById(com.actionbarsherlock.sample.demos.R.id.textView1);
		introText = mActivity
				.getString(com.actionbarsherlock.sample.demos.R.string.animated_action_item_content);
		mRadioText0 = mActivity
				.getString(com.actionbarsherlock.sample.demos.R.string.rotate);
		mRadioButton0 = (RadioButton) mActivity
				.findViewById(com.actionbarsherlock.sample.demos.R.id.radio0);
		mRadioText1 = mActivity
				.getString(com.actionbarsherlock.sample.demos.R.string.vibrate);
		mRadioButton1 = (RadioButton) mActivity
				.findViewById(com.actionbarsherlock.sample.demos.R.id.radio1);
		mRadioText2 = mActivity
				.getString(com.actionbarsherlock.sample.demos.R.string.blink);
		mRadioButton2 = (RadioButton) mActivity
				.findViewById(com.actionbarsherlock.sample.demos.R.id.radio2);
		mRadioText3 = mActivity
				.getString(com.actionbarsherlock.sample.demos.R.string.scale);
		mRadioButton3 = (RadioButton) mActivity
				.findViewById(com.actionbarsherlock.sample.demos.R.id.radio3);
		mToastText0 = mActivity
				.getString(com.actionbarsherlock.sample.demos.R.string.started_rotate);
		mToastText1 = mActivity
				.getString(com.actionbarsherlock.sample.demos.R.string.started_vibrate);
		mToastText2 = mActivity
				.getString(com.actionbarsherlock.sample.demos.R.string.started_blink);
		mToastText3 = mActivity
				.getString(com.actionbarsherlock.sample.demos.R.string.started_scale);

	}

	// checking that the widgets are not null
	@SmallTest
	public void testViews() {
		assertNotNull(mActivity);
		assertNotNull(mRadioGroup);
		assertNotNull(mRadioText0);
		assertNotNull(mRadioButton0);
		assertNotNull(mRadioText1);
		assertNotNull(mRadioButton1);
		assertNotNull(mRadioText2);
		assertNotNull(mRadioButton2);
		assertNotNull(mRadioText3);
		assertNotNull(mRadioButton3);
		assertNotNull(mTextView);
	}

	// checking that the widgets are on screen
	@SmallTest
	public void testVisibility() {

		ViewAsserts.assertOnScreen(mActivity.getWindow().getDecorView(),
				mTextView);

		ViewAsserts.assertOnScreen(mActivity.getWindow().getDecorView(),
				mRadioGroup);

		ViewAsserts.assertOnScreen(mActivity.getWindow().getDecorView(),
				mRadioButton0);

		ViewAsserts.assertOnScreen(mActivity.getWindow().getDecorView(),
				mRadioButton1);

		ViewAsserts.assertOnScreen(mActivity.getWindow().getDecorView(),
				mRadioButton2);

		ViewAsserts.assertOnScreen(mActivity.getWindow().getDecorView(),
				mRadioButton3);
	}

	// checking if the correct text is shown
	@SmallTest
	public void testText() {
		assertEquals(introText, (String) mTextView.getText());
		assertEquals(mRadioText0, (String) mRadioButton0.getText());
		assertEquals(mRadioText1, (String) mRadioButton1.getText());
		assertEquals(mRadioText2, (String) mRadioButton2.getText());
		assertEquals(mRadioText3, (String) mRadioButton3.getText());
	}

	// checking if app does not crash after some actions and the correct Toast
	// is shown
	@SmallTest
	public void testActions() {

		solo.clickOnView(mRadioButton0);
		solo.clickOnActionBarItem(0);
		solo.waitForText(mToastText0);
		solo.clickOnView(mRadioButton1);
		solo.clickOnActionBarItem(0);
		solo.waitForText(mToastText1);
		solo.clickOnView(mRadioButton2);
		solo.clickOnActionBarItem(0);
		solo.waitForText(mToastText2);
		solo.clickOnView(mRadioButton3);
		solo.clickOnActionBarItem(0);
		solo.waitForText(mToastText3);
	}

	// finish the tests
	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}

}
