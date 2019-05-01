package com.squad.betakua.tap_neko.devicerecord;

public class DeviceRecord {
    private static String recordType = "DEVICE";

    // universal identifiers
    private String productID;   // the barcode ID

    // pharmacy-specific identifiers
    private String rxNumber;    // prescription number (if applicable)

    // names and labels
    private String category;   // e.g. "Spacers", "Blood Pressure Monitors"
    private String tradeName;   // e.g. "Aerochamber"
    private String label;       // e.g. "Aerochamber Spacer for Asthma (Child)"

    // supplemental information
    private String url;         // e.g. I07EGu4Z9TU
    private String webUrl;      // e.g. "https://www.humira.com/humira-complete/injection"

    public String getRecordType() {
        return recordType;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
