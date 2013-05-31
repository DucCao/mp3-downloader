package com.google.android.gcm.demo.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

public class DownloadService extends Service {

    SharedPreferences preferences;

   private static final String DOCUMENT_VIEW_STATE_PREFERENCES = "DjvuDocumentViewState";

     private Looper mServiceLooper;
     private ServiceHandler mServiceHandler;
     private NotificationManager mNotificationManager;
     private String downloadUrl, fileName;
     public static boolean serviceState;

     // Handler that receives messages from the thread
     private final class ServiceHandler extends Handler {
         public ServiceHandler(Looper looper) {
             super(looper);
         }
         @Override
         public void handleMessage(Message msg) {
             downloadFile();
             showNotification(getResources().getString(R.string.app_name),"VVS");
             stopSelf(msg.arg1);
         }
     }


     @Override
     public void onCreate() {
         serviceState = true;
         mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
         HandlerThread thread = new HandlerThread("ServiceStartArguments", 1);
         thread.start();

         // Get the HandlerThread's Looper and use it for our Handler 
         mServiceLooper = thread.getLooper();
         mServiceHandler = new ServiceHandler(mServiceLooper);
     }



     @Override
     public int onStartCommand(Intent intent, int flags, int startId) {
         android.util.Log.d("TEST","onStartCommand");  

         Bundle extra = intent.getExtras();
         if(extra != null){
             this.downloadUrl = extra.getString(CommonUtilities.EXTRA_URL);
             this.fileName = extra.getString(CommonUtilities.EXTRA_FILE_NAME);
         }

         Message msg = mServiceHandler.obtainMessage();
         msg.arg1 = startId;
         mServiceHandler.sendMessage(msg);
         // If we get killed, after returning from here, restart
         return START_STICKY;
     }

     @Override
     public void onDestroy() {
         serviceState=false;
     }


     @Override
     public IBinder onBind(Intent intent) {
         // We don't provide binding, so return null
         return null;
     }


     public void downloadFile() {
         CommonUtilities.mp3Download(this.downloadUrl, this.fileName);
     }

     private void showNotification(String message, String title) {
         // In this sample, we'll use the same text for the ticker and the expanded notification
         CharSequence text = message;

         // Set the icon, scrolling text and timestamp
         Notification notification = new Notification(ProgressDialog.STYLE_SPINNER, "vvs",
                 System.currentTimeMillis());
         notification.flags |= Notification.FLAG_AUTO_CANCEL;
         Intent intent = new Intent(this, DemoActivity.class);
         intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
         // The PendingIntent to launch our activity if the user selects this notification
         PendingIntent contentIntent = PendingIntent.getActivity(this.getBaseContext(), 0,
                 intent, PendingIntent.FLAG_CANCEL_CURRENT);

         // Set the info for the views that show in the notification panel.
         notification.setLatestEventInfo(this, title,
                 text, contentIntent);
         // Send the notification.
         // We use a layout id because it is a unique number.  We use it later to cancel.
         mNotificationManager.notify(R.string.notification_id, notification);
     }
}