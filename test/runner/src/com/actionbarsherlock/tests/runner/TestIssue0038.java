package com.actionbarsherlock.tests.runner;

import android.test.suitebuilder.annotation.Smoke;
import com.actionbarsherlock.tests.app.Issue0038;

public class TestIssue0038 extends BaseTestCase<Issue0038> {
	public TestIssue0038() {
		super(Issue0038.class);
	}
	
	@Smoke
	public void testChildActivityHasNoActionBar() throws InterruptedException {
		String className;
		if (IS_HONEYCOMB) {
			className = "com.android.internal.view.ActionBarView";
		} else {
			className = "com.actionbarsherlock.internal.view.ActionBarView";
		}
		assertNull(findViewByClassName(getActivity().getWindow().getDecorView(), className));
	}
}
