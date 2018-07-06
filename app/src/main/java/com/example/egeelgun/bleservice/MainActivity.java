/**
 * Author: Ege Elgun.
 * Data: 06.07.2018
 * All rights reserved: ICTerra Information and Communication Technologies Inc.
 */

package com.example.egeelgun.bleservice;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show();
            finish();
        }
        LEScan scanner = new LEScan();
        scanner.scanDevice(true);

    }

    protected void onResume(Bundle saveInstanceState) {

    }
}
