package com.cedricnoiseux.tp2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MapActivity extends AppCompatActivity implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		LocationListener {

	public static boolean initialLocation = false;
	private SupportMapFragment mapFragment;
	private GoogleMap map;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	private long UPDATE_INTERVAL = 60000;  /* 60 secs */
	private long FASTEST_INTERVAL = 10000; /* 10 secs */

	/*
	 * Define a request code to send to Google Play services This code is
	 * returned in Activity.onActivityResult
	 */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
		if (mapFragment != null) {
			mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                }
            });
		} else {
			Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
		}

	}

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // MapActivity is ready
            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
			MapActivityPermissionsDispatcher.getMyLocationWithCheck(this);
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		MapActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
	}

	@NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
	void getMyLocation() {
		if (map != null) {
			// Now that map has loaded, let's get our location!
			map.setMyLocationEnabled(true);
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addApi(LocationServices.API)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).build();
			connectClient();
		}
	}

    protected void connectClient() {
        // Connect the client.
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /*
     * Called when the Activity becomes visible.
    */
    @Override
    protected void onStart() {
        super.onStart();
        connectClient();
    }

    /*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
		super.onStop();
	}

	/*
	 * Handle results returned to the FragmentActivity by Google Play services
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {

		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
			switch (resultCode) {
			case Activity.RESULT_OK:
				mGoogleApiClient.connect();
				break;
			}

		}
	}

	private boolean isGooglePlayServicesAvailable() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates", "Google Play services is available.");
			return true;
		} else {
			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
					CONNECTION_FAILURE_RESOLUTION_REQUEST);

			// If Google Play services can provide an error dialog
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				errorFragment.setDialog(errorDialog);
				errorFragment.show(getSupportFragmentManager(), "Location Updates");
			}

			return false;
		}
	}

	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle dataBundle) {
		// Display the connection status
		Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if (location != null) {
			Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
			if (!initialLocation) {
				try {
					InputStreamReader tmp = new InputStreamReader(openFileInput(String.valueOf(R.string.PROFILE)));
					BufferedReader reader = new BufferedReader(tmp);
					OutputStreamWriter writer = new OutputStreamWriter(openFileOutput("tempFile", 0));
					String currentLine;
					int counter = 0;
					while((currentLine = reader.readLine()) != null) {
						if (counter == 5) {
							writer.write(String.valueOf(latLng.longitude) + " " + String.valueOf(latLng.latitude) + "\r\n");
						} else {
							writer.write(currentLine + "\r\n");
						}
						counter++;
					}
					writer.close();
					reader.close();

					InputStreamReader tmp1 = new InputStreamReader(openFileInput("tempFile"));
					BufferedReader reader1 = new BufferedReader(tmp1);
					OutputStreamWriter writer1 = new OutputStreamWriter(openFileOutput(String.valueOf(R.string.PROFILE), 0));
					String currentLine1;
					while((currentLine1 = reader1.readLine()) != null) {
						writer1.write(currentLine1 + "\r\n");
					}
					writer1.close();
					reader1.close();

					AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
					dlgAlert.setTitle("Organisapp");
					dlgAlert.setCancelable(true);
					dlgAlert.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
									startActivity(intent);
								}
							});
					dlgAlert.setMessage("We need your to access your calendar to continue");
					dlgAlert.create().show();

//					Intent intent = new Intent(this, Menu.class);
//					startActivity(intent);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (initialLocation){
				MarkerOptions options = new MarkerOptions()
						.position(latLng)
						.title("I am here!");
				map.addMarker(options);

				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
				map.animateCamera(cameraUpdate);
			}
			initialLocation = true;
        } else {
        	Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
		}
		startLocationUpdates();
	}

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

		try {
			InputStreamReader tmp = new InputStreamReader(openFileInput(String.valueOf(R.string.PROFILE)));
			BufferedReader reader = new BufferedReader(tmp);
			OutputStreamWriter writer = new OutputStreamWriter(openFileOutput("tempFile", 0));
			String currentLine;
			int counter = 0;
			while((currentLine = reader.readLine()) != null) {
				if (counter == 5) {
					writer.write(String.valueOf(latLng.longitude) + " " + String.valueOf(latLng.latitude) + "\r\n");
				} else {
					writer.write(currentLine + "\r\n");
				}
				counter++;
			}
			writer.close();
			reader.close();

			InputStreamReader tmp1 = new InputStreamReader(openFileInput("tempFile"));
			BufferedReader reader1 = new BufferedReader(tmp1);
			OutputStreamWriter writer1 = new OutputStreamWriter(openFileOutput(String.valueOf(R.string.PROFILE), 0));
			String currentLine1;
			while((currentLine1 = reader1.readLine()) != null) {
				writer1.write(currentLine1 + "\r\n");
			}
			writer1.close();
			reader1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		map.clear();
		MarkerOptions options = new MarkerOptions()
				.position(latLng)
				.title("I am here!");
		map.addMarker(options);

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /*
     * Called by Location Services if the connection to the location client
     * drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

	/*
	 * Called by Location Services if the attempt to Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
		}
	}

	// Define a DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment {

		// Global field to contain the error dialog
		private Dialog mDialog;

		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

}

//import android.content.IntentSender;
//import android.location.Location;
//import android.support.v4.app.FragmentActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//import java.util.MapActivity;
//
//public class MapActivity extends FragmentActivity implements
//        //OnMapReadyCallback,
//        GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener,
//        LocationListener {
//
//    public final static String PROFILE = "profile.txt";
//    public static final String TAG = MapActivity.class.getSimpleName();
//    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
//
//    private com.google.android.gms.maps.MapActivity mMap;
//    private GoogleApiClient mGoogleApiClient;
//    private LocationRequest mLocationRequest;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_map);
//
//        isGooglePlayServicesAvailable();
//        setUpMapIfNeeded();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//
//        // Create the LocationRequest object
//        mLocationRequest = LocationRequest.create()
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setInterval(10000)        // 10 seconds, in milliseconds
//                .setFastestInterval(1000); // 1 second, in milliseconds
//
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
////        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
////        mapFragment.getMapAsync(this);
//    }
//
//    @Override
//    protected void onStart() {
//        setUpMapIfNeeded();
//        mGoogleApiClient.connect();
//        super.onStart();
//    }
//
//    @Override
//    protected void onPause() {
//        if (mGoogleApiClient.isConnected() && mGoogleApiClient != null) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//            mGoogleApiClient.disconnect();
//        }
//        super.onPause();
//    }
//
//    private boolean isGooglePlayServicesAvailable() {
//        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//        if (ConnectionResult.SUCCESS == status) {
//            return true;
//        } else {
//            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
//            return false;
//        }
//    }
//
//    private void setUpMapIfNeeded() {
//        // Do a null check to confirm that we have not already instantiated the map.
//        if (mMap == null) {
//            // Try to obtain the map from the SupportMapFragment.
//            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
//            // Check if we were successful in obtaining the map.
//            if (mMap != null) {
//                setUpMap();
//            }
//        }
//    }
//
//    private void setUpMap() {
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
//    }
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
////    @Override
////    public void onMapReady(MapActivity googleMap) {
////        mMap = googleMap;
////    }
//
//    private void handleNewLocation(Location location) {
//        Log.d(TAG, location.toString());
//
//        double currentLatitude = location.getLatitude();
//        double currentLongitude = location.getLongitude();
//
//        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
//
//        MarkerOptions options = new MarkerOptions()
//                .position(latLng)
//                .title("I am here!");
//        mMap.addMarker(options);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//    }
//
//    @Override
//    public void onConnected(Bundle bundle) {
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//        if (mGoogleApiClient.isConnected() && mGoogleApiClient != null) {
//            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            if (location == null) {
//                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//            } else {
//                handleNewLocation(location);
//            }
//        }
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        if (i == CAUSE_SERVICE_DISCONNECTED) {
//            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
//        } else if (i == CAUSE_NETWORK_LOST) {
//            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        if (connectionResult.hasResolution()) {
//            try {
//                // Start an Activity that tries to resolve the error
//                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
//            } catch (IntentSender.SendIntentException e) {
//                e.printStackTrace();
//            }
//        } else {
//            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
//        }
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        handleNewLocation(location);
//    }
//}
