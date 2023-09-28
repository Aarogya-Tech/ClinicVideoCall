package com.aarogyaforworkers.aarogyaFDC.composeScreens

import Commons.UserHomePageTags
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.ArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aarogyaforworkers.aarogyaFDC.AdminDB.AdminDBRepository
import com.aarogyaforworkers.aarogyaFDC.Commons.*
import com.aarogyaforworkers.aarogyaFDC.CsvGenerator.CsvRepository
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.Location.LocationRepository
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.Omron.OmronRepository
import com.aarogyaforworkers.aarogyaFDC.PC300.PC300Repository
import com.aarogyaforworkers.aarogya.R
import com.aarogyaforworkers.aarogya.composeScreens.isFromVital
import com.aarogyaforworkers.aarogyaFDC.S3.S3Repository
import com.aarogyaforworkers.aarogyaFDC.SubUser.*
import com.aarogyaforworkers.aarogyaFDC.Tracky.TrackyManager
import com.aarogyaforworkers.aarogyaFDC.checkBluetooth
import com.aarogyaforworkers.aarogyaFDC.composeScreens.ECGPainter.draw.BackGround
import com.aarogyaforworkers.aarogyaFDC.composeScreens.ECGPainter.recvdata.StaticReceive
import com.aarogyaforworkers.aarogyaFDC.isBluetoothEnabled
import com.aarogyaforworkers.aarogyaFDC.ui.theme.logoOrangeColor
import com.aarogyaforworkers.awsapi.models.SubUserProfile
import kotlinx.coroutines.delay
import java.util.Calendar

var isShown = false
val cardWidth = 150.dp
val cardHeight = 150.dp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserHomeScreen(navHostController: NavHostController, repository : AdminDBRepository, pc300Repository: PC300Repository, locationRepository: LocationRepository, subUserDBRepository: SubUserDBRepository, s3Repository: S3Repository, csvRepository: CsvRepository) {

    isItFromHistoryPage = false

    val context = LocalContext.current

    SideEffect {
        (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    Disableback()

    CheckInternet(context = LocalContext.current)

    MainActivity.sessionRepo.clearImageList()

    MainActivity.playerRepo.setPlayers(context)

    val bleEnabled by remember { mutableStateOf(isBluetoothEnabled()) }

    if(!bleEnabled) checkBluetooth(context)

    pc300Repository.isOnSessionPage = true

    MainActivity.adminDBRepo.updateLastSavedUnits(context)

    isOnUserHomeScreen = true

    MainActivity.subUserRepo.startFetchingFromOmronDevice()

    val isSaving = remember {
        mutableStateOf(false)
    }

    when(MainActivity.sessionRepo.sessionUpdatedStatus.value){

        true -> {
            MainActivity.subUserRepo.getSessionsByUserID(userId = repository.getSelectedSubUserProfile().user_id)
            //MainActivity.subUserRepo.updateProgressState(false)
            MainActivity.sessionRepo.updateIsSessionUpdatedStatus(null)
        }

        false -> {
            MainActivity.sessionRepo.updateSessionFetch(false)
            MainActivity.sessionRepo.updateIsSessionUpdatedStatus(null)
        }

        null -> {

        }
    }



    when(MainActivity.sessionRepo.fetchingSessionState.value){

        true -> {
            MainActivity.sessionRepo.updateSessionFetch(false)
            MainActivity.sessionRepo.updateSessionFetchStatus(null)
        }

        false -> {
            MainActivity.sessionRepo.updateSessionFetch(false)
            MainActivity.sessionRepo.updateSessionFetchStatus(null)
        }

        null -> {

        }
    }

    when(MainActivity.sessionRepo.sessionCreatedStatus.value){

        true -> {
            MainActivity.subUserRepo.getSessionsByUserID(userId = repository.getSelectedSubUserProfile().user_id)
            MainActivity.subUserRepo.updateProgressState(false)
            MainActivity.sessionRepo.updateIsSessionCreatedStatus(null)
        }

        false -> {
            MainActivity.subUserRepo.updateProgressState(false)
            MainActivity.sessionRepo.updateIsSessionCreatedStatus(null)
        }

        null -> {

        }
    }


    when(MainActivity.sessionRepo.sessionDeletedStatus.value){

        true -> {
            MainActivity.subUserRepo.clearSessionList()
            MainActivity.sessionRepo.updateSessionFetch(true)
            MainActivity.subUserRepo.getSessionsByUserID(userId = repository.getSelectedSubUserProfile().user_id)
            MainActivity.sessionRepo.updateSessionDeletedStatus(null)
        }

        false -> {
            Toast.makeText(context, "Couldn't delete session try again", Toast.LENGTH_SHORT).show()
            MainActivity.sessionRepo.updateSessionDeletedStatus(null)
        }

        null -> {

        }
    }

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .testTag(UserHomePageTags.shared.userHomeScreen)
    ) {

        Column() {
            UserHome(repository.getSelectedSubUserProfile(), MainActivity.subUserRepo.isResetQuestion.value, navHostController, repository, pc300Repository, locationRepository, subUserDBRepository, {MainActivity.subUserRepo.isResetQuestion.value = false})
        }

    }

    if(MainActivity.subUserRepo.showProgress.value || MainActivity.sessionRepo.fetching.value) showProgress()

}

@Composable
fun CardWithHeadingAndContent(navHostController: NavHostController,title:String, user: SubUserProfile, type : String) {

    val textToShow = when (type) {
        "0" -> user.chiefComplaint
        "1" -> user.HPI_presentIllness
        "2" -> user.FamilyHistory
        "3" -> user.SocialHistory
        "4" -> user.PastMedicalSurgicalHistory
        "5" -> user.Medication
        else -> ""
    }

    Column(
        horizontalAlignment=Alignment.Start
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable {
                isDoneClick = false
                navHostController.navigate(
                    route = Destination.EditTextScreen.routes + "/$title/$textToShow:$type"
                )
            }
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            RegularTextView(title = title, fontSize = 18)
            //Icon(imageVector = Icons.Outlined.ArrowForwardIos, contentDescription = "RightHeadArrow")
        }
        Surface(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    isDoneClick = false
                    navHostController.navigate(
                        route = Destination.EditTextScreen.routes + "/$title/$textToShow:$type"
                    )
                },
            shape = RoundedCornerShape(16.dp),
            color = Color(0xffdae3f3),
            shadowElevation = 4.dp
        ) {
            Row(modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {

                Text(
                    text = textToShow.ifEmpty { "NA" },
                    fontFamily = FontFamily(Font(R.font.roboto_regular)),
                    fontSize = 16.sp,
                    color = if(textToShow.isEmpty()) Color.Gray else Color.Black,
                    maxLines = 2, // Set the maximum number of lines
                    overflow = TextOverflow.Ellipsis,
                    modifier= Modifier
                        .height(48.dp)
                        .weight(1f)
                )

                Box(
                    Modifier
                        .fillMaxHeight()
                        .width(40.dp), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Outlined.ArrowForwardIos, contentDescription = "RightHeadArrow")
                }
            }
        }
    }
}

