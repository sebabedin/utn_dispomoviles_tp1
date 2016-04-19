package com.example.seb.aplicacion.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// agregadas
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.seb.aplicacion.R;

import extern.UsuariosSQLiteHelper;
import uIO.uMenu;

public class Activity_Main extends AppCompatActivity {

    private Button btnBotones;
    private Button btnLogin;
    private Button btnToolBar;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textView);

        /**
         * BASE DE DATOS
         */
        //Abrimos la base de datos 'DBUsuarios' en modo escritura
        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, "DBUsuarios", null, 1);

        SQLiteDatabase db = usdbh.getWritableDatabase();

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


//        btnBotones = (Button)findViewById(R.id.btnBotones);
//        btnBotones.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View arg0) {
//                Intent intent = new Intent(Activity_Main.this, Activity_Botones.class);
//                startActivity(intent);
//            }
//        });

//        btnLogin = (Button)findViewById(R.id.btnLogin);
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View arg0)
//            {
//                Intent intent = new Intent(Activity_Main.this, Activity_Login.class);
//                startActivity(intent);
//            }
//        });
    }

    /**
    MENU TOOLBAR
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        uMenu menu;
//        menu = new uMenu();
//        if(!menu.uMenuToolBar_onOptionsItemSelected(item))
//        {
//            return super.onOptionsItemSelected(item);
//        }
//
//
//
//        return true;

        Intent i;

        switch (item.getItemId()) {
            case R.id.itmTakePhoto:
                Log.i("onOptionsItemSelected", "itmTakePhoto");
                return true;
            case R.id.itmGallery:
                Log.i("onOptionsItemSelected", "itmGallery");
                return true;
            case R.id.itmSettings:
                Log.i("onOptionsItemSelected", "itmSettings");
                i = new Intent(Activity_Main.this, Activity_Settings.class);
                startActivity(i);
                return true;
            case R.id.itmHelp:
                Log.i("onOptionsItemSelected", "itmHelp");
                i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://google.com/"));
                startActivity(i);
                return true;
            case R.id.itmAbout:
                Log.i("onOptionsItemSelected", "itmAbout");
                i = new Intent(Activity_Main.this, Activity_About.class);
                startActivity(i);
                return true;
            case R.id.itmExit:
                Log.i("onOptionsItemSelected", "itmExit");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
