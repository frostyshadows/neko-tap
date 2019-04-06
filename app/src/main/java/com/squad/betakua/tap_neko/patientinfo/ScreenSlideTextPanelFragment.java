package com.squad.betakua.tap_neko.patientinfo;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squad.betakua.tap_neko.R;


public class ScreenSlideTextPanelFragment extends Fragment {
    private TextView transcriptTitle;
    private TextView transcriptView;
    private TextToSpeech mTts;
    private ImageButton largerFont;
    private ImageButton smallerFont;
    private ImageButton translateButton;
    private Integer fontSize = 0;
    private boolean isTranslated;

    // Navigation Bar
    private ImageButton navButtonLeft;
    private ImageButton navButtonRight;
    private OnButtonClickListener navButtonListener;

    private boolean hasInfo = false;
    private String nfcId;
    private String fileId;
    private String productName;
    private String transcript;
    private String translated;

    private String MOCK_PRODUCT_NAME = "Doxycycline 100mg Tablets";
    private String MOCK_TEXT = "[Placeholder Text] This medication is used to treat and prevent osteoporosis, a condition where the bones become thin, weak. It works by preventing bone breakdown and increasing bone density (thickness).\n" +
            "\n" +
            "Alendronate may not work properly and may damage the esophagus or cause sores in the mouth if it is not taken according to the following instructions. Tell your doctor if you do not understand these instructions.\n" +
            "\n" +
            "You must take alendronate just after you get out of bed in the morning, before you eat or drink anything. Never take alendronate at bedtime. \n" +
            "\n" +
            "Swallow alendronate tablets with a full glass of plain water. Never take alendronate tablets or solution with any liquid other than plain water.\n" +
            "\n" +
            "After you take alendronate, do not eat, drink, or take any other medications for at least 30 minutes. Do not lie down for at least 30 minutes after you take alendronate. Sit upright or stand upright until at least 30 minutes have passed and you have eaten your meal of the day.";
    private String MOCK_TRANSLATED = "这种药物用于治疗和预防骨质疏松症，骨质疏松，骨骼变薄，变弱。它通过防止骨质破坏和增加骨密度（厚度）起作用。\\n\" +\n" +
            "\"\\n\" +\n" +
            "\"如果不按照以下说明服用，阿仑膦酸盐可能无法正常工作，可能会损坏食道或导致口腔溃疡。如果您不理解这些说明，请告诉您的医生。\\n\" +\n" +
            "\"\\n\" +\n" +
            "\"早上起床后，你必须服用阿仑膦酸钠，然后才能吃或喝任何东西。不要在睡前服用阿仑膦酸钠。\\n";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_text_panel, container, false);

        // Navigation buttons
        navButtonLeft = rootView.findViewById(R.id.patient_view_audio_button);
        navButtonRight = rootView.findViewById(R.id.patient_view_video);
        navButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navButtonListener.onButtonClicked(v);
            }
        });
        navButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navButtonListener.onButtonClicked(v);
            }
        });

        // Translations button
        isTranslated = false;
        translateButton = rootView.findViewById(R.id.translate);
        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTranslated) {
                    transcriptView.setText(transcript);
                    isTranslated = false;
                } else {
                    transcriptView.setText(translated);
                    isTranslated = true;
                }
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        transcriptTitle = view.findViewById(R.id.patient_transcript_title);
        transcriptView = view.findViewById(R.id.transcriptView);
        largerFont = view.findViewById(R.id.zoom_in);
        smallerFont = view.findViewById(R.id.zoom_out);
        translateButton = view.findViewById(R.id.translate);

        nfcId = getArguments().getString("nfcId", "");
        translated = getArguments().getString("translated", MOCK_TRANSLATED);
        productName = getArguments().getString("productName", "Aerochamber (Child)") + "\nProduct ID: " + getArguments().getString("productID", "80092323");
        transcriptTitle.setText(productName);

        transcript = getArguments().getString("transcript", MOCK_TEXT);
        Log.e("transcript2 is ", transcript);
        String formattedTranscript = transcript.replaceAll("\\.", ".\n\n");
        transcriptView.setText(formattedTranscript);

        transcriptView.setMovementMethod(new ScrollingMovementMethod()); // make it scroll

        largerFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float newSize = adjustTextSize(true);
                Log.e("Text size", " " + transcriptView.getTextSize() + " " + newSize);
                transcriptView.setTextSize(TypedValue.COMPLEX_UNIT_SP, newSize);
            }
        });

        smallerFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float newSize = adjustTextSize(false);
                transcriptView.setTextSize(TypedValue.COMPLEX_UNIT_SP, newSize);
            }
        });

        // translateButton.setOnClickListener(new View.OnClickListener() {
        //     @Override›
        //     public void onClick(View view) {
        //         List<String> languages = new ArrayList<String>();
        //         languages.add("fr");
        //         languages.add("zh-Hans");
        //         languages.add("ko");
        //
        //         try {
        //             TranslationRecognizer recognizer = AzureInterface.getInstance().getTranslationRecognizer(languages);
        //             recognizer.
        //         } catch (AzureInterfaceException e) {
        //             Log.e("ERROR", e.toString());
        //         }
        //     }
        // });

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

    private float adjustTextSize(boolean isIncreasing) {
        float result;

        if (isIncreasing && fontSize < 4) {
            fontSize++;
        } else if (!isIncreasing && fontSize > 0) {
            fontSize--;
        }

        switch (fontSize) {
            case 0:
                result = 20;
                break;
            case 1:
                result = 24;
                break;
            case 2:
                result = 28;
                break;
            case 3:
                result = 32;
                break;
            case 4:
                result = 36;
                break;
            default:
                result = 20;
                break;
        }

        return result;
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
    // Enabling buttons to switch between fragments
    // https://stackoverflow.com/questions/23631975/viewpager-how-to-navigate-from-one-page-to-another-using-a-button

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        navButtonListener = (OnButtonClickListener) context;
    }
}


