package com.jakewharton.android.actionbarsherlock.sample.android_actionbar;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import com.jakewharton.android.actionbarsherlock.ActionBarMenu;
import com.jakewharton.android.actionbarsherlock.ActionBarMenuItem;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.ActionBarHandler;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.ActionBarMenuHandler;
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
	public static class Handler extends ActionBarHandler<ActionBar> implements ActionBarMenuHandler {
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
			
			//Add home action
			ActionBarMenuItem home = new ActionBarMenuItem(this.getActivity(), android.R.id.home, 0, 0, null);
			home.setIcon(this.getHomeIcon());
			this.findActionBar().setHomeAction(new Action(this, home));
		}
		
		/**
		 * Return the drawable resource ID of the icon used for the home button.
		 * 
		 * @return Drawable resource ID.
		 */
		protected int getHomeIcon() {
			return R.drawable.icon;
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

		@Override
		public void inflateMenu(ActionBarMenu menu) {
			for (ActionBarMenuItem item : menu.getItems()) {
				this.getActionBar().addAction(new Action(this, item));
			}
		}
		
		/**
		 * Custom Action which marshals the event on to the activity.
		 */
		private static class Action extends ActionBar.AbstractAction {
			private final Handler mHandler;
			private final MenuItem mMenuItem;
			
			public Action(Handler handler, ActionBarMenuItem item) {
				super(item.getIconId());
				
				this.mHandler = handler;
				this.mMenuItem = item;
			}

			@Override
			public void performAction(View view) {
				this.mHandler.clicked(mMenuItem);
			}
		}
	}
}
