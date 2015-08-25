package com.laithlab.core.echonest;

public class Response {
	private Status status;

	private Songs[] songs;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Songs[] getSongs() {
		return songs;
	}

	public void setSongs(Songs[] songs) {
		this.songs = songs;
	}

	@Override
	public String toString() {
		return "ClassPojo [status = " + status + ", songs = " + songs + "]";
	}

	public String trackImage() {
		for (Songs songs : getSongs()) {
			if (songs.getTrackImage() != null && !songs.getTrackImage().isEmpty()) {
				return songs.getTrackImage();
			}
		}
		return "";
	}
}
