package com.evgenii.maptest;

import android.location.Location;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class WalkLocationService implements
        com.google.android.gms.location.LocationListener {
    private static WalkLocationService ourInstance = new WalkLocationService();

    public static WalkLocationService getInstance() {
        return ourInstance;
    }

    private WalkLocationService() {
    }

    protected void startLocationUpdates() {
        if (!WalkGoogleApiClient.isConnected()) { return; }

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                WalkGoogleApiClient.getInstance().getClient(), mLocationRequest, this);
    }

    // com.google.android.gms.location.LocationListener
    @Override
    public void onLocationChanged(Location location) {
        WalkLocationeDetector.getInstance().checkReachedPosition(location);
    }
}
