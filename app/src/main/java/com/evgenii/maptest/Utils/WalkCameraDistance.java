package com.evgenii.maptest.Utils;

import android.view.View;

public class WalkCameraDistance {
    public static void setFragmentCameraDistance(View view) {
        float scale = view.getResources().getDisplayMetrics().density;
        view.setCameraDistance(3000 * scale);
    }
}
