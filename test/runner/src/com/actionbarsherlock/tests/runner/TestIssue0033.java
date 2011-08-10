package com.actionbarsherlock.tests.runner;

import android.view.View;
import com.actionbarsherlock.internal.view.menu.MenuItemImpl;
import com.actionbarsherlock.tests.app.Issue0033;

public class TestIssue0033 extends BaseTestCase<Issue0033> {
	public TestIssue0033() {
		super(Issue0033.class);
	}

	public void testMenuItemNativeVisiblity() throws InterruptedException {
		MenuItemImpl nativeItem = (MenuItemImpl)getActivity().getMenuItemNative();
        assertNotNull(nativeItem);
        assertTrue(nativeItem.isVisible());
        assertTrue(getInstrumentation().invokeMenuActionSync(getActivity(), Issue0033.ITEM_ID, 0));
        
        getActivity().hideMenuItemNative();
        assertFalse(nativeItem.isVisible());
        assertFalse(getInstrumentation().invokeMenuActionSync(getActivity(), Issue0033.ITEM_ID, 0));
	}

	public void testMenuItemActionVisiblity() throws InterruptedException {
		MenuItemImpl actionItem = (MenuItemImpl)getActivity().getMenuItemAction();
        assertNotNull(actionItem);
        assertTrue(actionItem.isVisible());

		View actionItemView = findActionItem(Issue0033.ITEM_TEXT);
		assertNotNull(actionItemView);
		assertEquals(View.VISIBLE, actionItemView.getVisibility());
		
		getActivity().hideMenuItemAction();
		assertFalse(actionItem.isVisible());
		assertEquals(View.GONE, actionItemView.getVisibility());
	}
}
