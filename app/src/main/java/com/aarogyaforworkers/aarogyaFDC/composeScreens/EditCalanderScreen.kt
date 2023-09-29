package com.aarogyaforworkers.aarogyaFDC.composeScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import java.util.Calendar

var isECSetUpDone = false
var isFromECSave = false
var isECDoneClick = false

@Composable
fun EditCalanderScreen(navHostController: NavHostController) {

    val onDonePressed= remember { mutableStateOf(false) }

    val selectedSession_Imp = MainActivity.sessionRepo.selectedsession


    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }

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
            isUpdating.value = false
            MainActivity.subUserRepo.updateIsAnyUpdateThere(false)
            if(isECDoneClick) {
                navHostController.navigate(Destination.UserHome.routes)
            }
            MainActivity.subUserRepo.getSessionsByUserID(userId = MainActivity.adminDBRepo.getSelectedSubUserProfile().user_id)
        }

        false -> {
            isUpdating.value = false
            MainActivity.sessionRepo.updateIsSessionUpdatedStatus(null)
        }

        null -> {

        }

    }

    Column(
        Modifier
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(15.dp))

        TopBarWithBackEditBtn(onBackClick = {
            if(MainActivity.subUserRepo.anyUpdateThere.value) {
                onDonePressed.value = true
            }
            else {
                navHostController.navigate(Destination.UserHome.routes)
            } },
            title = "Edit Follow-up Date",
            onSaveClick = {
                //on save btn click
                val session = MainActivity.sessionRepo.selectedsession
                session!!.nextVisit = selectedDate.toString()
                isUpdating.value = true
                MainActivity.sessionRepo.updateSession(session)
//                isFromIPSave = true
//                val text = impressionPlan.value
//                val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
//                selectedSession_Imp.ImpressionPlan = "${text}-:-${newUpdatedList}"
//                MainActivity.sessionRepo.updateSession(selectedSession_Imp)
            })

        Column(Modifier.weight(1f).padding(horizontal = 16.dp)) {
            CalendarView(
                selectedDate = selectedDate,
                onDateSelected = { newDate ->
                    selectedDate = newDate
                    MainActivity.subUserRepo.updateIsAnyUpdateThere(true)
                }, onSelected = {
//                    val session = MainActivity.sessionRepo.selectedsession
//                    session!!.nextVisit = it
//                    isUpdating.value = true
//                    MainActivity.sessionRepo.updateSession(session)
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
                session!!.nextVisit = selectedDate.toString()
                isECDoneClick = true
                MainActivity.sessionRepo.updateSession(session)
            }, Modifier.fillMaxWidth())
        }
    }
    if(isUpdating.value) showProgress()
}