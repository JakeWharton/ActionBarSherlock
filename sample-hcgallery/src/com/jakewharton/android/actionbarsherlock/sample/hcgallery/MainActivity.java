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

package com.jakewharton.android.actionbarsherlock.sample.hcgallery;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    private static final int NOTIFICATION_DEFAULT = 1;
    private static final String ACTION_DIALOG = "com.jakewharton.android.actionbarsherlock.sample.hcgallery.action.DIALOG";

    private View mActionBarView;
    private Animator mCurrentTitlesAnimator;
    private String[] mToggleLabels = {"Show Titles", "Hide Titles"};
    private int mLabelIndex = 1;
    private int mThemeId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null && savedInstanceState.getInt("theme", -1) != -1) {
            mThemeId = savedInstanceState.getInt("theme");
            this.setTheme(mThemeId);
        }

        setContentView(R.layout.main);

        Directory.initializeDirectory();

        ActionBar bar = getSupportActionBar();

        int i;
        for (i = 0; i < Directory.getCategoryCount(); i++) {
            bar.addTab(bar.newTab().setText(Directory.getCategory(i).getName())
                    .setTabListener(this));
        }

        mActionBarView = getLayoutInflater().inflate(
                R.layout.action_bar_custom, null);

        bar.setCustomView(mActionBarView);
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_USE_LOGO);
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayShowHomeEnabled(true);

        // If category is not saved to the savedInstanceState,
        // 0 is returned by default.
        if(savedInstanceState != null) {
            int category = savedInstanceState.getInt("category");
            bar.selectTab(bar.getTabAt(category));
        }
    }

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        TitlesFragment titleFrag = (TitlesFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frag_title);
        titleFrag.populateTitles(tab.getPosition());

        titleFrag.selectPosition(0);
    }

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.camera:
            Intent intent = new Intent(this, CameraSample.class);
            intent.putExtra("theme", mThemeId);
            startActivity(intent);
            return true;

        case R.id.toggleTitles:
            toggleVisibleTitles();
            return true;

        case R.id.toggleTheme:
            if (mThemeId == R.style.AppTheme_Dark) {
                mThemeId = R.style.AppTheme_Light;
            } else {
                mThemeId = R.style.AppTheme_Dark;
            }
            this.recreate();
            return true;

        case R.id.showDialog:
            showDialog("This is indeed an awesome dialog.");
            return true;

        case R.id.showStandardNotification:
            showNotification(false);
            return true;

        case R.id.showCustomNotification:
            showNotification(true);
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void toggleVisibleTitles() {
        // Use these for custom animations.
        final FragmentManager fm = getSupportFragmentManager();
        final TitlesFragment f = (TitlesFragment) fm
                .findFragmentById(R.id.frag_title);
        final View titlesView = f.getView();
        mLabelIndex = 1 - mLabelIndex;

        // Determine if we're in portrait, and whether we're showing or hiding the titles
        // with this toggle.
        final boolean isPortrait = getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT;

        final boolean shouldShow = f.isHidden() || mCurrentTitlesAnimator != null;

        // Cancel the current titles animation if there is one.
        if (mCurrentTitlesAnimator != null)
            mCurrentTitlesAnimator.cancel();

        // Begin setting up the object animator. We'll animate the bottom or right edge of the
        // titles view, as well as its alpha for a fade effect.
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(
                titlesView,
                PropertyValuesHolder.ofInt(
                        isPortrait ? "bottom" : "right",
                        shouldShow ? getResources().getDimensionPixelSize(R.dimen.titles_size)
                                   : 0),
                PropertyValuesHolder.ofFloat("alpha", shouldShow ? 1 : 0)
        );

        // At each step of the animation, we'll perform layout by calling setLayoutParams.
        final ViewGroup.LayoutParams lp = titlesView.getLayoutParams();
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                // *** WARNING ***: triggering layout at each animation frame highly impacts
                // performance so you should only do this for simple layouts. More complicated
                // layouts can be better served with individual animations on child views to
                // avoid the performance penalty of layout.
                if (isPortrait) {
                    lp.height = (Integer) valueAnimator.getAnimatedValue();
                } else {
                    lp.width = (Integer) valueAnimator.getAnimatedValue();
                }
                titlesView.setLayoutParams(lp);
            }
        });

        if (shouldShow) {
            fm.beginTransaction().show(f).commit();
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    mCurrentTitlesAnimator = null;
                }
            });

        } else {
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                boolean canceled;

                @Override
                public void onAnimationCancel(Animator animation) {
                    canceled = true;
                    super.onAnimationCancel(animation);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (canceled)
                        return;
                    mCurrentTitlesAnimator = null;
                    fm.beginTransaction().hide(f).commit();
                }
            });
        }

        // Start the animation.
        objectAnimator.start();
        mCurrentTitlesAnimator = objectAnimator;

        invalidateOptionsMenu();

        // Manually trigger onNewIntent to check for ACTION_DIALOG.
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (ACTION_DIALOG.equals(intent.getAction())) {
            showDialog(intent.getStringExtra(Intent.EXTRA_TEXT));
        }
    }

    void showDialog(String text) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        DialogFragment newFragment = MyDialogFragment.newInstance(text);

        // Show the dialog.
        newFragment.show(ft, "dialog");
    }

    void showNotification(boolean custom) {
        final Resources res = getResources();
        final NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_notify_example)
                .setAutoCancel(true)
                .setTicker(getString(R.string.notification_text))
                .setContentIntent(getDialogPendingIntent("Tapped the notification entry."));

        if (custom) {
            // Sets a custom content view for the notification, including an image button.
            RemoteViews layout = new RemoteViews(getPackageName(), R.layout.notification);
            layout.setTextViewText(R.id.notification_title, getString(R.string.app_name));
            layout.setOnClickPendingIntent(R.id.notification_button,
                    getDialogPendingIntent("Tapped the 'dialog' button in the notification."));
            builder.setContent(layout);

            // Notifications in Android 3.0 now have a standard mechanism for displaying large
            // bitmaps such as contact avatars. Here, we load an example image and resize it to the
            // appropriate size for large bitmaps in notifications.
            Bitmap largeIconTemp = BitmapFactory.decodeResource(res,
                    R.drawable.notification_default_largeicon);
            Bitmap largeIcon = Bitmap.createScaledBitmap(
                    largeIconTemp,
                    res.getDimensionPixelSize(android.R.dimen.notification_large_icon_width),
                    res.getDimensionPixelSize(android.R.dimen.notification_large_icon_height),
                    false);
            largeIconTemp.recycle();

            builder.setLargeIcon(largeIcon);

        } else {
            builder
                    .setNumber(7) // An example number.
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.notification_text));
        }

        notificationManager.notify(NOTIFICATION_DEFAULT, builder.getNotification());
    }

    PendingIntent getDialogPendingIntent(String dialogText) {
        return PendingIntent.getActivity(
                this,
                dialogText.hashCode(), // Otherwise previous PendingIntents with the same
                                       // requestCode may be overwritten.
                new Intent(ACTION_DIALOG)
                        .putExtra(Intent.EXTRA_TEXT, dialogText)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                0);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(1).setTitle(mToggleLabels[mLabelIndex]);
        return true;
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        ActionBar bar = getSupportActionBar();
        int category = bar.getSelectedTab().getPosition();
        outState.putInt("category", category);
        outState.putInt("theme", mThemeId);
    }


    public static class MyDialogFragment extends DialogFragment {

        public static MyDialogFragment newInstance(String title) {
            MyDialogFragment frag = new MyDialogFragment();
            Bundle args = new Bundle();
            args.putString("text", title);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String text = getArguments().getString("text");

            return new AlertDialog.Builder(getActivity())
                    .setTitle("A Dialog of Awesome")
                    .setMessage(text)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            }
                    )
                    .create();
        }
    }
}
