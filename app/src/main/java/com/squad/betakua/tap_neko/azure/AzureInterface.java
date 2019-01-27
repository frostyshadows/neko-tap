package com.squad.betakua.tap_neko.azure;

import android.content.Context;

import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.translation.SpeechTranslationConfig;
import com.microsoft.cognitiveservices.speech.translation.TranslationRecognizer;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squad.betakua.tap_neko.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.List;
import java.util.UUID;

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
            this.infoTable = mobileServiceClient.getTable("InfoTable", InfoItem.class);
            this.speechConfig = SpeechConfig.fromSubscription(SPEECH_SUB_KEY, SERVICE_REGION);
        } catch (URISyntaxException | InvalidKeyException | MalformedURLException e) {
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
        item.setId(UUID.randomUUID().toString());
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
     * @param audioTitle Title of audio clip to store; note: should be `$NFCID_$LANG`
     * @param in         InputStream to read from
     * @param length     Length in bytes of file (or -1 if unknown)
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
     * @param audioTitle Title of audio clip in Azure Storage; note: should be `$NFCID_$LANG`
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
        } catch (URISyntaxException | StorageException e) {
            throw new AzureInterfaceException(e.getMessage());
        }
    }

    /**
     * Create a new speech recognizer (for speech-to-text).
     * Subscribe to `recognizing` and `recognized` events to receive data while transcribing
     * and after transcription complete, respectively, then call `startContinuousRecognitionAsync()`
     * and `stopContinuousRecognitionAsync()` to start and stop transcription.
     * <p>
     * Note: see https://docs.microsoft.com/en-ca/azure/cognitive-services/speech-service/how-to-recognize-speech-java
     * for more details
     *
     * @return New speech recognizer
     */
    public SpeechRecognizer getSpeechRecognizer() {
        return new SpeechRecognizer(speechConfig);
    }

    /**
     * Create a new translation recognizer (for speech translation).
     * Subscribe to the `recognizing`, `recognized`, and `synthesizing` events to
     * receive text data while translating, text data after translation complete, and speech
     * data after translation complete, respectively, then call `startContinuousRecognitionAsync()`
     * and `stopContinuousRecognitionAsync()` to start and stop transcription.
     * <p>
     * Note: see https://docs.microsoft.com/en-ca/azure/cognitive-services/speech-service/how-to-translate-speech-java
     * for more details
     *
     * @param outputLanguages List of languages to translate to
     * @return New speech translator
     */
    public TranslationRecognizer getTranslationRecognizer(final List<String> outputLanguages) {
        SpeechTranslationConfig config = SpeechTranslationConfig.fromSubscription(SPEECH_SUB_KEY, SERVICE_REGION);
        for (final String lang : outputLanguages) {
            config.addTargetLanguage(lang);
        }
        return new TranslationRecognizer(config);
    }
}
