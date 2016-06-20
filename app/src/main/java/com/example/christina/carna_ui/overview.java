package com.example.christina.carna_ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by raphy-laptop on 20.06.2016.
 */
public class overview extends AppCompatActivity {

    enum SensorType {
        HEARTRATE,
        TEMPERATURE,
        STEPCOUNTER
    }

    BroadcastReceiver receiver;

    TextView mHeartRateTextView;
    TextView mStepCountTextView;
    TextView mTemperaturTextView;

    LinearLayout mHeartRateLayout;
    LinearLayout mTemperaturLayout;
    LinearLayout mStepCounterLayout;

    private String mBleDeviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        mHeartRateLayout = (LinearLayout)findViewById(R.id.heartRateLayout);
        mTemperaturLayout = (LinearLayout)findViewById(R.id.temperaturLayout);
        mStepCounterLayout = (LinearLayout)findViewById(R.id.stepCounterLayout);

        mHeartRateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoryView.class);
                intent.putExtra("Type",SensorType.HEARTRATE);
                startActivity(intent);
            }
        });

        mTemperaturLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoryView.class);
                intent.putExtra("Type",SensorType.TEMPERATURE);
                startActivity(intent);
            }
        });

        mStepCounterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoryView.class);
                intent.putExtra("Type",SensorType.STEPCOUNTER);
                startActivity(intent);
            }
        });

        mHeartRateTextView = (TextView)findViewById(R.id.heartRateTextView);
        mStepCountTextView = (TextView)findViewById(R.id.stepCounterTextView);
        mTemperaturTextView = (TextView)findViewById(R.id.temperatureTextView);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String s = intent.getStringExtra("SensorType");

                switch (s) {
                    case "Temperatur":

                        mTemperaturTextView.setText(""+intent.getFloatExtra("value",0));
                        break;
                    case "HeartRate":
                        mHeartRateTextView.setText(""+intent.getFloatExtra("value",0));
                        break;

                    case "StepCounter":
                        mStepCountTextView.setText(""+intent.getFloatExtra("value",0));
                        break;

                    case "BatteryLevel":
                        // todo
                        break;

                    default:
                        Toast.makeText(overview.this, "Unbekannte Sensor", Toast.LENGTH_SHORT).show();
                }

                // do something here.
            }
        };
    }

    // Wenn die App wieder gestartet wird (z.B. aus dem Standby, oder wenn es im Hintergrund offen war), dann verbindet es sich mit dem Angel Sensor
    protected void onStart() {
        super.onStart();

        Bundle extras = getIntent().getExtras();
        assert(extras != null);
        mBleDeviceAddress = extras.getString("ble_device_address");

        Intent intent = new Intent(this,ReceiverService.class);
        intent.putExtra("ble_device_address", extras.getString("ble_device_address"));
        startService(intent);

        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter("displayValues")
        );
    }

    // Wenn die App gestoppt wird, dann wird die Verbindung getrennt
    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
        //unscheduleUpdaters();
        //mBleDevice.disconnect();
    }

}
