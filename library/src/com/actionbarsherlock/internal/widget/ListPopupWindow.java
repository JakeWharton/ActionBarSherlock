package com.actionbarsherlock.internal.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

/**
 * A proxy between pre- and post-Honeycomb implementations of this class.
 */
public class ListPopupWindow {
    static interface Impl {
        void setAdapter(ListAdapter adapter);
        void setModal(boolean modal);
        void setAnchorView(View anchor);
        void setContentWidth(int width);
        void setOnItemClickListener(AdapterView.OnItemClickListener clickListener);
        void show();
        void dismiss();
        void setOnDismissListener(PopupWindow.OnDismissListener listener);
        void setInputMethodMode(int mode);
        boolean isShowing();
        ListView getListView();
    }

    final Impl mImpl;

    public ListPopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mImpl = new PostHoneycombImpl(context, attrs, defStyleAttr);
        } else {
            mImpl = new PreHoneycombImpl(context);
        }
    }

    public void setAdapter(ListAdapter adapter) {
        mImpl.setAdapter(adapter);
    }

    public void setModal(boolean modal) {
        mImpl.setModal(modal);
    }

    public void setAnchorView(View anchor) {
        mImpl.setAnchorView(anchor);
    }

    public void setContentWidth(int width) {
        mImpl.setContentWidth(width);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener clickListener) {
        mImpl.setOnItemClickListener(clickListener);
    }

    public void show() {
        mImpl.show();
    }

    public void dismiss() {
        mImpl.dismiss();
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        mImpl.setOnDismissListener(listener);
    }

    public void setInputMethodMode(int mode) {
        mImpl.setInputMethodMode(mode);
    }

    public boolean isShowing() {
        return mImpl.isShowing();
    }

    public ListView getListView() {
        return mImpl.getListView();
    }
}

class PostHoneycombImpl implements ListPopupWindow.Impl {
    private final android.widget.ListPopupWindow mBacking;

    public PostHoneycombImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        mBacking = new android.widget.ListPopupWindow(context, attrs, defStyleAttr);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mBacking.setAdapter(adapter);
    }

    @Override
    public void setModal(boolean modal) {
        mBacking.setModal(modal);
    }

    @Override
    public void setAnchorView(View anchor) {
        mBacking.setAnchorView(anchor);
    }

    @Override
    public void setContentWidth(int width) {
        mBacking.setContentWidth(width);
    }

    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener clickListener) {
        mBacking.setOnItemClickListener(clickListener);
    }

    @Override
    public void show() {
        mBacking.show();
    }

    @Override
    public void dismiss() {
        mBacking.dismiss();
    }

    @Override
    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        mBacking.setOnDismissListener(listener);
    }

    @Override
    public void setInputMethodMode(int mode) {
        mBacking.setInputMethodMode(mode);
    }

    @Override
    public boolean isShowing() {
        return mBacking.isShowing();
    }

    @Override
    public ListView getListView() {
        return mBacking.getListView();
    }
}

class PreHoneycombImpl implements ListPopupWindow.Impl {
    final Context mContext;

    private AlertDialog mDialog;
    private ListAdapter mAdapter;
    private AdapterView.OnItemClickListener mClickListener;
    private PopupWindow.OnDismissListener mDismissListener;

    public PreHoneycombImpl(Context context) {
        mContext = context;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public void setModal(boolean modal) {
        //We are always modal
    }

    @Override
    public void setAnchorView(View anchor) {
        //We are ignoring
    }

    @Override
    public void setContentWidth(int width) {
        //Determined by platform
    }

    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener clickListener) {
        mClickListener = clickListener;
    }

    @Override
    public void show() {
        if (mDialog == null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setAdapter(mAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mClickListener != null) {
                        //We only really care about the position
                        mClickListener.onItemClick(null, null, which, 0);
                    }
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (mDismissListener != null) {
                        mDismissListener.onDismiss();
                    }
                }
            });
            mDialog = builder.create();
        }
        mDialog.show();
    }

    @Override
    public void dismiss() {
        if (mDialog != null) {
            try {
                mDialog.dismiss();
            } catch (Exception e) {
                //Whatevs
            }
            mDialog = null;
        }
    }

    @Override
    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        mDismissListener = listener;
    }

    @Override
    public void setInputMethodMode(int mode) {
        //We are ignoring
    }

    @Override
    public boolean isShowing() {
        return (mDialog != null) && mDialog.isShowing();
    }

    @Override
    public ListView getListView() {
        if (mDialog != null) {
            return mDialog.getListView();
        }
        return null;
    }
}
