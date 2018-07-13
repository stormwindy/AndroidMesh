package com.example.egeelgun.bleservice.networkLayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class NetworkPDU {

    class PDUMessage {
        BitSet IVIndex;
        BitSet NID;
        BitSet CTL;
        BitSet TTL;
        BitSet SEQ;
        BitSet SRC;
        BitSet DST;
        BitSet transportPDU;
        BitSet NetIMC;
        public PDUMessage() {}
    }


    HashSet<String> NID = new HashSet<>();
}