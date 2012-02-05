package com.actionbarsherlock.internal.nineoldandroids.view.animation;

import java.util.WeakHashMap;
import android.graphics.Matrix;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public final class AnimatorProxy extends Animation {
    public static final boolean NEEDS_PROXY = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;

    private static final WeakHashMap<View, AnimatorProxy> PROXIES =
            new WeakHashMap<View, AnimatorProxy>();

    public static AnimatorProxy wrap(View view) {
        AnimatorProxy proxy = PROXIES.get(view);
        if (proxy == null) {
            proxy = new AnimatorProxy(view);
            PROXIES.put(view, proxy);
        }
        return proxy;
    }

    private final View mView;

    private float mAlpha = 1f;
    private float mTranslationX = 0f;
    private float mTranslationY = 0f;
    private float mScaleX = 1f;
    private float mScaleY = 1f;

    private AnimatorProxy(View view) {
        setDuration(0); //perform transformation immediately
        setFillAfter(true); //persist transformation beyond duration
        view.setAnimation(this);
        mView = view;
    }

    public float getAlpha() {
        return mAlpha;
    }
    public void setAlpha(float alpha) {
        if (mAlpha != alpha) {
            mAlpha = alpha;
            mView.invalidate();
        }
    }
    public float getTranslationX() {
        return mTranslationX;
    }
    public void setTranslationX(float translationX) {
        if (mTranslationX != translationX) {
            mTranslationX = translationX;
            invalidateParent();
        }
    }
    public float getTranslationY() {
        return mTranslationY;
    }
    public void setTranslationY(float translationY) {
        if (mTranslationY != translationY) {
            mTranslationY = translationY;
            invalidateParent();
        }
    }
    public float getScaleX() {
        return mScaleX;
    }
    public void setScaleX(float scale) {
        if (mScaleX != scale) {
            mScaleX = scale;
            invalidateParent();
        }
    }
    public float getScaleY() {
        return mScaleY;
    }
    public void setScaleY(float scaleY) {
        if (mScaleY != scaleY) {
            mScaleY = scaleY;
            invalidateParent();
        }
    }

    private void invalidateParent() {
        ((ViewGroup)mView.getParent()).invalidate();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        t.setAlpha(mAlpha);

        final Matrix m = t.getMatrix();
        m.postTranslate(mTranslationX, mTranslationY);
        m.postScale(mScaleX, mScaleY);
    }
}
