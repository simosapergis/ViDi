package com.sapergis.vidi.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.sapergis.vidi.R;
import com.sapergis.vidi.helper.VDCamera;
import com.sapergis.vidi.interfaces.IVDAutoCapture;
import com.sapergis.vidi.viewmodels.SharedViewModel;

public class CameraFragment extends Fragment {
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View view = inflater.inflate(R.layout.camera_fragment, container, false);
        TextureView viewFinder = (TextureView) view.findViewById(R.id.view_finder);
        Button cameraButton = (Button) view.findViewById(R.id.cameraButton);
        vdCamera = new VDCamera(this, viewFinder, cameraButton);
        attachCameraTo(viewFinder);
        //TODO Correct the below behavior when changing orientations
        //TODO fix capture repetitions
        //vdCamera.setAutoCapture(IVDAutoCapture.DEFAULT_INTERVAL, 2);
        sharedViewModel.isConnected().observe(getViewLifecycleOwner(), isConnected ->
            manageCamera(isConnected)
        );
        sharedViewModel.getValidRecognizedText().observe(getViewLifecycleOwner(), vdText ->
                manageCamera(Boolean.FALSE)
        );
        return view;
    }

    private void manageCamera(Boolean bool) {
        if(bool) {
            vdCamera.setAutoCapture(IVDAutoCapture.DEFAULT_INTERVAL, 5);
        }else{
            vdCamera.releaseAutoCapture();
        }
    }

    private void attachCameraTo(TextureView textureView) {
        textureView.post(vdCamera.getCamera());
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedViewModel.registerCMCallback();
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedViewModel.unRegisterCMCallback();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vdCamera.releaseAutoCapture();
        sharedViewModel.getValidRecognizedText().removeObservers(this);
    }


}