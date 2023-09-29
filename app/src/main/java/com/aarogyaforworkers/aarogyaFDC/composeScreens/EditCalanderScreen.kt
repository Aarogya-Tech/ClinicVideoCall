package com.aarogyaforworkers.aarogyaFDC.composeScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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

@Composable
fun EditCalanderScreen(navHostController: NavHostController) {

    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }

    var isUpdating by remember { mutableStateOf(false) }

    when(MainActivity.sessionRepo.sessionUpdatedStatus.value){

        true -> {
            isUpdating = false
            navHostController.navigate(Destination.UserHome.routes)
            MainActivity.subUserRepo.getSessionsByUserID(userId = MainActivity.adminDBRepo.getSelectedSubUserProfile().user_id)
        }

        false -> {
            isUpdating = false
            MainActivity.sessionRepo.updateIsSessionUpdatedStatus(null)
        }

        null -> {

        }

    }

    Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        CalendarView(
            selectedDate = selectedDate,
            onDateSelected = { newDate ->
                selectedDate = newDate
            }, onSelected = {
                val session = MainActivity.sessionRepo.selectedsession
                session!!.nextVisit = it
                isUpdating = true
                MainActivity.sessionRepo.updateSession(session)
            }
        )
    }
    if(isUpdating) showProgress()
}