Change Log
===============================================================================

Version 3.0.1 *(In Development)*
--------------------------------

* Fix: `onOptionsItemSelected()` not being called in fragments if the activity
  version returns `false`.
* Fix: `onCreateOptionsMenu()` not being called in fragments on Android 3.0+.


Version 3.0.0 *(2010-06-05)*
----------------------------

The API has been rewritten to mimic that of the native action bar. As a result,
usage now only requires changing a few imports to use the support versions
of classes and calling `getSupportActionBar()`. See the README for more info.

The rewrite necessitated tight interaction with the
[compatibility library](http://android-developers.blogspot.com/2011/03/fragments-for-all.html)
to the point where its sources are now included. You are no longer required to
have the standalone `.jar` file.

Also included is a default custom action bar for use by default on pre-3.0
devices. This custom implementation is based off of Johan Nilsson's
[Android-ActionBar](https://github.com/johannilsson/android-actionbar) and the
[work that I have done](https://github.com/johannilsson/android-actionbar/pull/25)
on it.

More details are available at http://actionbarsherlock.com


Version 2.1.1 *(2011-03-21)*
----------------------------

**No changes to library code.**

 * Moved library to the root of the repository.
 * Added `samples/dependencies.py` script to automatically download the needed
   dependencies for the sample projects.


Version 2.1.0 *(2011-03-21)*
----------------------------

**WARNING**: The
[Android Compatibility Library (v4)](http://android-developers.blogspot.com/2011/03/fragments-for-all.html)
is now required.

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
   should implement `ActionBarSherlock.HasMenu` for this functionality. *This
   feature requires that activities extend from one of the provided activity
   base classes.*
 * New API method: `homeAsUp(boolean)`. This mimics the native method
   `setDisplayHomeAsUpEnalbed` on the native action bar. Third-party action bar
   handlers should implement `ActionBarSherlock.HasHomeAsUp` for this
   functionality.
 * New API method: `useLogo(boolean)` will trigger the action bar to hide the
   application icon/home button and title and show a larger logo representing
   the application. Third-party action bar handlers should implement
   `ActionBarSherlock.HasLogo` for this functionality.
 * New API method: `listNavigation(SpinnerAdapter, OnNavigationListener)`. Tells
   the action bar to use drop-down style navigation with the specified list of
   items and callback listener. Third-party action bar handlers should
   implement `ActionBarSherlock.HasListNavigation` for this functionality.
 * Javadocs are now available at
   [jakewharton.github.com/ActionBarSherlock](http://jakewharton.github.com/ActionBarSherlock/).
 * A standalone JAR is now available via the
   [GitHub downloads page](https://github.com/JakeWharton/ActionBarSherlock/downloads)
   or in my
   [personal maven repository](http://r.jakewharton.com/maven/)
   as `com.actionbarsherlock:library:2.1.0`.


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
