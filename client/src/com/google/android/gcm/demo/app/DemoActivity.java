/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gcm.demo.app;

import static com.google.android.gcm.demo.app.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.google.android.gcm.demo.app.CommonUtilities.SENDER_ID;
import static com.google.android.gcm.demo.app.CommonUtilities.SERVER_URL;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gcm.GCMRegistrar;

/**
 * Main UI for the demo app.
 */
public class DemoActivity extends Activity {

    AsyncTask<Void, Void, Void> mRegisterTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkNotNull(SERVER_URL, "SERVER_URL");
        checkNotNull(SENDER_ID, "SENDER_ID");
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);
        
        registerReceiver(mHandleMessageReceiver,
                new IntentFilter(DISPLAY_MESSAGE_ACTION));
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, SENDER_ID);
        } else {
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered =
                                ServerUtilities.register(context, regId, "abcdef");
                        // At this point all attempts to register with the app
                        // server failed, so we need to unregister the device
                        // from GCM - the app will try to register again when
                        // it is restarted. Note that GCM will send an
                        // unregistered callback upon completion, but
                        // GCMIntentService.onUnregistered() will ignore it.
                        if (!registered) {
                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }
                };
                mRegisterTask.execute(null, null, null);
            }
        }
    }

    private void startService(String url, String fileName) {
        Intent itent = new Intent(this, DownloadService.class);
        itent.putExtra(CommonUtilities.EXTRA_URLS, url);
        itent.putExtra(CommonUtilities.EXTRA_FILE_NAME, fileName);
        startService(itent);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.options_clear:
        	new Thread() {
        	    public void run() {
        	    }
        	}.start();
        	
                return true;
            case R.id.options_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        unregisterReceiver(mHandleMessageReceiver);
        
        try {
	    GCMRegistrar.onDestroy(this);
	} catch (Exception e) {
	    e.printStackTrace();
	}
        
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        
        super.onDestroy();
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_TEAM_IN_JSON);
        if (newMessage != null) {
        }
    }
    
    private void checkNotNull(Object reference, String name) {
        if (reference == null) {
            throw new NullPointerException(
                    getString(R.string.error_config, name));
        }
    }
    
    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {

	@Override
        public void onReceive(Context context, Intent intent) {
            String data = null;
            if (intent.hasExtra(CommonUtilities.EXTRA_TEAM_IN_JSON)) {
                data = intent.getExtras().getString(CommonUtilities.EXTRA_TEAM_IN_JSON);
        	
                if (!TextUtils.isEmpty(data)) {
                }
            } 
//            else if (intent.hasExtra(CommonUtilities.EXTRA_MESSAGE)) {
//        	newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
//            }
        }
    };
}