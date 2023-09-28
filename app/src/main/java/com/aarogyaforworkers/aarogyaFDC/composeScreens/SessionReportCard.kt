package com.aarogyaforworkers.aarogyaFDC.composeScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aarogyaforworkers.aarogya.R
import com.aarogyaforworkers.awsapi.models.AdminProfile
import com.aarogyaforworkers.awsapi.models.Session
import com.aarogyaforworkers.awsapi.models.SubUserProfile

@Composable
fun SessionSummaryCard(session : Session, patient : SubUserProfile, doctor : AdminProfile) {

    val cardHeight = with(LocalDensity.current) {
        800.dp.toPx() // Minimum height
    }

    val chiefComplaintHeight = with(LocalDensity.current) {
        // Measure the height of chief complaint text based on its length
        val textHeight = remember {
            // You may adjust this value based on your layout and font size
            20.dp.toPx()
        }
        // Calculate the required height based on the number of lines for chief complaint
        val lines1 = patient.chiefComplaint.split("-:-").first().split('\n').size
        val lines2 = patient.PastMedicalSurgicalHistory.split("-:-").first().split('\n').size
        val tLines = lines1 + lines2
        val requiredHeight = maxOf(cardHeight, tLines * textHeight)
        requiredHeight.dp.toPx()
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(chiefComplaintHeight.dp)
            .background(color = Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {



        }



        Text(
            text = doctor.hospitalName,
            color = Color.Black,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium),
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .offset(x = 0.dp,
                    y = 30.dp))
        Box(
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(x = 211.dp,
                    y = 75.dp)
                .requiredWidth(width = 133.dp)
                .requiredHeight(height = 12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Powered by:",
                    color = Color.Black,
                    style = TextStyle(fontSize = 10.sp))
                Box(
                    modifier = Modifier
                        .requiredWidth(width = 15.dp)
                        .requiredHeight(height = 15.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_app),
                        contentDescription = "image 3",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .align(alignment = Alignment.CenterStart)
                            .offset(x = 0.dp, y = 0.dp)
                            .requiredWidth(width = 14.dp)
                            .requiredHeight(height = 14.dp))
                }
                Text(
                    text = "Aarogya Tech",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 10.sp))
            }
        }

        Box(
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(x = 16.dp,
                    y = 108.0361328125.dp)
                .requiredWidth(width = 328.dp)
                .requiredHeight(height = 535.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium)) {append("${doctor.first_name}, ")}
                    withStyle(style = SpanStyle(
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Light)) {append("${doctor.designation}")}})
            Text(
                text = "Patient Reg.No: ${patient.user_id}",
                color = Color.Black,
                style = TextStyle(
                    fontSize = 14.sp),
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)
                    .offset(x = 0.dp,
                        y = 82.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top),
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)
                    .offset(x = 0.dp,
                        y = 25.dp)
            ) {
                Text(
                    text = "Doctor Reg.No: ${doctor.registration_id}",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Light))
                Text(
                    text = "Address line: ${doctor.location}",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light))
                Text(
                    text = "Contact no: ${doctor.phone}",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light))
            }
            Text(
                text = "Name: ${patient.first_name}",
                color = Color.Black,
                style = TextStyle(
                    fontSize = 12.sp),
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)
                    .offset(x = 0.dp,
                        y = 114.dp)
                    .wrapContentHeight(align = Alignment.Bottom))

            Text(
                text = "Age: ${getAge(patient)}",
                color = Color.Black,
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Light),
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)
                    .offset(x = 143.dp,
                        y = 116.dp)
                    .wrapContentHeight(align = Alignment.Bottom))
            Text(
                text = "Gender: ${patient.gender}",
                color = Color.Black,
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Light),
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)
                    .offset(x = 184.dp,
                        y = 116.dp)
                    .wrapContentHeight(align = Alignment.Bottom))
            Text(
                text = "Date: ${session.date}",
                color = Color.Black,
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Light),
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)
                    .offset(x = 250.dp,
                        y = 116.dp)
                    .wrapContentHeight(align = Alignment.Bottom))
            Divider(
                color = Color.Black,
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)
                    .offset(x = 0.dp,
                        y = 136.dp)
                    .requiredWidth(width = 328.dp))
            Box(
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)
                    .offset(x = 0.dp,
                        y = 160.dp)
                    .requiredWidth(width = 297.dp)
                    .requiredHeight(height = 375.dp)
            ) {
                Column {
                    // Chief Complaint
                    Text(
                        text = "Chief Complaint",
                        color = Color(0xff030c43),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = patient.chiefComplaint,
                        color = Color.Black,
                        style = TextStyle(
                            fontSize = 12.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Past Medical & Surgical History
                    Text(
                        text = "Past Medical & Surgical History",
                        color = Color(0xff030c43),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = patient.PastMedicalSurgicalHistory,
                        color = Color.Black,
                        style = TextStyle(
                            fontSize = 12.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Vitals
                    Text(
                        text = "Vitals",
                        color = Color(0xff030c43),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    // Add Vitals information here

                    Spacer(modifier = Modifier.height(16.dp))

                    // Laboratory & Radiology
                    Text(
                        text = "Laboratory & Radiology",
                        color = Color(0xff030c43),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    // Add Laboratory & Radiology information here

                    Spacer(modifier = Modifier.height(16.dp))

                    // Next Visit
                    Text(
                        text = "Next Visit",
                        color = Color(0xff030c43),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    // Add Next Visit information here

                    Spacer(modifier = Modifier.height(16.dp))

                    // Impression & Plan
                    Text(
                        text = "Impression & Plan",
                        color = Color(0xff030c43),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    // Add Impression & Plan information here
                }
            }
        }

        Box(modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .offset(x = 0.dp,
                    y = 0.dp)
            .fillMaxWidth()
                .requiredHeight(height = 30.dp)
                .background(color = Color(0xfffca242)))
    }
}
