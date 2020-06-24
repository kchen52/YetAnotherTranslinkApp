package com.kchen52.yetanothertranslinkapp.handlers;

import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;

/**
 * Created by kevinchen on 2018-03-26.
 */

public class PermissionsHandler {
    private static String PERMISSIONS_HANDLER_TAG = "PermissionsHandler";
    public static void getPermissionIfNotGranted(Activity activity, String permission) {
        if (ContextCompat.checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(PERMISSIONS_HANDLER_TAG, "Permission was not granted for " + permission +
            ". Requesting now.");
            // Explain why we're requesting this
            // "Permissions suck, we know. We're requesting SMS permissions to allow...
            // You can decline it, but data-less requests will not work as a result."
            ActivityCompat.requestPermissions(activity, new String[]{permission}, 1);
        }
    }
}
