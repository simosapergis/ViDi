package com.sapergis.vidi.implementation;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognition;
import com.sapergis.vidi.helper.VDHelper;
import com.sapergis.vidi.helper.VDText;
import com.sapergis.vidi.interfaces.VDTextOperations;


import java.util.Arrays;

import androidx.annotation.NonNull;


public class VDTextRecognizer {
//    VDText vdText = new VDText();
    //SharedViewModel sharedViewModel;
//    VDTextOperations vdTextOperations;
//    Bitmap bitmap;
//    MutableLiveData<VDText>  mutableVDText;
//    FirebaseVisionImage vdImage;


    public VDTextRecognizer(Bitmap bitmap, VDTextOperations vdTextOperations) {
//        this.bitmap = bitmap;
//        this.vdTextOperations = vdTextOperations;
        //this.sharedViewModel = sharedViewModel;
    }

    public static void runCloudTextRecognition(Bitmap bitmap, VDTextOperations vdTextOperations) {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionCloudTextRecognizerOptions options = new FirebaseVisionCloudTextRecognizerOptions.Builder()
                .setLanguageHints(Arrays.asList(VDHelper.LOCALES))
                .build();
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getCloudTextRecognizer(options);
        Task<FirebaseVisionText> result = detector.processImage(firebaseVisionImage);
        result.addOnSuccessListener(
                new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        //String refinedText =  vdText.refineText(firebaseVisionText.getText());
                        //vdText.setRawText(refinedText);
                        Log.d(VDHelper.TAG, "TEXT FOUND [CLOUD]=> " + firebaseVisionText.getText());
                        vdTextOperations.onTextRecognized(firebaseVisionText.getText());
                        //mutableVDText.postValue(vdText);
                        //new VDLanguageIdentifier(mutableVDText).identifyLanguage(vdText);
                    }
                });
        result.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    public static void runDeviceTextRecognition(Bitmap bitmap, VDTextOperations vdTextOperations){
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient();
        Task<Text> result = recognizer.process(inputImage);
        result.addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                // Task completed successfully
                                Log.d(VDHelper.TAG, "TEXT FOUND [DEVICE]=>" +visionText.getText());
                                vdTextOperations.onTextRecognized(visionText.getText());
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });
    }

}
