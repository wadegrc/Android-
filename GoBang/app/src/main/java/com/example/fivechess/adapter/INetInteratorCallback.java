package com.example.fivechess.adapter;

import android.bluetooth.BluetoothDevice;

import com.example.fivechess.Utils.BlueToothWrapper;

import java.util.List;

/**
 * Created by Xuf on 2016/1/23.
 */
public interface INetInteratorCallback {

    void onMobileNotSupportDevice();


    void onBlueToothDeviceConnected();

    void onBlueToothDeviceConnectFailed();



    void onGetPairedToothPeers(List<BluetoothDevice> deviceList);

    void onFindBlueToothPeers(List<BluetoothDevice> deviceList);

    void onPeersNotFound();

    void onDataReceived(String o);
    void getWrapper(BlueToothWrapper wrapper);
    void takeWrapper();
    void onSendMessageFailed();
}
