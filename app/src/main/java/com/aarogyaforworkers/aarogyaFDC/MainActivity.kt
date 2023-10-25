@file:OptIn(ExperimentalTvMaterial3Api::class, ExperimentalTvMaterial3Api::class)

package com.aarogyaforworkers.aarogyaFDC
import android.Manifest
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PictureInPictureParams
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.aarogyaforworkers.aarogya.composeScreens.CameraScreen
import com.aarogyaforworkers.aarogya.composeScreens.VitalCollectionScreen
import com.aarogyaforworkers.aarogyaFDC.AdminDB.AdminDBRepository
import com.aarogyaforworkers.aarogyaFDC.Auth.AuthRepository
import com.aarogyaforworkers.aarogyaFDC.Camera.CameraRepository
import com.aarogyaforworkers.aarogyaFDC.Commons.selectedEcg
import com.aarogyaforworkers.aarogyaFDC.CsvGenerator.CsvRepository
import com.aarogyaforworkers.aarogyaFDC.Location.LocationRepository
import com.aarogyaforworkers.aarogyaFDC.MediaPlayer.PlayerRepo
import com.aarogyaforworkers.aarogyaFDC.Omron.OmronRepository
import com.aarogyaforworkers.aarogyaFDC.PC300.PC300Repository
import com.aarogyaforworkers.aarogyaFDC.PatientSession.PatientSessionManagerRepo
import com.aarogyaforworkers.aarogyaFDC.S3.S3Repository
import com.aarogyaforworkers.aarogyaFDC.Session.SessionStatusRepo
import com.aarogyaforworkers.aarogyaFDC.SubUser.SubUserDBRepository
import com.aarogyaforworkers.aarogyaFDC.Tracky.TrackyManager
import com.aarogyaforworkers.aarogyaFDC.composeScreens.AddNewUserScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.AdminProfileScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.ConfirmAdminSignInScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.DateAndTimePickerScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.DevicesConnectionScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.EditCalanderScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.EditTextScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.ForgotPasswordScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.GraphScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.GroupVideoCallingScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.HomeScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.ImagePainter
import com.aarogyaforworkers.aarogyaFDC.composeScreens.ImagePreviewScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.ImpressionPlanScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.LaboratoryRadioLogyScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.LoginScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.NearByDeviceListScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.PasswordResetScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.PastMedicalSurgicalHistoryScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.PatientList
import com.aarogyaforworkers.aarogyaFDC.composeScreens.PhysicalExaminationScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.RadioButtonHistoryScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.SavedImagePreviewScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.SavedImagePreviewScreen2
import com.aarogyaforworkers.aarogyaFDC.composeScreens.SessionSummaryScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.SetCalanderScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.SplashScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.UserHomeScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.UserSessionHistoryScreen
import com.aarogyaforworkers.aarogyaFDC.composeScreens.VideoCallingLobbyScreen
import com.aarogyaforworkers.aarogyaFDC.storage.Local.LocalSessionDBManager
import com.aarogyaforworkers.aarogyaFDC.ui.theme.AarogyaTheme
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1

private const val REQUEST_PERMISSIONS = 1003

