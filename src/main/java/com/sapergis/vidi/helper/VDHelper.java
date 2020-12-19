package com.sapergis.vidi.helper;

import android.util.Log;

public class VDHelper {

    /**

     */
    public static final String TAG = "ViDi =>";

    /**

     */
    public static final String UTILITY_CLASS = "Utility class";
    /**

     */
    public static final String [] LOCALES = {"en","el"};

    /**
     * Path of the MP3 speech file that will be stored temporary in the device.
     * This file is the result from calling Google Text-To-Speech API
     */

    public static final String MP3FILEPATH = "/storage/emulated/0/DCIM/tts.mp3";

    /**
     *
     */
    public static final String GOOGLE_SERVICE_PROP_PATH = "/storage/emulated/0/DCIM/Credentials/Vidi-0d69bdea8de7.json";

    /**
     *
     */
    public static final String TTS_AUDIO_BYTES = "ttsAudioBytes";

    /**
     *
     * @param className The name of the class that calls the debug log, for extra logging info
     * @param message The message to be logged
     */

    public static void debugLog (String className, String message){
        Log.d(TAG + " - "+className + " : ", message );
    }

    /**

     */
    public class GoogleTTSLanguages{
        public static final String GREEK = "el-GR";
        public static final String ENGLISH = "en-US";

    }
}
