package com.sapergis.vidi.viewmodels;

import android.content.Context;
import android.graphics.Bitmap;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.sapergis.vidi.helper.VDHelper;
import com.sapergis.vidi.helper.VDText;
import com.sapergis.vidi.implementation.VDTextRecognizer;
import com.sapergis.vidi.interfaces.VDTextOperations;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel implements VDTextOperations {
    private final MutableLiveData<Bitmap> captured = new MutableLiveData<Bitmap>();
    private final MutableLiveData<VDText> recognizedText = new MutableLiveData<VDText>();
    TextToSpeech  tts;

    public void setBitmap (Bitmap bitmap){
        captured.setValue(bitmap);
        textRecognitionOn(bitmap);
    }

    public LiveData<Bitmap> getCaptured(){
        return captured;
    }

   public void textToSpeech(Context context){
        //TODO Implement tts
//          tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if (status == TextToSpeech.SUCCESS){
//                    Locale locale = new Locale("el","GR");
//                    tts.setLanguage(Locale.getDefault());
//                    tts.speak("Καλημέρα",TextToSpeech.QUEUE_FLUSH,null, null);
//                }
//            }
//        });
    }

    @Override
    public void textRecognitionOn(Bitmap bitmap) {
        //VDTextRecognizer vdTextRecognizer = new VDTextRecognizer(bitmap, this);
        Log.d(VDHelper.TAG,"TEXT RECOGNITION STARTED ");
        //vdTextRecognizer.runCloudTextRecognition();
        VDTextRecognizer.runDeviceTextRecognition(bitmap, this);
    }

    @Override
    public void onTextRecognized(String recognizedText) {
        Log.d(VDHelper.TAG, "Text recognized is ["+recognizedText+"]");
    }

    @Override
    public void onLanguageIdentified() {

    }

    @Override
    public void onTextTranslated() {

    }
}
