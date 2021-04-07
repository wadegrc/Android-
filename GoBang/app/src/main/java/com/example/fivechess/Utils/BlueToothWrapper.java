package com.example.fivechess.Utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.example.fivechess.ActivityPkg.BlueToothGame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * Created by lenov0 on 2016/2/16.
 */
public class BlueToothWrapper {
    private static final String TAG = "BlueToothWrapper";

    private Context mContext;
    private BluetoothAdapter mAdapter;

//    private static final UUID MY_UUID = UUID.fromString("b2a770c2-529e-4e80-933b-99dc372d3e65");
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String APP_NAME = "GoBang";
    private DeviceDiscoveryListener mDeviceDiscoveryListener;
    private DeviceConnectListener mDeviceConnectListener;
    private DataListener mDataListener;

    private AcceptThread mAcceptThread = null;
    private ConnectThread mConnectThread = null;
    private DataTransferThread mDataTransferThread = null;
    private BluetoothSocket bsocket = null;
    private String mData = null;
    private BlueToothGame mGame;
    //监听器
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {//发现设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (mDeviceDiscoveryListener != null) {
                    mDeviceDiscoveryListener.onDeviceFounded(device);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {//监听完成
                if (mDeviceDiscoveryListener != null) {
                    mDeviceDiscoveryListener.onDiscoveryFinished();
                }
            }
        }
    };

    //设备发现监听接口
    public interface DeviceDiscoveryListener {
        void onDeviceFounded(BluetoothDevice device);

        void onDiscoveryFinished();
    }

    //设备连接接口
    public interface DeviceConnectListener {
        void onConnectResult(boolean success);
    }

    //数据接收监听接口
    public interface DataListener {
        void onDataReceived(String data);
    }

    public void setGameActivity(BlueToothGame game){
        mGame = game;
    }
    //上下文
    public BlueToothWrapper(Context context) {
        mContext = context;
    }

    //初始化
    public boolean init() {
        if (initBlueTooth()) {
            Log.v("gong","registerReceiver");
            //注册监听器
            registerReceiver();
            return true;
        }
        return false;
    }

    //结束时销毁
    public void unInit() {
        unregisterReceiver();
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        if (mDataTransferThread != null) {
            mDataTransferThread.cancel();
            mDataTransferThread = null;
        }
    }

    //设置设备连接和数据监听器
    public void setListener(DeviceConnectListener connectListener, DataListener dataListener) {
        mDeviceConnectListener = connectListener;
        mDataListener = dataListener;
    }

    //注册监听器
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mContext.registerReceiver(mReceiver, filter);
    }

    //解绑监听器
    private void unregisterReceiver() {
        mContext.unregisterReceiver(mReceiver);
    }

    //初始化蓝牙
    private boolean initBlueTooth() {
        //默认适配器
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mAdapter != null) {
            if (!mAdapter.isEnabled()) {//开启蓝牙
                Intent enableBlueToothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mContext.startActivity(enableBlueToothIntent);
            }
        }
        return mAdapter != null;
    }

    //设置可见性
    public void setDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        mContext.startActivity(discoverableIntent);
        startBlueToothService();
    }

    //开启蓝牙设备
    private void startBlueToothService() {
        if (mAdapter.isDiscovering()) {
            mAdapter.cancelDiscovery();
        }
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mDataTransferThread != null) {
            mDataTransferThread.cancel();
            mDataTransferThread = null;
        }
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
    }

    //关闭蓝牙
    public void stopBlueToothService() {
        if (mDataTransferThread != null) {
            mDataTransferThread.cancel();
            mDataTransferThread = null;
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
    }

    //连接设备
    public void connectToDevice(BluetoothDevice device) {
        if (mAdapter.isDiscovering()) {
            mAdapter.cancelDiscovery();
        }
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mDataTransferThread != null) {
            mDataTransferThread.cancel();
            mDataTransferThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }

    //获取已经匹配的设备
    public List<BluetoothDevice> getPairedDevices() {
        Set<BluetoothDevice> bondedDevices = mAdapter.getBondedDevices();
        return new ArrayList<>(bondedDevices);
    }

    //搜索设备
    public boolean discoveryDevices(DeviceDiscoveryListener discoveryListener) {
        if (mAdapter.isDiscovering()) {
            mAdapter.cancelDiscovery();
        }
        if(mAdapter.getState()==BluetoothAdapter.STATE_ON){
            Log.v("gong","discoveryDevices");
        }
        mDeviceDiscoveryListener = discoveryListener;
        return mAdapter.startDiscovery();
    }

    //管理已连接的Socket
    private void manageConnectedSocket(BluetoothSocket socket) {
        if (mDataTransferThread != null) {
            mDataTransferThread.cancel();
            mDataTransferThread = null;
        }

        if (mDeviceConnectListener != null) {
            if (mContext instanceof Activity) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDeviceConnectListener.onConnectResult(true);
                    }
                });
            }
        }
        bsocket = socket;
        mDataTransferThread = new DataTransferThread(socket);
        mDataTransferThread.start();
    }

    public BluetoothSocket getSocket(){
        return bsocket;
    }
    //发送数据
    public void sendData(String data) {
        if (mDataTransferThread != null) {
            mDataTransferThread.sendData(data);
        }
    }

    //接受线程
    private class AcceptThread extends Thread {
        private BluetoothServerSocket mServerSocket = null;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(APP_NAME, MY_UUID);
//                Class<?>[] args = new Class[] { int.class };
//                Method listenMethod = mAdapter.getClass().getMethod("listenUsingRfcommOn", new Class[] { int.class });
//                tmp = (BluetoothServerSocket) (mAdapter.getClass().getMethod
//                        ("listenUsingRfcommOn", new Class[] { int.class }).invoke(mAdapter, new Object[] { 1 }));

//                tmp = mAdapter.listenUsingEncryptedRfcommOn();

            } catch (IOException e) {
                e.printStackTrace();
            }
            if(tmp==null){
                Log.v("gong","null");
            }
            mServerSocket = tmp;
        }

        @Override
        public void run() {
            if (mServerSocket == null) {
                return;
            }
            BluetoothSocket socket = null;
            try {
                socket = mServerSocket.accept();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (socket != null) {
                manageConnectedSocket(socket);
                try {
                    mServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void cancel() {
            if (mServerSocket != null) {
                try {
                    mServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //连接线程
    private class ConnectThread extends Thread {
        private BluetoothSocket mSocket = null;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            String name = device.getAddress();
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
//                tmp=(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSocket = tmp;
            if(mSocket.getRemoteDevice()==device){
                Log.v("gong","equal");
            }
        }

        @Override
        public void run() {
            if (mSocket == null) {
                return;
            }
            if (mAdapter.isDiscovering()) {
                mAdapter.cancelDiscovery();
            }
            try {
                mSocket.connect();
                boolean val=mSocket.isConnected();
                if(!val){
                    Log.v("gong","test");
                }
            } catch (IOException e1) {
                e1.printStackTrace();

                if (mDeviceConnectListener != null) {
                    if (mContext instanceof Activity) {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDeviceConnectListener.onConnectResult(false);
                            }
                        });
                    }
                }
                try {
                    mSocket.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                return;
            }
            manageConnectedSocket(mSocket);
        }

        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //数据发送线程
    private class DataTransferThread extends Thread {
        private BluetoothSocket mSocket;
        private InputStream mInputStream;
        private OutputStream mOutputStream;

        public DataTransferThread(BluetoothSocket socket) {
            mSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = mSocket.getInputStream();
                tmpOut = mSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mInputStream = tmpIn;
            mOutputStream = tmpOut;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = mInputStream.read(buffer);
                    byte[] tmp = Arrays.copyOf(buffer, bytes);
                    final String data = new String(tmp, "UTF-8");
                    Log.i(TAG, "res:" + data);
                    if (!data.isEmpty()) {
                        if (mContext instanceof Activity) {
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mDataListener.onDataReceived(data);
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void sendData(String data) {
            try {
                byte[] buffer;
                buffer = data.getBytes("UTF-8");
                mOutputStream.write(buffer);
                Log.i(TAG, data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel() {
            if (mSocket != null) {
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}