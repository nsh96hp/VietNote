package com.example.nsh96.vietnote;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper{
    private static final String CHANNEL_ID="NOTE";
    private static final String CHANNEL_NAME="Notification Note";
    private NotificationManager manager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationHelper(Context base) {
        super(base);
        createChannels();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {
        NotificationChannel noChannel=new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
        noChannel.enableLights(true);
        noChannel.enableVibration(true);
        noChannel.setLightColor(Color.GREEN);
        noChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(noChannel);
    }

    public NotificationManager getManager() {
        if(manager==null)
            manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNoChannel(String title, String content){
        PendingIntent notificIntent=PendingIntent.getActivity(getApplicationContext(),0,new Intent(getApplicationContext(),MainActivity.class),0);

        return new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notes)
                .setContentText(content)
                .setContentTitle(title)
                .setTicker(getResources().getText(R.string.note))
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setContentIntent(notificIntent);



    }
}
