package com.sapergis.vidi.interfaces;

public interface IVDAutoCapture {
    public static int DEFAULT_INTERVAL = 5000;
    public static int CAPTURE_REPETITIONS = 100;
    void setAutoCapture(int interval, int captureRepetitions);
    void releaseAutoCapture();
}
