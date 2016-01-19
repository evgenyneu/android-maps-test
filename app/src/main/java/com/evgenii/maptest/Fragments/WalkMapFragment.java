package com.evgenii.maptest.Fragments;
import com.evgenii.maptest.R;
import com.evgenii.maptest.Utils.WalkFragments;
import com.evgenii.maptest.WalkConstants;
import com.evgenii.maptest.WalkGoogleApiClient;
import com.evgenii.maptest.WalkLocationDetector;
import com.evgenii.maptest.WalkLocationPermissions;
import com.evgenii.maptest.WalkLocationService;
import com.evgenii.maptest.WalkPosition;
import com.google.android.gms.maps.GoogleMap;
import android.app.Fragment;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evgenii.maptest.Utils.WalkCameraDistance;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class WalkMapFragment extends Fragment implements OnMapReadyCallback,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private Location lastLocationZoom;
    private WalkLocationService locationService = new WalkLocationService();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.map_fragment, container, false);
        WalkCameraDistance.setFragmentCameraDistance(view);
        initMap();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    // Create markers
    // ----------------------

    private void createMarkers() {
        ArrayList<WalkPosition> walkPositions = WalkLocationDetector.getInstance().getPositions();

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
        com.google.android.gms.maps.MapFragment mapFragment =
                (com.google.android.gms.maps.MapFragment) WalkFragments.getChildFragment(this, R.id.map);
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
        enableMyLocationAndZoom();
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    public void enableMyLocationAndZoom() {
        if (WalkLocationPermissions.getInstance().hasLocationPermission()) {
            enableMyLocation();
            startLocationUpdates();
        }
    }

    // My location
    // ----------------------

    void enableMyLocation() {
        if (mMap != null && WalkLocationPermissions.getInstance().hasLocationPermission()) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private static Location getLastLocation_disabled() {
        if (WalkGoogleApiClient.isConnected() && WalkLocationPermissions.getInstance().hasLocationPermission()) {
            return LocationServices.FusedLocationApi.getLastLocation(
                    WalkGoogleApiClient.getInstance().getClient());
        }

        return null;
    }

    private void zoomToCurrentLocation(Location location) {
        if (mMap == null) { return; }

        if (lastLocationZoom != null) {
            // Skip zoom if last location zoom was nearby
            if (lastLocationZoom.distanceTo(location) < 300) { return; }
        }

        Log.d("ii", "MAP zoomToCurrentLocation");


        lastLocationZoom = location;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        createMarkers();
    }

    // Location
    // ----------------------

    void startLocationUpdates() {
        Log.d("ii", "MAP startLocationUpdates");
        locationService.startLocationUpdates(this, 1000);
    }

    void stopLocationUpdates() {
        locationService.stopLocationUpdates();
    }

    // com.google.android.gms.location.LocationListener
    @Override
    public void onLocationChanged(Location location) {
        Log.d("ii", "MAP onLocationChanged");

        zoomToCurrentLocation(location);
        if (lastLocationZoom != null) { stopLocationUpdates(); }
    }
}
