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
		
		assertNotNull("Text-only action-item could not be found.", findActionItem(FeatureEnableActionItemText.MENU_ITEM_TEXT));
	}
}
