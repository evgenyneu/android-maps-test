package com.evgenii.maptest;

import android.app.Fragment;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapContainerFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean mDidZoomToCurrentLocation = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        initMap();
        return view;
    }

    public void didTapButton(View view) {
        //Intent intent = new Intent(this, LocationDeniedActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(intent);
        //finishAffinity();
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
        MapFragment mapFragment = (MapFragment) getChildFragmentManager()
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
        enableMyLocationAndZoom();
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    void enableMyLocationAndZoom() {
        if (WalkLocationPermissions.getInstance().hasLocationPermission()) {
            enableMyLocation();
            zoomToCurrentLocation();
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

    private void zoomToCurrentLocation() {
        Location lastLocation = getLastLocation();

        if (lastLocation == null) { return; }
        if (mMap == null) { return; }

        if (mDidZoomToCurrentLocation) { return; } // Zoom only once
        mDidZoomToCurrentLocation = true;

        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        createMarkers();
    }
}