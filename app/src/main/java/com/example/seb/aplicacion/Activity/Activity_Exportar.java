package com.example.seb.aplicacion.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seb.aplicacion.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import extern.UsuariosSQLiteHelper;

public class Activity_Exportar extends AppCompatActivity {

    private final String DEBUG_TAG              = "Exportar";

    public TextView viewContenido;
    public Button viewExportar;
    public Button viewLeer;
//    public TextView txtResultado;

    public SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exportar);

        viewContenido = (TextView)findViewById(R.id.viewContenido);
        viewExportar = (Button) findViewById(R.id.viewForm_Exportar);
        viewLeer = (Button) findViewById(R.id.viewForm_Leer);

        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, "DB", null, 1);
        db = usdbh.getWritableDatabase();

        viewExportar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                viewExportar_OnClick(arg0);
            }
        });
        viewLeer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                viewLeer_OnClick(arg0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manu_volver, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.itmVolver:
                Log.i(DEBUG_TAG, "onOptionsItemSelected: itmVolver");
                i = new Intent(Activity_Exportar.this, Activity_Main.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*******************************************************************************
     * OnClick
     *******************************************************************************/
    public void viewExportar_OnClick(View arg0) {
        Log.d(DEBUG_TAG, "viewExportar_OnClick");
        DatosExportar();
    }

    public void viewLeer_OnClick(View arg0) {
        Log.d(DEBUG_TAG, "viewLeer_OnClick");
        DatosLeer();
    }

    /*******************************************************************************
     * SD
     *******************************************************************************/
    private boolean SD_Check() {
        boolean ret = false;
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            Log.d(DEBUG_TAG, "SD_Check: ok " + Environment.MEDIA_MOUNTED);
            ret = true;
        } else if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            Log.d(DEBUG_TAG, "SD_Check: error " + Environment.MEDIA_MOUNTED_READ_ONLY);
        } else {
            Log.d(DEBUG_TAG, "SD_Check: error other");
        }
        return ret;
    }

    /*******************************************************************************
     * Manejo del fichero
     *******************************************************************************/
    public void DatosExportar() {
        Log.d(DEBUG_TAG, "Verificar SD ... ");
//                boolean ret = false;
        if(SD_Check()) {
            try {
                Log.d(DEBUG_TAG, "Crear archivo ...");
//                        File ruta_sd_global = Environment.getExternalStorageDirectory();
                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "datos.txt");
                OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(f));
                Log.d(DEBUG_TAG, "[OK]");
                Log.d(DEBUG_TAG, "Busqueda ...");
                String[] campos = new String[] {"cap_ID", "cap_Foto",
                        "cap_GPS", "cap_IMU", "cap_COM",
                        "cap_GPS_Lon", "cap_GPS_Lat",
                        "cap_IMU_Gyro_P", "cap_IMU_Gyro_R", "cap_IMU_Gyro_Y",
                        "cap_IMU_Acc_X", "cap_IMU_Acc_Y", "cap_IMU_Acc_Z",
                        "cap_COM_P", "cap_COM_R", "cap_COM_Y" };
//                        campos.length
                String[] args = null;
                Cursor c = db.query("capturas", campos, null, args, null, null, null);
                Log.d(DEBUG_TAG, "[OK]");
                Log.d(DEBUG_TAG, "Exportando ...");
                if (c.moveToFirst()) {
                    do {
                        String salida = "";
                        salida = salida + c.getPosition();
                        for(int i = 0; i < campos.length; i++) {
                            salida = salida + "," + c.getString(i);
                        }
                        salida = salida + "\r\n";
                        fout.write(salida);
                    } while(c.moveToNext());
                }
                Log.d(DEBUG_TAG, "[OK]");
                fout.close();
                Log.d(DEBUG_TAG, "Proceso finalizado");
                Toast.makeText(Activity_Exportar.this, "Información exportada", Toast.LENGTH_SHORT).show();
                DatosLeer();
            }
            catch (Exception ex) {
                Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
            }
        }
        else {
            Log.d(DEBUG_TAG, "SD_BMPSave: error SD_Check");
        }
//                return ret;
    }

    public void DatosLeer() {
        String salida = "";
        Log.d(DEBUG_TAG, "Leyendo ...");
//        File ruta_sd_global = Environment.getExternalStorageDirectory();
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "datos.txt");
        try {
            BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String texto;
            do {
                texto = fin.readLine();
                Log.d(DEBUG_TAG, "texto:" + texto);
                if(texto.length() > 0) {
                    salida = salida + "\r\n" + texto.toString();
                }
            }
            while(texto.length() > 0);
            fin.close();
            Log.d(DEBUG_TAG, "[OK]");
        }
        catch (Exception ex) {
            Log.d(DEBUG_TAG, "[ERROR]");
        }

//        viewContenido = (TextView)findViewById(R.id.contenido);
        viewContenido.setText( salida.toString() );
    }
}
