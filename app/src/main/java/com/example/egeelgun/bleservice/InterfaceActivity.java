package com.example.egeelgun.bleservice;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class InterfaceActivity extends Activity {
    private final String TAG = InterfaceActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDR = "DEVICE_ADDR";

    private LEService leService;
    private boolean bConnected;
    private TextView bConnectionState;
    private TextView bDataField;
    private String bDeviceName;
    private String bDeviceAddr;
    private ExpandableListView bDeviceLiestview;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> bCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private BluetoothGattCharacteristic notifyCharacteristic;
    private String uuid;

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
        registerReceiver(bleGattReciever, filter());
        if(leService != null) {
            final boolean conResult = leService.connect(bDeviceAddr);
            Log.i(TAG, "Notification:" + conResult);
        }
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(bleGattReciever);
    }

    public void onDestroy() {
        super.onDestroy();
        unbindService(bServiceConnection);
        leService = null;
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
                invalidateOptionsMenu();
            }
            if (leService.GATT_DISCOVERED.equals(action)) {
                displayGATTServices(leService.servicesList());
            }
            if (leService.DATA_AVAILABLE.equals(action)) {
                Log.i(TAG, "DATAA_AVAILABLE");
            }
        }
    };

    private void updateConnectionStateText (final int resourceID) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bConnectionState.setText(resourceID);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if(bConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    public boolean onOptionItemSelect(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                leService.connect(bDeviceAddr);
                return true;
            case R.id.menu_disconnect:
                leService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }
    private ExpandableListView.OnChildClickListener devListCliclListener
            = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView expandableListView,
                                    View view, int i, int i1, long l) {
            //WARNING - MAY NOT WORK.
            if(bCharacteristics != null) {
                BluetoothGattCharacteristic pivotCharacteristic = bCharacteristics.get(i).get(i1);
                final int properties = pivotCharacteristic.getProperties();

                if((properties | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    leService.readGATTCharacteristic(pivotCharacteristic);
                }
                return true;
            }
            return false;
        }
    };

    public void displayGATTServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) {
            return;
        }
        String uuid;
        String unknownServices = getResources().getString(R.string.unknown_service);
        String unKnownCharacteristics = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> serviceInfo = new ArrayList<>();
        ArrayList<ArrayList<HashMap<String, String>>> characInfo = new ArrayList<>();
        String unknownName = getResources().getString(R.string.unknown_service);

        for (BluetoothGattService service : gattServices) {
            HashMap<String, String> currServiceData = new HashMap<String, String>();
            uuid = service.getUuid().toString();
            currServiceData.put("NAME", GattAttributes.search(uuid, unknownName));
            currServiceData.put("UUID", uuid);
            serviceInfo.add(currServiceData);
            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<>();
            ArrayList<BluetoothGattCharacteristic> chars = new ArrayList<>();
            for (BluetoothGattCharacteristic charas : bCharacteristics.get(0)) {
                chars.add(charas);
                HashMap<String, String> currCharData = new HashMap<>();
                uuid = charas.getUuid().toString();
                currCharData.put("UUIS", uuid);
                gattCharacteristicGroupData.add(currCharData);
            }
            bCharacteristics.add(chars);
            characInfo.add(gattCharacteristicGroupData);
        }
        SimpleExpandableListAdapter gattServAdapter = new SimpleExpandableListAdapter(
          this,
          serviceInfo,
          android.R.layout.simple_expandable_list_item_2,
                new String[] {"NAME", "UUID"},
                new int[] {android.R.id.text1, android.R.id.text2},
                characInfo,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {"NAME", "UUID"},
                new int[] {android.R.id.text1, android.R.id.text2}
        );
        bDeviceLiestview.setAdapter(gattServAdapter);
    }

    public void updateState (final int resourceId){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bConnectionState.setText(resourceId);
            }
        });
    }

    public static IntentFilter filter() {
      final IntentFilter intentFilter = new IntentFilter();
      intentFilter.addAction(LEService.DATA_AVAILABLE);
      intentFilter.addAction(LEService.GATT_DISCOVERED);
      intentFilter.addAction(LEService.GATT_CONNECTED);
      intentFilter.addAction(LEService.GATT_DISSCONNECTED);
      return intentFilter;
    }

    public void displayData(String data) {
        if (data != null) {
            bDataField.setText(data);
        }
    }

    private void clearUI() {
        bDeviceLiestview.setAdapter((SimpleExpandableListAdapter) null);
        bDataField.setText(R.string.no_data);
    }
}
