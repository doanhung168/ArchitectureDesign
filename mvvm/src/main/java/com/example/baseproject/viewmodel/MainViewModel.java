package com.example.baseproject.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.baseproject._util.Util;
import com.example.baseproject.model.InfoApp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainViewModel extends ViewModel {

    private static final String USAGE_STATS_SERVICE = "usagestats";

    private final Application mApplication;

    private final MutableLiveData<List<InfoApp>> mInfoAppList = new MutableLiveData<>();
    private final MutableLiveData<InfoApp> mLongestUsageTimeApp = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mIsLoadingAppList = new MutableLiveData<>(false);


    public MainViewModel(Application application) {
        mApplication = application;
    }

    public LiveData<List<InfoApp>> getInfoAppList() {
        return mInfoAppList;
    }

    public LiveData<InfoApp> getLongestUsageTimeApp() {
        return mLongestUsageTimeApp;
    }

    public LiveData<Boolean> isLoadingInfoAppList() {
        return mIsLoadingAppList;
    }

    public void requestInfoAppList() {
        new Thread(() -> {
            mIsLoadingAppList.postValue(true);

            List<InfoApp> infoApps = getInfoApps();
            mInfoAppList.postValue(infoApps);

            InfoApp longestUsageTimeApp = getLongestUsageTimeApp(infoApps);
            mLongestUsageTimeApp.postValue(longestUsageTimeApp);

            mIsLoadingAppList.postValue(false);

        }).start();
    }

    @NonNull
    private List<InfoApp> getInfoApps() {

        List<InfoApp> infoApps = new ArrayList<>();

        @SuppressLint("WrongConstant")
        UsageStatsManager usm =
                (UsageStatsManager) mApplication.getSystemService(USAGE_STATS_SERVICE);

        List<UsageStats> usageStatsList = usm.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                System.currentTimeMillis() - (1000 * 60 * 60 * 24),
                System.currentTimeMillis()
        );

        for (UsageStats usageStats : usageStatsList) {

            if(usageStats.getTotalTimeInForeground() > 0) {
                String packageName = usageStats.getPackageName();

                String appName = Util.getAppNameByPackageName(mApplication, packageName);
                Drawable appIcon = Util.getAppIconByPackageName(mApplication, packageName);
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


    public static class MainViewModelFactory implements ViewModelProvider.Factory {

        private final Application mApplication;

        public MainViewModelFactory(Application application) {
            mApplication = application;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(MainViewModel.class)) {
                return (T) new MainViewModel(mApplication);
            }
            throw new IllegalStateException("unEnabled constructor");
        }
    }

}
