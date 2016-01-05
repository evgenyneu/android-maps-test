package com.evgenii.maptest;

import android.app.Dialog;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evgenii.maptest.Utils.WalkCameraDistance;
import com.evgenii.maptest.Utils.WalkGooglePlayServices;
import com.evgenii.maptest.Utils.WalkLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends AppCompatActivity {

    /**
     * Whether or not we're showing the back of the card (otherwise showing the front).
     */
    private static boolean mShowingBack = false;

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
        WalkLocationPermissions.getInstance().requestLocationPermissionIfNotGranted(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        startGooglePlayServices();
    }

    private void startGooglePlayServices() {
        WalkGooglePlayServices.logGooglePlayServicesVersion(this);

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
                reloadMap();
                WalkLocationService.getInstance().startLocationUpdates();
            }
        };
    }

    private void registerLocationPermissionCallback() {
        WalkLocationPermissions.getInstance().didGrantCallback = new Runnable() {
            @Override
            public void run() {
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

    WalkMapFragment getMapFragment() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.container);
        if (fragment instanceof WalkMapFragment) { return (WalkMapFragment)fragment; }
        return null;
    }

    // Permissions
    // ----------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        WalkLocationPermissions.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showLocationDeniedActivity() {

    }

    public void didTapFlipButton(View view) {
        flipCard();
    }

    private void flipCard() {
        int enterAnimation;
        int exitAnimation;
        Fragment fragment;

        if (mShowingBack) {
            enterAnimation = R.animator.flip_top_in;
            exitAnimation = R.animator.flip_top_out;
            fragment = new WalkMapFragment();
        } else {
            enterAnimation = R.animator.flip_bottom_in;
            exitAnimation = R.animator.flip_bottom_out;
            fragment = new CardBackFragment();
        }

        mShowingBack = !mShowingBack;

        getFragmentManager()
            .beginTransaction()
            .setCustomAnimations(enterAnimation, exitAnimation, 0, 0)
            .replace(R.id.container, fragment)
            .commit();
    }

    /**
     * A fragment representing the back of the card.
     */
    public static class CardBackFragment extends Fragment {
        public CardBackFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_card_back, container, false);
            WalkCameraDistance.setFragmentCameraDistance(view);
            return view;
        }
    }
}
