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

import com.actionbarsherlock.R;
import com.actionbarsherlock.view.MenuItem;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

/**
 * This class provides a handy way to tie together the functionality of
 * {@link DrawerLayout} and the framework <code>ActionBar</code> to implement the recommended
 * design for navigation drawers.
 *
 * <p>To use <code>ActionBarDrawerToggle</code>, create one in your Activity and call through
 * to the following methods corresponding to your Activity callbacks:</p>
 *
 * <ul>
 * <li>{@link Activity#onConfigurationChanged(android.content.res.Configuration) onConfigurationChanged}</li>
 * <li>{@link Activity#onOptionsItemSelected(android.view.MenuItem) onOptionsItemSelected}</li>
 * </ul>
 *
 * <p>Call {@link #syncState()} from your <code>Activity</code>'s
 * {@link Activity#onPostCreate(android.os.Bundle) onPostCreate} to synchronize the indicator
 * with the state of the linked DrawerLayout after <code>onRestoreInstanceState</code>
 * has occurred.</p>
 *
 * <p><code>ActionBarDrawerToggle</code> can be used directly as a
 * {@link DrawerLayout.DrawerListener}, or if you are already providing your own listener,
 * call through to each of the listener methods from your own.</p>
 */
public class ActionBarDrawerToggle implements DrawerLayout.DrawerListener {
	private static final int[] THEME_ATTRS = new int[] {
        R.attr.homeAsUpIndicator
	};
    // android.R.id.home as defined by public API in v11
    private static final int ID_HOME = 0x0102002c;

    private final Context mContext;
    private final ActionBar mActionBar;
    private final DrawerLayout mDrawerLayout;
    private boolean mDrawerIndicatorEnabled = true;

    private Drawable mThemeImage;
    private Drawable mDrawerImage;
    private SlideDrawable mSlider;
    private final int mDrawerImageResource;
    private final int mOpenDrawerContentDescRes;
    private final int mCloseDrawerContentDescRes;

    /**
     * Construct a new ActionBarDrawerToggle.
     *
     * <p>The given {@link Activity} will be linked to the specified {@link DrawerLayout}.
     * The provided drawer indicator drawable will animate slightly off-screen as the drawer
     * is opened, indicating that in the open state the drawer will move off-screen when pressed
     * and in the closed state the drawer will move on-screen when pressed.</p>
     *
     * <p>String resources must be provided to describe the open/close drawer actions for
     * accessibility services.</p>
     *
     * @param context A valid context for loading resources
     * @param actionBar The ActionBar of the hosting Activity
     * @param drawerLayout The DrawerLayout to link to the given Activity's ActionBar
     * @param drawerImageRes A Drawable resource to use as the drawer indicator
     * @param openDrawerContentDescRes A String resource to describe the "open drawer" action
     *                                 for accessibility
     * @param closeDrawerContentDescRes A String resource to describe the "close drawer" action
     *                                  for accessibility
     */
    public ActionBarDrawerToggle(Activity activity, ActionBar actionBar, DrawerLayout drawerLayout,
            int drawerImageRes, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        mContext = activity;
        mActionBar = actionBar;
        mDrawerLayout = drawerLayout;
        mDrawerImageResource = drawerImageRes;
        mOpenDrawerContentDescRes = openDrawerContentDescRes;
        mCloseDrawerContentDescRes = closeDrawerContentDescRes;

        mThemeImage = getThemeUpIndicator();
        mDrawerImage = mContext.getResources().getDrawable(drawerImageRes);
        mSlider = new SlideDrawable(mDrawerImage);
        mSlider.setOffsetBy(1.f / 3);
    }
    
	private Drawable getThemeUpIndicator() {
        final TypedArray a = mContext.obtainStyledAttributes(THEME_ATTRS);
        final Drawable result = a.getDrawable(0);
        a.recycle();
        return result;
    }

