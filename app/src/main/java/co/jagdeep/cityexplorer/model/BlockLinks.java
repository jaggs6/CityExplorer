package co.jagdeep.cityexplorer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BlockLinks {
	@JsonProperty("@tag")
	public String tag;

	@JsonProperty("@id")
	public String id;

	@JsonProperty("@parent")
	public String parent;

	@JsonProperty("@categories")
	public String categories;

	@JsonProperty("@role")
	public String role;

	@JsonProperty("@type")
	public String type;

	@JsonProperty("@latitude")
	public String latitude;

	@JsonProperty("@longitude")
	public String longitude;

	@JsonProperty("@title")
	public String title;

}
