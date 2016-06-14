package de.eissler.moco.angelsensortestv1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.TextView;

import de.eissler.moco.angelsensortestv1.BLE.BleCharacteristic;
import de.eissler.moco.angelsensortestv1.BLE.BleDevice;
import de.eissler.moco.angelsensortestv1.BLE.Services.SrvActivityMonitoring;
import de.eissler.moco.angelsensortestv1.BLE.Services.SrvBattery;
import de.eissler.moco.angelsensortestv1.BLE.Services.SrvHealthThermometer;
import de.eissler.moco.angelsensortestv1.BLE.Services.SrvHeartRate;
import de.eissler.moco.angelsensortestv1.BLE.Services.SrvWaveformSignal;
import de.eissler.moco.angelsensortestv1.BLE.Services.characteristics.ChBatteryLevel;
import de.eissler.moco.angelsensortestv1.BLE.Services.characteristics.ChHeartRateMeasurement;
import de.eissler.moco.angelsensortestv1.BLE.Services.characteristics.ChOpticalWaveform;
import de.eissler.moco.angelsensortestv1.BLE.Services.characteristics.ChStepCount;
import de.eissler.moco.angelsensortestv1.BLE.Services.characteristics.ChTemperatureMeasurement;

/**
 * Created by raphy-laptop on 31.05.2016.
 */
public class HomeActivity extends Activity {

    private String mBleDeviceAddress;
    private BleDevice mBleDevice;
    private Handler mHandler;
    private Runnable mPeriodicReader;

    private static final int RSSI_UPDATE_INTERVAL = 1000; // Milliseconds

