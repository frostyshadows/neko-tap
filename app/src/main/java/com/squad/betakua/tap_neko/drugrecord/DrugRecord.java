package com.squad.betakua.tap_neko.drugrecord;

public class DrugRecord {
    private static String recordType = "DRUG";

    // universal identifiers
    private String productID;   // the barcode ID
    private String DIN;         // the NDC (US), DIN (CA)

    // pharmacy-specific identifiers
    private String rxNumber;    // prescription number

    // names and labels
    private String genericName; // e.g. Amoxicillin/Clavulanate
    private String tradeName;   // e.g. "Apo-Amoxiclav"
    private String label;       // e.g. "Apo-Amoxiclav 250/5mg ORAL SUSPENSION (Strawberry Flavor)"

    // dosage information
    private String dose;       // e.g. 10/5, 60, 10.5
    private String dosageForm; // e.g. CAP, TAB
    private String doseUnit;   // e.g. mg, mcg, mL

    // supplemental information
    private String url;         // e.g. I07EGu4Z9TU
    private String webUrl;      // e.g. "https://www.humira.com/humira-complete/injection"

    public String getRecordType() {
        return recordType;
    }

    public String getProductID() {
        return this.productID;
    }
    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getDIN() {
        return this.DIN;
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
        return this.genericName;
    }
    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getTradeName() {
        return this.tradeName;
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
