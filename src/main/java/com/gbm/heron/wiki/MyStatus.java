package com.gbm.heron.wiki;

public class MyStatus {
    public MyStatus(String lang, String text) {
        this.lang = lang;
        this.text = text;
    }

    public MyStatus() {
    }

    private String lang;

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private String text;
}
