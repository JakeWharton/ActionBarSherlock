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

package com.actionbarsherlock.sample.knownbugs;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;

import static com.actionbarsherlock.app.ActionBar.NAVIGATION_MODE_TABS;
import static com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_ALWAYS;
import static com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_WITH_TEXT;

public class Issue379 extends SherlockActivity implements ActionBar.TabListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getSupportActionBar();
        ab.setNavigationMode(NAVIGATION_MODE_TABS);
        ab.addTab(ab.newTab().setTabListener(this).setText("Test"));

        TextView tv = new TextView(this);
        tv.setText("ColorDrawable ignores bounds on pre-HC. Make sure you see three colors.");
        setContentView(tv);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Test").setShowAsAction(SHOW_AS_ACTION_ALWAYS | SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }
}
