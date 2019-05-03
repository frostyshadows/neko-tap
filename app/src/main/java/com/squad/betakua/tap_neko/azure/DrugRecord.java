package com.squad.betakua.tap_neko.azure;

public class DrugRecord {
    @com.google.gson.annotations.SerializedName("productID")
    private String productID;
    @com.google.gson.annotations.SerializedName("DIN")
    private String DIN;
    @com.google.gson.annotations.SerializedName("rxNumber")
    private String rxNumber;
    @com.google.gson.annotations.SerializedName("genericName")
    private String genericName;
    @com.google.gson.annotations.SerializedName("tradeName")
    private String tradeName;
    @com.google.gson.annotations.SerializedName("label")
    private String label;
    @com.google.gson.annotations.SerializedName("dose")
    private String dose;
    @com.google.gson.annotations.SerializedName("dosageForm")
    private String dosageForm;
    @com.google.gson.annotations.SerializedName("doseUnit")
    private String doseUnit;
    @com.google.gson.annotations.SerializedName("url")
    private String url;
    @com.google.gson.annotations.SerializedName("webUrl")
    private String webUrl;

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getDIN() {
        return DIN;
    }

    public void setDIN(String DIN) {
        this.DIN = DIN;
    }

    public String getRxNumber() {
        return rxNumber;
    }

    public void setRxNumber(String rxNumber) {
        this.rxNumber = rxNumber;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getDoseUnit() {
        return doseUnit;
    }

    public void setDoseUnit(String doseUnit) {
        this.doseUnit = doseUnit;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }
}
