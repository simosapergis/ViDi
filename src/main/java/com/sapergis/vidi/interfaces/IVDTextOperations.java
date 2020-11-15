package com.sapergis.vidi.interfaces;

import android.graphics.Bitmap;

public interface IVDTextOperations {

    public void textRecognitionOn(Bitmap bitmap);
    public void onTextRecognized(String recognizedText);
    public void onLanguageIdentified(String languageCode, String recognizedText);
    public void onTextTranslated();
}
