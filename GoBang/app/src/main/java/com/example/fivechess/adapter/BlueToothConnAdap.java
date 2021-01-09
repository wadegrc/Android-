package com.example.fivechess.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fivechess.R;
import com.example.fivechess.Utils.Device;

import java.util.ArrayList;

public class BlueToothConnAdap extends BaseAdapter {
    public ArrayList<Device> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }

    private ArrayList<Device> devices;
    private Context context;
    private TextView devicename;

    //显示设备是否已经配对
    private ImageView headimg;
    private TextView device_ifchoise;
    private ImageView imag;

    public BlueToothConnAdap(Context context, ArrayList<Device> devices) {
        this.context = context;
        this.devices = devices;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.device_cell_layout, null);
        Device device = devices.get(position);
        imag = v.findViewById(R.id.headimg);
        devicename = (TextView) v.findViewById(R.id.device_name);
        devicename.setText(device.getDeviceName());
        device_ifchoise = (TextView) v.findViewById(R.id.device_ifchoise);
        if (device.getBundlestate() == BluetoothDevice.BOND_BONDED) {
            device_ifchoise.setText("已配对");
        }else{
            device_ifchoise.setText("未配对");
        }
        return v;
    }

}