var isScrollStateSetUp = false
var isSessionPlayedOnUserHome = false

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserHome(user : SubUserProfile, isResetQuestion : Boolean, navHostController: NavHostController, adminDBRepository: AdminDBRepository, pc300Repository: PC300Repository, locationRepository: LocationRepository, subUserDBRepository: SubUserDBRepository, onResetChange : () -> Unit){

    subUserDBRepository.selectedUserId = user.user_id

    userHometimeStamp = System.currentTimeMillis().toString()

    val context = LocalContext.current

    if(user.medical_history.contains(",")){
        subUserDBRepository.parseUserMedicalHistory(user)
    }

    var isShowAlert by remember {
        mutableStateOf(false)
    }

    var isShowEcgAlert by remember {
        mutableStateOf(false)
    }

    var isSaveSessionAlert by remember {
        mutableStateOf(false)
    }

    if(isShowEcgAlert) EcgAlert(title = "ECG Result", subTitle = MainActivity.pc300Repo.getEcgResultMsgBasedOnCode(context)) {
       isShowEcgAlert = false
    }

    if(MainActivity.pc300Repo.showEcgRealtimeAlert.value) RealtimeEcgAlertView()

    if(MainActivity.subUserRepo.bufferThere.value){
        if(!isSessionPlayedOnUserHome){
            isSessionPlayedOnUserHome = true
            MainActivity.sessionRepo.selectedsession = MainActivity.subUserRepo.getSession()
            navHostController.navigate(Destination.VitalCollectionScreen.routes)
        }
    }

    TopBarWithBackEditBtn(
        user,
        onProfileClicked = {
            isEditUser = true
            isSetUpDone = false
            userProfileToEdit = MainActivity.adminDBRepo.getSelectedUserProfileToEdit()
            isSubUserProfileSetUp = false
            navHostController.navigate(Destination.AddNewUser.routes)
        },
        onBackBtnPressed = {
            if(isFromPatientList){
                isFromPatientList = false
                navHostController.navigate(Destination.PatientList.routes)

            } else{
                navHostController.navigate(Destination.Home.routes)
            }
//            if(MainActivity.subUserRepo.bufferThere.value){
//                isShowAlert = true
//                ifIsExitAndSave = true
//            }else{
//                if(MainActivity.subUserRepo.lastSavedSession != null){
//                    selectedSession =  MainActivity.subUserRepo.lastSavedSession!!
//                    isFromUserHomePage = true
//                    isItFromHistoryPage = false
//                    MainActivity.subUserRepo.calculateAvgSession(MainActivity.subUserRepo.sessions.value, MainActivity.subUserRepo.sessions.value.size)
//                    navHostController.navigate(Destination.SessionSummary.routes)
//                }else{
//                    navHostController.navigate(Destination.Home.routes)
//                }
            //}
            isOnUserHomeScreen = false },
        onStartBtnPressed = {
            MainActivity.pc300Repo.clearSessionValues()

            if((MainActivity.pc300Repo.connectedPC300Device.value != null) || (MainActivity.omronRepo.connectedOmronDevice.value != null)){
                MainActivity.subUserRepo.createNewSession()
                isSessionPlayedOnUserHome = true
                navHostController.navigate(Destination.VitalCollectionScreen.routes)
            }else{
               Toast.makeText(context, "Please connect device first", Toast.LENGTH_SHORT).show()
            }
        },
        onEditBtnClicked = {
            isEditUser = true
            isSetUpDone = false
            userProfileToEdit = MainActivity.adminDBRepo.getSelectedUserProfileToEdit()
            isSubUserProfileSetUp = false
            navHostController.navigate(Destination.AddNewUser.routes)
        },
        onConnectionBtnClicked = {isFromUserHome = true
            navHostController.navigate(Destination.DeviceConnection.routes)}
    ){
        if(MainActivity.pc300Repo.isSessionStarted.value || MainActivity.omronRepo.isSessionStarted.value){
            isShowAlert = true
        }else{
            MainActivity.pc300Repo.isOnSessionPage = false
            isSubUserProfileSetUp = false
            navHostController.navigate(Destination.Home.routes)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
    ) {

        var isLongPress by remember { mutableStateOf(false) }

        var isSelectedSessionId by remember { mutableStateOf("") }

        AlertView(showAlert = isShowAlert,
            title = "Unsaved Data",
            subTitle = "Do you want to save user session data?",
            subTitle1 = "",
            onYesClick = {
                ifIsExitAndSave = true
                MainActivity.subUserRepo.updateProgressState(true)
                MainActivity.subUserRepo.saveOrUpdateSession()
                isShowAlert = false
            },
            onNoClick = {
                isShowAlert = false
                navHostController.navigate(Destination.Home.routes)
            }
        ) {
            isShowAlert = false
        }

        AlertView(showAlert = isLongPress,
            title = "Delete Session",
            subTitle = "Do you want to delete session? Your data will be lost.",
            subTitle1 = "",
            onYesClick = {
                MainActivity.sessionRepo.updateSessionFetch(true)
                MainActivity.adminDBRepo.deletePatientSession(isSelectedSessionId)
                isLongPress = false
            },
            onNoClick = {
                isLongPress = false
            }
        ) {
            isLongPress = false
        }

        val scrollState = MainActivity.sessionRepo.listState.value

        if(scrollState == null){
            MainActivity.sessionRepo.listState.value = rememberLazyListState()
        }

        val currentOffset = MainActivity.sessionRepo.listState.value

        if (MainActivity.sessionRepo.scrollToIndex.value != -1) {

            LaunchedEffect(MainActivity.sessionRepo.scrollToIndex.value) {
//              val offset = (((MainActivity.sessionRepo.scrollToIndex.value + 0.5f) * 100) - (MainActivity.sessionRepo.knownOffset))
              MainActivity.sessionRepo.listState.value!!.scrollToItem(MainActivity.sessionRepo.scrollToIndex.value)
                MainActivity.sessionRepo.scrollToIndex.value = -1 // Reset the index after scrolling
//              MainActivity.sessionRepo.knownOffset = MainActivity.sessionRepo.knownOffset + 500
            }
        }

        val sessionsList = MainActivity.subUserRepo.sessions.value.reversed().filter { it.sessionId.isNotEmpty() }

        val sessionsList1 = MainActivity.subUserRepo.sessions1.value.reversed().filter { it.sessionId.isNotEmpty() }

        if(scrollState != null){
            LazyColumn(
                modifier = Modifier
                    .background(Color(0xffFF9449))
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                state = scrollState
            ) {

                item {

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(colors = CardDefaults.cardColors(Color.White)) {
                        Column(Modifier.padding(10.dp)) {
                            CardWithHeadingAndContent(navHostController,title = "Chief Complaint", user ,"0")

                            Spacer(modifier = Modifier.height(6.dp))

                            CardWithHeadingAndContent(navHostController,"History of Present Illness (HPI)", user, "1")

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(2.dp), // Adjust spacing as needed
                            ) {
                                Box(
                                    modifier = Modifier.weight(1f)
                                )
                                {
                                    CardWithHeadingAndContentForHistory1(navHostController, "Family History", user , "2")
                                }
                                Box(
                                    modifier = Modifier.weight(1f)
                                )
                                {
                                    CardWithHeadingAndContentForHistory1(navHostController, "Social History", user, "3")
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            val parsedTextPMSH = user.PastMedicalSurgicalHistory.split("-:-")

                            val parsedPMSHList = MainActivity.sessionRepo.parseImageList(parsedTextPMSH.last())

                            CardWithHeadingContentAndAttachment(
                                navHostController = navHostController,
                                title = "Past Medical & Surgical History",
                                value = if(parsedTextPMSH.isNotEmpty()) parsedTextPMSH.first() else "",
                                onClick = {
                                    isPMSHDoneClick = false

                                    MainActivity.subUserRepo.updateTempPopUpText(parsedTextPMSH.first() ?: "")

                                    MainActivity.sessionRepo.clearImageList()
                                    isPMSHSetUpDone = false
                                    navHostController.navigate(Destination.PastMedicalSurgicalHistoryScreen.routes)
                                },
                                isAttachment = parsedPMSHList.isNotEmpty()
                            )

//                            CardWithHeadingAndContent(navHostController,"Past Medical & Surgical History", user, "4")

                            Spacer(modifier = Modifier.height(6.dp))

                            CardWithHeadingAndContent(navHostController,"Medication", user, "5")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(colors = CardDefaults.cardColors(Color.White), shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 0.dp, bottomEnd = 0.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(10.dp)) {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            )
                            {
                                RegularTextView(title = "Visits Summary",fontSize=18)

                                Box(
                                    Modifier
                                        .size(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ActionBtnUser(size = 30.dp, icon = Icons.Default.Add, onIconClick =  {
                                        // on add btn clicked
                                        MainActivity.sessionRepo.updateSessionFetch(true)
                                        MainActivity.sessionRepo.createNewEmptySessionForUser(user.user_id)
                                    }, bgColor = Color(0xFFFFD4B6))
                                }

                            }
                        }
                    }

                }

                itemsIndexed(sessionsList1){index, selectedSession ->


                    val item = sessionsList.find { item -> item.sessionId == selectedSession.sessionId }


                    if(item != null){

                        val expandState= remember { mutableStateOf(selectedSession.isExpanded) }

                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.background(Color.White)){
                            VisitSummaryCard(
                                navHostController = navHostController,
                                session = item,
                                onExpandClick = {
                                    //selectedSession.isExpanded = !selectedSession.isExpanded
                                    expandState.value = !expandState.value
                                    sessionsList1.forEach { session ->
                                        if (session.sessionId != selectedSession.sessionId) {
                                            session.isExpanded = false
                                        }
                                    }
//                                    expandState.value = selectedSession.isExpanded
                                    MainActivity.sessionRepo.scrollToIndex.value = index + 1
                                },
                                expandState = expandState,
                                onLongPressed = {
                                    isSelectedSessionId = it
                                    isLongPress = true
                                }
                            )
                            }
//                            VisitSummaryCard(navHostController = navHostController,item, it, {index ->
//                                // on expand clicked ->
//                                MainActivity.sessionRepo.scrollToIndex.value = index + 1
////                            }, sessionsList1.indexOf(it)){
////                                isSelectedSessionId = it
////                                isLongPress = true
////                            }
//                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardWithHeadingAndContentForHistory1(navHostController: NavHostController,title:String, user : SubUserProfile, type : String) {

    Column(
        horizontalAlignment=Alignment.Start
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable {
                val textToShow = if (type == "2") {
                    // Family
                    MainActivity.subUserRepo.updateOptionList(user.FamilyHistory)
                    MainActivity.subUserRepo.updateOptionList1(user.FamilyHistory)
                    user.FamilyHistory
                } else {
                    // Social
                    MainActivity.subUserRepo.updateOptionList(user.SocialHistory)
                    MainActivity.subUserRepo.updateOptionList1(user.SocialHistory)
                    user.SocialHistory
                }
                navHostController.navigate(route = Destination.RadioButtonHistoryScreen.routes + "/$title/$textToShow")
            },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            RegularTextView(title = title, fontSize = 18)
            Icon(imageVector = Icons.Outlined.ArrowForwardIos, contentDescription = "RightHeadArrow")
        }
        Surface(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    val textToShow = if (type == "2") {
                        // Family
                        MainActivity.subUserRepo.updateOptionList(user.FamilyHistory)
                        MainActivity.subUserRepo.updateOptionList1(user.FamilyHistory)
                        user.FamilyHistory
                    } else {
                        // Social
                        MainActivity.subUserRepo.updateOptionList(user.SocialHistory)
                        MainActivity.subUserRepo.updateOptionList1(user.SocialHistory)
                        user.SocialHistory
                    }
                    navHostController.navigate(route = Destination.RadioButtonHistoryScreen.routes + "/$title/$textToShow")
                },
            shape = RoundedCornerShape(16.dp),
            color = Color(0xffdae3f3),
//            color = Color(0xFFBFEFFF),
            shadowElevation = 4.dp
        ) {



            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {

                if(type == "2"){
                    // Family
                    val parsedList = parseOptions(user.FamilyHistory).filter { it.isSelected == "1" }

                    var listOfNames = ""

                    parsedList.forEach {
                        if(it.name=="Others")
                            listOfNames=listOfNames+it.value + "; "
                        else
                            listOfNames = listOfNames + it.name + "; "
                    }
                    listOfNames = listOfNames.removeSuffix("; ")

                    Text(
                        text = listOfNames.ifEmpty { "NA" },
                        fontFamily = FontFamily(Font(R.font.roboto_regular)),
                        fontSize = 16.sp,
                        color = if(listOfNames.isEmpty()) Color.Gray else Color.Black,
                        maxLines = 2, // Set the maximum number of lines
                        overflow = TextOverflow.Ellipsis,
                        modifier=Modifier.height(48.dp)
                    )

                }else{
                    // Social
                    val parsedList = parseOptions(user.SocialHistory).filter { it.isSelected == "1" }

                    var listOfNames = ""

                    parsedList.forEach {
                        if(it.name=="Others")
                            listOfNames=listOfNames+it.value + "; "
                        else
                            listOfNames = listOfNames + it.name + "; "
                    }
                    listOfNames = listOfNames.removeSuffix("; ")

                    Text(
                        text = listOfNames.ifEmpty { "NA" },
                        fontFamily = FontFamily(Font(R.font.roboto_regular)),
                        fontSize = 16.sp,
                        color = if(listOfNames.isEmpty()) Color.Gray else Color.Black,
                        maxLines = 2, // Set the maximum number of lines
                        overflow = TextOverflow.Ellipsis ,
                        modifier=Modifier.height(48.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun A41(modifier: Modifier = Modifier) {

    Box(

        modifier = modifier

            .fillMaxWidth()

            .requiredHeight(height = 842.dp)

            .background(color = Color.White)

    ) {

        Text(

            text = "Narayana Clininc",

            color = Color.Black,

            style = TextStyle(

                fontSize = 24.sp,

                fontWeight = FontWeight.Medium),

            modifier = Modifier

                .align(alignment = Alignment.TopCenter)

                .offset(x = 0.dp,

                    y = 28.dp))

        Text(

            text = buildAnnotatedString {

                withStyle(style = SpanStyle(

                    color = Color.Black,

                    fontSize = 20.sp,

                    fontWeight = FontWeight.Medium)
                ) {append("Dr Rakhi Jha, ")}

                withStyle(style = SpanStyle(

                    color = Color.Black,

                    fontSize = 20.sp,

                    fontWeight = FontWeight.Light)) {append("MBBS")}},

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 30.dp,

                    y = 125.dp))

        Text(

            text = "Patient Reg.No: 123456",

            color = Color.Black,

            style = TextStyle(fontSize = 14.sp),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 30.dp,

                    y = 204.dp))

        Text(

            text = "Name: Madhavan M",

            color = Color.Black,

            style = TextStyle(

                fontSize = 14.sp),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 30.dp,

                    y = 233.dp))

        Text(

            text = "Age: 22",

            color = Color.Black,

            style = TextStyle(

                fontSize = 14.sp),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 228.dp,

                    y = 233.dp))

        Text(

            text = "Gender: Male",

            color = Color.Black,

            style = TextStyle(

                fontSize = 14.sp),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 324.dp,

                    y = 233.dp))

        Text(

            text = "Date: 27/09/2023",

            color = Color.Black,

            style = TextStyle(

                fontSize = 14.sp),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 455.dp,

                    y = 233.dp))

        Text(

            text = "Powered by Aarogya Tech",

            color = Color.Black,

            style = TextStyle(

                fontSize = 18.sp),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 359.dp,

                    y = 111.dp))

        Text(

            text = "Chief Complaint",

            color = Color(0xff030c43),

            style = TextStyle(

                fontSize = 14.sp,

                fontWeight = FontWeight.Medium),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 30.dp,

                    y = 294.dp))

        Text(

            text = "Past medical & Surgical History",

            color = Color(0xff030c43),

            style = TextStyle(

                fontSize = 14.sp,

                fontWeight = FontWeight.Medium),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 30.dp,

                    y = 357.dp))

        Text(

            text = "Vitals",

            color = Color(0xff030c43),

            style = TextStyle(

                fontSize = 14.sp,

                fontWeight = FontWeight.Medium),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 31.dp,

                    y = 420.dp))

        Text(

            text = "Laboratory & Radiology",

            color = Color(0xff030c43),

            style = TextStyle(

                fontSize = 14.sp,

                fontWeight = FontWeight.Medium),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 30.dp,

                    y = 483.dp))

        Text(

            text = "Next Visit",

            color = Color(0xff030c43),

            style = TextStyle(

                fontSize = 14.sp,

                fontWeight = FontWeight.Medium),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 30.dp,

                    y = 545.dp))

        Text(

            text = "Address Line 1",

            color = Color.Black,

            style = TextStyle(

                fontSize = 14.sp),

            modifier = Modifier

                .align(alignment = Alignment.TopCenter)

                .offset(x = 0.dp,

                    y = 762.dp))

        Text(

            text = "Address Line 1",

            color = Color.Black,

            style = TextStyle(

                fontSize = 14.sp),

            modifier = Modifier

                .align(alignment = Alignment.TopCenter)

                .offset(x = 0.dp,

                    y = 787.dp))

        Box(

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 447.dp, y = 66.dp)

                .requiredWidth(width = 32.dp)

                .requiredHeight(height = 40.dp)

        ) {

            Image(

                painter = painterResource(id = R.drawable.logo_app),

                contentDescription = "image 3",

                contentScale = ContentScale.Crop,

                modifier = Modifier

                    .align(alignment = Alignment.Center)

                    .offset(x = 0.dp,

                        y = 0.dp)

                    .requiredWidth(width = 32.dp)

                    .requiredHeight(height = 40.dp))

        }

        Text(

            text = "Pain abdomen",

            color = Color.Black,

            style = TextStyle(

                fontSize = 12.sp),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 62.dp,

                    y = 319.dp))

        Text(

            text = "Pain abdomen",

            color = Color.Black,

            style = TextStyle(

                fontSize = 12.sp),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 62.dp,

                    y = 382.dp))

        Text(

            text = "BP- 120/80",

            color = Color.Black,

            style = TextStyle(

                fontSize = 12.sp),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 62.dp,

                    y = 445.dp))

        Text(

            text = "Heart rate: 99",

            color = Color.Black,

            style = TextStyle(

                fontSize = 12.sp),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 141.dp,

                    y = 445.dp))

        Text(

            text = "Temperature: 72ᵒc",

            color = Color.Black,

            style = TextStyle(

                fontSize = 12.sp),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 231.dp,

                    y = 445.dp))

        Text(

            text = "CT Scan for abdomen required.",

            color = Color.Black,

            style = TextStyle(

                fontSize = 12.sp),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 62.dp,

                    y = 507.dp))

        Text(

            text = "Next week, Monday.",

            color = Color.Black,

            style = TextStyle(

                fontSize = 12.sp),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 62.dp,

                    y = 570.dp))

        Divider(

            color = Color.Black,

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 30.dp,

                    y = 262.dp)

                .requiredWidth(width = 535.dp))

        Box(

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 0.dp,

                    y = 812.dp)

                .requiredWidth(width = 595.dp)

                .requiredHeight(height = 30.dp)

                .background(color = Color(0xfffe8b10).copy(alpha = 0.78f)))

        Text(

            text = "Weight: 60kg",

            color = Color.Black,

            style = TextStyle(

                fontSize = 12.sp),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 343.dp,

                    y = 445.dp))

        Text(

            text = "SPO2: 93",

            color = Color.Black,

            style = TextStyle(

                fontSize = 12.sp),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 430.dp,

                    y = 445.dp))

        Text(

            text = "ECG: Normal",

            color = Color.Black,

            style = TextStyle(

                fontSize = 12.sp),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 497.dp,

                    y = 445.dp))

        Text(

            text = "Address line: xxxxx",

            color = Color.Black,

            style = TextStyle(

                fontSize = 12.sp,

                fontWeight = FontWeight.Light),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 30.dp,

                    y = 156.dp))

        Text(

            text = "Contact no: 1234567890",

            color = Color.Black,

            style = TextStyle(

                fontSize = 12.sp,

                fontWeight = FontWeight.Light),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 30.dp,

                    y = 174.dp))

        Text(

            text = "Impression & Plan",

            color = Color(0xff030c43),

            style = TextStyle(

                fontSize = 14.sp,

                fontWeight = FontWeight.Medium),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 30.dp,

                    y = 608.dp))

        Text(

            text = "Patient is feeling high fever, seeking to come back.",

            color = Color.Black,

            style = TextStyle(

                fontSize = 12.sp),

            modifier = Modifier

                .align(alignment = Alignment.TopStart)

                .offset(x = 62.dp,

                    y = 633.dp))

    }

}



