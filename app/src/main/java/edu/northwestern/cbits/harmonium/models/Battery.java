package edu.northwestern.cbits.harmonium.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.UUID;

@Entity
public class Battery {
    @PrimaryKey
    @NonNull
    private String uuid;
    private long clientCreatedAt;
    private float chargedPercent;
    private String powerConnection;

    public Battery() {
        this(null);
    }

    public Battery(String powerConnection) {
        uuid = UUID.randomUUID().toString();
        clientCreatedAt = System.currentTimeMillis();
        this.powerConnection = powerConnection;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getClientCreatedAt() {
        return clientCreatedAt;
    }

    public void setClientCreatedAt(long clientCreatedAt) {
        this.clientCreatedAt = clientCreatedAt;
    }

    public float getChargedPercent() {
        return chargedPercent;
    }

    public void setChargedPercent(float chargedPercent) {
        this.chargedPercent = chargedPercent;
    }

    public String getPowerConnection() {
        return powerConnection;
    }

    public void setPowerConnection(String powerConnection) {
        this.powerConnection = powerConnection;
    }
}