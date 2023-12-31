package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogya.composeScreens.isFromVital
import com.aarogyaforworkers.aarogyaFDC.Commons.selectedSession
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

@Composable
fun SetCalanderScreen(navHostController: NavHostController) {

    val context = LocalContext.current

    var isUpdating by remember { mutableStateOf(false) }

    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var pickedDate = remember { mutableStateOf("") }

    if(isFromVital){

        when(MainActivity.sessionRepo.sessionCreatedStatus.value){

            true -> {
                selectedSession = MainActivity.sessionRepo.selectedsession!!
                MainActivity.pc300Repo.clearSessionValues()
                MainActivity.subUserRepo.getSessionsByUserID(userId = MainActivity.adminDBRepo.getSelectedSubUserProfile().user_id)
                isSessionPlayedOnUserHome = false
                MainActivity.sessionRepo.updateIsSessionCreatedStatus(null)
                navHostController.navigate(Destination.SessionSummary.routes)
                CoroutineScope(Dispatchers.Main).launch { delay(3000)
                    MainActivity.sessionRepo.clearImageList()
                    isUpdating = false
                    MainActivity.subUserRepo.updateIsAnyUpdateThere(false)
                }
            }

            false -> {

                isUpdating = false

                isSessionPlayedOnUserHome = false

                Toast.makeText(context, "Something went wrong please try again", Toast.LENGTH_SHORT).show()

                MainActivity.sessionRepo.updateIsSessionCreatedStatus(null)
            }

            null -> {

            }
        }

    }

    Column(
        Modifier
            .fillMaxSize()
    ) {
            Spacer(modifier = Modifier.height(15.dp))

            TopBarWithEditBtn(title = "Follow-up Date")

            Spacer(modifier = Modifier.height(40.dp))

        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)) {
            CalendarView(
                defSelectedDate = selectedDate,
                onDateSelected = { newDate ->
                    selectedDate = newDate
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
                val session = MainActivity.sessionRepo.selectedsession
                session!!.nextVisit = pickedDate.value
                isUpdating = true
                MainActivity.sessionRepo.createSession(session)
            }, Modifier.fillMaxWidth())
        }
    }

    if(isUpdating) showProgress()
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView_(defSelectedDate: Calendar, onDateSelected: (Calendar) -> Unit, onSelected: (String) -> Unit) {

    var currentMonth by remember { mutableStateOf(defSelectedDate) }
    val displayName = remember {
        mutableStateOf("")
    }
    displayName.value = currentMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
    val daysOfWeek = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfMonth = currentMonth.clone() as Calendar
    firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)
    val startDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1
