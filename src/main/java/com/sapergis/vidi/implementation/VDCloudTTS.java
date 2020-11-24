package com.sapergis.vidi.implementation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
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
import com.sapergis.vidi.interfaces.IVDTextOperations;

public class VDCloudTTS {
    private final String className = this.getClass().getSimpleName();
    private final Context context;
    private final IVDTextOperations ivdTextOperations;
    private AudioConfig audioConfig;
    private VoiceSelectionParams voice;
    private TextToSpeechSettings textToSpeechSettings;

    public VDCloudTTS(Context context, IVDTextOperations ivdTextOperations) {
        this.context = context;
        this.ivdTextOperations = ivdTextOperations;
        initialize();
    }

    private void initialize() {
        try {
            VDHelper.debugLog(className, context.getString(R.string.initiating_google_cloud_tts));
            textToSpeechSettings = TextToSpeechSettings.newBuilder().setCredentialsProvider(
                    FixedCredentialsProvider.create(ServiceAccountCredentials.fromStream(
                            //TODO : SET up google cloud credentials out of device storage
                            new FileInputStream(VDHelper.GOOGLE_SERVICE_PROP_PATH))
                    ))
                    .build();
            //TODO should shutdown textToSpeechClient
            voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(VDHelper.GoogleTTSLanguages.GREEK)
                    .setSsmlGender(SsmlVoiceGender.FEMALE)
                    .build();
            audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runTextToSpeechOnCloud(String text) {
        try {
            SynthesizeSpeechResponse response;
            try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create(textToSpeechSettings)) {
                VDHelper.debugLog(className, context.getString(R.string.send_google_cloud_tts_req));
                SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
                response = textToSpeechClient
                        .synthesizeSpeech(input, voice, audioConfig);
            }
            VDHelper.debugLog(className, context.getString(R.string.get_google_cloud_tts_res));
            ByteString audioContents = response.getAudioContent();
            //TODO : DO save the mp3 in a more generic way
            File mp3File = new File(VDHelper.MP3FILEPATH);
            try (OutputStream outputStream = new FileOutputStream(mp3File)) {
                outputStream.write(audioContents.toByteArray());
            }
            MediaPlayer mediaPlayer = MediaPlayer.create(context, Uri.fromFile(mp3File));
            VDHelper.debugLog(getClass().getSimpleName(), context.getString(R.string.cloud_tts_speaking));
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener( mp -> {
                    ivdTextOperations.onTextToSpeechFinished();
                    mp.release();
                    VDHelper.debugLog(getClass().getSimpleName(), context.getString(R.string.cloud_tts_finished));
                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
