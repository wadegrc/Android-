package com.example.fivechess.event;

import android.bluetooth.BluetoothDevice;

import com.example.fivechess.Utils.Device;

/**
 * Created by Administrator on 2016/1/25.
 */
public class ConnectPeerEvent {

    public Device mSalutDevice;
    public BluetoothDevice mBlueToothDevice;

    public ConnectPeerEvent(Device device, BluetoothDevice bluetoothDevice) {
        mSalutDevice = device;
        mBlueToothDevice = bluetoothDevice;
    }
}
