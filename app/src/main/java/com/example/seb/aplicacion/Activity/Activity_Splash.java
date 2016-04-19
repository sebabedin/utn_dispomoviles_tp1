package com.example.seb.aplicacion.Activity;

//import extern.Databasehelper;
import extern.Databasehelper;
import extern.UsuariosSQLiteHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.seb.aplicacion.R;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class Activity_Splash extends AppCompatActivity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    public Class gotoActivity;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
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

        Log.i("appSetting.init", appSetting.getBoolean("init", false) ? "true" : "false");
        Log.i("appSetting.name", appSetting.getString("name", ""));
        Log.i("appSetting.gps", appSetting.getBoolean("gps", false) ? "true" : "false");
        Log.i("appSetting.imu", appSetting.getBoolean("imu", false) ? "true" : "false");
        Log.i("appSetting.log", appSetting.getBoolean("log", false) ? "true" : "false");

        /**
         * DATABASE
         */
        //Abrimos la base de datos 'DBUsuarios' en modo escritura
        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, "DBUsuarios", null, 1);
        SQLiteDatabase db = usdbh.getWritableDatabase();

        //Si hemos abierto correctamente la base de datos
        if (db == null)
        {
            /*
            pantalla de error ???
             */
            Log.i("UsuariosSQLiteHelper", "Error con la base de datos");

//            //Insertamos 5 usuarios de ejemplo
//            for(int i=1; i<=5; i++)
//            {
//                //Generamos los datos
//                int codigo = i;
//                String nombre = "Usuario" + i;
//
//                //Insertamos los datos en la tabla Usuarios
//                db.execSQL( "INSERT INTO Usuarios (codigo, nombre) " +
//                            "VALUES (" + codigo + ", '" + nombre +"')");
//
//                Log.i("SQLiteDatabase", "INSERT");
//            }

            //Cerramos la base de datos
            db.close();
        }

        /**
         * goto LOGIN-MAIN
         */
        if (appSetting.getBoolean("log", false))
        {
            gotoActivity = Activity_Login.class;
        }
        else
        {
            gotoActivity = Activity_Main.class;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Activity_Splash.this, gotoActivity);
                Activity_Splash.this.startActivity(intent);
                Activity_Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

}
