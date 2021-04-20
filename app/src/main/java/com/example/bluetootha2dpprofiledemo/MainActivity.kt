package com.example.bluetootha2dpprofiledemo

import android.annotation.SuppressLint
import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.example.bluetootha2dpprofiledemo.btconnectionhelper.BluetoothConfiguration
import com.example.bluetootha2dpprofiledemo.btconnectionhelper.BluetoothService
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.lang.reflect.Method

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private val bluetoothConfiguration = BluetoothConfiguration()
    private var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var mContext: Context
    private lateinit var btServices: BluetoothService
    private var isEnabled: Boolean = false
    private var REQUEST_ENABLE_BT = 0
    private var mPlayer: MediaPlayer? = null
    private var devices: MutableSet<BluetoothDevice>? = null
    private var device: BluetoothDevice? = null
    private var iBinder: IBinder? = null
    private lateinit var bluetoothA2dp: BluetoothA2dp  //class to connect to an A2dp device
    private lateinit var iBluetoothA2dp: IBluetoothA2dp
    private var mIsA2dpReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this

        /* bluetoothConfiguration.context = applicationContext
         bluetoothConfiguration.bluetoothServiceClass = BluetoothClassicService::class.java
         bluetoothConfiguration.bufferSize = 1024
         bluetoothConfiguration.characterDelimiter = '\n'
         bluetoothConfiguration.deviceName = "bluetootha2dpprofiledemo"
         bluetoothConfiguration.callListenersInMainThread = true
         bluetoothConfiguration.uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Required

         BluetoothService.init(bluetoothConfiguration)
         btServices = BluetoothService.getDefaultInstance()!!
         btServices.startScan()
         initUI()*/
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        initUI()
    }

    private fun initUI() {
        buttonPlayAudio.setOnClickListener {
            playMusic()
        }
        buttonDevice1.setOnClickListener {
            if(getBluetoothDevice("98:09:CF:EC:5A:8E")?.createBond() == true) {
                Log.e(TAG, "initUI: CONNECTED")
                connectUsingBluetoothA2dp(getBluetoothDevice("98:09:CF:EC:5A:8E"))
            } else {
                getBluetoothDevice("98:09:CF:EC:5A:8E")?.createBond()
                Log.e(TAG, "initUI: not connected")
            }
            /*val bluetoothDevice = getBluetoothDevice("98:09:CF:EC:5A:8E")
            btServices.connect(bluetoothDevice)*/
        }
        buttonDevice2.setOnClickListener {
            if(getBluetoothDevice("20:19:B4:14:ED:CD")?.createBond() == true) {
                Log.e(TAG, "initUI: CONNECTED")
                connectUsingBluetoothA2dp(getBluetoothDevice("20:19:B4:14:ED:CD"))
            } else {
                getBluetoothDevice("20:19:B4:14:ED:CD")?.createBond()
                Log.e(TAG, "initUI: not connected")
            }
            //connectUsingBluetoothA2dp(getBluetoothDevice("FC:58:FA:60:9C:41"))
            //todo add mac
            /*val bluetoothDevice = getBluetoothDevice("")
           btServices.connect(bluetoothDevice)*/
        }
        buttonDevice3.setOnClickListener {
            //todo add mac
            /*val bluetoothDevice = getBluetoothDevice("")
            btServices.connect(bluetoothDevice)*/
        }
        buttonDevice4.setOnClickListener {
            //todo add mac
            /*val bluetoothDevice = getBluetoothDevice("")
            btServices.connect(bluetoothDevice)*/
        }

        /* btServices.setOnScanCallback(object : OnBluetoothScanCallback {
             override fun onDeviceDiscovered(device: BluetoothDevice?, rssi: Int) {
                 Log.e(TAG, "onDeviceDiscovered: ${device?.name}")
             }

             override fun onStartScan() {}
             override fun onStopScan() {}
         })

         btServices.setOnEventCallback(object : OnBluetoothEventCallback {
             override fun onDataRead(buffer: ByteArray, length: Int) {}
             override fun onStatusChange(status: BluetoothStatus?) {
                 textConnectionStatus.text = status.toString()
                 Log.e(TAG, "onStatusChange: Status = ${status.toString()}")
                 //Toast.makeText(mContext, "Connection status = ${status.toString()}", Toast.LENGTH_LONG).show()
             }
             override fun onDeviceName(deviceName: String?) {
                 textDeviceName.text = deviceName
                 Toast.makeText(mContext, "Device name = $deviceName", Toast.LENGTH_LONG).show()
             }
             override fun onToast(message: String) {}
             override fun onDataWrite(buffer: ByteArray) {}
         })*/
    }

    private fun getBluetoothDevice(address: String): BluetoothDevice? {
        //btServices.disconnect()
        return bluetoothAdapter?.getRemoteDevice(address)
    }


    override fun onDestroy() {
        releaseMediaPlayer()
        disConnectUsingBluetoothA2dp(device)
        super.onDestroy()
    }

    override fun onPause() {
        releaseMediaPlayer()
        super.onPause()
    }

    private fun releaseMediaPlayer() {
        mPlayer?.release()
    }

    private fun setIsA2dpReady(ready: Boolean) {
        mIsA2dpReady = ready
    }

    private fun connectUsingBluetoothA2dp(deviceToConnect: BluetoothDevice?) {
        try {
            val serviceManager = Class.forName("android.os.ServiceManager")
            val service: Method = serviceManager.getDeclaredMethod("getService", String::class.java)
            iBinder = service.invoke(serviceManager.newInstance(), "bluetooth_a2dp") as IBinder?
            if (iBinder == null) {
                // For Android 4.2 Above Devices
                device = deviceToConnect
                //establish a connection to the profile proxy object associated with the profile
                BluetoothAdapter.getDefaultAdapter().getProfileProxy(
                    this,
                    // listener notifies BluetoothProfile clients when they have been connected to or disconnected from the service
                    object : BluetoothProfile.ServiceListener {
                        override fun onServiceDisconnected(profile: Int) {
                            setIsA2dpReady(false)
                            disConnectUsingBluetoothA2dp(device)
                        }

                        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                            bluetoothA2dp = proxy as BluetoothA2dp
                            try {
                                //establishing bluetooth connection with A2DP devices
                                bluetoothA2dp.javaClass.getMethod("connect", BluetoothDevice::class.java).invoke(bluetoothA2dp, deviceToConnect)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            setIsA2dpReady(true)
                        }
                    }, BluetoothProfile.A2DP
                )
            } else {
                val serviceA2DP = Class.forName("android.bluetooth.IBluetoothA2dp")
                val service = serviceA2DP.declaredClasses
                val serviceInstance = service[0]
                val interfaceMethod: Method = serviceInstance.getDeclaredMethod("asInterface", IBinder::class.java)
                interfaceMethod.isAccessible = true
                iBluetoothA2dp = interfaceMethod.invoke(null, iBinder) as IBluetoothA2dp
                iBluetoothA2dp.connect(deviceToConnect)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    private fun disConnectUsingBluetoothA2dp(deviceToConnect: BluetoothDevice?) {
        try {
            // For Android 4.2 Above Devices
            if (iBinder == null) {
                try {
                    //disconnecting bluetooth device
                    bluetoothA2dp.javaClass.getMethod("disconnect", BluetoothDevice::class.java).invoke(bluetoothA2dp, deviceToConnect)
                    BluetoothAdapter.getDefaultAdapter()
                        .closeProfileProxy(BluetoothProfile.A2DP, bluetoothA2dp)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                iBluetoothA2dp.disconnect(deviceToConnect)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playMusic() {
        //streaming music on the connected A2DP device
        mPlayer = MediaPlayer()
        try {
            mPlayer?.setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
            mPlayer?.setDataSource(this, Uri.parse("https://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3"))
            mPlayer?.prepare()
            mPlayer?.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}