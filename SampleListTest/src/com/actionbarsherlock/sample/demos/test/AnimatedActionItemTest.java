package com.actionbarsherlock.sample.demos.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.actionbarsherlock.sample.demos.AnimatedActionItem;
import com.jayway.android.robotium.solo.Solo;

public class AnimatedActionItemTest extends
		ActivityInstrumentationTestCase2<AnimatedActionItem> {
	private Solo solo;
	private Activity mActivity;
	private RadioGroup mRadioGroup;
	private TextView mTextView;
	private String introText, mRadioText0, mRadioText1, mRadioText2,
			mRadioText3;
	private RadioButton mRadioButton0, mRadioButton1, mRadioButton2,
			mRadioButton3;

	public AnimatedActionItemTest() {
		super("com.actionbarsherlock.sample.demos", AnimatedActionItem.class);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		// check that we have the right activity

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

	}

	@SmallTest
	public void testViews() {
		assertNotNull(mActivity);
		assertNotNull(mRadioGroup);
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

	@SmallTest
	public void testText() {
		assertEquals(introText, (String) mTextView.getText());
		assertEquals(mRadioText0, (String) mRadioButton0.getText());
		assertEquals(mRadioText1, (String) mRadioButton1.getText());
		assertEquals(mRadioText2, (String) mRadioButton2.getText());
		assertEquals(mRadioText3, (String) mRadioButton3.getText());
	}
	
	  @Override
	  public void tearDown() throws Exception {
	    solo.finishOpenedActivities();
	  }

}
