package edu.northwestern.cbits.harmonium.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.List;

import edu.northwestern.cbits.harmonium.models.AppDatabase;
import edu.northwestern.cbits.harmonium_android_battery.Battery;
import edu.northwestern.cbits.harmonium_android_battery.PowerConnectionReceiver;
import edu.northwestern.cbits.harmonium_android_location.models.Location;
import edu.northwestern.cbits.harmonium_android_location.services.LocationUpdateIntentService;

public class SensorDataBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final AppDatabase appDatabase = AppDatabase.getInstance(context);

                if (PowerConnectionReceiver.ACTION_BATTERY_RECORDED.equals(intent.getAction())) {
                    final Bundle data = intent.getExtras();

                    if (data == null) {
                        return;
                    }

                    final Battery battery = data.getParcelable(PowerConnectionReceiver.BATTERY_KEY);
                    appDatabase.batteryDao().insert(battery);
                } else if (LocationUpdateIntentService.ACTION_LOCATION_CAPTURED.equals(intent.getAction())) {
                    final Bundle data = intent.getExtras();

                    if (data == null) {
                        return;
                    }

                    final List<Location> locations =
                        data.getParcelableArrayList(LocationUpdateIntentService.LOCATIONS_KEY);

                    if (locations == null) {
                        return;
                    }

                    appDatabase
                        .locationDao()
                        .insertAll(locations.toArray(new Location[locations.size()]));
                }
            }
        };
        new Thread(runnable).start();
    }
}
