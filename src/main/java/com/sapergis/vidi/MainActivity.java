package com.sapergis.vidi;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        GrantPermissions.allPermissionsGranted(this) ? viewFinder.post(startCamera)
//                : GrantPermissions.requestPermissions(this);

    }
}