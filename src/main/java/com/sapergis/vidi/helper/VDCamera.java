package com.sapergis.vidi.helper;

import android.content.Context;
import android.graphics.Matrix;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.api.internal.StatusExceptionMapper;

import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

public class VDCamera {
    private Runnable camera;
    Fragment fragment;
    TextureView viewFinder;
    Button b;

    public VDCamera (Fragment fragment, TextureView textureView){
        this.fragment = fragment;
        this.viewFinder = textureView;
        initiateCamera();
    }

    private void initiateCamera(){
        camera = new Runnable() {
            @Override
            public void run() {
                PreviewConfig previewConfig = new PreviewConfig.Builder()
                        .setLensFacing(CameraX.LensFacing.BACK)
                        .setTargetAspectRatio(new Rational(400, 580))
                        .setTargetResolution(new Size(400, 580))
                        .build();

                Preview preview = new Preview(previewConfig);
                preview.setOnPreviewOutputUpdateListener(
                        previewOutput -> {
                            // To update the SurfaceTexture, we have to remove it and re-add it
                            ViewGroup parent = (ViewGroup) viewFinder.getParent();
                            parent.removeView(viewFinder);
                            parent.addView(viewFinder, 0);

                            viewFinder.setSurfaceTexture(previewOutput.getSurfaceTexture());
                            updateTransform();
                        });
                CameraX.bindToLifecycle((LifecycleOwner) fragment,preview);
            }
        };
    }

    private void updateTransform() {
        android.graphics.Matrix matrix = new Matrix();

        float centerX = viewFinder.getWidth() / 2f;
        float centerY = viewFinder.getHeight() / 2f;

        // Correct preview output to account for display rotation
        float rotationDegrees;
        switch (viewFinder.getDisplay().getRotation()) {
            case Surface.ROTATION_0:
                rotationDegrees = 0f;
                break;
            case Surface.ROTATION_90:
                rotationDegrees = 90f;
                break;
            case Surface.ROTATION_180:
                rotationDegrees = 180f;
                break;
            case Surface.ROTATION_270:
                rotationDegrees = 270f;
                break;
            default:
                return;
        }

        matrix.postRotate(-rotationDegrees, centerX, centerY);

        // Finally, apply transformations to our TextureView
        viewFinder.setTransform(matrix);
    }
    public Runnable getCamera (){
        return camera;
    }
}
