ActionBarSherlock Plugin: Native Honeycomb
==========================================

This plugin will enable the use of the native action bar on Android 3.x rather
than using the custom implementation.

Some of the features of the ICS action bar will not be available if this plugin
is used (e.g., programmatically changing icon/logo).


Usage
-----

You need to register this plugin as an available implementation for use. The
best place to do this would be in a static block in a base activity that you
always extend from.

    public class BaseActivity extends SherlockActivity {
        static {
            ActionBarSherlock.registerImplementation(ActionBarSherlockNativeHoneycomb.class);
        }
    }

You could also do this in `Application.onCreate` or even in before the call to
`super.onCreate` in every activity if for some reason a base activity is not
appropriate to use.

Since the native action bar is disabled by default on Android 3.x you will need
to create your own theme that enables it for that API level.

  * `values/`
    
        <style name="MyTheme" parent="Theme.Sherlock"></style>

  * `values-v11/`
    
        <style name="MyTheme" parent="Theme.Sherlock">
            <item name="android:windowNoTitle">false</item>
            <item name="android:windowActionBar">true</item>
        </style>

In the manifest make sure you specify `android:theme="@style/MyTheme"` for your
activities.
