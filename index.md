---
title: Home
layout: default
---


ActionBarSherlock is an extension of the [compatibility library][1] designed
to facilitate the use of the action bar design pattern across all versions of
Android leveraging the best API available.

The library will automatically use the [native ActionBar][2] implementation on
Android 3.0 or later. For previous versions which do not include an ActionBar,
a custom action bar implementation will automatically be wrapped around the
layout. Support for this goes all the way back to Android 1.6.

All interaction with the action bar is handled by calling the method
`getSupportActionBar()` from within your activity.

This library also includes the support for [fragments][3] and [loaders][4].

![Example Image][5]





 [1]: http://developer.android.com/guide/topics/fundamentals/fragments.html
 [2]: http://developer.android.com/guide/topics/fundamentals/loaders.html
 [3]: http://android-developers.blogspot.com/2011/03/fragments-for-all.html
 [4]: http://developer.android.com/guide/topics/ui/actionbar.html
 [5]: /static/feature.png
