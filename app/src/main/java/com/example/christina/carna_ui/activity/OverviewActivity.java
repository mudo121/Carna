package com.example.christina.carna_ui.activity;

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

import com.example.christina.carna_ui.R;
import com.example.christina.carna_ui.database.AngelMemoUser;
import com.example.christina.carna_ui.enumclass.BroadcastIntentType;
import com.example.christina.carna_ui.enumclass.BroadcastIntentValueType;
import com.example.christina.carna_ui.enumclass.IntentValueType;
import com.example.christina.carna_ui.enumclass.SensorType;
import com.example.christina.carna_ui.service.ReceiverService;

/**
 * Created by raphy-laptop on 20.06.2016.
 */
public class OverviewActivity extends AppCompatActivity {


    BroadcastReceiver receiver;

    TextView mHeartRateTextView;
    TextView mStepCountTextView;
    TextView mTemperaturTextView;
    TextView mBatteryTextView;

    LinearLayout mHeartRateLayout;
    LinearLayout mTemperaturLayout;
    LinearLayout mStepCounterLayout;
    LinearLayout mBatteryLayout;

    private String mBleDeviceAddress;
    private AngelMemoUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        mHeartRateLayout = (LinearLayout)findViewById(R.id.heartRateLayout);
        mTemperaturLayout = (LinearLayout)findViewById(R.id.temperaturLayout);
        mStepCounterLayout = (LinearLayout)findViewById(R.id.stepCounterLayout);
        mBatteryLayout = (LinearLayout)findViewById(R.id.batteryLayout);

        mHeartRateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoryViewActivity.class);
                intent.putExtra(IntentValueType.SENSORTYPE.toString(), SensorType.HEARTRATE);
                intent.putExtra(IntentValueType.USER_ID.toString(),user.getuserId());
                startActivity(intent);
            }
        });

        mTemperaturLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoryViewActivity.class);
                intent.putExtra(IntentValueType.SENSORTYPE.toString(),SensorType.TEMPERATURE);
                intent.putExtra(IntentValueType.USER_ID.toString(),user.getuserId());
                startActivity(intent);
            }
        });

        mStepCounterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoryViewActivity.class);
                intent.putExtra(IntentValueType.SENSORTYPE.toString(),SensorType.STEPCOUNTER);
                intent.putExtra(IntentValueType.USER_ID.toString(),user.getuserId());
                startActivity(intent);
            }
        });

        mBatteryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoryViewActivity.class);
                intent.putExtra(IntentValueType.SENSORTYPE.toString(),SensorType.BATTERY);
                intent.putExtra(IntentValueType.USER_ID.toString(),user.getuserId());
                startActivity(intent);
            }
        });

        mHeartRateTextView = (TextView)findViewById(R.id.heartRateTextView);
        mStepCountTextView = (TextView)findViewById(R.id.stepCounterTextView);
        mTemperaturTextView = (TextView)findViewById(R.id.temperatureTextView);
        mBatteryTextView = (TextView)findViewById(R.id.batteryLevelTextView);

        Bundle extras = getIntent().getExtras();
        assert(extras != null);
        mBleDeviceAddress = extras.getString(IntentValueType.BLE_DEVICE_ADDRESS.toString());
        user = (AngelMemoUser) extras.getSerializable(IntentValueType.USER.toString());

        Intent intent = new Intent(this,ReceiverService.class);
        intent.putExtra(IntentValueType.BLE_DEVICE_ADDRESS.toString(), mBleDeviceAddress);
        intent.putExtra(IntentValueType.USER.toString(), user);
        startService(intent);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                SensorType s = SensorType.valueOf(intent.getStringExtra(IntentValueType.SENSORTYPE.toString()));

                switch (s) {
                    case TEMPERATURE:
                        mTemperaturTextView.setText(""+intent.getFloatExtra(BroadcastIntentValueType.SENSOR_VALUE.toString(),0));
                        break;
                    case HEARTRATE:
                        mHeartRateTextView.setText(""+intent.getFloatExtra(BroadcastIntentValueType.SENSOR_VALUE.toString(),0));
                        break;

                    case STEPCOUNTER:
                        mStepCountTextView.setText(""+intent.getFloatExtra(BroadcastIntentValueType.SENSOR_VALUE.toString(),0));
                        break;
                    case BATTERY:
                        mBatteryTextView.setText(""+(int)intent.getFloatExtra(BroadcastIntentValueType.SENSOR_VALUE.toString(),0) + "%");
                        break;

                    default:
                        Toast.makeText(OverviewActivity.this, "Unbekannte Sensor", Toast.LENGTH_SHORT).show();
                }

                // do something here.
            }
        };
    }

    // Wenn die App wieder gestartet wird (z.B. aus dem Standby, oder wenn es im Hintergrund offen war), dann verbindet es sich mit dem Angel Sensor
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(BroadcastIntentType.DISPLAY_VALUES.toString())
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
