package com.laithlab.core;

import android.app.Application;
import android.content.res.Resources;
import com.laithlab.core.musicutil.MusicUtility;

public class RhythmCoreApp extends Application {

	public RhythmCoreApp() {
		super();
	}

	public void onCreate() {
		super.onCreate();
		MusicUtility.updateMusicDB(this);
	}

	public Resources resources() {
		return getResources();
	}
}
