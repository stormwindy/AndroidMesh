package com.example.egeelgun.bleservice;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
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

import java.util.ArrayList;

public class InterfaceActivity extends Activity {
    private final String TAG = InterfaceActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDR = "DEVICE_ADDR";

    private LEService leService;
    private boolean bConnected;
    private TextView bConnectionState;
    private String bDeviceName;
    private String bDeviceAddr;
    private ExpandableListView bDeviceLiestview;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> bCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private BluetoothGattCharacteristic notifyCharacteristic;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_service_characteristics);

        final Intent intent = getIntent();
        bDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        bDeviceAddr = intent.getStringExtra(EXTRAS_DEVICE_ADDR);

        //UI references. Taken ready from a Google sample. Code is copyleft. Modified for current
        // field names.
        ((TextView) findViewById(R.id.device_address)).setText(bDeviceAddr);
        bDeviceLiestview = findViewById(R.id.gatt_services_list);
        bDeviceLiestview.setOnChildClickListener(devListCliclListener);
    }

    public void onResume () {
        super.onResume();
        //registerReceiver(bleGattReciever, X)  //TODO: Fill the function.
        if(leService != null) {
            final boolean conResult = leService.connect(bDeviceAddr);
            Log.i(TAG, "Notification:" + conResult);
        }
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(bleGattReciever); //TODO: Finish broadcastreciever.
    }

    private final ServiceConnection bServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            leService = ((LEService.LocalBinder) iBinder).getService();
            if (!leService.active()) {
                Log.e(TAG, "Unable to start Bluetooth Service.");
                finish();
            }
            leService.connect(bDeviceAddr);
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
                updateConnectionStateText(R.string.connected);
                invalidateOptionsMenu();

            }
            if (leService.GATT_DISSCONNECTED.equals(action)) {
                bConnected = false;
                updateConnectionStateText(R.string.disconnected);
            }
            if (leService.GATT_DISCOVERED.equals(action)) {
                //TODO: Implement a function to display available GATT devices.
            }
            if (leService.DATA_AVAILABLE.equals(action)) {
                //TODO: Up TODO.
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
            //TODO: ADD CASE SCENARIOS.
            if(bCharacteristics != null) {
                BluetoothGattCharacteristic pivotCharacteristic = bCharacteristics.get(i).get(i1);
                final int properties = pivotCharacteristic.getProperties();

                if(properties > 0) {
                    leService.readGATTCharacteristic(pivotCharacteristic);
                }
                return true;
            }
            return false;
        }
    };

    public void updateState (final int resourceId){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bConnectionState.setText(resourceId);
            }
        });
    }
}
