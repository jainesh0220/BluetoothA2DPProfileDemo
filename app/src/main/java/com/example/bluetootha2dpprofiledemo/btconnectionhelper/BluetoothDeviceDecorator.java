package com.example.bluetootha2dpprofiledemo.btconnectionhelper;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import androidx.annotation.RequiresPermission;

public class BluetoothDeviceDecorator {

    private BluetoothDevice mDevice;
    private int mRSSI;

    public BluetoothDeviceDecorator(BluetoothDevice device) {
        mDevice = device;
    }

    public BluetoothDeviceDecorator(BluetoothDevice device, int RSSI) {
        this(device);
        mRSSI = RSSI;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH)
    public String getName() {
        return mDevice.getName() != null && mDevice.getName().length() != 0 ? mDevice.getName() : "Desconhecido...";
    }

    public String getAddress() {
        return mDevice.getAddress();
    }

    public int getRSSI() {
        return mRSSI;
    }

    public void setRSSI(int RSSI) {
        mRSSI = RSSI;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BluetoothDeviceDecorator that = (BluetoothDeviceDecorator) o;

        return mDevice.equals(that.mDevice);
    }

    @Override
    public int hashCode() {
        return mDevice.hashCode();
    }

    public void setDevice(BluetoothDevice device) {
        this.mDevice = device;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }
}
