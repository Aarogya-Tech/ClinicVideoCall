package com.aarogyaforworkers.aarogya.composeScreens

import android.content.Context
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.composeScreens.BloodPressure
import com.aarogyaforworkers.aarogyaFDC.composeScreens.BoldTextView
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Disableback
import com.aarogyaforworkers.aarogyaFDC.composeScreens.ECG
import com.aarogyaforworkers.aarogyaFDC.composeScreens.EcgAlert
import com.aarogyaforworkers.aarogyaFDC.composeScreens.HeartRate
import com.aarogyaforworkers.aarogyaFDC.composeScreens.PopBtnDouble
import com.aarogyaforworkers.aarogyaFDC.composeScreens.PopUpBtnSingle
import com.aarogyaforworkers.aarogyaFDC.composeScreens.RealtimeEcgAlertView
import com.aarogyaforworkers.aarogyaFDC.composeScreens.RegularTextView
import com.aarogyaforworkers.aarogyaFDC.composeScreens.SPO2
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Temperature
import com.aarogyaforworkers.aarogyaFDC.composeScreens.TitleViewWithCancelBtn
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Weight
import com.aarogyaforworkers.aarogyaFDC.composeScreens.isIPSetUpDone
import com.aarogyaforworkers.aarogyaFDC.composeScreens.isLRSetUpDone
import com.aarogyaforworkers.aarogyaFDC.composeScreens.isPESetUpDone

var isFromVital = false

@Composable
fun VitalCollectionScreen(navHostController: NavHostController){
    Disableback()


    val context = LocalContext.current

    var isShowEcgAlert by remember {
        mutableStateOf(false)
    }
    var isHelpAlert = remember { mutableStateOf(false) }

    if(isShowEcgAlert) EcgAlert(title = "ECG Result", subTitle = MainActivity.pc300Repo.getEcgResultMsgBasedOnCode(context)) {
        isShowEcgAlert = false
    }

    if(MainActivity.pc300Repo.showEcgRealtimeAlert.value) RealtimeEcgAlertView()


    if(isHelpAlert.value){
        HelpAlert(onCancelClick = { isHelpAlert.value = false }) {
            //onClick
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 15.dp, end = 15.dp, top = 40.dp)) {




        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                BoldTextView(title = "Vitals", fontSize = 20)

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End,) {
                if (!MainActivity.subUserRepo.bufferThere.value) {
                    IconButton(onClick = { navHostController.navigate(Destination.UserHome.routes) },
                        modifier = Modifier
                        .size(30.dp) // Adjust the size of the circular border
                        .border(
                            width = 2.dp, // Adjust the border width
                            color = Color.Black, // Change the border color when in edit mode
                            shape = CircleShape
                        )) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "CloseVital")
                    }
                }
            }
        }

            //BoldTextView(title = "Vitals", fontSize = 20)
//            if(!MainActivity.subUserRepo.bufferThere.value){
//                Box(modifier = Modifier.fillMaxWidth() ,contentAlignment = Alignment.CenterEnd) {
//                    IconButton(onClick = { navHostController.navigate(Destination.UserHome.routes) }) {
//                        Icon(imageVector = Icons.Default.Close, contentDescription = "CloseVital")
//                    }
//                }
//            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(30.dp)) {
                BloodPressure(MainActivity.pc300Repo, LocalContext.current)

//                Spacer(modifier = Modifier.height(10.dp))

                Temperature(MainActivity.pc300Repo)

//                Spacer(modifier = Modifier.height(10.dp))

                SPO2(MainActivity.pc300Repo)

            }
            Spacer(modifier = Modifier.width(30.dp))
            Column(verticalArrangement = Arrangement.spacedBy(30.dp)) {
                HeartRate(MainActivity.pc300Repo)

//                Spacer(modifier = Modifier.height(10.dp))

                Weight(MainActivity.omronRepo)

//                Spacer(modifier = Modifier.height(10.dp))

                ECG(pc300Repository = MainActivity.pc300Repo, context = LocalContext.current) {
                    isShowEcgAlert = true
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { isHelpAlert.value = true }) {
                BoldTextView(title = "Help?", textColor = Color(0xFF397EF5), fontSize = 16)
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .weight(1f), verticalAlignment = Alignment.Bottom) {
            PopUpBtnSingle(btnName = "Next", {
                isFromVital = true
                isPESetUpDone = false
                isLRSetUpDone = false
                isIPSetUpDone = false
                val selectedSession = MainActivity.subUserRepo.getSession()
                MainActivity.sessionRepo.selectedsession = selectedSession
                MainActivity.subUserRepo.updateEditTextEnable(true)
                navHostController.navigate(Destination.PhysicalExaminationScreen.routes)
            }, Modifier.fillMaxWidth())
        }
    }
}



@Composable
fun HelpAlert(onCancelClick: () -> Unit, onClickContactUs: () -> Unit ){
    AlertDialog(onDismissRequest = { /*TODO*/ },
        title = {
            TitleViewWithCancelBtn(title = "How to take reading") {
                onCancelClick()
            }
        },
        confirmButton = { },
        text = {
            HelpContent(paragraph = "Please watch our YouTube playlist to learn how to take reading from a desired device", videoId = "wqikGiECnHM"){
                onClickContactUs()
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun HelpContent(paragraph: String, videoId: String, onClickContactUs: () -> Unit){
    Column() {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp), // adjust the height as necessary
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    settings.useWideViewPort = true
                    settings.loadWithOverviewMode = true
                    loadUrl("https://www.youtube.com/embed/$videoId")
                }
            })
        Spacer(modifier = Modifier.height(10.dp))
        RegularTextView(title = paragraph)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RegularTextView(title = "Still unable to take reading?")
            TextButton(onClick = {onClickContactUs}) {
                RegularTextView(title = "Contact Us", textColor = Color.Blue)
            }

        }
    }
}

@Preview
@Composable
fun prev(){
    VitalCollectionScreen(navHostController = rememberNavController())
}