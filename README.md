Action Bar Sherlock
===================

`ActionBarSherlock` is an extension of the [compatibility library][4]
designed to facilitate the use of the action bar design pattern across all
versions of Android leveraging the best API available.

The class will automatically use the [native ActionBar][1] implementation on
Android 3.0 or later. For previous versions which do not include ActionBar, a
custom action bar implementation will automatically be wrapped around the
layout. Support for this goes all the way back to Android 1.6.

All interaction with these action bars is handled using `getSupportActionBar()`
with the normal native API.

Examples of implementations can be found in `samples-*` folders of this
repository.

This library also includes the support for [fragments][2] and [loaders][3].

![Example Image][5]


Usage
=====

There are two ways to leverage `ActionBarSherlock` in your projects:

 1. If you're using the [Eclipse Development Environment][6] with the [ADT
    plugin][7] version 0.9.7 or greater you can include this as a library
    project. Create a new Android project in Eclipse using the `library/` folder
    as the existing source. Then, in your project properties, add the created
    project under the 'Libraries' section of the 'Android' category.
 2. If you use maven to build you Android project, you can simply add a
    dependency for this project.
    
        <dependency>
          <groupId>com.actionbarsherlock</groupId>
          <artifactId>library</artifactId>
          <version>3.0.0</version>
        </dependency>
    
    *Make sure you also include [r.jakewharton.com/maven/][8] in the repositories
    section of your `pom.xml`.*



Documentation
=============

There are a variety of samples to get you started with `ActionBarSherlock`.


Samples
--------

 * `sample-featuredemo/` - Feature showcase with buttons to control every
   setting of the action bar to demonstrate how it behaves on each platform.
 * `sample-hcgallery/` - Honeycomb gallery sample from Android 3.0.
 * `sample-shakespeare/` - A port of the Shakespeare fragments demo from the API
   demos which has been converted to use this library.
 * `sample-styled/` - Styled action bar sample from the Android Developer Blog.

You can also download pre-built .apks for each of these samples from the
[project download page][9] for demonstration.


Other Resources
---------------

Questions, comments, and suggestions should be posted on the [Google Group][10]
for Jake Wharton's projects.

 * Git repository located at [github.com/JakeWharton/ActionBarSherlock][11].
 * Javadoc site located at [jakewharton.github.com/ActionBarSherlock][12].



Developed By
============

* Jake Wharton - <jakewharton@gmail.com>


Inclusions
----------

This library contains a rewritten version of the [Android-ActionBar][13] library
by Johan Nilsson. A lot of the changes will be merged back into the library to
allow for the use of new functionality in his standalone package.

The [Android Compatability Package][14] is also included with very minor
additions to allow for the tight integration with fragments.

Both of the aforementioned libraries are also licensed under the Apache License
(version 2.0) and are copyright their respective owners. Every effort has been
made to retain their copyright noticies in the appropriate places.


License
=======

    Copyright 2011 Jake Wharton

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.





 [1]: http://developer.android.com/guide/topics/ui/actionbar.html
 [2]: http://developer.android.com/guide/topics/fundamentals/fragments.html
 [3]: http://developer.android.com/guide/topics/fundamentals/loaders.html
 [4]: http://android-developers.blogspot.com/2011/03/fragments-for-all.html
 [5]: http://img.jakewharton.com/ActionBarSherlock01.png
 [6]: http://www.eclipse.org
 [7]: http://developer.android.com/sdk/eclipse-adt.html
 [8]: http://r.jakewharton.com/maven/
 [9]: https://github.com/JakeWharton/ActionBarSherlock/downloads
 [10]: https://groups.google.com/forum/#!forum/jakewharton-projects
 [11]: https://github.com/JakeWharton/ActionBarSherlock/
 [12]: http://jakewharton.github.com/ActionBarSherlock/
