package com.sapergis.vidi.implementation;


import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;
import com.sapergis.vidi.GrantPermissions;
import com.sapergis.vidi.helper.VDHelper;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

public class VDTextToSpeech {

    private TextToSpeech textToSpeech;
    private final Context context;

    public VDTextToSpeech(Context context) {
        this.context = context;
        initialize();
    }

    private void initialize (){
        Log.d(VDHelper.TAG, "TextToSpeech on Device ->> Initiating...");
        textToSpeech = new TextToSpeech(context, status -> {
            if ( status == TextToSpeech.SUCCESS){
                textToSpeech.setLanguage(Locale.ENGLISH);
                Log.d(VDHelper.TAG, "TextToSpeech on Device ->> Initiated :: Language ->> "+ Locale.ENGLISH);
            }
        });
    }

    public void speak (String text){
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null,null);
    }

    public void shutDown (){
        textToSpeech.shutdown();
        Log.d("TextToSpeech", "->> was ShutDown");
    }

    public  void test(String text){

        try {
            TextToSpeechSettings textToSpeechSettings = null;
            Log.d(VDHelper.TAG, "Initiating TTS.. ");
            textToSpeechSettings = TextToSpeechSettings.newBuilder().setCredentialsProvider(
                    FixedCredentialsProvider.create(ServiceAccountCredentials.fromStream(
                            //TODO : SET up google cloud credentials out of device storage
                            new FileInputStream("/storage/emulated/0/DCIM/Credentials/Vidi-0d69bdea8de7.json"))
                                        ))
                                .build();
            TextToSpeechClient textToSpeechClient = TextToSpeechClient.create(textToSpeechSettings);
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(VDHelper.GoogleTTSLanguages.GREEK)
                    .setSsmlGender(SsmlVoiceGender.FEMALE)
                    .build();
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();
            Log.d(VDHelper.TAG, "Sending TTS request to Google Cloud.. ");
            SynthesizeSpeechResponse response = textToSpeechClient
                    .synthesizeSpeech(input, voice, audioConfig);
            Log.d(VDHelper.TAG, "TTS response received from Google Cloud");
            ByteString audioContents = response.getAudioContent();
            //TODO : DO save the mp3 in a more generic way
            File mp3File = new File("/storage/emulated/0/DCIM/TTS/tts.mp3");
            try(OutputStream outputStream = new FileOutputStream(mp3File)){
                outputStream.write(audioContents.toByteArray());
                MediaPlayer mediaPlayer = MediaPlayer.create(context, Uri.fromFile(mp3File));
                mediaPlayer.start();
//                FileDescriptor fd = context.getContentResolver().openFileDescriptor()
//
//                mediaPlayer.setDataSource();
            }

        }catch(Exception ex){
            ex.printStackTrace();

        }
    }
}
