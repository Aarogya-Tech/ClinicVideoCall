package com.aarogyaforworkers.aarogyaFDC.storage

import android.content.Context
import com.aarogyaforworkers.awsapi.models.Session
import com.google.gson.Gson

class BleDeviceStatusInfoPreferenceManager private constructor(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("BleDevice", Context.MODE_PRIVATE)
    private val omronKey = "omron"
    private val pc300Key = "pc300"
    private val trackyKey = "tracky"


    fun savePc300DeviceId(id: String) {
        sharedPreferences.edit().putString(pc300Key, id).apply()
    }

    fun saveOmronDeviceId(id: String) {
        sharedPreferences.edit().putString(omronKey, id).apply()
    }

    fun saveTrackyDeviceId(id: String) {
        sharedPreferences.edit().putString(trackyKey, id).apply()
    }

    fun getPc300DeviceId() : String?{
        return sharedPreferences.getString(pc300Key, "")
    }

    fun getOmronDeviceId() : String?{
        return sharedPreferences.getString(omronKey, "")
    }

    fun getTrackyDeviceId() : String?{
        return sharedPreferences.getString(omronKey, "")
    }

    companion object {

        @Volatile
        private var instance: BleDeviceStatusInfoPreferenceManager? = null

        fun getInstance(context: Context): BleDeviceStatusInfoPreferenceManager =
            instance ?: synchronized(this) {
                instance ?: BleDeviceStatusInfoPreferenceManager(context).also { instance = it }
            }
    }
}

