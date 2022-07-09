package com.doanhung.mvp._util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.doanhung.mvp.R;


public class Util {

    @SuppressLint("UseCompatLoadingForDrawables")
    public static Drawable getAppIconByPackageName(@NonNull Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return context.getDrawable(R.drawable.ic_android_green_24dp);
    }

    public static String getAppNameByPackageName(@NonNull Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;

        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (applicationInfo == null) {
            return "Unknown";
        } else {
            return (String) packageManager.getApplicationLabel(applicationInfo);
        }

    }
}
