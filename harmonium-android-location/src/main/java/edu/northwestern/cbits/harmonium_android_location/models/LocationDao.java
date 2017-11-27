package edu.northwestern.cbits.harmonium_android_location.models;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface LocationDao {
    @Query("SELECT * FROM location ORDER BY clientCreatedAt DESC LIMIT 1")
    LiveData<Location> fetchLatest();

    @Query("SELECT * FROM location")
    public Location[] fetchAll();

    @Query("SELECT COUNT(1) FROM location")
    public int count();

    @Insert
    long insert(Location Location);

    @Insert
    void insertAll(Location... locations);
}
