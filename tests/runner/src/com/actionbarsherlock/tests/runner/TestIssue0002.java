package com.actionbarsherlock.tests.runner;

import com.actionbarsherlock.tests.app.Issue0002;
import android.test.suitebuilder.annotation.Smoke;

public class TestIssue0002 extends BaseTestCase<Issue0002> {
	public TestIssue0002() {
		super(Issue0002.class);
	}
	
	@Smoke
	public void testFragmentReceivesOnMenuItemSelectedCallback() {
		assertEquals(Issue0002.NO, getSolo().getEditText(0).getText().toString());
		
		if (IS_HONEYCOMB) {
			getSolo().clickOnText(Issue0002.MENU_ITEM_TEXT);
		} else {
			getSolo().clickOnImage(1); //home is 0
		}
		
		assertEquals(Issue0002.YES, getSolo().getEditText(0).getText().toString());
	}
}
