package com.squad.betakua.tap_neko;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

public class ScreenSlideVideoPanelFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_video_panel, container, false);


        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        FloatingActionButton mainFab = getView().findViewById(R.id.mainFab);
        FloatingActionButton callFab = getView().findViewById(R.id.callFab);
        FloatingActionButton alertFab = getView().findViewById(R.id.alertFab);

        //VideoPlayer
        VideoView videoView = getView().findViewById(R.id.videoView);
        // videoView.setVideoURI();

        videoView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DisplayMetrics metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) videoView.getLayoutParams();
                params.width =  metrics.widthPixels;
                params.height = metrics.heightPixels;
                params.leftMargin = 0;
                videoView.setLayoutParams(params);
            }

        });

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



}
