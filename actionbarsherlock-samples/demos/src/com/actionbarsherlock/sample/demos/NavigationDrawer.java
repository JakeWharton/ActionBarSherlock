package com.actionbarsherlock.sample.demos;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActionBarDrawerToggle;
import com.actionbarsherlock.app.SherlockActionBarDrawerToggleDelegate;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class NavigationDrawer extends SherlockActivity implements ActionBarDrawerToggle.DelegateProvider {
    private static String STATE_INT_POSITION = NavigationDrawer.class.getName() + ".state.INT_POSITION";
    private static String STATE_BOOLEAN_IS_DRAWER_OPEN = NavigationDrawer.class.getName() +
            ".state.BOOLEAN_IS_DRAWER_OPEN";

    private int mPosition;
    private boolean mIsDrawerOpen;
    private String[] mTitles;
    private String[] mTexts;
    private TextView mText;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private SherlockActionBarDrawerToggle mDrawerToggle;
    private SherlockActionBarDrawerToggleDelegate mDrawerDelegate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        mTitles = getResources().getStringArray(R.array.navigation_drawer_titles);
        mTexts = getResources().getStringArray(R.array.navigation_drawer_texts);

        mText = (TextView) findViewById(R.id.text);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(getDrawerShadowResId(), GravityCompat.START);

        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.navigation_drawer_list_item, mTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerDelegate = new SherlockActionBarDrawerToggleDelegate(this);
        mDrawerToggle = new SherlockActionBarDrawerToggle(this, mDrawerLayout, getDrawerIndicatorResId(),
                R.string.navigation_drawer_open_description, R.string.navigation_drawer_closed_description) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mIsDrawerOpen = true;
                setTitle(getString(R.string.navigation_drawer_open_title));
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mIsDrawerOpen = false;
                selectedPosition(mPosition);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(STATE_INT_POSITION);
            mIsDrawerOpen = savedInstanceState.getBoolean(STATE_BOOLEAN_IS_DRAWER_OPEN);
        }

        selectedPosition(mPosition);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_INT_POSITION, mPosition);
        outState.putBoolean(STATE_BOOLEAN_IS_DRAWER_OPEN, mIsDrawerOpen);
    }

    @Override
    public ActionBarDrawerToggle.Delegate getDrawerToggleDelegate() {
        return mDrawerDelegate;
    }

    private int getDrawerIndicatorResId() {
        if (SampleList.THEME == R.style.Theme_Sherlock_Light) {
            return R.drawable.ic_drawer_inverse;
        }
        return R.drawable.ic_drawer;
    }

    private int getDrawerShadowResId() {
        if (SampleList.THEME == R.style.Theme_Sherlock_Light) {
            return R.drawable.drawer_shadow_inverse;
        }
        return R.drawable.drawer_shadow;
    }

    private void selectedPosition(int position) {
        if (mIsDrawerOpen) {
            setTitle(getString(R.string.navigation_drawer_open_title));
        } else {
            setTitle(mTitles[position]);
        }
        mText.setText(mTexts[position]);
        mDrawerList.setItemChecked(position, true);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            mPosition = position;
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }
}