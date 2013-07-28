package com.actionbarsherlock.sample.knownbugs;

import android.os.Bundle;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;

import static com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_ALWAYS;
import static com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_WITH_TEXT;

public class Issue969 extends SherlockActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);
        tv.setText("TextAllCaps value is ignored on pre-ICS.");
        setContentView(tv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Test").setShowAsAction(SHOW_AS_ACTION_ALWAYS | SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }
}
