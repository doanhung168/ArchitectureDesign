package com.doanhung.mvp.model;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

public class InfoApp {

    private String mId;
    private String mAppName;
    private Drawable mAppIcon;
    private long mAppUsageTime;

    public InfoApp() {
    }

    public InfoApp(String id, String appName, Drawable appIcon, long appUsageTime) {
        this.mId = id;
        this.mAppName = appName;
        this.mAppIcon = appIcon;
        this.mAppUsageTime = appUsageTime;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = mId;
    }

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String appName) {
        this.mAppName = appName;
    }

    public Drawable getAppIcon() {
        return mAppIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.mAppIcon = appIcon;
    }

    public long getAppUsageTime() {
        return mAppUsageTime;
    }

    public void setAppUsageTime(long appUsageTime) {
        this.mAppUsageTime = appUsageTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfoApp infoApp = (InfoApp) o;
        return Objects.equals(mAppName, infoApp.mAppName) && Objects.equals(mAppIcon, infoApp.mAppIcon);
    }


    public static DiffUtil.ItemCallback<InfoApp> diffCallback = new DiffUtil.ItemCallback<InfoApp>() {
        @Override
        public boolean areItemsTheSame(@NonNull InfoApp oldItem, @NonNull InfoApp newItem) {
            return oldItem.mId.equals(newItem.mId);
        }

        @Override
        public boolean areContentsTheSame(@NonNull InfoApp oldItem, @NonNull InfoApp newItem) {
            return oldItem.equals(newItem);
        }
    };
}
