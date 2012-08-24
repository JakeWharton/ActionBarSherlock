/*
 * Copyright (C) 2012 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.actionbarsherlock.internal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import com.actionbarsherlock.internal.util.XmlUtils;

/**
 * This class imitates the {@link TypedArray} class and is used in conjunction
 * with the {@link ContextCompat} class to emulate attribute styling.
 * 
 */
public class TypedArrayCompat {
	/** Logging tag. */
	private static final String TAG = "TypedArrayCompat";

	/** Aggregated {@link TypedArray} used to get values of attributes */
	private TypedArray mTypedArray;
	/** A {@link Resources} object used to get resources by ids. */
	private Resources mResources;
	/** {@link Theme} used to resolve attributes. */
	private Theme mTheme;
	/** A temporary {@link TypedValue} returned by the {@link #peekValue(int)} method. */
	private TypedValue mValue;

    /* package */ TypedArrayCompat(Context context, TypedArray typedArray) {
        mTypedArray = typedArray;
        mResources = typedArray.getResources();
        mTheme = context.getTheme();
        mValue = new TypedValue();
    }

	/**
	 * Return the number of values in this array.
	 */
	public int length() {
		return mTypedArray.length();
	}

	/**
	 * Return the number of indices in the array that actually have data.
	 */
	public int getIndexCount() {
		return mTypedArray.getIndexCount();
	}

	/**
	 * Return an index in the array that has data.
	 *
	 * @param at The index you would like to returned, ranging from 0 to
	 * {@link #getIndexCount()}.
	 *
	 * @return The index at the given offset, which can be used with
	 * {@link #getValue} and related APIs.
	 */
	public int getIndex(int at) {
		return mTypedArray.getIndex(at);
	}

	/**
	 * Return the Resources object this array was loaded from.
	 */
	public Resources getResources() {
		return mResources;
	}

	/**
	 * Retrieve the styled string value for the attribute at <var>index</var>.
	 *
	 * @param index Index of attribute to retrieve.
	 *
	 * @return CharSequence holding string data.  May be styled.  Returns
	 *         null if the attribute is not defined.
	 */
	public CharSequence getText(int index) {
        TypedValue value = mValue;
        int type = TypedValue.TYPE_NULL;
        if (getValueAt(index, value)) {
            type = value.type;
        }

        if (type == TypedValue.TYPE_NULL) {
            return null;
        } else if (type == TypedValue.TYPE_STRING) {
            return value.string;
        }

        Log.w(TAG, "getText of bad type: 0x"
                + Integer.toHexString(type));
        Log.w(TAG, "Converting to string: " + value);
        return value.coerceToString();
	}

	/**
	 * Retrieve the string value for the attribute at <var>index</var>.
	 *
	 * @param index Index of attribute to retrieve.
	 *
	 * @return String holding string data.  Any styling information is
	 * removed.  Returns null if the attribute is not defined.
	 */
	public String getString(int index) {
        TypedValue value = mValue;
        int type = TypedValue.TYPE_NULL;
        if (getValueAt(index, value)) {
            type = value.type;
        }

		if (type == TypedValue.TYPE_NULL) {
			return null;
		} else if (type == TypedValue.TYPE_STRING) {
			return value.string.toString();
		}

        Log.w(TAG, "getString of bad type: 0x"
                + Integer.toHexString(type));
        Log.w(TAG, "Converting to string: " + value);
        CharSequence cs = value.coerceToString();
        return cs != null ? cs.toString() : null;
	}

	/**
	 * Retrieve the string value for the attribute at <var>index</var>, but
	 * only if that string comes from an immediate value in an XML file.  That
	 * is, this does not allow references to string resources, string
	 * attributes, or conversions from other types.  As such, this method
	 * will only return strings for TypedArray objects that come from
	 * attributes in an XML file.
	 *
	 * @param index Index of attribute to retrieve.
	 *
	 * @return String holding string data.  Any styling information is
	 * removed.  Returns null if the attribute is not defined or is not
	 * an immediate string value.
	 */
	public String getNonResourceString(int index) {
        return mTypedArray.getNonResourceString(index);
	}

