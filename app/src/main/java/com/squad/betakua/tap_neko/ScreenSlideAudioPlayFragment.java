package com.squad.betakua.tap_neko;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;

public class ScreenSlideAudioPlayFragment extends Fragment {

    private  LottieAnimationView playToPauseAudioIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_audio_panel, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        playToPauseAudioIcon = (LottieAnimationView) getView().findViewById(R.id.play_to_pause_animation);

        playToPauseAudioIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playToPauseAudioIcon.playAnimation();
                //Play audio file
            }
        });
    }


}
