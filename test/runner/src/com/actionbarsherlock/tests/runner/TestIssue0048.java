package com.actionbarsherlock.tests.runner;

import android.test.suitebuilder.annotation.Smoke;
import com.actionbarsherlock.tests.app.Issue0048;
import com.actionbarsherlock.view.Menu;

public class TestIssue0048 extends BaseTestCase<Issue0048> {
	public TestIssue0048() {
		super(Issue0048.class);
	}
	
	@Smoke
	public void testViewPagerOnlyCurrentMenu() throws InterruptedException {
		Menu menu = getActivity().getMenu();
		assertNotNull(menu);
		assertEquals(1, menu.size());
	}
}
