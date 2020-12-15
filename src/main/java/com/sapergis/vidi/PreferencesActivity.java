package com.sapergis.vidi;

import android.os.Bundle;

import com.sapergis.vidi.fragments.PreferencesFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        PreferencesFragment preferencesFragment = new PreferencesFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.preferences, preferencesFragment)
                .commit();
    }
}
