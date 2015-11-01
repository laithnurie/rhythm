package com.laithlab.core;

import android.app.Application;
import android.content.res.Resources;
import com.laithlab.core.utils.MusicDataUtility;
import com.squareup.leakcanary.LeakCanary;

public class RhythmCoreApp extends Application {

	public RhythmCoreApp() {
		super();
	}

	public void onCreate() {
		super.onCreate();
		LeakCanary.install(this);
		new Thread(new Runnable() {
			public void run() {
				MusicDataUtility.updateMusicDB(RhythmCoreApp.this);
			}
		}).start();
	}

	public Resources resources() {
		return getResources();
	}
}
