package com.actionbarsherlock.sample.demos.app;

import java.util.Random;
import com.actionbarsherlock.sample.demos.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentPagerMenus extends FragmentActivity {
    private static final Random RANDOM = new Random();
    private static final int PAGES = 10;
    private static final int MENU_ITEM_RANDOM = 1;
    
    private ViewPager mPager;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_pagermenus);
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setId(1);
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

    public static class TestFragment extends Fragment {
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
            menu.add(text);
        }
    }
}
