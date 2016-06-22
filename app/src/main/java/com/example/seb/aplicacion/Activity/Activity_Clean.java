package com.example.seb.aplicacion.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seb.aplicacion.R;

import java.io.File;
import java.io.FileNotFoundException;

import extern.UsuariosSQLiteHelper;

public class Activity_Clean extends AppCompatActivity {

    private final String DEBUG_TAG              = "Clean";

    private Button viewBorrar;
    private CheckBox viewConfirmar;

    public SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);

        viewConfirmar = (CheckBox) findViewById(R.id.viewForm_Confirmar);
        viewBorrar = (Button) findViewById(R.id.viewForm_Borrar);

        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, "DB", null, 1);
        db = usdbh.getWritableDatabase();

        viewBorrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                viewBorrar_OnClick(arg0);
            }
        });

    }

    /*******************************************************************************
     * Menu
     *******************************************************************************/
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
                i = new Intent(Activity_Clean.this, Activity_Main.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*******************************************************************************
     * OnClick
     *******************************************************************************/
    public void viewBorrar_OnClick(View arg0) {
        Log.d(DEBUG_TAG, "viewBorrar_OnClick");
        if(viewConfirmar.isChecked()) {
            /**
             * Busqueda
             */
            Log.d(DEBUG_TAG, "Busqueda ...");
            String[] campos = new String[] {"cap_ID", "cap_Foto"};
            Cursor c = db.query("capturas", campos, null, null, null, null, null);
            Log.d(DEBUG_TAG, "[OK]");
            /**
             * Elimiando archivos de fotos
             */
            Log.d(DEBUG_TAG, "Eliminando fotos ...");
            if (c.moveToFirst()) {
                do {
                    String nombre = c.getString(1);
                    try {
                        File f = new File(nombre);
                        f.delete();
                        Log.d(DEBUG_TAG, nombre + " ... eliminada");
                    }
                    catch (Exception ex)
                    {
                        Log.d(DEBUG_TAG, nombre + " ... ERROR");
                    }

                } while(c.moveToNext());
            }
            Log.d(DEBUG_TAG, "[OK]");

            /**
             * Eliminando registros de la base de datos
             */
            Log.d(DEBUG_TAG, "Eliminando registros ...");
            db.delete("capturas", null, null);
            Log.d(DEBUG_TAG, "[OK]");

            /**
             * Saliendo
             */
            Log.d(DEBUG_TAG, "Tschüss!");
            Toast.makeText(Activity_Clean.this, "Información eliminada", Toast.LENGTH_SHORT).show();
            Intent i;
            i = new Intent(Activity_Clean.this, Activity_Main.class);
            startActivity(i);
        }
        else {
            Toast.makeText(Activity_Clean.this, "No confirmado", Toast.LENGTH_SHORT).show();
        }


    }
}
