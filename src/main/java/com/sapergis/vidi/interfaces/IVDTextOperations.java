package com.sapergis.vidi.interfaces;

import android.graphics.Bitmap;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.sapergis.vidi.helper.VDText;
import javax.annotation.Nullable;

public interface IVDTextOperations {

    void textRecognitionOn(Bitmap bitmap);
    void onTextRecognized(String recognizedText);
    void onLanguageIdentified(String languageCode, VDText vdText);
    void onTextTranslated(VDText vdText);
    void onCloudTTSFinished(SynthesizeSpeechResponse response);
    void onDeviceTTSFinished();
    void onOperationTerminated(@Nullable String message);
    void onSpeechServiceFinished();
}
