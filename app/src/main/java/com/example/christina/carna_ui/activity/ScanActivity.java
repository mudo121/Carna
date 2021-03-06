package com.example.christina.carna_ui.activity;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.christina.carna_ui.BLE.BleScanner;
import com.example.christina.carna_ui.R;
import com.example.christina.carna_ui.database.AngelMemoUser;
import com.example.christina.carna_ui.enumclass.IntentValueType;
import com.example.christina.carna_ui.listviewadapter.ListDeviceItem;
import com.example.christina.carna_ui.listviewadapter.ListDeviceItemsAdapter;

public class ScanActivity extends AppCompatActivity {

    private static final int IDLE = 0;
    private static final int SCANNING = 1;
    private static final int CONNECTED = 2;

    static private int sState = IDLE;

    private BleScanner mBleScanner;
    private ListDeviceItemsAdapter mDeviceListAdapter;
    private Dialog mDeviceListDialog;
    private AngelMemoUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        mDeviceListAdapter = new ListDeviceItemsAdapter(this, R.layout.list_item);

        findViewById(R.id.searchDevicesButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(sState){
                    case IDLE:
                        startScan();
                        break;
                    case SCANNING:
                        stopScan();
                        break;
                    case CONNECTED:
                        //disconnect();
                        break;
                }
            }
        });
    }

    BleScanner.ScanCallback mScanCallback = new BleScanner.ScanCallback() {
        @Override
        public void onBluetoothDeviceFound(BluetoothDevice device) {
            if (device.getName() != null && device.getName().startsWith("Angel")) {
                ListDeviceItem newDevice = new ListDeviceItem(device.getName(), device.getAddress(), device);

                mDeviceListAdapter.add(newDevice);
                mDeviceListAdapter.addItem(newDevice);
                mDeviceListAdapter.notifyDataSetChanged();
            }
        }
    };

    private void startScan() {

        // Initialize the of the Ble Scanner.
        try {
            if (mBleScanner == null) {

                mBleScanner = new BleScanner(this, mScanCallback);
            }
        } catch (Exception e) {
            throw new AssertionError("Bluetooth is not accessible");
        }

        sState = SCANNING;
        mBleScanner.startScan();

        showDeviceListDialog();
    }

    private void showDeviceListDialog() {
        mDeviceListDialog = new Dialog(this);
        mDeviceListDialog.setTitle("Select A Device");
        mDeviceListDialog.setContentView(R.layout.device_list);
        ListView lv = (ListView) mDeviceListDialog.findViewById(R.id.lv);
        lv.setAdapter(mDeviceListAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
                stopScan();
                mDeviceListDialog.dismiss();
                BluetoothDevice bluetoothDevice = mDeviceListAdapter.getItem(position).getBluetoothDevice();
                Bundle b = getIntent().getExtras();
                user = (AngelMemoUser) b.getSerializable(IntentValueType.USER.toString());
                Intent intent = new Intent(parent.getContext(), OverviewActivity.class);
                intent.putExtra(IntentValueType.BLE_DEVICE_ADDRESS.toString(), bluetoothDevice.getAddress());
                intent.putExtra(IntentValueType.USER.toString(), user);
                startActivity(intent);
            }
        });

        mDeviceListDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                stopScan();
            }
        });
        mDeviceListDialog.show();
    }

    private void stopScan() {
        if (sState == SCANNING) {
            mBleScanner.stopScan();
            sState = IDLE;
        }
    }
}
