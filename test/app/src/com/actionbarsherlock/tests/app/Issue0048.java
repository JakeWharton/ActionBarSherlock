package com.actionbarsherlock.tests.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public final class Issue0048 extends SherlockActivity {
    private Menu mMenu;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ViewPager pager = new ViewPager(this);
        pager.setId(1);
        setContentView(pager);
        
        pager.setAdapter(new TestAdapter(getSupportFragmentManager()));
        invalidateOptionsMenu();
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }
	
	public Menu getMenu() {
	    return mMenu;
	}

    static final class TestAdapter extends FragmentPagerAdapter {
        public TestAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            return new TestFragment();
        }
	}
	
	public static class TestFragment extends SherlockFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return new TextView(getActivity());
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            menu.add("Test");
        }
	    
	}
}