package com.example.seb.aplicacion.Activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seb.aplicacion.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import extern.UsuariosSQLiteHelper;

public class Activity_Take extends AppCompatActivity {

    private Button btnGoBack;
    private EditText intxtFoto;
    private Button btnProcesar;
    private Button btnCaptura;
    private ImageView imgImagen;

    public SQLiteDatabase db;
    final static int cons = 0;
    Bitmap bmp;
    Bundle ext;
    String ImagenNombre;
    String ImagenPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take);

        intxtFoto = (EditText) findViewById(R.id.intxtFoto);
        btnProcesar = (Button) findViewById(R.id.btnProcesar);
        btnGoBack = (Button) findViewById(R.id.btnGoBack);
        imgImagen = (ImageView) findViewById(R.id.imagen);
        btnCaptura = (Button) findViewById(R.id.btnCaptura);

        /**
         * Base de datos
         */
        Log.d("__TAKE__", "Abriendo la base de datos...");
        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, "DB", null, 1);
        db = usdbh.getWritableDatabase();
        if (db == null) {
            Log.e("__TAKE__", "[Error]");
            db.close();
        } else {
            Log.d("__TAKE__", "[OK]");
        }


        /**
         * Boton Volver
         */
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Log.d("__TAKE__", "Botón VOLVER");

                Intent intent = new Intent(Activity_Take.this, Activity_Main.class);
                startActivity(intent);

            }
        });

        /**
         * Boton Capturar
         */
        btnCaptura.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Log.d("__TAKE__", "Botón CAPTURAR");

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, cons);

            }
        });

        /**
         * Boton Procesar
         */
        btnProcesar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Log.d("__TAKE__", "Botón PROCESAR");

                /**
                 * FORM CHECK
                 */
                String foto = intxtFoto.getText().toString();
                if (foto.isEmpty()) {
                    Toast.makeText(Activity_Take.this, "Campo 'Foto' vacio :( ", Toast.LENGTH_SHORT).show();
                    Log.d("__TAKE__", "Campo 'Foto' vacio :( ");
                    return;
                }

                /**
                 * Guardar imagen en SD
                 */
                boolean sdDisponible = false;
                boolean sdAccesoEscritura = false;
                boolean sdImgGuardada = false;
                String estado = Environment.getExternalStorageState();
                if (estado.equals(Environment.MEDIA_MOUNTED))
                {
                    sdDisponible = true;
                    sdAccesoEscritura = true;
                    Log.d("__TAKE__", "sdDisponible = true; sdAccesoEscritura = true;");
                }
                else if (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
                {
                    sdDisponible = true;
                    sdAccesoEscritura = false;
                    Log.d("__TAKE__", "sdDisponible = true; sdAccesoEscritura = false;");
                }
                else
                {
                    sdDisponible = false;
                    sdAccesoEscritura = false;
                    Log.d("__TAKE__", "sdDisponible = false; sdAccesoEscritura = false;");
                }

                if (sdDisponible && sdAccesoEscritura)
                {
                    try
                    {
                        FileOutputStream outStream;
                        File f;

                        File ruta_sd_global = Environment.getExternalStorageDirectory();
                        f = new File(ruta_sd_global.getAbsolutePath(), ImagenNombre);
                        ImagenPath = f.toString();
                        Log.d("__TAKE__", "Ruta de almacenamiento: " + ImagenPath);

                        Log.d("__TAKE__", "Guardando ...");
                        outStream = new FileOutputStream(f);
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        outStream.flush();
                        outStream.close();
                        Log.d("__TAKE__", "[OK]");
                        sdImgGuardada = true;
                    }
                    catch (Exception ex)
                    {
                        Log.d("__TAKE__", "[ERROR]");
                        Toast.makeText(Activity_Take.this, "Error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                else
                {
                    Toast.makeText(Activity_Take.this, "Error", Toast.LENGTH_SHORT).show();
                    Log.d("__TAKE__", "[ERROR] Acceso a SD");
                }

                /**
                 * Guardar registro en DB
                 */
                boolean dbCapturaNueva = false;
                if (sdImgGuardada) {
                    Log.d("__TAKE__", "Insert ...");

                    String SQL_cmd = "INSERT INTO capturas (cap_Foto, cap_IMU, cap_GPS) " +
                            "VALUES ('" + ImagenPath + "', 0, 0)";
                    Log.d("__TAKE__", SQL_cmd);
                    db.execSQL(SQL_cmd);

                    Log.d("__TAKE__", "[OK]");

                    dbCapturaNueva = true;
                }

                /**
                 * Finalizo el proceso : Éxito
                 */
                if(dbCapturaNueva)
                {
                    intxtFoto.setText("");
                    imgImagen.setImageBitmap(null);

                    Toast.makeText(Activity_Take.this, "Nueva captura :)", Toast.LENGTH_SHORT).show();
                    Log.d("__TAKE__", "proceso [OK]");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity_Take.RESULT_OK) {
            ext = data.getExtras();
        }

        bmp = (Bitmap) ext.get("data");

        Date d = new Date();
        CharSequence s = DateFormat.format("yyyyMMddHHmmss", d.getTime());
        ImagenNombre = s + ".jpg";
        intxtFoto.setText(ImagenNombre.toString());

        imgImagen.setImageBitmap(bmp);
    }
}
