package com.sapergis.vidi.implementation;
import android.content.SharedPreferences;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.sapergis.vidi.helper.VDHelper;
import com.sapergis.vidi.interfaces.IVDTextOperations;

public class VDLanguageIdentifier {
    private static final String UND = "und";

    private VDLanguageIdentifier(){

    }

    public static void identify(String text, IVDTextOperations iVDTextOperations,
                                String inputLanguage){
        LanguageIdentifier languageIdentifier = LanguageIdentification.getClient();
        Task<String> result = languageIdentifier.identifyLanguage(text);
        result.addOnSuccessListener(languageCode ->{
                                String message;
                                //Checking if language was recognized and also if it is equals to
                                //the input language we set in preferences
                                if ( !UND.equals(languageCode) && languageCode.equals(inputLanguage)) {
                                    iVDTextOperations.onLanguageIdentified(languageCode, text);
                                }else if (UND.equals(languageCode)){
                                    message = "Can't identify language.";
                                    terminate(iVDTextOperations, message);
                                }
                                else {
                                    message = "Language not supported";
                                    terminate(iVDTextOperations, message);
                                }

                        })
                .addOnFailureListener(e -> {
                        // Model couldn’t be loaded or other internal error.
                        // ...
                        terminate(iVDTextOperations, e.getMessage());
                    })
                .addOnCompleteListener(task -> languageIdentifier.close());
    }

    private static void terminate(IVDTextOperations ivdTextOperations, String message){
        VDHelper.debugLog("VDLanguageIdentifier", message);
        ivdTextOperations.onOperationTerminated(message);
    }
}
