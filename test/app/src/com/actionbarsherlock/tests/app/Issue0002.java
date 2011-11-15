package com.actionbarsherlock.tests.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public final class Issue0002 extends SherlockActivity {
	public static final String MENU_ITEM_TEXT = "Click";
	
	public boolean triggered = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getSupportFragmentManager().beginTransaction()
        	.add(android.R.id.content, new TestFragment())
        	.commit();
    }
    
    public final class TestFragment extends SherlockFragment {
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