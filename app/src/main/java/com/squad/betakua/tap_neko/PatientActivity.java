package com.squad.betakua.tap_neko;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.squad.betakua.tap_neko.azure.AzureInterface;
import com.squad.betakua.tap_neko.azure.AzureInterfaceException;
import com.squad.betakua.tap_neko.azure.InfoItem;
import com.squad.betakua.tap_neko.nfc.NFCPatientActivity;
import com.squad.betakua.tap_neko.patientinfo.OnButtonClickListener;
import com.squad.betakua.tap_neko.patientinfo.ScreenSlideAudioPlayFragment;
import com.squad.betakua.tap_neko.patientinfo.ScreenSlideTextPanelFragment;
import com.squad.betakua.tap_neko.patientinfo.ScreenSlideVideoPanelFragment;
import com.squad.betakua.tap_neko.utils.Utils;

import java.io.File;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;
import static com.squad.betakua.tap_neko.nfc.NFCActivity.NFC_ID_KEY;

/**
 * Created by sherryuan on 2019-01-26.
 */

public class PatientActivity extends AppCompatActivity implements OnButtonClickListener {

    //Pages in pagerView
    private static final int NUM_PAGES = 3;
    public static final int INDEX_AUDIO_PAGE = 0;
    public static final int INDEX_TEXT_PAGE = 1;
    public static final int INDEX_VIDEO_PAGE = 2;

    //Allows swiping between fragments
    private ViewPager mPager;
    //Provides the pages (fragments) to the ViewPager
    private PagerAdapter mPagerAdapter;

    private String nfcId;
    private String fileId;
    private String barcodeId;
    private String productName;
    private String transcript;
    private String url;
    private String webUrl;
    private String pharmacyPhone;
    private String pharmacyName;
    private String pharmacist;
    private String translated;
    private String reminder;
    private String audioHash;

    private File audioFile;

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
    }

    public void retrieveProductInfo() {
        try {
            ListenableFuture<MobileServiceList<InfoItem>> infoItemsFuture = AzureInterface.getInstance().readInfoItem(nfcId);
            Futures.addCallback(infoItemsFuture, new FutureCallback<MobileServiceList<InfoItem>>() {
                public void onSuccess(MobileServiceList<InfoItem> infoItems) {
                    barcodeId = infoItems.get(0).getProductID();
                    productName = infoItems.get(0).getProductName();
                    transcript = infoItems.get(0).getTranscript();
                    url = infoItems.get(0).getURL();
                    webUrl = infoItems.get(0).getWebURL();
                    pharmacyPhone = infoItems.get(0).getPharmacyPhone();
                    pharmacyName = infoItems.get(0).getPharmacyName();
                    pharmacist = infoItems.get(0).getPharmacist();
                    translated = infoItems.get(0).getTranslated();
                    reminder = infoItems.get(0).getReminder();

                    audioHash = infoItems.get(0).getTranslationsID(); // TODO: workaround: using translations ID for now

                    mPager = findViewById(R.id.pager);
                    mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
                    mPager.setAdapter(mPagerAdapter);
                }

                public void onFailure(Throwable t) {
                    Log.e("HERE11", "fail " + t.toString());
                    t.printStackTrace();
                }
            });
        } catch (AzureInterfaceException e) {
            Log.e("ERROR:", e.toString());
        }
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
                    audioFile = new File(getFilesDir(), fileId + "_" + audioHash + ".wav");
                    args.putString("audioFilePath", audioFile.getAbsolutePath());
                    args.putString("productName", productName);
                    args.putString("nfcId", nfcId);
                    audioFragment.setArguments(args);

                    return audioFragment;
                }
                case 1: {
                    ScreenSlideTextPanelFragment screenSlideTextPanelFragment = new ScreenSlideTextPanelFragment();
                    Bundle args = new Bundle();
                    args.putString("productName", productName);
                    args.putString("transcript", transcript);
                    args.putString("nfcId", nfcId);
                    args.putString("translated", translated);
                    screenSlideTextPanelFragment.setArguments(args);
                    return screenSlideTextPanelFragment;
                }
                case 2: {
                    ScreenSlideVideoPanelFragment videoFragment = new ScreenSlideVideoPanelFragment();
                    Bundle args = new Bundle();
                    args.putString("productID", barcodeId);
                    args.putString("productName", productName);
                    args.putString("url", url);
                    args.putString("webUrl", webUrl);
                    args.putString("pharmacyPhone", pharmacyPhone);
                    args.putString("pharmacyName", pharmacyName);
                    args.putString("pharmacist", pharmacist);
                    args.putString("reminder", reminder);
                    videoFragment.setArguments(args);
                    return videoFragment;
                }
            }

            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    @Override
    public void onButtonClicked(View view){
        int currPos = mPager.getCurrentItem();

        switch(view.getId()) {
            case R.id.patient_view_text_button:
                mPager.setCurrentItem(INDEX_TEXT_PAGE);
                break;
            case R.id.patient_view_audio_button:
                mPager.setCurrentItem(INDEX_AUDIO_PAGE);
                break;
            case R.id.patient_view_video:
                mPager.setCurrentItem(INDEX_VIDEO_PAGE);
                break;
            case R.id.patient_tap_again_icon:
                Intent patientIntent = new Intent(getApplicationContext(), NFCPatientActivity.class);
                startActivity(patientIntent);
                break;
        }
    }
}
