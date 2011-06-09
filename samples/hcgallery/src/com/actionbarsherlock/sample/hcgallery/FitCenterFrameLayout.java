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

package com.actionbarsherlock.sample.hcgallery;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple layout that fits and centers each child view, maintaining aspect ratio.
 */
public class FitCenterFrameLayout extends ViewGroup {
    public FitCenterFrameLayout(Context context) {
        super(context);
    }

    public FitCenterFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements.
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        int childWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.UNSPECIFIED);
        int childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED);

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).measure(childWidthSpec, childHeightSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();

        final int parentLeft = getPaddingLeft();
        final int parentTop = getPaddingTop();
        final int parentRight = r - l - getPaddingRight();
        final int parentBottom = b - t - getPaddingBottom();

        final int parentWidth = parentRight - parentLeft;
        final int parentHeight = parentBottom - parentTop;

        int unpaddedWidth, unpaddedHeight, parentUnpaddedWidth, parentUnpaddedHeight;
        int childPaddingLeft, childPaddingTop, childPaddingRight, childPaddingBottom;

        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            // Fit and center the child within the parent. Make sure not to consider padding
            // as part of the child's aspect ratio.

            childPaddingLeft = child.getPaddingLeft();
            childPaddingTop = child.getPaddingTop();
            childPaddingRight = child.getPaddingRight();
            childPaddingBottom = child.getPaddingBottom();

            unpaddedWidth = child.getMeasuredWidth() - childPaddingLeft - childPaddingRight;
            unpaddedHeight = child.getMeasuredHeight() - childPaddingTop - childPaddingBottom;

            parentUnpaddedWidth = parentWidth - childPaddingLeft - childPaddingRight;
            parentUnpaddedHeight = parentHeight - childPaddingTop - childPaddingBottom;

            if (parentUnpaddedWidth * unpaddedHeight > parentUnpaddedHeight * unpaddedWidth) {
                // The child view should be left/right letterboxed.
                final int scaledChildWidth = unpaddedWidth * parentUnpaddedHeight
                        / unpaddedHeight + childPaddingLeft + childPaddingRight;
                child.layout(
                        parentLeft + (parentWidth - scaledChildWidth) / 2,
                        parentTop,
                        parentRight - (parentWidth - scaledChildWidth) / 2,
                        parentBottom);
            } else {
                // The child view should be top/bottom letterboxed.
                final int scaledChildHeight = unpaddedHeight * parentUnpaddedWidth
                        / unpaddedWidth + childPaddingTop + childPaddingBottom;
                child.layout(
                        parentLeft,
                        parentTop + (parentHeight - scaledChildHeight) / 2,
                        parentRight,
                        parentTop + (parentHeight + scaledChildHeight) / 2);
            }
        }
    }
}
