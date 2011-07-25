/*
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
package com.actionbarsherlock.sample.demos.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.SubMenu;
import com.actionbarsherlock.sample.demos.R;

public class ActionBarSubMenus extends FragmentActivity {
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
    	SubMenu subMenu1 = menu.addSubMenu("Action Item");
    	subMenu1.add("Sample");
    	subMenu1.add("Menu");
    	subMenu1.add("Items");
    	
    	MenuItem subMenu1Item = subMenu1.getItem();
    	subMenu1Item.setIcon(R.drawable.ic_title_share_default);
    	subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    	
    	SubMenu subMenu2 = menu.addSubMenu("Overflow Item");
    	subMenu2.add("These");
    	subMenu2.add("Are");
    	subMenu2.add("Sample");
    	subMenu2.add("Items");
    	
    	MenuItem subMenu2Item = subMenu2.getItem();
    	subMenu2Item.setIcon(R.drawable.ic_compose);
    	
		return super.onCreateOptionsMenu(menu);
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actionbar_submenus);
    }
}
