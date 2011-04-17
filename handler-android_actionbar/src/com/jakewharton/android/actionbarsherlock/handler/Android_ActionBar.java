package com.jakewharton.android.actionbarsherlock.handler;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import com.jakewharton.android.actionbarsherlock.ActionBarMenu;
import com.jakewharton.android.actionbarsherlock.ActionBarMenuItem;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.ActionBarHandler;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HomeAsUpHandler;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.MenuHandler;
import com.markupartist.android.widget.ActionBar;

/**
 * Container class. See {@link Handler}.
 */
public final class Android_ActionBar {
	//No instances
	private Android_ActionBar() {}
	
	/**
	 * Implementation for using Android-ActionBar as a third-party fallback
	 * action bar by
	 * {@link com.jakewharton.android.actionbarsherlock.ActionBarSherlock}.
	 * 
	 * @author Jake Wharton <jakewharton@gmail.com>
	 */
	public static class Handler extends ActionBarHandler<ActionBar> implements MenuHandler, HomeAsUpHandler {
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

		@Override
		public ActionBar initialize(Fragment fragment, FragmentManager manager) {
			this.initialize();
			
			manager.beginTransaction()
			       .replace(R.id.actionbar_content_view, fragment)
			       .commit();
			
			return this.findActionBar();
		}
		
		/**
		 * Remove Android's window title and set the activity layout to a
		 * simple layout which includes the action bar and a frame layout for
		 * the actual activity content.
		 */
		private void initialize() {
			this.getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
			this.getActivity().setContentView(R.layout.android_actionbar);
			
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
		public void useHomeAsUp() {
			this.getActionBar().setDisplayHomeAsUpEnabled(true);
		}

		@Override
		public void setMenuResourceId(int menuResourceId) {
			ActionBarMenu menu = this.inflateMenu(menuResourceId);
			for (ActionBarMenuItem item : menu.getItems()) {
				this.getActionBar().addAction(new Action(this, item));
			}
		}
		
		/**
		 * Custom Action which marshals the event on to the activity.
		 */
		private static class Action extends ActionBar.AbstractAction {
			private final Handler mHandler;
			private final ActionBarMenuItem mItem;
			
			public Action(Handler handler, ActionBarMenuItem item) {
				super(item.getIconId());
				
				this.mHandler = handler;
				this.mItem = item;
			}

			@Override
			public void performAction(View view) {
				if (this.mItem.hasSubMenu()) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(this.mHandler.getActivity());
					dialog.setTitle(this.mItem.getTitle());
					dialog.setItems(this.mItem.getSubMenu().getTitles(), new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int index) {
							mHandler.clicked(mItem.getSubMenu().getItem(index));
						}
					});
					dialog.show();
				} else {
					this.mHandler.clicked(mItem);
				}
			}
		}
	}
}
