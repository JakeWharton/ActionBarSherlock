package com.actionbarsherlock.sample.knownbugs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

public class Issue738 extends SherlockActivity implements ActionBar.OnNavigationListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setListNavigationCallbacks(new DropdownItemAdapter(this), this);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return true;
    }

    private class DropdownItemAdapter extends ArrayAdapter<String> implements SpinnerAdapter {

        private static final String LONG_TEXT = "This long text wraps in API 15,16 but does not wrap in API 10.";

        Activity activity;
        private String[] labels = new String[] {"short1", LONG_TEXT};

        public DropdownItemAdapter(Activity activity) {
            super(activity, R.layout.sherlock_spinner_item, new String[] {"short1", LONG_TEXT});

            this.activity = activity;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.issue738_spinner_dropdown_item, null);

            TextView title = ((TextView) convertView.findViewById(android.R.id.text1));
            title.setText(labels[position]);

            return convertView;
        }
    }
}
