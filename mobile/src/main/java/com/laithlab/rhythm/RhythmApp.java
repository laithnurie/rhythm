package com.laithlab.rhythm;

import android.app.Application;
import android.content.res.Resources;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;

public class RhythmApp extends Application {

    public RhythmApp() {
        super();
    }

    public void onCreate() {
        super.onCreate();

        if(BuildConfig.DEV_MODE){
            LeakCanary.install(this);
            Stetho.initializeWithDefaults(this);
        } else {
            Fabric.with(this, new Crashlytics());
        }
    }

    public Resources resources() {
        return getResources();
    }
}
