package com.sapergis.vidi.helper;

import android.util.Log;

import java.util.HashMap;

public class VDHelper {

    public static String isConnected;

    /**

     */
    public static final String TAG = "ViDi =>";

    /**

     */
    public static final String [] LOCALES = {"en","el"};

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
