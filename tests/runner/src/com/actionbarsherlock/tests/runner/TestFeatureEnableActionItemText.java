package com.actionbarsherlock.tests.runner;

import com.actionbarsherlock.tests.app.FeatureEnableActionItemText;
import android.test.suitebuilder.annotation.Smoke;

public class TestFeatureEnableActionItemText extends BaseTestCase<FeatureEnableActionItemText> {
	public TestFeatureEnableActionItemText() {
		super(FeatureEnableActionItemText.class);
	}
	
	@Smoke
	public void testFragmentReceivesOnMenuItemSelectedCallback() {
		if (IS_HONEYCOMB) {
			return;
		}
		
		assertTrue("Text-only action-item could not be found.", getSolo().searchText(FeatureEnableActionItemText.MENU_ITEM_TEXT));
	}
}
