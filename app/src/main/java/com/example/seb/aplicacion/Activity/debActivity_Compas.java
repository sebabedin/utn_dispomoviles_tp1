package com.example.seb.aplicacion.Activity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.seb.aplicacion.R;

public class debActivity_Compas extends AppCompatActivity {

    private float[] aValues = new float[3];
    private float[] mValues = new float[3];
//    private CompassView compassView;
    private SensorManager sensorManager;
    private int rotation;

    private TextView BearingTextView;
    private TextView PitchTextView;
    private TextView RollTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debactivity_compas);

        BearingTextView = (TextView)findViewById(R.id.Bearing);
        PitchTextView = (TextView)findViewById(R.id.Pitch);
        RollTextView = (TextView)findViewById(R.id.Roll);

//        compassView = (CompassView)findViewById(R.id.compassView);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        String windoSrvc = Context.WINDOW_SERVICE;
        WindowManager wm = ((WindowManager) getSystemService(windoSrvc));
        Display display = wm.getDefaultDisplay();
        rotation = display.getRotation();
        updateOrientation(new float[]{0, 0, 0});

//        Timer updateTimer = new Timer("gForceUpdate");
//        updateTimer.scheduleAtFixedRate(new TimerTask() {
//            public void run() {
//                updateGUI();
//            }
//        }, 0, 100);
    }

//    private void updateGUI() {
//        runOnUiThread(new Runnable() {
//            public void run() {
//                String currentG = currentAcceleration/SensorManager.STANDARD_GRAVITY
//                        + "Gs";
//                accelerationTextView.setText(currentG);
//                accelerationTextView.invalidate();
//                String maxG = maxAcceleration/SensorManager.STANDARD_GRAVITY + "Gs";
//                maxAccelerationTextView.setText(maxG);
//                maxAccelerationTextView.invalidate();
//            }
//        });
//    };

    private void updateOrientation(float[] values) {
//        if (compassView!= null) {
//            compassView.setBearing(values[0]);
//            compassView.setPitch(values[1]);
//            compassView.setRoll(-values[2]);
//            compassView.invalidate();
//        }
        String current;
        current = values[0] + " Bearing";
        BearingTextView.setText(current);
        BearingTextView.invalidate();

        current = values[1] + " Pitch";
        PitchTextView.setText(current);
        PitchTextView.invalidate();

        current = values[2] + " Roll";
        RollTextView.setText(current);
        RollTextView.invalidate();

    }

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

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                aValues = event.values;
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mValues = event.values;
            updateOrientation(calculateOrientation());
        }
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    @Override
    protected void onResume() {
        super.onResume();
        Sensor accelerometer
                = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(sensorEventListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(sensorEventListener,
                magField,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(sensorEventListener);
        super.onPause();
    }
}
