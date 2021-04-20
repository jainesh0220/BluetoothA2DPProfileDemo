package com.example.bluetootha2dpprofiledemo.btconnectionhelper;

import android.bluetooth.BluetoothDevice;

public class BluetoothDeviceDecoratorNull extends BluetoothDeviceDecorator {

    private BluetoothDeviceDecoratorNull(BluetoothDevice device) {
        super(device);
    }

    public static BluetoothDeviceDecoratorNull getInstance() {
        return new BluetoothDeviceDecoratorNull(null);
    }

    @Override
    public String getName() {
        return "No device found";
    }

    @Override
    public String getAddress() {
        return "";
    }

    @Override
    public int getRSSI() {
        return 9999;
    }

    @Override
    public void setRSSI(int RSSI) {
    }
}
