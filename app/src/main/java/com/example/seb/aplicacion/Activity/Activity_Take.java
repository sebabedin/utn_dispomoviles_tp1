package com.example.seb.aplicacion.Activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

import extern.UsuariosSQLiteHelper;

public class Activity_Take extends AppCompatActivity {

    private Button btnGoBack;

    private EditText intxtFoto;
    private Button btnProcesar;
    private TextView txtResultado;

    public SQLiteDatabase db;

    private Button btnCaptura;

    private ImageView img;

    final static int cons = 0;
    Bitmap bmp;
    Bundle ext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take);

        intxtFoto = (EditText) findViewById(R.id.intxtFoto);
        btnProcesar = (Button) findViewById(R.id.btnProcesar);
        txtResultado = (TextView) findViewById(R.id.txtResultado);

        Log.d("__TAKE__", "Abriendo la base de datos...");
        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, "DB", null, 1);
        db = usdbh.getWritableDatabase();
        if (db == null) {
            Log.e("__TAKE__", "[Error]");
            db.close();
        } else {
            Log.d("__TAKE__", "[OK]");
        }

        btnProcesar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Log.d("__TAKE__", "btnProcesar.onClick");

                /**
                 * FORM CHECK
                 */
                String foto = intxtFoto.getText().toString();
                if (foto.isEmpty()) {
                    txtResultado.setText("Campo 'Foto' vacio :( ");
                    Log.d("__TAKE__", "return false");
                    return;
                }

                /**
                 * Guardar imagen en SD
                 */
                boolean sdDisponible = false;
                boolean sdAccesoEscritura = false;
                boolean sdImgGuardada = false;

                //Comprobamos el estado de la memoria externa (tarjeta SD)
                String estado = Environment.getExternalStorageState();

                if (estado.equals(Environment.MEDIA_MOUNTED))
                {
                    sdDisponible = true;
                    sdAccesoEscritura = true;
                    Log.d("__TAKE__", "sdDisponible = true;");
                    Log.d("__TAKE__", "sdAccesoEscritura = true;");
                }
                else if (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
                {
                    sdDisponible = true;
                    sdAccesoEscritura = false;
                    Log.d("__TAKE__", "sdDisponible = true;");
                    Log.d("__TAKE__", "sdAccesoEscritura = false;");
                }
                else
                {
                    sdDisponible = false;
                    sdAccesoEscritura = false;
                    Log.d("__TAKE__", "sdDisponible = false;");
                    Log.d("__TAKE__", "sdAccesoEscritura = false;");
                }

                //Si la memoria externa est� disponible y se puede escribir
                FileOutputStream outStream;
                File f;
                if (sdDisponible && sdAccesoEscritura)
                {
                    try
                    {

                        Date d = new Date();
                        CharSequence s  = DateFormat.format("yyyyMMddHHmmss", d.getTime());

                        File ruta_sd_global = Environment.getExternalStorageDirectory();
                        f = new File(ruta_sd_global.getAbsolutePath(), s.toString() + ".jpg");
                        Log.d("__TAKE__", "Ruta de almacenamiento: " + f.toString());

                        Log.d("__TAKE__", "Guardando ...");
                        outStream = new FileOutputStream(f);
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        outStream.flush();
                        outStream.close();
                        Log.d("__TAKE__", "[OK]");

                        intxtFoto.setText(f.toString());
                        sdImgGuardada = true;
                    }
                    catch (Exception ex)
                    {
                        Log.d("__TAKE__", "[ERROR]");
                        Toast.makeText(Activity_Take.this, "Error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                /**
                 * Guardar registro en DB
                 */
                if (sdImgGuardada) {
                    Log.d("__TAKE__", "Insert");

                    String SQL_cmd = "INSERT INTO capturas (cap_Foto, cap_IMU, cap_GPS) " +
                            "VALUES ('" + intxtFoto.getText().toString() + "', 0, 0)";

                    db.execSQL( SQL_cmd );
                    Log.d("__TAKE__", SQL_cmd);

                    Toast.makeText(Activity_Take.this, "Nueva captura", Toast.LENGTH_SHORT).show();

                    txtResultado.setText("Nuevo campos insertado :)");
                    Log.d("__TAKE__", "return true");
                }
            }
        });

        btnGoBack = (Button) findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(Activity_Take.this, Activity_Main.class);
                startActivity(intent);
            }
        });

        img = (ImageView) findViewById(R.id.imagen);
        btnCaptura = (Button) findViewById(R.id.btnCaptura);
        btnCaptura.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, cons);
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

        img.setImageBitmap(bmp);
    }
}
