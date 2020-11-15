package com.sapergis.vidi.viewmodels;

import android.content.Context;
import android.graphics.Bitmap;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.sapergis.vidi.MainActivity;
import com.sapergis.vidi.implementation.VDTextRecognizer;

import java.util.Locale;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Bitmap> captured = new MutableLiveData<Bitmap>();
    TextToSpeech  tts;

    public void setBitmap (Bitmap bitmap){
        captured.setValue(bitmap);
        textRecognitionOn(bitmap);
    }

    public LiveData<Bitmap> getCaptured(){
        return captured;
    }

    private void textRecognitionOn(Bitmap bitmap) {
        VDTextRecognizer vdTextRecognizer = new VDTextRecognizer(bitmap);
        Log.d(MainActivity.TAG,"TEXT RECOGNITION STARTED ");
       //vdTextRecognizer.runCloudTextRecognition();
        vdTextRecognizer.runDeviceTextRecognition();
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
}
