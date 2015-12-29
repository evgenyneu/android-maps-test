package com.evgenii.maptest;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapContainerActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Location mLastLocation;
    private boolean mDidZoomToCurrentLocation = false;

    private WalkNotification walkNotification = new WalkNotification();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_container);
        registerApiClientCallback();
        registerLocationPermissionCallback();
        initMap();
        WalkLocationPermissions.getInstance().requestLocationPermissionIfNotGranted(this);
    }

    private void registerApiClientCallback() {
        WalkGoogleApiClient.getInstance().didConnectCallback = new Runnable() {
            @Override
            public void run() {
                enableMyLocationZoomAndStartLocationUpdates();
            }
        };
    }

    private void registerLocationPermissionCallback() {
        WalkLocationPermissions.getInstance().didGrantCallback = new Runnable() {
            @Override
            public void run() {
                enableMyLocationZoomAndStartLocationUpdates();
            }
        };
    }

    // Permissions
    // ----------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        WalkLocationPermissions.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Create markers
    // ----------------------

    private void createMarkers() {
        ArrayList<WalkPosition> walkPositions = WalkLocationeDetector.getInstance().getPositions();

        for(WalkPosition position: walkPositions) {
            createMarker(position);
        }
    }

    void createMarker(WalkPosition position) {
        CircleOptions circleOptions = new CircleOptions()
                .center(position.latLng)
                .fillColor(Color.parseColor("#33A4AFFF"))
                .strokeColor(Color.parseColor("#A4AFFF"))
                .strokeWidth(3)
                .radius(WalkConstants.mCircleRadiusMeters); // In meters

        // Add a marker
        mMap.addMarker(new MarkerOptions().position(position.latLng).title(position.name));

        // Get back the mutable Circle
        Circle circle = mMap.addCircle(circleOptions);
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
        if (WalkLocationPermissions.getInstance().hasLocationPermission()) {
            enableMyLocation();
            getLastLocation();
            zoomToCurrentLocation();
            WalkLocationService.getInstance().startLocationUpdates();
        }
    }

    // My location
    // ----------------------

    void enableMyLocation() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    void getLastLocation() {
        if (WalkGoogleApiClient.isConnected()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    WalkGoogleApiClient.getInstance().getClient());
        }
    }

    private void zoomToCurrentLocation() {
        if (mLastLocation == null) { return; }

        if (mDidZoomToCurrentLocation) { return; } // Zoom only once
        mDidZoomToCurrentLocation = true;

        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        createMarkers();
    }

}
