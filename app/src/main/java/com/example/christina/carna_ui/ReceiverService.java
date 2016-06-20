package com.example.christina.carna_ui;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.example.christina.carna_ui.BLE.BleCharacteristic;
import com.example.christina.carna_ui.BLE.BleDevice;
import com.example.christina.carna_ui.BLE.Services.SrvActivityMonitoring;
import com.example.christina.carna_ui.BLE.Services.SrvBattery;
import com.example.christina.carna_ui.BLE.Services.SrvHealthThermometer;
import com.example.christina.carna_ui.BLE.Services.SrvHeartRate;
import com.example.christina.carna_ui.BLE.Services.SrvWaveformSignal;
import com.example.christina.carna_ui.BLE.Services.characteristics.ChBatteryLevel;
import com.example.christina.carna_ui.BLE.Services.characteristics.ChHeartRateMeasurement;
import com.example.christina.carna_ui.BLE.Services.characteristics.ChOpticalWaveform;
import com.example.christina.carna_ui.BLE.Services.characteristics.ChStepCount;
import com.example.christina.carna_ui.BLE.Services.characteristics.ChTemperatureMeasurement;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by raphy-laptop on 20.06.2016.
 */
public class ReceiverService extends Service {

    static final public String VALUE_RESULT = "com.examp.REQUEST_PROCESSED";

    private String mBleDeviceAddress;
    private BleDevice mBleDevice;
    private Handler mHandler;
    private Runnable mPeriodicReader;

    private static final int RSSI_UPDATE_INTERVAL = 1000;

    LocalBroadcastManager broadcaster;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(this.getMainLooper());
        mPeriodicReader = new Runnable() {
            @Override
            public void run() {
                mBleDevice.readRemoteRssi();
                mHandler.postDelayed(mPeriodicReader, RSSI_UPDATE_INTERVAL);
            }
        };

        broadcaster = LocalBroadcastManager.getInstance(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Bundle extras = intent.getExtras();
            assert(extras != null);
            mBleDeviceAddress = extras.getString("ble_device_address");
            connect(mBleDeviceAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;//super.onStartCommand(intent, flags, startId);
    }

    private Timer mTimer;

    TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {



//            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//            v.vibrate(500);
        }
    };

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
                    displayValues("StepCounter",steps.value);
                }
            };


    // Listener für die Optische Wave Form, der Herz Frequenz (PPG Signal)
    private final BleCharacteristic.ValueReadyCallback<ChOpticalWaveform.OpticalWaveformValue> mOpticalWaveformListener = new BleCharacteristic.ValueReadyCallback<ChOpticalWaveform.OpticalWaveformValue>() {
        @Override
        public void onValueReady(ChOpticalWaveform.OpticalWaveformValue opticalWaveformValue) {
            if (opticalWaveformValue != null && opticalWaveformValue.wave != null)
                for (ChOpticalWaveform.OpticalSample item : opticalWaveformValue.wave) {
                    //mGreenOpticalWaveformView.addValue(item.green);
                    //mBlueOpticalWaveformView.addValue(item.blue);
                }
        }
    };

    // Listener für die Temperatur
    private final BleCharacteristic.ValueReadyCallback<ChTemperatureMeasurement.TemperatureMeasurementValue> mTemperatureListener =
            new BleCharacteristic.ValueReadyCallback<ChTemperatureMeasurement.TemperatureMeasurementValue>() {
                @Override
                public void onValueReady(final ChTemperatureMeasurement.TemperatureMeasurementValue temperature) {
                    displayValues("Temperatur",temperature.getTemperatureMeasurement());
                }
            };

    // Listener für den Batterie Status
    private final BleCharacteristic.ValueReadyCallback<ChBatteryLevel.BatteryLevelValue> mBatteryLevelListener = new BleCharacteristic.ValueReadyCallback<ChBatteryLevel.BatteryLevelValue>() {
        @Override
        public void onValueReady(final ChBatteryLevel.BatteryLevelValue value) {
            displayValues("BatteryLevel",value.value);
        }
    };

    // Listener für den Puls
    private final BleCharacteristic.ValueReadyCallback<ChHeartRateMeasurement.HeartRateMeasurementValue> mHeartRateListener = new BleCharacteristic.ValueReadyCallback<ChHeartRateMeasurement.HeartRateMeasurementValue>() {
        @Override
        public void onValueReady(final ChHeartRateMeasurement.HeartRateMeasurementValue hrMeasurement) {
            displayValues("HeartRate",hrMeasurement.getHeartRateMeasurement());
        }
    };

    private void displayValues(String sensorType, final float value) {
        Intent intent = new Intent("displayValues");
        intent.putExtra("value",value);
        intent.putExtra("SensorType",sensorType);
        broadcaster.sendBroadcast(intent);
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

    public void onDestroy() {
        try {
            mTimer.cancel();
            timerTask.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent("com.android.techtrainner");
        intent.putExtra("ble_device_address", mBleDeviceAddress);
        sendBroadcast(intent);
    }


}
