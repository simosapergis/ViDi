package com.sapergis.vidi.implementation;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.sapergis.vidi.helper.VDHelper;
import com.sapergis.vidi.interfaces.IVDTextOperations;

public class VDLanguageIdentifier {
    private static final String UND = "und";

    private VDLanguageIdentifier(){

    }

    public static void identify(String text, IVDTextOperations iVDTextOperations){
        LanguageIdentifier languageIdentifier = LanguageIdentification.getClient();
        Task<String> result = languageIdentifier.identifyLanguage(text);
        result.addOnSuccessListener(languageCode ->{
                                if ( !UND.equals(languageCode) ) {
                                    iVDTextOperations.onLanguageIdentified(languageCode, text);
                                } else {
                                    String message = "Can't identify language.";
                                    VDHelper.debugLog("VDLanguageIdentifier", message);
                                    iVDTextOperations.onOperationTerminated(message);
                                }

                        })
                .addOnFailureListener(e -> {
                        // Model couldn’t be loaded or other internal error.
                        // ...
                        iVDTextOperations.onOperationTerminated(e.getMessage());
                    })
                .addOnCompleteListener(task -> languageIdentifier.close());
    }
}
