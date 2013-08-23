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

package com.actionbarsherlock.internal.app;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ActionBarWrapperICS extends ActionBarWrapperNative {

    private final Activity mActivity;

    public ActionBarWrapperICS(Activity activity) {
        super(activity);
        mActivity = activity;
    }

    @Override
    public void setHomeAsUpIndicator(Drawable indicator) {
        final View home = mActivity.findViewById(android.R.id.home);
        if (home == null) {
            // Action bar doesn't have a known configuration, an OEM messed with things.
            return;
        }

        final ViewGroup parent = (ViewGroup) home.getParent();
        final int childCount = parent.getChildCount();
        if (childCount != 2) {
            // No idea which one will be the right one, an OEM messed with things.
            return;
        }

        final View first = parent.getChildAt(0);
        final View second = parent.getChildAt(1);
        final View up = first.getId() == android.R.id.home ? second : first;

        if (up instanceof ImageView) {
            // Jackpot! (Probably...)
            ((ImageView) up).setImageDrawable(indicator);
        }
    }

    @Override
    public void setHomeAsUpIndicator(int resId) {
        setHomeAsUpIndicator(mActivity.getResources().getDrawable(resId));
    }

    @Override
    public void setHomeActionContentDescription(CharSequence description) {
        // Not supported.
    }

    @Override
    public void setHomeActionContentDescription(int resId) {
        // Not supported.
    }
}
