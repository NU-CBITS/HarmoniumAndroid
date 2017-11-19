package edu.northwestern.cbits.harmonium.receivers;

import android.os.BatteryManager;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import edu.northwestern.cbits.harmonium.R;
import edu.northwestern.cbits.harmonium.models.AppDatabase;
import edu.northwestern.cbits.harmonium.models.Battery;

public class PowerConnectionReceiver extends Receiver {
    @Override
    public void onReceiveIntent(Context context, Intent intent, AppDatabase appDatabase) {
        final Intent chargingIntent = chargingIntent(context);
        final Battery battery = new Battery();

        battery.setChargedPercent(chargedPercent(chargingIntent));

        if (Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())) {
            battery.setPowerConnection(context.getString(powerConnection(chargingIntent)));
        } else if (Intent.ACTION_POWER_DISCONNECTED.equals(intent.getAction())) {
            battery.setPowerConnection(context.getString(R.string.power_disconnected));
        }

        insertBatteryReading(appDatabase, battery);
    }

    private Intent chargingIntent(Context context) {
        final Intent chargingIntent = context.registerReceiver(
            null,
            new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        );

        if (chargingIntent == null) {
            return new Intent();
        }

        return chargingIntent;
    }

    private int powerConnection(Intent chargingIntent) {
        final int pluggedState =
            chargingIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

        return new PowerStatus(pluggedState).powerConnection();
    }

    private float chargedPercent(Intent chargingIntent) {
        final int level = chargingIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        final int scale = chargingIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        return level / (float) scale;
    }

    private void insertBatteryReading(final AppDatabase appDatabase, final Battery battery) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                appDatabase.batteryDao().insert(battery);
            }
        }).start();
    }
}
