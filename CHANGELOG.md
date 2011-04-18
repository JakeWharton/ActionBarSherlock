Change Log
===============================================================================

Version 2.1.2 *(In Development)*
--------------------------------

 * The `attach()` method will now return the instance of the selected handler.
   This will allow for asynchronous interaction with the action bar rather than
   only through methods and callbacks in the appropriate handler.
   
   Snippet from the `attach()` JavaDoc regarding returned instance:
   
   > **Interacting with this instance directly should be considered highly
   > volatile and is not encouraged.** The recommended method of interaction is
   > to implement a custom interface with your interaction methods in both the
   > native and custom handlers and then cast this return value to your
   > interface.


Version 2.1.1 *(2011-03-21)*
----------------------------

**No changes to library code.**

 * Moved library to the root of the repository.
 * Added `samples/dependencies.py` script to automatically download the needed
   dependencies for the sample projects.


Version 2.1.0 *(2011-03-21)*
----------------------------

**WARNING**: The [Android Compatibility Library (v4)][1] is now required.

 * Added `ActionBarSherlock.Activity`, `ActionBarSherlock.FragmentActivity`,
   and `ActionBarSherlock.ListActivity` for extension by implementing
   activities, the latter of which is deprecated. This affords a much tighter
   integration and allows for the use of other new features listed below.
 * New API method: `layout(Fragment)` will use the fragment argument as the
   content to the activity.
 * New API method: `menu(int)` allows for the inflation of menu XMLs from a
   resource. For the non-native implementation, the XML can be inflated to a
   custom Menu which can then be applied appropriately to the third-party
   action bar. Sub-menus are also supported. Third-party action bar handlers
   should implement `ActionBarSherlock.MenuHandler` for this functionality.
   *This feature requires that activities extend from one of the provided
   activity base classes.*
 * New API method: `homeAsUp(boolean)`. This mimics the native method
   `setDisplayHomeAsUpEnalbed` on the native action bar. Third-party action bar
   handlers should implement `ActionBarSherlock.HomeAsUpHandler` for this
   functionality.
 * New API method: `useLogo(boolean)` will trigger the action bar to hide the
   application icon/home button and title and show a larger logo representing
   the application. Third-party action bar handlers should implement
   `ActionBarSherlock.LogoHandler` for this functionality.
 * New API method: `dropDown(SpinnerAdapter, OnNavigationListener)`. Tells the
   action bar to use drop-down style navigation with the specified list of
   items and callback listener. Third-party action bar handlers should
   implement `ActionBarSherlock.DropDownHandler` for this functionality.
 * Javadocs are now available at [jakewharton.github.com/ActionBarSherlock][2].
 * A standalone JAR is now available via the [GitHub downloads page][3] or in my
   [personal maven repository][4] as
   `com.jakewharton:android-actionbarsherlock:2.1.0`.


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
 [2]: http://jakewharton.github.com/ActionBarSherlock/
 [3]: https://github.com/JakeWharton/ActionBarSherlock/downloads
 [4]: http://repository.jakewharton.com
