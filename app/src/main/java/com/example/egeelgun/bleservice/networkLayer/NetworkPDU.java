package com.example.egeelgun.bleservice.networkLayer;

import android.os.Bundle;

import com.example.egeelgun.bleservice.InterfaceActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class NetworkPDU {

    private ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristics;
    public NetworkPDU() {
        InterfaceActivity gattActivity = new InterfaceActivity();
        gattCharacteristics = gattActivity.getCharacterInfo();
    }

    class PDUMessage {
        String IVIndex;
        String NID;
        String CTL;
        String TTL;
        String SEQ;
        String SRC;
        String DST;
        String transportPDU;
        String NetIMC;

        public PDUMessage() {
        }

        public BitSet getConcatPDU() {
            String strMessage = IVIndex + NID + CTL + TTL + SEQ + SRC + DST + transportPDU + NetIMC;
            BitSet messagePDU = new BitSet(strMessage.length());
            
            for (int i = 0; i < strMessage.length(); i++) {
                if (strMessage.charAt(i) == '1') {
                    messagePDU.set(i);
                }
            }
            return messagePDU;
        }
    }
}