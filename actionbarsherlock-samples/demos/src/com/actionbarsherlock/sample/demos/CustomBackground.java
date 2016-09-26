package com.actionbarsherlock.sample.demos;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

public class CustomBackground extends SherlockActivity {

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case 10:// green
			getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
					Color.parseColor("#00853c")));
			return false;
		case 11:// blue
			getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
					Color.parseColor("#0000A0")));
			return false;
		case 12:// red
			getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
					Color.parseColor("#F70D1A")));
			return false;
		default:
			return false;

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		SubMenu subMenu1 = menu.addSubMenu("Action Item");
		subMenu1.add(0, 10, 0, "Green");
		subMenu1.add(0, 11, 0, "Blue");
		subMenu1.add(0, 12, 0, "Red");

		MenuItem subMenu1Item = subMenu1.getItem();
		subMenu1Item.setIcon(R.drawable.ic_title_share_default);
		subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
				| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(SampleList.THEME); // Used for theme switching in samples
		//setTheme(R.style.Widget_MyTheme_ActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_background);
	//	((TextView) findViewById(R.id.text)).setText(R.string.submenus_content);
		
		/*getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
				Color.parseColor("#00853c")));
		*/
		Button btn = (Button)findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
						Color.parseColor("#0000A0")));
			}
		});
	}
}
