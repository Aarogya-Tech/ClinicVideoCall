package com.aarogyaforworkers.aarogyaFDC.composeScreens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTextScreen(navHostController: NavHostController,title:String,textToShow : String) {

    val isSaving = remember {
        mutableStateOf(false)
    }

    val user = MainActivity.adminDBRepo.getSelectedSubUserProfile().copy()

    when(MainActivity.adminDBRepo.subUserProfileCreateUpdateState.value){
        true -> {
            isSaving.value = false
            navHostController.navigate(Destination.UserHome.routes)
            MainActivity.adminDBRepo.searchUserByQuery(user.first_name.toCharArray().first().toString())
            MainActivity.adminDBRepo.updateSubUserProfileCreateUpdateState(false)
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
                title = {
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if(isEditable.value)
                            onDonePressed.value=true
                        else
                            navHostController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Filled.ArrowBack,contentDescription = "Back Button")
                    }
                },
                actions={
                    IconButton(
                        onClick = {
                            if(!isEditable.value)
                                isEditable.value=true
                        },
                        modifier = Modifier
                            .size(48.dp) // Adjust the size of the circular border
                            .border(
                                width = 2.dp, // Adjust the border width
                                color = if (!isEditable.value) Color.Gray else Color.Black, // Change the border color when in edit mode
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Text",
                            tint = if (!isEditable.value) Color.Gray else Color.Black
                        )
                    }
//                    IconButton(
//                        onClick = {
//                            if(!isEditable.value)
//                                isEditable.value=true
//                        },
//                        modifier = Modifier
//                            .size(48.dp) // Adjust the size of the circular border
//                            .border(
//                                width = 2.dp, // Adjust the border width
//                                color = if (!isEditable.value) Color.Gray else Color.Transparent, // Change the border color when in edit mode
//                                shape = CircleShape
//                            )
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Edit,
//                            contentDescription = "Edit Text",
//                            tint = if (!isEditable.value) Color.Gray else Color.Unspecified
//                        )
//                    }
                }
            )
        },
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            if(onDonePressed.value)
            {
                AlertView(
                    showAlert = true,
                    title = "Do you want to go back?",
                    subTitle = "You have unsaved changes.Your changes will be discarded if you press Yes.",
                    subTitle1 = "",
                    onYesClick = { navHostController.popBackStack()  },
                    onNoClick = { onDonePressed.value=false }) {
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Big text field for editing the text
            OutlinedTextField(
                value = text.value,
                onValueChange ={newText:String->
                    text.value=newText
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(300.dp),
                textStyle = TextStyle(fontSize = 16.sp, fontFamily = FontFamily(Font(R.font.roboto_regular)),),
                placeholder = { RegularTextView(title = "Please Enter Details", fontSize = 16)},
                enabled = isEditable.value,
                colors = TextFieldDefaults.textFieldColors(containerColor = Color(0xffdae3f3)),
            )

            // Add a Spacer to create some space between the text field and buttons
            Spacer(modifier = Modifier.height(16.dp))

            // Row to contain the Save and Close buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        isEditable.value=false
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
                        MainActivity.adminDBRepo.setNewSubUserprofile(user.copy())
                        MainActivity.adminDBRepo.setNewSubUserprofileCopy(user.copy())
                        isSaving.value = true
                        MainActivity.adminDBRepo.adminUpdateSubUser(user = user)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = Color(0xffdae3f3),
                        containerColor = Color(0xFF2f5597),
                    ),
                ) {
                    BoldTextView(
                        title = "Save",
                        fontSize = 22,
                        textColor = Color.White,
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        if(isEditable.value)
                            onDonePressed.value=true
                        else
                            navHostController.popBackStack()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = Color(0xffdae3f3),
                        containerColor = Color(0xFF2f5597),
                    ),
                ) {
                    BoldTextView(
                        title = "Done",
                        fontSize = 22,
                        textColor = Color.White,
                    )
                }
            }
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                // Save button
//                Button(
//                    onClick = {
//                              isEditable.value=false
//                    },
//                    Modifier.weight(1f)
//                        .padding(horizontal = 8.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        Color(0xFF2f5597)
//                    )
//                ) {
//                    BoldTextView(title = "Save", textColor = Color.White)
//                }
////                Button(
////                    onClick = {
////                        // Handle saving the edited text
////                    },
////                    modifier = Modifier.weight(1f)
////                ) {
////                    Text(text = "Save")
////                }
//
//                Spacer(modifier = Modifier.width(40.dp))
//
//                // Close button
//                Button(
//                    onClick = {
//                        navHostController.popBackStack()
//                    },
//                    Modifier.weight(1f)
//                        .padding(horizontal = 8.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        Color(0xFF2f5597)
//                    )
//                ) {
//                    BoldTextView(title = "Done", textColor = Color.White)
//                }
//            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        if(isSaving.value) showProgress()
    }
}


//@Preview
//@Composable
//fun previewScreen()
//{
//    EditTextScreen(navHostController = rememberNavController())
//}
