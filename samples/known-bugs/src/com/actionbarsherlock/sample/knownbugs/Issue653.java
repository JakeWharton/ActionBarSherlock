package com.actionbarsherlock.sample.knownbugs;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.SearchView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
/*
 * search suggestion can be fired if import android.widget.SearchView
 * search suggestion can not be fired if com.actionbarsherlock.widget.SearchView
 */
//import com.actionbarsherlock.widget.SearchView;



public class Issue653 extends SherlockActivity  {
	SearchManager searchManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
    	setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.issue653);
		
		searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

        SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        
        menu.add("Search")
        .setActionView(searchView)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		return true;
	}
}
