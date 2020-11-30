package com.sapergis.vidi.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import com.google.protobuf.ByteString;
import com.sapergis.vidi.R;
import com.sapergis.vidi.helper.VDHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class VDAudioService extends Service {
    private ServiceHandler serviceHandler;
    private IBinder vdBinder = new VDBinder();

    @Override
    public void onCreate() {
        HandlerThread handlerThread = new HandlerThread("VDServiceStartArguments");
        handlerThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        Looper serviceLooper = handlerThread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message message = serviceHandler.obtainMessage();
        message.arg1 = startId;
        message.setData(intent.getExtras());
        serviceHandler.sendMessage(message);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        Message message = serviceHandler.obtainMessage();
//        message.arg1 = startId;
//        message.setData(intent.getExtras());
//        serviceHandler.sendMessage(message);
        return vdBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    private final class ServiceHandler extends Handler{

        public ServiceHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            VDHelper.debugLog(this.getClass().getSimpleName(), getApplication().getString(R.string.handling_message));
            Bundle bundle  = msg.getData();
            byte [] ttsAudioBytes = bundle.getByteArray(VDHelper.TTS_AUDIO_BYTES);
            try{
                ByteString audioContents = ByteString.copyFrom(ttsAudioBytes);
                File mp3File = new File(VDHelper.MP3FILEPATH);
                try (OutputStream outputStream = new FileOutputStream(mp3File)) {
                    outputStream.write(audioContents.toByteArray());
                }
                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.fromFile(mp3File));
                VDHelper.debugLog(getClass().getSimpleName(), getApplicationContext().getString(R.string.service_tts_speaking));
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener( mp -> {
                            //ivdTextOperations.onCloudTTSFinished(response);
                            mp.reset();
                            mp.release();
                            mp = null;
                            VDHelper.debugLog(getClass().getSimpleName(), getApplicationContext().getString(R.string.service_tts_finished));
                        }
                );
                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    VDHelper.debugLog(getClass().getSimpleName(),
                            "Mediaplayer failed. Details:\n" +
                                    "mp :" + mp + "\n" +
                                    "what : " + what + "\n" +
                                    " extra : " + extra);
                    return false;
                });
            }catch (IOException ex){
                Log.e(getClass().getSimpleName(),
                        "IOException Thrown. Details-> "+ex.getMessage());
            }
            //arg1 is the id of the specific service
            stopSelf(msg.arg1);
           

        }
    }

    public class VDBinder extends Binder {

        VDAudioService getService(){
            return VDAudioService.this;
        }
    }

    public interface IVDBinder extends IBinder{

    }

}
