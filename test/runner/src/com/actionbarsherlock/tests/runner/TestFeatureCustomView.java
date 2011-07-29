package com.actionbarsherlock.tests.runner;

import android.test.suitebuilder.annotation.Smoke;
import com.actionbarsherlock.tests.app.FeatureCustomView;

public class TestFeatureCustomView extends BaseTestCase<FeatureCustomView> {
	public TestFeatureCustomView() {
		super(FeatureCustomView.class);
	}
	
	@Smoke
	public void testCustomViewDisplayedWhenEnabled() throws InterruptedException {
		getActivity().setCustomView();
		getActivity().enableCustomView();

		assertTrue("Custom view not displayed when enabled.", getActivity().customView.isShown());
	}
	
	@Smoke
	public void testCustomViewNotDisplayedWhenAssigned() throws InterruptedException {
		getActivity().setCustomView();
		
		assertTrue("Custom view displayed when assigned.", !getActivity().customView.isShown());
	}
}
