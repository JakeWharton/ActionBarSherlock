package com.actionbarsherlock.tests.runner;

import android.test.suitebuilder.annotation.Smoke;
import com.actionbarsherlock.tests.app.Issue0030;

public class TestIssue0030 extends BaseTestCase<Issue0030> {
	public TestIssue0030() {
		super(Issue0030.class);
	}
	
	@Smoke
	public void testMenuFindItemDoesNotError() throws InterruptedException {
		assertNull(getActivity().performFindItem());
	}
	
	@Smoke
	public void testMenuRemoveItemDoesNotError() throws InterruptedException {
		getActivity().performRemoveItem();
		assertTrue(true);
	}
}
