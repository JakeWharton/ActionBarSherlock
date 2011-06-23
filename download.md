---
title: Download
layout: default
---


Zip and Tar archives are available for download on the [downloads page][1] of
GitHub (under the "Download Tags" section).

You can also download compiled versions all of the sample applications to test
on your phones and tablets to give you an idea of how the library works (under
the "Download Packages" section).

The [changelog is available][2] with a detailed listing of what has changed
between versions.


Including In Your Project
-------------------------

There are two ways to leverage ActionBarSherlock in your projects:

 1. If you're using the [Eclipse Development Environment][3] with the [ADT
    plugin][4] version 0.9.7 or greater you can include this as a library
    project. Create a new Android project in Eclipse using the `library/` folder
    as the existing source. Then, in your project properties, add the created
    project under the 'Libraries' section of the 'Android' category.
 2. If you use maven to build your Android project you can simply add a
    dependency for this library.
    
        <dependency>
          <groupId>com.actionbarsherlock</groupId>
          <artifactId>library</artifactId>
          <version>3.0.2</version>
          <type>apklib</type>
        </dependency>
    
    *Make sure you also include [r.jakewharton.com/maven/][5] in the repositories
    section of your `pom.xml`.*

**Note**: If you were previously using the Android compatability library you
must remove its `.jar`. ActionBarSherlock is built on top of the compatability
library and comes bundled with its classes.




 [1]: https://github.com/JakeWharton/ActionBarSherlock/downloads
 [2]: https://github.com/JakeWharton/ActionBarSherlock/blob/master/CHANGELOG.md#readme
 [3]: http://www.eclipse.org
 [4]: http://developer.android.com/sdk/eclipse-adt.html
 [5]: http://r.jakewharton.com/maven/
