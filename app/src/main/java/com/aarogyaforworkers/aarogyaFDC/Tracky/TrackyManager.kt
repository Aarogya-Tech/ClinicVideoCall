package com.aarogyaforworkers.aarogyaFDC.Tracky

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.qingniu.qnble.utils.QNLogUtils
import com.qn.device.constant.CheckStatus
import com.qn.device.constant.QNHeightUnit
import com.qn.device.constant.QNInfoConst
import com.qn.device.constant.UserGoal
import com.qn.device.constant.UserShape
import com.qn.device.listener.QNBleConnectionChangeListener
import com.qn.device.listener.QNBleDeviceDiscoveryListener
import com.qn.device.listener.QNResultCallback
import com.qn.device.listener.QNScaleDataListener
import com.qn.device.out.QNBleApi
import com.qn.device.out.QNBleBroadcastDevice
import com.qn.device.out.QNBleDevice
import com.qn.device.out.QNBleKitchenDevice
import com.qn.device.out.QNIndicateConfig
import com.qn.device.out.QNScaleData
import com.qn.device.out.QNScaleStoreData
import java.text.SimpleDateFormat
import java.util.Date

class TrackyManager {

    private var qnBleApi : QNBleApi? = null

    private var isConnectedTrackyDevice : MutableState<QNBleDevice?> = mutableStateOf(null)

    private var isLatestDeviceData : MutableState<QNScaleData?> = mutableStateOf(null)

    var latestDeviceData : State<QNScaleData?> = isLatestDeviceData

    fun updateQnScaledData(data: QNScaleData?){
        isLatestDeviceData.value = data
    }

    var connectedTrackyDevice : State<QNBleDevice?> = isConnectedTrackyDevice

    private var isDeviceList : MutableState<ArrayList<QNBleDevice>?> = mutableStateOf(null)

    var deviceList : State<ArrayList<QNBleDevice>?> = isDeviceList

    private var deviceListL : ArrayList<QNBleDevice> = ArrayList()

    private val isTrackyConnectionStatus : MutableState<Boolean?> = mutableStateOf(null)

    val trackyConnectionState : MutableState<Boolean?> = isTrackyConnectionStatus

    fun updateConnectionState(isConnected : Boolean){
        isTrackyConnectionStatus.value = isConnected
    }

    fun updateConnectedDevice(device: QNBleDevice?){
        isConnectedTrackyDevice.value = device
    }

    fun updateDeviceList(list : ArrayList<QNBleDevice>?){
        isDeviceList.value = list
    }

    fun clearTracky(){
        isConnectedTrackyDevice.value = null
        connectedTrackyDevice = isConnectedTrackyDevice
        updateConnectionState(false)
    }

    fun getUser() : TrackyUser{
        val config = QNIndicateConfig()
        config.isShowBmi = true
        config.isShowWater = true
        config.isShowWater = true
        config.isShowUserName = true
        config.isShowBone = true
        config.isSound = true
        config.isShowFat = true
        config.isShowHeartRate = true
        config.isShowMuscle = true
        config.isShowWeather = true
        val user = TrackyUser("12", 165, "male", getDate(), QNInfoConst.CALC_ATHLETE, UserShape.SHAPE_NORMAL, UserGoal.GOAL_LOSE_FAT, 2.0, config)
        return user
    }

    private fun setUpConfig(){
        if(qnBleApi != null){
            val config = qnBleApi!!.config
            config.isEnhanceBleBroadcast = true
            config.scanOutTime = 6000
            config.duration = 10000
            config.connectOutTime = 30000
            config.heightUnit = QNHeightUnit.CM
            config.save(QNResultCallback { i, s ->
                Log.d("ScanActivity", "initData:$s")
            })
        }
    }

    fun stopScan(){
        if(qnBleApi != null){
            qnBleApi!!.stopBleDeviceDiscovery(QNResultCallback { i, s ->
                Log.d("TAG", "stopScan: $s")
                isDeviceList.value = arrayListOf()
            })
        }
    }

    fun setUpSdk(context : Context){

        val encryptPath = "file:///android_asset/Tracky20230303.qn"

        qnBleApi = QNBleApi.getInstance(context)

        qnBleApi?.initSdk(
            "Tracky20230303", encryptPath
        ) { code, msg -> Log.d("BaseApplication : QNBLE", "$msg")
            setUpConfig()
        }
    }

    private fun setUpBleListner(){

        qnBleApi?.setBleStateListener { p0 -> Log.d("TAG", "onBleSystemState: $p0") }

        qnBleApi?.setBleDeviceDiscoveryListener(object : QNBleDeviceDiscoveryListener {

            override fun onDeviceDiscover(device: QNBleDevice) {
                if(!deviceListL.contains(device)){
                    deviceListL.add(device)
                    updateDeviceList(deviceListL)
                }
            }

            override fun onStartScan() {
                QNLogUtils.log("ScanActivity", "onStartScan")
            }

            override fun onStopScan() {
                QNLogUtils.log("ScanActivity", "onStopScan")
            }

            override fun onScanFail(code: Int) {
                QNLogUtils.log("ScanActivity", "onScanFail:$code")
            }

            override fun onBroadcastDeviceDiscover(device: QNBleBroadcastDevice) {
                //BroadcastScaleActivity
            }

            override fun onKitchenDeviceDiscover(device: QNBleKitchenDevice) {
                // KitchenScaleActivity
                if (device.isBluetooth) {
                    //蓝牙厨房秤返回的对象不同，这里简单处理
                }
            }
        })

        setUpDeviceConnectionListner()

        setUpDeviceDataListner()

    }

