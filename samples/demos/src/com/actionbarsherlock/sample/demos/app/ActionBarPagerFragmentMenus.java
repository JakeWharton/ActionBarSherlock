package com.actionbarsherlock.sample.demos.app;

import java.util.Random;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.sample.demos.R;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ActionBarPagerFragmentMenus extends SherlockActivity {
    private static final Random RANDOM = new Random();
    private static final int PAGES = 10;
    private static final int MENU_ITEM_RANDOM = 1;
    
    private ViewPager mPager;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.actionbar_pagerfragmentmenus);
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(new TestAdapter(getSupportFragmentManager()));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_ITEM_RANDOM, 0, "Random");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_ITEM_RANDOM) {
            mPager.setCurrentItem(RANDOM.nextInt(PAGES));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static final class TestAdapter extends FragmentPagerAdapter {
        public TestAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return PAGES;
        }

        @Override
        public Fragment getItem(int position) {
            TestFragment f = new TestFragment();
            f.text = String.valueOf(position + 1);
            return f;
        }
    }
    
    public static class TestFragment extends SherlockFragment {
        String text = "???";
        
        public TestFragment() {
            setRetainInstance(true);
        }
        
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            TextView tv = new TextView(getActivity());
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(50);
            tv.setText(text);
            return tv;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            menu.add(text).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        }
    }
}
