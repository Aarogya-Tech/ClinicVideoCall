package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.aarogyaforworkers.aarogyaFDC.AdminDB.AdminDBRepository
import com.aarogyaforworkers.aarogyaFDC.Commons.*
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.Location.LocationRepository
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogya.R
import com.aarogyaforworkers.aarogya.composeScreens.FaceAnalyzer
import com.aarogyaforworkers.awsapi.models.AdminProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


@ExperimentalMaterial3Api
@Composable
fun AdminProfileScreen(navHostController: NavHostController, adminDBRepository: AdminDBRepository,locationRepository: LocationRepository) {
    Disableback()


    val doctor = MainActivity.adminDBRepo.adminProfileState.value


    locationRepository.getLocation(LocalContext.current)

    var isEdited by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }

    var showCamera by remember { mutableStateOf(false) }

    var isCaptured by remember { mutableStateOf(false) }

    var adminAddress = remember { mutableStateOf(doctor.location) }

    var adminSpecialization = remember { mutableStateOf(doctor.designation) }

    var capturedImage by remember {
        mutableStateOf<ByteArray?>(null)
    }

    var capturedImageBitmap by remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    val fullAddress = if(locationRepository.userLocation.value != null)"${locationRepository.userLocation.value?.address}, ${locationRepository.userLocation.value?.postalCode}" else ""

    var pc = ""
    var city = ""

    val splitAddress = fullAddress.split(",")

    if(splitAddress.size > 3){
        pc = splitAddress[3]
        city = splitAddress[0]
    }

    val address = if (city.isNotEmpty() || pc.isNotEmpty()) "$city, $pc" else ""


