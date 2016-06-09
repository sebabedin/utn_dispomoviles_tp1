package com.example.seb.aplicacion.Activity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.seb.aplicacion.R;

import java.util.Timer;
import java.util.TimerTask;

public class Activity_Acelerometro extends AppCompatActivity {

    private SensorManager sensorManager;
    private TextView accelerationTextView;
    private TextView maxAccelerationTextView;

    private TextView gyro_ang1View;
    private TextView gyro_ang2View;
    private TextView gyro_ang3View;

    private float currentAcceleration = 0;
    private float maxAcceleration = 0;

    private final double calibration = SensorManager.STANDARD_GRAVITY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acelerometro);

        accelerationTextView = (TextView)findViewById(R.id.acceleration);
        maxAccelerationTextView = (TextView)findViewById(R.id.maxAcceleration);

        gyro_ang1View = (TextView)findViewById(R.id.gyro_ang1);
        gyro_ang2View = (TextView)findViewById(R.id.gyro_ang2);
        gyro_ang3View = (TextView)findViewById(R.id.gyro_ang3);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        Sensor accelerometer =
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorEventListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST);


//        SensorManager sm
//                = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
//        int sensorType = Sensor.TYPE_GYROSCOPE;

        Sensor gyrosensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);

        sensorManager.registerListener(myGyroListener,
                gyrosensor,
                SensorManager.SENSOR_DELAY_NORMAL);

        Timer updateTimer = new Timer("gForceUpdate");
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                updateGUI();
            }
        }, 0, 100);
    }

    final float nanosecondsPerSecond = 1.0f / 1000000000.0f;
    private long lastTime = 0;
    final float[] angle = new float[3];
    private final SensorEventListener myGyroListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (lastTime != 0) {
                final float dT = (sensorEvent.timestamp - lastTime) *
                        nanosecondsPerSecond;
                angle[0] += sensorEvent.values[0] * dT;
                angle[1] += sensorEvent.values[1] * dT;
                angle[2] += sensorEvent.values[2] * dT;
            }
            lastTime = sensorEvent.timestamp;
        }
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    @Override
    protected void onResume() {
        super.onResume();
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorEventListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(sensorEventListener);
        super.onPause();
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }
        public void onSensorChanged(SensorEvent event) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            double a = Math.round(Math.sqrt(Math.pow(x, 2) +
                    Math.pow(y, 2) +
                    Math.pow(z, 2)));
            currentAcceleration = Math.abs((float)(a-calibration));
            if (currentAcceleration > maxAcceleration)
                maxAcceleration = currentAcceleration;
        }
    };

    private void updateGUI() {
        runOnUiThread(new Runnable() {
            public void run() {
                String currentG = currentAcceleration/SensorManager.STANDARD_GRAVITY
                        + "Gs";
                accelerationTextView.setText(currentG);
                accelerationTextView.invalidate();

                String maxG = maxAcceleration/SensorManager.STANDARD_GRAVITY + "Gs";
                maxAccelerationTextView.setText(maxG);
                maxAccelerationTextView.invalidate();

                String salida;
                salida = angle[0] + "rad?";
                gyro_ang1View.setText(salida);
                gyro_ang1View.invalidate();

                salida = angle[1] + "rad?";
                gyro_ang2View.setText(salida);
                gyro_ang2View.invalidate();

                salida = angle[2] + "rad?";
                gyro_ang3View.setText(salida);
                gyro_ang3View.invalidate();
            }
        });
    };
}
