package com.squad.betakua.tap_neko.utils;

import android.content.Context;

import org.json.JSONObject;

import java.io.InputStream;

public class Utils {

    public static String nfcToFileName(String nfc) {
        // Converts a string of hexes into a unique file name
        return nfc.replaceAll("\\s+", "_");
    }

    public static JSONObject loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        JSONObject jsonObj = null;

        try {
            InputStream is = context.getAssets().open(fileName + ".json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");
            jsonObj = new JSONObject(json);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return jsonObj;

    }
}
