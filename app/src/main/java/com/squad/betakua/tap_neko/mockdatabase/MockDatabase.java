package com.squad.betakua.tap_neko.mockdatabase;

import android.content.Context;
import android.util.Log;

import com.squad.betakua.tap_neko.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

public class MockDatabase {
    // Since drug info licencing agreements are too $$$, this class
    // is used to initialize a mock database and upload records to azure
    ArrayList<String> drugFiles;
    ArrayList<String> deviceFiles;
    private final String root = "mockdatabase/";

    public MockDatabase() {
        drugFiles = new ArrayList<>();
        deviceFiles = new ArrayList<>();

        drugFiles.add("drug_humira");
        drugFiles.add("drug_methotrexate");
        drugFiles.add("drug_ritalin_la");
        drugFiles.add("drug_spiriva");

        deviceFiles.add("device_accucheck");
        deviceFiles.add("device_aerochamber");
    }

    public void initMockDatabase(Context context) {
        // TODO: make asynchronous
        uploadAzureDrugs(context);
        uploadAzureDevices(context);
    }

    private void uploadAzureDrugs(Context context) {
        for (String fileName: drugFiles) {
            JSONObject json = Utils.loadJSONFromAsset(context, root + fileName);
            try {
                String label = json.getString("label");
                // Log.e("-------", "label is " + label);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadAzureDevices(Context context) {
        for (String fileName: deviceFiles) {
            JSONObject json = Utils.loadJSONFromAsset(context, root + fileName);
            try {
                String label = json.getString("label");
                // Log.e("-------", "label is " + label);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }


}
