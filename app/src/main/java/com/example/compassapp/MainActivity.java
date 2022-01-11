package com.example.compassapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Loader;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    float azimuth_angle;
    private SensorManager compassSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    TextView tv_degrees;
    ImageView iv_compass;
    float[] accel_read;
    float[] magnetic_read;
    private float current_degree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compassSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        get accelerometer hardware from device
        accelerometer = compassSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        get magnetometer hardware from device
        magnetometer = compassSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onResume() {
//        the sensor listeners are registered meaning
//        that the sensors are powered on again when the activity resumes
        super.onResume();
        compassSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        compassSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
//        the sensors are unregistered (disconnected) in the onPause()
//method when the activity pauses
        super.onPause();
        compassSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        tv_degrees = (TextView) findViewById(R.id.tv_degrees);
        iv_compass = (ImageView) findViewById(R.id.iv_compass);
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accel_read = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            magnetic_read = event.values;
        }
        if (accel_read != null && magnetic_read != null){
            float R[] = new float[9];
            float I[] = new float[9];
//            to get the rotation matrix R of the device as
//            follows
            boolean successful_read = SensorManager.getRotationMatrix(R, I, accel_read, magnetic_read);
//        If this operation is successful, the successful_read variable will be
//        true and the rotation matrix will be stored in the variable R
            if (successful_read){
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth_angle = orientation[0];
                float degrees = ((azimuth_angle * 180f) / 3.14f);
                int degreesInt = Math.round(degrees);
                tv_degrees.setText(Integer.toString(degreesInt) + (char) 0x00B0 + "To absolute North");
//                Declare a RotateAnimation object to Rotate the image on imageView
            RotateAnimation rotate = new RotateAnimation(current_degree, -degreesInt, Animation.RELATIVE_TO_SELF,
                    0.5f, Animation.RELATIVE_TO_SELF, 0.0f);
//            set the animation Duration
            rotate.setDuration(100);
            rotate.setFillAfter(true);
//            rotate the imageview
            iv_compass.startAnimation(rotate);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}