package com.sapergis.vidi.interfaces;

import android.graphics.Bitmap;

import com.sapergis.vidi.helper.VDText;

public interface IVDTextOperations {

    void textRecognitionOn(Bitmap bitmap);
    void onTextRecognized(String recognizedText);
    void onLanguageIdentified(String languageCode, String recognizedText);
    void onTextTranslated(VDText vdText);
    void onTextToSpeechFinished();
    void onTextOperationTerminated();
}
