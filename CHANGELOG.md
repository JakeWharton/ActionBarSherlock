Change Log
===============================================================================

Version 2.1.0 *(In Development)*
--------------------------------

**WARNING**: The [Android Compatibility Library (v4)][1] is now required.


 * Added `ActionBarSherlock.Activity`, `ActionBarSherlock.ListActivity`,
   and `ActionBarSherlock.FragmentActivity` for extension by implementing
   activities, the latter of which is deprecated. This affords a much tighter
   integration and allows for the use of other new features listed below.
 * New API method: `menu(int)` allows for the inflation of menu XMLs from a
   resource. For the non-native implementation, the XML is inflated to a custom
   Menu which can then be applied appropriately to the third-party action bar.
   Sub-menus are also supported. *This feature requires that activities extend
   from `ActionBarSherlock.Activity`*.
 * New API method: `homeAsUp(boolean)`. This mimics the native method
   `setDisplayHomeAsUpEnalbed` which will be called for the native action bar
   and sent as a callback to third-party handler implementations.
 * New API method: `useLogo(boolean)` will trigger the action bar to hide the
   application icon/home button and title and show a larger logo representing
   the application. Third-party action bar handlers should implement
   `ActionBarSherlock.LogoHandler` for this functionality.
 * New API method: `layout(Fragment)` will use the fragment argument as the
   content to the activity. *This feature requires that your activity extends
   from `ActionBarSherlock.FragmentActivity`*.
 * New API method: `dropDown(SpinnerAdapter, OnNavigationListener)`. Tells the
   action bar to use drop-down style navigation with the specified list of
   items and callback listener.


Version 2.0.1 *(2011-03-11)*
----------------------------

 * Use `Class.forName()` for detection of native action bar. This provides
   compatability all the way back to Android 1.5.


Version 2.0.0 *(2011-03-09)*
----------------------------
Complete rewrite!

 * New and better API.
 * More sane logic and attachment to activity.
 * Extensible via generics. Implement any ActionBar or roll your own with
   minimal effort.
 * Now a library project for easy inclusion in applications.


Version 1.0.0 *(2011-03-07)*
----------------------------
Initial release.





 [1]: http://android-developers.blogspot.com/2011/03/fragments-for-all.html
