package com.squad.betakua.tap_neko;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.squad.betakua.tap_neko.audiorecord.AudioRecorderActivity;
import com.squad.betakua.tap_neko.azure.AzureInterface;
import com.squad.betakua.tap_neko.azure.AzureInterfaceException;
import com.squad.betakua.tap_neko.azure.InfoItem;
import com.squad.betakua.tap_neko.azure.OnDownloadAudioFileListener;
import com.squad.betakua.tap_neko.patientListeners.TranscriptListener;
import com.squad.betakua.tap_neko.patientinfo.ScreenSlideAudioPlayFragment;
import com.squad.betakua.tap_neko.patientinfo.ScreenSlideTextPanelFragment;
import com.squad.betakua.tap_neko.patientinfo.ScreenSlideVideoPanelFragment;
import com.squad.betakua.tap_neko.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;
import static com.squad.betakua.tap_neko.nfc.NFCActivity.NFC_ID_KEY;

/**
 * Created by sherryuan on 2019-01-26.
 */

public class PatientActivity extends AppCompatActivity {

    //Pages in pagerView
    private static final int NUM_PAGES = 3;
    //Allows swiping between fragments
    private ViewPager mPager;
    //Provides the pages (fragments) to the ViewPager
    private PagerAdapter mPagerAdapter;

    VideoView vidView;
    MediaController vidControl;

    private boolean hasInfo = false;
    private String nfcId;
    private String fileId;
    private String barcodeId;
    private String transcript;

    private boolean hasAudio = false;
    private File audioFile;
    private FileOutputStream audioStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        if (ContextCompat.checkSelfPermission(PatientActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PatientActivity.this, new String[]{Manifest.permission.INTERNET}, REQUEST_CODE);
            return;
        }

        nfcId = getIntent().getStringExtra(NFC_ID_KEY);
        fileId = Utils.nfcToFileName(nfcId);
        retrieveProductInfo();

        mPager = findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    public void retrieveProductInfo() {
        try {
            ListenableFuture<MobileServiceList<InfoItem>> infoItemsFuture = AzureInterface.getInstance().readInfoItem(fileId);

            Futures.addCallback(infoItemsFuture, new FutureCallback<MobileServiceList<InfoItem>>() {
                public void onSuccess(MobileServiceList<InfoItem> infoItems) {
                    hasInfo = true;
                    barcodeId = infoItems.get(0).getProductID();
                    transcript = infoItems.get(0).getTranscript();
                }

                public void onFailure(Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (AzureInterfaceException e) {
            Log.e("ERROR:", e.toString());
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == NFC_REQ_CODE && resultCode == RESULT_OK) {
//            try {
//                String nfcId = data.getStringExtra(NFC_ID_KEY);
//                ListenableFuture<MobileServiceList<InfoItem>> infoItemsFuture = AzureInterface.getInstance().readInfoItem(nfcId);
//
//                Futures.addCallback(infoItemsFuture, new FutureCallback<MobileServiceList<InfoItem>>() {
//                    public void onSuccess(MobileServiceList<InfoItem> infoItems) {
//                        hasInfo = true;
//                        barcodeId = infoItems.get(0).getProductID();
//                    }
//
//                    public void onFailure(Throwable t) {
//                        t.getStackTrace();
//                    }
//                });
//                audioStream = new FileOutputStream(outputFile, false);
//                AzureInterface.getInstance().downloadAudio(outputFilePath, audioStream);
//                audioStream.close();
//            } catch (AzureInterfaceException | IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
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
            switch (position) {
                case 0: {
                    Fragment audioFragment = new ScreenSlideAudioPlayFragment();
                    Bundle args = new Bundle();
                    audioFile = new File(getFilesDir(), fileId + ".wav");
                    args.putString("audioFilePath", audioFile.getAbsolutePath());
                    args.putString("productName", barcodeId);
                    args.putString("nfcId", nfcId);
                    audioFragment.setArguments(args);

                    return audioFragment;
                }
                case 1: {
                    ScreenSlideTextPanelFragment screenSlideTextPanelFragment = new ScreenSlideTextPanelFragment();
                    Bundle args = new Bundle();
                    args.putString("productName", barcodeId);
                    args.putString("transcript", transcript);
                    args.putString("nfcId", nfcId);
                    screenSlideTextPanelFragment.setArguments(args);
                    return screenSlideTextPanelFragment;
                }
                case 2: {
                    return new ScreenSlideVideoPanelFragment();
                }
            }

            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