//    if(adminDBRepository.adminProfilePicUpdated.value != ""){
//        isLoading = false
//        lastupdateStatus = false
//        isAdminProfileUpdated = true
//        timestamp = System.currentTimeMillis().toString()
//        adminDBRepository.getProfile(MainActivity.authRepo.getAdminUID())
//    }

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

    when(MainActivity.adminDBRepo.adminProfileUpdateState.value){

        true -> {
            isEdited = false
            isCaptured = false
            isLoading = false
            lastupdateStatus = false
            isAdminProfileUpdated = true
            timestamp = System.currentTimeMillis().toString()
            adminDBRepository.getProfile(MainActivity.authRepo.getAdminUID())
            MainActivity.adminDBRepo.updateAdminProfileUpdateState(null)
        }

        false -> {
            Toast.makeText(context, "Failed to update try again", Toast.LENGTH_SHORT).show()
            MainActivity.adminDBRepo.updateAdminProfileUpdateState(null)
        }

        null -> {

        }

    }



    BackBtnAlert(navHostController)

    if(!showCamera){

        Box(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
        ) {
            Column {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp)
                    .height(55.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    IconButton(onClick = {
//                        isShowAlert = true
                        if(isAdminProfileUpdated){
                            MainActivity.adminDBRepo.getProfile(adminDBRepository.getLoggedInUser().admin_id)
                        }
                        navHostController.navigate(Destination.Home.routes)
                    }, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = ImageVector.vectorResource(id = R.drawable.back_btn_icon), contentDescription = "backBtn")
                    }
                    if (isCaptured || isEdited) {
                        // Show the save button if editing is enabled
                        TextButton(
                            onClick = {
                                isLoading = true
                                lastupdateStatus = false
                                if(capturedImage != null ){
                                    val loggedInUser = doctor.copy()
                                    loggedInUser.first_name = loggedInUser.first_name.replace("Dr.","").replace(" ", "")
                                    loggedInUser.designation = MainActivity.adminDBRepo.d_designation.value
                                    loggedInUser.location = address
//                                    loggedInUser.location = MainActivity.adminDBRepo.d_address.value
                                    MainActivity.adminDBRepo.uploadAdminProfilePic(capturedImage!!)
                                }else{
                                    val loggedInUser = doctor.copy()
                                    loggedInUser.first_name = loggedInUser.first_name.replace("Dr.","").replace(" ", "")
                                    loggedInUser.designation = MainActivity.adminDBRepo.d_designation.value
                                    loggedInUser.location = address
//                                    loggedInUser.location = MainActivity.adminDBRepo.d_address.value
                                    MainActivity.adminDBRepo.updateAdminProfilePic(loggedInUser)
                                }
                            }
                        ) {
                            Text(text = "Update",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF397EF5))
                        }
                    }
                }
                LazyColumn {
                    item{
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {

                            Box {
                                if(isCaptured) capturedImageBitmap?.let { Image(bitmap = it, contentDescription = "profilePic", modifier = Modifier
                                    .size(100.dp)
//                                    .rotate(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) 90f else 0f)
                                    .clip(CircleShape), contentScale = ContentScale.Crop,)
                                }else{
                                    if(doctor.profile_pic_url == "Not-given" || doctor.profile_pic_url.isEmpty()){
                                        Image(painter = painterResource(R.drawable.profile_icon),
                                            contentDescription = "AdminProfilePic",
                                            modifier = Modifier
                                                .size(100.dp)
                                                .clip(CircleShape))
                                    }else{
                                        LoadImage(user = doctor)
                                    }
                                }

                                FloatingActionButton(onClick = {
                                    showCamera = true
                                },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .align(Alignment.BottomEnd)) {
                                    Icon(painter = painterResource(id = R.drawable.camera_icon), contentDescription = "photoUpload")
                                }
                            }
                            Spacer(modifier = Modifier.height(25.dp))
                            AdminRegId()
                            Spacer(modifier = Modifier.height(20.dp))
                            AdminName(AdminDBRepository())
                            Spacer(modifier = Modifier.height(20.dp))
                            AdminGender(AdminDBRepository())
                            Spacer(modifier = Modifier.height(20.dp))
                            AdminEmail(AdminDBRepository())
                            Spacer(modifier = Modifier.height(20.dp))
                            AdminPhone(AdminDBRepository())
                            Spacer(modifier = Modifier.height(20.dp))
                            AdminClinicName()
                            Spacer(modifier = Modifier.height(20.dp))
                            AdminSpecialization(adminSpecialization){
                                isEdited = true
                                MainActivity.adminDBRepo.d_designation.value = it
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            AdminAddress(address)
//                            AdminAddress(adminAddress){
//                                isEdited = true
//                                MainActivity.adminDBRepo.d_address.value = it
//                            }
                            Spacer(modifier = Modifier.height(25.dp))

                            settingOptions(context)
                        }
                    }
                }
            }
        }
        if(isLoading) showProgress()
    }else{
        val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
        var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
        var preview by remember { mutableStateOf<androidx.camera.core.Preview?>(null) }
        val executor = ContextCompat.getMainExecutor(context)
        val cameraProvider = cameraProviderFuture.get()
        val lifecycleOwner = LocalLifecycleOwner.current
        Box {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    cameraProviderFuture.addListener({
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .apply {
                                setAnalyzer(executor, FaceAnalyzer())
                            }
                        imageCapture = ImageCapture.Builder()
                            .setTargetRotation(previewView.display.rotation)
                            .build()

                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build()

                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            imageCapture,
                            preview
                        )
                    }, executor)
                    preview = androidx.camera.core.Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    previewView
                }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .align(Alignment.TopStart)
            ) {
                IconButton(
                    onClick = {
                        showCamera = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "back arrow",
                        tint = Color.White
                    )
                }
            }


            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.DarkGray, RoundedCornerShape(15.dp))
                    .padding(8.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Button(
                    onClick = {
                        val imgCapture = imageCapture ?: return@Button
                        imgCapture.takePicture(executor, @ExperimentalGetImage object : ImageCapture.OnImageCapturedCallback(){
                            override fun onCaptureSuccess(image: ImageProxy) {
                                super.onCaptureSuccess(image)
                                val buffer = image.planes[0].buffer
                                val bytes = ByteArray(buffer.remaining())
                                buffer.get(bytes)
                                val byteArrayOutputStream = ByteArrayOutputStream()
                                val originalBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                val matrix = Matrix()
                                if (Build.VERSION.SDK_INT >= 30){ matrix.postRotate(90f) // Rotate the image by 90 degrees
                                }
                                val rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
                                capturedImageBitmap = rotatedBitmap.asImageBitmap()
                                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                                capturedImage = byteArrayOutputStream.toByteArray()
                                lastupdateStatus = false
                                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
//                                capturedImage = byteArrayOutputStream.toByteArray()
                                isUpdatingProfile = true
                                Handler(Looper.getMainLooper()).postDelayed({
                                    image.close()
                                    showCamera = false
                                    isCaptured = true
                                }, 1000)
                            }

                            override fun onError(exception: ImageCaptureException) {
                                super.onError(exception)
                            }
                        })

                    },
                    modifier = Modifier
                        .size(70.dp)
                        .background(Color.LightGray, CircleShape)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .border(5.dp, Color.LightGray, CircleShape),
                    colors = ButtonDefaults.buttonColors(Color.LightGray),
                ) {
                }
            }
        }
    }
}


