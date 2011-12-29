/*
 * Copyright (C) 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.actionbarsherlock.sample.demos.app;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.sample.demos.R;

public class ActionBarActionItemCustomView extends FragmentActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(0, android.R.id.copy, 0, "Test");

        final int twentyDp = (int) (20 * getResources().getDisplayMetrics().density);

        TypedArray a = getTheme().obtainStyledAttributes(R.styleable.SherlockTheme);
        final int abHeight = a.getLayoutDimension(R.styleable.SherlockTheme_abHeight, LayoutParams.FILL_PARENT);
        a.recycle();

        LinearLayout l = new LinearLayout(this);
        l.setPadding(twentyDp, 0, twentyDp, 20);
        l.setBackgroundColor(0x55FF0000);

        TextView tv = new TextView(this);
        tv.setText("HI!!");
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, abHeight));
        l.addView(tv);

        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(
                        ActionBarActionItemCustomView.this,
                        "Got custom action item click!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        item.setActionView(l);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actionbar_text);
        ((TextView) findViewById(R.id.text)).setText(R.string.actionbar_actionitemcustom_content);
    }
}