@Preview()
@Composable

private fun A41Preview() {

    A41(Modifier)

}




fun getAge(user: SubUserProfile) : String{
    val dob = user.dob.split("/")
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH) + 1 // add 1 since month is 0-based
    try {
        return if(dob.size == 2){
            val givenMonth = dob[0].toInt()
            val givenYear = dob[1].toInt()
            var age = currentYear - givenYear
            if(currentMonth < givenMonth){
                age--
            }
            age.toString()
        }else{
            ""
        }
    }catch (e : NumberFormatException){
        return ""
    }
}

@Composable
fun updateColorState(): Color {
    var isColor1 by remember { mutableStateOf(true) }
    val colorState = if (isColor1) Color(0xffdae3f3) else Color(0x80FFEB3B)

    LaunchedEffect(1000) {
        while (true) {
            delay(1000.toLong())
            isColor1 = !isColor1
        }
    }
    return colorState
}

@Composable
fun BloodPressure(pc300Repository: PC300Repository, context: Context){

    val isState = pc300Repository.bloodPressure.value
//    if(isState.contains("mmHg")){
    var bpValue = isState.replace("mmHg", "")
////    }
//    Log.d("TAG", "BloodPressure: $bpValue")
//
    val seprateBp =  bpValue.split("/")
    var sys = if(seprateBp.size == 2) seprateBp[0] else ""
    var dia = if(seprateBp.size == 2) seprateBp[1] else ""


    val colorHandler = Handler(Looper.getMainLooper())

    val colorState = updateColorState()

    val isError by remember { mutableStateOf(pc300Repository.bloodPressure.value.contains("0/0")) }

    Card(modifier = Modifier.size(width = cardWidth, height = cardHeight),
        shape = RoundedCornerShape(15.dp))
    {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                when {
                    isState.isNullOrEmpty() -> Color(0xffdae3f3)
                    isState.contains("0/0") -> Color(0x80BB3F3F)
                    isState.contains("/") -> Color(0x8090EE90)
                    isState.contains("e") -> Color(0x59BB3F3F)
                    else -> colorState
                }
            )){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            )
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    BoldTextView(title = "BP", fontSize = 18)

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.bp),
                        contentDescription ="BPIcon",Modifier.size(15.dp) )
                }
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {

//                    BoldTextView(title = "144/104", fontSize = 25)
//                    Log.d("TAG", "BloodPressure: value $isState")
                    when{
                        isState.isNullOrEmpty()-> {
                            Text(text = "")
                        }

                        isState.contains("0/0 mmHg") || isState == "e" -> {
                            MainActivity.playerRepo.stopBpSound()
                            Column() {
                                BoldTextView(title = "Error: Do", fontSize = 25)
                                Spacer(modifier = Modifier.height(5.dp))
                                BoldTextView(title = "it again", fontSize = 25)
                            }
                        }

                        isState.contains("/") -> {
                            MainActivity.playerRepo.stopBpSound()
                            colorHandler.removeCallbacksAndMessages(null)
                            BoldTextView(title = bpValue, fontSize = if(dia > "99") 25 else 30)
                        }

                        else -> {
                            MainActivity.playerRepo.startBpSound()
                            BoldTextView(title = bpValue
//                            pc300Repository.bloodPressure.value
                                , fontSize = 30)
                        }
                    }

                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    RegularTextView(title = if(bpValue.contains("/") && !bpValue.contains("0/0")) "mmHg" else "", fontSize = 18)
                }
            }
        }
    }
}

