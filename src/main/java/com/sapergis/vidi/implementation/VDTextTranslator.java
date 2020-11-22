package com.sapergis.vidi.implementation;

import android.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.sapergis.vidi.R;
import com.sapergis.vidi.helper.VDHelper;
import com.sapergis.vidi.helper.VDText;
import com.sapergis.vidi.interfaces.IVDTextOperations;
import androidx.annotation.NonNull;


public class VDTextTranslator {

    public static void initiateDeviceTranslation (VDText vdText, IVDTextOperations ivdTextOperations) {
        TranslatorOptions options = new TranslatorOptions.Builder()
                //.setSourceLanguage(TranslateLanguage.fromLanguageTag(vdText.getIdentifiedLanguage()))
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.GREEK)
                .build();
        final Translator vdTranslator = Translation.getClient(options);
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        //TODO : Model download to be done on boot of the app, to avoid delay here
        //TODO + Check for internet connection
        Task<Void> modelDownload = vdTranslator.downloadModelIfNeeded(conditions);
        modelDownload.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                translate(vdText, vdTranslator, ivdTextOperations);
            }
        });
        modelDownload.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private static void translate (VDText vdText, Translator vdTranslator,
                                   IVDTextOperations ivdTextOperations){
        Task<String> translation = vdTranslator.translate(vdText.getRawText());
        translation.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                vdText.setTranslatedText(translation.getResult());
                VDHelper.debugLog(getClass().getSimpleName(), "Translated text is => ["+translation.getResult()+"]");
                ivdTextOperations.onTextTranslated(vdText);
            }
        });
        translation.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                VDHelper.debugLog(getClass().getSimpleName(), "Translation failed...");
            }
        });

    }
}
