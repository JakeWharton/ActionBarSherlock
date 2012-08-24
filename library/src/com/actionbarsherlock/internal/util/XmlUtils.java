/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.actionbarsherlock.internal.util;

public class XmlUtils {
    public static final int
    convertValueToList(CharSequence value, String[] options, int defaultValue) {
        if (null != value) {
            for (int i = 0; i < options.length; i++) {
                if (value.equals(options[i]))
                    return i;
            }
        }

        return defaultValue;
    }

    public static final boolean
    convertValueToBoolean(CharSequence value, boolean defaultValue) {
        boolean result = false;

        if (null == value)
            return defaultValue;

        if (value.equals("1")
                || value.equals("true")
                || value.equals("TRUE"))
            result = true;

        return result;
    }

    public static final int
    convertValueToInt(CharSequence charSeq, int defaultValue) {
        if (null == charSeq)
            return defaultValue;

        String nm = charSeq.toString();

        // XXX This code is copied from Integer.decode() so we don't
        // have to instantiate an Integer!

        int value;
        int sign = 1;
        int index = 0;
        int len = nm.length();
        int base = 10;

        if ('-' == nm.charAt(0)) {
            sign = -1;
            index++;
        }

        if ('0' == nm.charAt(index)) {
            //  Quick check for a zero by itself
            if (index == (len - 1))
                return 0;

            char c = nm.charAt(index + 1);

            if ('x' == c || 'X' == c) {
                index += 2;
                base = 16;
            } else {
                index++;
                base = 8;
            }
        } else if ('#' == nm.charAt(index)) {
            index++;
            base = 16;
        }

        return Integer.parseInt(nm.substring(index), base) * sign;
    }

    public static final int
    convertValueToUnsignedInt(String value, int defaultValue) {
        if (null == value)
            return defaultValue;

        return parseUnsignedIntAttribute(value);
    }

    public static final int
    parseUnsignedIntAttribute(CharSequence charSeq) {
        String value = charSeq.toString();

        long bits;
        int index = 0;
        int len = value.length();
        int base = 10;

        if ('0' == value.charAt(index)) {
            //  Quick check for zero by itself
            if (index == (len - 1))
                return 0;

            char c = value.charAt(index + 1);

            if ('x' == c || 'X' == c) {     //  check for hex
                index += 2;
                base = 16;
            } else {                        //  check for octal
                index++;
                base = 8;
            }
        } else if ('#' == value.charAt(index)) {
            index++;
            base = 16;
        }

        return (int) Long.parseLong(value.substring(index), base);
    }
}
