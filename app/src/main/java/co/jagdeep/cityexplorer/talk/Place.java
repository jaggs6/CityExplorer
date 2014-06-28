package co.jagdeep.cityexplorer.talk;

import android.location.Location;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Alicia on 10/05/14.
 */
public class Place {

	private String title;               // The place name
	private String shortDescription;    // Short description of place
	private String longDescription;     // Long description of place
	private Location position;          // The location of the place
	private Marker marker;

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

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}
}
