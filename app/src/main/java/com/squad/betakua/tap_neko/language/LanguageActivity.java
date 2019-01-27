package com.squad.betakua.tap_neko.language;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.squad.betakua.tap_neko.R;

import java.util.HashMap;


public class LanguageActivity extends AppCompatActivity {

    public HashMap<String, String> languages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languages);

        languages = createMap();

        Spinner spinner = findViewById(R.id.languages_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    private static HashMap<String, String> createMap() {
        HashMap<String,String> langs = new HashMap<String, String>();
        langs.put("English", "en");
        langs.put("Français", "fr");
        langs.put("國語", "zh-Hans");
        langs.put("廣東話", "yue");
        langs.put("Español", "es");
        langs.put("ᜏᜒᜃᜅ᜔ ᜆᜄᜎᜓᜄ᜔", "fil");
        langs.put("العربية", "ar");
        langs.put("Deutsch", "de");

        return langs;
    }


}
