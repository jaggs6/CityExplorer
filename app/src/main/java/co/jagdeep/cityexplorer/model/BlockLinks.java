package co.jagdeep.cityexplorer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BlockLinks {
	@JsonProperty("@id")
	public String id;

	@JsonProperty("@latitude")
	public String latitude;

	@JsonProperty("@longitude")
	public String longitude;

	@JsonProperty("@title")
	public String title;

}
