package com.evgenii.maptest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class WalkLocationPermissions {
    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private static final int INITIAL_REQUEST=1337;
    private static final int LOCALTION_REQUEST=INITIAL_REQUEST+1;

    private static WalkLocationPermissions ourInstance = new WalkLocationPermissions();

    public static WalkLocationPermissions getInstance() {
        return ourInstance;
    }

    private WalkLocationPermissions() {
    }

    void requestLocationPermissionIfNotGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasLocationPermission()) {
                requestPermissions(INITIAL_PERMS, LOCALTION_REQUEST);
            }
        }
    }

    boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(WalkApplication.getAppContext(),
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
}
