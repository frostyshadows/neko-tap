package com.squad.betakua.tap_neko.azure;

public class PhraseItem {
    @com.google.gson.annotations.SerializedName("DIN")
    private String DIN;
    @com.google.gson.annotations.SerializedName("phrase")
    private String phrase;
    @com.google.gson.annotations.SerializedName("phraseID")
    private String phraseID;
    @com.google.gson.annotations.SerializedName("category")
    private String category;

    public String getDIN() {
        return DIN;
    }

    public void setDIN(String DIN) {
        this.DIN = DIN;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String getPhraseID() {
        return phraseID;
    }

    public void setPhraseID(String phraseID) {
        this.phraseID = phraseID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