    /**
     * Synchronize the state of the drawer indicator/affordance with the linked DrawerLayout.
     *
     * <p>This should be called from your <code>Activity</code>'s
     * {@link Activity#onPostCreate(android.os.Bundle) onPostCreate} method to synchronize after
     * the DrawerLayout's instance state has been restored, and any other time when the state
     * may have diverged in such a way that the ActionBarDrawerToggle was not notified.
     * (For example, if you stop forwarding appropriate drawer events for a period of time.)</p>
     */
    public void syncState() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mSlider.setOffset(1.f);
        } else {
            mSlider.setOffset(0.f);
        }

        if (mDrawerIndicatorEnabled) {
        	mActionBar.setHomeAsUpIndicator(mSlider);
        	mActionBar.setHomeActionContentDescription(mDrawerLayout.isDrawerOpen(GravityCompat.START) ?
                    mOpenDrawerContentDescRes : mCloseDrawerContentDescRes);
        }
    }

    /**
     * Enable or disable the drawer indicator. The indicator defaults to enabled.
     *
     * <p>When the indicator is disabled, the <code>ActionBar</code> will revert to displaying
     * the home-as-up indicator provided by the <code>Activity</code>'s theme in the
     * <code>android.R.attr.homeAsUpIndicator</code> attribute instead of the animated
     * drawer glyph.</p>
     *
     * @param enable true to enable, false to disable
     */
    public void setDrawerIndicatorEnabled(boolean enable) {
        if (enable != mDrawerIndicatorEnabled) {
            if (enable) {
            	mActionBar.setHomeAsUpIndicator(mSlider);
            	mActionBar.setHomeActionContentDescription(mDrawerLayout.isDrawerOpen(GravityCompat.START) ?
                        mOpenDrawerContentDescRes : mCloseDrawerContentDescRes);
            } else {
            	mActionBar.setHomeAsUpIndicator(mThemeImage);
            	mActionBar.setHomeActionContentDescription(0);
            }
            mDrawerIndicatorEnabled = enable;
        }
    }

    /**
     * @return true if the enhanced drawer indicator is enabled, false otherwise
     * @see #setDrawerIndicatorEnabled(boolean)
     */
    public boolean isDrawerIndicatorEnabled() {
        return mDrawerIndicatorEnabled;
    }

    /**
     * This method should always be called by your <code>Activity</code>'s
     * {@link Activity#onConfigurationChanged(android.content.res.Configuration) onConfigurationChanged}
     * method.
     *
     * @param newConfig The new configuration
     */
    public void onConfigurationChanged(Configuration newConfig) {
        // Reload drawables that can change with configuration
        mThemeImage = getThemeUpIndicator();
        mDrawerImage = mContext.getResources().getDrawable(mDrawerImageResource);
        syncState();
    }

    /**
     * This method should be called by your <code>Activity</code>'s
     * {@link Activity#onOptionsItemSelected(android.view.MenuItem) onOptionsItemSelected} method.
     * If it returns true, your <code>onOptionsItemSelected</code> method should return true and
     * skip further processing.
     *
     * @param item the MenuItem instance representing the selected menu item
     * @return true if the event was handled and further processing should not occur
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null && item.getItemId() == ID_HOME && mDrawerIndicatorEnabled) {
            if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return false;
    }

    /**
     * {@link DrawerLayout.DrawerListener} callback method. If you do not use your
     * ActionBarDrawerToggle instance directly as your DrawerLayout's listener, you should call
     * through to this method from your own listener object.
     *
     * @param drawerView The child view that was moved
     * @param slideOffset The new offset of this drawer within its range, from 0-1
     */
    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        float glyphOffset = mSlider.getOffset();
        if (slideOffset > 0.5f) {
            glyphOffset = Math.max(glyphOffset, Math.max(0.f, slideOffset - 0.5f) * 2);
        } else {
            glyphOffset = Math.min(glyphOffset, slideOffset * 2);
        }
        mSlider.setOffset(glyphOffset);
    }

    /**
     * {@link DrawerLayout.DrawerListener} callback method. If you do not use your
     * ActionBarDrawerToggle instance directly as your DrawerLayout's listener, you should call
     * through to this method from your own listener object.
     *
     * @param drawerView Drawer view that is now open
     */
    @Override
    public void onDrawerOpened(View drawerView) {
        mSlider.setOffset(1.f);
        if (mDrawerIndicatorEnabled) {
            mActionBar.setHomeActionContentDescription(mOpenDrawerContentDescRes);
        }
    }

    /**
     * {@link DrawerLayout.DrawerListener} callback method. If you do not use your
     * ActionBarDrawerToggle instance directly as your DrawerLayout's listener, you should call
     * through to this method from your own listener object.
     *
     * @param drawerView Drawer view that is now closed
     */
    @Override
    public void onDrawerClosed(View drawerView) {
        mSlider.setOffset(0.f);
        if (mDrawerIndicatorEnabled) {
        	mActionBar.setHomeActionContentDescription(mCloseDrawerContentDescRes);
        }
    }

    /**
     * {@link DrawerLayout.DrawerListener} callback method. If you do not use your
     * ActionBarDrawerToggle instance directly as your DrawerLayout's listener, you should call
     * through to this method from your own listener object.
     * 
     * @param newState The new drawer motion state
     */
    @Override
    public void onDrawerStateChanged(int newState) {
    }

    private static class SlideDrawable extends Drawable implements Drawable.Callback {
        private Drawable mWrapped;
        private float mOffset;
        private float mOffsetBy;

        private final Rect mTmpRect = new Rect();

        public SlideDrawable(Drawable wrapped) {
            mWrapped = wrapped;
        }

        public void setOffset(float offset) {
            mOffset = offset;
            invalidateSelf();
        }

        public float getOffset() {
            return mOffset;
        }

        public void setOffsetBy(float offsetBy) {
            mOffsetBy = offsetBy;
            invalidateSelf();
        }

        @Override
        public void draw(Canvas canvas) {
            mWrapped.copyBounds(mTmpRect);
            canvas.save();
            canvas.translate(mOffsetBy * mTmpRect.width() * -mOffset, 0);
            mWrapped.draw(canvas);
            canvas.restore();
        }

        @Override
        public void setChangingConfigurations(int configs) {
            mWrapped.setChangingConfigurations(configs);
        }

        @Override
        public int getChangingConfigurations() {
            return mWrapped.getChangingConfigurations();
        }

        @Override
        public void setDither(boolean dither) {
            mWrapped.setDither(dither);
        }

        @Override
        public void setFilterBitmap(boolean filter) {
            mWrapped.setFilterBitmap(filter);
        }

        @Override
        public void setAlpha(int alpha) {
            mWrapped.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            mWrapped.setColorFilter(cf);
        }

        @Override
        public void setColorFilter(int color, PorterDuff.Mode mode) {
            mWrapped.setColorFilter(color, mode);
        }

        @Override
        public void clearColorFilter() {
            mWrapped.clearColorFilter();
        }

        @Override
        public boolean isStateful() {
            return mWrapped.isStateful();
        }

        @Override
        public boolean setState(int[] stateSet) {
            return mWrapped.setState(stateSet);
        }

        @Override
        public int[] getState() {
            return mWrapped.getState();
        }

        @Override
        public Drawable getCurrent() {
            return mWrapped.getCurrent();
        }

        @Override
        public boolean setVisible(boolean visible, boolean restart) {
            return super.setVisible(visible, restart);
        }

        @Override
        public int getOpacity() {
            return mWrapped.getOpacity();
        }

        @Override
        public Region getTransparentRegion() {
            return mWrapped.getTransparentRegion();
        }

        @Override
        protected boolean onStateChange(int[] state) {
            mWrapped.setState(state);
            return super.onStateChange(state);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            mWrapped.setBounds(bounds);
        }

        @Override
        public int getIntrinsicWidth() {
            return mWrapped.getIntrinsicWidth();
        }

        @Override
        public int getIntrinsicHeight() {
            return mWrapped.getIntrinsicHeight();
        }

        @Override
        public int getMinimumWidth() {
            return mWrapped.getMinimumWidth();
        }

        @Override
        public int getMinimumHeight() {
            return mWrapped.getMinimumHeight();
        }

        @Override
        public boolean getPadding(Rect padding) {
            return mWrapped.getPadding(padding);
        }

        @Override
        public ConstantState getConstantState() {
            return super.getConstantState();
        }

        @Override
        public void invalidateDrawable(Drawable who) {
            if (who == mWrapped) {
                invalidateSelf();
            }
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            if (who == mWrapped) {
                scheduleSelf(what, when);
            }
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            if (who == mWrapped) {
                unscheduleSelf(what);
            }
        }
    }
}
