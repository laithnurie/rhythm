package com.laithlab.core.fma;


import java.util.List;

public class Curator {
	public String title;
	public List<Dataset> dataset;

	public class Dataset {
		public String curator_title;
		public String curator_tagline;
	}
}
