package edu.northwestern.cbits.harmonium.models;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import edu.northwestern.cbits.harmonium_android_battery.Battery;
import edu.northwestern.cbits.harmonium_android_battery.BatteryDao;
import edu.northwestern.cbits.harmonium_android_location.models.Location;
import edu.northwestern.cbits.harmonium_android_location.models.LocationDao;

@Database (entities = {
    Battery.class,
    Location.class
}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BatteryDao batteryDao();
    public abstract LocationDao locationDao();

    private static AppDatabase INSTANCE;
    private static final Object sLock = new Object();

    // RoomDatabase instances are expensive, so use singleton.
    public static AppDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class, "eralert.db"
                )
                    .addMigrations()
                    .build();
            }

            return INSTANCE;
        }
    }
}