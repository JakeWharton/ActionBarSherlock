package com.jakewharton.android.actionbarsherlock.sample.shakespeare.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import com.jakewharton.android.actionbarsherlock.ActionBarSherlock;
import com.jakewharton.android.actionbarsherlock.sample.shakespeare.ActionBarForAndroidActionBar;
import com.jakewharton.android.actionbarsherlock.sample.shakespeare.R;
import com.jakewharton.android.actionbarsherlock.sample.shakespeare.fragments.DetailsFragment;

public class DetailsActivity extends ActionBarSherlock.FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            this.finish();
            return;
        }

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            DetailsFragment details = new DetailsFragment();
            details.setArguments(this.getIntent().getExtras());
            
            ActionBarSherlock.from(this)
            	.layout(details)
            	.title(R.string.activity_details)
            	.handleCustom(ActionBarForAndroidActionBar.Handler.class)
            	.attach();
        }
    }
}
