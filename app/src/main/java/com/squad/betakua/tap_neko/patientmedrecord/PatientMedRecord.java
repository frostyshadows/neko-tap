package com.squad.betakua.tap_neko.patientmedrecord;

public class PatientMedRecord {
    @com.google.gson.annotations.SerializedName("id")
    public String id;
    @com.google.gson.annotations.SerializedName("productID")
    public String productID;
    @com.google.gson.annotations.SerializedName("nfcID")
    public String nfcID;
    @com.google.gson.annotations.SerializedName("rxNumber")
    private String rxNumber;
    @com.google.gson.annotations.SerializedName("directions")
    private String directions;
    @com.google.gson.annotations.SerializedName("quantity")
    private String quantity;
    @com.google.gson.annotations.SerializedName("refills")
    private String refills;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNfcID() {
        return nfcID;
    }

    public void setNfcID(String id) {
        this.nfcID = id;
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

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getRefills() {
        return refills;
    }

    public void setRefills(String refills) {
        this.refills = refills;
    }
}
