package com.example.seb.aplicacion.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seb.aplicacion.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;

import extern.UsuariosSQLiteHelper;

public class Activity_Take extends AppCompatActivity {

    private final String DEBUG_TAG              = "Take";
    private final int GPS_PROVIDER_CYCLETIME    = 5000;
    private final String MSG_CAMPO_FOTO_VACIO   = "Campo 'Foto' vacio :(";
    private final String MSG_PROCESO_OK         = "Nueva captura :)";
    private final String MSG_PROCESO_ERROR      = "Error";

    private Button viewProcesar;
    private Button viewCapturar;
    private Button viewCopiar;

    private ImageView   imgImagen;

    final static int cons = 0;
    Bitmap bmp;
    Bundle ext;
//    String ImagenNombre;
//    String ImagenPath;

    private SharedPreferences appSetting;
    private boolean sett_gps, sett_imu, com;

    private LocationManager locManager;

    private FormularioData formData;
    private GPSData gpsData;
    private CompasData compasData;
    private IMUData imuData;
    private SettingData sett;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take);

        gpsData = new GPSData();
        compasData = new CompasData();
        imuData = new IMUData();
        formData = new FormularioData();
        formData.setGPS(gpsData);
        formData.setCompas(compasData);
        formData.setIMU(imuData);

        /**
         * Preferencias
         */
        sett = new SettingData();
        if(sett.gps) {
            GPSLocation_Init();
        }

        LinearLayout viewFormGPSDebug = (LinearLayout) findViewById(R.id.viewForm_GPSDebug);
        LinearLayout viewFormComDebug = (LinearLayout) findViewById(R.id.viewForm_IMUDebug);
        LinearLayout viewFormIMUDebug = (LinearLayout) findViewById(R.id.viewForm_ComDebug);
        int visible = View.GONE;
        if(sett.debug) {
            visible = View.VISIBLE;
        }
        viewFormGPSDebug.setVisibility(visible);
        viewFormComDebug.setVisibility(visible);
        viewFormIMUDebug.setVisibility(visible);

        if(!sett.prefijo.isEmpty()) {
            EditText viewFoto = (EditText) findViewById(R.id.viewForm_Foto);
            viewFoto.setText(sett.prefijo);
        }

        imgImagen   = (ImageView) findViewById(R.id.imagen);

        DB_Init();

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

