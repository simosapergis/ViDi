package com.sapergis.vidi.implementation;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.sapergis.vidi.helper.VDHelper;
import com.sapergis.vidi.helper.VDText;
import com.sapergis.vidi.interfaces.IVDTextOperations;



public class VDTextTranslator {

    private VDTextTranslator(){
        throw new IllegalStateException(VDHelper.UTILITY_CLASS);
    }

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
        modelDownload.addOnSuccessListener(aVoid ->
                translate(vdText, vdTranslator, ivdTextOperations));
        modelDownload.addOnFailureListener(e -> {
            ivdTextOperations.onOperationTerminated(e.getMessage());
            vdTranslator.close();
        });
    }

    private static void translate (VDText vdText, Translator vdTranslator,
                                   IVDTextOperations ivdTextOperations){
        Task<String> translation = vdTranslator.translate(vdText.getRawText());
        translation.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                vdText.setTranslatedText(translation.getResult());
                VDHelper.debugLog(getClass().getSimpleName(), "Translating to => ["+vdText.getTraslateTo()+"]");
                VDHelper.debugLog(getClass().getSimpleName(), "Translated text is => ["+translation.getResult()+"]");
                ivdTextOperations.onTextTranslated(vdText);
                vdTranslator.close();
            }
        });
        translation.addOnFailureListener(e-> {
                VDHelper.debugLog("VDTextTranslator", "Translation failed...");
                ivdTextOperations.onOperationTerminated(e.getMessage());
                vdTranslator.close();
        });

    }
}