	/**
	 * Retrieve the boolean value for the attribute at <var>index</var>.
	 *
	 * @param index Index of attribute to retrieve.
	 * @param defValue Value to return if the attribute is not defined.
	 *
	 * @return Attribute boolean value, or defValue if not defined.
	 */
	public boolean getBoolean(int index, boolean defValue) {
        TypedValue value = mValue;
        int type = TypedValue.TYPE_NULL;
        if (getValueAt(index, value)) {
            type = value.type;
        }

        if (type == TypedValue.TYPE_NULL) {
			return defValue;
		} else if (type >= TypedValue.TYPE_FIRST_INT
				&& type <= TypedValue.TYPE_LAST_INT) {
			return value.data != 0;
		}

        Log.w(TAG, "getBoolean of bad type: 0x"
                + Integer.toHexString(type));
        Log.w(TAG, "Converting to boolean: " + value);
        return XmlUtils.convertValueToBoolean(
                value.coerceToString(), defValue);
	}

	/**
	 * Retrieve the integer value for the attribute at <var>index</var>.
	 *
	 * @param index Index of attribute to retrieve.
	 * @param defValue Value to return if the attribute is not defined.
	 *
	 * @return Attribute int value, or defValue if not defined.
	 */
	public int getInt(int index, int defValue) {
        TypedValue value = mValue;
        int type = TypedValue.TYPE_NULL;
        if (getValueAt(index, value)) {
            type = value.type;
        }

		if (type == TypedValue.TYPE_NULL) {
			return defValue;
		} else if (type >= TypedValue.TYPE_FIRST_INT
				&& type <= TypedValue.TYPE_LAST_INT) {
			return value.data;
		}

        Log.w(TAG, "getInt of bad type: 0x"
                + Integer.toHexString(type));
        Log.w(TAG, "Converting to int: " + value);
        return XmlUtils.convertValueToInt(
                value.coerceToString(), defValue);
	}

	/**
	 * Retrieve the float value for the attribute at <var>index</var>.
	 *
	 * @param index Index of attribute to retrieve.
	 *
	 * @return Attribute float value, or defValue if not defined..
	 */
	public float getFloat(int index, float defValue) {
		TypedValue value = mValue;
		int type = TypedValue.TYPE_NULL;
		if (getValueAt(index, value)) {
			type = value.type;
		}

		if (type == TypedValue.TYPE_NULL) {
			return defValue;
		} else if (type == TypedValue.TYPE_FLOAT) {
			return Float.intBitsToFloat(value.data);
		} else if (type >= TypedValue.TYPE_FIRST_INT
				&& type <= TypedValue.TYPE_LAST_INT) {
			return value.data;
		} else {
			Log.w(TAG, "Converting to float: " + value);
			CharSequence str = value.coerceToString();
			if (str != null) {
				return Float.parseFloat(str.toString());
			}
		}
		Log.w(TAG, "getFloat of bad type: 0x"
				+ Integer.toHexString(type));
		return defValue;
	}

	/**
	 * Retrieve the color value for the attribute at <var>index</var>.  If
	 * the attribute references a color resource holding a complex
	 * {@link android.content.res.ColorStateList}, then the default color from
	 * the set is returned.
	 *
	 * @param index Index of attribute to retrieve.
	 * @param defValue Value to return if the attribute is not defined or
	 *                 not a resource.
	 *
	 * @return Attribute color value, or defValue if not defined.
	 */
	public int getColor(int index, int defValue) {
		TypedValue value = mValue;
		int type = TypedValue.TYPE_NULL;
		if (getValueAt(index, value)) {
			type = value.type;
		}

		if (type == TypedValue.TYPE_NULL) {
			return defValue;
		} else if (type >= TypedValue.TYPE_FIRST_INT
				&& type <= TypedValue.TYPE_LAST_INT) {
			return value.data;
		} else if (type == TypedValue.TYPE_STRING) {
			ColorStateList csl = mResources.getColorStateList(value.resourceId);
			return csl.getDefaultColor();
		}

		throw new UnsupportedOperationException("Can't convert to color: type=0x"
				+ Integer.toHexString(type));
	}

