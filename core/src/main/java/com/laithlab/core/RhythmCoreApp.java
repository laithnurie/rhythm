package com.laithlab.core;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

public class RhythmCoreApp extends Application {

	public RhythmCoreApp() {
		super();
	}

	public void onCreate() {
		super.onCreate();
		LeakCanary.install(this);
		Stetho.initializeWithDefaults(this);
	}

	public Resources resources() {
		return getResources();
	}
}
