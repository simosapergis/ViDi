package com.sapergis.vidi.helper;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class VDBitmap  {

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

}
