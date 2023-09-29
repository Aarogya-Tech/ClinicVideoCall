package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.aarogyaforworkers.aarogya.composeScreens.isFromVital
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import androidx.compose.material3.MaterialTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateAndTimePickerScreen(navHostController: NavHostController){

    val selectedSession = MainActivity.sessionRepo.selectedsession

    val isUpdating = remember { mutableStateOf(false) }

    val context = LocalContext.current

    if(isFromVital){

        when(MainActivity.sessionRepo.sessionCreatedStatus.value){

            true -> {
                Log.d("TAG", "selected session ImpressionPlanScreen: ${com.aarogyaforworkers.aarogyaFDC.Commons.selectedSession}")
                MainActivity.pc300Repo.clearSessionValues()
                MainActivity.subUserRepo.getSessionsByUserID(userId = MainActivity.adminDBRepo.getSelectedSubUserProfile().user_id)
                isSessionPlayedOnUserHome = false
                MainActivity.sessionRepo.updateIsSessionCreatedStatus(null)
                navHostController.navigate(Destination.SessionSummary.routes)
                CoroutineScope(Dispatchers.Main).launch { delay(3000)
                    MainActivity.sessionRepo.clearImageList()
                    isUpdating.value = false
                    MainActivity.subUserRepo.updateIsAnyUpdateThere(false)
                }
            }

            false -> {

                isUpdating.value = false

                isSessionPlayedOnUserHome = false

                Toast.makeText(context, "Something went wrong please try again", Toast.LENGTH_SHORT).show()

                MainActivity.sessionRepo.updateIsSessionCreatedStatus(null)
            }

            null -> {

            }
        }

    } else {
        when(MainActivity.sessionRepo.sessionUpdatedStatus.value){

            true -> {
                MainActivity.subUserRepo.getSessionsByUserID(userId = MainActivity.adminDBRepo.getSelectedSubUserProfile().user_id)
                MainActivity.sessionRepo.updateIsSessionUpdatedStatus(null)
                if(isFromIPSave || isIPDoneClick) {
                    MainActivity.subUserRepo.updateIsAnyUpdateThere(false)
                }
                isUpdating.value = false
                if(isIPDoneClick) {
                    navHostController.navigate(Destination.UserHome.routes)
                }
            }

            false -> {
                MainActivity.sessionRepo.updateIsSessionUpdatedStatus(null)
            }

            null -> {
            }

        }
    }

    CalendarView(onSaveClick = {
        if(selectedSession != null ) {
            selectedSession.nextVisit = it
            isUpdating.value = true
            if(isFromVital){
                MainActivity.sessionRepo.createSession(selectedSession)
            } else{
                MainActivity.sessionRepo.updateSession(selectedSession)
            }
        }
    }){
        if(selectedSession != null){
            if(isFromVital){
                isUpdating.value = true
                MainActivity.sessionRepo.createSession(selectedSession)
            }else{
                navHostController.navigate(Destination.UserHome.routes)
            }
        }
    }

    if(isUpdating.value) showProgress()

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarView(onSaveClick: (String) -> Unit, onCancel: (String) -> Unit) {
    var _date by remember { mutableStateOf("") }

    val context = LocalContext.current
    val currentCalendar = Calendar.getInstance()
    val year = currentCalendar.get(Calendar.YEAR)
    val month = currentCalendar.get(Calendar.MONTH)
    val day = currentCalendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
            _date = selectedDate
            onSaveClick(_date)  // Call onSaveClick here
        },
        year, month, day
    )

    // Add a cancel button with a callback
    datePickerDialog.setButton(
        DialogInterface.BUTTON_NEGATIVE,
        "Cancel"
    ) { _, _ ->
        onCancel(_date)
    }

    datePickerDialog.show()
}








@Preview
@Composable
fun CalendarViewDemo() {

    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Selected Date: ${selectedDate.time}",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        CalendarView(
            defSelectedDate = selectedDate,
            onDateSelected = { newDate ->
                selectedDate = newDate
            }, onSelected = {

            }
        )
    }
}