	/**
	 * Retrieve the ColorStateList for the attribute at <var>index</var>.
	 * The value may be either a single solid color or a reference to
	 * a color or complex {@link android.content.res.ColorStateList} description.
	 *
	 * @param index Index of attribute to retrieve.
	 *
	 * @return ColorStateList for the attribute, or null if not defined.
	 */
	public ColorStateList getColorStateList(int index) {
		final TypedValue value = mValue;
		if (getValueAt(index, value)) {
			return mResources.getColorStateList(value.resourceId);
		}
		return null;
	}

	/**
	 * Retrieve the integer value for the attribute at <var>index</var>.
	 *
	 * @param index Index of attribute to retrieve.
	 * @param defValue Value to return if the attribute is not defined or
	 *                 not a resource.
	 *
	 * @return Attribute integer value, or defValue if not defined.
	 */
	public int getInteger(int index, int defValue) {
		TypedValue value = mValue;
		int type = TypedValue.TYPE_NULL;
		if (getValueAt(index, value)) {
			type = value.type;
		}

		if (type == TypedValue.TYPE_NULL) {
			return defValue;
		} else if (type >= TypedValue.TYPE_FIRST_INT
				&& type <= TypedValue.TYPE_LAST_INT) {
			return value.data;
		}

		throw new UnsupportedOperationException("Can't convert to integer: type=0x"
				+ Integer.toHexString(type));
	}

	/**
	 * Retrieve a dimensional unit attribute at <var>index</var>.  Unit
	 * conversions are based on the current {@link android.util.DisplayMetrics DisplayMetrics}
	 * associated with the resources this {@link TypedArray} object
	 * came from.
	 *
	 * @param index Index of attribute to retrieve.
	 * @param defValue Value to return if the attribute is not defined or
	 *                 not a resource.
	 *
	 * @return Attribute dimension value multiplied by the appropriate
	 * metric, or defValue if not defined.
	 *
	 * @see #getDimensionPixelOffset
	 * @see #getDimensionPixelSize
	 */
	public float getDimension(int index, float defValue) {
		TypedValue value = mValue;
		int type = TypedValue.TYPE_NULL;
		if (getValueAt(index, value)) {
			type = value.type;
		}

		if (type == TypedValue.TYPE_NULL) {
			return defValue;
		} else if (type == TypedValue.TYPE_DIMENSION) {
			return TypedValue.complexToDimension(
					value.data, mResources.getDisplayMetrics());
		}

		throw new UnsupportedOperationException("Can't convert to dimension: type=0x"
				+ Integer.toHexString(type));
	}

	/**
	 * Retrieve a dimensional unit attribute at <var>index</var> for use
	 * as an offset in raw pixels.  This is the same as
	 * {@link #getDimension}, except the returned value is converted to
	 * integer pixels for you.  An offset conversion involves simply
	 * truncating the base value to an integer.
	 *
	 * @param index Index of attribute to retrieve.
	 * @param defValue Value to return if the attribute is not defined or
	 *                 not a resource.
	 *
	 * @return Attribute dimension value multiplied by the appropriate
	 * metric and truncated to integer pixels, or defValue if not defined.
	 *
	 * @see #getDimension
	 * @see #getDimensionPixelSize
	 */
	public int getDimensionPixelOffset(int index, int defValue) {
		TypedValue value = mValue;
		int type = TypedValue.TYPE_NULL;
		if (getValueAt(index, value)) {
			type = value.type;
		}

		if (type == TypedValue.TYPE_NULL) {
			return defValue;
		} else if (type == TypedValue.TYPE_DIMENSION) {
			return TypedValue.complexToDimensionPixelOffset(
					value.data, mResources.getDisplayMetrics());
		}

		throw new UnsupportedOperationException("Can't convert to dimension: type=0x"
				+ Integer.toHexString(type));
	}

