package com.jakewharton.android.actionbarsherlock.sample.shakespeare.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.jakewharton.android.actionbarsherlock.sample.shakespeare.fragments.DetailsFragment;

public class DetailsActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            DetailsFragment details = new DetailsFragment();
            details.setArguments(getIntent().getExtras());
            
            getSupportFragmentManager()
            	.beginTransaction()
            	.add(android.R.id.content, details)
            	.commit();
        }
    }
}
