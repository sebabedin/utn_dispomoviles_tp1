package com.example.seb.aplicacion.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seb.aplicacion.R;

import extern.UsuariosSQLiteHelper;

public class Activity_Clean extends AppCompatActivity {

    private Button btnProcesar;
    private TextView txtResultado;

    public SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);

        btnProcesar     = (Button) findViewById(R.id.btnProcesar);
        txtResultado    = (TextView) findViewById(R.id.txtResultado);

        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, "DB", null, 1);
        db = usdbh.getWritableDatabase();

        btnProcesar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                /**
                 * TODO: poner un checkbox para validar que quiero borrar !
                  */

                db.delete("capturas", null, null);
                Log.i("setOnClickListener", "ELIMINADO");

                Toast.makeText(Activity_Clean.this, "Información eliminada", Toast.LENGTH_SHORT).show();
                Intent i;
                i = new Intent(Activity_Clean.this, Activity_Main.class);
                startActivity(i);
            }
        });

    }
}
