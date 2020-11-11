package com.sapergis.vidi.viewmodels;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Bitmap> captured = new MutableLiveData<Bitmap>();

    public void setBitmap (Bitmap bitmap){
        captured.setValue(bitmap);
    }

    public LiveData<Bitmap> getCaptured(){
        return captured;
    }
}
