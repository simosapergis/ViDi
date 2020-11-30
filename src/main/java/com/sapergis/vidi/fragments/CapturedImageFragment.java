package com.sapergis.vidi.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import android.os.IBinder;
import android.os.IInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.sapergis.vidi.R;
import com.sapergis.vidi.helper.VDHelper;
import com.sapergis.vidi.services.VDAudioService;
import com.sapergis.vidi.viewmodels.SharedViewModel;

import java.util.Objects;


public class CapturedImageFragment extends Fragment implements ServiceConnection {
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
        sharedViewModel.getCaptured().observe(this, vdBitmap ->
                previewImage.setImageBitmap(vdBitmap.rotateBitmap())
        );
        sharedViewModel.getGoogleTTsResponse().observe(this, this::startAudioService);
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
        sharedViewModel.getGoogleTTsResponse().removeObservers(getViewLifecycleOwner());
        //sharedViewModel.getValidRecognizedText().removeObservers(getViewLifecycleOwner());
        //vdDeviceTTS.shutDown();
    }

    private void startAudioService(byte [] ttsAudioBytes){
        if(getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED){
            VDHelper.debugLog(this.getClass().getSimpleName(), getString(R.string.starting_audio_service));
            Intent serviceIntent = new Intent(getActivity(), VDAudioService.class);
            Bundle bundle = new Bundle();
            bundle.putByteArray(VDHelper.TTS_AUDIO_BYTES, ttsAudioBytes);
            serviceIntent.putExtras(bundle);
            Objects.requireNonNull(getActivity()).
                    startService(serviceIntent);
            Objects.requireNonNull(getActivity()).
                    bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
        }


    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
            IBinder vdBinder = service ;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        int x = 2;
    }

    @Override
    public void onBindingDied(ComponentName name) {
        int x = 5;
    }

    @Override
    public void onNullBinding(ComponentName name) {
        int x = 3;
    }
}