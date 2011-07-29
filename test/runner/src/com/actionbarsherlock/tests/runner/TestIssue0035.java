package com.actionbarsherlock.tests.runner;

import android.test.suitebuilder.annotation.Smoke;
import com.actionbarsherlock.tests.app.Issue0035;

public class TestIssue0035 extends BaseTestCase<Issue0035> {
	public TestIssue0035() {
		super(Issue0035.class);
	}
	
	@Smoke
	public void testMenuCreatingOnActivityCreation() throws InterruptedException {
		assertFalse(getActivity().getWasMenuCreatedOnActivityCreation());
	}
}
