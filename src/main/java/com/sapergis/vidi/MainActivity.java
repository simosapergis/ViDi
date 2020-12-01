package com.sapergis.vidi;

import android.os.Bundle;
import android.widget.Toast;
import com.sapergis.vidi.fragments.CameraFragment;
import com.sapergis.vidi.fragments.CapturedImageFragment;
import com.sapergis.vidi.helper.GrantPermissions;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    private static final String CAMERA_FRAGMENT = "cameraFragment";
    private CameraFragment cameraFragment = CameraFragment.newInstance(null,null);
    private CapturedImageFragment capturedImageFragment = CapturedImageFragment.newInstance(null,null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null){
            cameraFragment = (CameraFragment) getSupportFragmentManager().getFragment(savedInstanceState, CAMERA_FRAGMENT);
        }
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
                .replace(R.id.cameraFragment, cameraFragment)
                .replace(R.id.captureFragment, capturedImageFragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, CAMERA_FRAGMENT, cameraFragment);
    }

}