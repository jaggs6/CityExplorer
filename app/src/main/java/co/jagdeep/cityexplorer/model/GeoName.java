package co.jagdeep.cityexplorer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoName {
	public String countryCode;
	public String distance;
	public int elevation;
	public String feature;
	public int geoNameId;
	public String lang;
	public double lat;
	public double lng;
	public int rank;
	public String summary;
	public String thumbnailImg;
	public String title;
	public String wikipediaUrl;
}
