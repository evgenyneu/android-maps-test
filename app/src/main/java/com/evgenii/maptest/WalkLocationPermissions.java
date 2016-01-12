package com.evgenii.maptest;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

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

    public Runnable didGrantCallback;
    public Runnable didDenyCallback;

    public void requestLocationPermissionIfNotGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasLocationPermission()) {
                activity.requestPermissions(INITIAL_PERMS, LOCALTION_REQUEST);
            }
        }
    }

    public boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(WalkApplication.getAppContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case LOCALTION_REQUEST:
                if (hasLocationPermission()) {
                    if (didGrantCallback != null) {
                        didGrantCallback.run();
                    }
                } else {
                    if (didDenyCallback != null) {
                        didDenyCallback.run();
                    }
                }

                break;
        }
    }
}
