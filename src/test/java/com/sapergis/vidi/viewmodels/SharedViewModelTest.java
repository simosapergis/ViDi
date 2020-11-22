package com.sapergis.vidi.viewmodels;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sapergis.vidi.MainActivity;

import org.junit.Before;
import org.junit.Test;
import java.io.File;
import static org.junit.Assert.*;

public class SharedViewModelTest {
    SharedViewModel sharedViewModel;
    Bitmap storedImage;
    MainActivity mainActivity = new MainActivity();

    @Before
    public void setUp() throws Exception {

//        if(GrantPermissions.allPermissionsGranted(mainActivity)){
//            storedImage = InputImage.fromFilePath(mainActivity.getApplicationContext(),
//                    Uri.fromFile(new File("/storage/emulated/0/DCIM/Camera/en_vidi_sample.jpg")));
            File file = new File("/storage/emulated/0/DCIM/Camera/en_vidi_sample.jpg");

            storedImage =BitmapFactory.decodeFile(file.getAbsolutePath(),null);

//        }
//        else{
//            GrantPermissions.requestPermissions(mainActivity);
//        }
    }

    @Test
    public void testTextRecognitionOn() {
        sharedViewModel.textRecognitionOn(storedImage);
        assertTrue(storedImage instanceof  Bitmap);
    }

    @Test
    public void onTextRecognized() {
    }

    @Test
    public void onLanguageIdentified() {
    }

    @Test
    public void onTextTranslated() {
    }
}