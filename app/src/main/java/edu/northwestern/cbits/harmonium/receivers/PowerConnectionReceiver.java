package edu.northwestern.cbits.harmonium.receivers;

import android.os.BatteryManager;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;

import edu.northwestern.cbits.harmonium.R;
import edu.northwestern.cbits.harmonium.models.AppDatabase;
import edu.northwestern.cbits.harmonium.models.Battery;

public class PowerConnectionReceiver extends Receiver {
    private Context mContext;
    private Intent mChargingIntent;
    private BatteryManager mBatteryManager;

    @Override
    public void onReceiveIntent(Context context, Intent intent, AppDatabase appDatabase) {
        mContext = context.getApplicationContext();
        mChargingIntent = chargingIntent();

        if (isAtLeastLollipop()) {
            mBatteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        }

        final Battery battery = new Battery();

        battery.setCapacity(capacity());
        battery.setChargeCounter(chargeCounter());
        battery.setChargedPercent(chargedPercent());
        battery.setCurrentAverage(currentAverage());
        battery.setCurrentNow(currentNow());
        battery.setEnergyCounter(energyCounter());
        battery.setHealth(context.getString(health()));
        battery.setPowerConnection(powerConnection(intent));
        battery.setTechnology(technology());
        battery.setTemperature(temperature());
        battery.setVoltage(voltage());
        battery.setChargingStatus(chargingStatus());

        insertBatteryReading(appDatabase, battery);
    }

    private Intent chargingIntent() {
        final Intent chargingIntent = mContext.registerReceiver(
            null,
            new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        );

        if (chargingIntent == null) {
            return new Intent();
        }

        return chargingIntent;
    }

    private String powerConnection(Intent intent) {
        if (Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())) {
            final int pluggedState =
                mChargingIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

            return mContext.getString(new PowerStatus(pluggedState).powerConnection());
        } else if (Intent.ACTION_POWER_DISCONNECTED.equals(intent.getAction())) {
            return mContext.getString(R.string.power_disconnected);
        }

        return mContext.getString(R.string.unknown_plugged_state);
    }

    private float chargedPercent() {
        final int level = mChargingIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        final int scale = mChargingIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        return level / (float) scale;
    }

    private int health() {
        final int health = mChargingIntent
            .getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN);

        return new BatteryHealthStatus(health).health();
    }

    private int capacity() {
        if (isAtLeastLollipop()) {
            return mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }

        return -1;
    }

    private int chargeCounter() {
        if (isAtLeastLollipop()) {
            return mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
        }

        return -1;
    }

    private int currentAverage() {
        if (isAtLeastLollipop()) {
            return mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
        }

        return -1;
    }

    private int currentNow() {
        if (isAtLeastLollipop()) {
            return mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
        }

        return -1;
    }

    private int energyCounter() {
        if (isAtLeastLollipop()) {
            return mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);
        }

        return -1;
    }

    private String technology() {
        return mChargingIntent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
    }

    private int temperature() {
        return mChargingIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Integer.MIN_VALUE);
    }

    private int voltage() {
        return mChargingIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, Integer.MIN_VALUE);
    }

    private String chargingStatus() {
        final int status = mChargingIntent
            .getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);

        return mContext.getString(new BatteryChargingStatus(status).status());
    }

    private boolean isAtLeastLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
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
