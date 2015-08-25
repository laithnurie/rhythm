package com.laithlab.core.echonest;


public class Tracks {
	private String id;

	private String foreign_release_id;

	private String foreign_id;

	private String catalog;

	private String preview_url;

	private String release_image;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getForeign_release_id() {
		return foreign_release_id;
	}

	public void setForeign_release_id(String foreign_release_id) {
		this.foreign_release_id = foreign_release_id;
	}

	public String getForeign_id() {
		return foreign_id;
	}

	public void setForeign_id(String foreign_id) {
		this.foreign_id = foreign_id;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String getPreview_url() {
		return preview_url;
	}

	public void setPreview_url(String preview_url) {
		this.preview_url = preview_url;
	}

	public String getRelease_image() {
		return release_image;
	}

	public void setRelease_image(String release_image) {
		this.release_image = release_image;
	}

	@Override
	public String toString() {
		return "ClassPojo [id = " + id + ", foreign_release_id = " + foreign_release_id + ", foreign_id = " + foreign_id + ", catalog = " + catalog + ", preview_url = " + preview_url + ", release_image = " + release_image + "]";
	}
}