@Composable
fun HeartRate(pc300Repository: PC300Repository){
    val hrWithUnit = pc300Repository.heartRate.value
    var hrWoUnit = hrWithUnit.replace("bpm", "")

    Card(modifier = Modifier.size(width = cardWidth, height = cardHeight),
        shape = RoundedCornerShape(15.dp))
    {

        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                when {
                    hrWoUnit.isNullOrEmpty() -> Color(0xffdae3f3)
                    hrWoUnit == "0 " -> Color(0x80BB3F3F)
                    else -> Color(0x8090EE90)
                }
//                when (pc300Repository.heartRate.value.isEmpty()) {
//                    true -> Color(0xffdae3f3)
//                    false -> Color(0x8090EE90)
//                }
            )){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            )
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    BoldTextView(title = "Heart Rate", fontSize = 18)

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.hr),
                        contentDescription ="HRIcon",Modifier.size(15.dp) )
                }

                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    BoldTextView(title = hrWoUnit, fontSize = 30)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    RegularTextView(title = if(hrWoUnit.isNullOrEmpty()) "" else "bpm", fontSize = 18)
                }
            }
        }
    }
}

@Composable
fun SPO2(pc300Repository: PC300Repository){

    val isTaken by remember { mutableStateOf(pc300Repository.spO2.value.isNotEmpty()) }
    var spO2WithUnit = pc300Repository.spO2.value
    var spo2WoUnit = spO2WithUnit.replace("%", "")

    Card(modifier = Modifier.size(width = cardWidth, height = cardHeight),
        shape = RoundedCornerShape(15.dp))
    {

        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                when (pc300Repository.spO2.value.isEmpty()) {
                    true -> Color(0xffdae3f3)
                    false -> Color(0x8090EE90)
                }
            )){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            )
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    BoldTextView(title = "SpO2", fontSize = 18)

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.userspo),
                        contentDescription ="Spo2Icon",Modifier.size(15.dp) )
                }
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    BoldTextView(title = spo2WoUnit, fontSize = 30)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    RegularTextView(title = if(spo2WoUnit.isNullOrEmpty()) "" else "%", fontSize = 18)
                }
            }
        }
    }
}

