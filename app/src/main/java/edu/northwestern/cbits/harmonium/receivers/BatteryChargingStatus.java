package edu.northwestern.cbits.harmonium.receivers;

import android.os.BatteryManager;

import edu.northwestern.cbits.harmonium.R;

class BatteryChargingStatus {
    private final int mStatus;

    BatteryChargingStatus(int status) {
        mStatus = status;
    }

    int status() {
        final int status;

        switch (mStatus) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                status = R.string.battery_status_charging;
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                status = R.string.battery_status_discharging;
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                status = R.string.battery_status_full;
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                status = R.string.battery_status_not_charging;
                break;
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
            default:
                status = R.string.battery_status_unknown;
        }

        return status;
    }
}
