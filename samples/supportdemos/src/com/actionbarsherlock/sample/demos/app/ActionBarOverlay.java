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

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Window;
import android.widget.TextView;
import com.actionbarsherlock.sample.demos.Shakespeare;
import com.actionbarsherlock.sample.demos.R;

public class ActionBarOverlay extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actionbar_overlay);
        
        //Load partially transparent black background
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_bg_black));

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
        	for (String dialog : Shakespeare.DIALOGUE) {
        		builder.append(dialog).append("\n\n");
        	}
        }
        
        TextView bunchOfText = (TextView)findViewById(R.id.bunch_of_text);
        bunchOfText.setText(builder.toString());
    }
}