@Composable
fun Weight(omronRepository: OmronRepository){

    var isRegistered by remember { mutableStateOf(true) }

    var isSynced by remember { mutableStateOf(true) }

    if(!isRegistered) StartRegistration()

    if(isRegistered && !isSynced) StartSyncing()

    Card(modifier = Modifier
        .size(width = cardWidth, height = cardHeight),
        shape = RoundedCornerShape(15.dp))
    {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                if(MainActivity.trackyRepo.trackyConnectionState.value == false){
                    when {
                        omronRepository.deviceStat.value == "Syncing" -> Color(0x80FFEB3B)

                        omronRepository.latestUserWeightInfo.value != null -> Color(0x8090EE90)

                        else -> {
                            Color(0xffdae3f3)
                        }
                    }
                }else{
                    Color(0xffdae3f3)
                }
            )){

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            )
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {

                    BoldTextView(title = "Weight", fontSize = 18)

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.weightuser), contentDescription ="weightIcon",Modifier.size(15.dp) )

                }

                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    BoldTextView(
                        title = if(omronRepository.latestUserWeightInfo.value != null)  MainActivity.adminDBRepo.getWeightBasedOnUnitSet(omronRepository.latestUserWeightInfo.value!!.weight.toDoubleOrNull()) else omronRepository.deviceStat.value ,
                        fontSize = 30)
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    RegularTextView(title = if(omronRepository.latestUserWeightInfo.value != null) MainActivity.adminDBRepo.getWeightUnit() else "", fontSize = 18)
                }

            }
        }
    }
}

