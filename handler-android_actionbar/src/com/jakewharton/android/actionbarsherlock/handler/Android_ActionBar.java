package com.jakewharton.android.actionbarsherlock.handler;

import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import com.jakewharton.android.actionbarsherlock.ActionBarMenuItem;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.ActionBarHandler;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasBackgroundDrawable;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasHome;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasMenu;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasTitle;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasVisibility;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.OnMenuVisibilityListener;
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
	public static class Handler extends ActionBarHandler<ActionBar> implements HasTitle, HasMenu, HasHome, HasVisibility, HasBackgroundDrawable {
		/** Maximum number of action bar items to display. */
		private static final int MAX_ACTION_BAR_ITEMS = 3;
		
		/** Holder for home action so we can show/hide it. */
		private Action mHomeAction;
		
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
			this.mHomeAction = new Action(this, home);
			this.findActionBar().setHomeAction(this.mHomeAction);
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
		public CharSequence getTitle() {
			throw new RuntimeException("Not implemented");
		}

		@Override
		public void setTitle(CharSequence title) {
			this.getActionBar().setTitle(title);
		}

		@Override
		public void setTitle(int resourceId) {
			this.getActionBar().setTitle(resourceId);
		}

		@Override
		public void setShowTitle(boolean value) {
			throw new RuntimeException("Not implemented");
		}

		@Override
		public void setMenuResourceId(int menuResourceId) {
			//Action bar items only, add the rest to context menu automatically.
			List<ActionBarMenuItem> items = this.parseMenu(menuResourceId, MAX_ACTION_BAR_ITEMS);
			for (ActionBarMenuItem item : items) {
				this.getActionBar().addAction(new Action(this, item));
			}
		}

		@Override
		public void setMenuVisiblityListener(OnMenuVisibilityListener listener) {
			throw new RuntimeException("Not implemented");
		}

		@Override
		public void setHomeAsUp(boolean homeAsUp) {
			this.getActionBar().setDisplayHomeAsUpEnabled(homeAsUp);
		}

		@Override
		public void setShowHome(boolean showHome) {
			if (showHome) {
				this.getActionBar().setHomeAction(this.mHomeAction);
			} else {
				this.getActionBar().clearHomeAction();
			}
		}

		@Override
		public void hide() {
			this.getActionBar().setVisibility(View.GONE);
		}

		@Override
		public boolean isShowing() {
			return this.getActionBar().getVisibility() == View.VISIBLE;
		}
		
		@Override
		public int getHeight() {
			return this.getActionBar().getHeight();
		}

		@Override
		public void show() {
			this.getActionBar().setVisibility(View.VISIBLE);
		}

		@Override
		public void setBackgroundDrawable(Drawable drawable) {
			this.getActionBar().setBackgroundDrawable(drawable);
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
