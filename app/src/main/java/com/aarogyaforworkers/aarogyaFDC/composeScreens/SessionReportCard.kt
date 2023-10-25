package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.aarogyaforworkers.aarogyaFDC.R

@Composable
fun Frame4997(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .requiredWidth(width = 360.dp)
            .requiredHeight(height = 800.dp)
            .background(color = Color.White)
    ) {
        Text(
            text = "Narayana Clinic",
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
                    style = TextStyle(
                        fontSize = 10.sp))
                Box(
                    modifier = Modifier
                        .requiredWidth(width = 8.dp)
                        .requiredHeight(height = 10.dp)
                ) {
                    Image(
                        painter = painterResource(id =

                        R.drawable.app_logo),
                        contentDescription = "image 3",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .align(alignment = Alignment.CenterStart)
                            .offset(x = 0.dp,
                                y = 0.dp)
                            .fillMaxWidth()
                            .requiredHeight(height = 10.dp))
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
                        fontWeight = FontWeight.Medium)) {append("Dr Rakhi Jha, ")}
                    withStyle(style = SpanStyle(
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Light)) {append("MBBS")}})
            Text(
                text = "Patient Reg.No: 123456",
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
                    text = "Doctor Reg.No: 123456",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Light))
                Text(
                    text = "Address line: xxxxx",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light))
                Text(
                    text = "Contact no: 1234567890",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light))
            }
            Text(
                text = "Name: Madhavan M",
                color = Color.Black,
                style = TextStyle(
                    fontSize = 12.sp),
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)
                    .offset(x = 0.dp,
                        y = 114.dp)
                    .wrapContentHeight(align = Alignment.Bottom))
            Text(
                text = "Age: 22",
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
                text = "Gender: Male",
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
                text = "Date: 27/09/2023",
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
                Text(
                    text = "Chief Complaint",
                    color = Color(0xff030c43),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium))
                Text(
                    text = "Past medical & Surgical History",
                    color = Color(0xff030c43),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 0.dp,
                            y = 63.dp))
                Text(
                    text = "Vitals",
                    color = Color(0xff030c43),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 1.dp,
                            y = 126.dp))
                Text(
                    text = "Pain abdomen",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 12.sp),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 32.dp,
                            y = 25.dp))
                Text(
                    text = "Pain abdomen",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 12.sp),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 32.dp,
                            y = 88.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 32.dp,
                            y = 151.dp)
                ) {
                    Text(
                        text = "BP- 120/80",
                        color = Color.Black,
                        style = TextStyle(
                            fontSize = 12.sp))
                    Text(
                        text = "Heart rate: 99",
                        color = Color.Black,
                        style = TextStyle(
                            fontSize = 12.sp))
                    Text(
                        text = "Temperature: 72ᵒc",
                        color = Color.Black,
                        style = TextStyle(
                            fontSize = 12.sp))
                }
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 0.dp,
                            y = 211.dp)
                        .requiredWidth(width = 297.dp)
                        .requiredHeight(height = 164.dp)
                ) {
                    Text(
                        text = "Laboratory & Radiology",
                        color = Color(0xff030c43),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium))
                    Text(
                        text = "Next Visit",
                        color = Color(0xff030c43),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium),
                        modifier = Modifier
                            .align(alignment = Alignment.TopStart)
                            .offset(x = 0.dp,
                                y = 62.dp))
                    Text(
                        text = "CT Scan for abdomen required.",
                        color = Color.Black,
                        style = TextStyle(
                            fontSize = 12.sp),
                        modifier = Modifier
                            .align(alignment = Alignment.TopStart)
                            .offset(x = 32.dp,
                                y = 24.dp))
                    Text(
                        text = "Next week, Monday.",
                        color = Color.Black,
                        style = TextStyle(
                            fontSize = 12.sp),
                        modifier = Modifier
                            .align(alignment = Alignment.TopStart)
                            .offset(x = 32.dp,
                                y = 87.dp))
                    Text(
                        text = "Impression & Plan",
                        color = Color(0xff030c43),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium),
                        modifier = Modifier
                            .align(alignment = Alignment.TopStart)
                            .offset(x = 0.dp,
                                y = 125.dp))
                    Text(
                        text = "Patient is feeling high fever, seeking to come back.",
                        color = Color.Black,
                        style = TextStyle(
                            fontSize = 12.sp),
                        modifier = Modifier
                            .align(alignment = Alignment.TopStart)
                            .offset(x = 32.dp,
                                y = 150.dp))
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 31.dp,
                            y = 173.dp)
                ) {
                    Text(
                        text = "Weight: 60kg",
                        color = Color.Black,
                        style = TextStyle(
                            fontSize = 12.sp))
                    Text(
                        text = "SPO2: 93",
                        color = Color.Black,
                        style = TextStyle(
                            fontSize = 12.sp))
                    Text(
                        text = "ECG: Normal",
                        color = Color.Black,
                        style = TextStyle(
                            fontSize = 12.sp))
                }
            }
        }
        Box(
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .offset(x = 0.dp,
                    y = 0.dp)
                .requiredWidth(width = 360.dp)
                .requiredHeight(height = 30.dp)
                .background(color = Color(0xfffca242)))
    }
}

@Preview()
@Composable
private fun Frame4997Preview() {
    Frame4997(Modifier)
}