//    @Override
//    protected void onStart() {
//        super.onStart();
//        setContentView(R.layout.activity_take);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        compasData.onResume();
    }

    @Override
    protected void onPause() {
        compasData.onPause();
        super.onPause();
    }

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
                i = new Intent(Activity_Take.this, Activity_Main.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
//        formData.GPSRefresh();
//        formData.CompasRefresh();
//        formData.IMURefresh();
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                compasData.aValues = event.values;
                imuData.accX = event.values[0];
                imuData.accY = event.values[1];
                imuData.accZ = event.values[2];
                imuData.Refresh();
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            {
                compasData.mValues = event.values;
                compasData.updateOrientation(compasData.calculateOrientation());
            }
        }
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    /*******************************************************************************
     * OnClick
     *******************************************************************************/
    public void viewCapturar_OnClick(View arg0) {
        Log.d(DEBUG_TAG, "viewCapturar_OnClick");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, cons);
    }

    public void viewCopiar_OnClick(View arg0) {
        Log.d(DEBUG_TAG, "viewCopiar_OnClick");
        formData.GPSRefresh();
        formData.CompasRefresh();
        formData.IMURefresh();
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

    /*******************************************************************************
     * Localizacion
     *******************************************************************************/
    private void GPSLocation_Init() {
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

    /*******************************************************************************
     * SD
     *******************************************************************************/
    private boolean SD_Check() {
        boolean ret = false;
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            Log.d(DEBUG_TAG, "SD_Check: ok " + Environment.MEDIA_MOUNTED);
            ret = true;
        } else if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            Log.d(DEBUG_TAG, "SD_Check: error " + Environment.MEDIA_MOUNTED_READ_ONLY);
        } else {
            Log.d(DEBUG_TAG, "SD_Check: error other");
        }
        return ret;
    }

    private boolean SD_BMPSave() {
        boolean ret = false;
        if(SD_Check()) {
            try {
                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), sett.prefijo + formData.getFoto());
                Log.d(DEBUG_TAG, "SD_BMPSave: " + f.toString());
                FileOutputStream outStream = new FileOutputStream(f);

                bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

                outStream.flush();
                outStream.close();

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

    private boolean DB_Insert() {
        boolean ret = false;
        Log.d(DEBUG_TAG, "DB_Insert");
        ContentValues values = new ContentValues();
        values.put("cap_Foto", sett.prefijo + formData.getFoto());
        values.put("cap_IMU", (sett_imu ? 1 : 0));
        values.put("cap_GPS", (sett_gps ? 1 : 0));
        values.put("cap_GPS_Lat", formData.getLat());
        values.put("cap_GPS_Lon", formData.getLon());

        values.put("cap_COM_Y", formData.getComY());
        values.put("cap_COM_P", formData.getComP());
        values.put("cap_COM_R", formData.getComR());

        values.put("cap_IMU_Gyro_Y", formData.getGyroY());
        values.put("cap_IMU_Gyro_P", formData.getGyroP());
        values.put("cap_IMU_Gyro_R", formData.getGyroR());

        values.put("cap_IMU_Acc_X", formData.getAccX());
        values.put("cap_IMU_Acc_Y", formData.getAccY());
        values.put("cap_IMU_Acc_Z", formData.getAccZ());

        if(-1 == db.insert("capturas", null, values)) {
            Log.d(DEBUG_TAG, "DB_Insert: error");
        } else {
            ret = true;
        }
        return ret;
    }

    /*******************************************************************************
    * Formulario Data
    *******************************************************************************/
    private boolean Form_FieldsCheck() {
        boolean ret = true;
        if (formData.strFoto.isEmpty()) {
            Log.d(DEBUG_TAG, MSG_CAMPO_FOTO_VACIO);
            Toast.makeText(Activity_Take.this, MSG_CAMPO_FOTO_VACIO, Toast.LENGTH_SHORT).show();
            ret = false;
        }
        return ret;
    }

    private class FormularioData
    {
        private EditText viewLon, viewLat, viewFoto;
        private EditText viewComY, viewComP, viewComR;
        private EditText viewGyroY, viewGyroP, viewGyroR;
        private EditText viewAccX, viewAccY, viewAccZ;
        private GPSData gps;
        private CompasData compas;
        private IMUData imu;

        public String strLat, strLon, strFoto;
        public String strComR, strComP, strComY;
        public String strGyroR, strGyroP, strGyroY, strAccX, strAccY, strAccZ;

        /** Public */
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

        public void setCompas(CompasData compas)
        {
            this.compas = compas;
        }

        public void setIMU(IMUData imu)
        {
            this.imu = imu;
        }

        public void GPSRefresh()
        {
            this.strLat = this.gps.strLat;
            this.strLon = this.gps.strLon;
            this.viewRefresh();
        }

        public void CompasRefresh()
        {
            this.strComY = this.compas.strY;
            this.strComP = this.compas.strP;
            this.strComR = this.compas.strR;
            this.viewRefresh();
        }

        public void IMURefresh()
        {
            this.strGyroY = this.imu.strGyroY;
            this.strGyroP = this.imu.strGyroP;
            this.strGyroR = this.imu.strGyroR;
            this.strAccX = this.imu.strAccX;
            this.strAccY = this.imu.strAccY;
            this.strAccZ = this.imu.strAccZ;
            this.viewRefresh();
        }
        public String getLat() {
            return this.viewLat.getText().toString();
        }

        public String getLon() {
            return this.viewLon.getText().toString();
        }

        public String getFoto() {
            return this.viewFoto.getText().toString();
        }

        public String getComY() {
            return this.viewComY.getText().toString();
        }
        public String getComP() {
            return this.viewComP.getText().toString();
        }
        public String getComR() {
            return this.viewComR.getText().toString();
        }
        public String getGyroY() {
            return this.viewGyroY.getText().toString();
        }
        public String getGyroP() {
            return this.viewGyroP.getText().toString();
        }
        public String getGyroR() {
            return this.viewGyroR.getText().toString();
        }
        public String getAccX() {
            return this.viewAccX.getText().toString();
        }
        public String getAccY() {
            return this.viewAccY.getText().toString();
        }
        public String getAccZ() {
            return this.viewAccZ.getText().toString();
        }

        public void setFoto(String foto) {
            if(null == foto)
            {
                Date d = new Date();
                CharSequence s = DateFormat.format("yyyyMMddHHmmss", d.getTime());
                foto = s + ".jpg";
            }
            this.strFoto = foto;
            this.viewRefresh();
        }

        /** Private */
        private void viewInit() {
            this.viewFoto = (EditText) findViewById(R.id.viewForm_Foto);
            this.viewLat = (EditText) findViewById(R.id.viewForm_Lat);
            this.viewLon = (EditText) findViewById(R.id.viewForm_Lon);

            this.viewComY = (EditText) findViewById(R.id.viewForm_ComY);
            this.viewComP = (EditText) findViewById(R.id.viewForm_ComP);
            this.viewComR = (EditText) findViewById(R.id.viewForm_ComR);

            this.viewGyroY = (EditText) findViewById(R.id.viewForm_GyroY);
            this.viewGyroP = (EditText) findViewById(R.id.viewForm_GyroP);
            this.viewGyroR = (EditText) findViewById(R.id.viewForm_GyroR);

            this.viewAccX = (EditText) findViewById(R.id.viewForm_AccX);
            this.viewAccY = (EditText) findViewById(R.id.viewForm_AccY);
            this.viewAccZ = (EditText) findViewById(R.id.viewForm_AccZ);
        }

        private void viewRefresh()
        {
            this.viewLat.setText(this.strLat);
            this.viewLon.setText(this.strLon);
            this.viewFoto.setText(this.strFoto);

            this.viewComY.setText(this.strComY);
            this.viewComP.setText(this.strComP);
            this.viewComR.setText(this.strComR);

            this.viewGyroY.setText(this.strGyroY);
            this.viewGyroP.setText(this.strGyroP);
            this.viewGyroR.setText(this.strGyroR);

            this.viewAccX.setText(this.strAccX);
            this.viewAccY.setText(this.strAccY);
            this.viewAccZ.setText(this.strAccZ);

        }
    }

    /*******************************************************************************
     * GPS Data
     *******************************************************************************/
    private class GPSData
    {
        private TextView viewLon, viewLat, viewStatus;
        public String strLat, strLon;
        public Integer intStatus;
        public Boolean enable;

        /** Public */
        public GPSData()
        {
            this.viewInit();
            this.setPos(null, null);
            this.Disabled();
            this.Status(0);
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

        /** Private */
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

    /*******************************************************************************
     * Compas
     *******************************************************************************/
    private class CompasData
    {
        private TextView viewR, viewP, viewY;
        public String strR, strP, strY;

        private SensorManager sensorManager;
        private int rotation;

        public void onResume() {
            Sensor magField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(sensorEventListener, magField, SensorManager.SENSOR_DELAY_FASTEST);

            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }

        public void onPause() {
            sensorManager.unregisterListener(sensorEventListener);
        }

        /** Public */
        public CompasData()
        {
            this.viewInit();

            sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
            String windoSrvc = Context.WINDOW_SERVICE;
            WindowManager wm = ((WindowManager) getSystemService(windoSrvc));
            Display display = wm.getDefaultDisplay();
            rotation = display.getRotation();
            updateOrientation(new float[]{0, 0, 0});
        }

        private void updateOrientation(float[] values) {
            this.strY = String.valueOf(values[0]);
            this.strP = String.valueOf(values[1]);
            this.strR = String.valueOf(values[2]);
            this.Refresh();
        }

        private float[] aValues = new float[3];
        private float[] mValues = new float[3];

        private float[] calculateOrientation() {
            float[] values = new float[3];
            float[] inR = new float[9];
            float[] outR = new float[9];
            // Determine the rotation matrix
            SensorManager.getRotationMatrix(inR, null, aValues, mValues);
            // Remap the coordinates based on the natural device orientation.
            int x_axis = SensorManager.AXIS_X;
            int y_axis = SensorManager.AXIS_Y;
            switch (rotation) {
                case (Surface.ROTATION_90):
                    x_axis = SensorManager.AXIS_Y;
                    y_axis = SensorManager.AXIS_MINUS_X;
                    break;
                case (Surface.ROTATION_180):
                    y_axis = SensorManager.AXIS_MINUS_Y;
                    break;
                case (Surface.ROTATION_270):
                    x_axis = SensorManager.AXIS_MINUS_Y;
                    y_axis = SensorManager.AXIS_X;
                    break;
                default: break;
            }
            SensorManager.remapCoordinateSystem(inR, x_axis, y_axis, outR);
            // Obtain the current, corrected orientation.
            SensorManager.getOrientation(outR, values);
            // Convert from Radians to Degrees.
            values[0] = (float) Math.toDegrees(values[0]);
            values[1] = (float) Math.toDegrees(values[1]);
            values[2] = (float) Math.toDegrees(values[2]);
            return values;
        }

        public void Refresh()
        {
            this.viewRefresh();
        }

        /** Private */
        private void viewInit() {
            this.viewY = (TextView) findViewById(R.id.viewCom_Y);
            this.viewP = (TextView) findViewById(R.id.viewCom_P);
            this.viewR = (TextView) findViewById(R.id.viewCom_R);
        }

        private void viewRefresh()
        {
            this.viewY.setText("Yaw: " + this.strY);
            this.viewP.setText("Pitch: " + this.strP);
            this.viewR.setText("Roll: " + this.strR);
        }
    }

    /*******************************************************************************
     * IMU
     *******************************************************************************/
    private class IMUData
    {
        private TextView viewGyroR, viewGyroP, viewGyroY, viewAccX, viewAccY, viewAccZ;
        public String strGyroR, strGyroP, strGyroY, strAccX, strAccY, strAccZ;
        public double accX, accY, accZ;

        private SensorManager sensorManager;
        private float currentAcceleration = 0;
        private float maxAcceleration = 0;
        private final double calibration = SensorManager.STANDARD_GRAVITY;

        final float nanosecondsPerSecond = 1.0f / 1000000000.0f;
        private long lastTime = 0;
        final float[] angle = new float[3];

//        private SensorManager sensorManager;
//        private int rotation;

        public void onResume() {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }

        public void onPause() {
            sensorManager.unregisterListener(sensorEventListener);
        }

        /** Public */
        public IMUData()
        {
            this.viewInit();

            sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);

            Sensor gyrosensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);

            sensorManager.registerListener(myGyroListener, gyrosensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        private final SensorEventListener myGyroListener = new SensorEventListener() {
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (lastTime != 0) {
                    final float dT = (sensorEvent.timestamp - lastTime) * nanosecondsPerSecond;
                    angle[0] += sensorEvent.values[0] * dT;
                    angle[1] += sensorEvent.values[1] * dT;
                    angle[2] += sensorEvent.values[2] * dT;
                }
                lastTime = sensorEvent.timestamp;
            }
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

//        private void updateOrientation(float[] values) {
//            this.strY = String.valueOf(values[0]);
//            this.strP = String.valueOf(values[1]);
//            this.strR = String.valueOf(values[2]);
//            this.Refresh();
//        }

//        private float[] aValues = new float[3];
//        private float[] mValues = new float[3];

//        private float[] calculateOrientation() {
//            float[] values = new float[3];
//            float[] inR = new float[9];
//            float[] outR = new float[9];
//            // Determine the rotation matrix
//            SensorManager.getRotationMatrix(inR, null, aValues, mValues);
//            // Remap the coordinates based on the natural device orientation.
//            int x_axis = SensorManager.AXIS_X;
//            int y_axis = SensorManager.AXIS_Y;
//            switch (rotation) {
//                case (Surface.ROTATION_90):
//                    x_axis = SensorManager.AXIS_Y;
//                    y_axis = SensorManager.AXIS_MINUS_X;
//                    break;
//                case (Surface.ROTATION_180):
//                    y_axis = SensorManager.AXIS_MINUS_Y;
//                    break;
//                case (Surface.ROTATION_270):
//                    x_axis = SensorManager.AXIS_MINUS_Y;
//                    y_axis = SensorManager.AXIS_X;
//                    break;
//                default: break;
//            }
//            SensorManager.remapCoordinateSystem(inR, x_axis, y_axis, outR);
//            // Obtain the current, corrected orientation.
//            SensorManager.getOrientation(outR, values);
//            // Convert from Radians to Degrees.
//            values[0] = (float) Math.toDegrees(values[0]);
//            values[1] = (float) Math.toDegrees(values[1]);
//            values[2] = (float) Math.toDegrees(values[2]);
//            return values;
//        }

        public void Refresh()
        {
            this.strAccX = String.valueOf(imuData.accX);
            this.strAccY = String.valueOf(imuData.accY);
            this.strAccZ = String.valueOf(imuData.accZ);

            this.viewRefresh();
        }

        /** Private */
        private void viewInit() {
            this.viewGyroY = (TextView) findViewById(R.id.viewGyro_Y);
            this.viewGyroP = (TextView) findViewById(R.id.viewGyro_P);
            this.viewGyroR = (TextView) findViewById(R.id.viewGyro_R);
            this.viewAccX = (TextView) findViewById(R.id.viewAcc_X);
            this.viewAccY = (TextView) findViewById(R.id.viewAcc_Y);
            this.viewAccZ = (TextView) findViewById(R.id.viewAcc_Z);
//            accelerationTextView = (TextView)findViewById(R.id.acceleration);
//            maxAccelerationTextView = (TextView)findViewById(R.id.maxAcceleration);
//            gyro_ang1View = (TextView)findViewById(R.id.gyro_ang1);
//            gyro_ang2View = (TextView)findViewById(R.id.gyro_ang2);
//            gyro_ang3View = (TextView)findViewById(R.id.gyro_ang3);
        }

        private void viewRefresh() {
            this.viewGyroY.setText("Yaw: " + this.strGyroY);
            this.viewGyroP.setText("Pitch: " + this.strGyroP);
            this.viewGyroR.setText("Roll: " + this.strGyroR);
            this.viewAccX.setText("X: " + this.strAccX);
            this.viewAccY.setText("Y: " + this.strAccY);
            this.viewAccZ.setText("Z: " + this.strAccZ);
        }
    }

    /*******************************************************************************
     * Settings
     *******************************************************************************/
    private class SettingData
    {
        private boolean gps, imu, com, debug;
        public String prefijo;

        public SettingData()
        {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(Activity_Take.this);
            this.gps    = pref.getBoolean("gps", true);
            this.imu    = pref.getBoolean("imu", true);
            this.com    = pref.getBoolean("com", true);
            this.debug  = pref.getBoolean("debug", true);
            this.prefijo = pref.getString("prefijo", "");
        }
    }
}
