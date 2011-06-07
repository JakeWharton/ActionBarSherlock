package com.actionbarsherlock.tests.runner;

import com.actionbarsherlock.tests.app.Issue0002;
import com.jayway.android.robotium.solo.Solo;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;

public class TestIssue0002 extends ActivityInstrumentationTestCase2<Issue0002> {
	private static final boolean IS_HONEYCOMB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	
	private Solo mSolo;
	
	public TestIssue0002() {
		super(Issue0002.class);
	}

	@Override
	protected void setUp() throws Exception {
		mSolo = new Solo(getInstrumentation(), getActivity());
	}
	
	@Smoke
	public void testWait() {
		assertEquals(Issue0002.NO, mSolo.getEditText(0).getText().toString());
		
		if (IS_HONEYCOMB) {
			mSolo.clickOnText(Issue0002.MENU_ITEM_TEXT);
		} else {
			mSolo.clickOnImage(1); //home is 0
		}
		
		assertEquals(Issue0002.YES, mSolo.getEditText(0).getText().toString());
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			mSolo.finalize();
		} catch (Throwable e) {  
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}
}
