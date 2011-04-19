Action Bar Sherlock
===================

The `ActionBarSherlock` class is a helper designed to facilitate the use of
the action bar design pattern across all versions of Android leveraging the
best APIs available.

The class will automatically use the [native ActionBar][1] implementation on
Android 3.0 or later. For previous versions which do not include ActionBar, a
custom action bar implementation can automatically be wrapped around the
layout. Support for this goes all the way back to Android 1.5.

All interaction with these action bars is handled through the optional
extension of static classes which should be implemented in each Activity as
inner-classes.

Examples of third-party implementations can be found in the `samples/` folder
of this repository.

`ActionBarSherlock` is also 100% compatible with the [Fragments][2] API using
the [compatability package][3].

![Example Image][4]



Usage
=====

There are three ways to leverage `ActionBarSherlock` in your projects:

 1. Download the JAR file from the [downloads page][5] and add it to your
    project's build path (commonly the `libs/` directory).
 2. If you're using the [Eclipse Development Environment][6] with the [ADT
    plugin][7] version 0.9.7 or greater you can include this as a library
    project. Create a new Android project in Eclipse using the `library/` folder
    as the existing source. Then, in your project properties, add the created
    project under the 'Libraries' section of the 'Android' category.
 3. If you use maven to build you Android project, you can simply add a
    dependency for this project.
    
        <dependency>
          <groupId>com.jakewharton</groupId>
          <artifactId>android-actionbarsherlock</artifactId>
          <version>2.1.1</version>
        </dependency>
    
    *Make sure you also include [repository.jakewharton.com][8] in the
    repositories section of your `pom.xml`.*



Documentation
=============

There are a variety of ways to get started with `ActionBarSherlock`.


Examples
--------

Two sample applications are included which demonstrate implementing custom
action bar handlers for [GreenDroid][9]* and [Android-ActionBar][10]. These are
located in the `samples/` folder of this repository.

There is also a port of the Shakespeare fragments demo from the Android 3.0 API
demos which has been converted to use this library for more of a real-world
implementation. It depends on the Android-ActionBar library as well.

In order for these sample applications to properly work, the respective library
that they depend on needs to be downloaded. This can be done automatically by
executing `python dependencies.py`. This script requires that you have both Git
and Apache Maven installed.

You can also download pre-built .apks for each of these samples from the
[project download page][11] for demonstration.

Due the use of third-party libraries, the samples have a minimum requirement of
Android 1.6.

_* NOTE: The GreenDroid implementation currently requires a fork of the official
repository and NOT the official repository itself. This is because the current
official GreenDroid library has API conflicts with Android 3.0. Until the changes
implemented in the fork are merged upstream, this is the only way to achieve
the GreenDroid implementation._


Other Resources
---------------

Questions, comments, and suggestions should be posted on the [Google Group][12]
for Jake Wharton's projects.

 * Git repository located at [github.com/JakeWharton/ActionBarSherlock][13].
 * Javadoc site located at [jakewharton.github.com/ActionBarSherlock][14].



Developed By
============

* Jake Wharton - <jakewharton@gmail.com>


Contributors
------------

 * [smith324](http://stackoverflow.com/users/413575/smith324) via StackOverflow



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
 [3]: http://android-developers.blogspot.com/2011/03/fragments-for-all.html
 [4]: http://img.jakewharton.com/ActionBarSherlock01.png
 [5]: https://github.com/JakeWharton/ActionBarSherlock/downloads
 [6]: http://www.eclipse.org
 [7]: http://developer.android.com/sdk/eclipse-adt.html
 [8]: http://repository.jakewharton.com
 [9]: https://github.com/hameno/GreenDroid
 [10]: https://github.com/johannilsson/android-actionbar
 [11]: https://github.com/JakeWharton/ActionBarSherlock/downloads
 [12]: https://groups.google.com/forum/#!forum/jakewharton-projects
 [13]: https://github.com/JakeWharton/ActionBarSherlock/
 [14]: http://jakewharton.github.com/ActionBarSherlock/
