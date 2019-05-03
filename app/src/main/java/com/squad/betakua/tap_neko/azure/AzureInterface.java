package com.squad.betakua.tap_neko.azure;

import android.content.Context;
import android.util.Log;

import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
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
    private static final String SPEECH_SUB_KEY = BuildConfig.nekotap_speech_key1;
    private static final String STORAGE_ACCOUNT_NAME = BuildConfig.azure_blob_storage_account_name;
    private static final String STORAGE_ACCOUNT_KEY = BuildConfig.nekotap_blob_key1;
    private static final String STORAGE_CONTAINER_NAME = BuildConfig.azure_blob_storage_blob_container_name;
    private static final String SERVICE_REGION = "westus";

    private static AzureInterface AZURE_INTERFACE = null;
    private final SpeechConfig speechConfig;
    private final CloudStorageAccount storageAccount;
    private OnDownloadAudioFileListener downloadAudioFileListener;
    private OnUploadAudioFileListener uploadAudioFileListener;

    // Mobile App Services
    private static final String MOBILE_APP_SERVICES_URL = "https://nekotap.azurewebsites.net";
    private static final String DEBUG_TABLE_NAME = "debug_item_table";
    private static final String INFO_TABLE_NAME = "info_item_table";
    private static final String DRUG_INFO_TABLE_NAME = "drug_info_item";
    private static final String DRUG_RECORD_TABLE_NAME = "drug_record_table";
    private static final String DEVICE_RECORD_TABLE_NAME = "device_record_table";
    private static final String TRANSLATIONS_TABLE_NAME = "translated_drug_info";
    private final MobileServiceClient mClient;
    private final MobileServiceTable<DebugItem> debugTable;
    private final MobileServiceTable<InfoItem> infoTable;
    private final MobileServiceTable<DrugInfoItem> drugInfoTable;
    private final MobileServiceTable<DrugRecord> drugRecordTable;
    private final MobileServiceTable<DeviceRecord> deviceRecordTable;
    private final MobileServiceTable<TranslationsItem> translationsTable;


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
            mClient = new MobileServiceClient(MOBILE_APP_SERVICES_URL, context);

            this.infoTable = mClient.getTable(INFO_TABLE_NAME, InfoItem.class);
            this.debugTable = mClient.getTable(DEBUG_TABLE_NAME, DebugItem.class);
            this.drugInfoTable = mClient.getTable(DRUG_INFO_TABLE_NAME, DrugInfoItem.class);
            this.drugRecordTable = mClient.getTable(DRUG_RECORD_TABLE_NAME, DrugRecord.class);
            this.deviceRecordTable = mClient.getTable(DEVICE_RECORD_TABLE_NAME, DeviceRecord.class);
            this.translationsTable = mClient.getTable(TRANSLATIONS_TABLE_NAME, TranslationsItem.class);
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
     * @param url        URL of instruction video
     */
    public ListenableFuture<InfoItem> writeInfoItem(String nfcID,
                                                    String productID,
                                                    String productName,
                                                    String transcript,
                                                    String url,
                                                    String webUrl,
                                                    String pharmacyPhone,
                                                    String pharmacyName,
                                                    String pharmacist,
                                                    String translated,
                                                    String reminder) {
        final InfoItem item = new InfoItem();
        // Log.e("writing... ", nfcID + " " + productID + " " + productName + " " + transcript);
        // Log.e("writing... ", url + " " + webUrl + " " + pharmacyPhone + " " + pharmacyName);
        // Log.e("writing... ", pharmacist + " " + translated + " " + reminder);

        // item.setId(nfcID);
        item.setNfcID(nfcID);
        item.setProductID(productID);
        item.setProductName(productName);
        item.setTranscript(transcript);
        item.setTranslationsID(UUID.randomUUID().toString());
        item.setURL(url);
        item.setWebURL(webUrl);
        item.setPharmacyPhone(pharmacyPhone);
        item.setPharmacyName(pharmacyName);
        item.setPharmacist(pharmacist);
        item.setTranslated(translated);
        item.setReminder(reminder);

        return this.infoTable.insert(item);
    }

    public ListenableFuture<InfoItem> checkIfInfoItemRowExists(String id) {
        return this.infoTable.lookUp(id);
    }

    public ListenableFuture<InfoItem> updateInfoItem(String nfcID,
                                                     String productID,
                                                     String productName,
                                                     String transcript,
                                                     String url,
                                                     String webUrl,
                                                     String pharmacyPhone,
                                                     String pharmacyName,
                                                     String pharmacist,
                                                     String translated,
                                                     String reminder) {
        final InfoItem item = new InfoItem();
        Log.e("UPDATING:", nfcID + " " + productID + " " + transcript + " " + url);

        item.setId(nfcID);
        item.setNfcID(nfcID);
        item.setProductID(productID);
        item.setProductName(productName);
        item.setTranscript(transcript);
        item.setTranslationsID(UUID.randomUUID().toString());
        item.setURL(url);
        item.setWebURL(webUrl);
        item.setPharmacyName(pharmacyName);
        item.setPharmacyPhone(pharmacyPhone);
        item.setPharmacist(pharmacist);
        item.setTranslated(translated);
        item.setReminder(reminder);
        return this.infoTable.update(item);
    }


    /**
     * Look up an info item in Azure InfoTable by NFC ID
     *
     * @param nfcID NFC ID to look up
     * @return Future for a list of matching InfoItems
     */
    public ListenableFuture<MobileServiceList<InfoItem>> readInfoItem(String nfcID) {
        Log.e("trying to read id ", nfcID);
        return this.infoTable.where().field("nfcID").eq(nfcID).execute();
    }

    /**
     * Write a new drug info item to the Azure DrugInfoTable
     *
     * @param nfcID      product ID
     * @param text       Text of instructions
     * @param youtubeURL YouTube URL of how-to video
     */
    public void writeDrugInfoItem(String nfcID, String text, String youtubeURL) {
        final DrugInfoItem item = new DrugInfoItem();
        item.setId(UUID.randomUUID().toString());
        item.setNfcID(nfcID);
        item.setText(text);
        item.setYoutubeURL(youtubeURL);
        this.drugInfoTable.insert(item);
    }

    /**
     * Look up a drug info item in Azure DrugInfoTable by NFC ID
     *
     * @param productID ProductID ID to look up
     * @return Future for a list of matching InfoItems
     */
    public ListenableFuture<MobileServiceList<DrugInfoItem>> readDrugInfoItem(String productID) {
        return this.drugInfoTable.where().field("productID").eq(productID).execute();
    }

    /**
     * Upload an audio file to Azure
     * Warning: will overwrite file if file with the same audioTitle already exists
     *
     * @param audioTitle Title of audio clip to store; note: should be `$NFCID_$LANG`
     * @param in         InputStream to read from
     * @param length     Length in bytes of file (or -1 if unknown)
     */
    public void uploadAudio(final String audioTitle, final InputStream in, final long length, OnUploadAudioFileListener listener) {
        uploadAudioFileListener = listener;
        new Thread(() -> {
            try {
                final CloudBlobClient blobClient = this.storageAccount.createCloudBlobClient();
                final CloudBlobContainer container =
                        blobClient.getContainerReference(STORAGE_CONTAINER_NAME);
                final CloudBlockBlob blockBlob = container.getBlockBlobReference(audioTitle);
                blockBlob.upload(in, length);
                uploadAudioFileListener.onUploadComplete("SUCCESS");
                Log.e("Azure uploadAudio: ", "uploaded with length: " + length + " key: " + audioTitle + " container: " + STORAGE_CONTAINER_NAME);
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
     */
    public void downloadAudio(final String audioTitle, final OutputStream out, OnDownloadAudioFileListener listener) {
        downloadAudioFileListener = listener;

        new Thread(() -> {
            try {
                final CloudBlobClient blobClient = this.storageAccount.createCloudBlobClient();
                final CloudBlobContainer container =
                        blobClient.getContainerReference(STORAGE_CONTAINER_NAME);
                final CloudBlockBlob blockBlob = container.getBlockBlobReference(audioTitle);
                blockBlob.download(out);
                downloadAudioFileListener.onDownloadComplete("SUCCESS");
                Log.e("Azure downloadAudio: ", "downloaded reference" + audioTitle + " and container " + STORAGE_CONTAINER_NAME);
            } catch (URISyntaxException | StorageException e) {
                try {
                    throw new AzureInterfaceException(e.getMessage());
                } catch (AzureInterfaceException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();
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
    public SpeechRecognizer getSpeechRecognizer(AudioConfig audioConfig) {
        return new SpeechRecognizer(speechConfig, audioConfig);
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

    /**
     * Look up available translations
     *
     * @param translationsID translations ID to look up
     * @return Future for a list of matching TranslationItems
     */
    public ListenableFuture<MobileServiceList<TranslationsItem>> readTranslationsItem(String translationsID) {
        return this.translationsTable.where().field("id").eq(translationsID).execute();
    }

    /**
     * Populate Azure with mock drug database
     *
     */
    public ListenableFuture<DrugRecord> writeDrugRecord(
                                                    String productID,
                                                    String DIN,
                                                    String rxNumber,
                                                    String genericName,
                                                    String tradeName,
                                                    String label,
                                                    String dose,
                                                    String dosageForm,
                                                    String doseUnit,
                                                    String url,
                                                    String webUrl) {
        final DrugRecord item = new DrugRecord();

        item.setProductID(productID);
        item.setDIN(DIN);
        item.setRxNumber(rxNumber);
        item.setGenericName(genericName);
        item.setTradeName(tradeName);
        item.setLabel(label);
        item.setDose(dose);
        item.setDosageForm(dosageForm);
        item.setDoseUnit(doseUnit);
        item.setUrl(url);
        item.setWebUrl(webUrl);

        return this.drugRecordTable.insert(item);
    }


    /**
     * Populate Azure with mock device database
     *
     */
    public ListenableFuture<DeviceRecord> writeDeviceRecord(
            String productID,
            String rxNumber,
            String className,
            String tradeName,
            String label,
            String url,
            String webUrl) {
        final DeviceRecord item = new DeviceRecord();

        item.setProductID(productID);
        item.setRxNumber(rxNumber);
        item.setClassName(className);
        item.setTradeName(tradeName);
        item.setLabel(label);
        item.setUrl(url);
        item.setWebUrl(webUrl);

        return this.deviceRecordTable.insert(item);
    }
}
