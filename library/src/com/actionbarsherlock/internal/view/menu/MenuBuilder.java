/*
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2011 Jake Wharton
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

package com.actionbarsherlock.internal.view.menu;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import com.actionbarsherlock.R;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MenuBuilder implements Menu {
	private static final boolean DEBUG = false;
	
	public static final int NUM_TYPES = 3;
	public static final int TYPE_NATIVE = 0;
	public static final int TYPE_ACTION_BAR = 1;
	//public static final int TYPE_DIALOG = 2;

	static final int[] THEME_RES_FOR_TYPE = new int [] {
		0,
		R.styleable.SherlockTheme_actionButtonStyle,
		//0,
	};
	static final int[] LAYOUT_RES_FOR_TYPE = new int[] {
		0,
		R.layout.action_menu_layout,
		//0,
	};
	static final int[] ITEM_LAYOUT_RES_FOR_TYPE = new int[] {
		0,
		R.layout.action_menu_item_layout,
		//android.R.layout.simple_list_item_1,
	};
	
	private static final int[] sCategoryToOrder = new int[] {
		1, /* No category */
		4, /* CONTAINER */
		5, /* SYSTEM */
		3, /* SECONDARY */
		2, /* ALTERNATIVE */
		0, /* SELECTED_ALTERNATIVE */
	};

	private ArrayList<MenuItemImpl> mActionItems;
	private int mActionWidthLimit;
	private Callback mCallback;
	private final Context mContext;
	private int mDefaultShowAsAction = MenuItem.SHOW_AS_ACTION_NEVER;
	private boolean mIsActionItemsStale;
	private boolean mIsVisibleItemsStale;
	private ArrayList<MenuItemImpl> mItems;
	private int mMaxActionItems;
	private ViewGroup mMeasureActionButtonParent;
	private MenuType[] mMenuTypes;
	private ArrayList<MenuItemImpl> mNonActionItems;
	private boolean mReserveActionOverflow;
	private boolean mPreventDispatchingItemsChanged = false;
	//private final Resources mResources;
	private ArrayList<MenuItemImpl> mVisibleItems;
	private SparseBooleanArray mActionButtonGroups;
	
	
	/**
	 * Create a new action bar menu.
	 * 
	 * @param context Context used if resource resolution is required.
	 */
	public MenuBuilder(Context context) {
		mMenuTypes = new MenuType[NUM_TYPES];
		
		mContext = context;
		//mResources = context.getResources();
		
		mItems = new ArrayList<MenuItemImpl>();
		mVisibleItems = new ArrayList<MenuItemImpl>();
		mActionItems = new ArrayList<MenuItemImpl>();
		mNonActionItems = new ArrayList<MenuItemImpl>();
		
		mActionButtonGroups = new SparseBooleanArray();
		
		mIsVisibleItemsStale = true;
		mIsActionItemsStale = true;
	}
	
	
	/**
	 * Adds an item to the menu.  The other add methods funnel to this.
	 * 
	 * @param itemId Unique item ID.
	 * @param groupId Group ID.
	 * @param order Order.
	 * @param title Item title.
	 * @return MenuItem instance.
	 */
	private MenuItem addInternal(int itemId, int groupId, int order, CharSequence title) {
		final int ordering = getOrdering(order);
		final MenuItemImpl item = new MenuItemImpl(this, itemId, groupId, order, ordering, title, mDefaultShowAsAction);
		
		mItems.add(findInsertIndex(mItems, ordering), item);
		onItemsChanged(false);
		
		return item;
	}
	
	private static int findInsertIndex(List<MenuItemImpl> items, int ordering) {
		int index = items.size() - 1;
		while (index >= 0) {
			if (items.get(index).getOrdering() <= ordering) {
				break;
			}
		}
		return index + 1;
	}
	
	private void flagActionItems(boolean reserveActionOverflow) {
		if (reserveActionOverflow != mReserveActionOverflow) {
			mReserveActionOverflow = reserveActionOverflow;
			mIsActionItemsStale = true;
		}
		if (!mIsActionItemsStale) {
			return;
		}
		
		ArrayList<MenuItemImpl> visibleItems = getVisibleItems();
		final int itemsSize = visibleItems.size();
		int maxActions = mMaxActionItems;
		int widthLimit = mActionWidthLimit;
		int querySpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		ViewGroup parent = getMeasureActionButtonParent();
		int requiredItems = 0;
		int requestedItems = 0;
		int firstActionWidth = 0;
		boolean hasOverflow = false;
		
		for (MenuItemImpl item : visibleItems) {
			if (item.requiresActionButton()) {
				requiredItems += 1;
			} else if (item.requestsActionButton()) {
				requestedItems += 1;
			} else {
				hasOverflow = true;
			}
		}
		
		if (reserveActionOverflow && hasOverflow && ((requiredItems + requestedItems) > maxActions)) {
			maxActions -= 1;
		}
		
		if (DEBUG) {
			Log.e("MenuBuilder", "visible item count = " + itemsSize);
			Log.e("MenuBuilder", "requiredItems = " + requiredItems);
			Log.e("MenuBuilder", "requestedItems = " + requestedItems);
			Log.e("MenuBuilder", "hasOverflow = " + hasOverflow);
			Log.e("MenuBuilder", "reserveOverflow = " + reserveActionOverflow);
			Log.e("MenuBuilder", "maxActions (global) = " + mMaxActionItems);
			Log.e("MenuBuilder", "maxActions (local) = " + maxActions);
		}
		
		mActionButtonGroups.clear();
		for (int i = 0; i < itemsSize; i++) {
			MenuItemImpl item = visibleItems.get(i);
			final int groupId = item.getGroupId();
			final boolean inGroup = mActionButtonGroups.get(groupId);
			
			if (DEBUG) {
				Log.e("MenuBuilder", "ITEM[itemId = " + item.getItemId() + ", groupId = " + groupId + ", groupExists = " + inGroup + "]");
			}

			if (item.requiresActionButton()) {
				View v = item.getActionView();
				if (v != null) {
					v = (View)item.getItemView(MenuBuilder.TYPE_ACTION_BAR, parent);
				}

				v.measure(querySpec, querySpec);
				int measuredWidth = v.getMeasuredWidth();
				widthLimit -= measuredWidth;

				if (firstActionWidth == 0) {
					firstActionWidth = measuredWidth;
				}

				if (groupId != 0) {
					mActionButtonGroups.put(groupId, true);
				}
			} else if (item.requestsActionButton()) {
				boolean isAction = ((maxActions > 0) || inGroup) && (widthLimit > 0);
				maxActions -= 1;
				if (isAction) {
					View v = item.getActionView();
					if (v == null) {
						v = (View)item.getItemView(MenuBuilder.TYPE_ACTION_BAR, parent);
					}

					v.measure(querySpec, querySpec);
					int measuredWidth = v.getMeasuredWidth();
					widthLimit -= measuredWidth;

					if (firstActionWidth == 0) {
						firstActionWidth = measuredWidth;
					}

					if ((widthLimit + firstActionWidth) <= 0) {
						isAction = false;
					}
				}
				if (isAction && (groupId != 0)) {
					mActionButtonGroups.put(groupId, true);
					item.setIsActionButton(true);
				}
			} else if (inGroup) {
				item.setIsActionButton(true);
			} else {
				mActionButtonGroups.put(groupId, false);
				for (int j = 0; j < i; j++) {
					MenuItemImpl areYouMyGroupie = visibleItems.get(j);
					if (areYouMyGroupie.getGroupId() == groupId) {
						areYouMyGroupie.setIsActionButton(false);
					}
				}
			}
		}
		
		mActionItems.clear();
		mNonActionItems.clear();
		for (MenuItemImpl item : visibleItems) {
			if (item.isActionButton()) {
				mActionItems.add(item);
			} else {
				mNonActionItems.add(item);
			}
		}
		
		mIsActionItemsStale = false;
		
		if (DEBUG) {
			Log.e("MenuBuilder", "item group count = " + mActionButtonGroups.size());
			Log.e("MenuBuilder", "action items count = " + mActionItems.size());
			Log.e("MenuBuilder", "non-action items count = " + mNonActionItems.size());
		}
	}
	
	private ViewGroup getMeasureActionButtonParent() {
		if (mMeasureActionButtonParent == null) {
			mMeasureActionButtonParent = (ViewGroup)getMenuType(TYPE_ACTION_BAR).getInflater().inflate(LAYOUT_RES_FOR_TYPE[TYPE_ACTION_BAR], null, false);
		}
		return mMeasureActionButtonParent;
	}
	
	private static int getOrdering(int order) {
		int category = (0xFFFF0000 & order) >> 16;
		if (category > sCategoryToOrder.length) {
			throw new IllegalArgumentException("order does not contain a valid category");
		}
		return (sCategoryToOrder[category] << 16) | (0xFFFF & order);
	}
	
	private void onItemsChanged(boolean cleared) {
		if (!mPreventDispatchingItemsChanged) {
			if (!mIsVisibleItemsStale) {
				mIsVisibleItemsStale = true;
			}
			if (!mIsActionItemsStale) {
				mIsActionItemsStale = true;
			}
			
			MenuType[] menuTypes = mMenuTypes;
			for (int i = 0; i < NUM_TYPES; i++) {
				if ((menuTypes[i] != null) && menuTypes[i].hasMenuView()) {
					menuTypes[i].mMenuView.get().updateChildren(cleared);
				}
			}
		}
	}
	
	private void removeItemAtInt(int index, boolean notify) {
		if ((index >= 0) && (index < mItems.size())) {
			mItems.remove(index);
			if (notify) {
				onItemsChanged(false);
			}
		}
	}

	@Override
	public MenuItem add(int titleResourceId) {
		return addInternal(0, 0, 0, mContext.getResources().getString(titleResourceId));
	}

	@Override
	public MenuItem add(int groupId, int itemId, int order, int titleResourceId) {
		return addInternal(itemId, groupId, order, mContext.getResources().getString(titleResourceId));
	}

	@Override
	public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
		return addInternal(itemId, groupId, order, title);
	}
	
	@Override
	public MenuItem add(CharSequence title) {
		return addInternal(0, 0, 0, title);
	}
	
	@Override
	public int addIntentOptions(int groupId, int itemId, int order, ComponentName caller, Intent[] specifics, Intent intent, int flags, android.view.MenuItem[] outSpecificItems) {
		PackageManager pm = mContext.getPackageManager();
		final List<ResolveInfo> lri =
				pm.queryIntentActivityOptions(caller, specifics, intent, 0);
		final int N = lri != null ? lri.size() : 0;

		if ((flags & FLAG_APPEND_TO_GROUP) == 0) {
			removeGroup(groupId);
		}

		for (int i=0; i<N; i++) {
			final ResolveInfo ri = lri.get(i);
			Intent rintent = new Intent(
				ri.specificIndex < 0 ? intent : specifics[ri.specificIndex]);
			rintent.setComponent(new ComponentName(
					ri.activityInfo.applicationInfo.packageName,
					ri.activityInfo.name));
			final MenuItem item = add(groupId, itemId, order, ri.loadLabel(pm))
					.setIcon(ri.loadIcon(pm))
					.setIntent(rintent);
			if (outSpecificItems != null && ri.specificIndex >= 0) {
				outSpecificItems[ri.specificIndex] = item;
			}
		}

		return N;
	}

	@Override
	public SubMenuBuilder addSubMenu(int titleResourceId) {
		return addSubMenu(0, 0, 0, mContext.getResources().getString(titleResourceId));
	}

	@Override
	public SubMenuBuilder addSubMenu(int groupId, int itemId, int order, int titleResourceId) {
		return addSubMenu(groupId, itemId, order, mContext.getResources().getString(titleResourceId));
	}

	@Override
	public SubMenuBuilder addSubMenu(int groupId, int itemId, int order, CharSequence title) {
		MenuItemImpl item = (MenuItemImpl)addInternal(itemId, groupId, order, title);
		SubMenuBuilder subMenu = new SubMenuBuilder(this.mContext, this, item);
		item.setSubMenu(subMenu);
		
		return subMenu;
	}

	@Override
	public SubMenuBuilder addSubMenu(CharSequence title) {
		return addSubMenu(0, 0, 0, title);
	}
	
	@Override
	public void clear() {
		this.mItems.clear();
		onItemsChanged(true);
	}
	
	public void clearAll() {
		mPreventDispatchingItemsChanged = true;
		clear();
		mPreventDispatchingItemsChanged = false;
		onItemsChanged(true);
	}
	
	@Override
	public void close() {
		close(true);
	}
	
	final void close(boolean something) {
		if (getCallback() != null) {
			getCallback().onCloseMenu(this, something);
		}
	}
	
	public int findGroupIndex(int groupId) {
		return findGroupIndex(groupId, 0);
	}
	
	public int findGroupIndex(int groupId, int startAt) {
		final int count = size();
		if (startAt < 0) {
			startAt = 0;
		}
		for (int i = startAt; i < count; i++) {
			if (mItems.get(i).getGroupId() == groupId) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public MenuItemImpl findItem(int itemId) {
		for (MenuItemImpl item : this.mItems) {
			if (item.getItemId() == itemId) {
				return item;
			}
		}
		throw new IndexOutOfBoundsException("No item with id " + itemId);
	}
	
	public int findItemIndex(int itemId) {
		final int count = mItems.size();
		for (int i = 0; i < count; i++) {
			if (mItems.get(i).getItemId() == itemId) {
				return i;
			}
		}
		return -1;
	}
	
	ArrayList<MenuItemImpl> getActionItems(boolean includeOverflow) {
		flagActionItems(includeOverflow);
		return mActionItems;
	}
	
	public Callback getCallback() {
		return mCallback;
	}
	
	public Context getContext() {
		return this.mContext;
	}
	
	@Override
	public MenuItem getItem(int index) {
		return mItems.get(index);
	}
	
	public MenuAdapter getMenuAdapter(int menuType) {
		return new MenuAdapter(menuType);
	}
	
	MenuType getMenuType(int menuType) {
		if (mMenuTypes[menuType] == null) {
			mMenuTypes[menuType] = new MenuType(menuType);
		}
		
		return mMenuTypes[menuType];
	}
	
	public View getMenuView(int menuType, ViewGroup parent) {
		return (View)getMenuType(menuType).getMenuView(parent);
	}
	
	ArrayList<MenuItemImpl> getNonActionItems(boolean includeOverflow) {
		flagActionItems(includeOverflow);
		return mNonActionItems;
	}
	
	public MenuItem getOverflowItem(int index) {
		flagActionItems(true);
		return mNonActionItems.get(index);
	}
	
	public MenuAdapter getOverflowMenuAdapter(int menuType) {
		return new OverflowMenuAdapter(menuType);
	}
	
	public MenuBuilder getRootMenu() {
		return this;
	}
	
	ArrayList<MenuItemImpl> getVisibleItems() {
		if (mIsVisibleItemsStale) {
			mVisibleItems.clear();
			for (MenuItemImpl item : mItems) {
				if (item.isVisible()) {
					mVisibleItems.add(item);
				}
			}
			
			mIsVisibleItemsStale = false;
			mIsActionItemsStale = true;
		}
		return mVisibleItems;
	}
	
	public boolean hasVisibleItems() {
		for (MenuItemImpl item : mItems) {
			if (item.isVisible()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isShortcutKey(int keyCode, KeyEvent event) {
		return false;
	}
	
	void onItemActionRequestChanged(MenuItemImpl menuItem) {
		onItemsChanged(false);
	}
	
	void onItemVisibleChanged(MenuItemImpl paramMenuItemImpl) {
		onItemsChanged(false);
	}
	
	@Override
	public boolean performIdentifierAction(int itemId, int flags) {
		return performItemAction(findItem(itemId), flags);
	}
	
	public boolean performItemAction(MenuItem item, int flags) {
		final MenuItemImpl itemImpl = (MenuItemImpl)item;
		
		if ((itemImpl == null) || !itemImpl.isEnabled()) {
			return false;
		}
		
		boolean invoked = itemImpl.invoke();
		
		if (itemImpl.hasSubMenu()) {
			close(false);
			
			if (mCallback != null) {
				// Return true if the sub menu was invoked or the item was invoked previously
				invoked |= mCallback.onSubMenuSelected((SubMenuBuilder)item.getSubMenu());
			}
		} else {
			if ((flags & Menu.FLAG_PERFORM_NO_CLOSE) == 0) {
				close(true);
			}
		}
		
		return invoked;
	}

	@Override
	public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
		return false;
	}
	
	@Override
	public void removeGroup(int groupId) {
		int i = findGroupIndex(groupId);
		for (int j = i; j >= 0; j--) {
			if (mItems.get(j).getGroupId() != groupId) {
				break;
			}
			removeItemAtInt(j, false);
		}
		onItemsChanged(false);
	}

	@Override
	public void removeItem(int itemId) {
		removeItemAtInt(findItemIndex(itemId), true);
	}
	
	public void removeItemAt(int index) {
		removeItemAtInt(index, true);
	}
	
	public void restoreHeirarchyState(Bundle saveInstanceState) {
		
	}
	
	public void saveHeirarchyState(Bundle saveInstanceState) {
		
	}
	
	public void setActionWidthLimit(int width) {
		mActionWidthLimit = width;
		mIsActionItemsStale = true;
	}
	
	public void setCallback(Callback callback) {
		mCallback = callback;
	}
	
	public MenuBuilder setDefaultShowAsAction(int showAsAction) { 
		mDefaultShowAsAction = showAsAction;
		return this;
	}

	void setExclusiveItemChecked(MenuItem item) {
		final int group = item.getGroupId();
		
		final int N = mItems.size();
		for (int i = 0; i < N; i++) {
			MenuItemImpl curItem = mItems.get(i);
			if (curItem.getGroupId() == group) {
				if (!curItem.isExclusiveCheckable()) continue;
				if (!curItem.isCheckable()) continue;
				
				// Check the item meant to be checked, uncheck the others (that are in the group)
				curItem.setCheckedInt(curItem == item);
			}
		}
	}

	@Override
	public void setGroupCheckable(int groupId, boolean checkable, boolean exclusive) {
		final int N = mItems.size();
		for (int i = 0; i < N; i++) {
			MenuItemImpl item = mItems.get(i);
			if (item.getGroupId() == groupId) {
				item.setExclusiveCheckable(exclusive);
				item.setCheckable(checkable);
			}
		}
	}

	@Override
	public void setGroupEnabled(int groupId, boolean enabled) {
		final int size = this.mItems.size();
		for (int i = 0; i < size; i++) {
			MenuItemImpl item = mItems.get(i);
			if (item.getGroupId() == groupId) {
				item.setEnabled(enabled);
			}
		}
	}

	@Override
	public void setGroupVisible(int groupId, boolean visible) {
		final int size = this.mItems.size();
		for (int i = 0; i < size; i++) {
			MenuItemImpl item = mItems.get(i);
			if (item.getGroupId() == groupId) {
				item.setVisible(visible);
			}
		}
	}
	
	void setMaxActionItems(int maxItems) {
		mMaxActionItems = maxItems;
		mIsActionItemsStale = true;
	}

	@Override
	public void setQwertyMode(boolean isQwerty) {
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public int size() {
		return this.mItems.size();
	}
	
	
	class OverflowMenuAdapter extends MenuAdapter {
		public OverflowMenuAdapter(int menuType) {
			super(menuType);
		}
		
		@Override
		public int getCount() {
			return getNonActionItems(true).size();
		}
		
		@Override
		public MenuItemImpl getItem(int index) {
			return getNonActionItems(true).get(index);
		}
	}
	
	public class MenuAdapter extends BaseAdapter {
		private int mMenuType;
		
		public MenuAdapter(int menuType) {
			mMenuType = menuType;
		}
		
		@Override
		public int getCount() {
			return getVisibleItems().size();
		}
		
		@Override
		public MenuItemImpl getItem(int index) {
			return getVisibleItems().get(index);
		}
		
		@Override
		public long getItemId(int itemId) {
			return itemId;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			return (View)((MenuItemImpl) getItem(position)).getItemView(mMenuType, parent);
		}
	}

	public interface ItemInvoker {
		boolean invokeItem(MenuItemImpl paramMenuItemImpl);
	}

	public interface Callback {
		void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean);
		void onCloseSubMenu(SubMenuBuilder paramSubMenuBuilder);
		boolean onMenuItemSelected(MenuBuilder paramMenuBuilder, MenuItem paramMenuItem);
		void onMenuModeChange(MenuBuilder paramMenuBuilder);
		boolean onSubMenuSelected(SubMenuBuilder paramSubMenuBuilder);
	}

	class MenuType {
		private LayoutInflater mInflater;
		private int mMenuType;
		private WeakReference<MenuView> mMenuView;
		
		MenuType(int menuType) {
			mMenuType = menuType;
		}
		
		
		LayoutInflater getInflater() {
			if (mInflater == null) {
				Context context = new ContextThemeWrapper(getContext(), MenuBuilder.THEME_RES_FOR_TYPE[mMenuType]);
				mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			return mInflater;
		}
		
		MenuView getMenuView(ViewGroup parent) {
			if (LAYOUT_RES_FOR_TYPE[mMenuType] == 0) {
				return null;
			}
			
			synchronized (this) {
				MenuView menuView = mMenuView != null ? mMenuView.get() : null;
				if (menuView == null) {
					menuView = (MenuView)getInflater().inflate(LAYOUT_RES_FOR_TYPE[mMenuType], parent, false);
					menuView.initialize(MenuBuilder.this, mMenuType);
					
					// Cache the view
					mMenuView = new WeakReference<MenuView>(menuView);
				}
				
				return menuView;
			}
		}
		
		boolean hasMenuView() {
			return (mMenuView != null) && (mMenuView.get() != null);
		}
	}
}