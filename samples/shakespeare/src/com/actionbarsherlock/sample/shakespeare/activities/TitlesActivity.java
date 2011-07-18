package com.actionbarsherlock.sample.shakespeare.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.actionbarsherlock.sample.shakespeare.R;

public class TitlesActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.activity_titles);
        setContentView(R.layout.activity_titles);
    }
}
