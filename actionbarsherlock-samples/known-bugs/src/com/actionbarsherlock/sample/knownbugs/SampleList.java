/*
 * Copyright (C) 2012 Jake Wharton
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

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.ACTION_MAIN;
import static com.actionbarsherlock.app.ActionBar.NAVIGATION_MODE_TABS;
import static java.util.Locale.ENGLISH;

public class SampleList extends SherlockListActivity implements ActionBar.TabListener {
    private final IntentAdapter mAdapter = new IntentAdapter();
    private String mCategory = "OPEN";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(NAVIGATION_MODE_TABS);
        actionBar.addTab(actionBar.newTab().setText("Open").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("Closed").setTabListener(this));

        setListAdapter(mAdapter);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onListItemClick(ListView l, View v, int position, long id) {
        startActivity(mAdapter.getItem(position));
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mCategory = tab.getText().toString().toUpperCase(ENGLISH);
        mAdapter.refresh();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        /* Empty */
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        /* Empty */
    }

  private class IntentAdapter extends BaseAdapter {
        private final List<CharSequence> mNames;
        private final Map<CharSequence, Intent> mIntents;

        IntentAdapter() {
            mNames = new ArrayList<CharSequence>();
            mIntents = new HashMap<CharSequence, Intent>();
        }

        void refresh() {
            mNames.clear();
            mIntents.clear();

            final Intent mainIntent = new Intent(ACTION_MAIN, null);
            mainIntent.addCategory("com.actionbarsherlock.sample.knownbugs." + mCategory);

            PackageManager pm = getPackageManager();
            final List<ResolveInfo> matches = pm.queryIntentActivities(mainIntent, 0);
            for (ResolveInfo match : matches) {
                Intent intent = new Intent();
                intent.setClassName(match.activityInfo.packageName, match.activityInfo.name);
                final CharSequence name = match.loadLabel(pm);
                mNames.add(name);
                mIntents.put(name, intent);
            }

            notifyDataSetChanged();
        }

      @Override
      public int getCount() {
          return mNames.size();
      }

      @Override
      public Intent getItem(int position) {
          return mIntents.get(mNames.get(position));
      }

      @Override
      public long getItemId(int position) {
          return position;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
          TextView tv = (TextView)convertView;
          if (convertView == null) {
              tv = (TextView) LayoutInflater.from(SampleList.this).inflate(android.R.layout.simple_list_item_1, parent, false);
          }
          tv.setText(mNames.get(position));
          return tv;
      }
    }
}
