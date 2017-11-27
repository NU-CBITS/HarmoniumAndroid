package edu.northwestern.cbits.harmonium_android_location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import edu.northwestern.cbits.harmonium_android_location.services.LocationUpdateIntentService;

/*
 * Manages Fine Location permission requests and starts
 */
public class LocationReporter implements
        ConnectionCallbacks,
        OnConnectionFailedListener {
    private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 1612;

    private final Activity mActivity;
    private final Context mContext;
    private final FusedLocationProviderClient mFusedLocationClient;

    public LocationReporter(Activity activity) {
        mActivity = activity;
        mContext = activity.getApplicationContext();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
    }

    public void startWithPermission() {
        if (!hasPermission()) {
            addPermission();
        } else {
            startUpdates();
        }
    }

    public void onRequestPermissionsResult(int requestCode, int[] grantResults) {
        if (requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startUpdates();
        }
    }

    @SuppressLint ("MissingPermission")
    @Override
    public void onConnected(Bundle bundle) {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10);
        locationRequest.setFastestInterval(10);
        locationRequest.setMaxWaitTime(10);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationClient.requestLocationUpdates(locationRequest, pendingIntent());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        System.out.println("failed");
    }

    @Override
    public void onConnectionSuspended(int thing) {

    }

    private void startUpdates() {
        getLocationApiClient();
    }

    private boolean hasPermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
        ContextCompat
        .checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED;
    }

    private void addPermission() {
        ActivityCompat.requestPermissions(mActivity,
            new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
            ACCESS_FINE_LOCATION_REQUEST_CODE);
    }

    private void getLocationApiClient() {
        new GoogleApiClient.Builder(mContext)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
            .connect();
    }

    private PendingIntent pendingIntent() {
        final Intent intent = new Intent(mContext, LocationUpdateIntentService.class);
        intent.setAction(LocationUpdateIntentService.ACTION_PROCESS_UPDATES);

        return PendingIntent.getBroadcast(
            mContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        );
    }
}
