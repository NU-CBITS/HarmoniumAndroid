package edu.northwestern.cbits.harmonium.receivers;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import edu.northwestern.cbits.harmonium.models.AppDatabase;
import edu.northwestern.cbits.harmonium.models.Battery;

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
    private AppDatabase mAppDatabase;

    @Before
    public void setupReceiverAndDatabase() {
        mReceiver = new PowerConnectionReceiver();
        mAppDatabase = Room
            .inMemoryDatabaseBuilder(RuntimeEnvironment.application, AppDatabase.class)
            .allowMainThreadQueries()
            .build();
    }

    @After
    public void closeDatabase() {
        mAppDatabase.close();
    }

    @Test
    public void onReceiveIntent_recordsChargedPercent() throws InterruptedException {
        final Intent intent = batteryChangedIntent()
            .putExtra(BatteryManager.EXTRA_LEVEL, 1)
            .putExtra(BatteryManager.EXTRA_SCALE, 2);
        context().sendStickyBroadcast(intent);

        mReceiver.onReceiveIntent(context(), new Intent(), mAppDatabase);

        waitForDatabaseInsert();
        assertEquals(0.5, firstBattery().getChargedPercent(),0.01);
    }

    @Test
    public void onReceiveIntent_recordsPowerConnection() throws InterruptedException {
        final Intent intent = batteryChangedIntent()
            .putExtra(BatteryManager.EXTRA_PLUGGED, BatteryManager.BATTERY_PLUGGED_WIRELESS);
        context().sendStickyBroadcast(intent);

        mReceiver.onReceiveIntent(
            context(),
            new Intent(Intent.ACTION_POWER_CONNECTED),
            mAppDatabase
        );

        waitForDatabaseInsert();
        assertEquals("Plugged into wireless charger", firstBattery().getPowerConnection());
    }

    @Test
    public void onReceiveIntent_recordsPowerDisconnected() throws InterruptedException {
        context().sendStickyBroadcast(batteryChangedIntent());

        mReceiver.onReceiveIntent(
            context(),
            new Intent(Intent.ACTION_POWER_DISCONNECTED),
            mAppDatabase
        );

        waitForDatabaseInsert();
        assertEquals("Power source disconnected", firstBattery().getPowerConnection());
    }

    @Test
    public void onReceiveIntent_recordsHealth() throws InterruptedException {
        final Intent intent = batteryChangedIntent()
            .putExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_COLD);
        context().sendStickyBroadcast(intent);

        mReceiver.onReceiveIntent(context(), new Intent(), mAppDatabase);

        waitForDatabaseInsert();
        assertEquals("Cold", firstBattery().getHealth());
    }

    @Test
    @Config (minSdk = LOLLIPOP)
    public void onReceiveIntent_recordsCapacity() throws InterruptedException {
        context().sendStickyBroadcast(batteryChangedIntent());

        mReceiver.onReceiveIntent(context(), new Intent(), mAppDatabase);

        waitForDatabaseInsert();
        assertEquals(Integer.MIN_VALUE, firstBattery().getCapacity());
    }

    @Test
    @Config (minSdk = LOLLIPOP)
    public void onReceiveIntent_recordsChargeCounter() throws InterruptedException {
        context().sendStickyBroadcast(batteryChangedIntent());

        mReceiver.onReceiveIntent(context(), new Intent(), mAppDatabase);

        waitForDatabaseInsert();
        assertEquals(Integer.MIN_VALUE, firstBattery().getChargeCounter());
    }

    @Test
    @Config (minSdk = LOLLIPOP)
    public void onReceiveIntent_recordsCurrentAverage() throws InterruptedException {
        context().sendStickyBroadcast(batteryChangedIntent());

        mReceiver.onReceiveIntent(context(), new Intent(), mAppDatabase);

        waitForDatabaseInsert();
        assertEquals(Integer.MIN_VALUE, firstBattery().getCurrentAverage());
    }

    @Test
    @Config (minSdk = LOLLIPOP)
    public void onReceiveIntent_recordsCurrentNow() throws InterruptedException {
        context().sendStickyBroadcast(batteryChangedIntent());

        mReceiver.onReceiveIntent(context(), new Intent(), mAppDatabase);

        waitForDatabaseInsert();
        assertEquals(Integer.MIN_VALUE, firstBattery().getCurrentNow());
    }

    @Test
    @Config (minSdk = LOLLIPOP)
    public void onReceiveIntent_recordsEnergyCounter() throws InterruptedException {
        context().sendStickyBroadcast(batteryChangedIntent());

        mReceiver.onReceiveIntent(context(), new Intent(), mAppDatabase);

        waitForDatabaseInsert();
        assertEquals(Integer.MIN_VALUE, firstBattery().getEnergyCounter());
    }

    @Test
    public void onReceiveIntent_recordsTechnology() throws InterruptedException {
        final Intent intent = batteryChangedIntent()
            .putExtra(BatteryManager.EXTRA_TECHNOLOGY, "flux capacitor");
        context().sendStickyBroadcast(intent);

        mReceiver.onReceiveIntent(context(), new Intent(), mAppDatabase);

        waitForDatabaseInsert();
        assertEquals("flux capacitor", firstBattery().getTechnology());
    }

    @Test
    public void onReceiveIntent_recordsTemperature() throws InterruptedException {
        final Intent intent = batteryChangedIntent()
            .putExtra(BatteryManager.EXTRA_TEMPERATURE, 7);
        context().sendStickyBroadcast(intent);

        mReceiver.onReceiveIntent(context(), new Intent(), mAppDatabase);

        waitForDatabaseInsert();
        assertEquals(7, firstBattery().getTemperature());
    }

    @Test
    public void onReceiveIntent_recordsVoltage() throws InterruptedException {
        final Intent intent = batteryChangedIntent()
            .putExtra(BatteryManager.EXTRA_VOLTAGE, 64);
        context().sendStickyBroadcast(intent);

        mReceiver.onReceiveIntent(context(), new Intent(), mAppDatabase);

        waitForDatabaseInsert();
        assertEquals(64, firstBattery().getVoltage());
    }

    @Test
    public void onReceiveIntent_recordsChargingStatus() throws InterruptedException {
        final Intent intent = batteryChangedIntent()
            .putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_FULL);
        context().sendStickyBroadcast(intent);

        mReceiver.onReceiveIntent(context(), new Intent(), mAppDatabase);

        waitForDatabaseInsert();
        assertEquals("Full", firstBattery().getChargingStatus());
    }

    private Intent batteryChangedIntent() {
        return new Intent(Intent.ACTION_BATTERY_CHANGED);
    }

    private void waitForDatabaseInsert() throws InterruptedException {
        // the database insertion happens on a separate thread, so we wait...
        while (mAppDatabase.batteryDao().count() == 0) {
            Thread.sleep(100);
        }
    }

    private Context context() {
        return RuntimeEnvironment.application;
    }

    private Battery firstBattery() {
        return mAppDatabase.batteryDao().fetchAll()[0];
    }
}
