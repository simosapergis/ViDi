package com.sapergis.vidi;

import android.os.Bundle;
import android.widget.Toast;
import com.sapergis.vidi.fragments.CameraFragment;
import com.sapergis.vidi.fragments.CapturedImageFragment;
import com.sapergis.vidi.implementation.VDTextToSpeech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private CameraFragment cameraFragment;
    private CapturedImageFragment capturedImageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if( GrantPermissions.allPermissionsGranted(this) ){
            attachFragments();
        }else{
            GrantPermissions.requestPermissions(this);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GrantPermissions.REQUEST_CODE_PERMISSIONS) {
            if ( GrantPermissions.allPermissionsGranted(this) ) {
                attachFragments();
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void attachFragments(){
        getSupportFragmentManager().beginTransaction()
                .add(R.id.cameraFragment, cameraFragment.newInstance(null, null))
                .add(R.id.captureFragment, capturedImageFragment.newInstance(null,null))
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}