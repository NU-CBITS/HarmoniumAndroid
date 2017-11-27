package edu.northwestern.cbits.harmonium_android_battery;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface BatteryDao {
    @Query("SELECT * FROM battery ORDER BY clientCreatedAt DESC LIMIT 1")
    LiveData<Battery> getLatest();

    @Query("SELECT * FROM battery")
    public Battery[] fetchAll();

    @Query("SELECT COUNT(1) FROM battery")
    public int count();

    @Insert
    long insert(Battery battery);
}
