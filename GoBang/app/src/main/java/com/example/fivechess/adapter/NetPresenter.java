package com.example.fivechess.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.example.fivechess.Utils.BlueToothWrapper;
import com.example.fivechess.Utils.Constants;

import java.util.List;

/**
 * Created by Xuf on 2016/1/23.
 */
public class NetPresenter implements INetInteratorCallback {
    private INetView mNetView;
    private NetInteractor mNetInteractor;

    private int mGameMode;

    public NetPresenter(Context context, INetView netView, int gameMode) {
        mNetView = netView;
        mGameMode = gameMode;
        if (isWifiMode()) {

        } else {
            mNetInteractor = new BlueToothInteractor(context, this);
        }
    }

    private boolean isWifiMode() {
        return mGameMode == Constants.WIFI_MODE;
    }

    //下列方法均可通过实现NetPreenter变量来调用
    public void init() {
        mNetInteractor.init();
    }

    public void unInit() {
        mNetInteractor.unInit();
    }

    public void startService() {
        mNetInteractor.startNetService();
    }

    public void stopService() {
        mNetInteractor.stopNetService();
    }

    public void sendToDevice(String message, boolean isHost) {
        mNetInteractor.sendToDevice(message, isHost);
    }

    public void findPeers() {
        mNetInteractor.findPeers();
    }

    public void connectToHost(BluetoothDevice blueToothHost) {
        mNetInteractor.connectToHost(blueToothHost);
    }


    @Override
    public void onMobileNotSupportDevice() {

    }

    //下列方法均可继承INetView接口，并使用下列方法
    @Override
    public void onBlueToothDeviceConnected() {
        mNetView.onBlueToothDeviceConnected();
    }

    @Override
    public void onBlueToothDeviceConnectFailed() {
        mNetView.onBlueToothDeviceConnectFailed();
    }


    @Override
    public void onGetPairedToothPeers(List<BluetoothDevice> deviceList) {
        mNetView.onGetPairedToothPeers(deviceList);
    }

    @Override
    public void onFindBlueToothPeers(List<BluetoothDevice> deviceList) {
        mNetView.onFindBlueToothPeers(deviceList);
    }

    @Override
    public void onPeersNotFound() {
        mNetView.onPeersNotFound();
    }

    @Override
    public void onDataReceived(String o) {
        mNetView.onDataReceived(o);
    }

    @Override
    public void getWrapper(BlueToothWrapper wrapper) {
        mNetView.getWrapper(wrapper);
    }

    @Override
    public void takeWrapper() {
        mNetInteractor.takeWrapper();
    }

//    @Override
//    public void getSocket(BluetoothSocket socket) {
//        mNetView.getSocket(socket);
//    }
//
//    @Override
//    public void takeSocket() {
//        mNetInteractor.takeSocket();
//    }

    @Override
    public void onSendMessageFailed() {
        mNetView.onSendMessageFailed();
    }

}
