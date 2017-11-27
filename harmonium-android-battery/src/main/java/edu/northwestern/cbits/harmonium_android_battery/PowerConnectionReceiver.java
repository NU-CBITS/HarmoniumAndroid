package edu.northwestern.cbits.harmonium_android_battery;

import android.content.BroadcastReceiver;
import android.os.BatteryManager;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;

public class PowerConnectionReceiver extends BroadcastReceiver {
    public static final String ACTION_BATTERY_RECORDED =
        "edu.northwestern.cbits.harmonium.ACTION_BATTERY_RECORDED";
    public static final String BATTERY_KEY = "battery";

    private Context mContext;
    private Intent mChargingIntent;
    private BatteryManager mBatteryManager;

    @Override
    public void onReceive(Context context, Intent intent) {
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

        broadcastBatteryReading(battery);
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

    private void broadcastBatteryReading(final Battery battery) {
        mContext.sendBroadcast(
            new Intent(ACTION_BATTERY_RECORDED)
                .putExtra(BATTERY_KEY, battery)
        );
    }
}
