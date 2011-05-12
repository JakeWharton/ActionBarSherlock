package com.jakewharton.android.actionbarsherlock.handler;

import java.util.List;
import greendroid.widget.GDActionBar;
import greendroid.widget.GDActionBarItem;
import greendroid.widget.NormalActionBarItem;
import greendroid.widget.GDActionBar.OnActionBarListener;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
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

/**
 * Container class. See {@link Handler}.
 */
public final class GreenDroid {
	//No instances
	private GreenDroid() {}
	

	/**
	 * Implementation for using GreenDroid as a third-party fallback
	 * action bar by
	 * {@link com.jakewharton.android.actionbarsherlock.ActionBarSherlock}.
	 * 
	 * @author Jake Wharton <jakewharton@gmail.com>
	 */
	public static class Handler extends ActionBarHandler<GDActionBar> implements HasTitle, HasVisibility, HasMenu, HasHome, HasBackgroundDrawable {
		/** Maximum number of action bar items to display. */
		private static final int MAX_ACTION_BAR_ITEMS = 3;
		
		/**
		 * {@link MenuItem} corresponding to the home action.
		 */
		private final ActionBarMenuItem mHome;
		
		
		/**
		 * Initialize this handler.
		 */
		public Handler() {
			this.mHome = new ActionBarMenuItem(this.getActivity(), android.R.id.home, 0, 0, null);
		}
		
		
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

		@Override
		public GDActionBar initialize(Fragment fragment, FragmentManager manager) {
			this.initialize();
			
			manager.beginTransaction()
			       .replace(R.id.gd_action_bar_content_view, fragment)
			       .commit();
			
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
						clicked(mHome);
					} else {
						GDActionBarItem item = getActionBar().getItem(position);
						if (item instanceof Item) {
							clicked(((Item)item).getMenuItem());
						} else {
							throw new RuntimeException("Non-handler created action bar item clicked (" + item.getClass().getName() + "[id=" + item.getItemId() + "])");
						}
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
		public CharSequence getTitle() {
			throw new RuntimeException("Not implemented.");
		}
		
		@Override
		public void setTitle(CharSequence title) {
			this.getActionBar().setTitle(title);
		}

		@Override
		public void setTitle(int resourceId) {
			this.getActionBar().setTitle(this.getActivity().getResources().getString(resourceId));
		}

		@Override
		public void setShowTitle(boolean value) {
			throw new RuntimeException("Not implemented.");
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
		public void setMenuResourceId(int menuResourceId) {
			//Action bar items only, add the rest to context menu automatically.
			List<ActionBarMenuItem> items = this.parseMenu(menuResourceId, MAX_ACTION_BAR_ITEMS);
			for (ActionBarMenuItem item : items) {
				this.getActionBar().addItem(new Item(item));
			}
		}

		@Override
		public void addMenuVisiblityListener(OnMenuVisibilityListener listener) {
			throw new RuntimeException("Not implemented");
		}

		@Override
		public void removeMenuVisiblityListener(OnMenuVisibilityListener listener) {
			throw new RuntimeException("Not implemented");
		}

		@Override
		public void setHomeAsUp(boolean homeAsUp) {
			throw new RuntimeException("Not implemented.");
		}

		@Override
		public void setShowHome(boolean showHome) {
			//Sort of a hack. We know this will always be the home button and
			//its separator since we use the gd_content_normal layout above.
			this.getActionBar().getChildAt(0).setVisibility(showHome ? View.VISIBLE : View.GONE);
			this.getActionBar().getChildAt(1).setVisibility(showHome ? View.VISIBLE : View.GONE);
		}

		@Override
		public void setBackgroundDrawable(Drawable drawable) {
			this.getActionBar().setBackgroundDrawable(drawable);
		}
		
		
		/**
		 * Custom action bar item used when inflating a menu from XML.
		 */
		private static final class Item extends NormalActionBarItem {
			private final MenuItem mItem;
			
			public Item(ActionBarMenuItem item) {
				this.mItem = item;
				this.setDrawable(item.getIconId());
			}
			
			@Override
			public int getItemId() {
				return this.mItem.getItemId();
			}
			
			public MenuItem getMenuItem() {
				return this.mItem;
			}
		}
	}
}
