package com.actionbarsherlock.tests.runner;

import android.test.suitebuilder.annotation.Smoke;
import com.actionbarsherlock.tests.app.Issue0036;

public class TestIssue0036 extends BaseTestCase<Issue0036> {
	public TestIssue0036() {
		super(Issue0036.class);
	}
	
	@Smoke
	public void testMenuFindItemDoesNotError() throws InterruptedException {
		Object menuItem = getActivity().getSubMenuItemParent();
		assertNotNull(menuItem);
		assertTrue(menuItem instanceof com.actionbarsherlock.view.MenuItem);
	}
}
