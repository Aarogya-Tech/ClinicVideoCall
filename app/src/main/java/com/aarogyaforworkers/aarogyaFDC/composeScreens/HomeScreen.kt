package com.aarogyaforworkers.aarogyaFDC.composeScreens

import Commons.HomePageTags
import Commons.UserHomePageTags
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogyaFDC.AdminDB.AdminDBRepository
import com.aarogyaforworkers.aarogyaFDC.Auth.AuthRepository
import com.aarogyaforworkers.aarogyaFDC.Commons.*
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.Location.LocationRepository
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.PC300.PC300Repository
import com.aarogyaforworkers.aarogyaFDC.SubUser.SessionStates
import com.aarogyaforworkers.aarogyaFDC.checkBluetooth
import com.aarogyaforworkers.aarogyaFDC.isBluetoothEnabled
import com.aarogyaforworkers.awsapi.models.SubUserProfile
import android.content.Intent
import android.media.Image
import android.speech.RecognizerIntent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aarogyaforworkers.aarogya.R
import com.aarogyaforworkers.aarogyaFDC.ui.theme.defCardDark
import com.aarogyaforworkers.aarogyaFDC.ui.theme.logoOrangeColor
import java.util.*

var lastUpdatedSignOutValue = false
var isAdminHomeScreenSetUp = false
var subUserSelected = false


@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(navHostController: NavHostController, authRepository: AuthRepository, adminRepository : AdminDBRepository, pc300Repository: PC300Repository, locationRepository: LocationRepository) {

    Disableback()

    CheckInternet(context = LocalContext.current)

    isOnUserHomeScreen = false
    val keyboardController = LocalSoftwareKeyboardController.current

    var isClickedOnSearch = remember { mutableStateOf(false) }
    var focusRequester = remember { FocusRequester() }


    val context = LocalContext.current

    val bleEnabled by remember { mutableStateOf(isBluetoothEnabled()) }

    if(!bleEnabled) checkBluetooth(context)

    MainActivity.adminDBRepo.getTotalRegistrationCounts()

    pc300Repository.isOnSessionPage = false

    MainActivity.shared.initializeOmronPC300(LocalContext.current)

    MainActivity.csvRepository.setUpNewContext(LocalContext.current)

//    RealtimeEcgAlertView()

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .testTag(HomePageTags.shared.homeScreen)
    ) {
        Column {
            Spacer(modifier = Modifier.height(25.dp))
            ProfileView(navHostController)
            Spacer(modifier = Modifier.height(25.dp))
//            SpeechToTextScreen()

            if(isClickedOnSearch.value){
                Column(Modifier.weight(1f)) {
                    UserSearchView(navHostController)
                }
            }
            else {
                Column(Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    BoldTextView(title = "Welcome to AarogyaTech")
                    TextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { RegularTextView("Enter Name, Phone no or Id...", 16, Color.Gray) },
                        leadingIcon = { Icon(Icons.Filled.Search, null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    isClickedOnSearch.value = true
                                    keyboardController?.show()
                                }
                            }
                            .testTag(HomePageTags.shared.searchView)
                            .background(defCardDark, shape = RoundedCornerShape(8.dp)),
                        singleLine = true,

                        )
                }
            }



//            Column(Modifier.weight(1f)) {
//                UserSearchView(navHostController)
//            }
            locationRepository.getLocation(LocalContext.current)
            subUserSelected = false
            Spacer(modifier = Modifier.height(15.dp))

            ActionBtnView(navHostController)
        }
    }

    if(authRepository.userSignOutState.value && isAdminHomeScreenSetUp){
        isLoginScreenSetUp = false
        isAllreadyOnHome = false
        isAdminHomeScreenSetUp = false
        if(lastUpdatedSignOutValue != authRepository.userSignOutState.value){
            MainActivity.adminDBRepo.resetLoggedInUser()
            navHostController.navigate(Destination.Login.routes)
            lastUpdatedSignOutValue = authRepository.userSignOutState.value
        }
        authRepository.updateSignInState(false)
    }

    if(!isAdminHomeScreenSetUp) isAdminHomeScreenSetUp = true

    if(adminRepository.getLoggedInUser().admin_id.isEmpty()){
        adminRepository.getProfile(authRepository.getAdminUID())
    }
}

