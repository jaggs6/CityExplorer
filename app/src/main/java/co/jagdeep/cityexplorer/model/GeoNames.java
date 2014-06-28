package co.jagdeep.cityexplorer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GeoNames {
	@JsonProperty("geonames")
	public GeoName[] geoNamesArray;
}
