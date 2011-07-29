package com.actionbarsherlock.tests.runner;

import android.test.suitebuilder.annotation.Smoke;
import com.actionbarsherlock.tests.app.Issue0031;

public class TestIssue0031 extends BaseTestCase<Issue0031> {
	public TestIssue0031() {
		super(Issue0031.class);
	}
	
	@Smoke
	public void testMenuItemResourceTitle() {
		String expected = getActivity().getResourceTitle();
		CharSequence actual = getActivity().getResourceMenuItem().getTitle();
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	@Smoke
	public void testMenuItemStringTitle() {
		String expected = getActivity().getStringTitle();
		CharSequence actual = getActivity().getStringMenuItem().getTitle();
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
}
