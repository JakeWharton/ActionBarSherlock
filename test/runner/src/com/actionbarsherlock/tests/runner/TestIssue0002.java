package com.actionbarsherlock.tests.runner;

import android.test.suitebuilder.annotation.Smoke;
import com.actionbarsherlock.tests.app.Issue0002;

public class TestIssue0002 extends BaseTestCase<Issue0002> {
	public TestIssue0002() {
		super(Issue0002.class);
	}
	
	@Smoke
	public void testFragmentReceivesOnMenuItemSelectedCallback() throws InterruptedException {
		assertFalse(getActivity().triggered);
		clickActionItem(Issue0002.MENU_ITEM_TEXT);
		assertTrue(getActivity().triggered);
	}
}
