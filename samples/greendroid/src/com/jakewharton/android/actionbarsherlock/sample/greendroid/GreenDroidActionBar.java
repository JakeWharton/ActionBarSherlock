package com.jakewharton.android.actionbarsherlock.sample.greendroid;

import greendroid.widget.GDActionBar;
import greendroid.widget.GDActionBar.OnActionBarListener;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.ActionBarHandler;

public class GreenDroidActionBar {
	public static final class Handler extends ActionBarHandler<GDActionBar> {
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
		
		private FrameLayout findContent() {
			return (FrameLayout)this.getActivity().findViewById(R.id.gd_action_bar_content_view);
		}
		
		private GDActionBar findActionBar() {
			return (GDActionBar)this.getActivity().findViewById(R.id.gd_action_bar);
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
		
		public void setIsHomeButtonVisible(boolean visible) {
			//Sort of a hack. We know this will always be the home button and
			//its separator since we use the gd_content_normal layout above.
			this.getActionBar().getChildAt(0).setVisibility(visible ? View.VISIBLE : View.GONE);
			this.getActionBar().getChildAt(1).setVisibility(visible ? View.VISIBLE : View.GONE);
		}
		
		public void onHomeClicked() {
			Toast.makeText(this.getActivity(), "Unhandled Event: Home clicked.", Toast.LENGTH_SHORT).show();
		}
		
		public void onItemClicked(int itemId) {
			Toast.makeText(this.getActivity(), "Unhandled Event: Item id " + itemId + " clicked.", Toast.LENGTH_SHORT).show();
		}
	}
}
