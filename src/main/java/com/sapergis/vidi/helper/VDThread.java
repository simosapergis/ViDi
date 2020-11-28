package com.sapergis.vidi.helper;

import com.sapergis.vidi.implementation.VDCloudTTS;
import com.sapergis.vidi.interfaces.IVDTextOperations;

public class VDThread implements Runnable {
    String vdText;
    VDCloudTTS vdCloudTTS;

    public VDThread(VDCloudTTS cloudTts, String text){
        this.vdText = text;
        this.vdCloudTTS = cloudTts;
    }

    @Override
    public void run() {
        vdCloudTTS.runTextToSpeechOnCloud(vdText);
    }
}
