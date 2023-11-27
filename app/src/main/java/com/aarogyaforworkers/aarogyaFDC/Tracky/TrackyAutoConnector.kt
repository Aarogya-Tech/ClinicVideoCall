package com.aarogyaforworkers.aarogyaFDC.Tracky

import android.content.Context
import android.util.Log
import com.aarogyaforworkers.aarogya.BluetoothTasks.AutoConnector.Pc300AutoConnector
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.storage.BleDeviceStatusInfoPreferenceManager
import net.huray.omronsdk.utility.Handler

class TrackyAutoConnector {


    var isAutoConnectorOn = true

    //observer the connenction state

    private fun observe(context: Context){
        when(MainActivity.trackyRepo.connectedTrackyDevice.value){

            null -> {
                checkAndStart(context)
            }

            else -> {
                connectingDeviceId = ""
            }
        }
    }

    fun updateLastConnectedDeviceId(context : Context, deviceId : String){
        BleDeviceStatusInfoPreferenceManager.getInstance(context).saveTrackyDeviceId(deviceId)
    }

    private fun getLastConnectedDeviceId(context: Context) : String?{
        return BleDeviceStatusInfoPreferenceManager.getInstance(context).getTrackyDeviceId()
    }

    fun checkAndStart(context: Context){
        val deviceId = getLastConnectedDeviceId(context)
        if(!deviceId.isNullOrEmpty()){
            if(MainActivity.trackyRepo.connectedTrackyDevice.value == null && connectingDeviceId.isEmpty()){
                connectingDeviceId = deviceId
                observe(context)
                MainActivity.trackyRepo.scanTrackyDevice(context)
                startConnectingLastConnectedDevice()
            }else{
                Log.d("TAG", "checkAndStart: device all ready connected device $deviceId")
            }
        }else{
            Log.d("TAG", "checkAndStart: no devices to connect")
        }
    }

    private val runnable = object : Runnable {
        override fun run() {
            if(MainActivity.pc300Repo.isBleOnState.value){
                if(isAutoConnectorOn){
                    Log.d("TAG", "checkAndStart: scanned device ${MainActivity.trackyRepo.deviceList.value}")
                    when(MainActivity.trackyRepo.connectedTrackyDevice.value){
                        null -> {
                            if (!MainActivity.trackyRepo.deviceList.value.isNullOrEmpty()) {
                                MainActivity.trackyRepo.deviceList.value!!.forEach { device ->
                                    if (device.mac == connectingDeviceId) {
                                        Log.d("TAG", "checkAndStart: started connecting device ${connectingDeviceId}" + "${device.mac}")
                                        MainActivity.trackyRepo.connect(device)
                                        MainActivity.trackyRepo.stopScan()
                                    }
                                }
                            }else{
                                MainActivity.trackyRepo.reScanTrackyDevice()
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
        @Volatile private var instance: TrackyAutoConnector? = null

        var connectingDeviceId = ""

        val handler = Handler()

        fun getInstance() = instance ?: synchronized(this) { instance ?: TrackyAutoConnector().also { instance = it } }

    }
}