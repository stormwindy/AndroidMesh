package com.example.egeelgun.bleservice.networkLayer;

import android.os.Bundle;

import com.example.egeelgun.bleservice.InterfaceActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang3.BitField;

public class NetworkPDU {

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

        public generic_PDU(boolean ctrl) {
            IVI = new BitSet(1);
            NID = new BitSet(7);
            CTL = new BitSet(1);
            TTL = new BitSet(7);
            SQL = new BitSet(24);
            SRC = new BitSet(16);
            DST = new BitSet(16);
            if(ctrl) {
                CTL.set(0);
                trnPDU = new BitSet(96);
            } else {
                trnPDU = new BitSet(128);
            }
        }
    }

}