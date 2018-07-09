package com.example.egeelgun.bleservice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class InterfaceActivity extends Activity {
    private final String TAG = InterfaceActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDR = "DEVICE_ADDR";

    private String bAddress;    //Address of the bluetoth device that will be used.

    private LEService bService;

    private final ServiceConnection bServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bService = ((LEService.LocalBinder) iBinder).getService();
            if (!bService.active()) {
                Log.e(TAG, "Unable to start Bluetooth Service.");
                finish();
            }
            bService.connect(bAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }
}
