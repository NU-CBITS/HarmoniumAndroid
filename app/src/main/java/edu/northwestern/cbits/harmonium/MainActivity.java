package edu.northwestern.cbits.harmonium;

import android.arch.lifecycle.Observer;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.stetho.Stetho;

import edu.northwestern.cbits.harmonium.models.AppDatabase;
import edu.northwestern.cbits.harmonium.receivers.SensorDataBroadcastReceiver;
import edu.northwestern.cbits.harmonium_android_battery.Battery;
import edu.northwestern.cbits.harmonium_android_location.LocationReporter;
import edu.northwestern.cbits.harmonium_android_location.services.LocationUpdateIntentService;

public class MainActivity
        extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {
    private LocationReporter mLocationReporter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        observeViewModel();
        initializeStetho();
        LocalBroadcastManager.getInstance(this).registerReceiver(new SensorDataBroadcastReceiver(),
            new IntentFilter(LocationUpdateIntentService.ACTION_LOCATION_CAPTURED));
        mLocationReporter = new LocationReporter(this);
        mLocationReporter.startWithPermission();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        mLocationReporter.onRequestPermissionsResult(requestCode, grantResults);
    }

    private void observeViewModel() {
        final Observer<Battery> batteryObserver = new Observer<Battery>() {
            @Override
            public void onChanged(@Nullable final Battery battery) {
                if (battery == null) {
                    return;
                }

                ((TextView) findViewById(R.id.powerConnection))
                    .setText(battery.getPowerConnection());
                ((TextView) findViewById(R.id.chargedPercent))
                    .setText(String.valueOf(battery.getChargedPercent()));
            }
        };

        AppDatabase
            .getInstance(this)
            .batteryDao()
            .getLatest()
            .observe(this, batteryObserver);
    }

    private void initializeStetho() {
        if (BuildConfig.DEBUG && !isRoboTest()) {
            Stetho.initializeWithDefaults(this);
        }
    }

    private static boolean isRoboTest() {
        return "robolectric".equals(Build.FINGERPRINT);
    }
}
