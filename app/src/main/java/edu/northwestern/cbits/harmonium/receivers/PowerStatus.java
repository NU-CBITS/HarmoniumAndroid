package edu.northwestern.cbits.harmonium.receivers;

import android.os.BatteryManager;

import edu.northwestern.cbits.harmonium.R;

class PowerStatus {
    private final int mPluggedState;

    PowerStatus(int pluggedState) {
        mPluggedState = pluggedState;
    }

    int powerConnection() {
        final int powerConnection;

        switch (mPluggedState) {
            case 0:
                powerConnection = R.string.running_on_battery;
                break;
            case BatteryManager.BATTERY_PLUGGED_AC:
                powerConnection = R.string.plugged_into_ac;
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                powerConnection = R.string.plugged_into_usb;
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                powerConnection = R.string.plugged_into_wireless;
                break;
            default:
                powerConnection = R.string.unknown_plugged_state;
        }

        return powerConnection;
    }
}
