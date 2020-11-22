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
import com.sapergis.vidi.interfaces.IVDTextOperations;
import java.util.Arrays;
import androidx.annotation.NonNull;


public class VDTextRecognizer {

    public VDTextRecognizer() {

    }

    public static void runCloudTextRecognition(Bitmap bitmap, IVDTextOperations IVDTextOperations) {
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
                        VDHelper.debugLog("VDTextRecognizer", "TEXT FOUND [CLOUD]=> " + firebaseVisionText.getText());
                        IVDTextOperations.onTextRecognized(firebaseVisionText.getText());
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

    public static void runDeviceTextRecognition(Bitmap bitmap, IVDTextOperations IVDTextOperations){
        InputImage inputImage = InputImage.fromBitmap(bitmap, 90);
        TextRecognizer recognizer = TextRecognition.getClient();
        Task<Text> result = recognizer.process(inputImage);
        result.addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                // Task completed successfully
                                VDHelper.debugLog("VDTextRecognizer", "TEXT FOUND [DEVICE]=>" +visionText.getText());
                                IVDTextOperations.onTextRecognized(visionText.getText());
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
