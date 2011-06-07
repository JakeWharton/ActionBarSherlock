package com.actionbarsherlock.tests.runner;

import com.jayway.android.robotium.solo.Solo;
import android.app.Activity;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;

public abstract class BaseTestCase<T extends Activity> extends ActivityInstrumentationTestCase2<T> {
	protected static final boolean IS_HONEYCOMB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	
	
	private Solo mSolo;
	
	
	public BaseTestCase(Class<T> clazz) {
		super(clazz);
	}
	
	
	protected final Solo getSolo() {
		return mSolo;
	}

	@Override
	protected void setUp() throws Exception {
		mSolo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			mSolo.finalize();
		} catch (Throwable e) {  
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}
}
