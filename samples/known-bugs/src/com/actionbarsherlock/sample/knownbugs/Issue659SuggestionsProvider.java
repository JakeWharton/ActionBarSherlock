package com.actionbarsherlock.sample.knownbugs;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Sample search recent suggestions provider. For Issue 659
 */
public class Issue659SuggestionsProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.actionbarsherlock.sample.knownbugs.Issue659SuggestionsProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public Issue659SuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
