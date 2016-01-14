package com.evgenii.maptest.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evgenii.maptest.R;
import com.evgenii.maptest.Utils.WalkCameraDistance;
import com.evgenii.maptest.Utils.WalkView;
import com.evgenii.maptest.WalkApplication;
import com.evgenii.maptest.WalkLocationPermissions;
import com.evgenii.maptest.WalkLocationService;

public class WalkLocationDeniedFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_walk_location_denied, container, false);
        WalkCameraDistance.setFragmentCameraDistance(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Activity activity = getActivity();
        if (activity == null) { return; }

        View requestPermissionGroup = activity.findViewById(R.id.location_denied_request_permission_group);
        View enablePermissionGroup = activity.findViewById(R.id.location_denied_enable_permission_group);

        if (requestPermissionGroup == null || enablePermissionGroup == null) { return; }

        boolean requestPermission = WalkLocationPermissions.getInstance().shouldShowRequestPermissionRationale(activity);

        WalkView.toggleView(requestPermissionGroup, requestPermission);
        WalkView.toggleView(enablePermissionGroup, !requestPermission);
    }

    public void didTapOpenSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("package:" + WalkApplication.getAppContext().getPackageName()));
        startActivity(intent);
    }
}
