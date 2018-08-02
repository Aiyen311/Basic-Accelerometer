package srikurk.accelerometer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor accelerationSensor;
    private Sensor linearAccelerationSensor;
    private Sensor gravitySensor;

    private long startTime = 0;
    private long graphXAxis = 0;

    // Graph Lines
    private LineGraphSeries<DataPoint> accelerationSeries;

    // Acceleration Values
    private float accel_x;
    private float accel_y;
    private float accel_z;
    private float last_accel_x = 0;
    private float last_accel_y = 0;
    private float last_accel_z = 0;
    private float deltaAccel_x;
    private float deltaAccel_y;
    private float deltaAccel_z;

    // Text Views
    private TextView accelXText, accelYText, accelZText;

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, linearAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void startClicked(View view){

    }

    public void stopClicked(View view){

    }

    public void settingsClicked(View view){

    }

    // unregister
    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            accel_x = sensorEvent.values[0];
            accel_y = sensorEvent.values[1];
            accel_z = sensorEvent.values[2];

            deltaAccel_x = Math.abs(last_accel_x - sensorEvent.values[0]);
            deltaAccel_y = Math.abs(last_accel_x - sensorEvent.values[1]);
            deltaAccel_z = Math.abs(last_accel_x - sensorEvent.values[2]);

            last_accel_x = sensorEvent.values[0];
            last_accel_y = sensorEvent.values[1];
            last_accel_z = sensorEvent.values[2];
            
            // We should also check for noise data, meaning that the sensor is picking up random stuff
            // Thats why we have the deltaAccel variables. 
            // One simple way to improve the accuracy could be to apply different window functions. 
            // I will try to add this functionality when I can
            // Also need to add functionality to add the values to the graph using some kind of timer

            // Update the text views and the graph
            accelXText.setText(Float.toString(accel_x));
            accelYText.setText(Float.toString(accel_y));
            accelZText.setText(Float.toString(accel_z));

            // Add the values to the graph
            /*graphXAxis = (int) ((System.currentTimeMillis() - startTime) / 1000);
            accelerationSeries.appendData(new DataPoint(graphXAxis, accel_x), true, 40);
            accelerationSeries.appendData(new DataPoint(graphXAxis, accel_y), true, 40);
            accelerationSeries.appendData(new DataPoint(graphXAxis, accel_z), true, 40);*/

        }

        if(sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){

        }

        if(sensorEvent.sensor.getType() == Sensor.TYPE_GRAVITY){

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public static class CloseAppDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Your device does not support all the required hardware components!")
                    .setPositiveButton("CLOSE APP", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startTime = System.currentTimeMillis();

        // Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        if(!checkDevices()){
            FragmentManager fragmentManager = getSupportFragmentManager();
            CloseAppDialogFragment dialogFragment = new CloseAppDialogFragment();
            dialogFragment.show(fragmentManager, "hi");
        }

        /*GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.addSeries(accelerationSeries);*/

        accelXText = (TextView) findViewById(R.id.accelerationXTextView);
        accelYText = (TextView) findViewById(R.id.accelerationYTextView);
        accelZText = (TextView) findViewById(R.id.accelerationZTextView);
    }

    private boolean checkDevices(){

        sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        //Accelerometer
        accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Linear Acceleration
        linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        // Gravity
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        if((accelerationSensor == null) || (linearAccelerationSensor == null) || (gravitySensor == null)){
            return false;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportActionBar().setTitle("Srikur and Adithya's Accelerometer");
        //getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}