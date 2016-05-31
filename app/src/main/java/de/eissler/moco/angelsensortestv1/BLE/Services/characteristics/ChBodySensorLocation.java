/*
 * Copyright (c) 2015, Seraphim Sense Ltd.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *    and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *    conditions and the following disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 *    endorse or promote products derived from this software without specific prior written
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.eissler.moco.angelsensortestv1.BLE.Services.characteristics;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.UUID;

import de.eissler.moco.angelsensortestv1.BLE.BleCharacteristic;
import de.eissler.moco.angelsensortestv1.BLE.BleDevice;


/** org.bluetooth.characteristic.body_sensor_location */
public class ChBodySensorLocation extends BleCharacteristic<Integer> {
    public final static UUID CHARACTERISTIC_UUID = UUID.fromString("00002a38-0000-1000-8000-00805f9b34fb");

    public final static int OTHER = 0;
    public final static int CHEST = 1;
    public final static int WRIST = 2;
    public final static int FINGER = 3;
    public final static int HAND = 4;
    public final static int EAR_LOBE = 5;
    public final static int FOOT = 6;


    public ChBodySensorLocation(BluetoothGattCharacteristic gattCharacteristic, BleDevice bleDevice) {
        super(CHARACTERISTIC_UUID, gattCharacteristic, bleDevice);
    }


    public ChBodySensorLocation() {
        super(CHARACTERISTIC_UUID);
    }


    @Override
    protected Integer processCharacteristicValue() {
        BluetoothGattCharacteristic c = getBaseGattCharacteristic();
        return c.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
    }

}
