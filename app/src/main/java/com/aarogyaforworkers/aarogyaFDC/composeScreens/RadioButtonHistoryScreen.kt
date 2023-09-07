package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.Options

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioButtonHistoryScreen(navHostController: NavHostController, title:String, textToShow : String) {

    val listOfOptions = MainActivity.subUserRepo.subUserProfileOptionList

    val isSaving = remember {
        mutableStateOf(false)
    }

    var otherText by remember {
        mutableStateOf(listOfOptions.value.last()?.value)
    }

    var onOtherTextEdited by remember {
        mutableStateOf(false)
    }

    var onDonePressed= remember {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = {
                            onDonePressed.value=true
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back Button"
                        )
                    }
                },
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if(listOfOptions.value.last()?.isSelected=="1" && otherText!="")
                        {
                            listOfOptions.value.last()?.value=otherText!!
                            MainActivity.subUserRepo.updateOptionList(listOfOptions.value.toString())
                        }
                        if(title == "Family History"){
                            user.FamilyHistory = listOfOptions.value.toString()
                            MainActivity.adminDBRepo.setNewSubUserprofile(user.copy())
                            MainActivity.adminDBRepo.setNewSubUserprofileCopy(user.copy())
                            isSaving.value = true
                            MainActivity.adminDBRepo.adminUpdateSubUser(user = user)
                        }else{
                            user.SocialHistory = listOfOptions.value.toString()
                            MainActivity.adminDBRepo.setNewSubUserprofile(user.copy())
                            MainActivity.adminDBRepo.setNewSubUserprofileCopy(user.copy())
                            isSaving.value = true
                            MainActivity.adminDBRepo.adminUpdateSubUser(user = user)
                        }
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
                            onDonePressed.value=true
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                listOfOptions.value.forEachIndexed { index, option ->
                    if(option != null){
                        Box(
                            Modifier
                                .padding(horizontal = 8.dp)
                                .selectable(
                                    selected = option.isSelected == "1",
                                    onClick = {
                                        if (option.isSelected == "1") option.isSelected =
                                            "0" else option.isSelected = "1"
                                        MainActivity.subUserRepo.updateOptionList(listOfOptions.value.toString())
                                    }
                                )
                                .width(400.dp)
                                .background(Color(0x80DAE3F3), RoundedCornerShape(100.dp))
//                            .padding(vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(45.dp)
                                        .clickable {
                                            if (option.isSelected == "1") option.isSelected =
                                                "0" else option.isSelected = "1"
                                            MainActivity.subUserRepo.updateOptionList(listOfOptions.value.toString())
                                        }
                                        .background(
                                            color = if (option.isSelected == "1") Color(0xFF2f5597) else Color(
                                                0xffdae3f3
                                            ),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    BoldTextView(
                                        title = (index + 1).toString(),
                                        textColor = if (option.isSelected == "1") Color.White else Color.Black,
                                        fontSize = 22
                                    )
                                }
                                Spacer(modifier = Modifier.width(15.dp))
                                RegularTextView(title = option.name, fontSize = 22)
                            }
                        }
                        if(option.name == "Others" && option.isSelected == "1") {
                            TextField(
                                value = otherText!!,
                                onValueChange ={
                                    otherText=it
                                    onOtherTextEdited=true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                                    .padding(horizontal = 16.dp)
                                    .height(60.dp),
                                textStyle = TextStyle(fontSize = 16.sp),
//                        readOnly = !isEditable.value,
                                colors = TextFieldDefaults.textFieldColors(containerColor = Color(0xffdae3f3)),
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        if(isSaving.value) showProgress()
    }
}


fun parseOptions(optionList : String) : MutableList<Options>{
    val reminderRegex = """Options\(([^)]+)\)""".toRegex()
    val reminderMatches = reminderRegex.findAll(optionList)
    val optionsList = ArrayList<Options>()
    for (match in reminderMatches) {
        val properties = match.groupValues[1].split(", ")
        var name = ""
        var isSelected = ""
        var value = ""
        for (property in properties) {
            val keyValue = property.split("=")
            val key = keyValue[0]
            val values = keyValue[1]
            when (key) {
                "name" -> name = values
                "isSelected" -> isSelected = values
                "value" -> value = values
            }
        }
        val reminder = Options(name, isSelected, value)
        if(name.isNotEmpty()){
            optionsList.add(reminder)
        }
    }
    return optionsList
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun RadioButtonHistoryScreen(navHostController: NavHostController, title:String) {
//
//    var isEditable= remember {
//        mutableStateOf(false)
//    }
//
//    val options = listOf(
//        "Option 1" to "Description for Option 1",
//        "Option 2" to "Description for Option 2",
//        "Option 3" to "Description for Option 3",
//        "Option 4" to "Description for Option 4"
//    )
//
//    var selectedOptions by remember { mutableStateOf(emptySet<String>()) }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(title)
//                },
//                navigationIcon = {
//                    IconButton(onClick = { navHostController.popBackStack() }) {
//                        Icon(imageVector = Icons.Filled.ArrowBack,contentDescription = "Back Button")
//                    }
//                },
//            )
//        },
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .padding(horizontal = 8.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//
//            for ((option, description) in options) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(Color.White)
//                        .border(1.dp, Color.Gray, MaterialTheme.shapes.small)
//                        .selectable(
//                            selected = option in selectedOptions,
//                            onClick = {
//                                // Toggle the selection state
//                                selectedOptions = if (option in selectedOptions) {
//                                    selectedOptions - option
//                                } else {
//                                    selectedOptions + option
//                                }
//                            }
//                        )
//                        .padding(8.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    RadioButton(
//                        selected = option in selectedOptions,
//                        onClick = {
//                            // Toggle the selection state
//                            selectedOptions = if (option in selectedOptions) {
//                                selectedOptions - option
//                            } else {
//                                selectedOptions + option
//                            }
//                        },
//                        modifier = Modifier.padding(end = 16.dp)
//                    )
//                    Text(text = option)
//                }
//
//                if (option in selectedOptions) {
//                    Text(
//                        text = description,
//                        modifier = Modifier.padding(start = 32.dp),
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Button(
//                    onClick = {
//                        isEditable.value=false
//                    },
//                    Modifier.weight(1f)
//                        .padding(horizontal = 8.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        Color(0xFF2f5597)
//                    )
//                ) {
//                    BoldTextView(title = "Save", textColor = Color.White)
//                }
//
//                Spacer(modifier = Modifier.width(40.dp))
//
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
//
//            Spacer(modifier = Modifier.height(16.dp))
//        }
//    }
//}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun preview()
//{
//    RadioButtonHistoryScreen(navHostController = rememberNavController(),"Family History")
//}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun RadioButtonHistoryScreen(navHostController: NavHostController, title:String) {
//    var selectedOptions by remember { mutableStateOf(mutableListOf<String>()) }
//    val onOptionClick: (String) -> Unit = { option ->
//        if (selectedOptions.contains(option)) {
//            selectedOptions.remove(option)
//        } else {
//            selectedOptions.add(option)
//        }
//    }
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    BoldTextView(title = title,22)
//                },
//                navigationIcon = {
//                    IconButton(onClick = { navHostController.popBackStack() }) {
//                        Icon(imageVector = Icons.Filled.ArrowBack,contentDescription = "Back Button")
//                    }
//                },
//            )
//        },
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            Text(
//                text = "Select Options",
//                style = TextStyle(
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 20.sp
//                )
//            )
//
//            OptionItem(
//                circleNum = "1",
//                label = "Option 1",
//                checked = selectedOptions.contains("1"),
//                onOptionClick = { onOptionClick("1") }
//            )
//
//            OptionItem(
//                circleNum = "2",
//                label = "Option 2",
//                checked = selectedOptions.contains("2"),
//                onOptionClick = { onOptionClick("2") }
//            )
//
//            OptionItem(
//                circleNum = "3",
//                label = "Option 3",
//                checked = selectedOptions.contains("3"),
//                onOptionClick = { onOptionClick("3") }
//            )
//
//            OptionItem(
//                circleNum = "4",
//                label = "Option 4",
//                checked = selectedOptions.contains("4"),
//                onOptionClick = { onOptionClick("4") }
//            )
//
//            OptionItem(
//                circleNum = "5",
//                label = "Option 5",
//                checked = selectedOptions.contains("5"),
//                onOptionClick = { onOptionClick("5") }
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Button(
//                    onClick = {},
//                    modifier = Modifier.weight(1f),
//                    colors = ButtonDefaults.buttonColors(
//                        disabledContainerColor = Color(0xffdae3f3),
//                        containerColor = Color(0xFF2f5597),
//                    ),
////                    enabled =
//                ) {
//                    BoldTextView(
//                        title = "Save",
//                        fontSize = 22,
//                        textColor = Color.White,
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(16.dp))
//
//                Button(
//                    onClick = {
//                    },
//                    modifier = Modifier.weight(1f),
//                    colors = ButtonDefaults.buttonColors(
//                        disabledContainerColor = Color(0xffdae3f3),
//                        containerColor = Color(0xFF2f5597),
//                    ),
////                    enabled =
//                ) {
//                    BoldTextView(
//                        title = "Done",
//                        fontSize = 22,
//                        textColor = Color.White,
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun OptionItem(
//    circleNum: String,
//    label: String,
//    checked: Boolean,
//    onOptionClick: (String) -> Unit
//) {
//    Box(
//        Modifier
//            .padding(horizontal = 8.dp)
//            .clickable { onOptionClick(circleNum) }
//            .width(400.dp)
//            .background(Color(0x80DAE3F3), RoundedCornerShape(100.dp))
//            .padding(vertical = 5.dp)) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Box(
//                modifier = Modifier
//                    .size(45.dp)
//                    .background(
//                        color = if (checked) Color(0xFF2f5597) else Color(0xffdae3f3),
//                        shape = CircleShape
//                    ),
//                contentAlignment = Alignment.Center
//            ){
//                BoldTextView(title = circleNum, textColor = if(checked) Color.White else Color.Black, fontSize = 22)
//            }
//            Spacer(modifier = Modifier.width(15.dp))
//            RegularTextView(title = label, fontSize = 22)
//        }
//    }
//}
//
//@Composable
//fun MultipleChoiceApp() {
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = Color.White
//        ) {
//            RadioButtonHistoryScreen(navHostController = rememberNavController(),"History")
//        }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewMultipleChoiceApp() {
//    MultipleChoiceApp()
//}