//    val currentDate = defSelectedDate
    val localCurrentDate = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    var formattedDate = remember { mutableStateOf("") }
    var selectedCalendarNew by remember { mutableStateOf(Calendar.getInstance()) }




    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Month navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    val newMonth = currentMonth.clone() as Calendar
                    newMonth.add(Calendar.MONTH, -1)
                    currentMonth = newMonth
                    displayName.value = currentMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
                }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
            }
            RegularTextView(
                title = "${displayName.value} ${currentMonth.get(Calendar.YEAR)}",
                fontSize = 16
            )
            IconButton(
                onClick = {
                    val newMonth = currentMonth.clone() as Calendar
                    newMonth.add(Calendar.MONTH, 1)
                    currentMonth = newMonth
                    displayName.value = currentMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
                }
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Days of the week headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (day in daysOfWeek) {
                RegularTextView(
                    title = day,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        for (i in 0 until (daysInMonth + startDayOfWeek)) {
            if (i % 7 == 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (j in 0 until 7) {
                        val dayIndex = i - startDayOfWeek + 1 + j
                        if (dayIndex > 0 && dayIndex <= daysInMonth) {
                            val day = Calendar.getInstance().apply {
                                time = currentMonth.time
                                set(Calendar.DAY_OF_MONTH, dayIndex)
                            }

                            val isSelected = day == selectedCalendarNew
                            val isOldSelected = day == defSelectedDate
                            val isPastDate = day.before(localCurrentDate)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .background(
                                        when {
                                            isSelected -> Color.LightGray
                                            isPastDate -> Color.Transparent
                                            isOldSelected -> Color.Gray
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable {
                                        if (!isPastDate) {
                                            formattedDate.value =
                                                "${day.get(Calendar.DAY_OF_MONTH)}/${
                                                    day.get(Calendar.MONTH) + 1
                                                }/${day.get(Calendar.YEAR)}"
                                            selectedCalendarNew = day
                                        } else {
                                            // Handle past dates
                                            formattedDate.value =
                                                "${defSelectedDate.get(Calendar.DAY_OF_MONTH)}/${
                                                    defSelectedDate.get(Calendar.MONTH) + 1
                                                }/${defSelectedDate.get(Calendar.YEAR)}"
                                            selectedCalendarNew = defSelectedDate
                                        }
                                        onDateSelected(selectedCalendarNew)
                                        onSelected(formattedDate.value)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                RegularTextView(
                                    title = day.get(Calendar.DAY_OF_MONTH).toString(),
                                    fontSize = 14,
                                    textColor = if (isSelected || isOldSelected) Color.White else Color.Black
                                )
                            }

                        } else {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CalendarView(defSelectedDate: Calendar, onDateSelected: (Calendar) -> Unit, onSelected: (String) -> Unit) {
    var currentMonth by remember {
        mutableStateOf(defSelectedDate.clone() as Calendar)
    }
    val displayName = remember {
        mutableStateOf("")
    }
    displayName.value = currentMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
    val daysOfWeek = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfMonth = currentMonth.clone() as Calendar
    firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)
    val startDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Month navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    val newMonth = currentMonth.clone() as Calendar
                    newMonth.add(Calendar.MONTH, -1)
                    currentMonth = newMonth
                    displayName.value = currentMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
                }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
            }
            RegularTextView(
                title = "${displayName.value} ${currentMonth.get(Calendar.YEAR)}",
                fontSize = 16
            )
            IconButton(
                onClick = {
                    val newMonth = currentMonth.clone() as Calendar
                    newMonth.add(Calendar.MONTH, 1)
                    currentMonth = newMonth
                    displayName.value = currentMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
                }
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Days of the week headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (day in daysOfWeek) {
                RegularTextView(
                    title = day,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        for (i in 0 until (daysInMonth + startDayOfWeek)) {
            if (i % 7 == 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (j in 0 until 7) {
                        val dayIndex = i - startDayOfWeek + 1 + j
                        if (dayIndex > 0 && dayIndex <= daysInMonth) {
                            val day = Calendar.getInstance().apply {
                                time = currentMonth.time
                                set(Calendar.DAY_OF_MONTH, dayIndex)
                            }

                            val isSelected = day == defSelectedDate
                            val isCurrentDay = day.get(Calendar.YEAR) == defSelectedDate.get(Calendar.YEAR) &&
                                    day.get(Calendar.MONTH) == defSelectedDate.get(Calendar.MONTH) &&
                                    day.get(Calendar.DAY_OF_MONTH) == defSelectedDate.get(Calendar.DAY_OF_MONTH)
                            val isPastDate = day.before(defSelectedDate)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .background(
                                        when {
                                            isSelected -> Color.Gray
                                            isCurrentDay -> Color.LightGray // Highlight the default selected date
                                            isPastDate -> Color.Transparent // Disable past dates
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable {
                                        if (!isPastDate) {
                                            val formattedDate =
                                                "${day.get(Calendar.DAY_OF_MONTH)}/${
                                                    day.get(
                                                        Calendar.MONTH
                                                    ) + 1
                                                }/${day.get(Calendar.YEAR)}"
                                            onSelected(formattedDate)
                                            onDateSelected(day)
                                        } else {
                                            // Handle past dates
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                RegularTextView(
                                    title = day.get(Calendar.DAY_OF_MONTH).toString(),
                                    fontSize = 14,
                                    textColor = if (isSelected) Color.White else Color.Black
                                )
                            }
                        } else {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditCalendarView(selectedDate: Calendar, onDateSelected: (Calendar) -> Unit, onSelected: (String) -> Unit) {
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    val displayName = remember {
        mutableStateOf("")
    }
    displayName.value = currentMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
    val daysOfWeek = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfMonth = currentMonth.clone() as Calendar
    firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)
    val startDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1
    val currentDate = selectedDate

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Month navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    val newMonth = currentMonth.clone() as Calendar
                    newMonth.add(Calendar.MONTH, -1)
                    currentMonth = newMonth
                    displayName.value = currentMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
                }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
            }
            RegularTextView(
                title = "${displayName.value} ${currentMonth.get(Calendar.YEAR)}",
                fontSize = 16
            )
            IconButton(
                onClick = {
                    val newMonth = currentMonth.clone() as Calendar
                    newMonth.add(Calendar.MONTH, 1)
                    currentMonth = newMonth
                    displayName.value = currentMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
                }
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Days of the week headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (day in daysOfWeek) {
                RegularTextView(
                    title = day,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        for (i in 0 until (daysInMonth + startDayOfWeek)) {
            if (i % 7 == 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (j in 0 until 7) {
                        val dayIndex = i - startDayOfWeek + 1 + j
                        if (dayIndex > 0 && dayIndex <= daysInMonth) {
                            val day = Calendar.getInstance().apply {
                                time = currentMonth.time
                                set(Calendar.DAY_OF_MONTH, dayIndex)
                            }

                            val isSelected = day == selectedDate
                            val isCurrentDay = day.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                                    day.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
                                    day.get(Calendar.DAY_OF_MONTH) == currentDate.get(Calendar.DAY_OF_MONTH)
                            val isPastDate = day.before(currentDate)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .background(
                                        when {
                                            isSelected -> Color.Gray
                                            isCurrentDay -> Color.LightGray // Highlight current day
                                            isPastDate -> Color.Transparent // Disable past dates
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable {
                                        if (!isPastDate) {
                                            val formattedDate =
                                                "${day.get(Calendar.DAY_OF_MONTH)}/${
                                                    day.get(
                                                        Calendar.MONTH
                                                    ) + 1
                                                }/${day.get(Calendar.YEAR)}"
                                            onSelected(formattedDate)
                                            onDateSelected(day)
                                        } else {
                                            // Handle past dates
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                RegularTextView(
                                    title = day.get(Calendar.DAY_OF_MONTH).toString(),
                                    fontSize = 14,
                                    textColor = if (isSelected) Color.White else Color.Black
                                )
                            }
                        } else {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun CalendarView(selectedDate: Calendar, onDateSelected: (Calendar) -> Unit) {
    var currentMonth by remember {
        mutableStateOf(selectedDate.clone() as Calendar)
    }

    val daysOfWeek = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfMonth = currentMonth.clone() as Calendar
    firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)
    val startDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Month navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    val newMonth = currentMonth.clone() as Calendar
                    newMonth.add(Calendar.MONTH, -1)
                    currentMonth = newMonth
                }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
            }
            Text(
                text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentMonth.time),
                fontSize = 16.sp
            )
            IconButton(
                onClick = {
                    val newMonth = currentMonth.clone() as Calendar
                    newMonth.add(Calendar.MONTH, 1)
                    currentMonth = newMonth
                }
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Days of the week headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (day in daysOfWeek) {
                Text(
                    text = day,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        for (i in 0 until (daysInMonth + startDayOfWeek)) {
            if (i % 7 == 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (j in 0 until 7) {
                        val dayIndex = i - startDayOfWeek + 1 + j
                        if (dayIndex > 0 && dayIndex <= daysInMonth) {
                            val day = Calendar.getInstance().apply {
                                time = currentMonth.time
                                set(Calendar.DAY_OF_MONTH, dayIndex)
                            }

                            val isSelected = day == selectedDate
                            val isCurrentDay = day.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
                                    day.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) &&
                                    day.get(Calendar.DAY_OF_MONTH) == selectedDate.get(Calendar.DAY_OF_MONTH)
                            val isPastDate = day.before(selectedDate)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .background(
                                        when {
                                            isSelected -> Color.Gray
                                            isCurrentDay -> Color.Transparent // Don't select the current day
                                            isPastDate -> Color.Transparent // Disable past dates
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable {
                                        if (!isPastDate) {
                                            onDateSelected(day)
                                        } else {
                                            // Handle past dates
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.get(Calendar.DAY_OF_MONTH).toString(),
                                    fontSize = 14.sp,
                                    color = if (isSelected) Color.White else Color.Black
                                )
                            }
                        } else {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}


