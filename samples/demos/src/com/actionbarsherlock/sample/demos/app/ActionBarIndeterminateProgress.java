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
import android.view.View;
import com.actionbarsherlock.sample.demos.R;

public class ActionBarIndeterminateProgress extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //This has to be called before setContentView and you must use the
        //class in android.support.v4.view and NOT android.view
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        setContentView(R.layout.actionbar_iprogress);
        setProgressBarIndeterminateVisibility(Boolean.FALSE);
        
        
        //Bind to the buttons which enable and disable the progress spinner.
        //Notice how we *MUST* pass TRUE/FALSE objects rather than the native
        //true/false values.
        findViewById(R.id.enable).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setProgressBarIndeterminateVisibility(Boolean.TRUE);
			}
		});
        findViewById(R.id.disable).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setProgressBarIndeterminateVisibility(Boolean.FALSE);
			}
		});
    }
}
