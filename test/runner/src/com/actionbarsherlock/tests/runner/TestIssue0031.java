package com.actionbarsherlock.tests.runner;

import android.support.v4.view.MenuItem;
import android.test.suitebuilder.annotation.Smoke;
import com.actionbarsherlock.tests.app.Issue0031;

public class TestIssue0031 extends BaseTestCase<Issue0031> {
	public TestIssue0031() {
		super(Issue0031.class);
	}
	
	@Smoke
	public void testMenuItemResourceTitle() {
		String expected = getActivity().getResourceTitle();
		MenuItem resourceMenuItem = getActivity().getResourceMenuItem();
		assertNotNull(resourceMenuItem);
		CharSequence actual = resourceMenuItem.getTitle();
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	@Smoke
	public void testMenuItemStringTitle() {
		String expected = getActivity().getStringTitle();
		MenuItem stringMenuItem = getActivity().getStringMenuItem();
		assertNotNull(stringMenuItem);
		CharSequence actual = stringMenuItem.getTitle();
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
}
