package com.sapergis.vidi.fragments;

import android.os.Bundle;

import androidx.camera.core.CameraX;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sapergis.vidi.GrantPermissions;
import com.sapergis.vidi.R;
import com.sapergis.vidi.helper.VDCamera;
import com.sapergis.vidi.viewmodels.CameraViewModel;
import com.sapergis.vidi.viewmodels.SharedViewModel;

public class CameraFragment extends Fragment {
    private TextureView viewFinder;
    private Button cameraButton;
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
        viewFinder = (TextureView)view.findViewById(R.id.view_finder);
        cameraButton = (Button) view.findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO take a shot
            }
        });
        vdCamera = new VDCamera(this, viewFinder);
        attachCameraTo(viewFinder);
        return view;
    }

    private void attachCameraTo (TextureView textureView){
        textureView.post(vdCamera.getCamera());
    }
}