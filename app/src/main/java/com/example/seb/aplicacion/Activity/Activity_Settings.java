package com.example.seb.aplicacion.Activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.example.seb.aplicacion.R;

public class Activity_Settings extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferencias);
    }
}
