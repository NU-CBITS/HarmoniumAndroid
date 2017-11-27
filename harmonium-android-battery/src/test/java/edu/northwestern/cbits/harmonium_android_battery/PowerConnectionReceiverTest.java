package edu.northwestern.cbits.harmonium_android_battery;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.util.List;

import static android.os.Build.VERSION_CODES.KITKAT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static org.junit.Assert.assertEquals;

/*
 * Note: ACTION_BATTERY_CHANGED is sticky so we broadcast it ahead of receiving the
 * ACTION_POWER_CONNECTED or ACTION_POWER_DISCONNECTED intent.
 */
@RunWith (RobolectricTestRunner.class)
@Config (minSdk = KITKAT)
public class PowerConnectionReceiverTest {
    private PowerConnectionReceiver mReceiver;
    private Intent mBroadcastIntent = null;

    @Before
    public void setupReceiver() {
        mReceiver = new PowerConnectionReceiver();
    }

    @Test
    public void onReceive_capturesChargedPercent() throws InterruptedException {
        final Intent intent = batteryChangedIntent()
            .putExtra(BatteryManager.EXTRA_LEVEL, 1)
            .putExtra(BatteryManager.EXTRA_SCALE, 2);
        context().sendStickyBroadcast(intent);

        mReceiver.onReceive(context(), new Intent());

        assertBatteryRecordedIntentBroadcast();
        assertEquals(0.5, batteryCaptured().getChargedPercent(), 0.01);
    }

    @Test
    public void onReceive_capturesPowerConnection() throws InterruptedException {
        final Intent intent = batteryChangedIntent()
            .putExtra(BatteryManager.EXTRA_PLUGGED, BatteryManager.BATTERY_PLUGGED_WIRELESS);
        context().sendStickyBroadcast(intent);

        mReceiver.onReceive(context(), new Intent(Intent.ACTION_POWER_CONNECTED));

        assertBatteryRecordedIntentBroadcast();
        assertEquals(
            "Plugged into wireless charger",
            batteryCaptured().getPowerConnection()
        );
    }

    @Test
    public void onReceive_recordsPowerDisconnected() throws InterruptedException {
        context().sendStickyBroadcast(batteryChangedIntent());

        mReceiver.onReceive(context(), new Intent(Intent.ACTION_POWER_DISCONNECTED));

        assertBatteryRecordedIntentBroadcast();
        assertEquals("Power source disconnected", batteryCaptured().getPowerConnection());
    }

    @Test
    public void onReceive_recordsHealth() throws InterruptedException {
        final Intent intent = batteryChangedIntent()
            .putExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_COLD);
        context().sendStickyBroadcast(intent);

        mReceiver.onReceive(context(), new Intent());

        assertBatteryRecordedIntentBroadcast();
        assertEquals("Cold", batteryCaptured().getHealth());
    }

    @Test
    @Config (minSdk = LOLLIPOP)
    public void onReceive_recordsCapacity() throws InterruptedException {
        context().sendStickyBroadcast(batteryChangedIntent());

        mReceiver.onReceive(context(), new Intent());

        assertBatteryRecordedIntentBroadcast();
        assertEquals(Integer.MIN_VALUE, batteryCaptured().getCapacity());
    }

    @Test
    @Config (minSdk = LOLLIPOP)
    public void onReceive_recordsChargeCounter() throws InterruptedException {
        context().sendStickyBroadcast(batteryChangedIntent());

        mReceiver.onReceive(context(), new Intent());

        assertBatteryRecordedIntentBroadcast();
        assertEquals(Integer.MIN_VALUE, batteryCaptured().getChargeCounter());
    }

    @Test
    @Config (minSdk = LOLLIPOP)
    public void onReceive_recordsCurrentAverage() throws InterruptedException {
        context().sendStickyBroadcast(batteryChangedIntent());

        mReceiver.onReceive(context(), new Intent());

        assertBatteryRecordedIntentBroadcast();
        assertEquals(Integer.MIN_VALUE, batteryCaptured().getCurrentAverage());
    }

    @Test
    @Config (minSdk = LOLLIPOP)
    public void onReceive_recordsCurrentNow() throws InterruptedException {
        context().sendStickyBroadcast(batteryChangedIntent());

        mReceiver.onReceive(context(), new Intent());

        assertBatteryRecordedIntentBroadcast();
        assertEquals(Integer.MIN_VALUE, batteryCaptured().getCurrentNow());
    }

    @Test
    @Config (minSdk = LOLLIPOP)
    public void onReceive_recordsEnergyCounter() throws InterruptedException {
        context().sendStickyBroadcast(batteryChangedIntent());

        mReceiver.onReceive(context(), new Intent());

        assertBatteryRecordedIntentBroadcast();
        assertEquals(Integer.MIN_VALUE, batteryCaptured().getEnergyCounter());
    }

    @Test
    public void onReceive_recordsTechnology() throws InterruptedException {
        final Intent intent = batteryChangedIntent()
            .putExtra(BatteryManager.EXTRA_TECHNOLOGY, "flux capacitor");
        context().sendStickyBroadcast(intent);

        mReceiver.onReceive(context(), new Intent());

        assertBatteryRecordedIntentBroadcast();
        assertEquals("flux capacitor", batteryCaptured().getTechnology());
    }

    @Test
    public void onReceive_recordsTemperature() throws InterruptedException {
        final Intent intent = batteryChangedIntent()
            .putExtra(BatteryManager.EXTRA_TEMPERATURE, 7);
        context().sendStickyBroadcast(intent);

        mReceiver.onReceive(context(), new Intent());

        assertBatteryRecordedIntentBroadcast();
        assertEquals(7, batteryCaptured().getTemperature());
    }

    @Test
    public void onReceive_recordsVoltage() throws InterruptedException {
        final Intent intent = batteryChangedIntent()
            .putExtra(BatteryManager.EXTRA_VOLTAGE, 64);
        context().sendStickyBroadcast(intent);

        mReceiver.onReceive(context(), new Intent());

        assertBatteryRecordedIntentBroadcast();
        assertEquals(64, batteryCaptured().getVoltage());
    }

    @Test
    public void onReceive_recordsChargingStatus() throws InterruptedException {
        final Intent intent = batteryChangedIntent()
            .putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_FULL);
        context().sendStickyBroadcast(intent);

        mReceiver.onReceive(context(), new Intent());

        assertBatteryRecordedIntentBroadcast();
        assertEquals("Full", batteryCaptured().getChargingStatus());
    }

    private Intent batteryChangedIntent() {
        return new Intent(Intent.ACTION_BATTERY_CHANGED);
    }

    private void assertBatteryRecordedIntentBroadcast() {
        assertEquals(
            PowerConnectionReceiver.ACTION_BATTERY_RECORDED,
            lastBroadcastIntent().getAction()
        );
    }

    // Returns the Intent broadcast by the class under test.
    private Intent lastBroadcastIntent() {
        if (mBroadcastIntent == null) {
            final List<Intent> intents = ShadowApplication.getInstance().getBroadcastIntents();
            mBroadcastIntent = intents.get(intents.size() - 1);
        }

        return mBroadcastIntent;
    }

    private Battery batteryCaptured() {
        final Bundle batteryExtras = lastBroadcastIntent().getExtras();

        if (batteryExtras == null) {
            return new Battery();
        }

        return (Battery) batteryExtras.get(PowerConnectionReceiver.BATTERY_KEY);
    }

    private Context context() {
        return RuntimeEnvironment.application;
    }
}
