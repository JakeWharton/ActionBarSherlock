package com.jakewharton.android.actionbarsherlock;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public final class ActionBarSherlock {
	public static final boolean HAS_NATIVE_ACTION_BAR;
	
	static {
		boolean hasNativeActionBar = false;
		try {
			hasNativeActionBar = (android.app.ActionBar.class != null);
		} catch (NoClassDefFoundError e) {}
		
		HAS_NATIVE_ACTION_BAR = hasNativeActionBar;
	}
	
	private boolean mAttached;
	private final Activity mActivity;
	private Bundle mSavedInstanceState;
	private Integer mLayoutResource;
	private View mView;
	private CharSequence mTitle;
	private Class<? extends NativeActionBarHandler> mNativeHandler;
	private Class<? extends ActionBarHandler<?>> mCustomHandler;
	
	public static ActionBarSherlock from(Activity activity) {
		return new ActionBarSherlock(activity);
	}
	
	private ActionBarSherlock(Activity activity) {
		this.mAttached = false;
		this.mActivity = activity;
	}
	
	public ActionBarSherlock with(Bundle savedInstanceState) {
		assert this.mAttached == false;
		assert this.mSavedInstanceState == null;
		
		this.mSavedInstanceState = savedInstanceState;
		return this;
	}
	public ActionBarSherlock layout(int layoutResource) {
		assert this.mAttached == false;
		assert this.mLayoutResource == null;
		//assert this.mView == null;
		
		this.mLayoutResource = layoutResource;
		return this;
	}
	public ActionBarSherlock layout(View view) {
		assert this.mAttached == false;
		assert this.mLayoutResource == null;
		assert this.mView == null;
		
		this.mView = view;
		
		return this;
	}
	public ActionBarSherlock title(int stringResourceId) {
		assert this.mAttached == false;
		return this.title(this.mActivity.getResources().getString(stringResourceId));
	}
	public ActionBarSherlock title(CharSequence title) {
		assert this.mAttached == false;
		assert this.mTitle == null;
		
		this.mTitle = title;
		return this;
	}
	public ActionBarSherlock handleNative(Class<? extends NativeActionBarHandler> handler) {
		assert this.mAttached == false;
		assert this.mNativeHandler == null;
		
		this.mNativeHandler = handler;
		return this;
	}
	public ActionBarSherlock handleCustom(Class<? extends ActionBarHandler<?>> handler) {
		assert this.mAttached == false;
		assert this.mCustomHandler == null;
		
		this.mCustomHandler = handler;
		return this;
	}
	
	public void attach() {
		assert this.mAttached == false;
		assert (this.mLayoutResource != null)
			|| (this.mView != null);
		
		if (this.mNativeHandler == null) {
			this.mNativeHandler = NativeActionBarHandler.class;
		}
		
		ActionBarHandler<?> handler;
		try {
			if (HAS_NATIVE_ACTION_BAR) {
				handler = this.mNativeHandler.newInstance();
			} else if (this.mCustomHandler != null) {
				handler = this.mCustomHandler.newInstance();
			} else {
				//No custom handler so pass the view directly to the activity
				if (this.mLayoutResource != null) {
					this.mActivity.setContentView(this.mLayoutResource);
				} else {
					this.mActivity.setContentView(this.mView);
				}
				return;
			}
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		
		handler.setActivity(this.mActivity);
		
		if (this.mLayoutResource != null) {
			handler.setLayout(this.mLayoutResource);
		} else {
			handler.setLayout(this.mView);
		}
		
		if (this.mTitle != null) {
			handler.setTitle(this.mTitle);
		}
		
		handler.onCreate(this.mSavedInstanceState);
	}
	
	public static abstract class ActionBarHandler<T> {
		private Activity mActivity;
		private T mActionBar;

		public final Activity getActivity() {
			return this.mActivity;
		}
		private void setActivity(Activity activity) {
			this.mActivity = activity;
		}
		public T getActionBar() {
			return this.mActionBar;
		}
		private void setActionBar(T actionBar) {
			this.mActionBar = actionBar;
		}
		
		private void setLayout(int layoutResourceId) {
			this.setActionBar(this.initialize(layoutResourceId));
		}
		private void setLayout(View view) {
			this.setActionBar(this.initialize(view));
		}
		
		public abstract T initialize(int layoutResourceId);
		public abstract T initialize(View view);
		public abstract void setTitle(CharSequence title);
		public abstract void onCreate(Bundle savedInstanceState);
	}
	public static class NativeActionBarHandler extends ActionBarHandler<ActionBar> {
		@Override
		public ActionBar initialize(int layoutResourceId) {
			this.getActivity().setContentView(layoutResourceId);
			
			Log.v("XXX", "ActionBar: " + this.getActivity().getActionBar());
			
			return this.getActivity().getActionBar();
		}
		
		@Override
		public ActionBar initialize(View view) {
			this.getActivity().setContentView(view);
			return this.getActivity().getActionBar();
		}

		@Override
		public void setTitle(CharSequence title) {
			this.getActionBar().setTitle(title);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			//Grumble, grumble...
		}
	}
}
