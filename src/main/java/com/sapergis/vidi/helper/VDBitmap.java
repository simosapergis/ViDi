package com.sapergis.vidi.helper;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class VDBitmap  {
    private Bitmap bitmapImage = null;
    private int degreesToRotate;

    public Bitmap getBitmapImage() {
        return bitmapImage;
    }

    public void setBitmapImage(Bitmap bitmapImage) {
        this.bitmapImage = bitmapImage;
    }

    public float getDegreesToRotate() {
        return degreesToRotate;
    }

    public void setDegreesToRotate(int degreesToRotate) {
        this.degreesToRotate = degreesToRotate;
    }


    public Bitmap rotateBitmap(){
        Matrix matrix = new Matrix();
        matrix.postRotate(degreesToRotate);
        return Bitmap.createBitmap(bitmapImage, 0, 0, bitmapImage.getWidth(),
                bitmapImage.getHeight(), matrix, true);
    }

}
