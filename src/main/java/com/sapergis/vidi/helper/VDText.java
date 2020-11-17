package com.sapergis.vidi.helper;

public class VDText {
    private String rawText;
    private String translatedText;
    private String identifiedLanguage;

    private String traslateTo;

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    public String getIdentifiedLanguage() {
        return identifiedLanguage;
    }

    public void setIdentifiedLanguage(String identifiedLanguage) {
        this.identifiedLanguage = identifiedLanguage;
    }

    public String getTraslateTo() {
        return traslateTo;
    }

    public void setTraslateTo(String traslateTo) {
        this.traslateTo = traslateTo;
    }
}
