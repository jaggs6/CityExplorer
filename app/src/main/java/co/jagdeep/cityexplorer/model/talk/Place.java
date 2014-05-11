package co.jagdeep.cityexplorer.model.talk;

import android.location.Location;

/**
 * Created by Alicia on 10/05/14.
 */
public class Place {

	private String title;               // The place name
	private String shortDescription;    // Short description of place
	private String longDescription;     // Long description of place
	private Location position;          // The location of the place
	private String markerID;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public Location getPosition() {
		return position;
	}

	public void setPosition(Location position) {
		this.position = position;
	}

	public String getMarkerID() {
		return markerID;
	}

	public void setMarkerID(String markerID) {
		this.markerID = markerID;
	}
}
