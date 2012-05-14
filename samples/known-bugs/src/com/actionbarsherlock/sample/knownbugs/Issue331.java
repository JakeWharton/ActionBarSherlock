package com.actionbarsherlock.sample.knownbugs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import static android.view.View.OnClickListener;

public class Issue331 extends SherlockActivity {
    boolean mShow = true;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button b = new Button(this);
        b.setText("Click action item and then this button twice on pre-ICS");
        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mShow = !mShow;
                invalidateOptionsMenu();
            }
        });
        setContentView(b);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (mShow) {
            menu.add("Refresh").setIcon(R.drawable.ic_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        ImageView iv = (ImageView) LayoutInflater.from(this).inflate(R.layout.issue331_action_view, null);
        Animation r = AnimationUtils.loadAnimation(this, R.anim.issue331_refresh);
        r.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(r);
        item.setActionView(iv);
        return true;
    }
}
