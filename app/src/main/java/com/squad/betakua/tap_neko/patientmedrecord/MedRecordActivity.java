package com.squad.betakua.tap_neko.patientmedrecord;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.squad.betakua.tap_neko.PatientActivity;
import com.squad.betakua.tap_neko.R;
import com.squad.betakua.tap_neko.azure.AzureInterface;
import com.squad.betakua.tap_neko.azure.DrugRecord;
import com.squad.betakua.tap_neko.nfc.NFCActivity;
import com.squad.betakua.tap_neko.nfc.NFCPatientActivity;
import com.squad.betakua.tap_neko.patientinfo.ScreenSlideAudioPlayFragment;

import static android.view.View.VISIBLE;

public class MedRecordActivity extends AppCompatActivity {
    Button addMedRecordBtn;
    LinearLayout emptyStatePanel;
    LinearLayout medListRoot;
    Button viewInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_med_record);
        addMedRecordBtn = findViewById(R.id.med_record_add_med_btn);
        emptyStatePanel = findViewById(R.id.empty_state_panel);
        medListRoot = findViewById(R.id.med_record_list);
        viewInfo = findViewById(R.id.viewButton);


        addMedRecordBtn.setOnClickListener((View view) -> {
            Intent drugIntent = new Intent(getApplicationContext(), NFCPatientActivity.class);
            startActivity(drugIntent);
        });

        emptyStatePanel.setVisibility(VISIBLE);

        checkForExistingRecords();
    }

    private void checkForExistingRecords() {
        try {
            ListenableFuture<MobileServiceList<PatientMedRecord>> patientMedRecordFuture = AzureInterface.getInstance().readPatientMedRecord();
            Futures.addCallback(patientMedRecordFuture, new FutureCallback<MobileServiceList<PatientMedRecord>>() {
                public void onSuccess(MobileServiceList<PatientMedRecord> items) {
                    Log.e("----", "it is " + items.getTotalCount());
                    if (items.getTotalCount() > 0) {
                        populateMedRecord(items);
                    } else {
                        // addEmptyRecordState();
                    }
                }

                public void onFailure(Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            Log.e("ERROR:", e.toString());
        }
    }

    private void removeEmptyRecordState() {
        ((ViewManager)emptyStatePanel.getParent()).removeView(emptyStatePanel);
    }

    private void populateMedRecord(MobileServiceList<PatientMedRecord> items) {
        for (PatientMedRecord record: items) {

            LinearLayout medicationRow = new LinearLayout(this);
            medicationRow.setOrientation(LinearLayout.VERTICAL);

            retrieveDrugRecord(medicationRow, record);
        }
    }

    private void retrieveDrugRecord(LinearLayout medicationRow, PatientMedRecord record) {
        try {
            ListenableFuture<MobileServiceList<DrugRecord>> drugRecordFuture = AzureInterface.getInstance().readDrugRecord(record.getProductID());
            Futures.addCallback(drugRecordFuture, new FutureCallback<MobileServiceList<DrugRecord>>() {
                public void onSuccess(MobileServiceList<DrugRecord> items) {
                    if (items.getTotalCount() <= 0 ) {
                        Log.e("ERROR: ", "The DrugRecord productID does not exist!");
                        return;
                    }
                    DrugRecord drug = items.get(0);

                    // assemble row 1
                    LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout row1 = new LinearLayout(getApplicationContext());
                    row1.setLayoutParams(lparams);
                    row1.setOrientation(LinearLayout.HORIZONTAL);
                    TextView rxNumber = new TextView(getApplicationContext());
                    rxNumber.setText(record.getRxNumber());
                    TextView label = new TextView(getApplicationContext());
                    label.setText(drug.getLabel());
                    row1.addView(rxNumber);
                    row1.addView(label);

                    // assemble row 2
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout row2 = new LinearLayout(getApplicationContext());
                    row2.setLayoutParams(layoutParams);
                    row1.setOrientation(LinearLayout.HORIZONTAL);
                    Button viewInfo = new Button(getApplicationContext());
                    viewInfo.setText("view drug");
                    row2.addView(viewInfo);

                    // assemble row 3

                    // finally, add the row to the med list
                    medicationRow.addView(row1);
                    medicationRow.addView(row2);
                    medListRoot.addView(medicationRow);

                    removeEmptyRecordState();
                }

                public void onFailure(Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            Log.e("ERROR:", e.toString());
        }
    }


}
