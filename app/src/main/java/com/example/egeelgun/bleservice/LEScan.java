/**
 * Author: Ege Elgun.
 * Data: 06.07.2018
 * All rights reserved: ICTerra Information and Communication Technologies Inc.
 */
package com.example.egeelgun.bleservice;

import android.app.ListActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

import android.companion.BluetoothLeDeviceFilter;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.app.ListActivity;
import android.bluetooth.le.ScanCallback;

import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class LEScan extends ListActivity {
    final BluetoothManager mManager = (BluetoothManager)
            getSystemService(Context.BLUETOOTH_SERVICE);
    private BluetoothAdapter bAdapter = mManager.getAdapter();

    private boolean mScan;
    public static final long scanLETime= 10000;
    private Handler bHandler;

    public void scanDevice(final boolean enable) {
        bHandler = new Handler();
        final BluetoothLeScanner leScanner = bAdapter.getBluetoothLeScanner();
        if(enable) {
            bHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScan = false;
                    leScanner.stopScan(bScanCallBack);
                    invalidateOptionsMenu();
                }
            }, scanLETime);
            mScan = true;
            leScanner.startScan(bScanCallBack);
        } else {
            mScan = false;
            leScanner.stopScan(bScanCallBack);
        }
        invalidateOptionsMenu();
    }
    private class LEDeviceList extends BaseAdapter {
        private ArrayList<BluetoothDevice> bDeviceList;
        private LayoutInflater bInflater;

        public LEDeviceList() {
            super();
            bDeviceList = new ArrayList<>();
            bInflater = LEScan.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (bDeviceList.contains(device)) {
                bDeviceList.add(device);
            }
        }

        public BluetoothDevice getDevice(int index) {
            return bDeviceList.get(index);
        }

        public void clear() {
            bDeviceList.clear();
        }

        @Override
        public int getCount() {
            return bDeviceList.size();
        }

        @Override
        public Object getItem(int i) {
            return bDeviceList.get(i);
        }

        @Override
        public long getItemId(int o) {
            return o;
        }

        public View getView(int i, View view, ViewGroup group) {
            ViewInfoHolder infoHolder;
            if (view == null) {
                view = bInflater.inflate(R.layout.device_list, null);
                infoHolder = new ViewInfoHolder();
                infoHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                infoHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(infoHolder);
            } else {
                infoHolder = (ViewInfoHolder) view.getTag();
            }

            BluetoothDevice device = bDeviceList.get(i);
            final String deviceName = device.getName();
            if(deviceName != null && deviceName.length() > 0) {
                infoHolder.deviceName.setText(deviceName);
            } else {
                infoHolder.deviceName.setText("Unknown device.");
                infoHolder.deviceAddress.setText(device.getAddress());
            }
            return view;
        }
    }

    private LEDeviceList deviceList; //Creates a LEDeviceList Object to be used by ViewHolder.

        final ScanCallback bScanCallBack = new ScanCallback() {
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        deviceList.addDevice(device);
                        deviceList.notifyDataSetChanged();
                    }
                });
            }
        };
    static class ViewInfoHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}
