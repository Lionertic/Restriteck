package com.restri_tech;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.restri_tech.DB.Package;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class BackCheck extends Service {

    Handler mHandler = new Handler();
    Runnable mRunnable;
    public static String CHANNEL_ID = "Check";
    private UsageStatsManager mUsageStatsManager;
    private String usageStatsList ;
    private List<Package> users ;
    private Package p = new Package();
    private ActivityManager activityManager;
    private long t;
    private int ik;
    boolean  c=true;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "LocationService",
                    NotificationManager.IMPORTANCE_MIN
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            createNotification();
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP | FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT );

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_block)
                    .setContentTitle("Blocking")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();

            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        saveUserLocation();
        return START_NOT_STICKY;
    }
    private void saveUserLocation() {
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                check();
                if(c)
                    mHandler.postDelayed(mRunnable, 1000);
            }
        }, 2000);
    }
    void check(){
        mUsageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        SharedPreferences sd=getApplicationContext().getSharedPreferences("Worker",Context.MODE_PRIVATE);
        usageStatsList = getUsageStatistics(UsageStatsManager.INTERVAL_DAILY);
        users = HomeActivity.myAppDatabase.myDao().getApps();
        t = HomeActivity.myAppDatabase.myDao().getRTime(usageStatsList);
        for (ik = 0; ik < users.size(); ik++)
            if (usageStatsList.equals(users.get(ik).getName()) && t <= 0) {
                sd.edit().putBoolean("Toast",true).commit();
                getApplicationContext().startActivity(i);
                activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                activityManager.killBackgroundProcesses(usageStatsList);
            }
            else if (usageStatsList.equals(users.get(ik).getName()) && t > 0)
                break;
        p.setName(usageStatsList);
        p.setRtime(t - 960);
        p.setFtime(HomeActivity.myAppDatabase.myDao().getFTime(usageStatsList));
        HomeActivity.myAppDatabase.myDao().setTime(p);
    }
    private String getUsageStatistics(int intervalType) {
        // Get the app statistics since one year ago from the current time.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -1);

        List<UsageStats> queryUsageStats = mUsageStatsManager.queryUsageStats(intervalType, cal.getTimeInMillis(), System.currentTimeMillis());
        Collections.sort(queryUsageStats, new LastTimeLaunchedComparatorDesc());
        return queryUsageStats.get(0).getPackageName();
    }
    private static class LastTimeLaunchedComparatorDesc implements Comparator<UsageStats> {
        @Override
        public int compare(UsageStats left, UsageStats right) {
            return Long.compare(right.getLastTimeUsed(), left.getLastTimeUsed());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        c=false;
        mHandler.removeCallbacks(mRunnable);
    }
}
