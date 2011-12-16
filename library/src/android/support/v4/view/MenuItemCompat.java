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

package android.support.v4.view;

import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;

/**
 * @Deprecated Use {@link FragmentActivity}, {@link Menu}, and {@link MenuItem}.
 */
@Deprecated
public class MenuItemCompat {

    /**
     * Never show this item as a button in an Action Bar.
     */
    public static final int SHOW_AS_ACTION_NEVER = 0;

    /**
     * Show this item as a button in an Action Bar if the system
     * decides there is room for it.
     */
    public static final int SHOW_AS_ACTION_IF_ROOM = 1;

    /**
     * Always show this item as a button in an Action Bar. Use sparingly!
     * If too many items are set to always show in the Action Bar it can
     * crowd the Action Bar and degrade the user experience on devices with
     * smaller screens. A good rule of thumb is to have no more than 2
     * items set to always show at a time.
     */
    public static final int SHOW_AS_ACTION_ALWAYS = 2;

    /**
     * When this item is in the action bar, always show it with a
     * text label even if it also has an icon specified.
     */
    public static final int SHOW_AS_ACTION_WITH_TEXT = 4;

    /**
     * This item's action view collapses to a normal menu item.
     * When expanded, the action view temporarily takes over
     * a larger segment of its container.
     */
    public static final int SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW = 8;

    /**
     * Interface for the full API.
     */
    interface MenuVersionImpl {
        public boolean setShowAsAction(MenuItem item, int actionEnum);
        public MenuItem setActionView(MenuItem item, View view);
    }

    /**
     * Interface implementation that doesn't use anything about v4 APIs.
     */
    static class BaseMenuVersionImpl implements MenuVersionImpl {
        @Override
        public boolean setShowAsAction(MenuItem item, int actionEnum) {
            return false;
        }

        @Override
        public MenuItem setActionView(MenuItem item, View view) {
            return item;
        }
    }

    /**
     * Interface implementation for devices with at least v11 APIs.
     */
    static class HoneycombMenuVersionImpl implements MenuVersionImpl {
        @Override
        public boolean setShowAsAction(MenuItem item, int actionEnum) {
            MenuItemCompatHoneycomb.setShowAsAction(item, actionEnum);
            return true;
        }
        @Override
        public MenuItem setActionView(MenuItem item, View view) {
            return MenuItemCompatHoneycomb.setActionView(item, view);
        }
    }

    /**
     * Select the correct implementation to use for the current platform.
     */
    static final MenuVersionImpl IMPL;
    static {
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            IMPL = new HoneycombMenuVersionImpl();
        } else {
            IMPL = new BaseMenuVersionImpl();
        }
    }

    // -------------------------------------------------------------------

    /** @Deprecated Use {@link MenuItem#setShowAsAction(int)}. */
    @Deprecated
    public static boolean setShowAsAction(MenuItem item, int actionEnum) {
        return IMPL.setShowAsAction(item, actionEnum);
    }

    /** @Deprecated Use {@link MenuItem#setActionView(int)}. */
    @Deprecated
    public static MenuItem setActionView(MenuItem item, View view) {
        return IMPL.setActionView(item, view);
    }
}
