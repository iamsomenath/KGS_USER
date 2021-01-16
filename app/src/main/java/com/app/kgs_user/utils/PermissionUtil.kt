package com.app.kgs_user.utils

import android.content.pm.PackageManager


object PermissionUtil {

    fun verifyPermissions(grantResults: IntArray): Boolean {
        // At least one result must be checked.
        if (grantResults.isEmpty()) {
            return false
        }
        // Verify that each required permission has been granted, otherwise return false.
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

}