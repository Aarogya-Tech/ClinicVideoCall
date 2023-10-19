package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

var isFromECSave = false
var isECDoneClick = false


// Add a function to parse the date string into a Calendar object
private fun parseDate(dateString: String): Calendar {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = dateFormat.parse(dateString)
    date?.let { calendar.time = it }
    return calendar
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditCalanderScreen(navHostController: NavHostController) {

    val onDonePressed= remember { mutableStateOf(false) }

    var pickedDate = remember { mutableStateOf("") }

    val selectedSession_Imp = MainActivity.sessionRepo.selectedsession

    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }

    if(selectedSession_Imp!!.nextVisit.isNotEmpty()){
        selectedDate = parseDate(selectedSession_Imp.nextVisit.toString())
    }

    var isUpdating = remember { mutableStateOf(false) }

    if(onDonePressed.value)
    {
        AlertView(
            showAlert = true,
            title = "Do you want to go back?",
            subTitle = "You have unsaved changes.Your changes will be discarded if you press Yes.",
            subTitle1 = "",
            onYesClick = {
                MainActivity.subUserRepo.updateIsAnyUpdateThere(false)
                navHostController.navigate(Destination.UserHome.routes) },
            onNoClick = { onDonePressed.value=false }) {
        }
    }

    when(MainActivity.sessionRepo.sessionUpdatedStatus.value){

        true -> {
            Log.d("TAG", "EditCalanderScreen:1 ${MainActivity.sessionRepo.sessionUpdatedStatus.value}")
            isUpdating.value = false
            if(isECDoneClick || isFromECSave){
                MainActivity.subUserRepo.updateIsAnyUpdateThere(false)
            }
            if(isECDoneClick) {
                navHostController.navigate(Destination.UserHome.routes)
            }
//            MainActivity.subUserRepo.getSessionsByUserID(userId = MainActivity.adminDBRepo.getSelectedSubUserProfile().user_id)
        }

        false -> {
            Log.d("TAG", "EditCalanderScreen:2 ${MainActivity.sessionRepo.sessionUpdatedStatus.value}")
            isUpdating.value = false
            MainActivity.sessionRepo.updateIsSessionUpdatedStatus(null)
        }

        null -> {
            Log.d("TAG", "EditCalanderScreen:3 ${MainActivity.sessionRepo.sessionUpdatedStatus.value}")
        }

    }

    Column(
        Modifier
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(15.dp))

        TopBarWithBackTitle(onBackClick = {
            if(MainActivity.subUserRepo.anyUpdateThere.value) {
                onDonePressed.value = true
            } else {
                navHostController.navigate(Destination.UserHome.routes)
            } },
            title = "Follow-up Date",
//            onSaveClick = {
//                //on save btn click
//                isFromECSave = true
//                val session = MainActivity.sessionRepo.selectedsession
//                session!!.nextVisit = selectedDate.toString()
//                isUpdating.value = true
//                MainActivity.sessionRepo.updateSession(session)
//            }
        )
        Spacer(modifier = Modifier.height(40.dp))

        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)) {
            CalendarView_(
                defSelectedDate = selectedDate,
                onDateSelected = { newDate ->
                    selectedDate = newDate
                    MainActivity.subUserRepo.updateIsAnyUpdateThere(true)
                }, onSelected = {
                    pickedDate.value = it
                }
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp)) {
            PopUpBtnSingle(btnName = "Done", {
                isUpdating.value = true
                val session = MainActivity.sessionRepo.selectedsession
                session!!.nextVisit = pickedDate.value
                isECDoneClick = true
                MainActivity.sessionRepo.updateSession(session)
            }, Modifier.fillMaxWidth())
        }
    }
    if(isUpdating.value) showProgress()
}