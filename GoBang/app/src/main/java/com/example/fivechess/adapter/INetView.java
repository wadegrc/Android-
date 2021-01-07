package com.example.fivechess.adapter;

import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 * Created by Xuf on 2016/1/23.
 */
public interface INetView {


    void onBlueToothDeviceConnected();

    void onBlueToothDeviceConnectFailed();



    void onGetPairedToothPeers(List<BluetoothDevice> deviceList);

    void onFindBlueToothPeers(List<BluetoothDevice> deviceList);

    void onPeersNotFound();

    void onDataReceived(String o);

    void onSendMessageFailed();
}
