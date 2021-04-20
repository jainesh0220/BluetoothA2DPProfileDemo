package com.example.bluetootha2dpprofiledemo.btconnectionhelper;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.bluetooth.BluetoothGatt;

import java.util.UUID;

public class BluetoothConfiguration {

    private static final String TAG = BluetoothConfiguration.class.getSimpleName();

    /**
     * Class reference for the {@link BluetoothService} implementation.
     *
     * @see BluetoothClassicService
     * @see BluetoothLeService
     */
    public Class<? extends BluetoothService> bluetoothServiceClass;

    /**
     * {@link android.app.Application} context reference.
     */
    public Context context;

    /**
     * Name of your application or device.
     */
    public String deviceName;

    /**
     * Maximum of bytes to keep in the buffer before call the
     * {@link package com.example.bluetootha2dpprofiledemo.btconnectionhelper.BluetoothService.OnBluetoothEventCallback#onDataRead(byte[], int)}
     */
    public int bufferSize;

    /**
     * Character delimiter to know if a data is received completly and call the
     * {@link com.example.bluetootha2dpprofiledemo.btconnectionhelper.OnBluetoothEventCallback#onDataRead(byte[], int)}
     */
    public char characterDelimiter;

    /**
     * Required in {@link BluetoothClassicService}, is the UUID of the device that will connect in serial mode. <br/>
     * Optional in {@link BluetoothLeService}, is the UUID of the device that will be filtered in scan.
     * Set {@link null} if you want to scan all devices.
     */
    public UUID uuid;

    /**
     * Required for {@link BluetoothLeService} <br/>
     * UUID of bluetooth service.
     */
    public UUID uuidService;

    /**
     * Required for {@link BluetoothLeService} <br/>
     * UUID of bluetooth characteristic.
     */
    public UUID uuidCharacteristic;

    /**
     * Preferred transport for GATT connections to remote dual-mode devices
     * {@link BluetoothDevice#TRANSPORT_AUTO} or
     * {@link BluetoothDevice#TRANSPORT_BREDR} or {@link BluetoothDevice#TRANSPORT_LE}
     */
    public int transport;

    /**
     * Whether to call the listener only in Main Thread (true)
     * or call in the Thread where the event occurs (false).
     */
    public boolean callListenersInMainThread = true;

    /**
     * Required for {@link BluetoothLeService} <br/>
     * Request a specific connection priority. Must be one of
     * {@link BluetoothGatt#CONNECTION_PRIORITY_BALANCED}, {@link BluetoothGatt#CONNECTION_PRIORITY_HIGH}
     * or {@link BluetoothGatt#CONNECTION_PRIORITY_LOW_POWER}.
     *
     * An application should only request high priority connection parameters to transfer
     * large amounts of data over LE quickly. Once the transfer is complete, the application
     * should request {@link BluetoothGatt#CONNECTION_PRIORITY_BALANCED} connection parameters
     * to reduce energy use.
     */
    public int connectionPriority;

    public BluetoothConfiguration() {
        setDefaultTransport();
    }

    /**
     * Set the default value for {@link BluetoothConfiguration#transport}.
     */
    private void setDefaultTransport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            transport = BluetoothDevice.TRANSPORT_LE;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // From Android LOLLIPOP (21) the transport types exists, but them are hide for use,
            // so is needed to use relfection to get the value
            try {
                transport = BluetoothDevice.class.getDeclaredField("TRANSPORT_LE").getInt(null);
            } catch (Exception ex) {
                Log.d(TAG, "Error on get BluetoothDevice.TRANSPORT_LE with reflection.", ex);
            }
        } else {
            transport = -1;
        }
    }
}