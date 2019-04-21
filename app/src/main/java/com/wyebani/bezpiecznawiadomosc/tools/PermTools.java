package com.wyebani.bezpiecznawiadomosc.tools;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermTools {

    /**
     * Check if app has permission granted
     * @param context - application context
     * @param permission - permission to check
     * @return true if permission granted, false if denied
     */
    public static boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check if app has permissions granded
     * @param context - application context
     * @param permissions - permissions to check
     * @return true if permissions granted, false if denied
     */
    public static boolean isPermissionsGranted(Context context, String[] permissions) {
        boolean result = false;
        for(String perm : permissions) {
            result = PermTools.isPermissionGranted(context, perm);

            if(!result)
                break;
        }
        return result;
    }

    /**
     * Runtime request for permissions
     * @param activity - activity object
     * @param permissions - required permissions
     * @param permCode - permissions code
     */
    public static void requestPermissions(Activity activity, String[] permissions, Integer permCode) {
        ActivityCompat.requestPermissions(activity,
                permissions,
                permCode);
    }

}
