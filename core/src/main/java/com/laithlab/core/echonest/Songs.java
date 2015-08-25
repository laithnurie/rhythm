package com.laithlab.core.echonest;

public class Songs {
	private String id;

	private String title;

	private String artist_name;

	private String artist_id;

	private Tracks[] tracks;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist_name() {
		return artist_name;
	}

	public void setArtist_name(String artist_name) {
		this.artist_name = artist_name;
	}


	public String getArtist_id() {
		return artist_id;
	}

	public void setArtist_id(String artist_id) {
		this.artist_id = artist_id;
	}

	public Tracks[] getTracks() {
		return tracks;
	}

	public void setTracks(Tracks[] tracks) {
		this.tracks = tracks;
	}

	@Override
	public String toString() {
		return "ClassPojo [id = " + id + ", title = " + title + ", artist_name = " + artist_name + ", artist_id = " + artist_id + ", tracks = " + tracks + "]";
	}

	public String getTrackImage() {
		for (Tracks tracks : getTracks()) {
			if (tracks.getRelease_image() != null && !tracks.getRelease_image().isEmpty()) {
				return tracks.getRelease_image();
			}
		}
		return "";
	}
}
