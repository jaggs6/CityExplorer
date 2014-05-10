package co.jagdeep.cityexplorer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoriesContent {
	@JsonProperty("category")
	public Category[] categoriesArray;
}
