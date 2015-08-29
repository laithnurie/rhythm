package com.laithlab.core.db;

import io.realm.RealmObject;

public class Song extends RealmObject {
	private String songTitle;
	private String songImageUrl;
	private int songDuration;
	private String songLocation;

	public void setSongTitle(String songTitle) {
		this.songTitle = songTitle;
	}

	public String getSongTitle() {
		return songTitle;
	}

	public void setSongImageUrl(String songImageUrl) {
		this.songImageUrl = songImageUrl;
	}

	public String getSongImageUrl() {
		return songImageUrl;
	}

	public void setSongDuration(int songDuration) {
		this.songDuration = songDuration;
	}

	public int getSongDuration() {
		return songDuration;
	}

	public void setSongLocation(String songLocation) {
		this.songLocation = songLocation;
	}

	public String getSongLocation() {
		return songLocation;
	}
}
