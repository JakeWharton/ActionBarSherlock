package com.jakewharton.android.actionbarsherlock.sample.greendroid;

import greendroid.widget.GDActionBar;
import greendroid.widget.GDActionBar.OnActionBarListener;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.ActionBarHandler;

/**
 * Container class. See {@link Handler}.
 */
public final class GreenDroidActionBar {
	//No instances
	private GreenDroidActionBar() {}
	

	/**
	 * Implementation for using GreenDroid as a third-party fallback
	 * action bar by
	 * {@link com.jakewharton.android.actionbarsherlock.ActionBarSherlock}.
	 * 
	 * @author Jake Wharton <jakewharton@gmail.com>
	 */
	public static class Handler extends ActionBarHandler<GDActionBar> {
		@Override
		public GDActionBar initialize(int layoutResourceId) {
			this.initialize();
			this.getActivity().getLayoutInflater().inflate(layoutResourceId, this.findContent());
			
			return this.findActionBar();
		}
	
		@Override
		public GDActionBar initialize(View view) {
			this.initialize();
			this.findContent().addView(view);
			
			return this.findActionBar();
		}

		/**
		 * <p>Remove Android's window title and set the activity layout to a
		 * simple layout which includes the action bar and a frame layout for
		 * the actual activity content.</p>
		 * 
		 * <p>This also applies the GreenDroid style to the activity to the
		 * action bar so it appears correctly.</p>
		 * 
		 * <p>We also setup the click handler to map to two local methods which
		 * should be overridden in an extending class local to an activity.</p>
		 */
		private void initialize() {
			this.getActivity().setTheme(R.style.Theme_GreenDroid);
			this.getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
			this.getActivity().setContentView(R.layout.gd_content_normal);

			this.findActionBar().setOnActionBarListener(new OnActionBarListener() {
				@Override
				public void onActionBarItemClicked(int position) {
					if (position == OnActionBarListener.HOME_ITEM) {
						onHomeClicked();
					} else {
						onItemClicked(getActionBar().getItem(position).getItemId());
					}
				}
			});
		}

		/**
		 * Find the {@link android.widget.FrameLayout} in which we will place
		 * the actual activity's content.
		 * 
		 * @return FrameLayout instance.
		 */
		private FrameLayout findContent() {
			return (FrameLayout)this.getActivity().findViewById(R.id.gd_action_bar_content_view);
		}

		/**
		 * Find the {@link greendroid.widget.GDActionBar}.
		 * 
		 * @return ActionBar instance.
		 */
		private GDActionBar findActionBar() {
			return (GDActionBar)this.getActivity().findViewById(R.id.gd_action_bar);
		}
	
		@Override
		public void setTitle(CharSequence title) {
			this.getActionBar().setTitle(title);
		}
		
		/**
		 * Convenience method to toggle the visibility of the home button.
		 * 
		 * @param visible Boolean indicating visibility.
		 */
		public void setIsHomeButtonVisible(boolean visible) {
			//Sort of a hack. We know this will always be the home button and
			//its separator since we use the gd_content_normal layout above.
			this.getActionBar().getChildAt(0).setVisibility(visible ? View.VISIBLE : View.GONE);
			this.getActionBar().getChildAt(1).setVisibility(visible ? View.VISIBLE : View.GONE);
		}
		
		/**
		 * Method executed when the home button is clicked. This should be
		 * overridden in each activity. 
		 */
		public void onHomeClicked() {
			//Grumble, grumble... OVERRIDE ME!
		}
		
		/**
		 * Method executed when an action button is clicked. This should be
		 * overridden in each activity.
		 * 
		 * @param itemId ID of the item clicked.
		 */
		public void onItemClicked(int itemId) {
			//Grumble, grumble... OVERRIDE ME!
		}
	}
}
