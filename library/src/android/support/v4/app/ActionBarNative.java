/*
 * Copyright 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.v4.app;

import java.util.HashMap;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MenuBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.SpinnerAdapter;

final class ActionBarNative {
	//No instances
	private ActionBarNative() {}
	
	static Class<? extends ActionBar> getImplementation() {
		return ActionBarNative.Impl.class;
	}

	/**
	 * <p>Handler for Android's native {@link android.app.ActionBar}.</p>
	 */
	static final class Impl extends ActionBar implements android.app.ActionBar.TabListener {
		private final HashMap<OnMenuVisibilityListener, android.app.ActionBar.OnMenuVisibilityListener> mMenuListenerMap = new HashMap<OnMenuVisibilityListener, android.app.ActionBar.OnMenuVisibilityListener>();
		
		/**
		 * Get the native {@link ActionBar} instance.
		 * 
		 * @return The action bar.
		 */
		private android.app.ActionBar getActionBar() {
			return this.getActivity().getActionBar();
		}
		
		/**
		 * Converts our Tab wrapper to a native version containing the wrapper
		 * instance as its tag.
		 * 
		 * @param tab Tab wrapper instance.
		 * @return Native tab.
		 */
		private android.app.ActionBar.Tab convertTabToNative(ActionBar.Tab tab) {
			return this.getActionBar().newTab()
					.setCustomView(tab.getCustomView())
					.setIcon(tab.getIcon())
					.setTabListener(this)
					.setTag(tab)
					.setText(tab.getText());
		}
		
		// ---------------------------------------------------------------------
		// ACTION BAR SHERLOCK SUPPORT
		// ---------------------------------------------------------------------

		@Override
		protected void setContentView(int layoutResId) {
			throw new IllegalStateException("This should have been passed to super from the Activity.");
		}
		
		@Override
		protected void setContentView(View view) {
			throw new IllegalStateException("This should have been passed to super from the Activity.");
		}

		@Override
		protected void setContentView(View view, android.view.ViewGroup.LayoutParams params) {
			throw new IllegalStateException("This should have been passed to super from the Activity.");
		}
		
		@Override
		MenuInflater getMenuInflater() {
			throw new IllegalStateException("This should have been passed to super from the Activity.");
		}
		
		@Override
		Menu getMenuInflationTarget(Menu nativeMenu) {
			throw new IllegalStateException("This should never be utilized for the native ActionBar.");
		}
		
		@Override
		public void onMenuVisibilityChanged(boolean isVisible) {
			throw new IllegalStateException("This should never be utilized for the native ActionBar.");
		}

		@Override
		public void onMenuInflated(MenuBuilder menu) {
			throw new IllegalStateException("This should never be utilized for the native ActionBar.");
		}

		@Override
		boolean requestWindowFeature(int featureId) {
			throw new IllegalStateException("This should have been passed to super from the Activity.");
		}
		
		// ---------------------------------------------------------------------
		// ACTION BAR SUPPORT
		// ---------------------------------------------------------------------
		
		private static class TabImpl extends ActionBar.Tab {
			final ActionBarNative.Impl mActionBar;
			
			View mCustomView;
			Drawable mIcon;
			ActionBar.TabListener mListener;
			Object mTag;
			CharSequence mText;
			
			TabImpl(ActionBarNative.Impl actionBar) {
				this.mActionBar = actionBar;
			}

			@Override
			public View getCustomView() {
				return this.mCustomView;
			}

			@Override
			public Drawable getIcon() {
				return this.mIcon;
			}

			@Override
			public int getPosition() {
				final int tabCount = this.mActionBar.getTabCount();
				for (int i = 0; i < tabCount; i++) {
					if (this.mActionBar.getTabAt(i).equals(this)) {
						return i;
					}
				}
				return ActionBar.Tab.INVALID_POSITION;
			}
			
			@Override
			public ActionBar.TabListener getTabListener() {
				return this.mListener;
			}

			@Override
			public Object getTag() {
				return this.mTag;
			}

			@Override
			public CharSequence getText() {
				return this.mText;
			}

			@Override
			public void select() {
				this.mActionBar.selectTab(this);
			}

			@Override
			public ActionBar.Tab setCustomView(int layoutResId) {
				this.mCustomView = this.mActionBar.getActivity().getLayoutInflater().inflate(layoutResId, null);
				return this;
			}

			@Override
			public ActionBar.Tab setCustomView(View view) {
				this.mCustomView = view;
				return this;
			}

			@Override
			public ActionBar.Tab setIcon(Drawable icon) {
				this.mIcon = icon;
				return this;
			}

			@Override
			public ActionBar.Tab setIcon(int resId) {
				this.mIcon = this.mActionBar.getActivity().getResources().getDrawable(resId);
				return this;
			}

			@Override
			public ActionBar.Tab setTabListener(TabListener listener) {
				this.mListener = listener;
				return this;
			}

			@Override
			public ActionBar.Tab setTag(Object obj) {
				this.mTag = obj;
				return this;
			}

			@Override
			public ActionBar.Tab setText(int resId) {
				this.mText = this.mActionBar.getActivity().getResources().getString(resId);
				return this;
			}

			@Override
			public ActionBar.Tab setText(CharSequence text) {
				this.mText = text;
				return this;
			}
			
		}
		
		@Override
		public void addOnMenuVisibilityListener(final OnMenuVisibilityListener listener) {
			if ((listener != null) && !this.mMenuListenerMap.containsKey(listener)) {
				android.app.ActionBar.OnMenuVisibilityListener nativeListener = new android.app.ActionBar.OnMenuVisibilityListener() {
					@Override
					public void onMenuVisibilityChanged(boolean isVisible) {
						listener.onMenuVisibilityChanged(isVisible);
					}
				};
				this.mMenuListenerMap.put(listener, nativeListener);
				
				this.getActionBar().addOnMenuVisibilityListener(nativeListener);
			}
		}

		@Override
		public void addTab(ActionBar.Tab tab, boolean setSelected) {
			this.getActionBar().addTab(this.convertTabToNative(tab), setSelected);
		}

		@Override
		public void addTab(ActionBar.Tab tab, int position, boolean setSelected) {
			this.getActionBar().addTab(this.convertTabToNative(tab), position, setSelected);
		}

		@Override
		public View getCustomView() {
			return this.getActionBar().getCustomView();
		}

		@Override
		public int getDisplayOptions() {
			return this.getActionBar().getDisplayOptions();
		}

		@Override
		public int getHeight() {
			return this.getActionBar().getHeight();
		}

		@Override
		public int getNavigationItemCount() {
			return this.getActionBar().getNavigationItemCount();
		}

		@Override
		public int getNavigationMode() {
			return this.getActionBar().getNavigationMode();
		}

		@Override
		public int getSelectedNavigationIndex() {
			return this.getActionBar().getSelectedNavigationIndex();
		}

		@Override
		public Tab getSelectedTab() {
			return (ActionBar.Tab)this.getActionBar().getSelectedTab().getTag();
		}

		@Override
		public CharSequence getSubtitle() {
			return this.getActionBar().getSubtitle();
		}

		@Override
		public ActionBar.Tab getTabAt(int index) {
			return (Tab)this.getActionBar().getTabAt(index).getTag();
		}

		@Override
		public int getTabCount() {
			return this.getActionBar().getTabCount();
		}

		@Override
		public CharSequence getTitle() {
			return this.getActionBar().getTitle();
		}

		@Override
		public void hide() {
			this.getActionBar().hide();
		}

		@Override
		public boolean isShowing() {
			return this.getActionBar().isShowing();
		}
		
		@Override
		public ActionBar.Tab newTab() {
			return new TabImpl(this);
		}

		@Override
		public void removeAllTabs() {
			this.getActionBar().removeAllTabs();
		}

		@Override
		public void removeOnMenuVisibilityListener(OnMenuVisibilityListener listener) {
			if ((listener != null) && this.mMenuListenerMap.containsKey(listener)) {
				this.getActionBar().removeOnMenuVisibilityListener(
					this.mMenuListenerMap.remove(listener)
				);
			}
		}

		@Override
		public void removeTab(Tab tab) {
			final int tabCount = this.getActionBar().getTabCount();
			for (int i = 0; i < tabCount; i++) {
				if (this.getActionBar().getTabAt(i).getTag().equals(tab)) {
					this.getActionBar().removeTabAt(i);
					break;
				}
			}
		}

		@Override
		public void removeTabAt(int position) {
			this.getActionBar().removeTabAt(position);
		}

		@Override
		public void selectTab(ActionBar.Tab tab) {
			final int tabCount = this.getActionBar().getTabCount();
			for (int i = 0; i < tabCount; i++) {
				if (this.getActionBar().getTabAt(i).getTag().equals(tab)) {
					this.getActionBar().setSelectedNavigationItem(i);
					break;
				}
			}
		}

		@Override
		public void setBackgroundDrawable(Drawable d) {
			this.getActionBar().setBackgroundDrawable(d);
		}

		@Override
		public void setCustomView(int resId) {
			this.getActionBar().setCustomView(resId);
		}

		@Override
		public void setCustomView(View view) {
			this.getActionBar().setCustomView(view);
		}

		@Override
		public void setCustomView(View view, LayoutParams layoutParams) {
			android.app.ActionBar.LayoutParams nativeLayoutParams = new android.app.ActionBar.LayoutParams(layoutParams);
			nativeLayoutParams.gravity = layoutParams.gravity;
			this.getActionBar().setCustomView(view, nativeLayoutParams);
		}

		@Override
		public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
			this.getActionBar().setDisplayHomeAsUpEnabled(showHomeAsUp);
		}

		@Override
		public void setDisplayOptions(int options, int mask) {
			this.getActionBar().setDisplayOptions(options, mask);
		}

		@Override
		public void setDisplayOptions(int options) {
			this.getActionBar().setDisplayOptions(options);
		}

		@Override
		public void setDisplayShowCustomEnabled(boolean showCustom) {
			this.getActionBar().setDisplayShowCustomEnabled(showCustom);
		}

		@Override
		public void setDisplayShowHomeEnabled(boolean showHome) {
			this.getActionBar().setDisplayShowHomeEnabled(showHome);
		}

		@Override
		public void setDisplayShowTitleEnabled(boolean showTitle) {
			this.getActionBar().setDisplayShowTitleEnabled(showTitle);
		}

		@Override
		public void setDisplayUseLogoEnabled(boolean useLogo) {
			this.getActionBar().setDisplayUseLogoEnabled(useLogo);
		}

		@Override
		public void setListNavigationCallbacks(SpinnerAdapter adapter, final OnNavigationListener callback) {
			this.getActionBar().setListNavigationCallbacks(adapter, new android.app.ActionBar.OnNavigationListener() {
				@Override
				public boolean onNavigationItemSelected(int itemPosition, long itemId) {
					if (callback != null) {
						return callback.onNavigationItemSelected(itemPosition, itemId);
					}
					return false;
				}
			});
		}

		@Override
		public void setNavigationMode(int mode) {
			this.getActionBar().setNavigationMode(mode);
		}

		@Override
		public void setSelectedNavigationItem(int position) {
			this.getActionBar().setSelectedNavigationItem(position);
		}

		@Override
		public void setSubtitle(int resId) {
			this.getActionBar().setSubtitle(resId);
		}

		@Override
		public void setSubtitle(CharSequence subtitle) {
			this.getActionBar().setSubtitle(subtitle);
		}

		@Override
		public void setTitle(CharSequence title) {
			this.getActionBar().setTitle(title);
		}

		@Override
		public void setTitle(int resId) {
			this.getActionBar().setTitle(resId);
		}

		@Override
		public void show() {
			this.getActionBar().show();
		}

		@Override
		public void onTabReselected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {
			ActionBar.TabListener listener = ((ActionBar.Tab)tab.getTag()).getTabListener();
			if (listener != null) {
				listener.onTabReselected((ActionBar.Tab)tab.getTag(), null);
			}
		}

		@Override
		public void onTabSelected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {
			ActionBar.TabListener listener = ((ActionBar.Tab)tab.getTag()).getTabListener();
			if (listener != null) {
				listener.onTabSelected((ActionBar.Tab)tab.getTag(), null);
			}
		}

		@Override
		public void onTabUnselected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {
			ActionBar.TabListener listener = ((ActionBar.Tab)tab.getTag()).getTabListener();
			if (listener != null) {
				listener.onTabUnselected((ActionBar.Tab)tab.getTag(), null);
			}
		}
	}
}
