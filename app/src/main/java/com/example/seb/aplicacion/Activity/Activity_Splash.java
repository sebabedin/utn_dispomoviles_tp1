package com.example.seb.aplicacion.Activity;

//import extern.Databasehelper;
import extern.Databasehelper;
import extern.UsuariosSQLiteHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.seb.aplicacion.R;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Activity_Splash extends AppCompatActivity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 5000;

    public Class gotoActivity;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);

//        /**
//         * SETTINGS
//         */
//        SharedPreferences appSetting = getSharedPreferences("settings", Context.MODE_PRIVATE);
//        boolean init = appSetting.getBoolean("init", false);
//        if(!init)
//        {
//            SharedPreferences.Editor appSettingEditor = appSetting.edit();
//            appSettingEditor.putBoolean("init", true);
//            appSettingEditor.putString("name", "");
//            appSettingEditor.putBoolean("gps", false);
//            appSettingEditor.putBoolean("imu", false);
//            appSettingEditor.putBoolean("log", false);
//            appSettingEditor.commit();
//        }

//        Log.i("appSetting.init", appSetting.getBoolean("init", false) ? "true" : "false");
//        Log.i("appSetting.name", appSetting.getString("name", ""));
//        Log.i("appSetting.gps", appSetting.getBoolean("gps", false) ? "true" : "false");
//        Log.i("appSetting.imu", appSetting.getBoolean("imu", false) ? "true" : "false");
//        Log.i("appSetting.log", appSetting.getBoolean("log", false) ? "true" : "false");

        /**
         * DATABASE
         */
        //Abrimos la base de datos 'DBUsuarios' en modo escritura
        Log.d("__SPLASH__", "Abriendo la base de datos...");
        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, "DB", null, 1);
        SQLiteDatabase db = usdbh.getWritableDatabase();

        //Si hemos abierto correctamente la base de datos
        if (db == null) {
            Log.e("__SPLASH__", "[Error]");
            /*
            pantalla de error ???
             */

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
        } else {
            Log.d("__SPLASH__", "[OK]");
        }


//        try
//        {
//            OutputStreamWriter fout=
//                    new OutputStreamWriter(
//                            openFileOutput("prueba_int.txt", Context.MODE_PRIVATE));
//
//            fout.write("Texto de prueba.");
//            fout.close();
//        }
//        catch (Exception ex)
//        {
//            Log.e("Ficheros", "Error al escribir fichero a memoria interna");
//        }

        /** ------------------------------------------------------------------ */


//        boolean sdDisponible = false;
//        boolean sdAccesoEscritura = false;
//
//        //Comprobamos el estado de la memoria externa (tarjeta SD)
//        String estado = Environment.getExternalStorageState();
//
//        if (estado.equals(Environment.MEDIA_MOUNTED))
//        {
//            sdDisponible = true;
//            sdAccesoEscritura = true;
//        }
//        else if (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
//        {
//            sdDisponible = true;
//            sdAccesoEscritura = false;
//        }
//        else
//        {
//            sdDisponible = false;
//            sdAccesoEscritura = false;
//        }
//
//        //Si la memoria externa est� disponible y se puede escribir
//        if (sdDisponible && sdAccesoEscritura)
//        {
//            try
//            {
//                File ruta_sd_global = Environment.getExternalStorageDirectory();
//                //File ruta_sd_app_musica = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
//
//                File f = new File(ruta_sd_global.getAbsolutePath(), "prueba_sd.txt");
//
//                OutputStreamWriter fout =
//                        new OutputStreamWriter(
//                                new FileOutputStream(f));
//
//                fout.write("Texto de prueba.");
//                fout.close();
//
//                Log.i("Ficheros", "Fichero SD creado!");
//            }
//            catch (Exception ex)
//            {
//                Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
//            }
//        }

        /** ------------------------------------------------------------------- */

//        try
//        {
//            File ruta_sd_global = Environment.getExternalStorageDirectory();
//
//            File f = new File(ruta_sd_global.getAbsolutePath(), "prueba_sd.txt");
//
//            Log.i("Ficheros ruta", ruta_sd_global.getAbsolutePath().toString());
//
//            BufferedReader fin =
//                    new BufferedReader(
//                            new InputStreamReader(
//                                    new FileInputStream(f)));
//
//            String texto = fin.readLine();
//            fin.close();
//
//            Log.i("Ficheros", "Fichero SD leido!");
//            Log.i("Ficheros", "Texto: " + texto);
//        }
//        catch (Exception ex)
//        {
//            Log.e("Ficheros", "Error al leer fichero desde tarjeta SD");
//        }


        /** ----------------------------------------------------------------------- */

        /**
         * goto LOGIN-MAIN
         */
//        if (appSetting.getBoolean("log", false))
//        {
//            gotoActivity = Activity_Login.class;
//        }
//        else
//        {
            gotoActivity = Activity_Main.class;
//        }

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
