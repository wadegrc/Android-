package com.example.fivechess.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.fivechess.Utils.BlueToothWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenov0 on 2016/2/21.
 */
public class BlueToothInteractor extends NetInteractor {

    private static final String TAG = "BlueToothInteractor";

    private Context mContext;

    private BlueToothWrapper mBlueToothWrapper;
    private INetInteratorCallback mCallback;

    private BlueToothWrapper.DeviceConnectListener mDeviceConnectListener;
    private BlueToothWrapper.DataListener mDataListener;

    public BlueToothInteractor(Context context, INetInteratorCallback callback) {
        mBlueToothWrapper = new BlueToothWrapper(context);
        mCallback = callback;
        mContext = context;
    }

    public boolean init() {
        boolean success = mBlueToothWrapper.init();
        setListener();
        return success;
    }

    public void unInit() {
        mBlueToothWrapper.unInit();
    }

    private void setListener() {
        mDeviceConnectListener = new BlueToothWrapper.DeviceConnectListener() {
            @Override
            public void onConnectResult(boolean success) {
                if (success) {
                    mCallback.onBlueToothDeviceConnected();
                } else {
                    mCallback.onBlueToothDeviceConnectFailed();
                }
            }
        };
        mDataListener = new BlueToothWrapper.DataListener() {
            @Override
            public void onDataReceived(String data) {
                mCallback.onDataReceived(data);
            }
        };
        mBlueToothWrapper.setListener(mDeviceConnectListener, mDataListener);
    }

    public void startNetService() {
        mBlueToothWrapper.setDiscoverable();
    }

    public void stopNetService() {
        mBlueToothWrapper.stopBlueToothService();
    }

    public void findPeers() {
        final List<BluetoothDevice> pairedDevices = mBlueToothWrapper.getPairedDevices();
        if (!pairedDevices.isEmpty()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCallback.onGetPairedToothPeers(pairedDevices);
                }
            }, 1000);
        }

        final List<BluetoothDevice> discoveryDevices = new ArrayList<>();
        boolean t=mBlueToothWrapper.discoveryDevices(new BlueToothWrapper.DeviceDiscoveryListener() {
            @Override
            public void onDeviceFounded(BluetoothDevice device) {
                if (!pairedDevices.contains(device)) {
                    discoveryDevices.add(device);
                }
            }
            @Override
            public void onDiscoveryFinished() {
                mCallback.onFindBlueToothPeers(discoveryDevices);
            }
        });
        if(!t){
            Log.v("gong","失败");
        }
    }

    @Override
    public void takeWrapper() {
        mCallback.getWrapper(mBlueToothWrapper);
    }
    //获得连接好的socket
//    public void takeSocket(){
//        mCallback.getSocket(mBlueToothWrapper.getSocket());
//    }

    public void connectToHost(BluetoothDevice blueToothHost) {
        mBlueToothWrapper.connectToDevice(blueToothHost);
    }

    public void sendToDevice(String message, boolean isHost) {
        mBlueToothWrapper.sendData(message);
    }
}