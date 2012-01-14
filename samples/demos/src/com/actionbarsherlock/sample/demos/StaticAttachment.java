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
package com.actionbarsherlock.sample.demos;

import com.actionbarsherlock.ActionBarSherlock;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class StaticAttachment extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Most interactions with what would otherwise be the system UI should
         * now be done through this instance. Content, title, action bar, and
         * menu inflation can all be done.
         *
         * All of the base activities use this class to provide the normal
         * action bar functionality so everything that they can do is possible
         * using this static attachment method.
         */
        ActionBarSherlock abs = ActionBarSherlock.wrap(this);

        abs.setContentView(R.layout.text);
        ((TextView)findViewById(R.id.text)).setText(R.string.actionbar_static_content);
    }
}
