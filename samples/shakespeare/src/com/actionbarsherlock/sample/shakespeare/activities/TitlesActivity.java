package com.actionbarsherlock.sample.shakespeare.activities;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.sample.shakespeare.R;

public class TitlesActivity extends SherlockActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.activity_titles);
        setContentView(R.layout.activity_titles);
    }
}
