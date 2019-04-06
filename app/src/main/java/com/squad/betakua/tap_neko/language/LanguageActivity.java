package com.squad.betakua.tap_neko.language;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.squad.betakua.tap_neko.R;

import java.util.HashMap;


public class LanguageActivity extends AppCompatActivity {

    public HashMap<String, String> languages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languages);
    }

    private static HashMap<String, String> createMap() {
        HashMap<String, String> langs = new HashMap<String, String>();
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
