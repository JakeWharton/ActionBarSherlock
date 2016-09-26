package com.actionbarsherlock.internal.widget;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.R;

/**
 * IcsToast is a child of {@link Toast} and is used to create custom Ice Cream
 * Sandwich {@code Toast}. Some properties of the {@code Toast} are passed in
 * the constructor and are custom, like text inside it and duration for how long
 * it is being shown, however, some values like text color are hard coded.
 * 
 */
public class IcsToast extends Toast {
	public static final int LENGTH_LONG = Toast.LENGTH_LONG;
	public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
	private static final String TAG = "Toast";

	/**
	 * Creates an {@code IceCreamSandwitch} {@link Toast} with the passed
	 * values. The gravity in the {@code} is always in the center and text color
	 * is always white.
	 * 
	 * @param context
	 *            see {@link Context}
	 * @param s
	 *            is the text to be shown in the {@link Toast}
	 * @param duration
	 *            for how long the Toast will be shown
	 * @return {@link Toast}
	 */
	public static Toast makeText(Context context, CharSequence s, int duration) {
		if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
			return Toast.makeText(context, s, duration);
		}
		IcsToast toast = new IcsToast(context);
		toast.setDuration(duration);
		TextView view = new TextView(context);
		view.setText(s);
		// Original AOSP using reference on
		// @android:color/bright_foreground_dark
		// bright_foreground_dark - reference on @android:color/background_light
		// background_light - 0xffffffff
		view.setTextColor(0xffffffff);
		view.setGravity(Gravity.CENTER);
		view.setBackgroundResource(R.drawable.abs__toast_frame);
		toast.setView(view);
		return toast;
	}

	public static Toast makeText(Context context, int resId, int duration) {
		return makeText(context, context.getResources().getString(resId),
				duration);
	}

	public IcsToast(Context context) {
		super(context);
	}

	/**
	 * The method sets the value of text to be shown in the {@link Toast}. If
	 * the value of the current Android API is greater than 14, then simply the
	 * text of this class parent is set. Otherwise, the {@class} is set as the
	 * text of the given {@link View}.
	 * 
	 * @param s
	 *            is the text to be shown in the {@link Toast}
	 * 
	 */
	@Override
	public void setText(CharSequence s) {
		if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
			super.setText(s);
			return;
		}
		if (getView() == null) {
			return;
		}
		try {
			((TextView) getView()).setText(s);
		} catch (ClassCastException e) {
			Log.e(TAG, "This Toast was not created with IcsToast.makeText", e);
		}
	}
}
