package com.sapergis.vidi.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.sapergis.vidi.R;
import com.sapergis.vidi.interfaces.IVDAutoCapture;
import com.sapergis.vidi.viewmodels.SharedViewModel;
import java.io.File;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

public class VDCamera implements IVDAutoCapture{
    private final String className = getClass().getSimpleName();
    private final Fragment fragment;
    private final TextureView viewFinder;
    private final Button captureButton;
    private final SharedViewModel sharedViewModel;
    private final Handler captureHandler = new Handler();
    private Runnable capture = null;
    private Runnable camera;
    private int degreesToRotate;
//    private float centerX;
//    private float centerY;
    private int count = 0;
    //private Handler handler = new Handler(Looper.getMainLooper());;

    public VDCamera (Fragment fragment, TextureView textureView, Button captureButton){
        this.fragment = fragment;
        this.viewFinder = textureView;
        this.captureButton = captureButton;
        this.sharedViewModel = new ViewModelProvider(fragment.requireActivity()).get(SharedViewModel.class);
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

                // Create configuration object for the image capture use case
                ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder()
                        .setTargetAspectRatio(new Rational(400, 580))
                        // We don't set a resolution for image capture; instead, we
                        // select a capture mode which will infer the appropriate
                        // resolution based on aspect ratio and requested mode
                        .setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
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



                // Build the image capture use case and attach button click listener
                ImageCapture imageCapture = new ImageCapture(imageCaptureConfig);
                captureButton.setOnClickListener(view -> {
                    try{
                        File file = new File(fragment.getActivity().getExternalMediaDirs()[0], System.currentTimeMillis() + ".jpg");
                        imageCapture.takePicture(file, new ImageCapture.OnImageSavedListener(){

                            @Override
                            public void onError(ImageCapture.UseCaseError error, String message,
                                                @Nullable Throwable exc) {
                                String msg = "Photo capture failed: " + message;
                                Toast.makeText(fragment.getContext(), msg, Toast.LENGTH_SHORT).show();
                                VDHelper.debugLog(className, msg);
                                if (exc != null) {
                                    exc.printStackTrace();
                                }
                            }

                            @Override
                            public void onImageSaved(File file) {
                                String msg = "Photo capture succeeded: " + file.getAbsolutePath();
                                Toast.makeText(fragment.getContext(), msg, Toast.LENGTH_SHORT).show();
                                VDHelper.debugLog(className, msg);
                                if(file.exists()){
                                    //listener.updateBitmap( BitmapFactory.decodeFile(file.getAbsolutePath()) );
                                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                    VDBitmap vdBitmap = new VDBitmap();
                                    vdBitmap.setBitmapImage(bitmap);
                                    vdBitmap.setDegreesToRotate(degreesToRotate);
                                    sharedViewModel.setBitmap(vdBitmap);
                                    //Delete the picture right after text identification finished
                                    boolean imageDeleted =  file.delete();
                                    VDHelper.debugLog(className, fragment.getString(R.string.image_deleted) + imageDeleted);

                                    //                                previewImage.setImageBitmap(rotateBitmap(imageBitmap, 90));
                                }
                            }
                        });
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }

                });

                // Setup image analysis pipeline that computes average pixel luminance
                HandlerThread analyzerThread = new HandlerThread("LuminosityAnalysis");
                analyzerThread.start();
                ImageAnalysisConfig analyzerConfig =
                        new ImageAnalysisConfig.Builder()
                                .setCallbackHandler(new Handler(analyzerThread.getLooper()))
                                // In our analysis, we care more about the latest image than
                                // analyzing *every* image
                                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                                .build();

                ImageAnalysis analyzerUseCase = new ImageAnalysis(analyzerConfig);
                analyzerUseCase.setAnalyzer(new VDImageAnalyzer());
                CameraX.bindToLifecycle((LifecycleOwner) fragment,preview, imageCapture, analyzerUseCase);
            }

        };
    }

    private void updateTransform() {
        android.graphics.Matrix matrix = new Matrix();

        float centerX = viewFinder.getWidth() / 2f;
        float centerY = viewFinder.getHeight() / 2f;
        float rotationDegrees;
        // Correct preview output to account for display rotation
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

    @Override
    public void setAutoCapture(Handler handler) {
        capture = captureButton::performClick;
        if(!handler.hasCallbacks(capture)){
            handler.postDelayed(capture, 5000);
            VDHelper.debugLog(getClass().getSimpleName(), "Handler instantiated..");
        }else{
            VDHelper.debugLog(this.getClass().getSimpleName(), "Handler hasCallbacks ");
        }

    }

    @Override
    public void releaseAutoCapture() {
        captureHandler.removeCallbacks(capture);
        //captureHandler = null;
        count = 0;
        VDHelper.debugLog(className, fragment.getString(R.string.autocapture_stopped));
    }


    private class VDImageAnalyzer implements ImageAnalysis.Analyzer{
        @Override
        public void analyze(ImageProxy image, int rotationDegrees) {
            degreesToRotate = rotationDegrees;
        }
    }
}
