package com.sapergis.vidi.interfaces;

import android.graphics.Bitmap;

public interface VDTextOperations {

    public void textRecognitionOn(Bitmap bitmap);
    public void onTextRecognized(String recognizedText);
    public void onLanguageIdentified();
    public void onTextTranslated();
}
