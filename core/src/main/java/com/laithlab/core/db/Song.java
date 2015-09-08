package com.laithlab.core.db;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class Song extends RealmObject {
	private String songTitle;
	private String songImageUrl;
	private float songDuration;
	private String songLocation;
	private String albumId;

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

	public void setSongDuration(float songDuration) {
		this.songDuration = songDuration;
	}

	public float getSongDuration() {
		return songDuration;
	}

	public void setSongLocation(String songLocation) {
		this.songLocation = songLocation;
	}

	public String getSongLocation() {
		return songLocation;
	}

	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}

	public String getAlbumId() {
		return albumId;
	}
}
