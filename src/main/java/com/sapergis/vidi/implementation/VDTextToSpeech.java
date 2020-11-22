package com.sapergis.vidi.implementation;


import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import com.google.api.gax.core.FixedCredentialsProvider;
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
import com.sapergis.vidi.R;
import com.sapergis.vidi.helper.VDHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Locale;

public class VDTextToSpeech {
    private final String className = getClass().getSimpleName();
    private TextToSpeech textToSpeech;
    private final Context context;

    public VDTextToSpeech(Context context) {
        this.context = context;
        initialize();
    }

    private void initialize (){
        VDHelper.debugLog(className, context.getString(R.string.tts_onDevice_initiating));
        textToSpeech = new TextToSpeech(context, status -> {
            if ( status == TextToSpeech.SUCCESS){
                textToSpeech.setLanguage(Locale.ENGLISH);
                VDHelper.debugLog(className, context.getString(R.string.tts_onDevice_initiated)+ Locale.ENGLISH);
            }
        });
    }

    public void speak (String text){
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null,null);
    }

    public void shutDown (){
        textToSpeech.shutdown();
        VDHelper.debugLog(className, "->> was ShutDown");
    }

    /**
    Executing Google Text-To-Speech API, when we need the text to be spoken on languages that
     android TTS does not support e.g. Greek
     */
    public static void runTextToSpeechOnCloud (String text, Context context){
        try {
            TextToSpeechSettings textToSpeechSettings = null;
            VDHelper.debugLog("VDTextToSpeech", context.getString(R.string.initiating_google_cloud_tts));
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
            VDHelper.debugLog("VDTextToSpeech", context.getString(R.string.send_google_cloud_tts_req));
            SynthesizeSpeechResponse response = textToSpeechClient
                    .synthesizeSpeech(input, voice, audioConfig);
            VDHelper.debugLog("VDTextToSpeech", context.getString(R.string.get_google_cloud_tts_res));
            ByteString audioContents = response.getAudioContent();
            //TODO : DO save the mp3 in a more generic way
            File mp3File = new File("/storage/emulated/0/DCIM/TTS/tts.mp3");
            OutputStream outputStream = new FileOutputStream(mp3File);
            outputStream.write(audioContents.toByteArray());
            MediaPlayer mediaPlayer = MediaPlayer.create(context, Uri.fromFile(mp3File));
            mediaPlayer.start();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
