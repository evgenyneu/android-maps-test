package com.evgenii.maptest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private boolean mDidStartLocationUpdates = false;
    private boolean mDidZoomToCurrentLocation = false;

    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private static final int INITIAL_REQUEST=1337;
    private static final int LOCALTION_REQUEST=INITIAL_REQUEST+1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initMap();
        requestLocationPermissionIfNotGranted();
        createGoogleApiClient();
    }

    // Markers
    // ----------------------

    private void createShapes() {
        LatLng latLng = new LatLng(-37.859621, 144.977781);

        // Instantiates a new CircleOptions object and defines the center and radius
        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .fillColor(Color.parseColor("#33A4AFFF"))
                .strokeColor(Color.parseColor("#A4AFFF"))
                .strokeWidth(3)
                .radius(90); // In meters

        // Add a marker
        mMap.addMarker(new MarkerOptions().position(latLng));

        // Get back the mutable Circle
        Circle circle = mMap.addCircle(circleOptions);
    }

    // Permissions
    // ----------------------

    void requestLocationPermissionIfNotGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasLocationPermission()) {
                requestPermissions(INITIAL_PERMS, LOCALTION_REQUEST);
            }
        }
    }

    boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch(requestCode) {

            case LOCALTION_REQUEST:
                enableMyLocationZoomAndStartLocationUpdates();

                if (!hasLocationPermission()) {
                    Toast.makeText(this, "Location denied", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    // Map
    // ----------------------

    void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * OnMapReadyCallback
     *
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocationZoomAndStartLocationUpdates();
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    void enableMyLocationZoomAndStartLocationUpdates() {
        if (hasLocationPermission()) {
            enableMyLocation();
            getLastLocation();
            zoomToCurrentLocation();
            startLocationUpdates();
        }
    }

    // My location
    // ----------------------

    void enableMyLocation() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    void getLastLocation() {
        if (mGoogleApiClient.isConnected()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    private void zoomToCurrentLocation() {
        if (mLastLocation == null) { return; }

        if (mDidZoomToCurrentLocation) { return; } // Zoom only once
        mDidZoomToCurrentLocation = true;

        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        createShapes();
    }

    // Google API client
    // ----------------------

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    void createGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    // GoogleApiClient.ConnectionCallbacks
    // ----------------------

    @Override
    public void onConnected(Bundle connectionHint) {
        enableMyLocationZoomAndStartLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int var1) {
    }

    // GoogleApiClient.OnConnectionFailedListener
    // ----------------------

    @Override
    public void onConnectionFailed(ConnectionResult var1) {

    }

    // Location updates
    // ----------------------

    protected void startLocationUpdates() {
        if (!mGoogleApiClient.isConnected()) { return; }

        if (mDidStartLocationUpdates) { return; } // Start location updates once
        mDidStartLocationUpdates = true;

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    // com.google.android.gms.location.LocationListener
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }
}
