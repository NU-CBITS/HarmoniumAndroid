package edu.northwestern.cbits.harmonium.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import edu.northwestern.cbits.harmonium.models.AppDatabase;

abstract class Receiver extends BroadcastReceiver {
    abstract void onReceiveIntent(Context context, Intent intent, AppDatabase appDatabase);

    @Override
    public void onReceive(Context context, Intent intent) {
        onReceiveIntent(context, intent, AppDatabase.getInstance(context.getApplicationContext()));
    }
}