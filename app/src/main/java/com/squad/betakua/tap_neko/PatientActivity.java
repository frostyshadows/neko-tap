package com.squad.betakua.tap_neko;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.VideoView;

import com.squad.betakua.tap_neko.azure.AzureInterface;
import com.squad.betakua.tap_neko.azure.AzureInterfaceException;
import com.squad.betakua.tap_neko.nfc.NFCActivity;

import java.io.OutputStream;

import static com.squad.betakua.tap_neko.nfc.NFCActivity.NFC_ID_KEY;
import static com.squad.betakua.tap_neko.nfc.NFCActivity.NFC_REQ_CODE;

/**
 * Created by sherryuan on 2019-01-26.
 */

public class PatientActivity extends AppCompatActivity {

    private Button stopButton;
    private Button playButton;

    //Pages in pagerView
    private static final int NUM_PAGES = 3;
    //Allows swiping between fragments
    private ViewPager mPager;
    //Provides the pages (fragments) to the ViewPager
    private PagerAdapter mPagerAdapter;

    VideoView vidView;
    MediaController vidControl;

    private boolean hasAudio = false;
    private OutputStream audioStream;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        initAudioPlayer();
        initPlayButton();
        initStopButton();
        initVideoPlayer();

        mPager = findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NFC_REQ_CODE && resultCode == RESULT_OK) {
            try {
                String nfcId = data.getStringExtra(NFC_ID_KEY);
                AzureInterface.getInstance().readInfoItem(nfcId);
                AzureInterface.getInstance().downloadAudio(nfcId, audioStream);
            } catch (AzureInterfaceException e) {
                e.printStackTrace();
            }
        }
    }

    private void initAudioPlayer() {
        try {
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // make something
            e.printStackTrace();
        }
    }

    private void initPlayButton() {
        playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener((View view) -> {
            mediaPlayer.start();
            Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
        });
    }

    private void initStopButton() {
        stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener((View view) -> {
            mediaPlayer.stop();
        });
    }

    private void initVideoPlayer() {
        vidView = findViewById(R.id.video);

        String vidAddress = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
        Uri vidUri = Uri.parse(vidAddress);
        vidView.setVideoURI(vidUri);
        // vidView.start();

        vidControl = new MediaController(this);
        vidControl.setAnchorView(vidView);
        vidView.setMediaController(vidControl);


    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: return new ScreenSlideAudioPlayFragment();
                case 1: return new ScreenSlideTextPanelFragment();
                case 2: return new ScreenSlideVideoPanelFragment();
            }

            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }
}
