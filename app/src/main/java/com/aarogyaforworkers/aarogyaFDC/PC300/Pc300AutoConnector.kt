package com.aarogyaforworkers.aarogya.BluetoothTasks.AutoConnector

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.storage.BleDeviceStatusInfoPreferenceManager
import net.huray.omronsdk.utility.Handler

class Pc300AutoConnector {

    var isAutoConnectorOn = true

    //observer the connenction state

    private fun observe(context: Context){
        when(MainActivity.pc300Repo.connectedPC300Device.value){

            null -> {
                checkAndStart(context)
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
            if(MainActivity.pc300Repo.connectedPC300Device.value == null && connectingDeviceId.isEmpty()){
                connectingDeviceId = deviceId
                observe(context)
                MainActivity.pc300Repo.scanPC300Device()
                startConnectingLastConnectedDevice()
            }else{
//                Log.d("TAG", "checkAndStart: device all ready connected device $deviceId")
            }
        }else{
//            Log.d("TAG", "checkAndStart: no devices to connect")
        }
    }

    private val runnable = object : Runnable {
        override fun run() {
            if(MainActivity.pc300Repo.isBleOnState.value){
                if(isAutoConnectorOn){
                    Log.d("TAG", "checkAndStart: scanned device ${MainActivity.pc300Repo.deviceList.value}")
                    when(MainActivity.pc300Repo.connectedPC300Device.value){
                        null -> {
                            if (!MainActivity.pc300Repo.deviceList.value.isNullOrEmpty()) {
                                MainActivity.pc300Repo.deviceList.value!!.forEach { device ->
                                    if (device.address == connectingDeviceId) {
                                        Log.d("TAG", "checkAndStart: started connecting device $connectingDeviceId" +
                                                "${device.address}")
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
            handler.postDelayed(this, 5000)
        }
    }

    private fun startConnectingLastConnectedDevice(){
        if(isAutoConnectorOn){
            handler.post(runnable)
        }else{
            Log.e("TAG", "checkAndStart: Auto connector is off")
        }
    }

    private fun stopAutoConnecting(){
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