package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aarogyaforworkers.aarogya.R
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import java.util.Locale

var isDoneClick = false

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTextScreen(navHostController: NavHostController,title:String,textToShow : String) {
    Disableback()


    val isSaving = remember {
        mutableStateOf(false)
    }

    val user = MainActivity.adminDBRepo.getSelectedSubUserProfile().copy()

    when(MainActivity.adminDBRepo.subUserProfileCreateUpdateState.value){
        true -> {
            isSaving.value = false
            //navHostController.navigate(Destination.UserHome.routes)
            MainActivity.adminDBRepo.searchUserByQuery(user.first_name.toCharArray().first().toString())
            MainActivity.adminDBRepo.updateSubUserProfileCreateUpdateState(false)
            MainActivity.subUserRepo.updateIsAnyUpdateThere(false)
            if(isDoneClick){
                navHostController.popBackStack()
            }
        }
        false -> {

        }
    }

    val textToSho = textToShow.split(":")

    var text= remember {
        mutableStateOf(textToSho.first())
    }

    var isEditable= remember {
        mutableStateOf(false)
    }

    var onDonePressed= remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(top = 15.dp),
                title = {
                    BoldTextView(title, fontSize = 20)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if(MainActivity.subUserRepo.anyUpdateThere.value)
                            onDonePressed.value=true
                        else
                            navHostController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Filled.ArrowBack,contentDescription = "Back Button")
                    }
                },
                actions={
                    Box(modifier = Modifier
                        .padding(end = 30.dp), contentAlignment = Alignment.CenterEnd) {
                        IconButton(
                            onClick = {
                                //save btn click
                                isEditable.value=false
                                if(textToShow!=text.value)
                                {
                                    val type = textToSho.last()
                                    when (type) {
                                        "0" -> user.chiefComplaint = text.value
                                        "1" -> user.HPI_presentIllness = text.value
                                        "2" -> user.FamilyHistory = text.value
                                        "3" -> user.SocialHistory = text.value
                                        "4" -> user.PastMedicalSurgicalHistory = text.value
                                        "5" -> user.Medication = text.value
                                        else -> ""
                                    }
                                    MainActivity.adminDBRepo.adminUpdateSubUser(user = user)
                                }
                                MainActivity.adminDBRepo.setNewSubUserprofile(user.copy())
                                MainActivity.adminDBRepo.setNewSubUserprofileCopy(user.copy())
                                isSaving.value = true
//                    if(!isEditable.value)
//                        MainActivity.subUserRepo.updateEditTextEnable(true)
                            },
                            modifier = Modifier
                                .size(30.dp) // Adjust the size of the circular border
                                .border(
                                    width = 2.dp, // Adjust the border width
                                    color = Color.Black, // Change the border color when in edit mode
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.floppy_disk),
                                contentDescription = "SaveBtn", Modifier.size(30.dp)
                            )
                        }
//                        IconButton(
//                            onClick = {
//                                if(!isEditable.value)
//                                    isEditable.value=true
//                            },
//                            modifier = Modifier
//                                .size(30.dp) // Adjust the size of the circular border
//                                .border(
//                                    width = 2.dp, // Adjust the border width
//                                    color = if (!isEditable.value) Color.Gray else Color.Black, // Change the border color when in edit mode
//                                    shape = CircleShape
//                                )
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Edit,
//                                contentDescription = "Edit Text",
//                                tint = if (!isEditable.value) Color.Gray else Color.Black
//                            )
//                        }
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                PopUpBtnSingle(btnName = "Done", onBtnClick = {
                    //Done btn click
                    isDoneClick = true

                    isEditable.value=false
                    if(textToShow!=text.value)
                    {
                        val type = textToSho.last()
                        when (type) {
                            "0" -> user.chiefComplaint = text.value
                            "1" -> user.HPI_presentIllness = text.value
                            "2" -> user.FamilyHistory = text.value
                            "3" -> user.SocialHistory = text.value
                            "4" -> user.PastMedicalSurgicalHistory = text.value
                            "5" -> user.Medication = text.value
                            else -> ""
                        }
                        MainActivity.adminDBRepo.adminUpdateSubUser(user = user)
                    }
                    MainActivity.adminDBRepo.setNewSubUserprofile(user.copy())
                    MainActivity.adminDBRepo.setNewSubUserprofileCopy(user.copy())
                    isSaving.value = true

                }, Modifier.fillMaxWidth())
//                PopBtnDouble(
//                    btnName1 = "Save",
//                    btnName2 = "Done",
//                    onBtnClick1 = {
//                        //save btn click
//                        isEditable.value=false
//                        if(textToShow!=text.value)
//                        {
//                            val type = textToSho.last()
//                            when (type) {
//                                "0" -> user.chiefComplaint = text.value
//                                "1" -> user.HPI_presentIllness = text.value
//                                "2" -> user.FamilyHistory = text.value
//                                "3" -> user.SocialHistory = text.value
//                                "4" -> user.PastMedicalSurgicalHistory = text.value
//                                "5" -> user.Medication = text.value
//                                else -> ""
//                            }
//                            MainActivity.adminDBRepo.adminUpdateSubUser(user = user)
//                        }
//                        MainActivity.adminDBRepo.setNewSubUserprofile(user.copy())
//                        MainActivity.adminDBRepo.setNewSubUserprofileCopy(user.copy())
//                        isSaving.value = true
//                    },
//                    onBtnClick2 = {
//                        //done btn click
//                        if(isEditable.value)
//                            onDonePressed.value=true
//                        else
//                            navHostController.popBackStack()
//                    },
//                    enable = isEditable.value
//                )
            }
        }
    )
    { innerPadding ->
        Column(modifier= Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            LazyColumn(
                Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    if(onDonePressed.value)
                    {
                        AlertView(
                            showAlert = true,
                            title = "Do you want to go back?",
                            subTitle = "You have unsaved changes.Your changes will be discarded if you press Yes.",
                            subTitle1 = "",
                            onYesClick = { navHostController.popBackStack()  },
                            onNoClick = { onDonePressed.value=false },
                        ) {
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(300.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {

                        val speechIntentLauncher = rememberLauncherForActivityResult(
                            ActivityResultContracts.StartActivityForResult()) { result ->
                            if (result.resultCode == ComponentActivity.RESULT_OK && result.data != null) {
                                val resultData = result.data
                                val resultText = resultData?.getStringArrayListExtra(
                                    RecognizerIntent.EXTRA_RESULTS)
                                val recognizedText = resultText?.get(0)
                                recognizedText?.let {
                                    text.value = text.value + " " + it
                                }
                            }
                        }

                        OutlinedTextField(
                            value = text.value,
                            onValueChange = { newText ->
                                text.value = newText
                                MainActivity.subUserRepo.updateIsAnyUpdateThere(true)
                            },
                            modifier = Modifier.fillMaxSize(),
                            textStyle = TextStyle(fontSize = 16.sp, fontFamily = FontFamily(Font(R.font.roboto_regular))),
                            placeholder = { RegularTextView(title = "Please Enter Details", fontSize = 16, textColor = Color.Gray) },
                            colors = TextFieldDefaults.textFieldColors(containerColor = Color(0xffdae3f3))
                        )
                        IconButton(
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
                            modifier = Modifier.padding(bottom = 4.dp),
                        ) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = "", modifier = Modifier.size(25.dp))
                        }
                    }
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 16.dp)
//                            .height(300.dp),
//                        contentAlignment = Alignment.BottomEnd
//                    ) {
//                        OutlinedTextField(
//                            value = text.value,
//                            onValueChange = { newText ->
//                                text.value = newText
//                            },
//                            modifier = Modifier.fillMaxSize(),
//                            textStyle = TextStyle(fontSize = 16.sp, fontFamily = FontFamily(Font(R.font.roboto_regular))),
//                            placeholder = { RegularTextView(title = "Please Enter Details", fontSize = 16) },
//                            enabled = isEditable.value,
//                            colors = TextFieldDefaults.textFieldColors(containerColor = Color(0xffdae3f3))
//                        )
//                            IconButton(
//                                onClick = { /*TODO*/ },
//                                modifier = Modifier.padding(bottom = 4.dp),
//                                enabled = isEditable.value,
//                            ) {
//                                Icon(imageVector = Icons.Default.Mic, contentDescription = "", modifier = Modifier.size(25.dp))
//                            }
//                        }
                }
            }
        }

        if(isSaving.value) showProgress()
    }
}


//@Preview(showSystemUi = true)
//@Composable
//fun previewScreen()
//{
//    EditTextScreen(navHostController = rememberNavController(), title = "asfawfawef", textToShow = "fsfawefawef")
//}
