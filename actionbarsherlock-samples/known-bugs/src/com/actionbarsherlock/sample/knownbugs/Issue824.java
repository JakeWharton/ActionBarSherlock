package com.actionbarsherlock.sample.knownbugs;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.os.Bundle;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class Issue824 extends SherlockFragmentActivity {
  // Consistent fragment instance
	myFragment myFrag = null;
	
	// Views
	FrameLayout fl = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		
		Button b = new Button(this);
		b.setText("Repeat");
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// Reattach the same fragment
				getSupportFragmentManager().beginTransaction().replace(fl.getId(), myFrag).commit();
				
			}
		});
		
		fl = new FrameLayout(this);
		fl.setId(200);
		fl.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		myFrag = new myFragment();
		getSupportFragmentManager().beginTransaction().add(fl.getId(), myFrag).commit();
	
		ll.addView(b);
		ll.addView(fl);
		
		setContentView(ll);
	}
	
	public static class myFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			TextView tv = new TextView(getActivity());
			tv.setText("My fragment");
			tv.setGravity(Gravity.CENTER);
			tv.setBackgroundColor(Color.RED);
			
			return tv;
		}
	}
	
}
