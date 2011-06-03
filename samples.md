---
title: Samples
layout: default
---


ActionBarSherlock comes with sample applications to help get you started with
using the library properly.

All of the samples are available to download as an `.apk` on the [project
downloads page][DL]. The source code for all of the samples are available in
the [repository][REPO] as folders prefixed with "`sample-`".


Feature Demo
------------

The feature demo sample is a simple activity that contains buttons which allow
you to manipulate the various settings of the action bar. This allows you to
easily see how various configurations will perform and behave on different
devices.

> [![Feature demo sample running on Android 2.3.3 in the default theme with a
home icon that also indicates a back action, list navigation, and three action
items][S_DEMO_01T]][S_DEMO_01]
> [![Feature demo sample running on Android 2.3.3 in the "light" theme with a
home icon and three tabs with only text.][S_DEMO_02T]][S_DEMO_02]


Shakespeare
-----------

The shakespeare sample is a simple application [posted on the Android
Developers Blog][SHAKE_POST] which gave an introduction to using fragments in
your application. The [code is a part][SHAKE_CODE] of the API demos included
in Android 3.0+.


Styled
------

The styled sample is an application [developed][STYLED_REPO] by Nick Butcher
and [posted on the Android Developers Blog][STYLED_POST] as an example of how to
use theming capabilities of Android to allow the action bar to match the style
of your application.

> [![Style sample running on Android 2.3.3 in landscape. Despite being the first
menu item, The search action is not shown because it does not have an icon
associated with it][S_STYLED_01T]][S_STYLED_01]


Honeycomb Gallery
-----------------

The Honeycomb gallery was [introduced with Android 3.0][GALLERY_CODE] to
demonstrate the use of several new APIs, two of which were fragments and the
action bar. The sample version of the application demonstrates how to use the
versions of of the fragment and action bar APIs alongside of newer APIs only
available on certain versions of Android.

**NOTE:** _This sample currently does NOT work on versions of Android prior to
3.0 and is being used as a measure of functionality for the library. With new
versions of the library will come support for this sample to function properly._

> [![Honecomb gallery demo in its default theme showing items in the overflow
menu][S_GALLERY_01T]][S_GALLERY_01]






 [REPO]: https://github.com/JakeWharton/ActionBarSherlock
 [DL]: https://github.com/JakeWharton/ActionBarSherlock/downloads
 [SHAKE_POST]: http://android-developers.blogspot.com/2011/02/android-30-fragments-api.html
 [SHAKE_CODE]: http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/app/FragmentLayout.html
 [STYLED_POST]: http://android-developers.blogspot.com/2011/04/customizing-action-bar.html
 [STYLED_REPO]: http://code.google.com/p/styled-action-bar/
 [GALLERY_CODE]: http://developer.android.com/resources/samples/HoneycombGallery/index.html
 [S_DEMO_01]: /static/sample_featuredemo_2.3.3_01.png
 [S_DEMO_01T]: /static/sample_featuredemo_2.3.3_01.thumb.png
 [S_DEMO_02]: /static/sample_featuredemo_2.3.3_02.png
 [S_DEMO_02T]: /static/sample_featuredemo_2.3.3_02.thumb.png
 [S_STYLED_01]: /static/sample_styled_2.3.3_01.png
 [S_STYLED_01T]: /static/sample_styled_2.3.3_01.thumb.png
 [S_GALLERY_01]: /static/sample_hcgallery_3.0_01.png
 [S_GALLERY_01T]: /static/sample_hcgallery_3.0_01.thumb.png
