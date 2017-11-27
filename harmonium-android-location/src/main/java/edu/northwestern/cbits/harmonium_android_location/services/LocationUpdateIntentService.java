package edu.northwestern.cbits.harmonium_android_location.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.LocationResult;

import java.util.ArrayList;
import java.util.List;

import edu.northwestern.cbits.harmonium_android_location.models.Location;

public class LocationUpdateIntentService extends BroadcastReceiver {
    public static final String ACTION_PROCESS_UPDATES =
        "edu.northwestern.cbits.harmonium_android_location.ACTION_PROCESS_UPDATES";
    public static final String ACTION_LOCATION_CAPTURED =
        "edu.northwestern.cbits.harmonium_android_location.ACTION_LOCATION_CAPTURED";
    public static final String LOCATIONS_KEY = "locations";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null &&
                ACTION_PROCESS_UPDATES.equals(intent.getAction()) &&
                LocationResult.hasResult(intent)) {
            final List<android.location.Location> rawLocations =
                LocationResult.extractResult(intent).getLocations();
            final ArrayList<Location> locations = new ArrayList<>(rawLocations.size());

            for (int i = 0; i < rawLocations.size(); i++) {
                final Location location = new Location();

                location.setAccuracy(rawLocations.get(i).getAccuracy());
                location.setAltitude(rawLocations.get(i).getAltitude());
                location.setBearing(rawLocations.get(i).getBearing());
                location.setBearingAccuracyDegrees(bearingAccuracyDegrees(rawLocations.get(i)));
                location.setElapsedRealtimeNanos(rawLocations.get(i).getElapsedRealtimeNanos());
                location.setLatitude(rawLocations.get(i).getLatitude());
                location.setLongitude(rawLocations.get(i).getLongitude());
                location.setProvider(rawLocations.get(i).getProvider());
                location.setSpeed(rawLocations.get(i).getSpeed());
                location.setSpeedAccuracyMetersPerSecond(
                    speedAccuracyMetersPerSecond(rawLocations.get(i))
                );
                location.setTime(rawLocations.get(i).getTime());
                location.setVerticalAccuracyMeters(verticalAccuracyMeters(rawLocations.get(i)));

                locations.add(location);
            }

            sendLocalBroadcast(
                context,
                new Intent(ACTION_LOCATION_CAPTURED)
                    .putParcelableArrayListExtra(LOCATIONS_KEY, locations)
            );
        }
    }

    public void sendLocalBroadcast(Context context, Intent intent) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private float bearingAccuracyDegrees(android.location.Location location) {
        if (isAtLeastOreo()) {
            return location.getBearingAccuracyDegrees();
        }

        return 0.0f;
    }

    private float speedAccuracyMetersPerSecond(android.location.Location location) {
        if (isAtLeastOreo()) {
            return location.getSpeedAccuracyMetersPerSecond();
        }

        return 0.0f;
    }

    private float verticalAccuracyMeters(android.location.Location location) {
        if (isAtLeastOreo()) {
            return location.getVerticalAccuracyMeters();
        }

        return 0.0f;
    }

    private boolean isAtLeastOreo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }
}
