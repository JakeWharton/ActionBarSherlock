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
package com.actionbarsherlock.sample.demos;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;

public class SearchViews extends SherlockActivity implements SearchView.OnQueryTextListener,
        SearchView.OnSuggestionListener {

    private static final String[] COLUMNS = {
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
    };

    private SuggestionsAdapter mSuggestionsAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Used to put dark icons on light action bar
        boolean isLight = SampleList.THEME == R.style.Theme_Sherlock_Light;

        //Create the search view
        SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Search for countries…");
        searchView.setOnQueryTextListener(this);
        searchView.setOnSuggestionListener(this);

        if (mSuggestionsAdapter == null) {
            MatrixCursor cursor = new MatrixCursor(COLUMNS);
            cursor.addRow(new String[]{"1", "'Murica"});
            cursor.addRow(new String[]{"2", "Canada"});
            cursor.addRow(new String[]{"3", "Denmark"});
            mSuggestionsAdapter = new SuggestionsAdapter(getSupportActionBar().getThemedContext(), cursor);
        }

        searchView.setSuggestionsAdapter(mSuggestionsAdapter);

        menu.add("Search")
            .setIcon(isLight ? R.drawable.ic_search_inverse : R.drawable.abs__ic_search)
            .setActionView(searchView)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text);
        ((TextView)findViewById(R.id.text)).setText(R.string.search_views_content);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, "You searched for: " + query, Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        Cursor c = (Cursor) mSuggestionsAdapter.getItem(position);
        String query = c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
        Toast.makeText(this, "Suggestion clicked: " + query, Toast.LENGTH_LONG).show();
        return true;
    }

    private class SuggestionsAdapter extends CursorAdapter {

        public SuggestionsAdapter(Context context, Cursor c) {
            super(context, c, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tv = (TextView) view;
            final int textIndex = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1);
            tv.setText(cursor.getString(textIndex));
        }
    }
}
