package com.squad.betakua.tap_neko.utils;

public class Utils {

    public static String nfcToFileName(String nfc) {
        // Converts a string of hexes into a unique file name
        return nfc.replaceAll("\\s+", "_");
    }

}