sealed class Destination(val routes : String){
    object Splash: Destination("Splash")
    object Home: Destination("Home")
    object Login : Destination("Login")
    object ForgotPasswordScreen : Destination("ForgotPassword")
    object UserHome : Destination("UserHome")
    object PasswordReset : Destination("PasswordReset")
    object ConfirmAdminSignIn : Destination("ConfirmAdminSignIn")
    object AddNewUser : Destination("AddNewUser")
    object AdminProfile : Destination("AdminProfile")
    object Camera : Destination("Camera")
    object DeviceList : Destination("DeviceList")
    object SessionHistory : Destination("Session")
    object Graphs : Destination("Graphs")
    object DeviceConnection : Destination("DeviceConnection")
    object SessionSummary : Destination("SessionSummary")
    object EditTextScreen : Destination("EditTextScreen/{title}")
    object RadioButtonHistoryScreen : Destination("RadioButtonHistoryScreen/{title}")
    object VitalCollectionScreen: Destination("VitalCollectionScreen")
    object PhysicalExaminationScreen: Destination("PhysicalExaminationScreen")
    object LaboratoryRadiologyScreen: Destination("LaboratoryRadiology")
    object ImpressionPlanScreen: Destination("ImpressionPlan")
    object ImagePreviewScreen: Destination("ImagePreviewScreen")
    object SavedImagePreviewScreen: Destination("SavedImagePreview")
    object SavedImagePreviewScreen2: Destination("SavedImagePreview2")
    object PastMedicalSurgicalHistoryScreen: Destination("PastMedicalSurgicalHistoryScreen")
    object PatientList: Destination("PatientList")
    object ImagePainter: Destination("ImagePainter")
    object VideoCallingLobbyScreen: Destination("VideoCallingLobbyScreen")
    object DateAndTimePickerScree: Destination("DateAndTimePickerScreen")
    object EditCalanderScreen: Destination("EditCalanderScreen")
    object SetCalanderScreen: Destination("SetCalanderScreen")
    object GroupVideoCallingScreen: Destination("GroupVideoCallingScreen")

}

class MainActivity : ComponentActivity(){

    companion object{
        val shared = MainActivity()
        var authRepo : AuthRepository = AuthRepository.getInstance()
        var trackyRepo : TrackyManager = TrackyManager.getInstance()
        var adminDBRepo : AdminDBRepository = AdminDBRepository.getInstance()
        var cameraRepo : CameraRepository = CameraRepository()
        var locationRepo : LocationRepository = LocationRepository.getInstance()
        var pc300Repo : PC300Repository = PC300Repository.getInstance()
        var omronRepo : OmronRepository = OmronRepository.getInstance()
        var subUserRepo : SubUserDBRepository = SubUserDBRepository.getInstance()
        var csvRepository : CsvRepository = CsvRepository.getInstance()
        var s3Repo : S3Repository = S3Repository()
        var sessionStatusRepo : SessionStatusRepo = SessionStatusRepo()
        var playerRepo : PlayerRepo = PlayerRepo.getInstance()
        var localDBRepo : LocalSessionDBManager = LocalSessionDBManager.getInstance()
        var sessionRepo : PatientSessionManagerRepo = PatientSessionManagerRepo.getInstance()
        var zegoCloudViewModel:ZegoCloudViewModel=ZegoCloudViewModel.getInstance()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val PERMISSIONS = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.RECORD_AUDIO,
        BLUETOOTH_SCAN,
        BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH_ADVERTISE,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.POST_NOTIFICATIONS
    )
    private val PERMISSION_REQUEST_CODE = 123

    private var permissionIndex = 0

    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)
    private lateinit var photoUri: Uri
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)

    fun initializeOmronPC300(context: Context){
        pc300Repo.initializePC300(context)
        adminDBRepo.initializeAPIManager()
        omronRepo.register(context)
        csvRepository.setUpContext(this)
    }

    // Call this function to start the permission request process
    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestPermissionsForLatest() {
        permissionIndex = 0
        requestNextPermission()
        csvRepository.setUpContext(this)
        csvRepository.checkECGDirectory(this)
    }


    private fun requestPermissionsForOlder() {
        requestPermissions(arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        ), REQUEST_PERMISSIONS)
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("tag", "requestLocationPermission: ")
        }
    }



    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestNextPermission() {
        if (permissionIndex < PERMISSIONS.size) {
            val permission = PERMISSIONS[permissionIndex]
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
            } else {
                permissionIndex++
                requestNextPermission()
            }
        }else{
            csvRepository.checkECGDirectory(this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkDirectory(){
        MainActivity.csvRepository.checkECGDirectory(this)
    }

    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionIndex++
                requestNextPermission()
            } else {
                // Permission denied
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AarogyaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        requestPermissionsForLatest()
                    }else{
                        requestPermissionsForOlder()
                    }

                    FirebaseMessagingService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

//                    FirebaseMessagingService.sendCurrentToken(applicationContext)

//                    if(FirebaseMessagingService.isfromnotification==true)
//                    {
//                        val intent = Intent(this, VideoConferencing::class.java)
//                        startActivity(intent)
//                    }


//                    if(intent.action=="android.intent.action.NOTIFICATION_CLICKED")
//                    {
//                        val intent1 = Intent(this, VideoConferencing::class.java)
//                        startActivity(intent1)
//                    }

                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }

                        Log.i("TAG", "FCM token = ${task.result}")
                        FirebaseMessagingService.token=task.result

                    })
