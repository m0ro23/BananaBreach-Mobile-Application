package com.bananabreach.receivers;

import android.content.Context;
import android.content.Intent;

/**
 * Device admin receiver placeholder.
 *
 * Intentional: this receiver is exported with the BIND_DEVICE_ADMIN permission
 * but performs no validation of the calling component — see
 * docs/VULNERABILITIES.md (Android Components: exported receiver).
 */
public class DeviceAdminReceiver extends android.app.admin.DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        android.util.Log.d("DeviceAdminReceiver", "Device admin enabled");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        android.util.Log.d("DeviceAdminReceiver", "Device admin disabled");
    }
}
