package com.example.seb.aplicacion.Activity;

import extern.UsuariosSQLiteHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.seb.aplicacion.R;
import android.content.Intent;

public class Activity_Splash extends AppCompatActivity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    private Class activityGoTo;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);

        /**
         * DATABASE
         */
        Log.d("__SPLASH__", "Abriendo la base de datos...");
        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, "DB", null, 1);
        SQLiteDatabase db = usdbh.getWritableDatabase();
        if (db == null) {
            Log.e("__SPLASH__", "[Error]");
            db.close();
            // FIXME: cerrar aplicación
        } else {
            Log.d("__SPLASH__", "[OK]");
        }

        /**
         * Preferencias
         */
        SharedPreferences appSetting = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        boolean log = appSetting.getBoolean("log", true);
        if(log) {
            activityGoTo = Activity_Main.class;
        }
        else {
            activityGoTo = Activity_Login.class;
        }

        /**
         * Delay
         */
        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Activity_Splash.this, activityGoTo);
                Activity_Splash.this.startActivity(intent);
                Activity_Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}