/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.actionbarsherlock.app;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Method;

import com.actionbarsherlock.R;

/**
 * This class encapsulates some awful hacks.
 * 
 * Before JB-MR2 (API 18) it was not possible to change the home-as-up indicator
 * glyph in an action bar without some really gross hacks. Since the MR2 SDK is
 * not published as of this writing, the new API is accessed via reflection here
 * if available.
 */
class ActionBarDrawerToggleSherlock {
	private static final String TAG = "ActionBarDrawerToggleHoneycomb";

	private static final int[] THEME_ATTRS = new int[] { R.attr.homeAsUpIndicator };

	public static Object setActionBarUpIndicator(Object info,
			Activity activity, Drawable drawable, int contentDescRes) {
		if (info == null) {
			info = new SetIndicatorInfo(activity);
		}

		if (!isSherlockActivity(activity)) {
			return info;
		}

		final SetIndicatorInfo sii = (SetIndicatorInfo) info;
		if (sii.upIndicatorView != null) {
			sii.upIndicatorView.setImageDrawable(drawable);
		} else {
			Log.w(TAG, "Couldn't set home-as-up indicator");
		}
		return info;
	}

	public static Object setActionBarDescription(Object info,
			Activity activity, int contentDescRes) {
		return info;
	}

	public static Drawable getThemeUpIndicator(Activity activity) {
		final TypedArray a = activity.obtainStyledAttributes(THEME_ATTRS);
		final Drawable result = a.getDrawable(0);
		a.recycle();
		return result;
	}

	private static boolean isSherlockActivity(Activity activity) {
		return activity instanceof SherlockActivity;
	}

	private static class SetIndicatorInfo {
		public ImageView upIndicatorView;

		SetIndicatorInfo(Activity activity) {
			ViewGroup homeView = (ViewGroup) activity.findViewById(
					R.id.abs__home).getParent();
			upIndicatorView = (ImageView) homeView.getChildAt(0);
		}
	}
}
