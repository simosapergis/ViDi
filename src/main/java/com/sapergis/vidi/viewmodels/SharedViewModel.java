package com.sapergis.vidi.viewmodels;

import android.graphics.Bitmap;
import android.util.Log;
import com.sapergis.vidi.helper.VDHelper;
import com.sapergis.vidi.helper.VDText;
import com.sapergis.vidi.implementation.VDLanguageIdentifier;
import com.sapergis.vidi.implementation.VDTextRecognizer;
import com.sapergis.vidi.interfaces.IVDTextOperations;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel implements IVDTextOperations {
    private final MutableLiveData<Bitmap> captured = new MutableLiveData<Bitmap>();
    private final MutableLiveData<VDText> validRecognizedText = new MutableLiveData<VDText>();

    public void setBitmap (Bitmap bitmap){
        captured.setValue(bitmap);
        textRecognitionOn(bitmap);
    }

    public void setValidRecognizedText (VDText validText){
        validRecognizedText.setValue(validText);
    }

    public LiveData<Bitmap> getCaptured(){
        return captured;
    }

    public LiveData<VDText> getValidRecognizedText(){
        return validRecognizedText;
    }

    @Override
    public void textRecognitionOn(Bitmap bitmap) {
        //VDTextRecognizer vdTextRecognizer = new VDTextRecognizer(bitmap, this);
        Log.d(VDHelper.TAG,"Text recognition started...");
        VDTextRecognizer.runCloudTextRecognition(bitmap,this);
       // VDTextRecognizer.runDeviceTextRecognition(bitmap, this);
    }

    @Override
    public void onTextRecognized(String recognizedText) {
        //Log.d(VDHelper.TAG, "Text recognized is ["+recognizedText+"]");
        VDLanguageIdentifier.identify(recognizedText, this);
    }

    @Override
    public void onLanguageIdentified(String languageCode, String recognizedText) {
        Log.d(VDHelper.TAG, "Identified language code is ["+languageCode+"]");
        VDText vdText = new VDText();
        vdText.setRawText(recognizedText);
        setValidRecognizedText(vdText);
    }

    @Override
    public void onTextTranslated() {

    }
}
