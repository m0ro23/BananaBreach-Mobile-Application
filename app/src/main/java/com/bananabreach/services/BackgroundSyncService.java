package com.bananabreach.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Background sync placeholder.
 *
 * Intentional: this service is exported in the manifest with no caller
 * verification, so any app on the device can start/bind to it — see
 * docs/VULNERABILITIES.md (Android Components: exported service).
 */
public class BackgroundSyncService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        android.util.Log.d("BackgroundSyncService", "Sync triggered by intent: " + intent);
        return START_NOT_STICKY;
    }
}
