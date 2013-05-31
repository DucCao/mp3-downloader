package com.google.android.gcm.demo.app;

import android.os.AsyncTask;

public class DownloadMp3Asynctask extends AsyncTask<String, Void, Void> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    
    @Override
    protected Void doInBackground(String... params) {
        String url = "";
        String fileName = "";
        if (params != null && params.length == 2) {
            url = params[0];
            url = params[1];
        }
        
        CommonUtilities.mp3Download(url, fileName);
        return null;
    }
    
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        
    }
}
