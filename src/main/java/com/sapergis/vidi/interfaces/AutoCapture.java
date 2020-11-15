package com.sapergis.vidi.interfaces;

import android.widget.Button;

public interface AutoCapture {
    public static int DEFAULT_INTERVAL = 5000;
    public static int CAPTURE_REPETITIONS = 100;
    void setAutoCapture(int interval, int captureRepetitions);
    void releaseAutoCapture();
}
