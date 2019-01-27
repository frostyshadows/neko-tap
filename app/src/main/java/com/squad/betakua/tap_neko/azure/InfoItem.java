package com.squad.betakua.tap_neko.azure;

public class InfoItem {
    private String nfcID;
    private String productID;
    private String instrTranscript;

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

    public String getInstrTranscript() {
        return instrTranscript;
    }

    public void setInstrTranscript(final String instrTranscript) {
        this.instrTranscript = instrTranscript;
    }

}
