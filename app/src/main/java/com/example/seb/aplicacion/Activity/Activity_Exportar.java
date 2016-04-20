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

public class Activity_Exportar extends AppCompatActivity {

    private TextView textView;

    private Button btnProcesar;
    private TextView txtResultado;

    public SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exportar);

        textView = (TextView)findViewById(R.id.textView);

        btnProcesar     = (Button) findViewById(R.id.btnProcesar);
        txtResultado    = (TextView) findViewById(R.id.txtResultado);

        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, "DBUsuarios", null, 1);
        db = usdbh.getWritableDatabase();

        btnProcesar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                String[] campos = new String[] {"codigo", "nombre"};
                String[] args = new String[] {"usu1"};

//        Cursor c = db.query("Usuarios", campos, "nombre=?", args, null, null, null);
                Cursor c = db.query("Usuarios", campos, null, null, null, null, null);

                //Nos aseguramos de que existe al menos un registro
                if (c.moveToFirst()) {
                    //Recorremos el cursor hasta que no haya más registros
                    do {
                        String codigo = c.getString(0);
                        String nombre = c.getString(1);
                        String salida = textView.getText().toString() + "\r\n" +
                                codigo + " - " +
                                nombre;
                        textView.setText( salida.toString() );

                        Log.i("SQLiteDatabase", codigo);
                        Log.i("SQLiteDatabase", nombre);
                        Log.i("SQLiteDatabase", salida);
                    } while(c.moveToNext());
                }
                Log.i("setOnClickListener", "EXPORTAR");

                txtResultado.setText("iNFORMACIÓN EXPORTADA");
            }
        });
    }
}
