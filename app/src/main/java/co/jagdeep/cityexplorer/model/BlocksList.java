package co.jagdeep.cityexplorer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BlocksList {
	@JsonProperty("link")
	public BlockLinks[] blockLinks;
}
