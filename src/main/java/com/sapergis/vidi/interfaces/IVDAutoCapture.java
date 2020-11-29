package com.sapergis.vidi.interfaces;

import android.os.Handler;

public interface IVDAutoCapture {
    String START = "START";
    String STOP = "STOP";
    int DEFAULT_INTERVAL = 5000;
    int CAPTURE_REPETITIONS = 100;
    void setAutoCapture(Handler handler);
    void releaseAutoCapture();
}
