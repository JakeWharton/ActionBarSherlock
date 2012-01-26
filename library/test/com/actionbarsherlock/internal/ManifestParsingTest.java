package com.actionbarsherlock.internal;

import static com.actionbarsherlock.internal.ActionBarSherlockCompat.cleanActivityName;
import junit.framework.TestCase;

public class ManifestParsingTest extends TestCase {
    public void testFullyQualifiedClassName() {
        String expected = "com.other.package.SomeClass";
        String actual = cleanActivityName("com.jakewharton.test", "com.other.package.SomeClass");
        assertEquals(expected, actual);
    }

    public void testFullyQualifiedClassNameSamePackage() {
        String expected = "com.jakewharton.test.SomeClass";
        String actual = cleanActivityName("com.jakewharton.test", "com.jakewharton.test.SomeClass");
        assertEquals(expected, actual);
    }

    public void testUnqualifiedClassName() {
        String expected = "com.jakewharton.test.SomeClass";
        String actual = cleanActivityName("com.jakewharton.test", "SomeClass");
        assertEquals(expected, actual);
    }

    public void testRelativeClassName() {
        String expected = "com.jakewharton.test.ui.SomeClass";
        String actual = cleanActivityName("com.jakewharton.test", ".ui.SomeClass");
        assertEquals(expected, actual);
    }
}