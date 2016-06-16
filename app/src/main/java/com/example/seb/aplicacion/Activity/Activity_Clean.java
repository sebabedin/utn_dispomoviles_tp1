package com.example.seb.aplicacion.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seb.aplicacion.R;

import java.io.File;
import java.io.FileNotFoundException;

import extern.UsuariosSQLiteHelper;

public class Activity_Clean extends AppCompatActivity {

    private final String DEBUG_TAG              = "Clean";

    private Button btnProcesar;

    public SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);

        btnProcesar     = (Button) findViewById(R.id.btnProcesar);

        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, "DB", null, 1);
        db = usdbh.getWritableDatabase();

        btnProcesar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                /**
                 * TODO: poner un checkbox para validar que quiero borrar !
                 */

                /**
                 * Busqueda
                 */
                Log.d("__CLEAN__", "Busqueda ...");
                String[] campos = new String[] {"cap_ID", "cap_Foto"};
                Cursor c = db.query("capturas", campos, null, null, null, null, null);
                Log.d("__CLEAN__", "[OK]");

                /**
                 * Elimiando archivos de fotos
                 */
                Log.d("__CLEAN__", "Eliminando fotos ...");
                if (c.moveToFirst()) {
                    do {
                        String nombre = c.getString(1);

                        try {
                            File f = new File(nombre);
                            f.delete();
                            Log.d("__CLEAN__", nombre + " ... eliminada");
                        }
                        catch (Exception ex)
                        {
                            Log.d("__CLEAN__", nombre + " ... ERROR");
                        }

                    } while(c.moveToNext());
                }
                Log.d("__CLEAN__", "[OK]");

                /**
                 * Eliminando registros de la base de datos
                 */
                Log.d("__CLEAN__", "Eliminando registros ...");
                db.delete("capturas", null, null);
                Log.d("__CLEAN__", "[OK]");

                /**
                 * Saliendo
                 */
                Log.d("__CLEAN__", "Tschüss!");
                Toast.makeText(Activity_Clean.this, "Información eliminada", Toast.LENGTH_SHORT).show();
                Intent i;
                i = new Intent(Activity_Clean.this, Activity_Main.class);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.itmVolver:
                Log.i(DEBUG_TAG, "onOptionsItemSelected: itmVolver");
                i = new Intent(Activity_Clean.this, Activity_Main.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