@Composable
fun WeightTracky(trackyRepo: TrackyManager){

    Card(modifier = Modifier
        .size(width = cardWidth, height = cardHeight),
        shape = RoundedCornerShape(15.dp))
    {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                if(trackyRepo.latestDeviceData.value != null){
                    Color(0x8090EE90)
                }else{
                    Color(0xffdae3f3)

                }
            )){

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            )
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {

                    BoldTextView(title = "Weight", fontSize = 18)

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.weightuser), contentDescription ="weightIcon",Modifier.size(15.dp) )
                }

                Row(modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    BoldTextView(title = if(MainActivity.trackyRepo.latestDeviceData.value != null) MainActivity.trackyRepo.latestDeviceData.value!!.bleScaleData.weight.toString() else "", fontSize = 30)
                }

            }
        }
    }
}

@Composable
fun Temperature(pc300Repository: PC300Repository){

    val tempInC = pc300Repository.temp.value.substringBefore("°C").toDoubleOrNull()

    var tempWoUnit = MainActivity.adminDBRepo.getTempBasedOnUnitSet(tempInC)

    Card(modifier = Modifier.size(width = cardWidth, height = cardHeight),
        shape = RoundedCornerShape(15.dp))
    {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                when (pc300Repository.temp.value.isEmpty()) {
                    true -> Color(0xffdae3f3)
                    false -> Color(0x8090EE90)
                }
            )
        ){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween)
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    BoldTextView(title = "Temp", fontSize = 18)

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.temp),
                        contentDescription ="tempIcon",Modifier.size(15.dp) )

                }

                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    BoldTextView(title = tempWoUnit, fontSize = 30)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    RegularTextView(title = if(tempWoUnit.isNullOrEmpty()) "" else MainActivity.adminDBRepo.getTempUnit(), fontSize = 18)
                }
            }
        }
    }
}

@Composable
fun ECG(pc300Repository: PC300Repository, context: Context, onClickEcgResult : () -> Unit){

    val isState = pc300Repository.ecg.value

    val colorHandler = Handler(Looper.getMainLooper())

    val colorState = updateColorState()

    Card(modifier = Modifier.size(width = cardWidth, height = cardHeight),
        shape = RoundedCornerShape(15.dp))
    {
        Box(modifier = Modifier
            .fillMaxSize()
            .clickable {
                if (isState == 1 || isState == 2 || isState == 3 || isState == 4) {
                    MainActivity.pc300Repo.isShowEcgRealtimeAlert.value =
                        !MainActivity.pc300Repo.isShowEcgRealtimeAlert.value
                }
            }
            .background(
                when (isState) {
                    0 -> colorState
                    1 -> colorState
                    2 -> Color(0x8090EE90)
                    3, 4 -> Color(0x80BB3F3F)
                    else -> {
                        Color(0xffdae3f3)
                    }
                }
            )) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {

                    BoldTextView(title = "ECG", fontSize = 18)

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ecg),
                        contentDescription ="ECGIcon",Modifier.size(15.dp) )
                }

                Row(modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    when (isState){

                        0,1-> {
                            MainActivity.playerRepo.startEcgSound()
                            if(!isShown) {
                                isShown = true
                                isWriting = true
                                MainActivity.pc300Repo.isShowEcgRealtimeAlert.value = true
                            }
                            if(MainActivity.pc300Repo.ecgWireOff.value){
                                BoldTextView(title = "ECG leadwire off", fontSize = 25)
                            }else{
                                BoldTextView(title = "Measuring", fontSize = 25)
                            }
                        }

                        2-> {
                            if(isWriting){
                                isWriting = false
                            }
                            MainActivity.playerRepo.stopEcgSound()
                            isShown = false
                            colorHandler.removeCallbacksAndMessages(null)
                            BoldTextView(title = "Done (${MainActivity.pc300Repo.ecgResultCode.value})", fontSize = 25)
                        }

                        3-> {
                            MainActivity.playerRepo.stopEcgSound()
                            isShown = false
                            Column() {
                                BoldTextView(title = "Error: Do", fontSize = 25)
                                Spacer(modifier = Modifier.height(5.dp))
                                BoldTextView(title = "it again", fontSize = 25)
                            }
                        }

                        4-> {
                            MainActivity.playerRepo.stopEcgSound()
                            colorHandler.removeCallbacksAndMessages(null)
                            BoldTextView(title = "Stopped", fontSize = 25)
                        }
                        else -> {
                            Text(text = "")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BodyFat(omronRepository: OmronRepository){
    Card(modifier = Modifier
        .size(width = cardWidth, height = 70.dp),
        shape = RoundedCornerShape(15.dp))
    {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                when {
                    omronRepository.deviceStat.value == "Syncing" -> Color.Yellow

                    omronRepository.latestUserWeightInfo.value != null -> Color(0xFF90EE90)

                    else -> {
                        Color.LightGray
                    }
                }
            )){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    BoldTextView(title = "Body Fat", fontSize = 16)

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.bodyfat),
                        contentDescription ="Body Fat Icon",Modifier.size(15.dp) )
                }
                Row(modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    BoldTextView(title = if(omronRepository.latestUserWeightInfo.value != null) omronRepository.latestUserWeightInfo.value!!.bodyFat + " %" else omronRepository.deviceStat.value, fontSize = 14)
                }
            }
        }
    }
}

