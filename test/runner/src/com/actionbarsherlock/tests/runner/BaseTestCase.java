package com.actionbarsherlock.tests.runner;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import android.app.Activity;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public abstract class BaseTestCase<T extends Activity> extends ActivityInstrumentationTestCase2<T> {
	protected static final boolean IS_HONEYCOMB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	
	public BaseTestCase(Class<T> clazz) {
		super(clazz);
	}
	
	protected View findViewByClassName(View root, String className) {
		LinkedList<View> lookupQueue = new LinkedList<View>();
		lookupQueue.add(root);
		
		while (!lookupQueue.isEmpty()) {
			View target = lookupQueue.removeFirst();
			if (className.equals(target.getClass().getName())) {
				return target;
			}
			if (target instanceof ViewGroup) {
				ViewGroup targetGroup = (ViewGroup)target;
				for (int i = 0, count = targetGroup.getChildCount(); i < count; i++) {
					lookupQueue.addLast(targetGroup.getChildAt(i));
				}
			}
		}
		
		return null;
	}
	
	protected List<View> findViewsByClassName(View root, String className) {
		LinkedList<View> results = new LinkedList<View>();
		LinkedList<View> lookupQueue = new LinkedList<View>();
		lookupQueue.add(root);
		
		while (!lookupQueue.isEmpty()) {
			View target = lookupQueue.removeFirst();
			if (className.equals(target.getClass().getName())) {
				results.add(target);
			} else if (target instanceof ViewGroup) {
				ViewGroup targetGroup = (ViewGroup)target;
				for (int i = 0, count = targetGroup.getChildCount(); i < count; i++) {
					lookupQueue.addLast(targetGroup.getChildAt(i));
				}
			}
		}
		
		return results;
	}
	
	protected View findActionItem(final String text) {
		List<View> items = null;
		if (IS_HONEYCOMB) {
			items = findViewsByClassName(getActivity().getWindow().getDecorView(), "com.android.internal.view.menu.ActionMenuItemView");
			for (View item : items) {
				Button textView = (Button)findViewByClassName(item, "android.widget.Button");
				if (textView != null && text.equals(textView.getText())) {
					return textView;
				}
			}
		} else {
			items = findViewsByClassName(getActivity().getWindow().getDecorView(), "com.actionbarsherlock.internal.view.menu.ActionMenuItemView");
			for (View item : items) {
				TextView textView = (TextView)findViewByClassName(item, "android.widget.Button");
				if (textView != null && text.equals(textView.getText())) {
					return item;
				}
			}
		}
		return null;
	}
	
	protected void clickActionItem(final String text) {
		final View result = findActionItem(text);
		if (result != null) {
			final CountDownLatch latch = new CountDownLatch(1);
			
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					result.performClick();
					latch.countDown();
				}
			});
			
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void tearDown() throws Exception {
		getActivity().finish();
		super.tearDown();
	}
}
