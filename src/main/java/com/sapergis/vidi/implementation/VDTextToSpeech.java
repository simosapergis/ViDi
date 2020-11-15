package com.sapergis.vidi.implementation;

import android.app.Activity;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.sapergis.vidi.helper.VDHelper;

import java.util.Locale;

public class VDTextToSpeech {

//    private static VDTextToSpeech instance;
    private TextToSpeech textToSpeech;
    private final Context context;

    public VDTextToSpeech(Context context) {
        this.context = context;
        initialize();
    }

//    public static synchronized VDTextToSpeech getInstance(){
//        if(instance == null){
//            instance = new VDTextToSpeech();
//            instance.initialize();
//        }
//        return instance;
//    }

    private void initialize (){
        textToSpeech = new TextToSpeech(context, status -> {
            if ( status == TextToSpeech.SUCCESS){
                int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                Log.d(VDHelper.TAG, "TextToSpeech->> Initiated :: Language ->> "+lang);
            }
        });
    }

    public void speak (String text){
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null,null);
    }

    public void shutDown (){
        textToSpeech.shutdown();
        Log.d("TextToSpeech", "->> was ShutDown");
    }
}
