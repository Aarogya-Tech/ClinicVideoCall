package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogya.R
import com.kizitonwose.calendar.compose.CalendarLayoutInfo
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import kotlinx.coroutines.flow.filterNotNull
import java.time.DayOfWeek
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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

    showProgress()
    if(isUpdating.value) showProgress()

}
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

    datePickerDialog.setButton(
        DialogInterface.BUTTON_NEGATIVE,
        "Cancel"
    ) { _, _ ->
        onCancel(_date)
    }

    datePickerDialog.show()
}




