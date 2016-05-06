package com.example.seb.aplicacion.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// agregadas
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.seb.aplicacion.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import extern.UsuariosSQLiteHelper;
import uIO.uMenu;

public class Activity_Main extends AppCompatActivity {

    private Button btnBotones;
    private Button btnLogin;
    private Button btnToolBar;
    private TextView textView;

    private ImageView img;
    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textView);
        img = (ImageView) findViewById(R.id.imagen);

        /**
         * BASE DE DATOS
         */
        //Abrimos la base de datos 'DBUsuarios' en modo escritura
        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, "DB", null, 1);

        SQLiteDatabase db = usdbh.getWritableDatabase();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textView);
        img = (ImageView) findViewById(R.id.imagen);

        /**
         * BASE DE DATOS
         */
        //Abrimos la base de datos 'DBUsuarios' en modo escritura
        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, "DB", null, 1);

        SQLiteDatabase db = usdbh.getWritableDatabase();

        String[] campos = new String[] {"cap_ID", "cap_Foto"};
        String[] args = new String[] {"usu1"};

        Cursor c = db.query("capturas", campos, null, null, null, null, null);

        if (c.moveToFirst()) {
            do {
                String codigo = c.getString(0);
                String nombre = c.getString(1);
                String salida = textView.getText().toString() + "\r\n" +
                        codigo + " - " +
                        nombre;
                textView.setText(salida.toString());

                Uri output = Uri.fromFile(new File(nombre));
//                img.setImageURI(output);

                InputStream is;
                try {
                    is = getContentResolver().openInputStream(output);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    Bitmap bitmap = BitmapFactory.decodeStream(bis);
                    img.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {}

                Log.i("SQLiteDatabase", codigo);
                Log.i("SQLiteDatabase", nombre);
                Log.i("SQLiteDatabase", salida);
            } while(c.moveToNext());
        }
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
//        return true;

        Intent i;

        switch (item.getItemId()) {
            case R.id.itmTakePhoto:
                Log.i("onOptionsItemSelected", "itmTakePhoto");
                i = new Intent(Activity_Main.this, Activity_Take.class);
                startActivity(i);
                return true;
            case R.id.itmExport:
                Log.i("onOptionsItemSelected", "itmExport");
                i = new Intent(Activity_Main.this, Activity_Exportar.class);
                startActivity(i);
                return true;
            case R.id.itmSettings:
                Log.i("onOptionsItemSelected", "itmSettings");
                i = new Intent(Activity_Main.this, Activity_Settings.class);
                startActivity(i);
                return true;
            case R.id.itmClean:
                Log.i("onOptionsItemSelected", "itmExport");
                i = new Intent(Activity_Main.this, Activity_Clean.class);
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
