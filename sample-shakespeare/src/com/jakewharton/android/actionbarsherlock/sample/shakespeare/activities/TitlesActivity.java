package com.jakewharton.android.actionbarsherlock.sample.shakespeare.activities;

import android.os.Bundle;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock;
import com.jakewharton.android.actionbarsherlock.sample.shakespeare.ActionBarForAndroidActionBar;
import com.jakewharton.android.actionbarsherlock.sample.shakespeare.R;

public class TitlesActivity extends ActionBarSherlock.FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ActionBarSherlock.from(this)
        	.with(savedInstanceState)
        	.layout(R.layout.activity_titles)
        	.title(R.string.activity_titles)
        	.handleCustom(ActionBarForAndroidActionBar.Handler.class)
        	.attach();
    }
}
