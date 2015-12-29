package com.evgenii.maptest;

import android.app.Application;
import android.content.Context;

public class WalkApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();

        WalkApplication.context = getApplicationContext();

        WalkGoogleApiClient.getInstance().onConnectedCallbackForLocationUpdates = new Runnable() {
            @Override
            public void run() {
                WalkLocationService.getInstance().startLocationUpdates();
            }
        };

        WalkGoogleApiClient.getInstance().create();
    }

    public static Context getAppContext() {
        return WalkApplication.context;
    }
}
