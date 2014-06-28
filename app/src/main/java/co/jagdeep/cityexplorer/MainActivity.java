package co.jagdeep.cityexplorer;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import co.jagdeep.cityexplorer.model.GeoName;
import co.jagdeep.cityexplorer.model.GeoNames;
import co.jagdeep.cityexplorer.talk.Place;
import co.jagdeep.cityexplorer.talk.Talk;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.gn.intelligentheadset.IHS;
import com.gn.intelligentheadset.IHSDevice;
import com.gn.intelligentheadset.IHSDeviceDescriptor;
import com.gn.intelligentheadset.IHSListener;
import com.gn.intelligentheadset.subsys.IHSSensorPack;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.spothero.volley.JacksonNetwork;
import com.spothero.volley.JacksonRequest;
import com.spothero.volley.JacksonRequestListener;

import java.util.*;

import static com.gn.intelligentheadset.IHSDevice.IHSDeviceListener;


public class MainActivity extends Activity implements GoogleMap.OnMarkerClickListener {

	private static final String TAG = "CityExplorerLog";
	//	private static final String API_KEY = "apikey=8rNAM8hxtPgpJaWCPofUNCWJ3VU2STdS";
	private static final String API_KEY = "apikey=6400efb9c1d7e64df6407a6d58bd2f00";
	private static final String HEADSET_KEY = "WvZlDar7pdT5sqFenYhnP/3RxO3b2GUeZrjz1KdKPOM=";
	//	public static final double FAKE_LATITUDE = 38.8977;
	//	public static final double FAKE_LONGITUDE = -77.0366;
	private GoogleMap mMap;
	MapFragment mMapFragment;
	private String currentCity = "";
	RequestQueue mRequestQueue;
	int radius = 1200;
	//	int radius = 40000000;
	//	private float previousZoomLevel = -1.0f;

	private Map<Marker, GeoName> geoNameMap;

	private MainActivity thisObj;
	private String[] categoriesArray;
	private View progressBar;
	private TextToSpeech tts;
	private IHS mIHS;
	private IHSDevice mMyDevice = null;
	private Location myCurrentLocation;


