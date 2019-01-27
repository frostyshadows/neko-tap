package com.squad.betakua.tap_neko.azure;

import com.microsoft.cognitiveservices.speech.translation.SpeechTranslationConfig;
import com.microsoft.windowsazure.mobileservices.table.DateTimeOffset;

public class DrugInfoItem {
    @com.google.gson.annotations.SerializedName("id")
    private String id;
    @com.google.gson.annotations.SerializedName("version")
    private String version;
    @com.google.gson.annotations.SerializedName("nfcID")
    private String nfcID;
    @com.google.gson.annotations.SerializedName("text")
    private String text;
    @com.google.gson.annotations.SerializedName("youtubeURL")
    private String youtubeURL;

    public String getId() {
        return id;
    }

    public final void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public final void setVersion(String version) {
        this.version = version;
    }

    public String getNfcID() {
        return nfcID;
    }

    public final void setNfcID(String nfcID) {
        this.nfcID = nfcID;
    }

    public String getText() {
        return text;
    }

    public final void setText(String text) {
        this.text = text;
    }

    public String getYoutubeURL() {
        return youtubeURL;
    }

    public final void setYoutubeURL(String youtubeURL) {
        this.youtubeURL = youtubeURL;
    }

}
