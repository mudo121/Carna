package com.example.christina.carna_ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by raphy-laptop on 20.06.2016.
 */
public class ReceiverCall extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        assert(extras != null);
        String mBleDeviceAddress = extras.getString("ble_device_address");

        Intent intentCall = new Intent(context,ReceiverService.class);
        intentCall.putExtra("ble_device_address", mBleDeviceAddress);
        context.startService(intentCall);
    }

}
