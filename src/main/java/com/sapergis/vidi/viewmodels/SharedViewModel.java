package com.sapergis.vidi.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Handler;
import android.widget.Toast;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.protobuf.ByteString;
import com.sapergis.vidi.R;
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
import com.sapergis.vidi.services.VDServiceManager;

import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

public class SharedViewModel extends AndroidViewModel implements IVDTextOperations{
    private final String className = getClass().getSimpleName();
    private final MutableLiveData<VDBitmap> captured = new MutableLiveData<>();
    private final MutableLiveData<VDText> validRecognizedText = new MutableLiveData<>();
    private final MutableLiveData<Boolean> hasInternetConnection = new MutableLiveData<>();
    private final MutableLiveData<byte[]> googleTTsResponse = new MutableLiveData<>();
    private final VDApplication application;
    private MutableLiveData<Boolean> operationFinished;
    private VDCloudTTS vdCloudTTS;
    private VDDeviceTTS vdDeviceTTS;
    private ConnectivityManager cm;
    private ConnectivityManager.NetworkCallback networkCallback;
    private NetworkRequest networkRequest;
    private VDServiceManager vdServiceManager;
    private boolean connected;

    public SharedViewModel(@NonNull Application application) {
        super(application);
        this.application = (VDApplication)application;
        initialize();
    }

    private void initialize(){
        vdCloudTTS = new VDCloudTTS(application, this);
        vdDeviceTTS = new VDDeviceTTS(application);
        operationFinished = new MutableLiveData<>(Boolean.valueOf(true));
        vdServiceManager = new VDServiceManager(application, this);
        initConnectivityManager();
        registerCMCallback();
    }

    public void setBitmap (VDBitmap vdBitmap){
        setOperationFinished(false);
        captured.setValue(vdBitmap);
        textRecognitionOn(vdBitmap.getBitmapImage());
    }

    public void setValidRecognizedText (VDText validText){
        validRecognizedText.setValue(validText);
    }

    public void setOperationFinished(boolean isFinished){
        operationFinished.postValue(isFinished);
    }

    public MutableLiveData<Boolean> isTTSOperationFinished() {
        return operationFinished;
    }

    public LiveData<VDBitmap> getCaptured(){
        return captured;
    }

    public MutableLiveData<byte[]> getGoogleTTsResponse() {
        return googleTTsResponse;
    }

    public Handler getHandler(){
        return application.mainThreadHandler;
    }

    @Override
    public void textRecognitionOn(Bitmap bitmap) {
        VDHelper.debugLog(getClass().getSimpleName(), "Text recognition started...");
        VDTextRecognizer.runDeviceTextRecognition(bitmap, this);
        //VDTextRecognizer.runCloudTextRecognition(bitmap,this);
    }

    @Override
    public void onTextRecognized(String recognizedText) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(application);
        String inputLanguage = preferences.getString( application.getString(R.string.input_lang_key),
                application.getString(R.string.en));
        VDLanguageIdentifier.identify(recognizedText, this, inputLanguage);
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
            Thread thread = new Thread(vdThread);
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
        if(response !=null ){
            ByteString byteString = response.toByteString();
            byte[] byteArray =  byteString.toByteArray();
            vdServiceManager.startAudioService(byteArray);
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
        showToast(message);
        setOperationFinished(true);
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
        showToast( application.getString(R.string.connection_restablished) );
    }

    public void notifyConnectionLost(){
        showToast( application.getString(R.string.connection_lost) );
    }

    public void notifyCheckConnection(){
        showToast( application.getString(R.string.connection_check) );
    }

    private void showToast(String message){
        Toast.makeText(application, message,
                Toast.LENGTH_LONG ).show();
    }

}
