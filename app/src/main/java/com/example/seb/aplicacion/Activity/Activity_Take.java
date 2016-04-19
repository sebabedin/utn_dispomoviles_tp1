package com.example.seb.aplicacion.Activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.seb.aplicacion.R;

import extern.UsuariosSQLiteHelper;

public class Activity_Take extends AppCompatActivity {

    private Button btnGoBack;

    private EditText intxtFoto;
    private Button btnProcesar;
    private TextView txtResultado;

    public SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take);

        intxtFoto      = (EditText) findViewById(R.id.intxtFoto);
        btnProcesar     = (Button) findViewById(R.id.btnProcesar);
        txtResultado    = (TextView) findViewById(R.id.txtResultado);

        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, "DBUsuarios", null, 1);
        db = usdbh.getWritableDatabase();

        btnProcesar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                String foto = intxtFoto.getText().toString();

                if (foto.isEmpty()) {
                    txtResultado.setText("Campo \"Foto\" vacio :( ");
                    return;
                }

                db.execSQL( "INSERT INTO Usuarios (codigo, nombre) " +
                            "VALUES (0, '" + foto.toString() +"')");

                Log.i("SQLiteDatabase", "INSERT");

                txtResultado.setText("Nuevo campos insertado :)");
            }
        });

        btnGoBack = (Button)findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(Activity_Take.this, Activity_Main.class);
                startActivity(intent);
            }
        });
    }
}