//                    d7b91ggkRFi0MUqDhCtdPx:APA91bExNB5uHFaigxvQfKzBGKbpWDTNJkUY-9U_Y0WpDrVCJZUp9JhQdw4hime5_Xsr7AHOoOPuiABn6AeWBGV_osOVOfalqKbR22zSh0UR6y9pWNDBliP17DCOQIc6Qu_4kGNLnv-1

//                    zegoCloudViewModel.application=application
//                    zegoCloudViewModel.sp = getSharedPreferences("offline", Context.MODE_PRIVATE)
//                    zegoCloudViewModel.sp.edit().clear().apply()
//                    zegoCloudViewModel.userId= zegoCloudViewModel.getUserID()!!
//                    zegoCloudViewModel.username= zegoCloudViewModel.getUserName()!!
//                    if(zegoCloudViewModel.userId!="")
//                        zegoCloudViewModel.initCallInviteService()
                    val navController = rememberNavController()
//                    zegoCloudViewModel.navHostController=navController
                    NavigationAppHost(navController = navController)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        zegoCloudViewModel.unInitCallInviteService()
    }

    private fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        context.createConfigurationContext(configuration)
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
    }


    override fun onResume() {
        super.onResume()
        initializeRepository()
        localDBRepo.setDBDao(this)
        FirebaseAnalytics.getInstance(this);
    }

    fun initializeRepository() = authRepo.initializeAmplify(this)

}

// Function to check if Bluetooth is enabled
fun isBluetoothEnabled(): Boolean {
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    return bluetoothAdapter?.isEnabled ?: false
}

@SuppressLint("MissingPermission")
fun turnOn(context: Context) {
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        val activity = context as? Activity
        activity?.startActivityForResult(enableBtIntent, 0)
    }
}

fun checkBluetooth(context: Context){
    if(!isBluetoothEnabled()) turnOn(context)
}



