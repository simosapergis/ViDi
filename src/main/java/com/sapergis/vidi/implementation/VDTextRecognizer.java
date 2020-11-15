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

import com.google.mlkit.vision.text.TextRecognizerOptions;
import com.sapergis.vidi.MainActivity;
import com.sapergis.vidi.helper.VDText;

import java.util.Arrays;

import androidx.annotation.NonNull;


public class VDTextRecognizer {
    VDText vdText = new VDText();
    Bitmap bitmap;
//    MutableLiveData<VDText>  mutableVDText;
//    FirebaseVisionImage vdImage;


    public VDTextRecognizer(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void runCloudTextRecognition() {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionCloudTextRecognizerOptions options = new FirebaseVisionCloudTextRecognizerOptions.Builder()
                .setLanguageHints(Arrays.asList("en","el"))
                .build();
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getCloudTextRecognizer(options);
        Task<FirebaseVisionText> result = detector.processImage(firebaseVisionImage);
        result.addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                //String refinedText =  vdText.refineText(firebaseVisionText.getText());
                                //vdText.setRawText(refinedText);
                                Log.d(MainActivity.TAG, "TEXT FOUND [CLOUD]=> "+firebaseVisionText.getText());
                                vdText.setRawText(firebaseVisionText.getText());
                                //mutableVDText.postValue(vdText);
                                //new VDLanguageIdentifier(mutableVDText).identifyLanguage(vdText);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        }
                );
    }

    public void runDeviceTextRecognition(){
        InputImage inputImage = InputImage.fromBitmap(bitmap, 90);
        TextRecognizer recognizer = TextRecognition.getClient();
        Task<Text> result = recognizer.process(inputImage);
        result.addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                // Task completed successfully
                                String ttxt = visionText.getText();
                                Log.d(MainActivity.TAG, "TEXT FOUND [DEVICE]=>" +ttxt);
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

    public VDText getText() {
        return vdText;
    }

}
