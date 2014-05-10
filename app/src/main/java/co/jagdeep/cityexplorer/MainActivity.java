package co.jagdeep.cityexplorer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import co.jagdeep.cityexplorer.model.Categories;
import co.jagdeep.cityexplorer.model.Category;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.spothero.volley.JacksonNetwork;
import com.spothero.volley.JacksonRequest;
import com.spothero.volley.JacksonRequestListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.app.ActionBar.OnNavigationListener;


public class MainActivity extends Activity implements OnNavigationListener {

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private static final String TAG = "CityExplorerLog";
	private GoogleMap mMap;
	MapFragment mMapFragment;
	private String currentCity = "";
	RequestQueue mRequestQueue;
	String cities[] = {
			"london", "barcelona", "berlin", "newyork", "paris", "prague", "rome", "venice", "washington"
	};
	List citiesList = Arrays.asList(cities);
	private ActionBar actionBar;
	private MainActivity thisObj;
	private String[] categoriesArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		thisObj = this;
		mRequestQueue = JacksonNetwork.newRequestQueue(this);

		setContentView(R.layout.activity_main);
		if (mMapFragment == null) {

			GoogleMapOptions options = new GoogleMapOptions();
			options.mapType(GoogleMap.MAP_TYPE_NORMAL)
					.compassEnabled(true)
					.rotateGesturesEnabled(false)
					.tiltGesturesEnabled(false);

			mMapFragment = MapFragment.newInstance(options);
			FragmentTransaction fragmentTransaction =
					getFragmentManager().beginTransaction();
			fragmentTransaction.replace(R.id.container, mMapFragment);
			fragmentTransaction.commit();
		}

		setupActionBar();
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			mMap = mMapFragment.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				// The Map is verified. It is now safe to manipulate the map.
				mMap.setMyLocationEnabled(true);
				mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
					@Override
					public void onMyLocationChange(Location location) {
						if (currentCity.isEmpty()) {
							(new GetAddressTask(getApplicationContext())).execute(location);
							LatLng myLocation = new LatLng(location.getLatitude(),
									location.getLongitude());
							mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
									15.0f));
						}
					}
				});
			}
		}
	}

	private void getPOI(String s) {

	}

	private void getCategories() {
		mRequestQueue.add(new JacksonRequest<Categories>(Request.Method.GET,
						"https://api.pearson.com/eyewitness/" + currentCity + "/categories.json",
						new JacksonRequestListener<Categories>() {
							@Override
							public void onResponse(Categories response, int statusCode, VolleyError error) {
								if (response != null) {
									responseToCategoriesArray(response);
									setCategoriesIntoActionBar();
								} else {
									Log.e(TAG, "An error occurred while parsing the data! Stack trace follows:");
									error.printStackTrace();
								}
							}

							@Override
							public JavaType getReturnType() {
								return SimpleType.construct(Categories.class);
							}
						}
				)
		);
	}

	private void responseToCategoriesArray(Categories response) {
		List<String> categories = new ArrayList<String>(response.categoriesContent.categoriesArray
				.length);
		for (Category category : response.categoriesContent.categoriesArray) {
			categories.add(category.category.replace("_", " "));
		}
		categoriesArray = new String[categories.size()];
		categoriesArray = categories.toArray(categoriesArray);
	}

	private void setCategoriesIntoActionBar() {
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(
				new ArrayAdapter<String>(
						actionBar.getThemedContext(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, categoriesArray
				),
				thisObj
		);
	}

	private void setupActionBar() {
		actionBar = getActionBar();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
				getActionBar().getSelectedNavigationIndex());
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		//		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		getPOI(categoriesArray[position]);
		return true;
	}

	private class GetAddressTask extends
			AsyncTask<Location, Void, String> {
		Context mContext;

		public GetAddressTask(Context context) {
			super();
			mContext = context;
		}

		/**
		 * Get a Geocoder instance, get the latitude and longitude
		 * look up the address, and return it
		 *
		 * @return A string containing the address of the current
		 * location, or an empty string if no address can be found,
		 * or an error message
		 * @params params One or more Location objects
		 */
		@Override
		protected String doInBackground(Location... params) {
			Geocoder geocoder =
					new Geocoder(mContext, Locale.getDefault());
			// Get the current location from the input parameter list
			Location loc = params[0];
			// Create a list to contain the result address
			List<Address> addresses = null;
			try {
				/*
				 * Return 1 address.
                 */
				addresses = geocoder.getFromLocation(loc.getLatitude(),
						loc.getLongitude(), 1);
			} catch (IOException e1) {
				Log.e("LocationSampleActivity",
						"IO Exception in getFromLocation()");
				e1.printStackTrace();
				return ("IO Exception trying to get address");
			} catch (IllegalArgumentException e2) {
				// Error message to post in the log
				String errorString = "Illegal arguments " +
						Double.toString(loc.getLatitude()) +
						" , " +
						Double.toString(loc.getLongitude()) +
						" passed to address service";
				Log.e("LocationSampleActivity", errorString);
				e2.printStackTrace();
				return errorString;
			}
			// If the reverse geocode returned an address
			if (addresses != null && addresses.size() > 0) {
				// Get the first address
				Address address = addresses.get(0);

				// Return the text
				return address.getLocality();
			} else {
				return "";
			}
		}

		@Override
		protected void onPostExecute(String address) {
			super.onPostExecute(address);
			currentCity = address.replace(" ", "").toLowerCase().trim();
			if (citiesList.contains(currentCity)) {
				Toast.makeText(getApplicationContext(), address + " is supported",
						Toast.LENGTH_LONG).show();
				getCategories();
			} else {
				Toast.makeText(getApplicationContext(), "Sorry " + address + " is not supported yet",
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
