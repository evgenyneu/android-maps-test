package com.evgenii.maptest.Utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class WalkLocation {
    public static Location locationFromLatLng(LatLng latLng) {
        Location location = new Location("any string");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);

        return location;
    }
}
