package com.squad.betakua.tap_neko.azure;

public class InfoItem {
    @com.google.gson.annotations.SerializedName("id")
    public String id;
    @com.google.gson.annotations.SerializedName("nfcID")
    public String nfcID;
    @com.google.gson.annotations.SerializedName("productID")
    public String productID;
    @com.google.gson.annotations.SerializedName("transcript")
    public String transcript;
    @com.google.gson.annotations.SerializedName("translationsID")
    public String translationsID;
    @com.google.gson.annotations.SerializedName("url")
    public String url;

    public String getId() {
        return id;
    }

    public final void setId(String id) {
        this.id = id;
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

    public void setTranslationsID(final String translationsID) {
        this.translationsID = translationsID;
    }

    public String getTranslationsID() {
        return translationsID;
    }

    public String getURL() {
        return url;
    }

    public void setURL(final String url) {
        this.url = url;
    }
}
