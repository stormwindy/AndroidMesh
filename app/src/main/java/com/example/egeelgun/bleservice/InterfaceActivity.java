package com.example.egeelgun.bleservice;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class InterfaceActivity extends Activity {
    private final String TAG = InterfaceActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDR = "DEVICE_ADDR";

    private String bAddress;    //Address of the bluetoth device that will be used.

    private LEService leService;
    private boolean bConnected;
    private TextView bConnectionState;
    private String bDeviceName;
    private String bDeviceAddr;
    private ExpandableListView bDeviceLiestview;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_service_characteristics);

        final Intent intent = getIntent();
        bDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        bDeviceAddr = intent.getStringExtra(EXTRAS_DEVICE_ADDR);

        //UI references. Taken ready from a Google sample. Code is copyleft. Modified for current
        // field names.
        ((TextView) findViewById(R.id.device_address)).setText(bDeviceAddr);
        bDeviceLiestview = (ExpandableListView) findViewById(R.id.gatt_services_list);
        bDeviceLiestview.setOnChildClickListener(devListCliclListener);
    }

    private final ServiceConnection bServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            leService = ((LEService.LocalBinder) iBinder).getService();
            if (!leService.active()) {
                Log.e(TAG, "Unable to start Bluetooth Service.");
                finish();
            }
            leService.connect(bAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            leService = null;
        }
    };

    private final BroadcastReceiver bleGattReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (leService.GATT_CONNECTED.equals(action)) {
                bConnected = true;

            }
        }
    };

    private void updateConnectionStateText (final int resourceID) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //TODO: Additional method implementation required for this method.
            }
        });
    }

    private ExpandableListView.OnChildClickListener devListCliclListener
            = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView expandableListView,
                                    View view, int i, int i1, long l) {
            //TODO: Implement listener.
            return false;
        }
    };
}
