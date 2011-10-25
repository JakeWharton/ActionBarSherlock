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

package com.actionbarsherlock.sample.demos.content;


import com.actionbarsherlock.sample.demos.R;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Demonstrates the use of a LocalBroadcastManager to easily communicate
 * data from a service to any other interested code.
 */
public class LocalServiceBroadcaster extends Activity {
    static final String ACTION_STARTED = "com.actionbarsherlock.sample.demos.STARTED";
    static final String ACTION_UPDATE = "com.actionbarsherlock.sample.demos.UPDATE";
    static final String ACTION_STOPPED = "com.actionbarsherlock.sample.demos.STOPPED";

    LocalBroadcastManager mLocalBroadcastManager;
    BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.local_service_broadcaster);

        // This is where we print the data we get back.
        final TextView callbackData = (TextView)findViewById(R.id.callback);

        // Put in some initial text.
        callbackData.setText("No broadcast received yet");

        // We use this to send broadcasts within our local process.
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        // We are going to watch for interesting local broadcasts.
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_STARTED);
        filter.addAction(ACTION_UPDATE);
        filter.addAction(ACTION_STOPPED);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ACTION_STARTED)) {
                    callbackData.setText("STARTED");
                } else if (intent.getAction().equals(ACTION_UPDATE)) {
                    callbackData.setText("Got update: " + intent.getIntExtra("value", 0));
                } else if (intent.getAction().equals(ACTION_STOPPED)) {
                    callbackData.setText("STOPPED");
                }
            }
        };
        mLocalBroadcastManager.registerReceiver(mReceiver, filter);

        // Watch for button clicks.
        Button button = (Button)findViewById(R.id.start);
        button.setOnClickListener(mStartListener);
        button = (Button)findViewById(R.id.stop);
        button.setOnClickListener(mStopListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mReceiver);
    }

    private OnClickListener mStartListener = new OnClickListener() {
        public void onClick(View v) {
            startService(new Intent(LocalServiceBroadcaster.this, LocalService.class));
        }
    };

    private OnClickListener mStopListener = new OnClickListener() {
        public void onClick(View v) {
            stopService(new Intent(LocalServiceBroadcaster.this, LocalService.class));
        }
    };

    public static class LocalService extends Service {
        LocalBroadcastManager mLocalBroadcastManager;
        int mCurUpdate;

        static final int MSG_UPDATE = 1;

        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_UPDATE: {
                        mCurUpdate++;
                        Intent intent = new Intent(ACTION_UPDATE);
                        intent.putExtra("value", mCurUpdate);
                        mLocalBroadcastManager.sendBroadcast(intent);
                        Message nmsg = mHandler.obtainMessage(MSG_UPDATE);
                        mHandler.sendMessageDelayed(nmsg, 1000);
                    } break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };

        @Override
        public void onCreate() {
            super.onCreate();
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            // Tell any local interested parties about the start.
            mLocalBroadcastManager.sendBroadcast(new Intent(ACTION_STARTED));

            // Prepare to do update reports.
            mHandler.removeMessages(MSG_UPDATE);
            Message msg = mHandler.obtainMessage(MSG_UPDATE);
            mHandler.sendMessageDelayed(msg, 1000);
            return Service.START_STICKY;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            // Tell any local interested parties about the stop.
            mLocalBroadcastManager.sendBroadcast(new Intent(ACTION_STOPPED));

            // Stop doing updates.
            mHandler.removeMessages(MSG_UPDATE);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}
