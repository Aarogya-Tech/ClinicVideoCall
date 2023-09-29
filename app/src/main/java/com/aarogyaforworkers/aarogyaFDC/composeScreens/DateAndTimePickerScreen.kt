package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.app.DatePickerDialog
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.twilio.rest.bulkexports.v1.export.Day
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatterBuilder

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DateAndTimePickerScreen(navHostController: NavHostController){
    val selecetdDate = remember {
        mutableStateOf("")
    }

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(0) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library

    Column {



        val state = rememberCalendarState(
            startMonth = startMonth,
            endMonth = endMonth,
            firstVisibleMonth = currentMonth,
            firstDayOfWeek = firstDayOfWeek
        )

        HorizontalCalendar(
            state = state,
            dayContent = {  Day(it) }
        )


//        DatePicker(state = )

//        DatePickerDialog(onDismissRequest = { /*TODO*/ },
//            onDateChange = {
//                selecetdDate.value = it.toString()
//                Log.d("TAG", "DateAndTimePickerScreen:$selecetdDate ")
//            },
//            initialDate = LocalDate.now()
//
//        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Day(day: CalendarDay) {
    Box(
        modifier = Modifier
            .aspectRatio(1f), // This is important for square sizing!
        contentAlignment = Alignment.Center
    ) {
        Text(text = day.date.dayOfMonth.toString())
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun previewDateTime(){
    DateAndTimePickerScreen(navHostController = rememberNavController())
}