package com.laithlab.core;

import android.app.Application;
import com.laithlab.core.musicutil.MusicUtility;

public class RhythmCoreApp extends Application {

	public RhythmCoreApp() {
		super();
	}

	public void onCreate() {
		super.onCreate();
		MusicUtility.updateMusicDB(this);
	}
}
