package com.evgenii.maptest;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class WalkGoogleApiClient implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    public GoogleApiClient getClient() { return mGoogleApiClient; }

    private static WalkGoogleApiClient ourInstance = new WalkGoogleApiClient();

    public static WalkGoogleApiClient getInstance() {
        return ourInstance;
    }

    private WalkGoogleApiClient() {
    }

    public static boolean isConnected() {
        return WalkGoogleApiClient.getInstance().mGoogleApiClient.isConnected();
    }

    // Google API client
    // ----------------------

    public void create() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(WalkApplication.getAppContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mGoogleApiClient.connect();
        }
    }

    // GoogleApiClient.ConnectionCallbacks
    // ----------------------

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("ii", "Google API client connected");
        // enableMyLocationZoomAndStartLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int var1) {
    }

    // GoogleApiClient.OnConnectionFailedListener
    // ----------------------

    @Override
    public void onConnectionFailed(ConnectionResult var1) {
    }
}
