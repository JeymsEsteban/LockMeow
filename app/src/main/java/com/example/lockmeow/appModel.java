package com.example.lockmeow;

import android.graphics.drawable.Drawable;

public class appModel {
    String appName;
    Drawable appIcon;
    int appStatus; // 0 -> app funciona, 1 -> app no funciona
    String namePackage;

    public appModel(String appName, Drawable appIcon, int appStatus, String namePackage ) {
        this.appName = appName;
        this.appIcon = appIcon;
        this.appStatus = appStatus;
        this.namePackage = namePackage;
    }

    public String getappName () {
        return appName;
    }

    public Drawable getappIcon () {
        return appIcon;
    }

    public int getappStatus () {
        return appStatus;
    }

    public String getnamePackage () {
        return namePackage;
    }
}