@Composable
fun LoadAdminHomeImage(profileUrl: String, navHostController: NavHostController){
    isAdminProfileUpdated = false
    var profileUrlWithTimestamp = "$profileUrl?t=$timestamp"
    var painter = rememberImagePainter(data = profileUrlWithTimestamp)
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(painter) {
        if (painter.state is ImagePainter.State.Loading) {
            coroutineScope.launch {
                while (painter.state is ImagePainter.State.Loading) {
                    delay(10)
                }
            }
        }
    }
    Image(
        painter = painter,
        contentDescription = "Image",
        modifier = Modifier
            .size(65.dp)
            .rotate(90f)
            .clip(CircleShape)
            .clickable {
                navHostController.navigate(Destination.AdminProfile.routes)
            },
        contentScale = ContentScale.FillHeight
    )
}




@Composable
fun LoadUserHomeImage(profileUrl: String){
    var profileUrlWithTimestamp = "$profileUrl?t=$timestamp"
    var painter = rememberImagePainter(data = profileUrlWithTimestamp)
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(painter) {
        if (painter.state is ImagePainter.State.Loading) {
            coroutineScope.launch {
                while (painter.state is ImagePainter.State.Loading) {
                    delay(10)
                }
            }
        }
    }
    Image(
        painter = painter,
        contentDescription = "Image",
        modifier = Modifier
            .size(100.dp)
            .rotate(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) 90f else 0f)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun LoadSearchUserImage(profileUrl: String){
    var profileUrlWithTimestamp = "$profileUrl?t=$timestamp"
    var painter = rememberImagePainter(data = profileUrlWithTimestamp)
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(painter) {
        if (painter.state is ImagePainter.State.Loading) {
            coroutineScope.launch {
                while (painter.state is ImagePainter.State.Loading) {
                    delay(10)
                }
            }
        }
    }
    Image(
        painter = painter,
        contentDescription = "Image",
        modifier = Modifier
            .size(55.dp)
            .rotate(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) 90f else 0f)
            .clip(CircleShape),
        contentScale = ContentScale.FillHeight
    )
}

