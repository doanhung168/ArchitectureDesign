package com.doanhung.mvp.presenter;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.doanhung.mvp._util.Util;
import com.doanhung.mvp.model.InfoApp;

import java.util.ArrayList;
import java.util.List;

public class MainPresenter implements MainContract.MainPresenter {

    private static final String TAG = "MainPresenter";
    private static final String USAGE_STATS_SERVICE = "usagestats";

    private MainContract.MainView mMainView;

    private List<InfoApp> mInfoAppList;
    private InfoApp mLongestUsageTimeApp;

    public List<InfoApp> getInfoAppList() {
        return mInfoAppList;
    }

    public Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(MainContract.MainView mainView) {
        mMainView = mainView;
    }

    @Override
    public void getInfoAppListAndLongestUsageTimeApp(Application application) {

        new Thread(() -> {

            Log.i(TAG, "main Thread run: " + Thread.currentThread().getName());
            handler.post(() -> mMainView.triggerLoadingInfoAppList(true));

            mInfoAppList = getInfoApps(application);

            handler.post(() -> {
                Log.i(TAG, "post thread : " + Thread.currentThread().getName());
                mMainView.submitInfoAppList(mInfoAppList);
                mMainView.triggerLoadingInfoAppList(false);
                mMainView.triggerLoadingLongestTimeApp(true);
            });

            mLongestUsageTimeApp = getLongestUsageTimeApp(mInfoAppList);

            handler.post(() -> {
                mMainView.submitLongestUsageTimeApp(mLongestUsageTimeApp);
                mMainView.triggerLoadingLongestTimeApp(false);
            });
        }).start();
    }

    @NonNull
    private List<InfoApp> getInfoApps(Application application) {

        List<InfoApp> infoApps = new ArrayList<>();

        @SuppressLint("WrongConstant")
        UsageStatsManager usageStatsManager =
                (UsageStatsManager) application.getSystemService(USAGE_STATS_SERVICE);
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST,
                System.currentTimeMillis() - (1000 * 60 * 60 * 24),
                System.currentTimeMillis()
        );

        for (UsageStats usageStats : usageStatsList) {

            if (usageStats.getTotalTimeInForeground() > 0) {
                String packageName = usageStats.getPackageName();
                String appName = Util.getAppNameByPackageName(application, packageName);
                Drawable appIcon = Util.getAppIconByPackageName(application, packageName);
                long appUsageTime = usageStats.getTotalTimeInForeground();

                InfoApp app = new InfoApp(packageName, appName, appIcon, appUsageTime);
                infoApps.add(app);
            }
        }

        return infoApps;
    }

    private InfoApp getLongestUsageTimeApp(List<InfoApp> infoApps) {

        if (infoApps == null || infoApps.isEmpty()) {
            return null;
        }

        InfoApp longestUsageTimeApp = infoApps.get(0);
        for (InfoApp infoApp : infoApps) {
            if (infoApp.getAppUsageTime() > longestUsageTimeApp.getAppUsageTime()) {
                longestUsageTimeApp = infoApp;
            }
        }
        return longestUsageTimeApp;
    }
}