@ExperimentalTvMaterial3Api
@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterial3Api
@Composable
fun NavigationAppHost(navController: NavHostController){
      NavHost(navController = navController, startDestination = Destination.Splash.routes){
          composable (Destination.Splash.routes) { SplashScreen (navController, MainActivity.authRepo) }
          composable (Destination.Home.routes) { HomeScreen (navController, MainActivity.authRepo, MainActivity.adminDBRepo, MainActivity.pc300Repo, MainActivity.locationRepo) }
          composable(Destination.DeviceConnection.routes) { DevicesConnectionScreen(navController, MainActivity.pc300Repo, MainActivity.omronRepo)}
          composable(Destination.Login.routes) { LoginScreen(navController, MainActivity.authRepo) }
          composable(Destination.ForgotPasswordScreen.routes) { ForgotPasswordScreen(navController, MainActivity.authRepo)}
          composable(Destination.UserHome.routes) { UserHomeScreen(navController, MainActivity.adminDBRepo, MainActivity.pc300Repo, MainActivity.locationRepo, MainActivity.subUserRepo, MainActivity.s3Repo, MainActivity.csvRepository)}
          composable(Destination.ConfirmAdminSignIn.routes) { ConfirmAdminSignInScreen(navController)}
          composable(Destination.PasswordReset.routes) { PasswordResetScreen(navController,MainActivity.authRepo)}
          composable(Destination.AddNewUser.routes) { AddNewUserScreen(navController, MainActivity.adminDBRepo, MainActivity.cameraRepo, MainActivity.locationRepo, MainActivity.subUserRepo)}
          composable(Destination.AdminProfile.routes) { AdminProfileScreen(navController, MainActivity.adminDBRepo, MainActivity.locationRepo) }
          composable(Destination.Camera.routes) { CameraScreen(MainActivity.cameraRepo, navController)}
          composable(Destination.SessionSummary.routes) { SessionSummaryScreen(navController) }
          composable(Destination.Graphs.routes) { GraphScreen(navHostController = navController, selectedEcg) }
          composable(Destination.DeviceList.routes) { NearByDeviceListScreen(navHostController = navController, MainActivity.pc300Repo, MainActivity.omronRepo)}
          composable(Destination.SessionHistory.routes) { UserSessionHistoryScreen(navHostController = navController, subUserDBRepository = MainActivity.subUserRepo, MainActivity.adminDBRepo)}
          composable(Destination.EditTextScreen.routes + "/{title}" + "/{textToShow}") { navBackStack ->
              val title = navBackStack.arguments?.getString("title")
              val textToSho = navBackStack.arguments?.getString("textToShow")
              if (title != null) {
                  if (textToSho != null) {
                      EditTextScreen(navHostController = navController,title = title, textToShow = textToSho)
                  }
              }
          }
          composable(Destination.VitalCollectionScreen.routes){VitalCollectionScreen(navHostController = navController) }
          composable(Destination.RadioButtonHistoryScreen.routes + "/{title}" + "/{textToShow}") { navBackStack ->
              val title = navBackStack.arguments?.getString("title")
              val textToSho = navBackStack.arguments?.getString("textToShow")
              val type = navBackStack.arguments?.getString("type")
              if (title != null) {
                  if (textToSho != null) {
                      RadioButtonHistoryScreen(navHostController = navController,title = title, textToShow = textToSho)
                  }
              }
          }
          composable(Destination.PhysicalExaminationScreen.routes){ PhysicalExaminationScreen(navHostController = navController)}
          composable(Destination.LaboratoryRadiologyScreen.routes){ LaboratoryRadioLogyScreen(navHostController = navController) }
          composable(Destination.ImpressionPlanScreen.routes){ ImpressionPlanScreen(navHostController = navController) }
          composable(Destination.ImagePreviewScreen.routes){ ImagePreviewScreen(cameraRepository = MainActivity.cameraRepo, navHostController = navController)}
          composable(Destination.SavedImagePreviewScreen.routes){ SavedImagePreviewScreen(navHostController = navController, cameraRepository = MainActivity.cameraRepo) }
          composable(Destination.PastMedicalSurgicalHistoryScreen.routes){ PastMedicalSurgicalHistoryScreen(navHostController = navController) }
          composable(Destination.SavedImagePreviewScreen2.routes){ SavedImagePreviewScreen2(navHostController = navController, cameraRepository = MainActivity.cameraRepo) }
          composable(Destination.PatientList.routes){ PatientList(navHostController = navController)}
          composable(Destination.ImagePainter.routes){ ImagePainter(capturedImageBitmap = CameraRepository.getInstance().capturedImageBitmap) }
          composable(Destination.VideoCallingLobbyScreen.routes){ VideoCallingLobbyScreen(navHostController=navController) }
          composable(Destination.DateAndTimePickerScree.routes){ DateAndTimePickerScreen(navHostController = navController)}
          composable(Destination.EditCalanderScreen.routes){ EditCalanderScreen(navHostController = navController)}
          composable(Destination.SetCalanderScreen.routes){ SetCalanderScreen(navHostController = navController)}
          composable(Destination.GroupVideoCallingScreen.routes){ GroupVideoCallingScreen(navHostController = navController ) }
      }
}






