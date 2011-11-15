package com.actionbarsherlock.tests.runner;

import android.view.View;
import com.actionbarsherlock.tests.app.Issue0033;
import com.actionbarsherlock.tests.app.R;
import com.actionbarsherlock.view.MenuItem;

public class TestIssue0033 extends BaseTestCase<Issue0033> {
    public TestIssue0033() {
        super(Issue0033.class);
    }

    public void testVisibleMenuItemHiding() throws InterruptedException {
        //Get native item
        MenuItem nativeItem = getActivity().menuItemNativeVisible;
        assertNotNull(nativeItem);
        assertTrue(nativeItem.isVisible());
        assertTrue(getInstrumentation().invokeMenuActionSync(getActivity(), Issue0033.ID_VISIBLE, 0));

        //Get action item
        MenuItem actionItem = getActivity().menuItemActionVisible;
        assertNotNull(actionItem);
        assertTrue(actionItem.isVisible());

        //Get action item view
        View actionItemView = findActionItem(Issue0033.TEXT_VISIBLE);
        assertNotNull(actionItemView);
        assertEquals(View.VISIBLE, actionItemView.getVisibility());

        //Hide and test hidden
        getActivity().hideVisibleMenuItems();
        assertFalse(nativeItem.isVisible());
        assertFalse(getInstrumentation().invokeMenuActionSync(getActivity(), Issue0033.ID_VISIBLE, 0));
        assertFalse(actionItem.isVisible());
        assertEquals(View.GONE, actionItemView.getVisibility());
    }

    public void testHiddenMenuItems() throws InterruptedException {
        //Get the menu items
        MenuItem actionXmlHidden = getActivity().menuItemActionXmlHidden;
        MenuItem actionCodeHidden = getActivity().menuItemActionCodeHidden;
        MenuItem nativeXmlHidden = getActivity().menuItemNativeXmlHidden;
        MenuItem nativeCodeHidden = getActivity().menuItemNativeCodeHidden;

        //Make sure they all were created properly
        assertNotNull(actionXmlHidden);
        assertNotNull(actionCodeHidden);
        assertNotNull(nativeXmlHidden);
        assertNotNull(nativeCodeHidden);

        //Make sure the items think they're hidden
        assertFalse(actionXmlHidden.isVisible());
        assertFalse(actionCodeHidden.isVisible());
        assertFalse(nativeXmlHidden.isVisible());
        assertFalse(nativeCodeHidden.isVisible());

        //Test native items are not present
        assertFalse(getInstrumentation().invokeMenuActionSync(getActivity(), Issue0033.ID_HIDDEN, 0));
        assertFalse(getInstrumentation().invokeMenuActionSync(getActivity(), R.id.issue0033_native, 0));

        //Test action items are not present
        View codeView = findActionItem(Issue0033.TEXT_HIDDEN);
        assertNotNull(codeView);
        assertEquals(View.GONE, codeView.getVisibility());
        View xmlView = findActionItem(getActivity().getXmlHiddenText());
        assertNotNull(xmlView);
        assertEquals(View.GONE, xmlView.getVisibility());
    }
}
