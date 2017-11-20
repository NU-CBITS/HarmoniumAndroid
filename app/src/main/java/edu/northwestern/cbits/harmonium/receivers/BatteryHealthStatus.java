package edu.northwestern.cbits.harmonium.receivers;

import android.os.BatteryManager;

import edu.northwestern.cbits.harmonium.R;

class BatteryHealthStatus {
    private final int mHealth;

    BatteryHealthStatus(int health) {
        mHealth = health;
    }

    int health() {
        final int health;

        switch (mHealth) {
            case BatteryManager.BATTERY_HEALTH_COLD:
                health = R.string.battery_health_cold;
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                health = R.string.battery_health_dead;
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                health = R.string.battery_health_good;
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                health = R.string.battery_health_overheat;
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                health = R.string.battery_health_over_voltage;
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                health = R.string.battery_health_unspecified_failure;
                break;
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
            default:
                health = R.string.battery_health_unknown;
        }

        return health;
    }
}
