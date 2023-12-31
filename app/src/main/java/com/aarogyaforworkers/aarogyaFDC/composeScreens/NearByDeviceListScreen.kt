package com.aarogyaforworkers.aarogyaFDC.composeScreens

import Commons.ConnectionPageTags
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import com.aarogyaforworkers.aarogyaFDC.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.Omron.OmronRepository
import com.aarogyaforworkers.aarogyaFDC.PC300.PC300Repository
import com.aarogyaforworkers.aarogyaFDC.checkBluetooth
import com.aarogyaforworkers.aarogyaFDC.isBluetoothEnabled

var deviceType = 0

@SuppressLint("MissingPermission")
@Composable
fun NearByDeviceListScreen(navHostController: NavHostController, pC300Repository: PC300Repository, omronRepository: OmronRepository){

    val context = LocalContext.current

    var bleEnabled by remember { mutableStateOf(isBluetoothEnabled()) }

    if(!bleEnabled) checkBluetooth(context)

    var isConnecting by remember {
        mutableStateOf(false)
    }

    var previousPc300UpdatedValue by remember {
        mutableStateOf(false)
    }

    var previousOmronUpdatedValue by remember {
        mutableStateOf(false)
    }

    var previousTrackyUpdatedValue by remember {
        mutableStateOf(false)
    }


    when(deviceType){

        0 -> {

            // update Pc300 connection status

            if(!pC300Repository.pc300connectionStatus.value && isConnecting) {
                showProgress()
            }

            if(pC300Repository.pc300connectionStatus.value) {
                if(pC300Repository.pc300connectionStatus.value != previousPc300UpdatedValue){
                    navHostController.navigate(Destination.DeviceConnection.routes)
                    previousPc300UpdatedValue = pC300Repository.pc300connectionStatus.value
                }
            }
        }

        1 -> {

            // update Omron connection status

            if(!omronRepository.omronConnectionStatus.value && isConnecting) {
                showProgress()
            }

            if(omronRepository.omronConnectionStatus.value){
                if(omronRepository.omronConnectionStatus.value != previousOmronUpdatedValue){
                    navHostController.navigate(Destination.DeviceConnection.routes)
                    previousOmronUpdatedValue = omronRepository.omronConnectionStatus.value
                }
            }
        }

        2 -> {

            if(MainActivity.trackyRepo.trackyConnectionState.value == false && isConnecting) {
                showProgress()
            }

            if(MainActivity.trackyRepo.trackyConnectionState.value == true) {
                if(MainActivity.trackyRepo.trackyConnectionState.value != previousTrackyUpdatedValue){
                    navHostController.navigate(Destination.DeviceConnection.routes)
                    previousTrackyUpdatedValue = MainActivity.trackyRepo.trackyConnectionState.value!!
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().testTag(ConnectionPageTags.shared.bleScanningScreen)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navHostController.navigate(Destination.DeviceConnection.routes) }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.back_btn_icon),
                    contentDescription = "BackBtn"
                )
            }
            IconButton(onClick = {
                when(deviceType){
                    0 -> {
                        pC300Repository.updateDeviceList(null)
                        pC300Repository.scanPC300Device()
                    }

                    1 -> {
                        omronRepository.startScan()
                    }

                    2 -> {
                        MainActivity.trackyRepo.reScanTrackyDevice()
                    }
                }
            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.refresh_icon),
                    contentDescription = "RefreshBtn"
                )
            }
        }



        when(deviceType){

            0 -> {
                LazyColumn(modifier = Modifier.fillMaxSize()){
                    if(pC300Repository.deviceList.value != null){
                        items(pC300Repository.deviceList.value!!){ device ->
                            Box(
                                modifier = Modifier.clickable(onClick = {
                                    // connect pc300 device
                                    isConnecting = true
                                    pC300Repository.connectPC300(device)
                                })
                            ){
                                Card(modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(), shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(modifier = Modifier
                                        .padding(10.dp)) {
                                        Text(text = device.name, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.height(5.dp))
                                        Text(text = device.address, fontSize = 16.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                LazyColumn(modifier = Modifier.fillMaxSize()){
                    if(omronRepository.deviceList.value != null){
                        items(omronRepository.deviceList.value!!){ device ->
                            Box(
                                modifier = Modifier.clickable(onClick = {
                                    // connect omron device
                                    isConnecting = true
                                    omronRepository.connectOmronDevice(device)
                                    Log.d("TAG", "NearByDeviceListScreen: ")
                                })
                            ){
                                Card(modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(), shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(modifier = Modifier
                                        .padding(10.dp)) {
                                        Text(text = device.localName.toString(), fontSize = 16.sp)
                                        Spacer(modifier = Modifier.height(5.dp))
                                        Text(text = device.address, fontSize = 16.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                LazyColumn(modifier = Modifier.fillMaxSize()){
                    if(MainActivity.trackyRepo.deviceList.value != null){
                        items(MainActivity.trackyRepo.deviceList.value!!){ device ->
                            Box(
                                modifier = Modifier.clickable(onClick = {
                                    // connect pc300 device
                                    isConnecting = true
                                    MainActivity.trackyRepo.connect(device)
                                })
                            ){
                                Card(modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(), shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(modifier = Modifier
                                        .padding(10.dp)) {
                                        Text(text = device.name, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.height(5.dp))
                                        Text(text = device.bluetoothName, fontSize = 16.sp)
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}