@Composable
fun BodyFatTracky(trackyRepo: TrackyManager){
    Card(modifier = Modifier
        .size(width = cardWidth, height = 70.dp),
        shape = RoundedCornerShape(15.dp))
    {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                if(trackyRepo.latestDeviceData.value != null){
                    Color(0x8090EE90)
                }else{
                    Color(0xffdae3f3)
                }
            )){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    BoldTextView(title = "Body Fat", fontSize = 16)

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.bodyfat),
                        contentDescription ="Body Fat Icon",Modifier.size(15.dp) )
                }
                Row(modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    BoldTextView(title = if(MainActivity.trackyRepo.latestDeviceData.value != null) MainActivity.trackyRepo.latestDeviceData.value!!.bleScaleData.bodyfat.toString() else "", fontSize = 14)
                }
            }
        }
    }
}

@Composable
fun BmiTracky(trackyRepo: TrackyManager){
    Card(modifier = Modifier
        .size(width = cardWidth, height = 70.dp),
        shape = RoundedCornerShape(15.dp))
    {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                if(trackyRepo.latestDeviceData.value != null){
                    Color(0x8090EE90)
                }else{
                    Color(0xffdae3f3)
                }
            )){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    BoldTextView(title = "BMI", fontSize = 16)

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.bmi),
                        contentDescription ="BMI Icon",Modifier.size(15.dp) )
                }
                Row(modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    BoldTextView(title = if(MainActivity.trackyRepo.latestDeviceData.value != null) MainActivity.trackyRepo.latestDeviceData.value!!.bleScaleData.bmi.toString() else "", fontSize = 14)
                }
            }
        }
    }
}

@Composable
fun BMI(omronRepository: OmronRepository){

    Card(modifier = Modifier
        .size(width = cardWidth, height = 70.dp),
        shape = RoundedCornerShape(15.dp))
    {

        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                when {
                    omronRepository.deviceStat.value == "Syncing" -> Color.Yellow

                    omronRepository.latestUserWeightInfo.value != null -> Color(0xFF90EE90)

                    else -> {
                        Color.LightGray
                    }
                }
            )){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {

                    BoldTextView(title = "BMI", fontSize = 16)

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.bmi),
                        contentDescription ="BMI Icon",Modifier.size(15.dp) )
                }

                Row(modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    BoldTextView(title = if(omronRepository.latestUserWeightInfo.value != null) omronRepository.latestUserWeightInfo.value!!.bmi else omronRepository.deviceStat.value, fontSize = 14)
                }
            }
        }
    }
}

@Composable
fun GLU(repo: PC300Repository){

    Card(modifier = Modifier
        .size(width = cardWidth, height = 70.dp),
        shape = RoundedCornerShape(15.dp))
    {

        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                when {

                    repo.glu.value.isNotEmpty() -> Color(0xFF90EE90)

                    else -> {
                        Color.LightGray
                    }
                }
            )){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {

                    BoldTextView(title = "GLU", fontSize = 16)

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.bmi),
                        contentDescription ="BMI Icon",Modifier.size(15.dp) )
                }

                Row(modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    BoldTextView(title = if(repo.glu.value != null) repo.glu.value else "", fontSize = 14)
                }
            }
        }
    }
}


@Composable
fun UserQuestionOne(pc300Repository: PC300Repository, isReset : Boolean, changeResetState: () -> Unit) {

    var isSelected1A = MainActivity.subUserRepo.isSelected1A

    var isSelected1B = MainActivity.subUserRepo.isSelected1B

    var isSelected1C = MainActivity.subUserRepo.isSelected1C

    var isAnswerExpanded by remember { mutableStateOf(false) }

    if(isReset) {
        isSelected1A.value = false
        isSelected1B.value = false
        isSelected1C.value = false
    }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        BoldTextView(title = "How are you feeling?", fontSize = 14)

        Icon(
            imageVector = when (isAnswerExpanded){
                true -> ImageVector.vectorResource(id = R.drawable.dropdown_up_icon)
                false ->ImageVector.vectorResource(id = R.drawable.dropdown_icon)
            },
            contentDescription = if (isAnswerExpanded) "Collapse answer" else "Expand answer",
            tint = Color.Black,
            modifier = Modifier.clickable {
                MainActivity.subUserRepo.isResetQuestion.value = false
                isAnswerExpanded = !isAnswerExpanded }
        )
    }
    if (isAnswerExpanded) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Card(
                Modifier
                    .clickable {
                        isSelected1A.value = true
                        isSelected1B.value = false
                        isSelected1C.value = false
                        changeResetState()
                        pc300Repository.updateOnGoingSessionQuestionAnswers(1, "1")
                    }
                    .size(width = 90.dp, height = 30.dp)
            ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(if (isSelected1A.value) Color(0xFF397EF5) else Color.LightGray),
                    contentAlignment = Alignment.Center) {
                    RegularTextView(title = "Awesome", fontSize = 14)
                }
            }

            Spacer(modifier = Modifier.width(30.dp))

            Card(
                Modifier
                    .clickable {
                        isSelected1A.value = false
                        isSelected1B.value = true
                        isSelected1C.value = false
                        changeResetState()
                        pc300Repository.updateOnGoingSessionQuestionAnswers(1, "2")
                    }
                    .size(width = 90.dp, height = 30.dp))
            {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isSelected1B.value) Color(0xFF397EF5) else Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    RegularTextView(title = "Well", fontSize = 14)
                }
            }

            Spacer(modifier = Modifier.width(30.dp))


            Card(
                Modifier
                    .clickable {
                        isSelected1A.value = false
                        isSelected1B.value = false
                        isSelected1C.value = true
                        pc300Repository.updateOnGoingSessionQuestionAnswers(1, "3")
                    }
                    .size(width = 90.dp, height = 30.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isSelected1C.value) Color(0xFF397EF5) else Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    RegularTextView(title = "Not well", fontSize = 14)
                }
            }
        }
    }
}

