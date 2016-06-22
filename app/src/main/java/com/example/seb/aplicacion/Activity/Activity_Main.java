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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    private final String DEBUG_TAG              = "Main";

//    private Button btnBotones;
//    private Button btnLogin;
//    private Button btnToolBar;
//    private TextView textView;
//
    private ImageView img;
//    Bitmap bmp;

    private ViewGroup layout;

//    private String[] menuItemListID;
//    private String[] menuItemListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DB_Init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_main);

        layout = (ViewGroup) findViewById(R.id.contenido);

        String[] campos = new String[] {"cap_ID", "cap_Foto", "cap_GPS_Lat", "cap_GPS_Lon"};
        String[] args = null;
        Cursor c = db.query("capturas", campos, null, args, null, null, null);

//        menuItemListID = new String[c.getCount()];
//        menuItemListName = new String[c.getCount()];

        LayoutInflater inflater = LayoutInflater.from(this);
        int id = R.layout.activity_main_item;
        if (c.moveToFirst()) {
            do {
                Log.d(DEBUG_TAG, "new item");
                RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(id, null, false);
                relativeLayout.getId();

                TextView viewFoto = (TextView) relativeLayout.findViewById(R.id.viewFoto);
                TextView viewLat = (TextView) relativeLayout.findViewById(R.id.viewLat);
                TextView viewLon = (TextView) relativeLayout.findViewById(R.id.viewLon);
                ImageView viewImagen = (ImageView) relativeLayout.findViewById(R.id.viewImagen);

                String strID = c.getString(0);
//                String strFoto = "[" + strID + "]" + c.getString(1);
                String strFoto = c.getString(1);
                String strLat = "Latitud: " + c.getString(2);
                String strLon = "Longitud: " + c.getString(3);

                Log.d(DEBUG_TAG, "cap_ID:" + strID);
                Log.d(DEBUG_TAG, "cap_Foto:" + strFoto);
                viewFoto.setText(strFoto);
                viewLat.setText(strLat);
                viewLon.setText(strLon);

                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), strFoto);
                Uri output = Uri.fromFile(f);
                InputStream is;
                try {
                    is = getContentResolver().openInputStream(output);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    Bitmap bitmap = BitmapFactory.decodeStream(bis);
                    viewImagen.setImageBitmap(bitmap);
                }
                catch (FileNotFoundException e) {
                    Log.d(DEBUG_TAG, "error");
                }

                layout.addView(relativeLayout);

//                menuItemListName[c.getPosition()] = strFoto;
//                menuItemListID[c.getPosition()] = strID;

//                registerForContextMenu(relativeLayout);
//                relativeLayout.setAdapter(adaptador);
//                relativeLayout.setAdapter(adaptador);

            } while(c.moveToNext());




//            //Rellenamos la lista con datos de ejemplo
//            String[] menuItemListID = new String[];
//            String[] menuItemListName = new String[];



//            lstLista.setAdapter(adaptador);
        }
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
//    {
//        super.onCreateContextMenu(menu, v, menuInfo);
//
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.item, menu);
//    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//
//        item.getOrder()
//
//        switch (item.getItemId()) {
//            case R.id.viewItemModificar:
////                lblMensaje.setText("Etiqueta: Opcion 1 pulsada!");
//                Log.d(DEBUG_TAG, "Etiqueta: Opcion 1 pulsada!" + info.position);
//                return true;
//            case R.id.viewItemEliminar:
////                lblMensaje.setText("Etiqueta: Opcion 2 pulsada!");
//                Log.d(DEBUG_TAG, "Etiqueta: Opcion 2 pulsada!" + info.position);
//                return true;
//            default:
//                return super.onContextItemSelected(item);
//        }
//    }

    /**
    MENU TOOLBAR
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*******************************************************************************
     * DB
     *******************************************************************************/
    public SQLiteDatabase db;

    private boolean DB_Init() {
        boolean ret = true;
        Log.d(DEBUG_TAG, "DB_Init");
        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, "DB", null, 1);
        db = usdbh.getWritableDatabase();
        if (db == null) {
            Log.d(DEBUG_TAG, "DB_Init error");
            ret = false;
            db.close();
        }
        return ret;
    }
}
