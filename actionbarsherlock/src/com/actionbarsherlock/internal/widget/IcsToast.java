
package com.actionbarsherlock.internal.widget;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.actionbarsherlock.R;

public class IcsToast extends android.widget.Toast {
    public static final int LENGTH_LONG = android.widget.Toast.LENGTH_LONG;
    public static final int LENGTH_SHORT = android.widget.Toast.LENGTH_SHORT;
    private static final String TAG = "Toast";

    public static IcsToast makeText(Context context, CharSequence s, int duration) {
        IcsToast toast = new IcsToast(context);
        toast.setDuration(duration);
        TextView view = new TextView(context);
        view.setText(s);
        view.setTextColor(0xFFDADADA);
        view.setGravity(Gravity.CENTER);
        view.setBackgroundResource(R.drawable.abs__toast_frame);
        toast.setView(view);
        return toast;
    }

    public static IcsToast makeText(Context context, int resId, int duration) {
        return IcsToast.makeText(context, context.getResources().getString(resId),
                duration);
    }

    public IcsToast(Context context) {
        super(context);
    }

    @Override
    public void setText(CharSequence s) {
        if (getView() == null) {
            return;
        }
        try {
            ((TextView) getView()).setText(s);
        } catch (ClassCastException e) {
            Log.e(IcsToast.TAG, "This IcsToast was not created with IcsToast.makeText", e);
        }
    }
}
