package com.evgenii.maptest.Fragments;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evgenii.maptest.R;
import com.evgenii.maptest.Utils.WalkCameraDistance;

public class WalkLocationDeniedFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_walk_location_denied, container, false);
        WalkCameraDistance.setFragmentCameraDistance(view);
        return view;
    }
}
