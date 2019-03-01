package com.squad.betakua.tap_neko.patientinfo;

import android.os.Bundle;

import android.speech.tts.TextToSpeech;

import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.squad.betakua.tap_neko.R;
import com.squad.betakua.tap_neko.azure.AzureInterface;
import com.squad.betakua.tap_neko.azure.AzureInterfaceException;
import com.squad.betakua.tap_neko.azure.DrugInfoItem;


public class ScreenSlideTextPanelFragment extends Fragment {
    private TextView transcriptView;
    private TextToSpeech mTts;
    private Button largerFont;
    private Button smallerFont;

    private boolean hasInfo = false;
    private String nfcId;
    private String fileId;
    private String transcript;

    private String MOCK_TEXT = "[Placeholder Text] This medication is used to treat and prevent osteoporosis, a condition where the bones become thin, weak. It works by preventing bone breakdown and increasing bone density (thickness).\n" +
            "\n" +
            "Alendronate may not work properly and may damage the esophagus or cause sores in the mouth if it is not taken according to the following instructions. Tell your doctor if you do not understand these instructions.\n" +
            "\n" +
            "You must take alendronate just after you get out of bed in the morning, before you eat or drink anything. Never take alendronate at bedtime. \n" +
            "\n" +
            "Swallow alendronate tablets with a full glass of plain water. Never take alendronate tablets or solution with any liquid other than plain water.\n" +
            "\n" +
            "After you take alendronate, do not eat, drink, or take any other medications for at least 30 minutes. Do not lie down for at least 30 minutes after you take alendronate. Sit upright or stand upright until at least 30 minutes have passed and you have eaten your meal of the day.";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_text_panel, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        transcriptView = view.findViewById(R.id.transcriptView);
        largerFont = view.findViewById(R.id.largerFont);
        smallerFont = view.findViewById(R.id.smallerFont);

        nfcId = getArguments().getString("nfcId", "");
        transcript = getArguments().getString("transcript", MOCK_TEXT);
        transcriptView.setText(transcript);

        try {
            ListenableFuture<MobileServiceList<DrugInfoItem>> druginfoItemsFuture = AzureInterface.getInstance().readDrugInfoItem(nfcId);

            Futures.addCallback(druginfoItemsFuture, new FutureCallback<MobileServiceList<DrugInfoItem>>() {
                public void onSuccess(MobileServiceList<DrugInfoItem> infoItems) {
                    hasInfo = true;
                    // transcriptListener.onTranscriptLoaded("dummy transcript");
                    // transcriptListener.onTranscriptLoaded(infoItems.get(0).getText());
                }

                public void onFailure(Throwable t) {
                    t.printStackTrace();
                }
            });

        } catch (AzureInterfaceException e) {
            e.printStackTrace();
        }

        largerFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float size = transcriptView.getTextSize() - 1;
                transcriptView.setTextSize(size);
            }
        });

        smallerFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float size = transcriptView.getTextSize() - 1;
                transcriptView.setTextSize(size);
            }
        });

        // transcriptView.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View view) {
        //         mTts = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
        //             @Override
        //             public void onInit(int status) {
        //                 if (status == TextToSpeech.SUCCESS) {
        //                     int result = mTts.setLanguage(Locale.KOREA);
        //                     if (result == TextToSpeech.LANG_MISSING_DATA
        //                             || result == TextToSpeech.LANG_NOT_SUPPORTED) {
        //                         Toast.makeText(getActivity().getApplicationContext(), "This language is not supported", Toast.LENGTH_SHORT).show();
        //                     } else {
        //                         // TODO - insert text here
        //                         speak("This medication is used to treat and prevent osteoporosis, a condition where the bones become thin, weak. It works by preventing bone breakdown and increasing bone density (thickness).\n" +
        //                                 "\n" +
        //                                 "Alendronate may not work properly and may damage the esophagus or cause sores in the mouth if it is not taken according to the following instructions. Tell your doctor if you do not understand these instructions.\n" +
        //                                 "\n" +
        //                                 "You must take alendronate just after you get out of bed in the morning, before you eat or drink anything. Never take alendronate at bedtime. \n" +
        //                                 "\n" +
        //                                 "Swallow alendronate tablets with a full glass of plain water. Never take alendronate tablets or solution with any liquid other than plain water.\n" +
        //                                 "\n" +
        //                                 "After you take alendronate, do not eat, drink, or take any other medications for at least 30 minutes. Do not lie down for at least 30 minutes after you take alendronate. Sit upright or stand upright until at least 30 minutes have passed and you have eaten your meal of the day.");
        //                     }
        //                 } else {
        //                     Toast.makeText(getActivity().getApplicationContext(), "Initialization failed", Toast.LENGTH_SHORT).show();
        //                 }
        //             }
        //         });
        //     }
        // });

        FloatingActionButton mainFab = view.findViewById(R.id.mainFab);
        FloatingActionButton callFab = view.findViewById(R.id.callFab);
        FloatingActionButton alertFab = view.findViewById(R.id.alertFab);
        // Button translate = view.findViewById(R.id.translate);

        callFab.setVisibility(View.INVISIBLE);
        alertFab.setVisibility(View.INVISIBLE);

        mainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callFab.getVisibility() == View.VISIBLE) {
                    callFab.setVisibility(View.INVISIBLE);
                    alertFab.setVisibility(View.INVISIBLE);
                } else {
                    callFab.setVisibility(View.VISIBLE);
                    alertFab.setVisibility(View.VISIBLE);
                }
            }
        });

        // translate.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View view) {
        //         transcriptView.setText("这种药物用于治疗和预防骨质疏松症，骨质疏松，骨骼变薄，变弱。它通过防止骨质破坏和增加骨密度（厚度）起作用。\n" +
        //                 "\n" +
        //                 "如果不按照以下说明服用，阿仑膦酸盐可能无法正常工作，可能会损坏食道或导致口腔溃疡。如果您不理解这些说明，请告诉您的医生。\n" +
        //                 "\n" +
        //                 "早上起床后，你必须服用阿仑膦酸钠，然后才能吃或喝任何东西。不要在睡前服用阿仑膦酸钠。\n");
        //     }
        // });
    }

    // private void speak(String s){
    //     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    //         Bundle bundle = new Bundle();
    //         bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
    //         mTts.speak(s, TextToSpeech.QUEUE_FLUSH, bundle, null);
    //     } else {
    //         HashMap<String, String> param = new HashMap<>();
    //         param.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
    //         mTts.speak(s, TextToSpeech.QUEUE_FLUSH, param);
    //     }
    // }

    // @Override
    // public void onTranscriptLoaded (String transcript){
    //     if (this.isVisible()) {
    //         transcriptView.setText("This medication is used to treat and prevent osteoporosis, a condition where the bones become thin, weak. It works by preventing bone breakdown and increasing bone density (thickness).\n" +
    //                 "\n" +
    //                 "Alendronate may not work properly and may damage the esophagus or cause sores in the mouth if it is not taken according to the following instructions. Tell your doctor if you do not understand these instructions.\n" +
    //                 "\n" +
    //                 "You must take alendronate just after you get out of bed in the morning, before you eat or drink anything. Never take alendronate at bedtime. \n" +
    //                 "\n" +
    //                 "Swallow alendronate tablets with a full glass of plain water. Never take alendronate tablets or solution with any liquid other than plain water.\n" +
    //                 "\n" +
    //                 "After you take alendronate, do not eat, drink, or take any other medications for at least 30 minutes. Do not lie down for at least 30 minutes after you take alendronate. Sit upright or stand upright until at least 30 minutes have passed and you have eaten your meal of the day.");
    //     }
    // }

}


