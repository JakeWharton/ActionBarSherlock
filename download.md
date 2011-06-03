---
title: Download
layout: default
---


Zip and Tar archives are available for download on the [downloads page][1] of
GitHub (under the "Download Tags" section).

You can also download compiled versions all of the sample applications to test
on your phones and tablets to give you an idea of how the library works (under
the "Download Packages" section).


Including In Your Project
-------------------------

There are two ways to leverage ActionBarSherlock in your projects:

 1. If you're using the [Eclipse Development Environment][2] with the [ADT
    plugin][3] version 0.9.7 or greater you can include this as a library
    project. Create a new Android project in Eclipse using the `library/` folder
    as the existing source. Then, in your project properties, add the created
    project under the 'Libraries' section of the 'Android' category.
 2. If you use maven to build you Android project, you can simply add a
    dependency for this project.
    
        <dependency>
          <groupId>com.actionbarsherlock</groupId>
          <artifactId>library</artifactId>
          <version>3.0.0</version>
          <type>apklib</type>
        </dependency>
    
    *Make sure you also include [r.jakewharton.com/maven/][4] in the repositories
    section of your `pom.xml`.*




 [1]: https://github.com/JakeWharton/ActionBarSherlock/downloads
 [2]: http://www.eclipse.org
 [3]: http://developer.android.com/sdk/eclipse-adt.html
 [4]: http://r.jakewharton.com/maven/