@Composable
fun LoadImage(user: AdminProfile){
    val timestamp = System.currentTimeMillis()
    val profileUrlWithTimestamp = "${user.profile_pic_url}?t=$timestamp"
    var painter = rememberImagePainter(data = profileUrlWithTimestamp)
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(painter) {
        if (painter.state is ImagePainter.State.Loading) {
            coroutineScope.launch {
                while (painter.state is ImagePainter.State.Loading) {
                    delay(50)
                }
            }
        }
    }
    Image(
        painter = painter,
        contentDescription = "Image",
        modifier = Modifier
            .size(100.dp)
//            .rotate(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) 90f else 0f)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun settingOptions(context : Context){

    val adminRepo = MainActivity.adminDBRepo

    Row(modifier = Modifier
        .fillMaxWidth()
//        .height(30.dp)
        .background(Color.LightGray), verticalAlignment = Alignment.CenterVertically){
        BoldTextView(title = "Settings", modifier = Modifier.padding(start = 10.dp, top = 5.dp, bottom = 5.dp))

    }
    Spacer(modifier = Modifier.height(10.dp))

    Column {
        settingsRow(
            text = "Temperature",
            options = listOf("°F", "°C"),
            selectedOption = adminRepo.tempUnit.value,
            onOptionSelected = {
                adminRepo.updateTempUnit(it, context)}
        )
        Spacer(modifier = Modifier.height(10.dp))

        settingsRow(
            text = "Weight",
            options = listOf("kg", "lbs"),
            selectedOption = adminRepo.weightUnit.value,
            onOptionSelected = {
                adminRepo.updateWeightUnit(it, context) }
        )
        Spacer(modifier = Modifier.height(10.dp))

        settingsRow(
            text = "Height",
            options = listOf("cm", "ft"),
            selectedOption = adminRepo.heightUnit.value,
            onOptionSelected = {
                adminRepo.updateHeightUnit(it, context) }
        )
    }
}


@Composable
fun settingsRow(
    text: String,
    options: List<String>,
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Box(modifier = Modifier.width(110.dp)) {
            BoldTextView(title = text)
        }

        options.forEachIndexed { index, option ->
            Box(
                Modifier
                    .size(22.dp),
                contentAlignment = Alignment.Center
            ) {
                RadioButton(
                    selected = selectedOption == index,
                    onClick = { onOptionSelected(index) },
                )
            }
            RegularTextView(title = option, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun AdminProfession(){
    Row(Modifier.fillMaxWidth()) {
        NonEditText(title = "I am", detail = "Doctor")
    }
}

@Composable
fun AdminRegId(){
    val doctor = MainActivity.adminDBRepo.adminProfileState.value

    Row(Modifier.fillMaxWidth()) {
        NonEditText(title = "Reg. Id:", detail = doctor.registration_id)
    }
}




@Composable
fun AdminName(adminDBRepository: AdminDBRepository){
    val doctor = MainActivity.adminDBRepo.adminProfileState.value

    Row(Modifier.fillMaxWidth()) {
        NonEditText(title = "Name:", detail = "${doctor.first_name } ${doctor.last_name}")
    }
}

@Composable
fun AdminGender(adminDBRepository: AdminDBRepository){
    val doctor = MainActivity.adminDBRepo.adminProfileState.value

    Row(
        Modifier
            .fillMaxWidth()
            .padding(end = 30.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        NonEditText(title = "Gender:", detail = doctor.gender)
        NonEditText(title = "Age:", detail = doctor.age)
    }
}

@Composable
fun AdminEmail(adminDBRepository: AdminDBRepository){
    val doctor = MainActivity.adminDBRepo.adminProfileState.value
    Row(Modifier.fillMaxWidth()) {
        NonEditText(title = "Email:", detail = doctor.email)
    }
}

@Composable
fun AdminPhone(adminDBRepository: AdminDBRepository){
    val doctor = MainActivity.adminDBRepo.adminProfileState.value

    Row(Modifier.fillMaxWidth()) {
        NonEditText(title = "Phone:", detail = if(doctor.phone.isNotEmpty()) "+${doctor.phone}" else "")
    }
}

@Composable
fun AdminClinicName(){
    val doctor = MainActivity.adminDBRepo.adminProfileState.value
    Row(Modifier.fillMaxWidth()) {
        NonEditText(title = "Clinic:", detail = doctor.hospitalName.ifEmpty { "Dr. ${doctor.first_name} Clinic" })
    }
}


@Composable
fun AdminSpecialization(adminSpecialization: MutableState<String>, onChange : (String) -> Unit){
    Row(Modifier.fillMaxWidth()) {
        Box(Modifier.width(75.dp)) {
            BoldTextView(title = "Specialization:")
        }
        TwoLineTextField(input = adminSpecialization.value,
            onChangeInput = {
                adminSpecialization.value = it
                onChange(it)
            },
            keyboardType = KeyboardType.Text,
            placeholderText = "Enter Specialization")
    }
}

@Composable
fun AdminAddress(address: String){

    Row(Modifier.fillMaxWidth()) {
        NonEditText(title = "Address:", detail = address.ifEmpty { "" })
    }

//       adminDBRepository.getLoggedInUser().location,
}

@Composable
fun BackBtnAlert(navHostController: NavHostController){
    AlertView(
        showAlert = isShowAlert
        ,title = "Are you sure?",
        subTitle = "Do you really want to close it?",
        subTitle1 = "",
        onYesClick = {
            navHostController.navigate(Destination.Home.routes)
            isShowAlert = false
        },
        onNoClick = {
            isShowAlert = false
        }
    ){
        isShowAlert = false
    }
}



