package com.example.nsh96.vietnote;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        String music=intent.getExtras().getString(VietNote.KEY_MUSIC);

        int day=intent.getExtras().getInt(VietNote.KEY_DAY);
        int month=intent.getExtras().getInt(VietNote.KEY_MONTH);
        int year=intent.getExtras().getInt(VietNote.KEY_YEAR);

        //CreateNotification(context,"NOTE","Content Note","Alert");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent myIntent=new Intent(context, ServiceAlarm.class);
            myIntent.putExtra(VietNote.KEY_MUSIC,music);
            myIntent.putExtra(VietNote.KEY_DAY,day);
            myIntent.putExtra(VietNote.KEY_MONTH,month);
            myIntent.putExtra(VietNote.KEY_YEAR,year);
            context.startForegroundService(myIntent);
        } else {
            Intent myIntent=new Intent(context, ServiceAlarm.class);
            myIntent.putExtra(VietNote.KEY_MUSIC,music);
            myIntent.putExtra(VietNote.KEY_DAY,day);
            myIntent.putExtra(VietNote.KEY_MONTH,month);
            myIntent.putExtra(VietNote.KEY_YEAR,year);
            context.startService(myIntent);
        }
    }

}
