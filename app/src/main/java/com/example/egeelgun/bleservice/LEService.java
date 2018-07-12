package com.example.egeelgun.bleservice;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothGattCallback;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;

import static android.nfc.NfcAdapter.EXTRA_DATA;

public abstract class LEService extends Service {

    public final static String GATT_CONNECTED = "com.example.egeelgun.bleservice.GATT_CONNECTED";
    public final static String GATT_DISSCONNECTED = "com.example.egeelgun.bleservice.GATT_DISCONNECTED";
    public final static String GATT_DISCOVERED = "com.example.egeelgun.bleservice.GATT_DISCOVERED";
    public final static String DATA_AVAILABLE = "com.example.egeelgun.bleservice.DATA_AVAILABLE";

    Activity active;
    private static final int PERMISSION_REQUEST_CODE = 1;
    public static int SDK_INT;
    private BluetoothManager mManager = (BluetoothManager)
            getSystemService(Context.BLUETOOTH_SERVICE);
    private BluetoothAdapter blueAdapter = mManager.getAdapter();
    private final static String TAG = LEService.class.getSimpleName();
    private String blueDeviceAddr;
    private BluetoothGatt bGatt;
    private static int conState = 0; //State 0 = not connected, 1 = connecting, 2 = connected.
    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(GattAttributes.heartRate);

    public LEService(Activity active) {
        this.active = active;
    }

    public void init(){
        SDK_INT = Build.VERSION.SDK_INT;
        if (SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(active,
                Manifest.permission.BLUETOOTH_ADMIN ) != PackageManager.PERMISSION_GRANTED ) {
            if(blueAdapter == null || !blueAdapter.isEnabled()) {
                Intent enableBlueIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBlueIntent);
            }

            if(this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle("Location access is required");
                alertBuilder.setPositiveButton(android.R.string.ok, null);
                alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        System.exit(0);
                        }
                });
            }
        }
    }

    public class LocalBinder extends Binder {
        LEService getService() {
            return LEService.this;
        }
    }

    public boolean active() {
        if(mManager == null) {
            mManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if(mManager == null) {
                return false;
            }
        }
        return true;
    }

    public boolean connect(String addr) {
        if(blueAdapter == null || addr == null) {
            Log.w(TAG, "Trying to use and existing connection.");
            return false;
        }

        if((blueAdapter != null && blueDeviceAddr != null && addr.equals(blueDeviceAddr)
                && bGatt != null)) {
            Log.d(TAG, "Trying to connecto to an existing GATT device.");
            if(bGatt.connect()) {
                conState = 1;
                return true;
            } else {
                return false;
            }

        }

        final BluetoothDevice device = blueAdapter.getRemoteDevice(addr);
        if (device == null) {
            Log.w(TAG, "No device has found.");
            return false;
        }
        bGatt = device.connectGatt(this, false, bCallBack);
        Log.d(TAG, "Attempting to create a new connection.");
        blueDeviceAddr = addr;
        conState = 1;
        return true;
    }

    private final BluetoothGattCallback bCallBack = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAct;
            if(newState == 3) {
                conState = 3;
                intentAct = "com.example.egeelgun.bleservice.GATT_CONNECTED";
                broadcastUpdate(intentAct);
                Log.i(TAG, "Connection to GATT Service initialized");
                Log.i(TAG, "Attempting to find GATT devices by discovering." +
                        bGatt.discoverServices());
            } else if (newState == 1) {
                conState = 1;
                intentAct = "com.example.egeelgun.bleservice.GATT_DISCONNECTED";
                Log.i(TAG, "Disconnected from the GATT server");
                broadcastUpdate(intentAct);
            }
        }

        public void onServiceDiscovered(BluetoothGatt gatt, int status) {
            if(status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate("com.example.egeelgun.bleservice.GATT_DISCOVERED");
            } else {
                Log.w(TAG, "Service status" + status);
            }
        }

        public void onCharacteristicRead(BluetoothGatt gatt,
                                         final BluetoothGattCharacteristic charac, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate("com.example.egeelgun.bleservice.DATA_AVAILABLE", charac);
            }
        }

        public void inCharacteristicChange(BluetoothGatt gatt,
                                           BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate("com.example.egeelgun.bleservice.DATA_AVAILABLE"
                        + characteristic);
            }
        }
    };

    private void broadcastUpdate(final String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String act, final BluetoothGattCharacteristic characteristic) {
        //Used ready code because I have no idea how to use a parser.
        //TODO: Understand what this code does.
        final Intent intent = new Intent(act);

        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }

    public void onServiceDiscover(BluetoothGatt gatt, int status) {
        if(status == BluetoothGatt.GATT_SUCCESS) {
            //TODO: BroadcastUpdate will be used when implemented.
        }
    }

    public void disconnect () {
        if (blueAdapter == null || bGatt == null) {
            Log.w(TAG, "Bluetooth adapter is either not initialized or stopped working.");
            return;
        }
        bGatt.disconnect();
    }

    public void close() {
        if (bGatt == null) {
            return;
        }
        bGatt.close();
        bGatt = null;
    }

    public void readGATTCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (blueAdapter == null || bGatt == null) {
            Log.w(TAG, "Bluetooth not initialized.");
            return;
        }
        bGatt.readCharacteristic(characteristic);
    }
}
