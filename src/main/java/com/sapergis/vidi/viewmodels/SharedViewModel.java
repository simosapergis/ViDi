package com.sapergis.vidi.viewmodels;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.widget.Toast;

import com.sapergis.vidi.R;
import com.sapergis.vidi.helper.VDHelper;
import com.sapergis.vidi.helper.VDText;
import com.sapergis.vidi.implementation.VDCloudTTS;
import com.sapergis.vidi.implementation.VDLanguageIdentifier;
import com.sapergis.vidi.implementation.VDTextRecognizer;
import com.sapergis.vidi.implementation.VDDeviceTTS;
import com.sapergis.vidi.implementation.VDTextTranslator;
import com.sapergis.vidi.interfaces.IVDTextOperations;
import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SharedViewModel extends AndroidViewModel implements IVDTextOperations {
    private final String className = getClass().getSimpleName();
    private final MutableLiveData<Bitmap> captured = new MutableLiveData<Bitmap>();
    private final MutableLiveData<VDText> validRecognizedText = new MutableLiveData<VDText>();
    private final MutableLiveData<Boolean> hasInternetConnection = new MutableLiveData<Boolean>();
    private final MutableLiveData<Boolean> cloudTTSFinished = new MutableLiveData<Boolean>();
    private final VDCloudTTS vdCloudTTS;
    private final VDDeviceTTS vdDeviceTTS;
    private final Application application;
    private ConnectivityManager cm;
    private ConnectivityManager.NetworkCallback networkCallback;
    private NetworkRequest networkRequest;
    private boolean connected;

    public SharedViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        vdCloudTTS = new VDCloudTTS(application, this);
        vdDeviceTTS = new VDDeviceTTS(application);
        initConnectivityManager();
        registerCMCallback();
    }

    public void setBitmap (Bitmap bitmap){
        captured.setValue(bitmap);
        textRecognitionOn(bitmap);
    }

    public void setValidRecognizedText (VDText validText){
        validRecognizedText.setValue(validText);
    }

    public void setIsConnected (boolean isConnected){
        hasInternetConnection.setValue(isConnected);
    }

    public void setCloudTTSFinished(boolean isFinished){
        cloudTTSFinished.setValue(isFinished);
    }

    public MutableLiveData<Boolean> isCloudTTSFinished() {
        return cloudTTSFinished;
    }

    public LiveData<Boolean> isConnected (){
        return hasInternetConnection;
    }

    public LiveData<Bitmap> getCaptured(){
        return captured;
    }

    public LiveData<VDText> getValidRecognizedText(){
        return validRecognizedText;
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
            vdCloudTTS.runTextToSpeechOnCloud(vdText.getTranslatedText());
        }else {
            //Sends raw text since Greek language is not supported on device TTS
            //TODO Make it parameterizable
            vdDeviceTTS.speak(vdText.getRawText(), this);
        }
    }

    @Override
    public void onTextToSpeechFinished() {
         cloudTTSFinished.setValue(true);
    }

    @Override
    public void onTextOperationTerminated() {
        VDHelper.debugLog(getClass().getSimpleName(), application.getString(R.string.operation_terminated));
        cloudTTSFinished.setValue(true);
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

    public Network getCurrentNetwork (){
        return cm.getActiveNetwork();
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
}
