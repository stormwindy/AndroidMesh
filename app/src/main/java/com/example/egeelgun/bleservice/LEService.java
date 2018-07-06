/**
 * Author: Ege Elgun.
 * Data: 06.07.2018
 * All rights reserved: ICTerra Information and Communication Technologies Inc.
 */
package com.example.egeelgun.bleservice;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Service;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothGattCallback;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;

public abstract class LEService extends Service {
    Activity active;
    private static final int PERMISSION_REQUEST_CODE = 1;
    public static int SDK_INT;
    final BluetoothManager mManager = (BluetoothManager)
            getSystemService(Context.BLUETOOTH_SERVICE);
    private BluetoothAdapter blueAdapter = mManager.getAdapter();
    private final static String TAG = LEService.class.getSimpleName();
    private String blueDeviceAddr;
    private BluetoothGatt bGatt;
    private static int conState = 0; //State 0 = not connected, 1 = connecting, 2 = connected.


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

/*    private static BluetoothGattCallback bCallBack = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intent;
            if (newState == 2) {
                intent = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
                conState = 2; //Change state to connected.
                broadcastUpdate(intent);

            }
        }
    };
*/
}
