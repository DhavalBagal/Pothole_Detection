package com.android.citygroom;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BumpsActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorMan;
    private Sensor accelerometer;
    DatabaseReference rootref;

    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    long current_time, last_time, dt;
    float depth;
    TextView depth_txtbx;

    TextView acc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bumps);

        sensorMan = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        current_time = 0;
        last_time = 0;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        acc = findViewById(R.id.acc_value);

        depth_txtbx = findViewById(R.id.depth);

        rootref = FirebaseDatabase.getInstance().getReference("BUMPS");
        
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorMan.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorMan.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            mGravity = event.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float)(Math.sqrt(x*x + y*y + z*z));
            float delta = Math.abs(mAccelCurrent - mAccelLast);
            //last_time = current_time;

            if(delta > 10)
            {
                current_time = System.currentTimeMillis();
                dt = current_time - last_time;
                depth = (float)((mAccelCurrent + mAccelLast)*Math.pow(dt,2))/4;
                depth = depth/1000000;
                if(depth<0.1)
                depth_txtbx.setText(depth+"");

            }
            else
                last_time = System.currentTimeMillis();

            acc.setText(String.valueOf(mAccelCurrent));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // required method
    }
}
