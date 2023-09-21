package com.aarogyaforworkers.aarogyaFDC.composeScreens

import Commons.SessionSummaryPageTags
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.aarogyaforworkers.aarogya.R
import com.aarogyaforworkers.aarogyaFDC.Commons.*
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.SubUser.SessionStates
import com.aarogyaforworkers.aarogyaFDC.ui.theme.RobotoBoldFontFamily
import com.aarogyaforworkers.aarogyaFDC.ui.theme.RobotoRegularFontFamily
import com.aarogyaforworkers.aarogyaFDC.ui.theme.logoOrangeColor
import com.aarogyaforworkers.awsapi.models.Session
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Credentials
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.Locale

var isSharingStarted = false

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun SessionSummaryScreen(navHostController: NavHostController){

    val session = selectedSession

    val context = LocalContext.current

    var isSharing by remember { mutableStateOf(false) }

    val captureController = rememberCaptureController()

    val avgSession = MainActivity.subUserRepo.lastAvgSession

    val user = MainActivity.adminDBRepo.getSelectedSubUserProfile()

    var showConfirmOTPAlert by remember { mutableStateOf(false) }

    var showAddPhoneAlert by remember { mutableStateOf(false) }

    if(showAddPhoneAlert) ShowAddPhoneNoAlert(user.phone, showOtpAlert = {
        showAddPhoneAlert = false
        showConfirmOTPAlert = true
        MainActivity.subUserRepo.selectedPhoneNoForVerification.value = it
        val completePhone = "+" + MainActivity.adminDBRepo.userPhoneCountryCode.value + it
        MainActivity.adminDBRepo.sendSubUserVerificationCode(completePhone)
    }) {
        showAddPhoneAlert = false
    }


    when(MainActivity.adminDBRepo.subUserProfileCreateUpdateState.value){

        true -> {
            captureController.capture()
            MainActivity.adminDBRepo.updateSubUserProfileCreateUpdateState(null)
        }

        false -> {
            isSharing = false
            MainActivity.adminDBRepo.updateSubUserProfileCreateUpdateState(null)
        }

        null -> {
            Log.d("TAG", "AddNewUserScreen: procressAlert session saving status null = ${isSaving} ")
        }

    }

    if(showConfirmOTPAlert) ShowConfirmOtpAlert(userphone = MainActivity.subUserRepo.selectedPhoneNoForVerification.value, onConfrimOtp = {
        if(it){
            showConfirmOTPAlert = false
            user.isUserVerified = true
            user.phone = MainActivity.subUserRepo.selectedPhoneNoForVerification.value
            user.country_code = MainActivity.adminDBRepo.userPhoneCountryCode.value
            MainActivity.adminDBRepo.adminUpdateSubUser(user)
            MainActivity.adminDBRepo.setNewSubUserprofile(user.copy())
            MainActivity.adminDBRepo.setNewSubUserprofileCopy(user.copy())
            isSharing = true
//            captureController.capture()
        }
    } ) {
        val handler = Handler(Looper.getMainLooper())
        handler.post(Runnable {
            user.isUserVerified = false
            showConfirmOTPAlert = false
        })
    }
    when{
        MainActivity.s3Repo.sessionSummaryUploaded.value == true -> {
            when(isFromUserHomePage){
                true -> {
                    navHostController.navigate(Destination.UserHome.routes)
                }
                false -> {
                    navHostController.navigate(Destination.SessionHistory.routes)
                }
            }
            Toast.makeText(context, "Session summary shared successfully", Toast.LENGTH_LONG).show()
            MainActivity.s3Repo.updateSessionSummaryUploadStatus(null, "")
            isSharing = false
        }
        MainActivity.s3Repo.sessionSummaryUploaded.value == false -> {
            Toast.makeText(context, "Failed to share try again", Toast.LENGTH_LONG).show()
            MainActivity.s3Repo.updateSessionSummaryUploadStatus(null, "")
            isSharing = false
        }
    }

//    Box(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier
            .fillMaxSize().background(Color.White)
            .testTag(SessionSummaryPageTags.shared.summaryScreen)
            ) {

            TopBarWithCancelBtn {
                when(isFromUserHomePage){
                    true -> {
                        navHostController.navigate(Destination.UserHome.routes)
                    }
                    false -> {
                        navHostController.navigate(Destination.SessionHistory.routes)
                    }
                }
            }


            LazyColumn(Modifier.weight(1f)){
                item {
                    Capturable(
                        controller = captureController,
                        onCaptured = { bitmap, error ->
                            // This is captured bitmap of a content inside Capturable Composable.
                            if (bitmap != null) {
//                                //to save image local
//                                val savedUri = saveBitmapToStorage(context, bitmap.asAndroidBitmap(), "capturedImage.jpg")
//                                if (savedUri != null) {
//                                    Toast.makeText(context, "Image saved successfully!", Toast.LENGTH_SHORT).show()
//                                } else {
//                                    Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
//                                }
                                // Bitmap is captured successfully. Do something with it!
                                val image = bitmapToByteArray(bitmap.asAndroidBitmap())
                                isSharingStarted = true
                                isSharing = true
//                      202754:3519:d9fc1b:919340413756
                                var reqId = ""
                                Log.d("TAG", "SessionSummaryScreen: sessionId ${session.sessionId}")

                                val ses = session.sessionId.split(":").toMutableList()
//                        when(ses.size){
//                            4 -> {
//                                reqId  = ses[0]+":"+ses[1]+":"+ses[2]+":"+ses[3]
//                                MainActivity.s3Repo.startUploadingSessionSummary(image, reqId)
//                            }
//
//                            5 -> {
//                                reqId  = ses[0]+":"+ses[1]+":"+ses[2]+":"+ses[3]+":"+ses[4]
//                                Toast.makeText(context, "session can not be shared", Toast.LENGTH_LONG).show()
//                            }
//                            else -> {
//                                reqId = session.sessionId+":"+MainActivity.adminDBRepo.getSelectedSubUserProfile().phone
//                                MainActivity.s3Repo.startUploadingSessionSummary(image, reqId)
//                            }
//                        }

                                reqId = session.sessionId+":"+MainActivity.adminDBRepo.getSelectedSubUserProfile().phone
                                Log.d("TAG", "SessionSummaryScreen: selected phoen $reqId")
                                MainActivity.s3Repo.startUploadingSessionSummary(image, reqId)

//                        if(ses.size == 4 || ses.size == 5){
//                            ses[3] = MainActivity.adminDBRepo.getSelectedSubUserProfile().phone
//                            reqId  = ses[0]+":"+ses[1]+":"+ses[2]+":"+ses[3]
//                        }else{
//                            reqId = session.sessionId+":"+MainActivity.adminDBRepo.getSelectedSubUserProfile().phone
//                        }

                            }

                            if (error != null) {
                                // Error occurred. Handle it!
                                Toast.makeText(context, "Failed to share try again", Toast.LENGTH_LONG).show()
                                isSharing = false
                            }
                        }
                    ) {
                        // Composable content to be captured.
                        // Here, `MovieTicketContent()` will be get captured
                                SessionCard(session = session, avgSession = avgSession)


                    }
                }
            }



            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 25.dp),
