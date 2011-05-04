/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.styledactionbar;

import com.jakewharton.android.actionbarsherlock.ActionBarSherlock;
import com.jakewharton.android.actionbarsherlock.ActionBarTab;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.ActionBarHandler;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.FragmentActivity;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasHomeAsUp;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasListNavigation;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasLogo;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasNavigationState;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasTabNavigation;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasTitle;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.OnNavigationListener;
import com.jakewharton.android.actionbarsherlock.handler.Android_ActionBar;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ArrayAdapter;

public class MainActivity extends FragmentActivity implements ActionBarSherlock.TabListener {
	/** Proxy to the action bar. */
	private ActionBarHandler<?> mHandler;
	private RoundedColourFragment mFragmentSide;
	private RoundedColourFragment mFragmentMain;
	private boolean mUseLogo = false;
	private boolean mShowHomeUp = false;
	
	private final Handler mRefreshHandler = new Handler();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//"Elementary"
		this.mHandler = ActionBarSherlock
				.from(this)
				.layout(R.layout.main)
				.menu(R.menu.main_menu)
				.handleCustom(Android_ActionBar.Handler.class)
				.attach();
		
		if (this.mHandler instanceof HasHomeAsUp) {
			((HasHomeAsUp)this.mHandler).useHomeAsUp(this.mShowHomeUp);
		}
		if (this.mHandler instanceof HasLogo) {
			((HasLogo)this.mHandler).useLogo(this.mUseLogo);
		}
		if (this.mHandler instanceof HasTabNavigation) {
			HasTabNavigation tabHandler = (HasTabNavigation)this.mHandler;
			
			for (int i = 1; i < 4; i++) {
				tabHandler.addTab(new ActionBarTab().setText("Tab " + i));
			}
		}
		if (this.mHandler instanceof HasListNavigation) {
			((HasListNavigation)this.mHandler).setList(
				ArrayAdapter.createFromResource(this, R.array.sections, android.R.layout.simple_spinner_dropdown_item),
				new OnNavigationListener() {
					@Override
					public boolean onNavigationItemSelected(int itemPosition, long itemId) {
						// FIXME add proper implementation
						rotateLeftFrag();
						return false;
					}
				}
			);
		}
		
		// default to tab navigation
		this.showTabsNav();

		// create a couple of simple fragments as placeholders
		final int MARGIN = 16;
		this.mFragmentSide = new RoundedColourFragment(this.getResources().getColor(R.color.android_green), 1f, MARGIN, MARGIN / 2, MARGIN, MARGIN);
		this.mFragmentMain = new RoundedColourFragment(this.getResources().getColor(R.color.honeycombish_blue), 2f, MARGIN / 2, MARGIN, MARGIN, MARGIN);

		this.getSupportFragmentManager().beginTransaction()
				.add(R.id.root, this.mFragmentSide)
				.add(R.id.root, this.mFragmentMain)
				.commit();
	}

	@Override
	public void onOptionsMenuCreated(Menu menu) {
		// set up a listener for the refresh item
		final MenuItem refresh = (MenuItem)menu.findItem(R.id.menu_refresh);
		refresh.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			// on selecting show progress spinner for 1s
			public boolean onMenuItemClick(MenuItem item) {
				// item.setActionView(R.layout.progress_action);
				mRefreshHandler.postDelayed(new Runnable() {
					public void run() {
						refresh.setActionView(null);
					}
				}, 1000);
				return false;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				// TODO handle clicking the app icon/logo
				return false;
				
			case R.id.menu_refresh:
				// switch to a progress animation
				item.setActionView(R.layout.indeterminate_progress_action);
				return true;
				
			case R.id.menu_both:
				// rotation animation of green fragment
				this.rotateLeftFrag();
				return true;
				
			case R.id.menu_text:
				// alpha animation of blue fragment
				//XXX: New-style animations are not supported!
				//ObjectAnimator alpha = ObjectAnimator.ofFloat(this.mFragmentMain.getView(), "alpha", 1f, 0f);
				//alpha.setRepeatMode(ObjectAnimator.REVERSE);
				//alpha.setRepeatCount(1);
				//alpha.setDuration(800);
				//alpha.start();
				return true;
				
			case R.id.menu_logo:
				this.mUseLogo = !this.mUseLogo;
				item.setChecked(this.mUseLogo);
				this.getActionBar().setDisplayUseLogoEnabled(this.mUseLogo);
				return true;
				
			case R.id.menu_up:
				this.mShowHomeUp = !this.mShowHomeUp;
				item.setChecked(this.mShowHomeUp);
				this.getActionBar().setDisplayHomeAsUpEnabled(this.mShowHomeUp);
				return true;
				
			case R.id.menu_nav_tabs:
				item.setChecked(true);
				this.showTabsNav();
				return true;
				
			case R.id.menu_nav_label:
				item.setChecked(true);
				this.showStandardNav();
				return true;
				
			case R.id.menu_nav_drop_down:
				item.setChecked(true);
				this.showDropDownNav();
				return true;
				
			case R.id.menu_bak_none:
				item.setChecked(true);
				this.getActionBar().setBackgroundDrawable(null);
				return true;
				
			case R.id.menu_bak_gradient:
				item.setChecked(true);
				this.getActionBar().setBackgroundDrawable(
					this.getResources().getDrawable(R.drawable.ad_action_bar_gradient_bak)
				);
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void rotateLeftFrag() {
		if (this.mFragmentSide != null) {
			//XXX: New-style animations are not supported!
			//ObjectAnimator.ofFloat(this.mFragmentSide.getView(), "rotationY", 0, 180).setDuration(500).start();
		}
	}

	private void showStandardNav() {
		if (this.mHandler instanceof HasNavigationState) {
			HasNavigationState stateHandler = (HasNavigationState)this.mHandler;
			if (stateHandler.getNavigationMode() != HasNavigationState.MODE_STANDARD) {
				if (this.mHandler instanceof HasTitle) {
					((HasTitle)this.mHandler).showTitle(true);
				}
				stateHandler.setNavigationMode(HasNavigationState.MODE_STANDARD);
			}
		}
	}

	private void showDropDownNav() {
		if (this.mHandler instanceof HasNavigationState) {
			HasNavigationState stateHandler = (HasNavigationState)this.mHandler;
			if (stateHandler.getNavigationMode() != HasNavigationState.MODE_LIST) {
				if (this.mHandler instanceof HasTitle) {
					((HasTitle)this.mHandler).showTitle(false);
				}
				stateHandler.setNavigationMode(HasNavigationState.MODE_LIST);
			}
		}
	}

	private void showTabsNav() {
		if (this.mHandler instanceof HasNavigationState) {
			HasNavigationState stateHandler = (HasNavigationState)this.mHandler;
			if (stateHandler.getNavigationMode() != HasNavigationState.MODE_TABS) {
				if (this.mHandler instanceof HasTitle) {
					((HasTitle)this.mHandler).showTitle(false);
				}
				stateHandler.setNavigationMode(HasNavigationState.MODE_TABS);
			}
		}
	}

	@Override
	public void onTabReselected(ActionBarTab tab) {
		// FIXME add a proper implementation, for now just rotate the left fragment
		this.rotateLeftFrag();
	}

	@Override
	public void onTabSelected(ActionBarTab tab) {
		// FIXME implement this
	}

	@Override
	public void onTabUnselected(ActionBarTab tab) {
		// FIXME implement this
	}
}