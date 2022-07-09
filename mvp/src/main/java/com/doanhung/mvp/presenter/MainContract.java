package com.doanhung.mvp.presenter;

import android.app.Application;

import com.doanhung.mvp.model.InfoApp;

import java.util.List;

public interface MainContract {

    interface MainView {

        void submitInfoAppList(List<InfoApp> infoAppList);

        void submitLongestUsageTimeApp(InfoApp infoApp);

        void triggerLoadingInfoAppList(boolean isLoading);
        void triggerLoadingLongestTimeApp(boolean isLoading);
    }

    interface MainPresenter {

        void onCreate(MainView mainView);

        void getInfoAppListAndLongestUsageTimeApp(Application application);

    }
}
