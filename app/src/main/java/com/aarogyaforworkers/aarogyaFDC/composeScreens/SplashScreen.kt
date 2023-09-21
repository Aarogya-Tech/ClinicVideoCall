package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogyaFDC.Auth.AuthRepository
import com.aarogyaforworkers.aarogyaFDC.Commons.*
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.TimerTask


@Composable
fun SplashScreen(navHostController: NavHostController, repository: AuthRepository) {
    val context = LocalContext.current

    when(MainActivity.adminDBRepo.adminProfileSyncedState.value){

        true -> {
            showProgress()
            navHostController.navigate(Destination.Home.routes)
            MainActivity.adminDBRepo.updateAdminProfileSyncedState(null)
        }

        false -> {
            MainActivity.adminDBRepo.updateAdminProfileSyncedState(null)
        }

        null -> {

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
                MainActivity.adminDBRepo.getProfile(MainActivity.authRepo.getAdminUID())
//                navigateToHome(navHostController = navHostController)
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
                } else if(splashTime == 6){
                    splashTime -= 1
                    repository.isUserSignedIn()
                }else{
                    splashTime -= 1
                }

            }
        }, 0, 1000)
    }
}
