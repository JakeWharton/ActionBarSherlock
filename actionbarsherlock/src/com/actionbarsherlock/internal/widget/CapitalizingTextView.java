package com.actionbarsherlock.internal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

/**
 * {@code CapitalizingTextView} is a child of {@link TextView}. It's purpose is
 * to capitalise all letters of {@link TextView} and set them as its text. The
 * {@code TextView} can have default style or have custom style if constructor
 * with used to create an instance of {@code CapitalizingTextView}.
 * 
 */
public class CapitalizingTextView extends TextView {
	private static final boolean SANS_ICE_CREAM = Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	private static final boolean IS_GINGERBREAD = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;

	private static final int[] R_styleable_TextView = new int[] { android.R.attr.textAllCaps };
	private static final int R_styleable_TextView_textAllCaps = 0;

	private boolean mAllCaps;

	public CapitalizingTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * 
	 * The constructor sets the value of Boolean {@code mAllCaps} to True if
	 * the passed {@code AttributeSet} contains attribute Boolean stating that
	 * all letter of the {@code TextView} are capitals. This is done by creating
	 * an instance of {@code TypedArray} and getting the Boolean from it.
	 * 
	 * @param context
	 *            see {@link Context}
	 * @param attrs
	 *            contains the attributes to be retrieved from {@code defStyle}
	 * @param defStyle
	 *            is a reference to style resource
	 */
	public CapitalizingTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R_styleable_TextView, defStyle, 0);
		mAllCaps = a.getBoolean(R_styleable_TextView_textAllCaps, true);
		a.recycle();
	}

	/**
	 * Capitalises the given {@code CharSequence} text and sets it as
	 * {@code CapitalizingTextView} text using different ways depending on
	 * Android API version.
	 * 
	 * @param text
	 *            that is set as {@code CapitalizingTextView} text.
	 */
	public void setTextCompat(CharSequence text) {
		if (SANS_ICE_CREAM && mAllCaps && text != null) {
			if (IS_GINGERBREAD) {
				try {
					setText(text.toString().toUpperCase(Locale.ROOT));
				} catch (NoSuchFieldError e) {
					// Some manufacturer broke Locale.ROOT. See #572.
					setText(text.toString().toUpperCase());
				}
			} else {
				setText(text.toString().toUpperCase());
			}
		} else {
			setText(text);
		}
	}
}
