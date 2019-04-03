package com.squad.betakua.tap_neko.azure;

public class TranslationsItem {
    @com.google.gson.annotations.SerializedName("id")
    private String id;
    @com.google.gson.annotations.SerializedName("en")
    private String en;
    @com.google.gson.annotations.SerializedName("fr")
    private String fr;
    @com.google.gson.annotations.SerializedName("zh_Hans")
    private String zh_Hans;
    @com.google.gson.annotations.SerializedName("yue")
    private String yue;
    @com.google.gson.annotations.SerializedName("es")
    private String es;
    @com.google.gson.annotations.SerializedName("fil")
    private String fil;
    @com.google.gson.annotations.SerializedName("ar")
    private String ar;
    @com.google.gson.annotations.SerializedName("de")
    private String de;
    @com.google.gson.annotations.SerializedName("ko")
    private String ko;

    public String getId() {
        return id;
    }

    public final void setId(String id) {
        this.id = id;
    }

    public String getEn() {
        return en;
    }

    public String getFr() {
        return fr;
    }

    public String getZh_Hans() {
        return zh_Hans;
    }

    public String getYue() {
        return yue;
    }

    public String getEs() {
        return es;
    }

    public String getFil() {
        return fil;
    }

    public String getAr() {
        return ar;
    }

    public String getDe() {
        return de;
    }

    public String getKo() {
        return ko;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public void setFr(String fr) {
        this.fr = fr;
    }

    public void setZh_Hans(String zh_Hans) {
        this.zh_Hans = zh_Hans;
    }

    public void setYue(String yue) {
        this.yue = yue;
    }

    public void setEs(String es) {
        this.es = es;
    }

    public void setFil(String fil) {
        this.fil = fil;
    }

    public void setAr(String ar) {
        this.ar = ar;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public void setKo(String ko) {
        this.ko = ko;
    }
}
