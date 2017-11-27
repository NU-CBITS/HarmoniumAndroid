package edu.northwestern.cbits.harmonium_android_location;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.location.LocationResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import edu.northwestern.cbits.harmonium_android_location.models.Location;
import edu.northwestern.cbits.harmonium_android_location.services.LocationUpdateIntentService;

import static android.os.Build.VERSION_CODES.KITKAT;
import static android.os.Build.VERSION_CODES.O;
import static junit.framework.Assert.assertEquals;

/*
 * This subclass allows spying on the locally broadcast intent.
 */
class TestLocationUpdateIntentService extends LocationUpdateIntentService {
    public Intent mLocallyBroadcastIntent;

    @Override
    public void sendLocalBroadcast(Context context, Intent intent) {
        mLocallyBroadcastIntent = intent;
    }
}

@RunWith (RobolectricTestRunner.class)
@Config (minSdk = KITKAT)
public class LocationUpdateIntentServiceTest {
    private static final String KEY_LOCATION_RESULT =
        "com.google.android.gms.location.EXTRA_LOCATION_RESULT";

    private TestLocationUpdateIntentService mReceiver;
    private android.location.Location mLocation;
    private Intent mLocationUpdatesIntent;

    @Before
    public void setupReceiver() {
        mReceiver = new TestLocationUpdateIntentService();
    }

    @Before
    public void initializeData() {
        mLocation = new android.location.Location("");
        initializeIntent();
    }

    @Test
    public void onReceive_broadcastsActionLocationCaptured() {
        mReceiver.onReceive(context(), mLocationUpdatesIntent);

        assertEquals(
            LocationUpdateIntentService.ACTION_LOCATION_CAPTURED,
            mReceiver.mLocallyBroadcastIntent.getAction()
        );
    }

    @Test
    public void onReceive_capturesAccuracy() {
        mLocation.setAccuracy(1.5f);

        mReceiver.onReceive(context(), mLocationUpdatesIntent);

        assertEquals(1.5f, firstExtraLocation().getAccuracy());
    }

    @Test
    public void onReceive_capturesAltitude() {
        mLocation.setAltitude(4.2);

        mReceiver.onReceive(context(), mLocationUpdatesIntent);

        assertEquals(4.2, firstExtraLocation().getAltitude());
    }

    @Test
    public void onReceive_capturesBearing() {
        mLocation.setBearing(101.5f);

        mReceiver.onReceive(context(), mLocationUpdatesIntent);

        assertEquals(101.5f, firstExtraLocation().getBearing());
    }

    @Test
    @Config (minSdk = O)
    public void onReceive_capturesBearingAccuracyDegrees() {
        mLocation.setBearingAccuracyDegrees(10.5f);

        mReceiver.onReceive(context(), mLocationUpdatesIntent);

        assertEquals(10.5f, firstExtraLocation().getBearingAccuracyDegrees());
    }

    @Test
    public void onReceive_capturesElapsedRealtimeNanos() {
        mLocation.setElapsedRealtimeNanos(1234);

        mReceiver.onReceive(context(), mLocationUpdatesIntent);

        assertEquals(1234, firstExtraLocation().getElapsedRealtimeNanos());
    }

    @Test
    public void onReceive_capturesLatitude() {
        mLocation.setLatitude(12.34);

        mReceiver.onReceive(context(), mLocationUpdatesIntent);

        assertEquals(12.34, firstExtraLocation().getLatitude());
    }

    @Test
    public void onReceive_capturesLongitude() {
        mLocation.setLongitude(-56.78);

        mReceiver.onReceive(context(), mLocationUpdatesIntent);

        assertEquals(-56.78, firstExtraLocation().getLongitude());
    }

    @Test
    public void onReceive_capturesProvider() {
        mLocation.setProvider("abcde");

        mReceiver.onReceive(context(), mLocationUpdatesIntent);

        assertEquals("abcde", firstExtraLocation().getProvider());
    }

    @Test
    public void onReceive_capturesSpeed() {
        mLocation.setSpeed(345.6f);

        mReceiver.onReceive(context(), mLocationUpdatesIntent);

        assertEquals(345.6f, firstExtraLocation().getSpeed());
    }

    @Test
    @Config (minSdk = O)
    public void onReceive_capturesSpeedAccuracyMetersPerSecond() {
        mLocation.setSpeedAccuracyMetersPerSecond(9.87f);

        mReceiver.onReceive(context(), mLocationUpdatesIntent);

        assertEquals(9.87f, firstExtraLocation().getSpeedAccuracyMetersPerSecond());
    }

    @Test
    public void onReceive_capturesTime() {
        mLocation.setTime(12345);

        mReceiver.onReceive(context(), mLocationUpdatesIntent);

        assertEquals(12345, firstExtraLocation().getTime());
    }

    @Test
    @Config (minSdk = O)
    public void onReceive_capturesVerticalAccuracyMeters() {
        mLocation.setVerticalAccuracyMeters(9.87f);

        mReceiver.onReceive(context(), mLocationUpdatesIntent);

        assertEquals(9.87f, firstExtraLocation().getVerticalAccuracyMeters());
    }

    private Context context() {
        return RuntimeEnvironment.application;
    }

    private void initializeIntent() {
        mLocationUpdatesIntent = new Intent(LocationUpdateIntentService.ACTION_PROCESS_UPDATES);
        final List<android.location.Location> locations = new ArrayList<>();
        locations.add(mLocation);
        mLocationUpdatesIntent.putExtra(KEY_LOCATION_RESULT, LocationResult.create(locations));
    }

    @NonNull
    private Location firstExtraLocation() {
        final Bundle extras = mReceiver.mLocallyBroadcastIntent.getExtras();

        if (extras == null) {
            return new Location();
        }

        final List<Location> locations =
            extras.getParcelableArrayList(LocationUpdateIntentService.LOCATIONS_KEY);

        if (locations == null || locations.size() == 0) {
            return new Location();
        }

        return locations.get(0);
    }
}
