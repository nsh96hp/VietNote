package com.example.nsh96.vietnote;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

public class ServiceAlarm extends Service {
    MediaPlayer mediaPlayer;
    Calendar calendar = Calendar.getInstance();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1,new Notification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int currentAPIVersion = Build.VERSION.SDK_INT;

        //mediaPlayer=MediaPlayer.create(this,R.raw.test);
        String Music=intent.getExtras().getString("Music");
        int day=intent.getExtras().getInt("Day");
        int month=intent.getExtras().getInt("Month");
        int year=intent.getExtras().getInt("Year");
        String date=day+"/"+month+"/"+year;
        if(Music.equals("On")){
            String[] datetime= calendar.getTime().toString().split(" ");
            changeDateTime(datetime);
            String tdate =datetime[2]+"/"+datetime[1]+"/"+datetime[5];
            if(date.equals(tdate)){
                if (currentAPIVersion>= Build.VERSION_CODES.O){
                    Log.e("NotificationChannel","ON");
                    NotificationHelper helper= new NotificationHelper(getApplicationContext());
                    Notification.Builder builder= helper.getNoChannel(getResources().getString(R.string.note),getResources().getString(R.string.noti_note)+" "+day+"/"+month+"/"+year+"!");
                    builder.setAutoCancel(true);
                    helper.getManager().notify(2,builder.build());

                }else {
                    Log.e("Notification","ON");
                    PendingIntent notificIntent=PendingIntent.getActivity(getApplicationContext(),0,new Intent(getApplicationContext(),MainActivity.class),0);

                    NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(R.drawable.ic_notes)
                            .setContentTitle(getResources().getString(R.string.note))
                            .setContentText(getResources().getString(R.string.noti_note)+" "+day+"/"+month+"/"+year+"!")
                            .setTicker(getResources().getText(R.string.note))
                            .setAutoCancel(true);
                    mBuilder.setContentIntent(notificIntent);

                    mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);

                    NotificationManager mNotificationManager=(NotificationManager)getApplicationContext()
                            .getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
                    mNotificationManager.notify(2,mBuilder.build());
                }

            }
        }else {
            if (Music.equals("Off")){
                //mediaPlayer.stop();
                //mediaPlayer.reset();
            }
        }

        return START_NOT_STICKY;
    }
    void changeDateTime(String[] datetime){
        switch (datetime[1]){
            case "Jan":
                datetime[1]="1";
                break;
            case "Feb":
                datetime[1]="2";
                break;
            case "Mar":
                datetime[1]="3";
                break;
            case "Apr":
                datetime[1]="4";
                break;
            case "May":
                datetime[1]="5";
                break;
            case "Jun":
                datetime[1]="6";
                break;
            case "Jul":
                datetime[1]="7";
                break;
            case "Aug":
                datetime[1]="8";
                break;
            case "Sep":
                datetime[1]="9";
                break;
            case "Oct":
                datetime[1]="10";
                break;
            case "Nov":
                datetime[1]="11";
                break;
            case "Dec":
                datetime[1]="12";
                break;
        }

    }

}
