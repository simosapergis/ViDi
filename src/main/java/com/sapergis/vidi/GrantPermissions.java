package com.sapergis.vidi;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class GrantPermissions {
    public static final int REQUEST_CODE_PERMISSIONS = 1000;
    private static  String[] REQUIRED_PERMISSIONS = new String[] {Manifest.permission.CAMERA};

    public static boolean  allPermissionsGranted(Activity activity){
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(activity.getBaseContext(), permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void requestPermissions(Activity activity){
        ActivityCompat.requestPermissions(
                activity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
    }
}