//                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PopBtnDouble(
                    btnName1 = "Cancel",
                    btnName2 = "Share",
                    onBtnClick1 = {
                        //on Cancel click
                        navHostController.navigate(Destination.UserHome.routes)

//                        when(isFromUserHomePage){
//                            true -> {
//                                navHostController.navigate(Destination.UserHome.routes)
//                            }
//                            false -> {
//                                navHostController.navigate(Destination.SessionHistory.routes)
//                            }
//                        }
                    },
                    onBtnClick2 = {
                        //on share click
                        if(!user.isUserVerified){
                            showAddPhoneAlert = true
                        }else{
                            isSharing = true
                            isSessionShared = false
                            captureController.capture()
                        }
                    })
//                Spacer(modifier = Modifier.width(5.dp))
//                ActionButton(action = {
//                    when(isFromUserHomePage){
//                        true -> {
//                            navHostController.navigate(Destination.Home.routes)
//                        }
//                        false -> {
//                            navHostController.navigate(Destination.SessionHistory.routes)
//                        }
//                    }
//                     }, buttonName = "Cancel")
//                ActionButton(action = {
//                    if(!user.isUserVerified){
//                        showAddPhoneAlert = true
//                    }else{
//                        isSharing = true
//                        isSessionShared = false
//                        captureController.capture()
//                    } }, buttonName = "Share")
//                Spacer(modifier = Modifier.width(5.dp))
            }
        }
        if(isSharing) showProgress()