	private IHSSensorPack.IHSSensorsListener mSensorInfoListener;
	private IHSListener mIHSListener = new IHSListener() {

		@Override
		public void onAPIstatus(APIStatus apiStatus) {
			if (apiStatus == APIStatus.READY) {
				// We want it to stay alive until explicitly stopped (in onBackPressed)
				mIHS.connectHeadset(); // May already be connected, but that doesn't matter
			}
		}

		@Override
		public void onIHSDeviceSelectionRequired(List<IHSDeviceDescriptor> list) {
			Toast.makeText(thisObj, "Please Select headset", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onIHSDeviceSelected(IHSDevice device) {
			mMyDevice = device;
			mMyDevice.getSensorPack().addListener(mSensorInfoListener);
			mMyDevice.addListener(mDeviceInfoListener);
		}

	};

	// Listener for device-level events
	private IHSDeviceListener mDeviceInfoListener = new IHSDevice.IHSDeviceListener() {

		@Override
		public void connectedStateChanged(IHSDevice device, IHSDevice.IHSDeviceConnectionState connectionState) {
			super.connectedStateChanged(device, connectionState);

			switch (connectionState) {
			case IHSDeviceConnectionStateDisconnected:
				// In this example, we decide that we want automatic reconnection in case of
				// disconnects.
				// Other apps may choose differently.

				// Generic (re)connect - beware that this may end up connecting to
				// a different device!
				mIHS.connectHeadset();
				break;
			default:
				// No action.
				break;
			}
		}

		@Override
		public void rssiAvailable(IHSDevice dev, int rssi) {
		}

		@Override
		public void staticInfoAvailable(IHSDevice dev) {
			// As a simple example, get the SW revision of the headset
		}

		;
	};
	private TextView tvCheading;
	private Marker myMarker;
	private TextView txtBearing;
	private Talk[] talkArray;
	private List<Talk> nearbyTalkList;
	//	private Location fakeLocation;
	private Handler handler;
	private boolean isAnimatedCamera = false;

	List<Talk> getNearbyMarkers(Location location) {
		List<Talk> talkList = new ArrayList<Talk>();
		if (talkArray != null) {
			for (Talk talk : talkArray) {
				int distance = Talk.calculateDistance(location, talk.place.getPosition());
				//				float rad = (radius * 664 * previousZoomLevel) / 256;
				if (distance < radius) {
					talkList.add(talk);
				}
			}
		}
		return talkList;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		thisObj = this;
		mRequestQueue = JacksonNetwork.newRequestQueue(this);

		setContentView(R.layout.activity_main);

		tvCheading = (TextView) findViewById(R.id.textCompass);
		txtBearing = (TextView) findViewById(R.id.textBearing);

		//		fakeLocation = new Location("");
		//		fakeLocation.setLatitude(FAKE_LATITUDE);
		//		fakeLocation.setLongitude(FAKE_LONGITUDE);

		handler = new Handler();

		mIHS = new IHS(this, HEADSET_KEY, mIHSListener);

		progressBar = findViewById(R.id.progress);

		mSensorInfoListener = new IHSSensorPack.IHSSensorsListener() {
			@Override
			public void compassHeadingChanged(IHSSensorPack ihsSensorPack, float v) {
				checkCompass(v);
			}
		};

		tts = new TextToSpeech(getApplicationContext(),
				new TextToSpeech.OnInitListener() {
					@Override
					public void onInit(int status) {
						if (status != TextToSpeech.ERROR) {
							tts.setLanguage(Locale.UK);
						}
					}
				}
		);

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
	}

	private void checkCompass(float v) {
		if (myMarker != null) {
			myMarker.setRotation(v + 90);
			v = (Math.round(v) / 10);
			tvCheading.setText(String.format("%.1f∞", v));
			if (myCurrentLocation != null) {
				if (nearbyTalkList != null) {
					for (Talk talk : nearbyTalkList) {
						int round = Math.round(talk.place.getPosition().bearingTo(myCurrentLocation)) / 10 + 9;
						if (round < 0) {
							round += 9;
						}
						txtBearing.setText(Integer.toString(round));
						if (round == v) {
							if (!talk.hasSpoken) {
								talk.sayTitleAndDistance(myCurrentLocation);
							}
							talk.place.getMarker().showInfoWindow();
							break;
						}
					}
				}
			}
		}
	}

	private void setUpMapIfNeeded() {
		geoNameMap = new HashMap<Marker, GeoName>();
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			mMap = mMapFragment.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				// The Map is verified. It is now safe to manipulate the map.
				mMap.setMyLocationEnabled(true);
				//				mMap.setOnCameraChangeListener(this);
				mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
					@Override
					public void onMyLocationChange(Location location) {
						//						location.set(fakeLocation);
						myCurrentLocation = location;
						LatLng myLocation = new LatLng(location.getLatitude(),
								location.getLongitude());
						getGeoNames();
						if (!isAnimatedCamera) {
							mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
									15.0f));
							isAnimatedCamera = !isAnimatedCamera;
						}
						if (myMarker == null) {
							myMarker = mMap.addMarker(getMyMarkerOptions(myLocation));
						}
						nearbyTalkList = getNearbyMarkers(myCurrentLocation);
					}
				});
			}
		}
	}

	private MarkerOptions getMyMarkerOptions(LatLng myLocation) {
		return new MarkerOptions()
				.position(new LatLng(myLocation.latitude, myLocation.longitude))
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.cone2));
	}

	private void getGeoNames() {


		final String url = "http://ws.geonames.net/findNearbyWikipediaJSON?lat=" + myCurrentLocation
				.getLatitude() + "&lng=" + myCurrentLocation.getLongitude() + "&username=wikimedia&lang="+Locale.getDefault().getLanguage();
		Log.i(TAG, url);

		mRequestQueue.add(new JacksonRequest<GeoNames>(Request.Method.GET,
						url,
						new JacksonRequestListener<GeoNames>() {
							@Override
							public void onResponse(GeoNames response, int statusCode, VolleyError error) {
								if (response != null) {
									mMap.clear();
									if (myMarker != null) {
										myMarker = mMap.addMarker(getMyMarkerOptions(myMarker.getPosition()));
									}
									responseToGeoNamesArray(response);
									hideProgress();
								} else {
									Log.e(TAG, "An error occurred while parsing the data! Stack trace follows:");
									error.printStackTrace();
								}
							}

							@Override
							public JavaType getReturnType() {
								return SimpleType.construct(GeoNames.class);
							}
						}
				)
		);

	}

	private void responseToGeoNamesArray(GeoNames response) {
		List<Talk> talkList = new ArrayList<Talk>();
		if (response.geoNamesArray.length > 0) {
			for (GeoName geoName : response.geoNamesArray) {
				if (geoName.title != null) {

					Location location = new Location("");
					location.setLatitude(geoName.lat);
					location.setLongitude(geoName.lng);

					Talk talk = new Talk(tts, makePlace(geoName), handler);
					talkList.add(talk);

					Marker marker = mMap.addMarker(new MarkerOptions()
									.position(new LatLng(geoName.lat, geoName.lng))
									.title(geoName.title)
									.snippet(geoName.summary)
					);

					geoNameMap.put(marker, geoName);

					talk.place.setMarker(marker);
				}
			}
			talkArray = talkList.toArray(new Talk[talkList.size()]);
			nearbyTalkList = getNearbyMarkers(myCurrentLocation);
			mMap.setOnMarkerClickListener(this);
		}
	}

	void showProgress() {
		progressBar.setVisibility(View.VISIBLE);
	}

	void hideProgress() {
		progressBar.setVisibility(View.GONE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		if (mMyDevice != null) {
			mMyDevice.addListener(mDeviceInfoListener);
			mMyDevice.getSensorPack().addListener(mSensorInfoListener);
		}

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

	private Place makePlace(GeoName geoName) {
		Place samplePlace = new Place();
		samplePlace.setTitle(geoName.title);
		Location testLocation = new Location("");
		testLocation.setLatitude(geoName.lat);
		testLocation.setLongitude(geoName.lng);
		samplePlace.setPosition(testLocation);
		samplePlace.setShortDescription(geoName.summary);
		//		samplePlace.setShortDescription("The Sydney Opera House is a multi-venue performing arts centre in
		// Sydney");
		//		samplePlace.setLongDescription("The Opera House’s magnificent harbourside location,
		// stunning architecture and excellent programme of events make it Sydney’s number one destination. The modern
		// masterpiece reflects the genius of its architect, Jørn Utzon. In 1999, Utzon agreed to prepare a guide of
		// design principles for future changes to the building. This was welcome news for all who marvel at his
		// masterpiece and for the four million visitors to the site each year.");
		return samplePlace;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		if (marker != myMarker) {
			final Place testData = makePlace(geoNameMap.get(marker));
			Talk talkObj = new Talk(tts, testData, handler);
			if (talkObj.place.getTitle() != null) {
				talkObj.sayShortDescription();
			}
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		mIHS.stopService();
		super.onBackPressed();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mMyDevice != null) {
			mMyDevice.removeListener(mDeviceInfoListener);
			mMyDevice.getSensorPack().removeListener(mSensorInfoListener);
		}

	}

	//	@Override
	//	public void onCameraChange(CameraPosition position) {
	//		Log.d("Zoom", "Zoom: " + position.zoom);
	//
	//		if (previousZoomLevel != position.zoom) {
	//			//			isZooming = true;
	//		}
	//
	//		previousZoomLevel = position.zoom;
	//	}

}
