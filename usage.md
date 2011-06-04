---
title: Usage
layout: default
---


ActionBarSherlock is able to replicate the functionality of Android 3.0+ on
older versions by providing custom implementations of common classes coupled
with some clever tricks of the Java language. The goal of the library is to
provide 100% functionality of an application written exclusively for Honeycomb
through the follow three simple modifications:

 * Replace the package name of a few classes in `android.app` and `android.view`
   with `android.support.v4.app` and `android.support.v4.view`, respectively.
 * Replace all calls to `getActionBar()` with `getSupportActionBar()`.
 * Modify any themes to inherit from `Theme.Sherlock` or `Theme.Sherlock.Light`
   and to not prefix any action bar-related attributes with the `android`
   namespace.

While there are certain caveats to heed in this conversion and additional tweaks
may be required, this will afford you proper functionality in almost all cases.


API
---

The `getSupportActionBar()` method will return an `ActionBar` instance that is
appropriate for the current executing platform. The API of the instance mirrors
that of the native action bar. You should interact with this exactly as you
would the native implementation, except using the classes in the
`android.support.v4.*` packages.

_More to come..._


See also:

 * [Theming](/theming.html)
 * [Samples](/samples.html)
 * [Frequently Asked Questions](/faq.html)
