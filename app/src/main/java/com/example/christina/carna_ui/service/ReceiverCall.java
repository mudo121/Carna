package com.example.christina.carna_ui.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.christina.carna_ui.enumclass.IntentValueType;

/**
 * Created by raphy-laptop on 20.06.2016.
 */
public class ReceiverCall extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        assert(extras != null);
        String mBleDeviceAddress = extras.getString(IntentValueType.BLE_DEVICE_ADDRESS.toString());

        Intent intentCall = new Intent(context,ReceiverService.class);
        intentCall.putExtra(IntentValueType.BLE_DEVICE_ADDRESS.toString(), mBleDeviceAddress);
        context.startService(intentCall);
    }

}
