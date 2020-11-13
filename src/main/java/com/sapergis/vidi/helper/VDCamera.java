package com.sapergis.vidi.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.sapergis.vidi.viewmodels.SharedViewModel;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

public class VDCamera {
    private Runnable camera;
    private Fragment fragment;
    private TextureView viewFinder;
    private Button captureButton;
    //private UpdateViewObjectsListener listener;
    private static final String TAG = "VDCamera ->";
    private SharedViewModel sharedViewModel;

//    Listener will not be used since we have used the ViewModel to update data
//    public interface UpdateViewObjectsListener{
//        void updateBitmap(Bitmap bitmap);
//        void updatePreviewImage(ImageView imageView);
//    }

    public VDCamera (Fragment fragment, TextureView textureView, Button captureButton){
        this.fragment = fragment;
        this.viewFinder = textureView;
        this.captureButton = captureButton;
        sharedViewModel = new ViewModelProvider(fragment.requireActivity()).get(SharedViewModel.class);
        //setListener();
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
                // Create configuration object for the image capture use case
                ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder()
                        .setTargetAspectRatio(new Rational(400, 580))
                        // We don't set a resolution for image capture; instead, we
                        // select a capture mode which will infer the appropriate
                        // resolution based on aspect ratio and requested mode
                        .setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                        .build();


                // Build the image capture use case and attach button click listener
                ImageCapture imageCapture = new ImageCapture(imageCaptureConfig);
                captureButton.setOnClickListener(view -> {
                    File file = new File(fragment.getActivity().getExternalMediaDirs()[0], System.currentTimeMillis() + ".jpg");
                    imageCapture.takePicture(file, new ImageCapture.OnImageSavedListener(){

                        @Override
                        public void onError(ImageCapture.UseCaseError error, String message,
                                            @Nullable Throwable exc) {
                            String msg = "Photo capture failed: " + message;
                            Toast.makeText(fragment.getContext(), msg, Toast.LENGTH_SHORT).show();
                            Log.e(TAG, msg);
                            if (exc != null) {
                                exc.printStackTrace();
                            }
                        }

                        @Override
                        public void onImageSaved(File file) {
                            String msg = "Photo capture succeeded: " + file.getAbsolutePath();
                            Toast.makeText(fragment.getContext(), msg, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, msg);

                            if(file.exists()){
                                //listener.updateBitmap( BitmapFactory.decodeFile(file.getAbsolutePath()) );
                                sharedViewModel.setBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
//                                previewImage.setImageBitmap(rotateBitmap(imageBitmap, 90));
                            }
                        }
                    });
                });
                CameraX.bindToLifecycle((LifecycleOwner) fragment,preview, imageCapture);
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

//    private void setListener(){
//        if(fragment instanceof UpdateViewObjectsListener){
//            listener = (UpdateViewObjectsListener) fragment;
//        }else{
//            throw new RuntimeException(fragment.toString()
//                    + " must implement UpdateViewObjectsListener");
//        }
//    }

    public Runnable getCamera (){
        return camera;
    }
}
