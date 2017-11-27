package edu.northwestern.cbits.harmonium_android_battery;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.UUID;

@Entity
public class Battery implements Parcelable {
    @PrimaryKey
    @NonNull
    private String uuid;
    private long clientCreatedAt;
    private int capacity;
    private int chargeCounter;
    private float chargedPercent;
    private String chargingStatus;
    private int currentAverage;
    private int currentNow;
    private int energyCounter;
    private String health;
    private String powerConnection;
    private String technology;
    private int temperature;
    private int voltage;

    public Battery() {
        uuid = UUID.randomUUID().toString();
        clientCreatedAt = System.currentTimeMillis();
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getChargeCounter() {
        return chargeCounter;
    }

    public void setChargeCounter(int chargeCounter) {
        this.chargeCounter = chargeCounter;
    }

    public float getChargedPercent() {
        return chargedPercent;
    }

    public void setChargedPercent(float chargedPercent) {
        this.chargedPercent = chargedPercent;
    }

    public int getEnergyCounter() {
        return energyCounter;
    }

    public void setEnergyCounter(int energyCounter) {
        this.energyCounter = energyCounter;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public String getPowerConnection() {
        return powerConnection;
    }

    public void setPowerConnection(String powerConnection) {
        this.powerConnection = powerConnection;
    }

    public int getCurrentAverage() {
        return currentAverage;
    }

    public void setCurrentAverage(int currentAverage) {
        this.currentAverage = currentAverage;
    }

    public int getCurrentNow() {
        return currentNow;
    }

    public void setCurrentNow(int currentNow) {
        this.currentNow = currentNow;
    }

    public String getChargingStatus() {
        return chargingStatus;
    }

    public void setChargingStatus(String chargingStatus) {
        this.chargingStatus = chargingStatus;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getVoltage() {
        return voltage;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Battery createFromParcel(Parcel in) {
            return new Battery(in);
        }
        public Battery[] newArray(int size) {
            return new Battery[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getUuid());
        dest.writeLong(getClientCreatedAt());
        dest.writeInt(getCapacity());
        dest.writeInt(getChargeCounter());
        dest.writeFloat(getChargedPercent());
        dest.writeString(getChargingStatus());
        dest.writeInt(getCurrentAverage());
        dest.writeInt(getCurrentNow());
        dest.writeInt(getEnergyCounter());
        dest.writeString(getHealth());
        dest.writeString(getPowerConnection());
        dest.writeString(getTechnology());
        dest.writeInt(getTemperature());
        dest.writeInt(getVoltage());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private Battery(Parcel in) {
        setUuid(in.readString());
        setClientCreatedAt(in.readLong());
        setCapacity(in.readInt());
        setChargeCounter(in.readInt());
        setChargedPercent(in.readFloat());
        setChargingStatus(in.readString());
        setCurrentAverage(in.readInt());
        setCurrentNow(in.readInt());
        setEnergyCounter(in.readInt());
        setHealth(in.readString());
        setPowerConnection(in.readString());
        setTechnology(in.readString());
        setTemperature(in.readInt());
        setVoltage(in.readInt());
    }
}