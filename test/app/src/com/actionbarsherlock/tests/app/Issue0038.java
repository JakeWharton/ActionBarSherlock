package com.actionbarsherlock.tests.app;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public final class Issue0038 extends TabActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issue0038);
        
        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
        tabHost.addTab(
        	tabHost.newTabSpec("tab1")
        	       .setIndicator("First Tab Name")
        	       .setContent(new Intent(this, InnerActivity.class))
        );
    }
	
	public static class InnerActivity extends FragmentActivity {
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.blank);
	    }
	}
}