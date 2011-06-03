---
title: Development
layout: default
---


The ActionBarSherlock library along with its sample applications are all built
using Apache Maven. Maven will automate the process of compiling and assembling
the `apklib` and `apk`s for each project.


IDE
---

Even though the project uses Maven, each folder within the repository still
maintains the standard Android project layout. By specifying the desired folder
as an existing source for a new Android project, you should be able to
successfully import the sources.


Prerequisites
-------------

Maven compilation requires that you have installed the Android SDK into your
local maven repository. This can be done automatically by the [SDK Deployer][1].

Follow the instructions on setting up the SDK Deployer. Then, execute the
following command in the SDK Deployer folder:

    mvn install -P 3.1

This will install the Android SDK 3.1 into the local repository.


Compiling
---------

Once the prerequisites have been successfully installed, building is as easy
as running `mvn clean package`. The `apklib` for the library will be located
in the `library/target/` folder.

`apk`s for the samples are their respective `target/` folders.





 [1]: https://github.com/mosabua/maven-android-sdk-deployer
