package com.laithlab.rhythm;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.stetho.Stetho;
import com.onesignal.OneSignal;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class RhythmApp extends Application {
    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    private SharedPreferences mSharedPreferences;
    private int currentNightMode;

    public RhythmApp() {
        super();
    }

    public void onCreate() {
        super.onCreate();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        CrashlyticsCore core = new CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG)
                .build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());

        OneSignal.startInit(this).init();
        if (BuildConfig.DEV_MODE) {
            LeakCanary.install(this);
            Stetho.initializeWithDefaults(this);
            Timber.plant(new Timber.DebugTree());
            OneSignal.sendTag("is_test", "true");
        } else {
            Timber.plant(new CrashlyticsTree());
            OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                @Override
                public void idsAvailable(String usernameID, String registrationId) {
                    Crashlytics.setString("usernameID", usernameID);
                    Crashlytics.setString("registrationId", registrationId);
                }
            });
        }
    }

    public String getSelectedTheme() {
        return mSharedPreferences.getString("theme_list", "light");
    }

    public int getCurrentNighMode() {
        return currentNightMode;
    }

    public void setCurrentNightMode(int currentNightMode) {
        this.currentNightMode = currentNightMode;
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
