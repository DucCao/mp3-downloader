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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

/**
 * Main UI for the demo app.
 */
public class DemoActivity extends Activity {

    AsyncTask<Void, Void, Void> mRegisterTask;
    private ArrayList<String> listRemovedMembers = new ArrayList<String>();

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
                                ServerUtilities.register(context, regId);
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
        
        if (getIntent().hasExtra(CommonUtilities.EXTRA_TEAM_IN_JSON)) {
            Log.e("TEST", "onCreate");
            String newMessage = getIntent().getExtras().getString(CommonUtilities.EXTRA_TEAM_IN_JSON);
            if (newMessage != null) {
        	displayTeams(newMessage);
            }
        } else {
            LinearLayout mainLayout = new LinearLayout(this);
            mainLayout.setOrientation(LinearLayout.VERTICAL);

            final Button button = new Button(this);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.weight = 1f;
            button.setLayoutParams(params);
            button.setTextSize(30f);
            button.setTextColor(Color.GREEN);
            button.setText("UPDATE TEAMS");
            button.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        	    new Thread() {
        		public void run() {
        		    updateTeams();
        		}
        	    }.start();
        	}
            });
            mainLayout.addView(button);

            setContentView(mainLayout);
        }
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
            /*
             * Typically, an application registers automatically, so options
             * below are disabled. Uncomment them if you want to manually
             * register or unregister the device (you will also need to
             * uncomment the equivalent options on options_menu.xml).
             */
            /*
            case R.id.options_register:
                GCMRegistrar.register(this, SENDER_ID);
                return true;
            case R.id.options_unregister:
                GCMRegistrar.unregister(this);
                return true;
             */
            case R.id.options_clear:
        	new Thread() {
        	    public void run() {
        		updateTeams();
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
        Log.e("TEST", "newIntent");
        String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_TEAM_IN_JSON);
        if (newMessage != null) {
            displayTeams(newMessage);
        }
    }
    
    private void updateTeams() {
	HttpClient client = new DefaultHttpClient();
        URI website;
	try {
	    String url = "";
	    for (String removedMembers : listRemovedMembers) {
		url += removedMembers + ",";
	    }
	    website = new URI(CommonUtilities.SERVER_URL + "/sendAll?remove_members=" + url);
	    HttpGet request = new HttpGet();
	    request.setURI(website);
	    HttpResponse response = client.execute(request);
	    response.getStatusLine().getStatusCode();
	} catch (Exception e) {
	    e.printStackTrace();
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
        	    displayTeams(data);
        	}
            } 
//            else if (intent.hasExtra(CommonUtilities.EXTRA_MESSAGE)) {
//        	newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
//            }
        }
    };
    
    private void displayTeams(String teamInJson) {
	Log.e("TEST", "displayTeams: " + teamInJson);
	
	LinearLayout mainLayout = new LinearLayout(this);
	mainLayout.setOrientation(LinearLayout.VERTICAL);
	
	try {
	    JSONArray array = new JSONArray(teamInJson);
	    for (int i = 0; i < array.length(); ++i) {
		try {
		    JSONArray team = (JSONArray) array.get(i);
		    mainLayout.addView(getTeamView(team));
		} catch (JSONException e) {
		    e.printStackTrace();
		}
	    }
	} catch (JSONException e) {
	    e.printStackTrace();
	}
	
	final Button button = new Button(this);
	LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	params.weight = 1f;
	button.setLayoutParams(params);
	button.setTextSize(30f);
	button.setTextColor(Color.GREEN);
	button.setText("UPDATE TEAMS");
	button.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		new Thread() {
        	    public void run() {
        		updateTeams();
        	    }
        	}.start();
	    }
	});
	mainLayout.addView(button);
	
	setContentView(mainLayout);
    }
    
    private LinearLayout getTeamView(JSONArray array) {
	LinearLayout teamView = new LinearLayout(this);
	
	for (int i = 0; i < array.length(); ++i) {
	    try {
		String memberName = (String) array.get(i);
		
		final Button button = new Button(this);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.weight = 1f;
		button.setLayoutParams(params);
		button.setTextSize(20f);
		button.setTextColor(Color.RED);
		button.setText(memberName);
		button.setOnClickListener(new OnClickListener() {
		    private boolean isRemoved = false;
		    
		    @Override
		    public void onClick(View v) {
			isRemoved = !isRemoved;
			
			if (isRemoved) {
			    listRemovedMembers.add(button.getText().toString());
			    button.setTextColor(Color.WHITE);
			} else {
			    listRemovedMembers.remove(button.getText().toString());
			    button.setTextColor(Color.RED);
			}
		    }
		});
		
		teamView.addView(button);
	    } catch (JSONException e) {
		e.printStackTrace();
	    }
	}
	
	return teamView;
    }

}