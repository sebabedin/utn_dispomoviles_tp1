package com.example.seb.aplicacion.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seb.aplicacion.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import extern.UsuariosSQLiteHelper;

public class Activity_Take extends AppCompatActivity {

    private final String DEBUG_TAG = "Take";
    private final int GPS_PROVIDER_CYCLETIME = 5000;
    private final String MSG_CAMPO_FOTO_VACIO = "Campo 'Foto' vacio :(";
    private final String MSG_PROCESO_OK = "Nueva captura :)";
    private final String MSG_PROCESO_ERROR = "Error";

    private Button viewProcesar;
    private Button viewCapturar;
    private Button viewCopiar;

    private ImageView   imgImagen;

    public SQLiteDatabase db;
    final static int cons = 0;
    Bitmap bmp;
    Bundle ext;
    String ImagenNombre;
    String ImagenPath;

    private SharedPreferences appSetting;
    private boolean sett_gps, sett_imu, com;

    private LocationManager locManager;
    private LocationListener locListener;

    private FormularioData formData;
    private GPSData gpsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take);

        gpsData = new GPSData();
        formData = new FormularioData();
        formData.setGPS(gpsData);

        /**
         * Preferencias
         */
        appSetting = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        sett_gps = appSetting.getBoolean("gps", true);
        sett_imu = appSetting.getBoolean("gps", true);
        com = appSetting.getBoolean("com", true);
        if(sett_gps) {
            GPSLocation_Init();
        }