//    }
}

@Composable
fun sendMessage(sendingMessage : Boolean, url : String, onSuccess : () -> Unit, onFailed : () -> Unit){
    // send the message in a separate coroutine to avoid blocking the UI
    LaunchedEffect(sendingMessage) {
        if (sendingMessage) {
            try {
                val accountSid = "ACff27e924d692ca08ac83991f75fc9445"
                val authToken = "11568edad8a8ce37dde0b90de48f0608"
                val recipientNumber = "+919340413756"
                val senderNumber = "+13203773220"
                val message = ""
                val credentials = Credentials.basic(accountSid, authToken)
                // replace with the URL of the image file you want to send
                val client = OkHttpClient()
                val requestBody = FormBody.Builder()
                    .add("To", "whatsapp:$recipientNumber")
                    .add("From", "whatsapp:$senderNumber")
                    .add("Body", message)
                    .add("MediaUrl", url)
                    .build()

                val request = Request.Builder()
                    .url("https://api.twilio.com/2010-04-01/Accounts/$accountSid/Messages.json")
                    .addHeader("Authorization", credentials)
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        // Handle failure to make the request
                        onFailed()
                    }
                    override fun onResponse(call: Call, response: Response) {
                        // Handle the response from the API
                        Log.d("TAG", "onResponse: ")
                        if(response.isSuccessful){
                            onSuccess()
                        }else{
                            onFailed()
                        }
                    }
                })
            } catch (e: Exception) {
                // handle any errors
                e.printStackTrace()
                onFailed()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@ExperimentalTvMaterial3Api
@Composable
fun SessionCard(session: Session, avgSession: Session){
    val tempInC = session.temp.substringBefore("°C").toDoubleOrNull()
    val sysValue = session.sys?.replace(Regex("[^\\d.]"), "")?.toIntOrNull()
    val diaValue = session.dia?.replace(Regex("[^\\d.]"), "")?.toIntOrNull()
    val hrValue = session.heartRate?.replace(Regex("[^\\d.]"), "")?.toIntOrNull()
    val spo2Value = session.spO2?.replace(Regex("[^\\d.]"), "")?.toIntOrNull()
    val bmiValue = session.weight?.replace(Regex("[^\\d.]"), "")?.toDoubleOrNull()
    val bodyFatValue = session.bodyFat?.replace(Regex("[^\\d.]"), "")?.toDoubleOrNull()
    val tempValue = session.temp?.replace(Regex("[^\\d.]"), "")?.toDoubleOrNull()
    var monthArray : List<String> = listOf<String>("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    val sys = sysValue?.toString() ?: ""
    val dia = diaValue?.toString() ?: ""
    val hr = hrValue?.toString() ?: ""
    val spo2 = spo2Value?.toString() ?: ""
    val bmi = bmiValue?.toString() ?: ""
    val bodyFat = bodyFatValue?.toString() ?: ""
    val temp = tempValue?.toString() ?: ""

    val selectedUser = MainActivity.adminDBRepo.getSelectedSubUserProfile()

    Box(modifier = Modifier
        .background(Color.White),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp, top = 20.dp, bottom = 10.dp)
                .fillMaxSize()
                .background(Color.White),
        ){
            Row(
                Modifier
                    .height(40.dp)
                    .fillMaxWidth(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Box(Modifier.size(40.dp)) {
                    ReportAppLogo()
                }

                BoldTextView(title = "Aarogya Health Card", fontSize = 20)

            }
//            Spacer(modifier = Modifier.height(20.dp))
//            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
//            }

            Spacer(modifier = Modifier.height(30.dp))

            Row{
                BoldTextView(title = "Reg No: ")
                val id = selectedUser.user_id.replace("-", "")
                val count = id.takeLast(4)
                val newId = id.replace(count, "-$count")
                RegularTextView(title = newId)
            }
            Spacer(modifier = Modifier.height(6.dp))

            Row() {
                Column(Modifier.weight(1f)) {
                    Row() {
                        BoldTextView(title = "Date: ")
                        if(session.date.isNotEmpty()){
                            val date = convertCustomDateFormat(session.date)
                            val time = convertTimeToAMPMFormat(session.time)
                            RegularTextView(title = "$date; $time")
                        }else{
                            RegularTextView(title = "")
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row() {
                        BoldTextView(title = "Name: ")
                        RegularTextView(title = formatTitle(selectedUser.first_name, selectedUser.last_name))
                    }
                }

                Column() {
                    Row() {
                        BoldTextView(title = "Place: ")
                        val location = session.location.split(",")
                        if(location.isNotEmpty()){
                            RegularTextView(title = location[2])
                        }else{
                            RegularTextView(title = "")
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row() {
                        BoldTextView(title = "Age: ")
                        RegularTextView(title = getAge(selectedUser))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Divider(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),color = Color.Black)

            Row(modifier = Modifier
                .background(logoOrangeColor)
                .fillMaxWidth()
                .height(25.dp),Arrangement.Center, Alignment.CenterVertically) {
                BoldTextView(title = "Chief Complaint", textColor = Color.White, fontSize = 14)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row() {
                RegularTextView(title = selectedUser.chiefComplaint.ifEmpty { "NA" }, fontSize = 14, lineHeight = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),color = Color.Black)


            val bpAvg = if(avgSession.sys.isEmpty() || avgSession.dia.isEmpty())  "" else "${avgSession.sys}/${avgSession.dia}"

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                HeaderRow(title1 = "Vital", title2 = "Measured", title3 = "Trend*", title4 = "Reference")
                DataRow(title = "BP",
                    unit = "mmHg",
                    value = "$sys/$dia",
                    avg = bpAvg,
                    range = "120/80",
                    rowColor = Color.White)

                DataRow(title = "HR",
                    unit = "bpm",
                    value = hr,
                    avg = avgSession.heartRate,
                    range = "60.0 - 100.0",
                    validRange = 60.0..100.0,
                    rowColor = Color(0xfffae9db))

                DataRow(title = "SpO2",
                    unit = "%",
                    value = spo2,
                    avg = avgSession.spO2,
                    range = "95.0 - 100.0",
                    validRange = 95.0..100.0,
                    rowColor = Color.White)
                var avgTempss = ""
                if(avgSession.temp.isNotEmpty()){
                    avgTempss = MainActivity.adminDBRepo.getTempBasedOnUnit(avgSession.temp.toDouble())
                }
                DataRow(title = "Temp",
                    unit = MainActivity.adminDBRepo.getTempUnit(),
                    value = MainActivity.adminDBRepo.getTempBasedOnUnit(tempInC),
                    avg = avgTempss,
                    range = if(MainActivity.adminDBRepo.tempUnit.value == 0) "97.0 - 99.0" else "36.1 - 37.2", //implement as unit changes
                    validRange = if(MainActivity.adminDBRepo.tempUnit.value == 0) 97.0..99.0 else 36.1..37.2,
                    rowColor = Color(0xfffae9db))

                DataRow(title = "Weight",
                    unit = MainActivity.adminDBRepo.getWeightUnit(),
                    value = if(session.weight.isNotEmpty()) MainActivity.adminDBRepo.getWeightBasedOnUnitSet(session.weight.toDouble()) else "",
                    avg = avgSession.weight,
                    range = calculateMinRangeBYBmiHeight(avgSession.weight, selectedUser.height) +" - "+ calculateMaxRangeBYBmiHeight(avgSession.weight, selectedUser.height),
                    validRange = calculateMinRangeBYBmiHeight(avgSession.weight, selectedUser.height).toDouble()..calculateMaxRangeBYBmiHeight(avgSession.weight, selectedUser.height).toDouble(),
                    rowColor = Color.White)

//                DataRow(title = "Body Fat",
//                    unit = "%",
//                    value = bodyFat,
//                    avg = avgSession.bodyFat,
//                    range = getRange(selectedUser.gender),
//                    validRange = getValidRange(selectedUser.gender),
//                    rowColor = Color(0xfffae9db) )
//                DataRow(title = "BMI",
//                    unit = "",
//                    value = bmi,
//                    avg = avgSession.weight,
//                    range = "18.5 - 24.9",
//                    validRange = 18.5..24.9,
//                    rowColor = Color.White)
//                DataRow(title = "GLU",
//                    unit = "mmol/L ",
//                    value = bmi,
//                    avg = "",
//                    range = "3.9 - 5.5",
//                    validRange = 3.9..5.5,
//                    rowColor = Color.White)
            }


            Divider(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),color = Color.Black)


            var impressionPlan = session.ImpressionPlan.split("-:-")
            var impressionText = impressionPlan[0]

            Row(modifier = Modifier
                .background(logoOrangeColor)
                .fillMaxWidth()
                .height(25.dp),Arrangement.Center, Alignment.CenterVertically) {
                BoldTextView(title = "Impression & Plan", textColor = Color.White, fontSize = 14)
            }

            Spacer(modifier = Modifier.height(16.dp))


            Row() {
                RegularTextView(title = impressionText.ifEmpty { "NA" }, fontSize = 14, lineHeight = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))


            Divider(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),color = Color.Black)

//            Spacer(modifier = Modifier.height(20.dp))
//
//            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
//                ItalicTextView(title = "Eat Right, Sleep Well & Exercise - 3 Mantras To Be Happy!", fontSize = 12)
//            }
            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                RegularTextView(title = "hello@aarogyatech.com", fontSize = 12, textColor = Color.Blue, textDecoration = TextDecoration.Underline)//use underline
                RegularTextView(title = "https://www.aarogyatech.com", fontSize = 12, textColor = Color.Blue, TextDecoration.Underline)//use underline
            }
        }
    }
}



fun calculateMinRangeBYBmiHeight(bmi: String, height: String): String {
    return try {
        val bmiDouble = 18.5
        val heightDouble = height.toDouble() / 100.0 // convert height from cm to m
        val weight = bmiDouble * heightDouble * heightDouble
        val basedOnUnit = MainActivity.adminDBRepo.getWeightBasedOnUnits(weight).toFloat()
        "%.1f".format(basedOnUnit)
    } catch (e: NumberFormatException) {
        println("Invalid input: $bmi or $height cannot be converted to Double")
        ""
    }
}

fun calculateMaxRangeBYBmiHeight(bmi: String, height: String): String {
    return try {
        val bmiDouble = 24.9
        val heightDouble = height.toDouble() / 100.0 // convert height from cm to m
        val weight = bmiDouble * heightDouble * heightDouble
        val basedOnUnit = MainActivity.adminDBRepo.getWeightBasedOnUnits(weight).toFloat()
        "%.1f".format(basedOnUnit)
    } catch (e: NumberFormatException) {
        println("Invalid input: $bmi or $height cannot be converted to Double")
        ""
    }
}

fun calculateWeightByBmiHeight(bmi: String, height: String): String {
    return try {
        val bmiDouble = bmi.toDouble()
        val heightDouble = height.toDouble() / 100.0 // convert height from cm to m
        val weight = bmiDouble * heightDouble * heightDouble
        val basedOnUnit = MainActivity.adminDBRepo.getWeightBasedOnUnits(weight).toFloat()
        "%.1f".format(basedOnUnit)
    } catch (e: NumberFormatException) {
        println("Invalid input: $bmi or $height cannot be converted to Double")
        ""
    }
}

@Composable
fun ActionButton(action:() -> Unit, buttonName:String){
    Button(onClick = { action() }, colors = ButtonDefaults.buttonColors(Color(0xFF030C44))) {
        Text(text = buttonName)
    }
}


fun saveBitmapToStorage(context: Context, bitmap: Bitmap, imageName: String): Uri? {
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, imageName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    if (uri != null) {
        context.contentResolver.openOutputStream(uri)?.use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
    }

    return uri
}


@RequiresApi(Build.VERSION_CODES.Q)
@Preview
@Composable
fun viewSessionCard(){
    SessionSummaryScreen(navHostController = rememberNavController())
}