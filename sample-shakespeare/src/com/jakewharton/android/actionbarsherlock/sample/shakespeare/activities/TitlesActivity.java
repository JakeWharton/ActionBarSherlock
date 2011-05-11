package com.jakewharton.android.actionbarsherlock.sample.shakespeare.activities;

import android.os.Bundle;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.ActionBarHandler;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock.HasTitle;
import com.jakewharton.android.actionbarsherlock.handler.Android_ActionBar;
import com.jakewharton.android.actionbarsherlock.sample.shakespeare.R;

public class TitlesActivity extends ActionBarSherlock.FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ActionBarHandler<?> handler = ActionBarSherlock.from(this)
        	.with(savedInstanceState)
        	.layout(R.layout.activity_titles)
        	.handleCustom(Android_ActionBar.Handler.class)
        	.attach();
        
        if (handler instanceof HasTitle) {
        	((HasTitle)handler).setTitle(R.string.activity_titles);
        }
    }
}
