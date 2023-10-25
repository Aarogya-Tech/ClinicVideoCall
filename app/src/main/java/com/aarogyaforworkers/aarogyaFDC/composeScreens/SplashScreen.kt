package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogyaFDC.Auth.AuthRepository
import com.aarogyaforworkers.aarogyaFDC.Commons.*
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.FirebaseMessagingService
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.VideoConferencing
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.TimerTask


var isProfileRequested = mutableStateOf(false)

@Composable
fun SplashScreen(navHostController: NavHostController, repository: AuthRepository) {

    val context = LocalContext.current

    if(isProfileRequested.value){

        when(MainActivity.adminDBRepo.adminProfileSyncedState.value){

            true -> {
                showProgress()
                Log.d("TAG", "SplashScreen: is synced")
                timestamp = System.currentTimeMillis().toString()
                timestampd = System.currentTimeMillis().toString()
                val loggedInUser = MainActivity.adminDBRepo.getLoggedInUser()
                MainActivity.adminDBRepo.d_address.value = loggedInUser.location
                MainActivity.adminDBRepo.d_designation.value = loggedInUser.designation
                navHostController.navigate(Destination.Home.routes)
                isProfileRequested.value = false
                MainActivity.adminDBRepo.updateAdminProfileSyncedState(null)

            }

            false -> {
                MainActivity.adminDBRepo.updateAdminProfileSyncedState(null)
            }

            null -> {

            }

        }
    }

    SideEffect {
        (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    val timer = Timer()
    startTimer(timer = timer, repository = repository, navHostController)
    val updatedValue = repository.userSignInState.value
    splashLogo()
    when(repository.userSignInState.value){
        true -> {
            if(lastUpdatedSignInValue != updatedValue){
                if(isSplashScreenSetup) stopTimer(timer)
                lastUpdatedSignInValue = updatedValue
                showProgress()
                isProfileRequested.value = true
                Log.d("TAG", "SplashScreen: is requesting")

                if(FirebaseMessagingService.isfromnotification==true)
                {
                    LaunchedEffect(key1 = true){
                        CoroutineScope(Dispatchers.Main).launch {

                            delay(1000)

                            val intent = Intent(context, VideoConferencing::class.java)
                            context.startActivity(intent)
                        }
                    }
                }

                MainActivity.adminDBRepo.getProfile(MainActivity.authRepo.getAdminUID())
            }
            if(!isSplashScreenSetup) isSplashScreenSetup = true
        }
        false -> {
            if(lastUpdatedSignInValue != updatedValue){
                if(isSplashScreenSetup) stopTimer(timer)
                lastUpdatedSignInValue = updatedValue
                navigateToLogin(navHostController = navHostController)
            }
            if(!isSplashScreenSetup) isSplashScreenSetup = true
        }
    }
}

fun stopTimer(timer: Timer){
    isTimerStopped = true
    timer.cancel()
}

fun startTimer(timer: Timer, repository: AuthRepository, navHostController: NavHostController) {
    var splashTime = 10
    timer.apply {
        scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if(splashTime - 1 == 0){
                    CoroutineScope(Dispatchers.Main).launch {
                        if (!isTimerStopped) {
                            navHostController.navigate(Destination.Login.routes)
                        }
                        withContext(Dispatchers.Default) {
                            if(!isTimerStopped)stopTimer(timer)
                        }
                    }
                } else if(splashTime == 2){
                    splashTime -= 1
                    repository.isUserSignedIn()
                }else{
                    splashTime -= 1
                }

            }
        }, 0, 1000)
    }
}
