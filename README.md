Action Bar Sherlock
===================

The `ActionBarSherlock` class is a helper designed to facilitate the use of
the action bar design pattern across all version of Android leveraging the best
APIs available.

The class will automatically use the [native ActionBar][1] implementation on
Android 3.0 or later. For previous versions which do not include ActionBar, a
[GreenDroid][2] GDActionBar will automatically be wrapped around the layout.

All interaction with these action bars is handled through two static classes
which should be implemented in each Activity as inner-classes. The two classes
should extend `HoneycombActionBarHandler` and `PreHoneycombActionBarHandler`.
Each will allow for overriding various methods to handle the creation of and
interaction with each type of action bar.

`ActionBarSherlock` is also 100% compatible with the [Fragments][3] API, both
natively and using the [compatability package][4].



Example
=======
The following example will set up a simple `Activity` that contains an action
bar and show a toast upon its creation.

From within each handler, you can call `getActivity()` for the parent activity
instance and `getActionBar()` for the action bar instance. While this example
only shows a toast you should perform the action bar setup exactly as you would
in the `onCreate` method of an `Activity`.


    public class HelloActionBarActivity extends Activity {
        &#064;Override
        public void onCreate(Bundle savedInstanceState) {
            ActionBarSherlock.newInstance()
                    .setActivity(this, savedInstanceState)
                    .setLayout(R.layout.activity_hello)
                    .setTitle("Hello, ActionBar!")
                    .setHoneycombHandler(HelloHoneycombActionBarHandler.class)
                    .setPreHoneycombHandler(HelloPreHoneycombActionBarHandler.class);
        }
        
        public static final class HelloHoneycombActionBarHandler
                extends ActionBarSherlock.HoneycombActionBarHandler {
            &#064;Override
            public void onCreate(Bundle savedInstanceState) {
                Toast.makeText(
                    this.getActivity(),
                    "Hello, Honeycomb ActionBar!"
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
        public static final class HelloPreHoneycombActionBarHandler
                extends ActionBarSherlock.PreHoneycombActionBarHandler {
            &#064;Override
            public void onCreate(Bundle savedInstanceState) {
                Toast.makeText(
                    this.getActivity(),
                    "Hello, Pre-Honeycomb ActionBar!"
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
    }



Developed By
============
* Jake Wharton - <jakewharton@gmail.com>

Git repository located at [github.com/JakeWharton/ActionBarSherlock][5].



License
=======

    Copyright 2011 Jake Wharton

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.





 [1]: http://developer.android.com/guide/topics/ui/actionbar.html
 [2]: https://github.com/cyrilmottier/GreenDroid/
 [3]: http://developer.android.com/guide/topics/fundamentals/fragments.html
 [4]: http://android-developers.blogspot.com/2011/03/fragments-for-all.html
 [5]: https://github.com/JakeWharton/ActionBarSherlock/
