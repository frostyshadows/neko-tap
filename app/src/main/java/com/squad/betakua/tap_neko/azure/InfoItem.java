package com.squad.betakua.tap_neko.azure;

import com.microsoft.windowsazure.mobileservices.table.DateTimeOffset;

public class InfoItem {
    @com.google.gson.annotations.SerializedName("id")
    private String id;
    @com.google.gson.annotations.SerializedName("createdAt")
    private DateTimeOffset createdAt;
    @com.google.gson.annotations.SerializedName("updatedAt")
    private DateTimeOffset updatedAt;
    @com.google.gson.annotations.SerializedName("version")
    private String version;
    @com.google.gson.annotations.SerializedName("nfcID")
    private String nfcID;
    @com.google.gson.annotations.SerializedName("productID")
    private String productID;
    @com.google.gson.annotations.SerializedName("transcript")
    private String transcript;
    @com.google.gson.annotations.SerializedName("url")
    private String url;

    public String getId() {
        return id;
    }

    public final void setId(String id) {
        this.id = id;
    }

    public DateTimeOffset getUpdatedAt() {
        return updatedAt;
    }

    protected void setUpdatedAt(DateTimeOffset updatedAt) {
        this.updatedAt = updatedAt;
    }

    public DateTimeOffset getCreatedAt() {
        return createdAt;
    }

    protected void setCreatedAt(DateTimeOffset createdAt) {
        this.createdAt = createdAt;
    }

    public String getVersion() {
        return version;
    }

    public final void setVersion(String version) {
        this.version = version;
    }

    public String getNfcID() {
        return nfcID;
    }

    public void setNfcID(final String nfcID) {
        this.nfcID = nfcID;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(final String productID) {
        this.productID = productID;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(final String transcript) {
        this.transcript = transcript;
    }

    public String getURL() {
        return url;
    }

    public void setURL(final String url) {
        this.url = url;
    }

}
