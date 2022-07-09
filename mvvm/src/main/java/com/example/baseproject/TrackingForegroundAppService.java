package com.example.baseproject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.baseproject._util.Util;

import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

public class TrackingForegroundAppService extends Service {

    private static final String USAGE_STATS_SERVICE = "usagestats";

    private static final int NOTIFY_ID = 1;
    private String currentPackageName = "";

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            new Thread(this::getForegroundApp).start();
        }
        return START_STICKY;
    }

    private void createNotify(String processName) {


        Notification notification =
                new NotificationCompat.Builder(this, App.NOTIFICATION_CHANNEL_ID)
                        .setContentTitle("Current Foreground App")
                        .setContentText(processName)
                        .setSmallIcon(R.drawable.ic_android_green_24dp)
                        .setAutoCancel(false)
                        .build();

        startForeground(NOTIFY_ID, notification);

    }

    public void getForegroundApp() {
        String tempPackageName = "";
        @SuppressLint("WrongConstant")
        UsageStatsManager usm = (UsageStatsManager) this.getSystemService(USAGE_STATS_SERVICE);
        List<UsageStats> appList = usm.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                System.currentTimeMillis() - 1000 * 60,
                System.currentTimeMillis()
        );

        if (appList != null && appList.size() > 0) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : appList) {
                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (!mySortedMap.isEmpty()) {
                tempPackageName = Objects.requireNonNull(mySortedMap.get(mySortedMap.lastKey())).getPackageName();
            }

        }

        if (!tempPackageName.equals(currentPackageName)) {
            currentPackageName = tempPackageName;
            String appName = Util.getAppNameByPackageName(this, currentPackageName);
            mHandler.post(() -> {
                createNotify(appName);
                Toast.makeText(getApplicationContext(), appName, Toast.LENGTH_LONG).show();
            });

        }
        if (mHandler != null) {
            mHandler.postDelayed(this::getForegroundApp, 1000);
        }
    }

    @Override
    public void onDestroy() {
        mHandler = null;
        super.onDestroy();
    }
}
