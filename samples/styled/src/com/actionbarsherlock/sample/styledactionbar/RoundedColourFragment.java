/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.actionbarsherlock.sample.styledactionbar;

import com.actionbarsherlock.sample.styledactionbar.R;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class RoundedColourFragment extends Fragment {
	private View mView;
	private int mColor;
	private float mWeight;
	private int mMarginLeft;
	private int mMarginRight;
	private int mMarginTop;
	private int mMarginBottom;

	// need a public empty constructor for framework to instantiate
	public RoundedColourFragment() {}
	
	public RoundedColourFragment(int color, float weight, int margin_left, int margin_right, int margin_top, int margin_bottom) {
		this.mColor = color;
		this.mWeight = weight;
		this.mMarginLeft = margin_left;
		this.mMarginRight = margin_right;
		this.mMarginTop = margin_top;
		this.mMarginBottom = margin_bottom;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mView = new View(this.getActivity());

		GradientDrawable background = (GradientDrawable)this.getResources().getDrawable(R.drawable.rounded_rect);
		background.setColor(this.mColor);

		this.mView.setBackgroundDrawable(background);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT, mWeight);
		lp.setMargins(this.mMarginLeft, this.mMarginTop, this.mMarginRight, this.mMarginBottom);
		this.mView.setLayoutParams(lp);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return this.mView;
	}
}
