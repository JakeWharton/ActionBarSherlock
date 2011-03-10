package com.jakewharton.android.actionbarsherlock.sample.android_actionbar;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.ActionBarHandler;
import com.markupartist.android.widget.ActionBar;

public class ActionBarForAndroidActionBar {
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
		
		private void initialize() {
			this.getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
			this.getActivity().setContentView(R.layout.actionbarforandroid);
		}
		
		private FrameLayout findContent() {
			return (FrameLayout)this.getActivity().findViewById(R.id.actionbar_content_view);
		}
		
		private ActionBar findActionBar() {
			return (ActionBar)this.getActivity().findViewById(R.id.actionbar);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			//Grumble, grumble...
			//OVERRIDE ME!
		}

		@Override
		public void setTitle(CharSequence title) {
			this.getActionBar().setTitle(title);
		}
	}
}