@Composable
fun UserQuestionTwo(pc300Repository: PC300Repository, isReset: Boolean, changeResetState : () -> Unit){

    var isSelected2A = MainActivity.subUserRepo.isSelected2A

    var isSelected2B = MainActivity.subUserRepo.isSelected2B

    var isAnswerExpanded by remember { mutableStateOf(false) }

    if(isReset) {
        isSelected2A.value = false
        isSelected2B.value = false
    }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        BoldTextView(title = "Have you drunk alcohol in the last 24 hours?", fontSize = 14)

        Icon(
            imageVector = when (isAnswerExpanded){
                true -> ImageVector.vectorResource(id = R.drawable.dropdown_up_icon)
                false ->ImageVector.vectorResource(id = R.drawable.dropdown_icon)
            },
            contentDescription = if (isAnswerExpanded) "Collapse answer" else "Expand answer",
            tint = Color.Black,
            modifier = Modifier.clickable {
                MainActivity.subUserRepo.isResetQuestion.value = false
                isAnswerExpanded = !isAnswerExpanded }
        )
    }
    if (isAnswerExpanded) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Card(
                Modifier
                    .clickable {
                        isSelected2A.value = true
                        isSelected2B.value = false
                        changeResetState()
                        pc300Repository.updateOnGoingSessionQuestionAnswers(2, "1")
                    }
                    .size(60.dp, height = 30.dp)
            ){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isSelected2A.value) Color(0xFF397EF5) else Color.LightGray),
                    contentAlignment = Alignment.Center,

                    ) {
                    RegularTextView(title = "Yes", fontSize = 14)
                }
            }

            Spacer(modifier = Modifier.width(100.dp))

            Card(
                Modifier
                    .clickable {
                        isSelected2A.value = false
                        isSelected2B.value = true
                        changeResetState()
                        pc300Repository.updateOnGoingSessionQuestionAnswers(2, "2")
                    }
                    .size(width = 60.dp, height = 30.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isSelected2B.value) Color(0xFF397EF5) else Color.LightGray),
                    contentAlignment = Alignment.Center,
                ) {
                    RegularTextView(title = "No", fontSize = 14)
                }
            }
        }
    }
}

@Composable
fun UserQuestionThree(pc300Repository: PC300Repository, medicationName : String, isReset: Boolean, changeResetState : () -> Unit){

    var isSelected3A = MainActivity.subUserRepo.isSelected3A

    var isSelected3B = MainActivity.subUserRepo.isSelected3B

    var isAnswerExpanded by remember { mutableStateOf(false) }

    if(isReset) {
        isSelected3A.value = false
        isSelected3B.value = false
    }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BoldTextView(title = "Did you take your $medicationName medicine?", fontSize = 14)
        Icon(
            imageVector = when (isAnswerExpanded){
                true -> ImageVector.vectorResource(id = R.drawable.dropdown_up_icon)
                false ->ImageVector.vectorResource(id = R.drawable.dropdown_icon)
            },
            contentDescription = if (isAnswerExpanded) "Collapse answer" else "Expand answer",
            tint = Color.Black,
            modifier = Modifier.clickable {
                MainActivity.subUserRepo.isResetQuestion.value = false
                isAnswerExpanded = !isAnswerExpanded }
        )
    }
    if (isAnswerExpanded) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Card(
                Modifier
                    .clickable {
                        isSelected3A.value = true
                        isSelected3B.value = false
                        changeResetState()
                        pc300Repository.updateOnGoingSessionQuestionAnswers(3, "1")
                    }
                    .size(60.dp, height = 30.dp)
            ){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isSelected3A.value) Color(0xFF397EF5) else Color.LightGray),
                    contentAlignment = Alignment.Center,

                    ) {
                    RegularTextView(title = "Yes", fontSize = 14)
                }
            }

            Spacer(modifier = Modifier.width(100.dp))

            Card(
                Modifier
                    .clickable {
                        isSelected3A.value = false
                        isSelected3B.value = true
                        changeResetState()
                        pc300Repository.updateOnGoingSessionQuestionAnswers(3, "2")
                    }
                    .size(width = 60.dp, height = 30.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isSelected3B.value) Color(0xFF397EF5) else Color.LightGray),
                    contentAlignment = Alignment.Center,
                ) {
                    RegularTextView(title = "No", fontSize = 14)
                }
            }
        }
    }
}


@Composable
fun StartRegistration(){

    var isRegistring by remember { mutableStateOf(false) }
    var isRegTitleMsg by remember { mutableStateOf("Start User registration") }
    var isRegSubTitleMsg by remember { mutableStateOf("Please, put device in pairing mode") }


    if(MainActivity.omronRepo.omronRegistrationFailed.value == true){
        isRegTitleMsg = "Failed, Pelase put device in pairing mode"
        isRegSubTitleMsg = "Device must be in pairing mode before starting"
    }

    Box(){
        AlertDialog(
            onDismissRequest = { },
            text = {
                Column( Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(isRegTitleMsg, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(isRegSubTitleMsg, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                if(!isRegistring){
                    Button(
                        onClick = {
                            isRegistring = true
                            isRegTitleMsg = "Registring"
                            isRegSubTitleMsg = "Device must be in pair mode"
                            MainActivity.omronRepo.getLastSeqIncrementKeyForSelectedUser()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ){Text("Start Registration",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center)}
                }
            },
            dismissButton = null
        )
        if(isRegistring) showProgress()
    }
}

@Composable
fun StartSyncing(){
    var isSyncingTitleMsg by remember { mutableStateOf("Start Syncing profile") }
    var isSyncingSubTitleMsg by remember { mutableStateOf("Please, put device in pairing mode") }
    var isSyncing by remember { mutableStateOf(false) }
    if(MainActivity.omronRepo.omronRegistrationFailed.value == true){
        isSyncingTitleMsg = "Failed, Pelase pur device in pairing mode"
        isSyncingSubTitleMsg = "Device must be in pairing mode before starting"
    }

    Box(){
        AlertDialog(
            onDismissRequest = { },
            text = {
                Column( Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(isSyncingTitleMsg, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(isSyncingSubTitleMsg, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                if(!isSyncing){
                    Button(onClick = {
                        isSyncingTitleMsg = "Syncing"
                        isSyncingSubTitleMsg = "Device must be in pair mode"
                        isSyncing = true
                        MainActivity.omronRepo.syncUserDataToDevice()
                    },
                        modifier = Modifier.fillMaxWidth()
                    ){Text("Start Syncing",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center)}
                } }, dismissButton = null
        )
        if(isSyncing) showProgress()
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun viewUserHome(){
    UserHomeScreen(
        navHostController = rememberNavController(),
        repository = AdminDBRepository(),
        pc300Repository = PC300Repository(),
        locationRepository = LocationRepository(),
        subUserDBRepository = SubUserDBRepository(),
        s3Repository = S3Repository(),
        csvRepository = CsvRepository()
    )
}