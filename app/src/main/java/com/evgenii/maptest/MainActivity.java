package com.evgenii.maptest;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {

    /**
     * Whether or not we're showing the back of the card (otherwise showing the front).
     */
    private static boolean mShowingBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new MapContainerFragment())
                    .commit();
        }

        registerApiClientCallback();
        registerLocationPermissionCallback();
        WalkLocationPermissions.getInstance().requestLocationPermissionIfNotGranted(this);

    }

    private void registerApiClientCallback() {
        WalkGoogleApiClient.getInstance().didConnectCallback = new Runnable() {
            @Override
            public void run() {
                //enableMyLocationZoomAndStartLocationUpdates();
            }
        };
    }

    private void registerLocationPermissionCallback() {
        WalkLocationPermissions.getInstance().didGrantCallback = new Runnable() {
            @Override
            public void run() {
                //enableMyLocationZoomAndStartLocationUpdates();
            }
        };

        WalkLocationPermissions.getInstance().didDenyCallback = new Runnable() {
            @Override
            public void run() {
                showLocationDeniedActivity();
            }
        };
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
            fragment = new MapContainerFragment();
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
     * A fragment representing the front of the card.
     */
//    public static class CardFrontFragment extends Fragment {
//        public CardFrontFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View view = inflater.inflate(R.layout.fragment_card_front, container, false);
//
//            float scale = getResources().getDisplayMetrics().density;
//            view.setCameraDistance(3000 * scale);
//            return view;
//        }
//    }

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

            float scale = getResources().getDisplayMetrics().density;
            view.setCameraDistance(3000 * scale);
            return view;
        }
    }
}
