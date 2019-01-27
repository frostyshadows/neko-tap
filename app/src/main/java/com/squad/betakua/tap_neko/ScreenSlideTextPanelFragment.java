package com.squad.betakua.tap_neko;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squad.betakua.tap_neko.patientListeners.TranscriptListener;

public class ScreenSlideTextPanelFragment extends Fragment implements TranscriptListener {

    TextView transcriptView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_text_panel, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

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

    @Override
    public void onTranscriptLoaded(String transcript) {
        if (this.isVisible()) {
            transcriptView.setText(transcript);
        }
    }
}
