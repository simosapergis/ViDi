package com.sapergis.vidi;

import android.Manifest;
import android.os.Bundle;
import android.view.TextureView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;
    private TextureView viewFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        GrantPermissions.allPermissionsGranted(this) ? viewFinder.post(startCamera)
//                : GrantPermissions.requestPermissions(this);

    }
}