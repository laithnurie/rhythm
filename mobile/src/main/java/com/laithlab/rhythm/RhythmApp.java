package com.laithlab.rhythm;

import android.app.Application;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.stetho.Stetho;
import com.onesignal.OneSignal;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class RhythmApp extends Application {

    public RhythmApp() {
        super();
    }

    public void onCreate() {
        super.onCreate();

        CrashlyticsCore core = new CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG)
                .build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());

        if (BuildConfig.DEV_MODE) {
            LeakCanary.install(this);
            Stetho.initializeWithDefaults(this);
            Timber.plant(new Timber.DebugTree());
        }else {
            Timber.plant(new CrashlyticsTree());
            OneSignal.startInit(this).init();
        }
    }

    public class CrashlyticsTree extends Timber.Tree {
        private static final String CRASHLYTICS_KEY_PRIORITY = "priority";
        private static final String CRASHLYTICS_KEY_TAG = "tag";
        private static final String CRASHLYTICS_KEY_MESSAGE = "message";

        @Override
        protected void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
                return;
            }

            Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority);
            Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag);
            Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message);

            if (t == null) {
                Crashlytics.logException(new Exception(message));
            } else {
                Crashlytics.logException(t);
            }
        }
    }
}
