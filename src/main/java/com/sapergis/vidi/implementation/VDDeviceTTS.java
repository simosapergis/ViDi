package com.sapergis.vidi.implementation;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import com.sapergis.vidi.R;
import com.sapergis.vidi.helper.VDHelper;
import com.sapergis.vidi.interfaces.IVDTextOperations;

import java.util.Locale;

public class VDDeviceTTS {
    private final String className = getClass().getSimpleName();
    private TextToSpeech textToSpeech;
    private final Context context;

    public VDDeviceTTS(Context context) {
        this.context = context;
        initialize();
    }

    private void initialize (){
        VDHelper.debugLog(className, context.getString(R.string.tts_onDevice_initiating));
        textToSpeech = new TextToSpeech(context, status -> {
            if ( status == TextToSpeech.SUCCESS){
                textToSpeech.setLanguage(Locale.ENGLISH);
                VDHelper.debugLog(className, context.getString(R.string.tts_onDevice_initiated)+ Locale.ENGLISH);
            }
        });
    }

    public void speak (String text){
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null,null);
    }

    public synchronized void speak (String text, IVDTextOperations ivdTextOperations){
        try {
            VDHelper.debugLog(className, context.getString(R.string.device_tts_speaking));
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null,null);
            VDHelper.debugLog(className, context.getString(R.string.device_tts_finished));
            ivdTextOperations.onDeviceTTSFinished();
        }catch (Exception e){
            ivdTextOperations.onOperationTerminated(e.getMessage());
        }

    }

    public void shutDown (){
        textToSpeech.shutdown();
        VDHelper.debugLog(className, "->> was ShutDown");
    }

}