//        intxtFoto   = (EditText) findViewById(R.id.intxtFoto);
        imgImagen   = (ImageView) findViewById(R.id.imagen);

        /**
         * Base de datos
         */
        Log.d("__TAKE__", "Abriendo la base de datos...");
        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, "DB", null, 1);
        db = usdbh.getWritableDatabase();
        if (db == null) {
            Log.e("__TAKE__", "[Error]");
            db.close();
        } else {
            Log.d("__TAKE__", "[OK]");
        }

        viewCapturar = (Button) findViewById(R.id.viewForm_Capturar);
        viewCapturar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                viewCapturar_OnClick(arg0);
            }
        });
        viewCopiar = (Button) findViewById(R.id.viewForm_Copiar);
        viewCopiar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                viewCopiar_OnClick(arg0);
            }
        });
        viewProcesar = (Button) findViewById(R.id.viewForm_Procesar);
        viewProcesar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                viewProcesar_OnClick(arg0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity_Take.RESULT_OK == resultCode) {
            ext = data.getExtras();
        }
        bmp = (Bitmap) ext.get("data");
        imgImagen.setImageBitmap(bmp);
        formData.setFoto(null);
        formData.GPSRefresh();
    }

    /**
     * Localización
     */
    private void GPSLocation_Init()
    {
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                GPS_PROVIDER_CYCLETIME,
                0,
                new LocationListener() {
                    public void onLocationChanged(Location loc) {
                        if (loc != null) {
                            String lat = String.valueOf(loc.getLatitude());
                            String lon = String.valueOf(loc.getLongitude());
                            Log.i("", "GPS lat:" + lat);
                            Log.i("", "GPS lon:" + lon);
                            gpsData.setPos(lat, lon);
                        } else {
                            Log.i("", "GPS no locatio");
                            gpsData.setPos(null, null);
                        }
                    }

                    public void onProviderDisabled(String provider) {
                        Log.i("", "GPS OFF");
                        gpsData.Disabled();
                    }

                    public void onProviderEnabled(String provider) {
                        Log.i("", "GPS ON");
                        gpsData.Enabled();
                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.i("", "GPS status: " + status);
                        gpsData.Status(status);
                    }
                });
    }

    public void viewCapturar_OnClick(View arg0) {
        Log.d(DEBUG_TAG, "viewCapturar_OnClick");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, cons);
    }

    public void viewCopiar_OnClick(View arg0) {
        Log.d(DEBUG_TAG, "viewCopiar_OnClick");
        formData.GPSRefresh();
    }

    private boolean Form_FieldsCheck() {
        boolean ret = true;
        if (formData.strFoto.isEmpty()) {
            Log.d(DEBUG_TAG, MSG_CAMPO_FOTO_VACIO);
            Toast.makeText(Activity_Take.this, MSG_CAMPO_FOTO_VACIO, Toast.LENGTH_SHORT).show();
            ret = false;
        }
        return ret;
    }

    private boolean SD_Check() {
        boolean ret = false;
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED))
        {
            Log.d(DEBUG_TAG, "SD_Check: ok " + Environment.MEDIA_MOUNTED);
            ret = true;
        }
        else if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
        {
            Log.d(DEBUG_TAG, "SD_Check: error " + Environment.MEDIA_MOUNTED_READ_ONLY);
        }
        else
        {
            Log.d(DEBUG_TAG, "SD_Check: error other");
        }
        return ret;
    }

    private boolean SD_BMPSave() {
        boolean ret = false;
        if(SD_Check()) {
            try {
//                File ruta_sd_global = Environment.getExternalStorageDirectory();
                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), formData.getFoto());
                Log.d(DEBUG_TAG, "SD_BMPSave: " + f.toString());
                FileOutputStream outStream = new FileOutputStream(f);

                bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

                outStream.flush();
                outStream.close();

//                Log.d("__TAKE__", "[OK]");
//                sdImgGuardada = true;

                ret = true;
            }
            catch (Exception ex) {
                Log.d(DEBUG_TAG, "SD_BMPSave: error catch");
                return false;
            }
        }
        else {
            Log.d(DEBUG_TAG, "SD_BMPSave: error SD_Check");
        }
        return ret;
    }

    private boolean DB_Insert() {
        boolean ret = false;
        Log.d(DEBUG_TAG, "DB_Insert");

//        boolean dbCapturaNueva = false;
//                    String SQL_cmd;
//                    SQL_cmd = "INSERT INTO capturas (" +
//                            "cap_Foto, cap_IMU, cap_GPS, cap_GPS_Lon, cap_GPS_Lat" +
//                            ") VALUES (" +
//                            "'" + ImagenPath + "'," +
//                            (sett_imu ? 1 : 0) + "," +
//                            (sett_gps ? 1 : 0) + "," +
//                            "'" + gpsData.strLat + "'," +
//                            "'" + gpsData.strLon + ")";
//                    Log.d("__TAKE__", SQL_cmd);
//                    db.execSQL(SQL_cmd);
//                    Log.d("__TAKE__", "[OK]");
//            dbCapturaNueva = true;

            ContentValues values = new ContentValues();
            values.put("cap_Foto", formData.getFoto());
            values.put("cap_IMU", (sett_imu ? 1 : 0));
            values.put("cap_GPS", (sett_gps ? 1 : 0));
            values.put("cap_GPS_Lat", formData.getLat());
            values.put("cap_GPS_Lon", formData.getLon());
            if(-1 == db.insert("capturas", null, values)) {
                Log.d(DEBUG_TAG, "DB_Insert: error");
            } else {
                ret = true;
            }
//                    Cursor c = db.query("capturas", campos, null, args, null, null, null);
//                    Log.d("__MAIN__", "[OK]");

        return ret;
    }

    public void viewProcesar_OnClick(View arg0) {
        Log.d(DEBUG_TAG, "viewProcesar_OnClick");

        if(!Form_FieldsCheck())
            return;

        if(!SD_BMPSave()) {
            Log.d(DEBUG_TAG, "viewProcesar_OnClick: SD_BMPSave");
            Toast.makeText(Activity_Take.this, MSG_PROCESO_ERROR, Toast.LENGTH_SHORT).show();
            return;
        }

        if(!DB_Insert()) {
            Log.d(DEBUG_TAG, "viewProcesar_OnClick: DB_Insert");
            Toast.makeText(Activity_Take.this, MSG_PROCESO_ERROR, Toast.LENGTH_SHORT).show();
            return;
        }

        imgImagen.setImageBitmap(null);
        Log.d(DEBUG_TAG, MSG_PROCESO_OK);
        Toast.makeText(Activity_Take.this, MSG_PROCESO_OK, Toast.LENGTH_SHORT).show();
    }

    /**
     * Formulario Data
     */
    private class FormularioData
    {
        private EditText viewLon, viewLat, viewFoto;
        private Button viewCopiar;
        private GPSData gps;

        public String strLat, strLon, strFoto;

        /**
         * Public
         */
        public FormularioData()
        {
            this.viewInit();
        }

        public void Refresh()
        {
            this.viewRefresh();
        }

        public void setGPS(GPSData gps)
        {
            this.gps = gps;
        }

        public void GPSRefresh()
        {
            this.strLat = this.gps.strLat;
            this.strLon = this.gps.strLon;
            this.viewRefresh();
        }

        public String getLat()
        {
            return this.viewLat.getText().toString();
        }

        public String getLon()
        {
            return this.viewLon.getText().toString();
        }

        public String getFoto()
        {
            return this.viewFoto.getText().toString();
        }

        public void setFoto(String foto)
        {
            if(null == foto)
            {
                Date d = new Date();
                CharSequence s = DateFormat.format("yyyyMMddHHmmss", d.getTime());
                foto = s + ".jpg";
            }
            this.strFoto = foto;
            this.viewRefresh();
//            return this.viewFoto.getText().toString();
        }

        /**
         * Private
         */
        private void viewInit() {
            this.viewFoto = (EditText) findViewById(R.id.viewForm_Foto);
            this.viewLat = (EditText) findViewById(R.id.viewForm_Lat);
            this.viewLon = (EditText) findViewById(R.id.viewForm_Lon);
            this.viewCopiar = (Button) findViewById(R.id.viewForm_Copiar);
        }

        private void viewRefresh()
        {
            this.viewLat.setText(this.strLat);
            this.viewLon.setText(this.strLon);
            this.viewFoto.setText(this.strFoto);
        }
    }

    /**
     * GPS Data
     */
    private class GPSData
    {
        private TextView viewLon, viewLat, viewStatus, viewAccuracy;
        public String strLat, strLon, strStatus, strAccuracy;
        public Integer intStatus;
        public Boolean enable;

        /**
         * Public
         */
        public GPSData()
        {
            this.viewInit();
            this.setPos(null, null);
            this.Disabled();
            this.Status(0);
//            this.Refresh();
        }

        public void Disabled()
        {
            this.enable = false;
            this.Refresh();
        }

        public void Enabled()
        {
            this.enable = true;
            this.Refresh();
        }

        public void Status(int status)
        {
            this.intStatus = status;
            this.Refresh();
        }

        public void setPos(String lat, String lon)
        {
            if((null == lat) || (null == lon))
            {
                lat = "sin datos";
                lon = "sin datos";
            }
            this.strLat = lat;
            this.strLon = lon;
            this.Refresh();
        }

        public void Refresh()
        {
            this.viewRefresh();
        }

        /**
         * Private
         */
        private void viewInit() {
            this.viewLat      = (TextView) findViewById(R.id.viewGPS_Lat);
            this.viewLon      = (TextView) findViewById(R.id.viewGPS_Lon);
            this.viewStatus   = (TextView) findViewById(R.id.viewGPS_Status);
//            this.viewAccuracy = (TextView) findViewById(R.id.GPS_viewAccuracy);
        }

        private void viewRefresh()
        {
            this.viewLat.setText("Latitud: " + this.strLat);
            this.viewLon.setText("Longitud: " + this.strLon);
            this.viewStatus.setText("Estado: " + this.intStatus);
//            this.viewAccuracy.setText(this.strAccuracy);
        }
    }
}
