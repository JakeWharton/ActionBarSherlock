Change Log
===============================================================================

Version 3.1.0 *(In Development)*
--------------------------------

Due to various shortcomings of the method in which Android provides themes and
styles, creating a unified theme that will work on all API levels requires that
the custom attributes be specified in the root of said theme.

Much like the action bar-related attributes already specified in the theme,
style attributes for the action bar and action mode are now included prefixed
with `ab` and `am`, respectively, with the first letter of their attribute
capitalized.

Despite the bump in the minor version increment, the new style method should not
necessitate any changes if you were not using explicit styles for
`actionBarStyle` or `actionModeStyle`. If you were using either or both of these
styles, the previous implementation was broken and likely would not work on all
platforms. Move these defined attributes into the root of the theme and alter
their names in accordance to the rules above.

Further details are available on http://actionbarsherlock.com.


* Buttons for displaying the determinate and indeterminate progress bars have
  been added to the feature demo.
* Added support for indeterminate progress bar. Due to the `final` modifier on
  the native type, you must use `setIndeterminateProgressBarVisibility(Boolean)`
  and pass `Boolean.TRUE` or `Boolean.FALSE`.


Version 3.0.2 *(2010-06-23)*
----------------------------

* Sub-menus for action items are now shown in a list dialog.
* Moved certain classes to the `com.actionbarsherlock.internal` package which
  were not meant for public consumption. Despite being given `public` scope in
  this new package, these classes should **NOT** be used under any circumstances
  as their API can be considered highly volatile and is subject to change often
  and without warning.


Version 3.0.1 *(2010-06-08)*
----------------------------

* Fix: `onOptionsItemSelected()` not being called in fragments if the activity
  version returns `false`.
* Fix: `onCreateOptionsMenu()` not being called in fragments on Android 3.0+.
* New: Enable action item text display on pre-Android 3.0 by calling
  `requestWindowFeature` with `Window.FEATURE_ENABLE_ACTION_BAR_WATSON_TEXT`.
* Fix: `setCustomView()` no longer automatically enables the custom view on
  pre-3.0. You must call `setDisplayShowCustomEnabled()` in order to display
  the view.


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
   as `com.jakewharton:android-actionbarsherlock:2.1.0`.


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