	/**
	 * Retrieve a dimensional unit attribute at <var>index</var> for use
	 * as a size in raw pixels.  This is the same as
	 * {@link #getDimension}, except the returned value is converted to
	 * integer pixels for use as a size.  A size conversion involves
	 * rounding the base value, and ensuring that a non-zero base value
	 * is at least one pixel in size.
	 *
	 * @param index Index of attribute to retrieve.
	 * @param defValue Value to return if the attribute is not defined or
	 *                 not a resource.
	 *
	 * @return Attribute dimension value multiplied by the appropriate
	 * metric and truncated to integer pixels, or defValue if not defined.
	 *
	 * @see #getDimension
	 * @see #getDimensionPixelOffset
	 */
	public int getDimensionPixelSize(int index, int defValue) {
		TypedValue value = mValue;
		int type = TypedValue.TYPE_NULL;
		if (getValueAt(index, value)) {
			type = value.type;
		}

		if (type == TypedValue.TYPE_NULL) {
			return defValue;
		} else if (type == TypedValue.TYPE_DIMENSION) {
			return TypedValue.complexToDimensionPixelSize(
					value.data, mResources.getDisplayMetrics());
		}

		throw new UnsupportedOperationException("Can't convert to dimension: type=0x"
				+ Integer.toHexString(type));
	}

	/**
	 * Special version of {@link #getDimensionPixelSize} for retrieving
	 * {@link android.view.ViewGroup}'s layout_width and layout_height
	 * attributes.  This is only here for performance reasons; applications
	 * should use {@link #getDimensionPixelSize}.
	 *
	 * @param index Index of the attribute to retrieve.
	 * @param name Textual name of attribute for error reporting.
	 *
	 * @return Attribute dimension value multiplied by the appropriate
	 * metric and truncated to integer pixels.
	 */
	public int getLayoutDimension(int index, String name) {
		TypedValue value = mValue;
		int type = TypedValue.TYPE_NULL;
		if (getValueAt(index, value)) {
			type = value.type;
		}

		if (type >= TypedValue.TYPE_FIRST_INT
				&& type <= TypedValue.TYPE_LAST_INT) {
			return value.data;
		} else if (type == TypedValue.TYPE_DIMENSION) {
			return TypedValue.complexToDimensionPixelSize(
					value.data, mResources.getDisplayMetrics());
		}

		throw new RuntimeException(getPositionDescription()
				+ ": You must supply a " + name + " attribute.");
	}

	/**
	 * Special version of {@link #getDimensionPixelSize} for retrieving
	 * {@link android.view.ViewGroup}'s layout_width and layout_height
	 * attributes.  This is only here for performance reasons; applications
	 * should use {@link #getDimensionPixelSize}.
	 *
	 * @param index Index of the attribute to retrieve.
	 * @param defValue The default value to return if this attribute is not
	 * default or contains the wrong type of data.
	 *
	 * @return Attribute dimension value multiplied by the appropriate
	 * metric and truncated to integer pixels.
	 */
	public int getLayoutDimension(int index, int defValue) {
		TypedValue value = mValue;
		int type = TypedValue.TYPE_NULL;
		if (getValueAt(index, value)) {
			type = value.type;
		}

		if (type >= TypedValue.TYPE_FIRST_INT
				&& type <= TypedValue.TYPE_LAST_INT) {
			return value.data;
		} else if (type == TypedValue.TYPE_DIMENSION) {
			return TypedValue.complexToDimensionPixelSize(
					value.data, mResources.getDisplayMetrics());
		}

		return defValue;
	}

	/**
	 * Retrieve a fractional unit attribute at <var>index</var>.
	 *
	 * @param index Index of attribute to retrieve.
	 * @param base The base value of this fraction.  In other words, a
	 *             standard fraction is multiplied by this value.
	 * @param pbase The parent base value of this fraction.  In other
	 *             words, a parent fraction (nn%p) is multiplied by this
	 *             value.
	 * @param defValue Value to return if the attribute is not defined or
	 *                 not a resource.
	 *
	 * @return Attribute fractional value multiplied by the appropriate
	 * base value, or defValue if not defined.
	 */
	public float getFraction(int index, int base, int pbase, float defValue) {
		TypedValue value = mValue;
		int type = TypedValue.TYPE_NULL;
		if (getValueAt(index, value)) {
			type = value.type;
		}

		if (value.type == TypedValue.TYPE_NULL) {
			return defValue;
		} else if (value.type == TypedValue.TYPE_FRACTION) {
			return TypedValue.complexToFraction(value.data, base, pbase);
		}
		throw new UnsupportedOperationException("Can't convert to fraction: type=0x"
				+ Integer.toHexString(type));
	}