@Composable
fun SpeechToTextScreen() {

    var speechText by remember { mutableStateOf("") }

    val speechIntentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == ComponentActivity.RESULT_OK && result.data != null) {
            val resultData = result.data
            val resultText = resultData?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognizedText = resultText?.get(0)
            recognizedText?.let {
                speechText = it
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicTextField(
            value = speechText,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            readOnly = true
        )

        Button(
            onClick = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

                try {
                    speechIntentLauncher.launch(intent)
                } catch (e: Exception) {
                    // Handle exceptions as needed
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Start Speech Recognition")
        }
    }
}






/*
 * A Composable function that renders the user's profile view.
 * Takes a NavHostController as a parameter to navigate to other destinations.
 */
@Composable
fun ProfileView(navHostController: NavHostController){

    val profile = MainActivity.adminDBRepo.adminProfileState.value

    var showSignOutAlert by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            UserImageView(imageUrl = profile.profile_pic_url, size = 40.dp){
                navHostController.navigate(Destination.AdminProfile.routes)
            }
        }

        //UserImageView(imageUrl = profile.profile_pic_url, size = 65.dp) { navHostController.navigate(Destination.AdminProfile.routes) }

        Spacer(modifier = Modifier.width(15.dp))

        val heyGreeting = stringResource(id = R.string.hey_greeting)

        Box(modifier = Modifier
            .weight(1f)
            .testTag(HomePageTags.shared.getAdminTag(profile))){ TitleView(title = "$heyGreeting, "+MainActivity.adminDBRepo.adminProfileState.value.first_name + " ") }


        Box(
            Modifier
                .size(44.dp)
                .testTag(UserHomePageTags.shared.connectionBtn),
            contentAlignment = Alignment.Center
        ) {
            ConnectionActionBtn(isConnected = MainActivity.pc300Repo.connectionStatus.value, 44.dp) {
                isFromUserHome = false
                navHostController.navigate(Destination.DeviceConnection.routes)
            }
        }

//        ConnectionBtnView(isConnected = MainActivity.pc300Repo.connectionStatus.value, 36.dp) {
//            isFromUserHome = false
//            navHostController.navigate(Destination.DeviceConnection.routes) }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            Modifier
                .size(44.dp)
                .testTag(UserHomePageTags.shared.connectionBtn),
            contentAlignment = Alignment.Center
        ) {
            SignOutBtnView { showSignOutAlert = true }
        }


        Spacer(modifier = Modifier.width(5.dp))

        // Conditionally show the sign-out alert
        SignOutAlertView(showAlert = showSignOutAlert, onSignOutClick = {
            isLastUpdatedValue = false
            isAllreadyOnHome = false
            lastUpdatedSignOutValue = false
            MainActivity.authRepo.signOut() }) {
            // on Cancel
            showSignOutAlert = false
        }
    }
}

/*
 * A Composable function that renders the action button view.
 * Takes a NavHostController as a parameter to navigate to other destinations.
 */
