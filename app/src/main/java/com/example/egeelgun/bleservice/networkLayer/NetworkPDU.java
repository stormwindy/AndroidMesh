package com.example.egeelgun.bleservice.networkLayer;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.content.Context;

import com.example.egeelgun.bleservice.InterfaceActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang3.BitField;

import static com.example.egeelgun.bleservice.Utils.convert;

public class NetworkPDU extends Activity {
    private final String TAG = NetworkPDU.class.getSimpleName();
    private ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristics;
    public NetworkPDU() {
        InterfaceActivity gattActivity = new InterfaceActivity();
        gattCharacteristics = gattActivity.getCharacterInfo();
    }

    class generic_PDU {
        BitSet IVI;
        BitSet NID;
        BitSet CTL;
        BitSet TTL;
        BitSet SQL;
        BitSet SRC;
        BitSet DST;
        BitSet trnPDU;
        BitSet netMIC;

        public generic_PDU(boolean ctrl, int TTLCount) {
            if (TTLCount > 128 || convert(TTLCount).length() != 7) {
                Log.e(TAG, "TTL exceeds maximum possible relay number.");
                finish();
            }

            IVI = new BitSet(1);
            NID = new BitSet(7);
            CTL = new BitSet(1);
            TTL = convert(TTLCount);
            SQL = new BitSet(24);
            SRC = new BitSet(16);
            DST = new BitSet(16);

            //Transport PDU is initialized as a max size. All empty bits will remain 0.
            if(ctrl) {
                CTL.set(0);
                trnPDU = new BitSet(96);
                netMIC = new BitSet(64);
            } else {
                trnPDU = new BitSet(128);
                netMIC = new BitSet(32);
            }
        }
    }
}