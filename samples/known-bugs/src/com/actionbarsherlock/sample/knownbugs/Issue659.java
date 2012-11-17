package com.actionbarsherlock.sample.knownbugs;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.actionbarsherlock.R;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.widget.SearchView;

/**
 * Example of Search Suggestions not working for SearchView.
 * Issue #659
 * @author michael@turntable.fm
 *
 */
public class Issue659 extends SherlockActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new FrameLayout(this));

        Intent intent  = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    Issue659SuggestionsProvider.AUTHORITY, Issue659SuggestionsProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            Toast.makeText(this, "Search called with: " + query, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.issue659_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ((SearchView) menu.findItem(R.id.action_search).getActionView()).setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // uncommenting the following code will allow recent search suggestions to work on honeycomb and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            android.widget.SearchView searchView = new android.widget.SearchView(getSupportActionBar().getThemedContext());
//            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//            menu.findItem(R.id.action_search).setActionView(searchView);
        }

        return true;
    }
}