	/**
	 * Retrieve the resource identifier for the attribute at
	 * <var>index</var>.  Note that attribute resource as resolved when
	 * the overall {@link TypedArray} object is retrieved.  As a
	 * result, this function will return the resource identifier of the
	 * final resource value that was found, <em>not</em> necessarily the
	 * original resource that was specified by the attribute.
	 *
	 * @param index Index of attribute to retrieve.
	 * @param defValue Value to return if the attribute is not defined or
	 *                 not a resource.
	 *
	 * @return Attribute resource identifier, or defValue if not defined.
	 */
	public int getResourceId(int index, int defValue) {
		TypedValue value = mValue;
		if (getValueAt(index, value)) {
			return value.resourceId;
		}
		return defValue;
	}

	/**
	 * Retrieve the Drawable for the attribute at <var>index</var>.  This
	 * gets the resource ID of the selected attribute, and uses
	 * {@link Resources#getDrawable Resources.getDrawable} of the owning
	 * Resources object to retrieve its Drawable.
	 *
	 * @param index Index of attribute to retrieve.
	 *
	 * @return Drawable for the attribute, or null if not defined.
	 */
	public Drawable getDrawable(int index) {
		TypedValue value = mValue;
		if (getValueAt(index, value)) {
			return mResources.getDrawable(value.resourceId);
		}
		return null;
	}

	/**
	 * Retrieve the CharSequence[] for the attribute at <var>index</var>.
	 * This gets the resource ID of the selected attribute, and uses
	 * {@link Resources#getTextArray Resources.getTextArray} of the owning
	 * Resources object to retrieve its String[].
	 *
	 * @param index Index of attribute to retrieve.
	 *
	 * @return CharSequence[] for the attribute, or null if not defined.
	 */
	public CharSequence[] getTextArray(int index) {
		TypedValue value = mValue;
		if (getValueAt(index, value)) {
			return mResources.getTextArray(value.resourceId);
		}
		return null;
	}

	/**
	 * Retrieve the raw TypedValue for the attribute at <var>index</var>.
	 *
	 * @param index Index of attribute to retrieve.
	 * @param outValue TypedValue object in which to place the attribute's
	 *                 data.
	 *
	 * @return Returns true if the value was retrieved, else false.
	 */
	public boolean getValue(int index, TypedValue outValue) {
		return getValueAt(index, outValue);
	}

	/**
	 * Determines whether there is an attribute at <var>index</var>.
	 *
	 * @param index Index of attribute to retrieve.
	 *
	 * @return True if the attribute has a value, false otherwise.
	 */
	public boolean hasValue(int index) {
		return mTypedArray.hasValue(index);
	}

	/**
	 * Retrieve the raw TypedValue for the attribute at <var>index</var>
	 * and return a temporary object holding its data.  This object is only
	 * valid until the next call on to {@link TypedArray}.
	 *
	 * @param index Index of attribute to retrieve.
	 *
	 * @return Returns a TypedValue object if the attribute is defined,
	 *         containing its data; otherwise returns null.  (You will not
	 *         receive a TypedValue whose type is TYPE_NULL.)
	 */
	public TypedValue peekValue(int index) {
		if (getValueAt(index, mValue)) {
			return mValue;
		}
		return null;
	}

	/**
	 * Returns a message about the parser state suitable for printing error messages.
	 */
	public String getPositionDescription() {
		return mTypedArray.getPositionDescription();
	}

	/**
	 * Give back a previously retrieved StyledAttributes, for later re-use.
	 */
	public void recycle() {
		mTypedArray.recycle();
	}

	private boolean getValueAt(int index, TypedValue outValue) {
		if (!mTypedArray.getValue(index, outValue)) {
			return false;
		}

		if (outValue.type != TypedValue.TYPE_ATTRIBUTE) {
			return true;
		}

		return mTheme.resolveAttribute(outValue.data, outValue, true);
	}
}
