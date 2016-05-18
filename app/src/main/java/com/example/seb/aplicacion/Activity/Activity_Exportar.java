package com.example.seb.aplicacion.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    public TextView textView;
    public Button btnProcesar;
    public Button btnLeer;
    public TextView txtResultado;

    public SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exportar);

        textView = (TextView)findViewById(R.id.contenido);

        btnProcesar = (Button) findViewById(R.id.btnProcesar);
        btnLeer = (Button) findViewById(R.id.btnLeer);

        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, "DB", null, 1);
        db = usdbh.getWritableDatabase();

        btnProcesar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Log.d("__EXPORTAR__", "Verificar SD ... ");
                boolean sdDisponible = false;
                boolean sdAccesoEscritura = false;
                String estado = Environment.getExternalStorageState();
                if (estado.equals(Environment.MEDIA_MOUNTED))
                {
                    sdDisponible = true;
                    sdAccesoEscritura = true;
                }
                else if (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
                {
                    sdDisponible = true;
                    sdAccesoEscritura = false;
                }
                else
                {
                    sdDisponible = false;
                    sdAccesoEscritura = false;
                }

                if(sdDisponible && sdAccesoEscritura)
                {
                    Log.d("__EXPORTAR__", "[OK]");
                    try
                    {
                        Log.d("__EXPORTAR__", "Crear archivo ...");
                        File ruta_sd_global = Environment.getExternalStorageDirectory();
                        File f = new File(ruta_sd_global.getAbsolutePath(), "datos.txt");
                        OutputStreamWriter fout =
                                new OutputStreamWriter(
                                        new FileOutputStream(f));
                        Log.d("__EXPORTAR__", "[OK]");

                        Log.d("__EXPORTAR__", "Busqueda ...");
                        String[] campos = new String[] {"cap_ID", "cap_Foto"};
                        Cursor c = db.query("capturas", campos, null, null, null, null, null);
                        Log.d("__EXPORTAR__", "[OK]");

                        Log.d("__EXPORTAR__", "Exportando ...");
                        if (c.moveToFirst()) {
                            do {
                                String codigo = c.getString(0);
                                String nombre = c.getString(1);
                                String salida = codigo + "," + nombre + "," + "\r\n";

                                fout.write(salida);

                            } while(c.moveToNext());
                        }
                        Log.d("__EXPORTAR__", "[OK]");

                        fout.close();
                        Log.d("__EXPORTAR__", "Proceso finalizado");
                        Toast.makeText(Activity_Exportar.this, "Información exportada", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception ex)
                    {
                        Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
                    }
                }
            }
        });

        btnLeer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String salida = "";

                Log.d("__EXPORTAR__", "Leyendo ...");
                File ruta_sd_global = Environment.getExternalStorageDirectory();
                File f = new File(ruta_sd_global.getAbsolutePath(), "datos.txt");
                try {
                    BufferedReader fin =
                            new BufferedReader(
                                    new InputStreamReader(
                                            new FileInputStream(f)));

                    String texto;

                    do {
                        texto = fin.readLine();
                        Log.d("__EXPORTAR__", "texto:" + texto);

                        if(texto.length() > 0)
                        {
                            salida = salida.toString() + "\r\n" + texto.toString();
                        }
                    }
                    while(texto.length() > 0);

                    fin.close();
                    Log.d("__EXPORTAR__", "[OK]");
                }
                catch (Exception ex) {
                    Log.d("__EXPORTAR__", "[ERROR]");
                }

                textView = (TextView)findViewById(R.id.contenido);
                textView.setText( salida.toString() );
            }
        });
    }
}
