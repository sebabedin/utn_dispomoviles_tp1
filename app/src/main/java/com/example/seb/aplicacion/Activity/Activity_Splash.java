package com.example.seb.aplicacion.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.seb.aplicacion.R;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class Activity_Splash extends AppCompatActivity {

    private Button btnLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /**
         * SETTINGS
         */
        SharedPreferences appSetting = getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean init = appSetting.getBoolean("init", false);
        if(!init)
        {
            SharedPreferences.Editor appSettingEditor = appSetting.edit();
            appSettingEditor.putBoolean("init", true);
            appSettingEditor.putString("name", "");
            appSettingEditor.putBoolean("gps", false);
            appSettingEditor.putBoolean("imu", false);
            appSettingEditor.putBoolean("log", false);
            appSettingEditor.commit();
        }

        /**
         * goto LOGIN
         */
        btnLog = (Button)findViewById(R.id.btnLog);
        btnLog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Log.i("setOnClickListener", "btnLog");
                Intent intent = new Intent(Activity_Splash.this, Activity_Login.class);
                startActivity(intent);
            }
        });
    }
}
