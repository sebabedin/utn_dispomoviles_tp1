package com.example.seb.aplicacion.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// agregadas
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.seb.aplicacion.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import extern.UsuariosSQLiteHelper;

public class Activity_Main extends AppCompatActivity {

    private Button btnBotones;
    private Button btnLogin;
    private Button btnToolBar;
    private TextView textView;

    private ImageView img;
    Bitmap bmp;

    private ViewGroup layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Menu contextual
         * TODO: agregar
         */

        /**
         * BASE DE DATOS
         */
        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, "DB", null, 1);
        SQLiteDatabase db = usdbh.getWritableDatabase();
        if (db == null) {
            Log.e("__MAIN__", "[Error]");
            db.close();
            // FIXME: cerrar aplicación
        } else {
            Log.d("__MAIN__", "[OK]");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_main);

        layout = (ViewGroup) findViewById(R.id.contenido);

        /**
         * BASE DE DATOS
         */
        Log.d("__MAIN__", "Busqueda ...");
        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, "DB", null, 1);
        SQLiteDatabase db = usdbh.getWritableDatabase();
        if (db == null) {
            Log.e("__MAIN__", "[Error]");
            db.close();
            // FIXME: cerrar aplicación
        } else {
            Log.d("__MAIN__", "[OK]");
        }
        String[] campos = new String[] {"cap_ID", "cap_Foto", "cap_Fecha", "cap_GPS_Lat", "cap_GPS_Lon"};
        String[] args = null;
        Cursor c = db.query("capturas", campos, null, args, null, null, null);
        Log.d("__MAIN__", "[OK]");

        LayoutInflater inflater = LayoutInflater.from(this);
        int id = R.layout.activity_main_item;

        if (c.moveToFirst()) {
            do {
                Log.d("__MAIN__", "Nuevo item");
                RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(id, null, false);
                relativeLayout.getId();

                TextView viewID     = (TextView) relativeLayout.findViewById(R.id.id);
                TextView viewFecha  = (TextView) relativeLayout.findViewById(R.id.fecha);
                TextView viewLat    = (TextView) relativeLayout.findViewById(R.id.lat);
                TextView viewLon    = (TextView) relativeLayout.findViewById(R.id.lon);
                ImageView imageView = (ImageView) relativeLayout.findViewById(R.id.imagen);

                String strID        = c.getString(0);
                String strNombre    = c.getString(1);
                String strFecha     = c.getString(2);
                String strLat       = c.getString(3);
                String strLon       = c.getString(4);

                viewID.setText(strID);
                viewFecha.setText(strFecha);
                viewLat.setText(strLat);
                viewLon.setText(strLon);

                Log.d("__MAIN__", "Texto");

                Uri output = Uri.fromFile(new File(strNombre));
                InputStream is;
                try {
                    Log.d("__MAIN__", "Carga imagen ...");
                    is = getContentResolver().openInputStream(output);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    Bitmap bitmap = BitmapFactory.decodeStream(bis);
                    imageView.setImageBitmap(bitmap);
                    Log.d("__MAIN__", "[OK]");
                } catch (FileNotFoundException e) {}

                layout.addView(relativeLayout);

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

            case R.id.itmLocalizacion:
                Log.i("onOptionsItemSelected", "itmLocalizacion");
                i = new Intent(Activity_Main.this, Activity_Localizacion.class);
                startActivity(i);
                return true;

            case R.id.itmAcelerometro:
                Log.i("onOptionsItemSelected", "itmAcelerometro");
                i = new Intent(Activity_Main.this, Activity_Acelerometro.class);
                startActivity(i);
                return true;

            case R.id.itmCompas:
                Log.i("onOptionsItemSelected", "itmCompas");
                i = new Intent(Activity_Main.this, Activity_Compas.class);
                startActivity(i);
                return true;

            case R.id.itmVisual:
                Log.i("onOptionsItemSelected", "itmVisual");
                i = new Intent(Activity_Main.this, visual.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
