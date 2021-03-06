package com.evgenii.maptest.Fragments;
import com.evgenii.maptest.R;
import com.evgenii.maptest.Utils.WalkLocation;
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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class WalkMapFragment extends Fragment implements OnMapReadyCallback,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
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
        zoomToLastLocationAndStartLocationUpdated();
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
        MapFragment mapFragment =  MapFragment.newInstance();

        getChildFragmentManager().beginTransaction()
            .setCustomAnimations(R.animator.keep_child_fragment, 0, 0, 0)
            .add(R.id.map, mapFragment)
            .commit();

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
        createMarkers();
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    public void enableMyLocationAndZoom() {
        if (WalkLocationPermissions.getInstance().hasLocationPermission()) {
            enableMyLocation();
            zoomToLastLocationAndStartLocationUpdated();
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

    private static Location getLastLocation() {
        if (WalkGoogleApiClient.isConnected() && WalkLocationPermissions.getInstance().hasLocationPermission()) {
            return LocationServices.FusedLocationApi.getLastLocation(
                    WalkGoogleApiClient.getInstance().getClient());
        }

        return null;
    }

    private void zoomToLastLocationAndStartLocationUpdated() {
        Log.d("ii", "zoomToLastLocationAndStartLocationUpdated");

        // 1. First, get last location and zoom the map there.
        // Last location is returned immediately but can be null.
        // -------------

        Location lastLocation = getLastLocation();

        if (lastLocation != null) {
            zoomMapToLocation(lastLocation);
        }

        // 2. Start location updates.
        // Zoom the map to this updated location if it is significantly different from last location.
        // -------------

        startLocationUpdates();
    }

    private void zoomMapToLocation(Location location) {
        if (mMap == null) { return; }

        // Skip zoom if map is already centered correctly
        Location mapCenter =  WalkLocation.getMapCenter(mMap);
        if (mapCenter.distanceTo(location) < 150) {
            float currentZoom = mMap.getCameraPosition().zoom;
            Log.d("ii", "Current zoom " + currentZoom);
            // Skip zoom if already zoomed
            if (Math.abs(WalkConstants.mapInitialZoom - currentZoom) < 1.5) {
                Log.d("ii", "MAP zoomToCurrentLocation ALREADY ZOOMED");
                return;
            }
        }

        Log.d("ii", "MAP zoomToCurrentLocation");

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, WalkConstants.mapInitialZoom));
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

        zoomMapToLocation(location);
        stopLocationUpdates();
    }
}