    private fun setUpDeviceConnectionListner(){

        qnBleApi?.setBleConnectionChangeListener(object : QNBleConnectionChangeListener {

            override fun onConnecting(p0: QNBleDevice?) {
                Log.d("TAG", "Tracky : onConnecting: $p0")
                updateConnectionState(false)
                updateQnScaledData(null)
            }

            override fun onConnected(p0: QNBleDevice?) {
                Log.d("TAG", "Tracky : onConnected: $p0")
                if(p0 != null ){
                    updateConnectionState(true)
                    updateConnectedDevice(p0)
                }
            }

            override fun onServiceSearchComplete(p0: QNBleDevice?) {
                Log.d("TAG", "Tracky : onServiceSearchComplete: $p0")
            }

            override fun onStartInteracting(p0: QNBleDevice?) {
                Log.d("TAG", "Tracky : onStartInteracting: $p0")
            }

            override fun onDisconnecting(p0: QNBleDevice?) {
                Log.d("TAG", "Tracky : onDisconnecting: $p0")
                updateConnectionState(false)
                deviceListL = arrayListOf()
                updateDeviceList(deviceListL)
                updateQnScaledData(null)
                updateConnectedDevice(null)
            }

            override fun onDisconnected(p0: QNBleDevice?) {
                Log.d("TAG", "Tracky : onDisconnected: $p0")
                updateConnectionState(false)
            }

            override fun onConnectError(p0: QNBleDevice?, p1: Int) {
                Log.d("TAG", "Tracky : onerror: $p0")
            }
        })
    }

    private fun setUpDeviceDataListner(){

        qnBleApi?.setDataListener(object : QNScaleDataListener {

            override fun onGetUnsteadyWeight(p0: QNBleDevice?, p1: Double) {
                Log.d("TAG", "Tracky : onGetUnsteadyWeight: $p1")
            }

            override fun onGetScaleData(p0: QNBleDevice?, p1: QNScaleData?) {
                if(p1 != null){
                    Log.d("TAG", "Tracky : onGetScaleData: weight -> ${p1.bleScaleData.weight} water -> ${p1.bleScaleData.water}, fat% -> ${p1.bleScaleData.bodyfat}")
                }
                if(p1 != null) updateQnScaledData(p1)
            }

            override fun onGetStoredScale(p0: QNBleDevice?, p1: MutableList<QNScaleStoreData>?) {
                Log.d("TAG", "Tracky : onGetStoredScale: $p1")
            }

            override fun onGetElectric(p0: QNBleDevice?, p1: Int) {
                Log.d("TAG", "Tracky : onGetElectric: $p1")
            }

            override fun onScaleStateChange(p0: QNBleDevice?, p1: Int) {
                Log.d("TAG", "Tracky : onScaleStateChange: $p1")
            }

            override fun onScaleEventChange(p0: QNBleDevice?, p1: Int) {
                Log.d("TAG", "Tracky : onScaleEventChange: $p1")
            }

            override fun readSnComplete(p0: QNBleDevice?, p1: String?) {
                Log.d("TAG", "Tracky : readSnComplete: $p1")
            }
        })
    }

    fun scanTrackyDevice(context: Context){
        deviceListL = arrayListOf()
        setUpBleListner()
        qnBleApi?.startBleDeviceDiscovery(QNResultCallback { code, msg ->
            Log.d("ScanActivity", "code:$code;msg:$msg")
            if (code != CheckStatus.OK.code) {
                Toast.makeText(context, "$msg", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun reScanTrackyDevice(){
        deviceListL = arrayListOf()
        setUpBleListner()
        qnBleApi?.startBleDeviceDiscovery(QNResultCallback { code, msg ->
            Log.d("ScanActivity", "code:$code;msg:$msg")
        })
    }

    fun getDate(): Date {
        val pattern = "dd/MM/yyyy"
        val dateString = "20/06/1998"
        val dateFormat = SimpleDateFormat(pattern)
        val date = dateFormat.parse(dateString)
        return date
    }

    fun connect(device : QNBleDevice){
        updateConnectionState(false)
        val u = getUser()
        val t =  qnBleApi?.buildUser(u.userId!!,
            u.height,
            u.gender!!,
            u.birthDay!!,
            u.athleteType,
            u.choseShape!!,
            u.choseGoal!!,
            QNResultCallback { code, msg ->
                Log.d("ConnectActivity", "创建用户信息返回:$msg")
            })
        qnBleApi?.connectDevice(device, t, object : QNResultCallback{
            override fun onResult(code: Int, msg: String?) {
                Log.d("TAG", "onResultConnect: $msg")
            }
        })
    }

    fun disconnect(){
        if(connectedTrackyDevice.value != null){
            qnBleApi?.disconnectDevice(connectedTrackyDevice.value!!) {
              code, msg -> Log.d("ConnectActivity", "创建用户信息返回:$msg")
            }
        }else{
            updateConnectionState(false)
        }
    }

    companion object {
        // Singleton instantiation you already know and love
        @Volatile private var instance: TrackyManager? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: TrackyManager().also { instance = it }
        }
    }

}