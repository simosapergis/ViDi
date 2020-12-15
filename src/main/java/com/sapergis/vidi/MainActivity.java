package com.sapergis.vidi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.sapergis.vidi.fragments.CameraFragment;
import com.sapergis.vidi.fragments.CapturedImageFragment;
import com.sapergis.vidi.fragments.PreferencesFragment;
import com.sapergis.vidi.helper.GrantPermissions;
import com.sapergis.vidi.helper.VDHelper;

import java.util.Map;

import androidx.annotation.NonNull;
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
        //getPreferences().contains()
        SharedPreferences sharedPreferences = getSharedPreferences(VDHelper.VD_SHARED_PREFERENCES,MODE_PRIVATE);
        Map prefs = sharedPreferences.getAll();

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
               //.replace(R.id.preferences, new PreferencesFragment())
                .replace(R.id.cameraFragment, cameraFragment)
                .replace(R.id.captureFragment, capturedImageFragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, CAMERA_FRAGMENT, cameraFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    public void openPreferences(MenuItem item) {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }

//    private void optionsSetup(int seconds){
//        SharedPreferences sharedPreferences =
//                getSharedPreferences(String.valueOf(R.string.vd_shared_pref), MODE_PRIVATE);
//        SharedPreferences.Editor editor =
//                sharedPreferences.edit().putInt(String.valueOf(R.string.capture_seq_val), seconds);
//        editor.apply();
//    }


}