    private TextView mTextView;
    private GraphView mBlueOpticalWaveformView, mGreenOpticalWaveformView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);



        mTextView = (TextView) findViewById(R.id.angelAdressTextView);
        mHandler = new Handler(this.getMainLooper());

        // erstelle Graph Views
        mGreenOpticalWaveformView = (GraphView) findViewById(R.id.graph_green);
        mGreenOpticalWaveformView.setStrokeColor(Color.GREEN);
        mBlueOpticalWaveformView = (GraphView) findViewById(R.id.graph_blue);
        mBlueOpticalWaveformView.setStrokeColor(Color.BLUE);

        mPeriodicReader = new Runnable() {
            @Override
            public void run() {
                mBleDevice.readRemoteRssi();
                mHandler.postDelayed(mPeriodicReader, RSSI_UPDATE_INTERVAL);
            }
        };
    }

    // Wenn die App wieder gestartet wird (z.B. aus dem Standby, oder wenn es im Hintergrund offen war), dann verbindet es sich mit dem Angel Sensor
    protected void onStart() {
        super.onStart();

        Bundle extras = getIntent().getExtras();
        assert(extras != null);
        mBleDeviceAddress = extras.getString("ble_device_address");
        mTextView.setText(mBleDeviceAddress);

        connect(mBleDeviceAddress);
    }


    // Wenn die App gestoppt wird, dann wird die Verbindung getrennt
    @Override
    protected void onStop() {
        super.onStop();
        //unscheduleUpdaters();
        //mBleDevice.disconnect();
    }

    private void connect(String deviceAddress) {
        // A device has been chosen from the list. Create an instance of BleDevice,
        // populate it with interesting services and then connect

        if (mBleDevice != null) {
            mBleDevice.disconnect();
        }
        mBleDevice = new BleDevice(this, mDeviceLifecycleCallback, mHandler);

        try {
            // Hier werden die Services Registriert, die alle benutzt werden wollen.
            // Wenn man mehr vom Sensor nutzen will, dann müssen auch die entsprechenden Services registriert werden
            mBleDevice.registerServiceClass(SrvHeartRate.class);
            mBleDevice.registerServiceClass(SrvHealthThermometer.class);
            mBleDevice.registerServiceClass(SrvBattery.class);
            mBleDevice.registerServiceClass(SrvActivityMonitoring.class);
            mBleDevice.registerServiceClass(SrvWaveformSignal.class);


        } catch (NoSuchMethodException e) {
            throw new AssertionError();
        } catch (IllegalAccessException e) {
            throw new AssertionError();
        } catch (InstantiationException e) {
            throw new AssertionError();
        }

        mBleDevice.connect(deviceAddress);

        scheduleUpdaters();
        displayOnDisconnect();
    }

    private final BleDevice.LifecycleCallback mDeviceLifecycleCallback = new BleDevice.LifecycleCallback() {
        @Override
        public void onBluetoothServicesDiscovered(BleDevice device) {

            // Hier werden die Entsprechenden Servies aktiviert
            // Für jeden Services gibt es einen Listener. Wenn der Angel Sensor einen Wert hat, dann wird die Funktion aufgerufen, die im Listener steht

            device.getService(SrvHeartRate.class).getHeartRateMeasurement().enableNotifications(mHeartRateListener);
            device.getService(SrvHealthThermometer.class).getTemperatureMeasurement().enableNotifications(mTemperatureListener);
            device.getService(SrvBattery.class).getBatteryLevel().enableNotifications(mBatteryLevelListener);
            device.getService(SrvActivityMonitoring.class).getStepCount().enableNotifications(mStepCountListener);
            device.getService(SrvWaveformSignal.class).getOpticalWaveform().enableNotifications(mOpticalWaveformListener);
        }

        @Override
        public void onBluetoothDeviceDisconnected() {
            displayOnDisconnect();
            unscheduleUpdaters();

            // Re-connect immediately
            connect(mBleDeviceAddress);
        }

        @Override
        public void onReadRemoteRssi(final int rssi) {
            // Wird in dieser App nicht gebraucht
            //displaySignalStrength(rssi);
        }
    };

    // Listener für den Step Counter
    private final BleCharacteristic.ValueReadyCallback<ChStepCount.StepCountValue> mStepCountListener =
            new BleCharacteristic.ValueReadyCallback<ChStepCount.StepCountValue>() {
                @Override
                public void onValueReady(final ChStepCount.StepCountValue steps) {
                    displaySteps(steps.value);
                }
            };


    // Listener für die Optische Wave Form, der Herz Frequenz (PPG Signal)
    private final BleCharacteristic.ValueReadyCallback<ChOpticalWaveform.OpticalWaveformValue> mOpticalWaveformListener = new BleCharacteristic.ValueReadyCallback<ChOpticalWaveform.OpticalWaveformValue>() {
        @Override
        public void onValueReady(ChOpticalWaveform.OpticalWaveformValue opticalWaveformValue) {
            if (opticalWaveformValue != null && opticalWaveformValue.wave != null)
                for (ChOpticalWaveform.OpticalSample item : opticalWaveformValue.wave) {
                    mGreenOpticalWaveformView.addValue(item.green);
                    mBlueOpticalWaveformView.addValue(item.blue);
                }
        }
    };

    // Listener für die Temperatur
    private final BleCharacteristic.ValueReadyCallback<ChTemperatureMeasurement.TemperatureMeasurementValue> mTemperatureListener =
            new BleCharacteristic.ValueReadyCallback<ChTemperatureMeasurement.TemperatureMeasurementValue>() {
                @Override
                public void onValueReady(final ChTemperatureMeasurement.TemperatureMeasurementValue temperature) {
                    displayTemperature(temperature.getTemperatureMeasurement());
                }
            };

    // Listener für den Batterie Status
    private final BleCharacteristic.ValueReadyCallback<ChBatteryLevel.BatteryLevelValue> mBatteryLevelListener = new BleCharacteristic.ValueReadyCallback<ChBatteryLevel.BatteryLevelValue>() {
        @Override
        public void onValueReady(final ChBatteryLevel.BatteryLevelValue value) {
            displayBattery(value.value);
        }
    };

    // Listener für den Puls
    private final BleCharacteristic.ValueReadyCallback<ChHeartRateMeasurement.HeartRateMeasurementValue> mHeartRateListener = new BleCharacteristic.ValueReadyCallback<ChHeartRateMeasurement.HeartRateMeasurementValue>() {
        @Override
        public void onValueReady(final ChHeartRateMeasurement.HeartRateMeasurementValue hrMeasurement) {
            displayHeartRate(hrMeasurement.getHeartRateMeasurement());
        }
    };


    private void displaySteps(final float step) {
        TextView textView = (TextView)findViewById(R.id.stepTextView);
        textView.setText(step+"");
    }

    private void displayTemperature(final float degreesCelsius) {
        TextView textView = (TextView)findViewById(R.id.temperaturTextView);
        textView.setText(degreesCelsius+"");
        long pattern[] = { 0, 100, 200, 300, 400,};

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        v.vibrate(500);

        //v.cancel();
    }

    private void displayHeartRate(final int bpm) {
        TextView textView = (TextView)findViewById(R.id.heartrateTextView);
        textView.setText(bpm + " bpm");
    }

    private void displayBattery(final int value) {
        TextView textView = (TextView)findViewById(R.id.batteryLevelTextView);
        textView.setText(value+"%");
    }

    private void scheduleUpdaters() {
        mHandler.post(mPeriodicReader);
    }

    private void unscheduleUpdaters() {
        mHandler.removeCallbacks(mPeriodicReader);
    }

    private void displayOnDisconnect() {
        // TODO: 31.05.2016
        //displaySignalStrength(-99);
        //displayBatteryLevel(0);
    }
}