@Composable
fun ActionBtnView(navHostController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        val createPlaceholder = stringResource(id = R.string.Create_New_User)

        PopUpBtnSingle(btnName = createPlaceholder, {
            MainActivity.adminDBRepo.userPhoneCountryCode.value = "91"
            MainActivity.subUserRepo.clearSessionList()
            isEditUser = false
            lastCreateUserValue = false
            lastUserRegisteredState = true
            lastUserNotRegisteredState = false
            isEditUser = false
            userProfileToEdit = null
            isSetUpDone = false
            isUpdatingProfile = false
            MainActivity.adminDBRepo.setSubUserProfilePicture(null)
            isCurrentUserVerifiedPhone = ""
            newUserProfile = SubUserProfile("","","","",false,"","","","","","","", "","","","","","","","","","")
            isCameraCliked = false
            isCheckingUserBeforeSendingOTP = false
            isUserAllreadyRegistered = false
            allReadyRegisteredPhone = ""
            isSavingOrUpdating = false
            isAllreadyOtpSent = false
            MainActivity.adminDBRepo.resetStates()
            newUserProfile = SubUserProfile("","","","",false,"","","","","","","", "","","","","","","","","","")
            MainActivity.adminDBRepo.resetMedicalAnswers()
            navHostController.navigate(Destination.AddNewUser.routes)
        },
            Modifier
                .fillMaxWidth()
                .height(50.dp), containerColor = logoOrangeColor)

//        Box(modifier = Modifier.weight(1f)) {
//            ActionBtn(title = "Create New User") {
//                MainActivity.subUserRepo.clearSessionList()
//                isEditUser = false
//                lastCreateUserValue = false
//                lastUserRegisteredState = true
//                lastUserNotRegisteredState = false
//                isEditUser = false
//                userProfileToEdit = null
//                isSetUpDone = false
//                isUpdatingProfile = false
//                MainActivity.adminDBRepo.setSubUserProfilePicture(null)
//                isCurrentUserVerifiedPhone = ""
//                newUserProfile = SubUserProfile("","","","",false,"","","","","","","", "","","","","","","","","")
//                isCameraCliked = false
//                isCheckingUserBeforeSendingOTP = false
//                isUserAllreadyRegistered = false
//                allReadyRegisteredPhone = ""
//                isSavingOrUpdating = false
//                isAllreadyOtpSent = false
//                MainActivity.adminDBRepo.resetStates()
//                newUserProfile = SubUserProfile("","","","",false,"","","","","","","", "","","","","","","","","")
//                MainActivity.adminDBRepo.resetMedicalAnswers()
//                navHostController.navigate(Destination.AddNewUser.routes)
//            }
//        }
//        Spacer(modifier = Modifier.width(15.dp))
//
//        Box(modifier = Modifier.weight(1f)) {
//            ActionBtn(title = "Guest User Data") {
//                isGuest = true
//                navHostController.navigate(Destination.SessionHistory.routes)
//            }
//        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterial3Api
@Composable
fun UserSearchView(navHostController: NavHostController) {

    var searchText by remember { mutableStateOf("") }

    var isEmptyResult by remember { mutableStateOf(false) }

    var isSearching by remember { mutableStateOf(false) }

    var searchResults by remember { mutableStateOf(listOf<SubUserProfile>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        SearchView(searchText = searchText, isSearching = isSearching, onValueChange = {
            searchText = it
            isEmptyResult = it.isEmpty()
            if(!isEmptyResult) isSearching = true
            searchResults = if (it.isNotEmpty()) {
                performSearch(it)
            } else {
                isSearching = false
                emptyList()
            }
        })

        Box(Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "logo",
                    alpha = .20f,
                    alignment = Alignment.Center,
                    modifier = Modifier.size(300.dp)
                )
            }

            if (searchText.isNotEmpty()) {
                searchResults = performSearch(searchText.replace(" ", ""))
                if (searchResults.isNotEmpty() || searchResults.isEmpty()) isSearching = false
                SearchResultView(searchResults = searchResults, onResultFound = {
                    isSearching = false
                }, onSelectingPatient = {
                    if (!subUserSelected) {
                        MainActivity.pc300Repo.clearSessionValues()
                        isSetRequestSent = false
                        lastFailed = false
                        isReadyForWeight = false
                        // if different user goes then reset omron sync status
                        if (MainActivity.adminDBRepo.getSelectedSubUserProfile().user_id != it.user_id) {
                            MainActivity.omronRepo.isReadyForFetch = false
                            MainActivity.subUserRepo.isResetQuestion.value = true
                        }

                        MainActivity.subUserRepo.clearSessionList()
                        MainActivity.sessionRepo.updateSessionFetch(true)
                        MainActivity.sessionRepo.updateSessionFetchStatus(null)
                        MainActivity.subUserRepo.getSessionsByUserID(userId = it.user_id)
                        MainActivity.pc300Repo.isShowEcgRealtimeAlert.value = false
                        isShown = false
                        MainActivity.adminDBRepo.setNewSubUserprofile(it.copy())
                        MainActivity.adminDBRepo.setNewSubUserprofileCopy(it.copy())
                        MainActivity.subUserRepo.isResetQuestion.value = true
                        MainActivity.subUserRepo.updateSessionState(
                            SessionStates(
                                false,
                                false,
                                false,
                                false,
                                false
                            )
                        )
                        MainActivity.subUserRepo.resetStates()
                        ifIsExitAndSave = false
                        MainActivity.subUserRepo.lastSavedSession = null
                        MainActivity.subUserRepo.createNewSession()
//                  MainActivity.localDBRepo.createNewSession()
                        navHostController.navigate(Destination.UserHome.routes)
                        isOnUserHomeScreen = true
                    }
                }) {
                    navHostController.navigate(Destination.AddNewUser.routes)
                }
            }

        }
    }
}











