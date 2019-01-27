package com.squad.betakua.tap_neko;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

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

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.INTERNET}, REQUEST_CODE);
        } else {
            Uri uri = Uri.parse("https://www.youtube.com/watch?v=a1sn_UlUOio");
            videoView.setVideoURI(uri);
            videoView.start();
            videoView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    DisplayMetrics metrics = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) videoView.getLayoutParams();
                    params.width = metrics.widthPixels;
                    params.height = metrics.heightPixels;
                    params.leftMargin = 0;
                    videoView.setLayoutParams(params);
                }

            });
        }
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
