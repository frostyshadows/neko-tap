package com.squad.betakua.tap_neko.pharmacistsettings;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.squad.betakua.tap_neko.R;
import com.squad.betakua.tap_neko.mockdatabase.MockDatabase;

public class PharmacistSettingsActivity extends AppCompatActivity {
    Button initDatabaseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacist_settings);

        initDatabaseBtn = findViewById(R.id.pharmacist_settings_database_button);
        initDatabaseBtn.setOnClickListener((View view) -> {
            initDatabaseBtn.setEnabled(false);
            onInitDatabaseClick();
            initDatabaseBtn.setEnabled(true);
        });
    }

    private void onInitDatabaseClick() {
        Context ctx = getApplicationContext();
        MockDatabase db = new MockDatabase();
        db.initMockDatabase(ctx);
    }
}
