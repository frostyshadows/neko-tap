package com.squad.betakua.tap_neko.azure;

public class InfoItem {
    @com.google.gson.annotations.SerializedName("id")
    public String id;
    @com.google.gson.annotations.SerializedName("nfcID")
    public String nfcID;
    @com.google.gson.annotations.SerializedName("productID")
    public String productID;
    @com.google.gson.annotations.SerializedName("productName")
    public String productName;
    @com.google.gson.annotations.SerializedName("transcript")
    public String transcript;
    @com.google.gson.annotations.SerializedName("translationsID")
    public String translationsID;
    @com.google.gson.annotations.SerializedName("url")
    public String url;
    @com.google.gson.annotations.SerializedName("weburl")
    public String webUrl;
    @com.google.gson.annotations.SerializedName("pharmacyPhone")
    public String pharmacyPhone;
    @com.google.gson.annotations.SerializedName("pharmacyName")
    public String pharmacyName;
    @com.google.gson.annotations.SerializedName("pharmacist")
    public String pharmacist;
    @com.google.gson.annotations.SerializedName("translated")
    public String translated;
    @com.google.gson.annotations.SerializedName("reminder")
    public String reminder;

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

    public String getProductName() {
        return productName;
    }

    public void setProductName(final String productName) {
        this.productName = productName;
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

    public String getWebURL() {
        return webUrl;
    }

    public void setWebURL(final String webUrl) {
        this.webUrl = webUrl;
    }

    public String getPharmacyPhone() {
        return pharmacyPhone;
    }

    public void setPharmacyPhone(final String pharmacyPhone) {
        this.pharmacyPhone = pharmacyPhone;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(final String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    public String getPharmacist() {
        return pharmacist;
    }

    public void setPharmacist(final String pharmacist) {
        this.pharmacist = pharmacist;
    }

    public String getTranslated() {
        return translated;
    }

    public void setTranslated(final String translated) {
        this.translated = translated;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(final String reminder) {
        this.reminder = reminder;
    }
}
