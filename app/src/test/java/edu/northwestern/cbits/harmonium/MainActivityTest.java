package edu.northwestern.cbits.harmonium;

import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static android.os.Build.VERSION_CODES.KITKAT;
import static org.junit.Assert.assertEquals;

@RunWith (RobolectricTestRunner.class)
@Config(minSdk = KITKAT)
public class MainActivityTest {
    private MainActivity mActivity;

    @Before
    public void initializeActivity() {
        mActivity = Robolectric.setupActivity(MainActivity.class);
    }

    @Test
    public void activityLoads() {
        assertEquals("power connection", getPowerConnectionText());
    }

    private String getPowerConnectionText() {
        return ((TextView) mActivity.findViewById(R.id.powerConnection)).getText().toString();
    }
}
