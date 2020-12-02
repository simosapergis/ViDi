package com.sapergis.vidi.viewmodels;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.protobuf.ByteString;
import com.sapergis.vidi.R;
import com.sapergis.vidi.fragments.CapturedImageFragment;
import com.sapergis.vidi.helper.VDApplication;
import com.sapergis.vidi.helper.VDBitmap;
import com.sapergis.vidi.helper.VDHelper;
import com.sapergis.vidi.helper.VDText;
import com.sapergis.vidi.helper.VDThread;
import com.sapergis.vidi.implementation.VDCloudTTS;
import com.sapergis.vidi.implementation.VDLanguageIdentifier;
import com.sapergis.vidi.implementation.VDTextRecognizer;
import com.sapergis.vidi.implementation.VDDeviceTTS;
import com.sapergis.vidi.implementation.VDTextTranslator;
import com.sapergis.vidi.interfaces.IVDTextOperations;
import com.sapergis.vidi.services.VDAudioService;

import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SharedViewModel extends AndroidViewModel implements IVDTextOperations, ServiceConnection {
    private final String className = getClass().getSimpleName();
    private final MutableLiveData<VDBitmap> captured = new MutableLiveData<>();
    private final MutableLiveData<VDText> validRecognizedText = new MutableLiveData<>();
    private final MutableLiveData<Boolean> hasInternetConnection = new MutableLiveData<>();
    private final MutableLiveData<byte[]> googleTTsResponse = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationFinished;
    private final VDCloudTTS vdCloudTTS;
    private final VDDeviceTTS vdDeviceTTS;
    private final VDApplication application;
    private ConnectivityManager cm;
    private ConnectivityManager.NetworkCallback networkCallback;
    private NetworkRequest networkRequest;
    private boolean connected;
    private boolean isBound;
    private Thread thread;
    public IBinder service ;
    Intent serviceIntent;

    public SharedViewModel(@NonNull Application application) {
        super(application);
        this.application = (VDApplication)application;
        vdCloudTTS = new VDCloudTTS(application, this);
        vdDeviceTTS = new VDDeviceTTS(application);
        initConnectivityManager();
        registerCMCallback();
        operationFinished = new MutableLiveData<>(Boolean.valueOf(true));
    }

    public void setBitmap (VDBitmap vdBitmap){
        setOperationFinished(false);
        captured.setValue(vdBitmap);
        textRecognitionOn(vdBitmap.getBitmapImage());
    }

    public void setValidRecognizedText (VDText validText){
        validRecognizedText.setValue(validText);
    }

    public void setIsConnected (boolean isConnected){
        hasInternetConnection.setValue(isConnected);
    }

    public void setOperationFinished(boolean isFinished){
        operationFinished.postValue(isFinished);
    }

    public MutableLiveData<Boolean> isTTSOperationFinished() {
        return operationFinished;
    }

    public LiveData<Boolean> isConnected (){
        return hasInternetConnection;
    }

    public LiveData<VDBitmap> getCaptured(){
        return captured;
    }

    public LiveData<VDText> getValidRecognizedText(){
        return validRecognizedText;
    }

    public MutableLiveData<byte[]> getGoogleTTsResponse() {
        return googleTTsResponse;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean getConnected(){
        return connected;
    }

    @Override
    public void textRecognitionOn(Bitmap bitmap) {
        VDHelper.debugLog(getClass().getSimpleName(), "Text recognition started...");
        VDTextRecognizer.runDeviceTextRecognition(bitmap, this);
        //VDTextRecognizer.runCloudTextRecognition(bitmap,this);
    }

    @Override
    public void onTextRecognized(String recognizedText) {
        VDLanguageIdentifier.identify(recognizedText, this);
    }

    @Override
    public void onLanguageIdentified(String languageCode, String recognizedText) {
        VDHelper.debugLog(getClass().getSimpleName(), "Identified language code is ["+languageCode+"]");
        VDText vdText = new VDText();
        vdText.setRawText(recognizedText);
        vdText.setIdentifiedLanguage(languageCode);
        setValidRecognizedText(vdText);
        VDTextTranslator.initiateDeviceTranslation(vdText, this);
    }

    @Override
    public void onTextTranslated(VDText vdText) {
        if(connected) {
            //vdCloudTTS.runTextToSpeechOnCloud(vdText.getTranslatedText());
            VDThread vdThread = new VDThread(vdCloudTTS, vdText.getTranslatedText());
            thread = new Thread(vdThread);
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.setUncaughtExceptionHandler((t, e) -> {
                VDHelper.debugLog(getClass().getSimpleName(), "Exception caught for" +
                        t + " message: "+e.getMessage());
            });
            thread.start();
        }else {
            //Sends raw text since Greek language is not supported on device TTS
            //TODO Make it parameterizable
            vdDeviceTTS.speak(vdText.getRawText(), this);
        }
    }


    @Override
    public void onCloudTTSFinished(SynthesizeSpeechResponse response) {
         //setOperationFinished(true);
        if(response !=null ){
            ByteString byteString = response.toByteString();
            byte[] byteArray =  byteString.toByteArray();
           // googleTTsResponse.postValue(byteArray);
            startAudioService(byteArray);
        }else{
            VDHelper.debugLog(getClass().getSimpleName(),
                    application.getString(R.string.google_tts_resp_null));
        }


    }

    @Override
    public void onDeviceTTSFinished() {
        setOperationFinished(true);
    }

    @Override
    public void onSpeechServiceFinished() {
        setOperationFinished(true);
    }

    @Override
    public void onOperationTerminated(String message) {
        VDHelper.debugLog(getClass().getSimpleName(), application.getString(R.string.operation_terminated) +" "+message);
        setOperationFinished(true);
    }

    public IVDTextOperations getCallbackInstance(){
        return this;
    }

    private void initConnectivityManager(){
        cm = (ConnectivityManager) Objects.requireNonNull(application.getApplicationContext())
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();
        networkCallback = new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                updateNetworkStatus(true);
                VDHelper.debugLog(className, application.getString(R.string.internet_conn_available));
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();
                updateNetworkStatus(false);
                VDHelper.debugLog(className, application.getString(R.string.internet_conn_unavailable));
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                updateNetworkStatus(false);
                VDHelper.debugLog(className, application.getString(R.string.internet_conn_lost));
                notifyConnectionLost();
            }
        };

    }

    public Handler getHandler(){
        return application.mainThreadHandler;
    }

    public Network getCurrentNetwork (){
        return cm.getActiveNetwork();
    }


    private void startAudioService(byte [] ttsAudioBytes){
            VDHelper.debugLog(this.getClass().getSimpleName(),
                    application.getString(R.string.starting_audio_service));
            serviceIntent = new Intent(application, VDAudioService.class);
            Bundle bundle = new Bundle();
            bundle.putByteArray(VDHelper.TTS_AUDIO_BYTES, ttsAudioBytes);
            MessageHandler messageHandler = new MessageHandler(Looper.getMainLooper());
            Messenger messenger = new Messenger(messageHandler);
            bundle.putParcelable("serviceMessenger", messenger);
            serviceIntent.putExtras(bundle);
            //application.startService(serviceIntent);
           isBound =  application.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);

    }

    public Intent getServiceIntent(){
        return serviceIntent;
    }

    private void finishService(){
        application.unbindService(this);
        application.stopService(serviceIntent);
        isBound = false;
    }
    /**
    This is getting handled by the activity/fragment
     */
    public void registerCMCallback(){
        cm.registerNetworkCallback(networkRequest,networkCallback);
    }


    /**
     This is getting handled by the activity/fragment
     */
    public void unRegisterCMCallback(){
        cm.unregisterNetworkCallback(networkCallback);
    }

    private void updateNetworkStatus(boolean isConnected) {
        connected = isConnected;
        hasInternetConnection.postValue(isConnected);

    }

    public void notifyConnectionEstablished(){
        Toast.makeText(application, application.getString(R.string.connection_restablished),
                Toast.LENGTH_LONG ).show();
    }

    public void notifyConnectionLost(){
        Toast.makeText(application, application.getString(R.string.connection_lost),
                Toast.LENGTH_LONG ).show();
    }

    public void notifyCheckConnection(){
        Toast.makeText(application, application.getString(R.string.connection_check),
                Toast.LENGTH_LONG ).show();
    }

    @Override
    public void onBindingDied(ComponentName name) {
        ComponentName n = name;
    }

    @Override
    public void onNullBinding(ComponentName name) {
        ComponentName n = name;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
            this.service = service;
        try {
            service.linkToDeath(getDethRecipient(), 1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        ComponentName n = name;
    }

    private class MessageHandler extends Handler{
        public MessageHandler(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            onSpeechServiceFinished();
            if(isBound){
                finishService();
            }
        }
    }

    private IBinder.DeathRecipient getDethRecipient (){
        return new IBinder.DeathRecipient() {
            @Override
            public void binderDied() {
                int x =1;
            }
        };
    }
}
