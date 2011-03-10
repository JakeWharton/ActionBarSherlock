ActionBarSherlock
=================

This project is an Android library. You should include this project by doing
one of the following:

1.  Including a reference to this folder (`library/`) in your application's
    `default.properties` file.
    
    Example:
    
        android.library.reference.1=../ActionBarSherlock/library
    
2.  Including this library as a dependency of your project through eclipse.
    
    1. Right click your project and select 'Properties'.
    2. Choose the 'Android' category in the list.
    3. Click 'Add' in the 'Library' section of the window.
    4. Select the ActionBarSherlock project and click 'OK'.
    5. Press 'Apply' to finalize the library addition to your project.

3.  Copy the `ActionBarSherlock.java` file from the
    `src/com/jakewharton/android/actionbarsherlock` to your project's `src`
    directory.
    
    This is not recommended, however, as it decouples the file from the
    repository and simple updates. Performing one of the two actions above
    is equivalent to this operation but keeps the library as a wholly separate
    project which can be updated independently.