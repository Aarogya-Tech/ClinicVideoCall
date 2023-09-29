package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.aarogyaforworkers.aarogya.composeScreens.isFromVital
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import java.lang.reflect.Array.set
import java.text.DateFormatSymbols
import java.util.*

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

    CalanderView(context, onSaveClick = {
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
fun CalanderView(context : Context, onSaveClick : (String) -> Unit, onCancel : (String) -> Unit){

    // Fetching the Local Context
    // Declaring integer values
    // for year, month and day
    val mYear: Int
    val mMonth: Int
    val mDay: Int
fun CalendarView(onSaveClick: (String) -> Unit, onCancel: (String) -> Unit) {
    var _date by remember { mutableStateOf("") }

    val context = LocalContext.current
    val currentCalendar = Calendar.getInstance()
    val year = currentCalendar.get(Calendar.YEAR)
    val month = currentCalendar.get(Calendar.MONTH)
    val day = currentCalendar.get(Calendar.DAY_OF_MONTH)

    // Initializing a Calendar
    val mCalendar = Calendar.getInstance()

    // Fetching current year, month and day
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    // Declaring a string value to
    // store date in string format
    val mDate = remember { mutableStateOf("") }

    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mDayOfMonth/${mMonth+1}/$mYear"
        }, mYear, mMonth, mDay
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
    mDatePickerDialog.setButton(
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
            selectedDate = selectedDate,
            onDateSelected = { newDate ->
                selectedDate = newDate
            }, onSelected = {

            }
        )
    }
}






