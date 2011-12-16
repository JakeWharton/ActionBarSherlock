package com.actionbarsherlock.tests.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public final class Issue0002 extends FragmentActivity {
	public static final String MENU_ITEM_TEXT = "Click";
	
	public boolean triggered = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getSupportFragmentManager().beginTransaction()
        	.add(android.R.id.content, new TestFragment())
        	.commit();
    }
    
    public final class TestFragment extends Fragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
    		setHasOptionsMenu(true);
		}

		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			menu.add(0, 0, 0, MENU_ITEM_TEXT)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			return new FrameLayout(getActivity());
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			triggered = true;
			return true;
		}
    }
}