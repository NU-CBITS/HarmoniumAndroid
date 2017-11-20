package edu.northwestern.cbits.harmonium;

import android.arch.lifecycle.Observer;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.stetho.Stetho;

import edu.northwestern.cbits.harmonium.models.AppDatabase;
import edu.northwestern.cbits.harmonium.models.Battery;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        observeViewModel();
        initializeStetho();
    }

    private void observeViewModel() {
        final Observer<Battery> batteryObserver = new Observer<Battery>() {
            @Override
            public void onChanged(@Nullable final Battery battery) {
                if (battery == null) {
                    return;
                }

                ((TextView) findViewById(R.id.powerConnection))
                    .setText(battery.getPowerConnection());
                ((TextView) findViewById(R.id.chargedPercent))
                    .setText(String.valueOf(battery.getChargedPercent()));
            }
        };

        AppDatabase
            .getInstance(this)
            .batteryDao()
            .getLatest()
            .observe(this, batteryObserver);
    }

    private void initializeStetho() {
        if (BuildConfig.DEBUG && !isRoboTest()) {
            Stetho.initializeWithDefaults(this);
        }
    }

    private static boolean isRoboTest() {
        return "robolectric".equals(Build.FINGERPRINT);
    }
}
