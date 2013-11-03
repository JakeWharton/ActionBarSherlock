package com.actionbarsherlock.sample.demos;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class CustomTabs extends SherlockActivity implements TabListener{

	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Used to put dark icons on light action bar
        boolean isLight = SampleList.THEME == R.style.Theme_Sherlock_Light;

        menu.add("Search")
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        menu.add("Refresh")
            .setIcon(isLight ? R.drawable.ic_refresh_inverse : R.drawable.ic_refresh)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(SampleList.THEME); //Used for theme switching in samples
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(Boolean.TRUE); 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text);
        setContent((TextView)findViewById(R.id.text));
        
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        for (int i = 1; i <= 3; i++) {
            ActionBar.Tab tab = getSupportActionBar().newTab();
            //tab.setText("Tab " + i);
            tab.setIcon(R.drawable.abs__ic_search);
            tab.setTabListener(this);
            getSupportActionBar().addTab(tab);
        }
        
        
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }
        
    }

    protected void setContent(TextView view) {
        view.setText(R.string.action_items_content);
    }
    

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		
	}
	
}
