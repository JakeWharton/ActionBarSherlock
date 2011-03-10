package com.jakewharton.android.actionbarsherlock.sample.android_actionbar;

import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.ActionBarHandler;
import com.markupartist.android.widget.ActionBar;

/**
 * Container class. See {@link Handler}.
 */
public final class ActionBarForAndroidActionBar {
	//No instances
	private ActionBarForAndroidActionBar() {}
	
	/**
	 * Implementation for using Android-ActionBar as a third-party fallback
	 * action bar by
	 * {@link com.jakewharton.android.actionbarsherlock.ActionBarSherlock}.
	 * 
	 * @author Jake Wharton <jakewharton@gmail.com>
	 */
	public static class Handler extends ActionBarHandler<ActionBar> {
		@Override
		public ActionBar initialize(int layoutResourceId) {
			this.initialize();
			this.getActivity().getLayoutInflater().inflate(layoutResourceId, this.findContent());
			
			return this.findActionBar();
		}

		@Override
		public ActionBar initialize(View view) {
			this.initialize();
			this.findContent().addView(view);
			
			return this.findActionBar();
		}
		
		/**
		 * Remove Android's window title and set the activity layout to a
		 * simple layout which includes the action bar and a frame layout for
		 * the actual activity content.
		 */
		private void initialize() {
			this.getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
			this.getActivity().setContentView(R.layout.actionbarforandroid);
		}
		
		/**
		 * Find the {@link android.widget.FrameLayout} in which we will place
		 * the actual activity's content.
		 * 
		 * @return FrameLayout instance.
		 */
		private FrameLayout findContent() {
			return (FrameLayout)this.getActivity().findViewById(R.id.actionbar_content_view);
		}
		
		/**
		 * Find the {@link com.markupartist.android.widget.ActionBar}.
		 * 
		 * @return ActionBar instance.
		 */
		private ActionBar findActionBar() {
			return (ActionBar)this.getActivity().findViewById(R.id.actionbar);
		}

		@Override
		public void setTitle(CharSequence title) {
			this.getActionBar().setTitle(title);
		}
	}
}
