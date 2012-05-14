package com.actionbarsherlock.sample.knownbugs;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import static android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM;

public class Issue435 extends SherlockActivity implements View.OnClickListener, ActionBar.TabListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView t = new TextView(this);
        t.setText("Must be on a portrait device where the tabs are stacked. Stacked background will disappear when action mode is triggered.");
        layout.addView(t);

        Button b = new Button(this);
        b.setText("Start ActionMode");
        b.setOnClickListener(this);
        layout.addView(b);

        setContentView(layout);

        ActionBar ab = getSupportActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ab.addTab(ab.newTab().setText("One").setTabListener(this));
        ab.addTab(ab.newTab().setText("One").setTabListener(this));
        ab.addTab(ab.newTab().setText("One").setTabListener(this));

        ab.setBackgroundDrawable(new GradientDrawable(TOP_BOTTOM, new int[] { 0xFF004400, 0xFF002200 }));
        ab.setStackedBackgroundDrawable(new GradientDrawable(TOP_BOTTOM, new int[] { 0xFF440000, 0xFF220000 }));
    }

    @Override public void onClick(View v) {
        ActionMode am = startActionMode(new SuperSweetActionModeOfScience());
        am.setTitle("Hello, Broken?");
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        /* Empty */
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        /* Empty */
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        /* Empty */
    }

    private static final class SuperSweetActionModeOfScience implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            /* Empty */
        }
    }
}
