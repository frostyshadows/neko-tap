package com.squad.betakua.tap_neko.azure;

import android.content.Context;
import android.icu.text.IDNA;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squad.betakua.tap_neko.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.concurrent.ExecutionException;

public class AzureInterface {
    private static final String CONNECTION_STRING_TEMPLATE = "DefaultEndpointsProtocol=https;" +
            "AccountName=%s;" +
            "AccountKey=%s";
    private static AzureInterface AZURE_INTERFACE = null;
    private final CloudBlobClient blobClient;
    private final MobileServiceTable<InfoItem> infoTable;

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
            final String accountName = BuildConfig.AzureStorageAccountName;
            final String accountKey = BuildConfig.AzureStorageAccountKey;
            final String connectionString =
                    String.format(CONNECTION_STRING_TEMPLATE, accountName, accountKey);
            final CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse(connectionString);
            this.blobClient = storageAccount.createCloudBlobClient();
            final MobileServiceClient mobileServiceClient =
                    new MobileServiceClient("https://neko-tap.azurewebsites.net", context);
            this.infoTable = mobileServiceClient.getTable(InfoItem.class);
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
     * @param nfcID           NFC ID
     * @param productID       Product ID
     * @param instrTranscript Transcript of instruction audio
     */
    public void writeInfoItem(String nfcID, String productID, String instrTranscript) {
        this.infoTable.insert(new InfoItem(nfcID, productID, instrTranscript));
    }

    /**
     * Look up an info item in Azure InfoTable by NFC ID
     *
     * @param nfcID NFC ID to look up
     * @return InfoItem matching NFC ID
     * @throws AzureInterfaceException If something goes wrong
     */
    public InfoItem readInfoItem(String nfcID) throws AzureInterfaceException {
        try {
            return this.infoTable.where().field("nfcID").eq(nfcID).execute().get().get(0);
        } catch (InterruptedException e) {
            throw new AzureInterfaceException(e.getMessage());
        } catch (ExecutionException e) {
            throw new AzureInterfaceException(e.getMessage());
        }
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
    public void uploadAudio(final String audioTitle, final InputStream in, final long length)
            throws AzureInterfaceException {
        try {
            final CloudBlobContainer container =
                    this.blobClient.getContainerReference("instructionaudio");
            final CloudBlockBlob blockBlob = container.getBlockBlobReference(audioTitle);
            blockBlob.upload(in, length);
        } catch (URISyntaxException e) {
            throw new AzureInterfaceException(e.getMessage());
        } catch (StorageException e) {
            throw new AzureInterfaceException(e.getMessage());
        } catch (IOException e) {
            throw new AzureInterfaceException(e.getMessage());
        }
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
            final CloudBlobContainer container =
                    this.blobClient.getContainerReference("instructionaudio");
            final CloudBlockBlob blockBlob = container.getBlockBlobReference(audioTitle);
            blockBlob.download(out);
        } catch (URISyntaxException e) {
            throw new AzureInterfaceException(e.getMessage());
        } catch (StorageException e) {
            throw new AzureInterfaceException(e.getMessage());
        }
    }
}
