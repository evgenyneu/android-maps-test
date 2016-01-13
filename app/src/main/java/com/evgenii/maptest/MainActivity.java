package com.evgenii.maptest;

import android.app.Dialog;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.evgenii.maptest.Fragments.WalkLocationDeniedFragment;
import com.evgenii.maptest.Fragments.WalkMapFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new WalkMapFragment())
                    .commit();
        }

        registerApiClientCallback();
        registerLocationPermissionCallback();
    }

    @Override
    protected void onResume() {
        super.onResume();

        startGooglePlayServices();
        WalkLocationPermissions.getInstance().requestLocationPermissionIfNotGranted(this);
    }

    private void startGooglePlayServices() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (resultCode == ConnectionResult.SUCCESS) {
            WalkGoogleApiClient.getInstance().create();
        } else if (resultCode == ConnectionResult.SERVICE_MISSING ||
                resultCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED ||
                resultCode == ConnectionResult.SERVICE_DISABLED) {

            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, resultCode, 1);
            dialog.show();
        }
    }

    private void registerApiClientCallback() {
        WalkGoogleApiClient.getInstance().didConnectCallback = new Runnable() {
            @Override
            public void run() {
                showMapFragment();
                reloadMap();
                WalkLocationService.getInstance().startLocationUpdates();
            }
        };
    }

    private void registerLocationPermissionCallback() {
        WalkLocationPermissions.getInstance().didGrantCallback = new Runnable() {
            @Override
            public void run() {
                showMapFragment();
                reloadMap();
                WalkLocationService.getInstance().startLocationUpdates();
            }
        };

        WalkLocationPermissions.getInstance().didDenyCallback = new Runnable() {
            @Override
            public void run() {
                showLocationDeniedActivity();
            }
        };
    }


    // Map
    // ----------------------

    void reloadMap() {
        WalkMapFragment map = getMapFragment();
        if (map != null) {
            map.enableMyLocationAndZoom();
        }
    }

    void showMapFragment() {
        if (getMapFragment() != null) { return; } // Already showing map
        showFragmentWithFlipAnimation(new WalkMapFragment());
    }

    WalkMapFragment getMapFragment() {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof WalkMapFragment) { return (WalkMapFragment)fragment; }
        return null;
    }

    // Location denied fragment
    // ----------------------

    public void locationDenied_didTapOpenSettingsButton(View view) {
        WalkLocationDeniedFragment fragment = getLocationDeniedFragment();
        if (fragment == null) { return; }
        fragment.didTapOpenSettings();
    }

    WalkLocationDeniedFragment getLocationDeniedFragment() {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof WalkLocationDeniedFragment) { return (WalkLocationDeniedFragment)fragment; }
        return null;
    }

    // Permissions
    // ----------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        WalkLocationPermissions.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showLocationDeniedActivity() {
        showFragmentWithFlipAnimation(new WalkLocationDeniedFragment());
    }

    public void didTapFlipButton(View view) {
        if (getMapFragment() == null) {
            showFragmentWithFlipAnimation(new WalkMapFragment());
        } else {
            showFragmentWithFlipAnimation(new WalkLocationDeniedFragment());
        }
    }

    // Show fragments
    // ----------------------

    private void showFragmentWithFlipAnimation(Fragment fragment) {
        WalkAnimation animation = getNextAnimation();

        getFragmentManager()
            .beginTransaction()
            .setCustomAnimations(animation.enter, animation.exit, 0, 0)
            .replace(R.id.container, fragment)
            .commit();
    }

    private WalkAnimation getNextAnimation() {
        if (getCurrentFragment() instanceof WalkMapFragment) {
            return new WalkAnimation(R.animator.flip_top_in, R.animator.flip_top_out);
        } else {
            return new WalkAnimation(R.animator.flip_bottom_in, R.animator.flip_bottom_out);
        }
    }

    private Fragment getCurrentFragment() {
        return getFragmentManager().findFragmentById(R.id.container);
    }
}
