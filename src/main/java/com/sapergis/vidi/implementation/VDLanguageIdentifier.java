package com.sapergis.vidi.implementation;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.sapergis.vidi.R;
import com.sapergis.vidi.helper.VDHelper;
import com.sapergis.vidi.interfaces.IVDTextOperations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class VDLanguageIdentifier {

    public static void identify(String text, IVDTextOperations IVDTextOperations){
        LanguageIdentifier languageIdentifier = LanguageIdentification.getClient();
        Task<String> result = languageIdentifier.identifyLanguage(text);
        result.addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@Nullable String languageCode) {
                                if ( !languageCode.equals("und") ) {
                                    IVDTextOperations.onLanguageIdentified(languageCode, text);
                                } else {
                                    VDHelper.debugLog(getClass().getSimpleName(), "Can't identify language.");
                                }
                            }
                        });
        result.addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be loaded or other internal error.
                                // ...
                            }
                        });
    }
}
