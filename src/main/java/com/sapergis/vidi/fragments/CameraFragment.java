package com.sapergis.vidi.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.sapergis.vidi.R;
import com.sapergis.vidi.helper.VDCamera;
import com.sapergis.vidi.helper.VDHelper;
import com.sapergis.vidi.interfaces.IVDAutoCapture;
import com.sapergis.vidi.viewmodels.SharedViewModel;

import java.util.Objects;

public class CameraFragment extends Fragment {
    private static final String HAS_AUTOCAPTURE_CALLBACKS = "hasAutoCaptureCallbacks";
    private SharedViewModel sharedViewModel;
    private VDCamera vdCamera;

    public CameraFragment() {
        // Required empty public constructor
    }

    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.isTTSOperationFinished().observe(this, isFinished -> {
            if (isFinished &&
                    getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                manageCamera(IVDAutoCapture.START);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_fragment, container, false);
        TextureView viewFinder = (TextureView) view.findViewById(R.id.view_finder);
        Button cameraButton = (Button) view.findViewById(R.id.cameraButton);
        vdCamera = new VDCamera(this, viewFinder, cameraButton);
        attachCameraTo(viewFinder);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        VDHelper.debugLog(getClass().getSimpleName(), getClass().getSimpleName()+" View Created");
        //TODO Correct the below behavior when changing orientations
        if(Objects.equals(sharedViewModel.isTTSOperationFinished().getValue(), Boolean.TRUE)){
            manageCamera(IVDAutoCapture.START);
        }
        //VDHelper.debugLog(this.getClass().getSimpleName(), "hasCallbacks : "+hasCallBacks);
        VDHelper.debugLog(this.getClass().getSimpleName(), "isTTSOperationFinished : "+
                sharedViewModel.isTTSOperationFinished().getValue());

    }

    private void manageCamera(String action) {
        if( action.equals(IVDAutoCapture.START) ){
            vdCamera.setAutoCapture(sharedViewModel.getHandler());
        }else if (action.equals(IVDAutoCapture.STOP) ){
        //    vdCamera.releaseAutoCapture();
        }
    }

    private void attachCameraTo(TextureView textureView) {
        textureView.post(vdCamera.getCamera());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        manageCamera(IVDAutoCapture.STOP);
        sharedViewModel.isTTSOperationFinished().removeObservers(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}