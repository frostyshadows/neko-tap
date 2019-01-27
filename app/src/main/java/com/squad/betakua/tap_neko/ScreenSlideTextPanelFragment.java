package com.squad.betakua.tap_neko;

import android.app.PendingIntent;
import android.media.AudioManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;

import android.speech.tts.TextToSpeech;

import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.util.HashMap;
import java.util.Locale;

public class ScreenSlideTextPanelFragment extends Fragment {

import android.widget.TextView;

import com.squad.betakua.tap_neko.patientListeners.TranscriptListener;
    TextView transcriptView;
    TextView text;
    TextToSpeech mTts;
    Button largerFont;
    Button smallerFont;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_text_panel, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        transcriptView = getView().findViewById(R.id.transcriptView);
        largerFont = getView().findViewById(R.id.largerFont);
        smallerFont = getView().findViewById(R.id.smallerFont);

        largerFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float size = largerFont.getTextSize() - 1;
                largerFont.setTextSize(size);
            }
        });

        smallerFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float size = smallerFont.getTextSize() - 1;
                smallerFont.setTextSize(size);
            }
        });

        transcriptView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTts = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            int result = mTts.setLanguage(Locale.KOREA);
                            if (result == TextToSpeech.LANG_MISSING_DATA
                                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                Toast.makeText(getActivity().getApplicationContext(), "This language is not supported", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                // TODO - insert text here
                                speak("Tap your phone against the bottle cap or medical device tag");
                            }
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Initialization failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        FloatingActionButton mainFab = getView().findViewById(R.id.mainFab);
        FloatingActionButton callFab = getView().findViewById(R.id.callFab);
        FloatingActionButton alertFab = getView().findViewById(R.id.alertFab);
        transcriptView = getView().findViewById(R.id.transcriptView);


        callFab.setVisibility(View.INVISIBLE);
        alertFab.setVisibility(View.INVISIBLE);

        mainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callFab.getVisibility()==View.VISIBLE){
                    callFab.setVisibility(View.INVISIBLE);
                    alertFab.setVisibility(View.INVISIBLE);
                } else{
                    callFab.setVisibility(View.VISIBLE);
                    alertFab.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    void speak(String s){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle bundle = new Bundle();
            bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
            mTts.speak(s, TextToSpeech.QUEUE_FLUSH, bundle, null);
        } else {
            HashMap<String, String> param = new HashMap<>();
            param.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
            mTts.speak(s, TextToSpeech.QUEUE_FLUSH, param);
        }
    }

    @Override
    public void onTranscriptLoaded(String transcript) {
        if (this.isVisible()) {
            transcriptView.setText(transcript);
        }
    }
}
