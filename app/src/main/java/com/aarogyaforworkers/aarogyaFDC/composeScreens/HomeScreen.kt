@file:OptIn(ExperimentalMaterial3Api::class)

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
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.filled.VideoChat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.aarogyaforworkers.aarogyaFDC.FirebaseMessagingService
import com.aarogyaforworkers.aarogyaFDC.R
import com.aarogyaforworkers.aarogyaFDC.VideoConferencing
import com.aarogyaforworkers.aarogyaFDC.storage.ProfilePreferenceManager
import com.aarogyaforworkers.aarogyaFDC.ui.theme.defCardDark
import com.aarogyaforworkers.aarogyaFDC.ui.theme.defDark
import com.aarogyaforworkers.aarogyaFDC.ui.theme.defLight
import com.aarogyaforworkers.aarogyaFDC.ui.theme.logoOrangeColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

var lastUpdatedSignOutValue = false
var isAdminHomeScreenSetUp = false
var subUserSelected = false

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(navHostController: NavHostController, authRepository: AuthRepository, adminRepository : AdminDBRepository, pc300Repository: PC300Repository, locationRepository: LocationRepository) {

    val context = LocalContext.current

    MainActivity.firebaseRepo.getToken(context = context)

    Disableback()

    when(MainActivity.adminDBRepo.adminProfileSyncedState.value){

        true -> {
            MainActivity.adminDBRepo.updateAdminProfileSyncedState(null)
        }

        false -> {
            MainActivity.adminDBRepo.updateAdminProfileSyncedState(null)
        }

        null -> {

        }

    }

    CheckInternet(context = LocalContext.current)

    isOnUserHomeScreen = false

    var isClickedOnSearch = remember { mutableStateOf(false) }

    var focusRequester = remember { FocusRequester() }

    val bleEnabled by remember { mutableStateOf(isBluetoothEnabled()) }

    if(!bleEnabled) checkBluetooth(context)

    if(MainActivity.adminDBRepo.getLoggedInUser().groupid.isNotEmpty()){
        MainActivity.adminDBRepo.getTotalRegistrationCountsByGroupId(MainActivity.adminDBRepo.getLoggedInUser().groupid)
    }

    pc300Repository.isOnSessionPage = false

    MainActivity.shared.initializeOmronPC300(LocalContext.current)

    MainActivity.csvRepository.setUpNewContext(LocalContext.current)

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .testTag(HomePageTags.shared.homeScreen)
    ) {
        Column(Modifier.padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(25.dp))
            ProfileView(navHostController)
            Spacer(modifier = Modifier.height(25.dp))
            Column(Modifier.weight(1f)) {
                UserSearchView(
                    navHostController = navHostController,
                    focusRequester = focusRequester,
                    isClickedOnSearch = isClickedOnSearch
                ){
                    isClickedOnSearch.value = true
                }
            }
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

//    if(adminRepository.getLoggedInUser().admin_id.isEmpty()){
//        adminRepository.getProfile(authRepository.getAdminUID())
//    }
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
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            DoctorImageView(imageUrl = profile.profile_pic_url, size = 40.dp){
                navHostController.navigate(Destination.AdminProfile.routes)
            }
        }

        Spacer(modifier = Modifier.width(15.dp))

        val heyGreeting = stringResource(id = R.string.hey_greeting)

        Box(modifier = Modifier
            .weight(1f)
            .testTag(HomePageTags.shared.getAdminTag(profile))){
            Column() {
                TitleView(title = "$heyGreeting, "+MainActivity.adminDBRepo.adminProfileState.value.first_name + " ")
                Spacer(modifier = Modifier.height(4.dp))
                RegularTextView(title = "View Patients", modifier = Modifier.clickable {
                    MainActivity.adminDBRepo.isSearching.value = true
                    MainActivity.adminDBRepo.getAllPatientsOfTheDoctor()
                    navHostController.navigate(Destination.PatientList.routes) }, textColor = defLight)
            }
        }

        Box(
            Modifier
                .size(44.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFFFD4B6), shape = CircleShape),
                contentAlignment = Alignment.Center) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    IconButton(onClick = {
                        val doctor = MainActivity.adminDBRepo.adminProfileState.value
                        MainActivity.adminDBRepo.getGroupMembersList(doctor.admin_id)
                        navHostController.navigate(Destination.VideoCallingLobbyScreen.routes)
                    }) {
                        Icon(imageVector = Icons.Default.VideoCall, contentDescription = "video call", Modifier.size(44.dp),
                            tint = defDark )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

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
            MainActivity.firebaseRepo.deleteToken()
            MainActivity.authRepo.signOut()
        }) {
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
            .fillMaxWidth(),
    ) {
        PopUpBtnSingle(btnName = "Create New Patient", {
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
            Modifier.fillMaxWidth(),
            containerColor = logoOrangeColor,
            contentPadding = PaddingValues(vertical = 16.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterial3Api
@Composable
fun UserSearchView(navHostController: NavHostController, focusRequester: FocusRequester, isClickedOnSearch: MutableState<Boolean>, onFocusChange: () -> Unit) {

    var searchText by remember { mutableStateOf("") }

    var isEmptyResult by remember { mutableStateOf(false) }

    var isSearching by remember { mutableStateOf(false) }

    var searchResults by remember { mutableStateOf(listOf<SubUserProfile>()) }


        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Box(
                Modifier
                    .background( if(isClickedOnSearch.value) Color.Transparent else logoOrangeColor.copy(alpha = .9f), shape = RoundedCornerShape(15.dp))

            ) {
                Column(Modifier.padding(horizontal =  if(isClickedOnSearch.value) 0.dp else 20.dp, vertical = if(isClickedOnSearch.value) 0.dp else 60.dp)) {

                    if(!isClickedOnSearch.value){
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            Icon(imageVector = ImageVector.vectorResource(id = R.drawable.solar_health_linear), contentDescription = "", Modifier.size(48.dp), tint = Color.White)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            BoldTextView(title = "Aarogya Clinic", fontSize = 24, textColor = Color.White)
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                    }

                    SearchView(searchText = searchText,
                        isSearching = isSearching,
                        onValueChange = {
                            searchText = it
                            isEmptyResult = it.isEmpty()
                            if(!isEmptyResult) isSearching = true
                            searchResults = if (it.isNotEmpty()) {
                                performSearch(it)
                            } else {
                                isSearching = false
                                emptyList()
                            }

                        }, focusRequester = focusRequester, onFocusChange =  {
                        onFocusChange()
                    }, color = if (isClickedOnSearch.value) defCardDark else Color.White)
                }
            }





//        SearchView(searchText = searchText, isSearching = isSearching, onValueChange = {
//            searchText = it
//            isEmptyResult = it.isEmpty()
//            if(!isEmptyResult) isSearching = true
//            searchResults = if (it.isNotEmpty()) {
//                performSearch(it)
//            } else {
//                isSearching = false
//                emptyList()
//            }
//        })

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

                        timestamp = System.currentTimeMillis().toString()
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



@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun previewHM(){
    HomeScreen(
        navHostController = rememberNavController(),
        authRepository = AuthRepository(),
        adminRepository = AdminDBRepository(),
        pc300Repository = PC300Repository(),
        locationRepository = LocationRepository()
    )
}








