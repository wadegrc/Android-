package com.example.fivechess.adapter;

import android.app.Application;

import com.example.fivechess.Utils.BlueToothWrapper;

public class BlueSocketWrapper extends Application {
    BlueToothWrapper wrapper;

    public BlueToothWrapper getWrapper() {
        return wrapper;
    }

    public void setWrapper(BlueToothWrapper wrapper) {
        this.wrapper = wrapper;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public boolean isHost;
}
