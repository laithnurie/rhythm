package com.laithlab.core.fragment;

import android.support.v7.graphics.Palette;

public interface SongFragmentListener {
	void changePlayerStyle(Palette.Swatch vibrantColor);
	void setToolBarText(String artistTitle, String albumTitle);
}
