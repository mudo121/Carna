package com.example.christina.carna_ui.service;

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
import com.example.christina.carna_ui.database.AngelMemoDataSource;
import com.example.christina.carna_ui.database.AngelMemoUser;
import com.example.christina.carna_ui.enumclass.BroadcastIntentType;
import com.example.christina.carna_ui.enumclass.BroadcastIntentValueType;
import com.example.christina.carna_ui.enumclass.IntentValueType;
import com.example.christina.carna_ui.enumclass.SensorType;

/**
 * Created by raphy-laptop on 20.06.2016.
 */
public class ReceiverService extends Service {

    static final public String VALUE_RESULT = "com.examp.REQUEST_PROCESSED";

    private String mBleDeviceAddress;
    private BleDevice mBleDevice;
    private Handler mHandler;
    private Runnable mPeriodicReader;
    private AngelMemoDataSource source = null;
    private AngelMemoUser user;

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
        source = new AngelMemoDataSource(this);
        source.open();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Bundle extras = intent.getExtras();
            assert(extras != null);
            mBleDeviceAddress = extras.getString(IntentValueType.BLE_DEVICE_ADDRESS.toString());
            user = (AngelMemoUser) extras.getSerializable(IntentValueType.USER.toString());
            connect(mBleDeviceAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
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
                    displayValues(SensorType.STEPCOUNTER.toString(),steps.value);
                    source.addWert(user.getuserId(),source.getSensorByName(SensorType.STEPCOUNTER.toString()).getSensorId(),steps.value);
                }
            };


    // Listener für die Optische Wave Form, der Herz Frequenz (PPG Signal)
    private final BleCharacteristic.ValueReadyCallback<ChOpticalWaveform.OpticalWaveformValue> mOpticalWaveformListener = new BleCharacteristic.ValueReadyCallback<ChOpticalWaveform.OpticalWaveformValue>() {
        @Override
        public void onValueReady(ChOpticalWaveform.OpticalWaveformValue opticalWaveformValue) {
            if (opticalWaveformValue != null && opticalWaveformValue.wave != null)
                for (ChOpticalWaveform.OpticalSample item : opticalWaveformValue.wave) {
                    displayOpticalwave(BroadcastIntentValueType.OPTICAL_GREEN.toString(),item.green);
                    displayOpticalwave(BroadcastIntentValueType.OPTICAL_BLUE.toString(),item.blue);
                }
        }
    };

    // Listener für die Temperatur
    private final BleCharacteristic.ValueReadyCallback<ChTemperatureMeasurement.TemperatureMeasurementValue> mTemperatureListener =
            new BleCharacteristic.ValueReadyCallback<ChTemperatureMeasurement.TemperatureMeasurementValue>() {
                @Override
                public void onValueReady(final ChTemperatureMeasurement.TemperatureMeasurementValue temperature) {
                    displayValues(SensorType.TEMPERATURE.toString(),temperature.getTemperatureMeasurement());
                    source.addWert(user.getuserId(),source.getSensorByName(SensorType.TEMPERATURE.toString()).getSensorId(),temperature.getTemperatureMeasurement());
                }
            };

    // Listener für den Batterie Status
    private final BleCharacteristic.ValueReadyCallback<ChBatteryLevel.BatteryLevelValue> mBatteryLevelListener = new BleCharacteristic.ValueReadyCallback<ChBatteryLevel.BatteryLevelValue>() {
        @Override
        public void onValueReady(final ChBatteryLevel.BatteryLevelValue value) {
            displayValues(SensorType.BATTERY.toString(),value.value);
            source.addWert(user.getuserId(),source.getSensorByName(SensorType.BATTERY.toString()).getSensorId(),value.value);
        }
    };

    // Listener für den Puls
    private final BleCharacteristic.ValueReadyCallback<ChHeartRateMeasurement.HeartRateMeasurementValue> mHeartRateListener = new BleCharacteristic.ValueReadyCallback<ChHeartRateMeasurement.HeartRateMeasurementValue>() {
        @Override
        public void onValueReady(final ChHeartRateMeasurement.HeartRateMeasurementValue hrMeasurement) {
            displayValues(SensorType.HEARTRATE.toString(),hrMeasurement.getHeartRateMeasurement());
            source.addWert(user.getuserId(),source.getSensorByName(SensorType.HEARTRATE.toString()).getSensorId(),hrMeasurement.getHeartRateMeasurement());
        }
    };

    private void displayValues(String sensorType, final float value) {
        Intent intent = new Intent(BroadcastIntentType.DISPLAY_VALUES.toString());
        intent.putExtra(BroadcastIntentValueType.SENSOR_VALUE.toString(),value);
        intent.putExtra(IntentValueType.SENSORTYPE.toString(),sensorType);
        broadcaster.sendBroadcast(intent);
    }

    private void displayOpticalwave(String opticalwaveType, final int value) {
        Intent intent = new Intent(BroadcastIntentType.OPTICAL_WAVE.toString());
        intent.putExtra(BroadcastIntentValueType.OPTICAL_SENSOR_COLOR.toString(), opticalwaveType);
        intent.putExtra(BroadcastIntentValueType.OPTICAL_VALUE.toString(),value);
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
        Intent intent = new Intent("com.android.techtrainner");
        intent.putExtra(IntentValueType.BLE_DEVICE_ADDRESS.toString(), mBleDeviceAddress);
        intent.putExtra(IntentValueType.USER.toString(), user);
        sendBroadcast(intent);
    }


}
