package com.squad.betakua.tap_neko.azure;

public class DebugItem {
    @com.google.gson.annotations.SerializedName("id")
    public String id;
    @com.google.gson.annotations.SerializedName("nfcID")
    public String nfcID;

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
}
