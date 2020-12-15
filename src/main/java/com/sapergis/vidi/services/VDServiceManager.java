package com.sapergis.vidi.services;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import com.sapergis.vidi.R;
import com.sapergis.vidi.helper.VDHelper;
import com.sapergis.vidi.interfaces.IVDTextOperations;

import androidx.annotation.NonNull;

public class VDServiceManager implements ServiceConnection {
    private final Application application;
    private final IVDTextOperations ivdTextOperations;
    private Intent serviceIntent;
    private boolean isBound;

    public VDServiceManager(Application application, IVDTextOperations ivdTextOperations){
        this.application = application;
        this.ivdTextOperations = ivdTextOperations;
    }

    public void startAudioService(byte[] ttsAudioBytes){
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

    private void finishAudioService(){
        application.unbindService(this);
        application.stopService(serviceIntent);
        isBound = false;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    public void onBindingDied(ComponentName name) {

    }

    @Override
    public void onNullBinding(ComponentName name) {

    }

    private class MessageHandler extends Handler {
        public MessageHandler(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            ivdTextOperations.onSpeechServiceFinished();
            if(isBound){
                finishAudioService();
            }
        }
    }
}
