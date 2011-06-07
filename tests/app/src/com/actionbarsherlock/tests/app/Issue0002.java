package com.actionbarsherlock.tests.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;

public final class Issue0002 extends FragmentActivity {
	public static final String NO = "NO";
	public static final String YES = "YES";
	public static final int MENU_ITEM_ID = 823462;
	public static final String MENU_ITEM_TEXT = "Click";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Frag frag = new Frag();
        getSupportFragmentManager().beginTransaction()
        	.add(android.R.id.content, frag)
        	.commit();
    }
    
    private static final class Frag extends Fragment {
    	private EditText mText;
    	
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
    		setHasOptionsMenu(true);
		}

		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			menu.add(0, MENU_ITEM_ID, 0, MENU_ITEM_TEXT)
				.setIcon(R.drawable.ic_menu_star_holo_light)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			mText = new EditText(getActivity());
			mText.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			mText.setText(NO);
			return mText;
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			mText.setText(YES);
			return true;
		}
    }
}