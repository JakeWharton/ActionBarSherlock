---
title: Theming
layout: default
---


Theming of the action bar to work on both pre-3.0 and 3.0+ devices is a very
straightforward and simple process. You should follow the existing theming
recommendations for [customizing the native action bar][1] except with the
two modifications list below.

An example of a customized action bar can be seen in the "Styled" application
on the [samples page][2].


Parent Themes
-------------

In order for ActionBarSherlock to function on pre-3.0 devices your application
must use `Theme.Sherlock` or `Theme.Sherlock.Light`, or your custom theme must
use one of the aforementioned two as its parent. **This MUST be done or your
application will not run.**

These two themes serve as baseline configurations for the pre-3.0 action bar
as well as mapping the attributes to their native versions on 3.0+.

The themes should be defined in your manifest for the entire application or on
a per-activity basis. You may also define the theme in the code of each activity
*before* calling `super.onCreate(Bundle)`. This must be done for every activity
on which you extend from `FragmentActivity` and intend to use the action bar.
More information on how to specify a theme can be found in the [official
Android documentation][3].


Attributes
----------

There are 22 attributes which are used to style the various elements on the
action bars. All of these attributes are used in the form
`android:attributeName` when working only with the native action bar. To allow
use on both, drop the `android:` prefix and use only the attribute name.

The following attributes should be modified to remove the android namespace:

 * `actionBarSize`
 * `actionBarStyle`
 * `actionBarTabStyle`
 * `actionBarTabBarStyle`
 * `actionBarTabTextStyle`
 * `actionButtonStyle`
 * `actionDropDownStyle`
 * `actionMenuTextAppearance`
 * `actionMenuTextColor`
 * `actionModeBackground`
 * `actionModeCloseDrawable`
 * `actionModeCloseButtonStyle`
 * `actionModeCopyDrawable`
 * `actionModeCutDrawable`
 * `actionModePasteDrawable`
 * `actionModePopupWindowStyle`
 * `actionModeStyle`
 * `actionOverflowButtonStyle`
 * `actionSpinnerItemStyle`
 * `dropDownListViewStyle`
 * `popupMenuStyle`
 * `selectableItemBackground`

There are also additional attributes which apply only to the pre-3.0 action bar
to allow for specific customization that is not available on the native action
bar.

 * `actionHomeButtonStyle` - Style of the home button. This style should use
   `Widget.Sherlock.ActionButton.Home` as its parent.
 * `homeAsUpIndicator` - Drawable used to indicate "up" for the home action.






 [1]: http://android-developers.blogspot.com/2011/04/customizing-action-bar.html
 [2]: /samples.html
 [3]: http://developer.android.com/guide/topics/ui/themes.html#ApplyATheme
