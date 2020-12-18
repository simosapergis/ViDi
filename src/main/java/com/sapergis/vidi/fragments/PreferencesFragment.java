package com.sapergis.vidi.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import com.sapergis.vidi.R;
import com.sapergis.vidi.interfaces.IVDAutoCapture;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

public class PreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        constructPreferences(sharedPreferences);
    }

    private void constructPreferences(SharedPreferences sharedPreferences){
        Context context = getActivity();
        PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(context);
        setPreferenceScreen(preferenceScreen);
        TypedValue themeValue = new TypedValue();
        assert context != null;
        context.getTheme().resolveAttribute(R.style.my_pref_style, themeValue, true);
        ContextThemeWrapper contextThemeWrapper =
                new ContextThemeWrapper(context, themeValue.resourceId);
        PreferenceCategory preferenceCategory = new PreferenceCategory(contextThemeWrapper);
        preferenceCategory.setTitle(getString(R.string.preferences_title));
        ListPreference capturePref = new ListPreference(contextThemeWrapper);
        capturePref.setTitle(R.string.capture_sequence_title);
        capturePref.setKey( getString(R.string.capture_seq_key) );
        capturePref.setEntries(R.array.seconds);
        capturePref.setEntryValues(R.array.secondsValues);
        String captureKey = getString(R.string.capture_seq_key);
        String currentInterval = sharedPreferences.getString( captureKey,
                String.valueOf(IVDAutoCapture.DEFAULT_INTERVAL) );
        setCaptureIntervalValues(capturePref, currentInterval);
        capturePref.setOnPreferenceChangeListener((preference, newValue) ->
                setCaptureIntervalValues( (ListPreference) preference, newValue));
        ListPreference inputLangPref = new ListPreference(contextThemeWrapper);
        inputLangPref.setTitle( getString(R.string.input_language_title) );
        inputLangPref.setKey(getString(R.string.input_lang_key));
        inputLangPref.setEntries(R.array.languages);
        inputLangPref.setEntryValues(R.array.languageValues);
        String currentInputLang = sharedPreferences.getString(
                getString(R.string.input_lang_key), getString(R.string.en)
        );
        setLanguageValues(inputLangPref, currentInputLang);
        inputLangPref.setOnPreferenceChangeListener((preference, newValue) ->
                setLanguageValues( (ListPreference) preference, newValue)
        );
        ListPreference outputLangPref = new ListPreference(contextThemeWrapper);
        outputLangPref.setTitle( getString(R.string.output_language_title) );
        outputLangPref.setKey( getString(R.string.output_lang_key) );
        outputLangPref.setEntries(R.array.languages);
        outputLangPref.setEntryValues(R.array.languageValues);
        String currentOutputLang = sharedPreferences.getString(
                getString(R.string.output_lang_key), getString(R.string.el)
        );
        setLanguageValues(outputLangPref, currentOutputLang);
        outputLangPref.setOnPreferenceChangeListener((preference, newValue) ->
                setLanguageValues( (ListPreference) preference, newValue)
        );
        getPreferenceScreen().addPreference(preferenceCategory);
        preferenceCategory.addPreference(capturePref);
        preferenceCategory.addPreference(inputLangPref);
        preferenceCategory.addPreference(outputLangPref);
    }

    private boolean setCaptureIntervalValues(ListPreference preference, Object value) {
        String captureInterval = value.toString();
        String selectedValue = Integer.parseInt(captureInterval)/1000 + " Seconds";
        preference.setSummary(selectedValue);
        preference.setValue(captureInterval);
        return false;
    }

    private boolean setLanguageValues(ListPreference preference, Object value){
        String selectedValue = value.toString();
        preference.setSummary(selectedValue);
        preference.setValue(selectedValue);
        return false;
    }
}
