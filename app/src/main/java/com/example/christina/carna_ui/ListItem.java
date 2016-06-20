package com.example.christina.carna_ui;

import android.bluetooth.BluetoothDevice;

/**
 * Created by raphy-laptop on 20.06.2016.
 */

public class ListItem implements Comparable<ListItem> {
    private final String mItemKey;
    private final String mItemName;
    private final BluetoothDevice mBluetoothDevice;


    public ListItem(String itemName, String itemKey, BluetoothDevice bluetoothDevice) {
        mItemKey = itemKey;
        mItemName = itemName;
        mBluetoothDevice = bluetoothDevice;
    }


    public String getItemKey() {
        return mItemKey;
    }


    public String getItemName() {
        return mItemName;
    }


    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothDevice;
    }


    @Override
    public String toString() {
        return mItemName;
    }


    @Override
    public int compareTo(ListItem item) {
        int nameCmp = mItemName.compareTo(item.getItemName());
        return (nameCmp != 0 ? nameCmp : mItemKey.compareTo(item.getItemKey()));
    }
}