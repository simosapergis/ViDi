package com.sapergis.vidi.implementation;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.sapergis.vidi.helper.VDText;
import com.sapergis.vidi.interfaces.IVDTextOperations;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class VDCloudTTSTest extends TestCase {

    VDCloudTTS vdCloudTTS ;
    @Before
    public void setUp() throws Exception {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.sapergis.vidi", appContext.getPackageName());
        vdCloudTTS = new VDCloudTTS(appContext, getIVDTextOperations());
    }

    @Test
    public void runTextToSpeechOnCloudTest() {
            getIVDTextOperations().onTextToSpeechFinished();
    }

    @After
    public void ttsTest(){
        vdCloudTTS.runTextToSpeechOnCloud("Καλημέρα");
    }

    public IVDTextOperations getIVDTextOperations (){
        return new IVDTextOperations() {
            @Override
            public void textRecognitionOn(Bitmap bitmap) {

            }

            @Override
            public void onTextRecognized(String recognizedText) {

            }

            @Override
            public void onLanguageIdentified(String languageCode, String recognizedText) {

            }

            @Override
            public void onTextTranslated(VDText vdText) {

            }

            @Override
            public void onTextToSpeechFinished() {
                Log.i("VDCloudTTSTest", "TTS playback finished");
            }

            @Override
            public void onTextOperationTerminated() {

            }
        };
    }
}