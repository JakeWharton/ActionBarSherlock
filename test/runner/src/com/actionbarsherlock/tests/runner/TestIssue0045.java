package com.actionbarsherlock.tests.runner;

import android.test.suitebuilder.annotation.Smoke;
import com.actionbarsherlock.tests.app.Issue0045;

public class TestIssue0045 extends BaseTestCase<Issue0045> {
	public TestIssue0045() {
		super(Issue0045.class);
	}
	
	@Smoke
	public void testMenuFindItemDoesNotError() throws InterruptedException {
		Object menuItem = getActivity().getMenuItemSubMenu();
		assertNotNull(menuItem);
		assertTrue(menuItem instanceof com.actionbarsherlock.view.SubMenu);
	}
}
