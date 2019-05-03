package com.squad.betakua.tap_neko.azure;

public class DeviceRecord {
    @com.google.gson.annotations.SerializedName("id")
    public String id;
    @com.google.gson.annotations.SerializedName("productID")
    private String productID;
    @com.google.gson.annotations.SerializedName("rxNumber")
    private String rxNumber;
    @com.google.gson.annotations.SerializedName("className")
    private String className;
    @com.google.gson.annotations.SerializedName("tradeName")
    private String tradeName;
    @com.google.gson.annotations.SerializedName("label")
    private String label;
    @com.google.gson.annotations.SerializedName("url")
    private String url;
    @com.google.gson.annotations.SerializedName("webUrl")
    private String webUrl;

    public String getId() {
        return id;
    }

    public final void setId(String id) {
        this.id = id;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getRxNumber() {
        return rxNumber;
    }

    public void setRxNumber(String rxNumber) {
        this.rxNumber = rxNumber;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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
