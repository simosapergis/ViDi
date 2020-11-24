package com.sapergis.vidi.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.sapergis.vidi.R;
import com.sapergis.vidi.helper.VDBitmap;
import com.sapergis.vidi.viewmodels.SharedViewModel;


public class CapturedImageFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private ImageView previewImage;;
    //private Observer<VDText> vdTextObserver = vdText -> VDTextToSpeech.runTextToSpeechOnCloud( vdText.getTranslatedText(), this.);

    public CapturedImageFragment() {
        // Required empty public constructor
    }

    public static CapturedImageFragment newInstance(String param1, String param2) {
        CapturedImageFragment fragment = new CapturedImageFragment();
        Bundle args = new Bundle();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getCaptured().observe(this, bitmap ->
                previewImage.setImageBitmap(VDBitmap.rotateBitmap(bitmap,90))
        );

//        sharedViewModel.getValidRecognizedText().removeObserver(vdTextObserver);
//        sharedViewModel.getValidRecognizedText().observe(this, vdTextObserver);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.capture_fragment, container, false);
        previewImage = (ImageView) view.findViewById(R.id.previewImage);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sharedViewModel.getCaptured().removeObservers(getViewLifecycleOwner());
        //sharedViewModel.getValidRecognizedText().removeObservers(getViewLifecycleOwner());
        //vdDeviceTTS.shutDown();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}