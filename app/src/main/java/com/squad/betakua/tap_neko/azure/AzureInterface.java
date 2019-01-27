package com.squad.betakua.tap_neko.azure;

import android.content.Context;
import android.icu.text.IDNA;
import android.os.Handler;

import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionEventArgs;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.internal.SpeechTranslationConfig;
import com.microsoft.cognitiveservices.speech.internal.TranslationRecognizer;
import com.microsoft.cognitiveservices.speech.internal.TranslationSynthesisEventListener;
import com.microsoft.cognitiveservices.speech.internal.TranslationTexEventListener;
import com.microsoft.cognitiveservices.speech.internal.VoidFuture;
import com.microsoft.cognitiveservices.speech.util.EventHandler;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squad.betakua.tap_neko.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AzureInterface {
    private static final String CONNECTION_STRING_TEMPLATE = "DefaultEndpointsProtocol=https;" +
            "AccountName=%s;" +
            "AccountKey=%s";
    private static final String STORAGE_ACCOUNT_NAME = BuildConfig.AzureStorageAccountName;
    private static final String STORAGE_ACCOUNT_KEY = BuildConfig.AzureStorageAccountKey;
    private static final String SPEECH_SUB_KEY = BuildConfig.AzureSpeechSubscriptionKey;
    private static final String SERVICE_REGION = "westus";
    private static AzureInterface AZURE_INTERFACE = null;
    private final CloudStorageAccount storageAccount;
    private final MobileServiceTable<InfoItem> infoTable;
    private final SpeechConfig speechConfig;

    /**
     * Initialize singleton instance of Azure interface
     * Note: Ensure you have Azure storage account name and key in gradle.properties
     *
     * @param context Context to pass to Azure Mobile App SDK
     * @throws AzureInterfaceException If something goes wrong
     */
    public static void init(Context context) throws AzureInterfaceException {
        if (AZURE_INTERFACE == null) {
            AZURE_INTERFACE = new AzureInterface(context);
        }
        throw new AzureInterfaceException("AzureInterface already initialized");
    }

    /**
     * Get singleton instance of Azure interface
     *
     * @return Singleton instance of Azure interface
     * @throws AzureInterfaceException If something goes wrong
     */
    public static AzureInterface getInstance() throws AzureInterfaceException {
        if (AZURE_INTERFACE == null) {
            throw new AzureInterfaceException("AzureInterface not initialized yet");
        }
        return AZURE_INTERFACE;
    }

    private AzureInterface(Context context) throws AzureInterfaceException {
        try {
            final String connectionString = String.format(CONNECTION_STRING_TEMPLATE,
                    STORAGE_ACCOUNT_NAME,
                    STORAGE_ACCOUNT_KEY);
            this.storageAccount = CloudStorageAccount.parse(connectionString);
            final MobileServiceClient mobileServiceClient =
                    new MobileServiceClient("https://neko-tap.azurewebsites.net", context);
            this.infoTable = mobileServiceClient.getTable(InfoItem.class);
            this.speechConfig = SpeechConfig.fromSubscription(SPEECH_SUB_KEY, SERVICE_REGION);
        } catch (URISyntaxException e) {
            throw new AzureInterfaceException(e.getMessage());
        } catch (InvalidKeyException e) {
            throw new AzureInterfaceException(e.getMessage());
        } catch (MalformedURLException e) {
            throw new AzureInterfaceException(e.getMessage());
        }
    }

    /**
     * Write a new info item to the Azure InfoTable
     *
     * @param nfcID      NFC ID
     * @param productID  Product ID
     * @param transcript Transcript of instruction audio
     */
    public void writeInfoItem(String nfcID,
                              String productID,
                              String transcript,
                              String url) {
        final InfoItem item = new InfoItem();
        item.setNfcID(nfcID);
        item.setProductID(productID);
        item.setTranscript(transcript);
        item.setURL(url);
        this.infoTable.insert(item);
    }

    /**
     * Look up an info item in Azure InfoTable by NFC ID
     *
     * @param nfcID NFC ID to look up
     * @return Future for a list of matching InfoItems
     */
    public ListenableFuture<MobileServiceList<InfoItem>> readInfoItem(String nfcID) {
        return this.infoTable.where().field("nfcID").eq(nfcID).execute();
    }

    /**
     * Upload an audio file to Azure
     * Warning: will overwrite file if file with the same audioTitle already exists
     *
     * @param audioTitle Title of audio clip to store; note: should be same as NFC ID
     * @param in         InputStream to read from
     * @param length     Length in bytes of file (or -1 if unknown)
     * @throws AzureInterfaceException If something goes wrong
     */
    public void uploadAudio(final String audioTitle, final InputStream in, final long length) {
        new Thread(() -> {
            try {
                final CloudBlobClient blobClient = this.storageAccount.createCloudBlobClient();
                final CloudBlobContainer container =
                        blobClient.getContainerReference("instructionaudio");
                final CloudBlockBlob blockBlob = container.getBlockBlobReference(audioTitle);
                blockBlob.upload(in, length);
            } catch (URISyntaxException | StorageException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Download audio file from Azure
     *
     * @param audioTitle Title of audio clip in Azure Storage; note: should be same as NFC ID
     * @param out        OutputStream to write to
     * @throws AzureInterfaceException If something goes wrong
     */
    public void downloadAudio(final String audioTitle, final OutputStream out)
            throws AzureInterfaceException {
        try {
            final CloudBlobClient blobClient = this.storageAccount.createCloudBlobClient();
            final CloudBlobContainer container =
                    blobClient.getContainerReference("instructionaudio");
            final CloudBlockBlob blockBlob = container.getBlockBlobReference(audioTitle);
            blockBlob.download(out);
        } catch (URISyntaxException e) {
            throw new AzureInterfaceException(e.getMessage());
        } catch (StorageException e) {
            throw new AzureInterfaceException(e.getMessage());
        }
    }

    /**
     * Transcribe audio from the given file
     *
     * @param filename Filename of .wav file to transcribe
     * @param handler  Handler that receives transcribed text
     * @return Future to end task - call `.get()` to end transcription
     */
    public Future<Void> transcribeAudio(final String filename,
                                        final EventHandler<SpeechRecognitionEventArgs> handler) {
        AudioConfig audioInput = AudioConfig.fromWavFileInput(filename);
        SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioInput);
        recognizer.recognized.addEventListener(handler);
        recognizer.startContinuousRecognitionAsync();
        return recognizer.stopContinuousRecognitionAsync();
    }

    /**
     * Translate audio from given file
     *
     * @param filename        Filename of .wav file to translate
     * @param textHandler     Handler for translated text
     * @param audioHandler    Handler for translated audio
     * @param outputLanguages List of languages to output
     * @return Future to end task - call `.get()` to end translation
     */
    public VoidFuture translateAudio(final String filename,
                                     final TranslationTexEventListener textHandler,
                                     final TranslationSynthesisEventListener audioHandler,
                                     final List<String> outputLanguages) {
        SpeechTranslationConfig config =
                SpeechTranslationConfig.FromSubscription(SPEECH_SUB_KEY, SERVICE_REGION);
        for (final String lang : outputLanguages) {
            config.AddTargetLanguage(lang);
        }
        TranslationRecognizer recognizer = TranslationRecognizer.FromConfig(config);
        recognizer.getRecognized().AddEventListener(textHandler);
        recognizer.getSynthesizing().AddEventListener(audioHandler);
        recognizer.StartContinuousRecognitionAsync();
        return recognizer.StopContinuousRecognitionAsync();
    }
}
