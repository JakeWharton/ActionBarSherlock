package com.actionbarsherlock.internal.nineoldandroids.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.actionbarsherlock.internal.nineoldandroids.view.animation.AnimatorProxy;

public abstract class NineFrameLayout extends FrameLayout {
    private static final boolean NEEDS_PROXY = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;

    private final AnimatorProxy mProxy;

    public NineFrameLayout(Context context) {
        super(context);
        mProxy = NEEDS_PROXY ? AnimatorProxy.wrap(this) : null;
    }
    public NineFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mProxy = NEEDS_PROXY ? AnimatorProxy.wrap(this) : null;
    }
    public NineFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mProxy = NEEDS_PROXY ? AnimatorProxy.wrap(this) : null;
    }

    public float getAlpha() {
        if (NEEDS_PROXY) {
            return mProxy.getAlpha();
        } else {
            return super.getAlpha();
        }
    }
    public void setAlpha(float alpha) {
        if (NEEDS_PROXY) {
            mProxy.setAlpha(alpha);
        } else {
            super.setAlpha(alpha);
        }
    }
    public float getTranslationY() {
        if (NEEDS_PROXY) {
            return mProxy.getTranslationY();
        } else {
            return super.getTranslationY();
        }
    }
    public void setTranslationY(float translationY) {
        if (NEEDS_PROXY) {
            mProxy.setTranslationY(translationY);
        } else {
            super.setTranslationY(translationY);
        }
    }
}
