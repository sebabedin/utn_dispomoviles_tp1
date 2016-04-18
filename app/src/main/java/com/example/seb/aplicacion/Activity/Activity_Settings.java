package com.example.seb.aplicacion.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.seb.aplicacion.R;

public class Activity_Settings extends AppCompatActivity {

//    private TextView txtName;
//    private TextView txtGPS;
//    private TextView txtIMU;
//    private TextView txtLogin;
    private EditText intxtName;
    private ToggleButton tglGPS;
    private ToggleButton tglIMU;
    private ToggleButton tglLogin;
    private Button btnAccept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        intxtName = (EditText) findViewById(R.id.intxtName);
        tglGPS = (ToggleButton) findViewById(R.id.tglGPS);
        tglIMU = (ToggleButton) findViewById(R.id.tglIMU);
        tglLogin = (ToggleButton) findViewById(R.id.tglLogin);

        SharedPreferences appSetting = getSharedPreferences("settings", Context.MODE_PRIVATE);
        intxtName.setText(appSetting.getString("name", "").toString());
        tglGPS.setChecked(appSetting.getBoolean("gps", false));
        tglIMU.setChecked(appSetting.getBoolean("imu", false));
        tglLogin.setChecked(appSetting.getBoolean("log", false));

        btnAccept = (Button) findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                SharedPreferences appSetting = getSharedPreferences("settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor appSettingEditor = appSetting.edit();
                appSettingEditor.putString("name", intxtName.getText().toString());
                appSettingEditor.putBoolean("gps", tglGPS.isChecked());
                appSettingEditor.putBoolean("imu", tglIMU.isChecked());
                appSettingEditor.putBoolean("log", tglLogin.isChecked());
                appSettingEditor.commit();
            }
        });
    }
}
