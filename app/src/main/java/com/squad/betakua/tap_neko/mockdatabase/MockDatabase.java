package com.squad.betakua.tap_neko.mockdatabase;

import android.content.Context;
import android.util.Log;

import com.squad.betakua.tap_neko.azure.AzureInterface;
import com.squad.betakua.tap_neko.azure.DeviceRecord;
import com.squad.betakua.tap_neko.azure.DrugRecord;
import com.squad.betakua.tap_neko.azure.PhraseItem;
import com.squad.betakua.tap_neko.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class MockDatabase {
    // Since drug info licencing agreements are too $$$, this class
    // is used to initialize a mock database and upload records to azure
    ArrayList<String> drugFiles;
    ArrayList<String> deviceFiles;
    private final String root = "mockdatabase/";

    public MockDatabase() {
        drugFiles = new ArrayList<>();
        deviceFiles = new ArrayList<>();

        drugFiles.add("drug_alendronate");
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
        DrugRecord drugRecord;

        for (String fileName: drugFiles) {
            JSONObject json = Utils.loadJSONFromAsset(context, root + fileName);
            try {
                // Upload DrugRecord
                drugRecord = new DrugRecord();
                drugRecord.setProductID(json.getString("productID"));
                drugRecord.setDIN(json.getString("DIN"));
                drugRecord.setRxNumber(json.getString("rxNumber"));
                drugRecord.setGenericName(json.getString("genericName"));
                drugRecord.setTradeName(json.getString("tradeName"));
                drugRecord.setLabel(json.getString("label"));
                drugRecord.setDose(json.getString("dose"));
                drugRecord.setDosageForm(json.getString("dosageForm"));
                drugRecord.setDoseUnit(json.getString("doseUnit"));
                drugRecord.setUrl(json.getString("url"));
                drugRecord.setWebUrl(json.getString("webUrl"));
                AzureInterface.getInstance().writeDrugRecord(drugRecord);

                // Process and Upload Phrases
                uploadPhrases(json.getString("DIN"), json.getString("monograph_description"), "description");
                uploadPhrases(json.getString("DIN"), json.getString("monograph_instructions"), "instructions");
                uploadPhrases(json.getString("DIN"), json.getString("monograph_side_effects"), "side effects");
                uploadPhrases(json.getString("DIN"), json.getString("monograph_warnings"), "warnings");
                uploadPhrases(json.getString("DIN"), json.getString("monograph_interactions"), "interactions");

            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadPhrases(String DIN, String text, String category) {
        // clean & split the text
        PhraseItem phraseItem;
        String[] splitText = text.split("\\.");
        Log.d("MEDICATION -- ", " DIN: " + DIN);
        int n = 0;
        for (String phrase : splitText) {
            try {
                // upload PhraseItem
                phraseItem = new PhraseItem();
                phraseItem.setDIN(DIN);
                phraseItem.setPhrase(phrase + ".");
                phraseItem.setPhraseID(UUID.randomUUID().toString());
                phraseItem.setCategory(category);
                AzureInterface.getInstance().writePhraseItem(phraseItem);
                Thread.sleep(200);
                Log.d("----", "THROTTLING " + n);
                n++;
                // Log.e("--- " + DIN + " --- " + category + " --- ", phrase);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadAzureDevices(Context context) {
        DeviceRecord deviceRecord;
        for (String fileName: deviceFiles) {
            JSONObject json = Utils.loadJSONFromAsset(context, root + fileName);
            try {
                // Upload DeviceRecord
                deviceRecord = new DeviceRecord();
                deviceRecord.setProductID(json.getString("productID"));
                deviceRecord.setRxNumber(json.getString("rxNumber"));
                deviceRecord.setClassName(json.getString("className"));
                deviceRecord.setTradeName(json.getString("tradeName"));
                deviceRecord.setLabel(json.getString("label"));
                deviceRecord.setUrl(json.getString("url"));
                deviceRecord.setWebUrl(json.getString("webUrl"));
                AzureInterface.getInstance().writeDeviceRecord(deviceRecord);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
