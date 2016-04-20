package com.example.seb.aplicacion.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, "DBUsuarios", null, 1);
        db = usdbh.getWritableDatabase();

        btnProcesar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                /**
                 * TODO: poner un checkbox para validar que quiero borrar !
                  */

                db.delete("Usuarios", null, null);
                Log.i("setOnClickListener", "ELIMINADO");

                txtResultado.setText("iNFORMACIÓN EXPORTADA");
            }
        });

    }
}
