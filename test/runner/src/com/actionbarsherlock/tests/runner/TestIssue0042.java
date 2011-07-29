package com.actionbarsherlock.tests.runner;

import android.test.suitebuilder.annotation.Smoke;
import com.actionbarsherlock.tests.app.Issue0042;

public class TestIssue0042 extends BaseTestCase<Issue0042> {
	public TestIssue0042() {
		super(Issue0042.class);
	}
	
	@Smoke
	public void testActivitySetTitleWithString() throws InterruptedException {
		getActivity().setTitleString();
		CharSequence actual = getActivity().getSupportActionBarTitle();
		assertNotNull(actual);
		assertEquals(Issue0042.TITLE, actual);
	}
	
	@Smoke
	public void testActivitySetTitleWithResource() throws InterruptedException {
		String expected = getActivity().setTitleResource();
		CharSequence actual = getActivity().getSupportActionBarTitle();
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
}
