Change Log
===============================================================================

Version 2.1.0 *(In Development)*
--------------------------------

 * Add `ActionBarSherlock.Activity` for extension by implementing activities.
   This affords a much tighter integration and allows for the use of other new
   features listed below.
 * New API method: `menu(int)` allows for the inflation of menu XMLs from a
   resource. For the non-native implementation, the XML is inflated to a custom
   Menu which can then be applied appropriately to the third-party action bar.
   Sub-menus are also supported. *This feature requires that activities extend
   from `ActionBarSherlock.Activity`*.
 * New API method: `homeAsUp(boolean)`. This mimics the native method
   `setDisplayHomeAsUpEnalbed` which will be called for the native action bar
   and sent as a callback to third-party handler implementations.


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
