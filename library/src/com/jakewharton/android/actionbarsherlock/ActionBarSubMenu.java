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

package com.jakewharton.android.actionbarsherlock;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/**
 * An implementation of the {@link android.view.SubMenu} interface for use in
 * inflating menu XML resources to be added to a third-party action bar. 
 * 
 * @author Jake Wharton <jakewharton@gmail.com>
 * @see <a href="http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob;f=core/java/com/android/internal/view/menu/SubMenuBuilder.java">com.android.internal.view.menu.SubMenuBuilder</a>
 */
public class ActionBarSubMenu extends ActionBarMenu implements SubMenu {
	/**
	 * Parent menu.
	 */
	private final ActionBarMenu mParent;
	
	/**
	 * Parent item which owns this sub-menu.
	 */
	private final ActionBarMenuItem mItem;
	

	/**
	 * Create a new action bar sub-menu.
	 * 
	 * @param context Context used if resource resolution is required.
	 */
	public ActionBarSubMenu(Context context, ActionBarMenu parent, ActionBarMenuItem item) {
		super(context);
		
		this.mParent = parent;
		this.mItem = item;
	}

	
	@Override
	public MenuItem getItem() {
		return this.mItem;
	}
	
	/**
	 * Get the parent menu.
	 * 
	 * @return Parent menu instance.
	 */
	public ActionBarMenu getParent() {
		return this.mParent;
	}

	@Override
	public SubMenu setIcon(int iconResourceId) {
		this.mItem.setIcon(iconResourceId);
		return this;
	}

	@Override
	public SubMenu setIcon(Drawable icon) {
		this.mItem.setIcon(icon);
		return this;
	}


	@Override
	public void clearHeader() {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public SubMenu setHeaderIcon(int arg0) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public SubMenu setHeaderIcon(Drawable arg0) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public SubMenu setHeaderTitle(int arg0) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public SubMenu setHeaderTitle(CharSequence arg0) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public SubMenu setHeaderView(View arg0) {
		throw new RuntimeException("Method not supported.");
	}
}
