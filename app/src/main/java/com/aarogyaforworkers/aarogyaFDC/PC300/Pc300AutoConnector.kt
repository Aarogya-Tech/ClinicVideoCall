package com.aarogyaforworkers.aarogya.BluetoothTasks.AutoConnector

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.storage.BleDeviceStatusInfoPreferenceManager
import net.huray.omronsdk.utility.Handler

class Pc300AutoConnector {

    private var isAutoConnectorOn : MutableState<Boolean> = mutableStateOf(true)

    var autoConnectorStatus : State<Boolean> = isAutoConnectorOn

    fun updateAutoConnectorStatus(isOn : Boolean){
        isAutoConnectorOn.value = isOn
    }

    //observer the connenction state

    private fun observe(context: Context){
        when(MainActivity.pc300Repo.connectedPC300Device.value){
            null -> {
//                checkAndStart(context)
            }

            else -> {
                connectingDeviceId = ""
            }
        }
    }

    fun updateLastConnectedDeviceId(context : Context, deviceId : String){
        BleDeviceStatusInfoPreferenceManager.getInstance(context).savePc300DeviceId(deviceId)
    }

    private fun getLastConnectedDeviceId(context: Context) : String?{
        return BleDeviceStatusInfoPreferenceManager.getInstance(context).getPc300DeviceId()
    }

    fun checkAndStart(context: Context){
        val deviceId = getLastConnectedDeviceId(context)
        if(!deviceId.isNullOrEmpty()){
            Log.d("TAG", "checkAndStart: trying to connect last pc300 device $deviceId")
            if(MainActivity.pc300Repo.connectedPC300Device.value == null && connectingDeviceId.isEmpty()){
                connectingDeviceId = deviceId
                observe(context)
                MainActivity.pc300Repo.scanPC300Device()
                startConnectingLastConnectedDevice()
            }else{
            }
        }else{
            Log.d("TAG", "checkAndStart: no pc300 devices to connect")
        }
    }

    private val runnable = object : Runnable {
        override fun run() {
            if(MainActivity.pc300Repo.isBleOnState.value){
                if(autoConnectorStatus.value){
                    Log.d("TAG", "checkAndStart: scanned pc300 device ${MainActivity.pc300Repo.deviceList.value}")
                    when(MainActivity.pc300Repo.connectedPC300Device.value){
                        null -> {
                            if (!MainActivity.pc300Repo.deviceList.value.isNullOrEmpty()) {
                                MainActivity.pc300Repo.deviceList.value!!.forEach { device ->
                                    if (device.address == connectingDeviceId && autoConnectorStatus.value) {
                                        Log.e("TAG", "checkAndStart: started connecting PC300 device $connectingDeviceId" + "${device.address}")
                                        MainActivity.pc300Repo.stopScanPC300Device()
                                        MainActivity.pc300Repo.connectPC300(device)
                                    }
                                }
                            }else{
                                MainActivity.pc300Repo.scanPC300Device()
                            }
                        }
                        else -> {
                            stopAutoConnecting()
                        }
                    }
                }else{
                    stopAutoConnecting()
                }
            }else{
                Log.e("TAG", "checkAndStart: Please enable bluetooth")
            }
            handler.postDelayed(this, 3000)
        }
    }

    private fun startConnectingLastConnectedDevice(){
        if(autoConnectorStatus.value){
            handler.post(runnable)
        }else{
            Log.e("TAG", "checkAndStart: Auto connector is off")
        }
    }

    fun stopAutoConnecting(){
        handler.removeCallbacks(runnable)
    }

    companion object {

        // Singleton instantiation you already know and love
        @Volatile private var instance: Pc300AutoConnector? = null

        var connectingDeviceId = ""

        val handler = Handler()

        fun getInstance() = instance ?: synchronized(this) { instance ?: Pc300AutoConnector().also { instance = it } }

    }
}