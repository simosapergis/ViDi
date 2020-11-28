package com.sapergis.vidi.helper;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class VDBitmap  {
    private Bitmap bitmapImage = null;
    private float rotationDegrees = 0.0f;
    private float width;
    private float height;
    private static final float ANGLE = 90.0f;

    public Bitmap getBitmapImage() {
        return bitmapImage;
    }

    public void setBitmapImage(Bitmap bitmapImage) {
        this.bitmapImage = bitmapImage;
    }

    public float getRotationDegrees() {
        return rotationDegrees;
    }

    public void setRotationDegrees(float rotationDegrees) {
        this.rotationDegrees = rotationDegrees;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Bitmap rotateBitmap(){
        Matrix matrix = new Matrix();
        matrix.postRotate(this.rotationDegrees);
        matrix.postRotate(0);
        return Bitmap.createBitmap(bitmapImage, 0, 0, bitmapImage.getWidth(),
                bitmapImage.getHeight(), matrix, true);
    }

}
