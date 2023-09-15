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
import com.aarogyaforworkers.aarogyaFDC.SubUser.SubUserDBRepository
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioButtonHistoryScreen(navHostController: NavHostController, title:String, textToShow : String) {
    Disableback()


    val listOfOptions = MainActivity.subUserRepo.subUserProfileOptionList

    val listOfOptions1 = MainActivity.subUserRepo.subUserProfileOptionList1

    val isSaving = remember {
        mutableStateOf(false)
    }

    var otherText by remember {
        mutableStateOf(listOfOptions.value.last()?.value)
    }

    var onOtherTextEdited = remember {
        mutableStateOf(false)
    }

    var onDonePressed= remember {
        mutableStateOf(false)
    }

    var otherTextError= remember {
        mutableStateOf(false)
    }

    var isEdited= remember {
        mutableStateOf(false)
    }


    val user = MainActivity.adminDBRepo.getSelectedSubUserProfile().copy()

    when(MainActivity.adminDBRepo.subUserProfileCreateUpdateState.value){
        true -> {
            isSaving.value = false
//            navHostController.navigate(Destination.UserHome.routes)
            MainActivity.adminDBRepo.searchUserByQuery(user.first_name.toCharArray().first().toString())
            MainActivity.adminDBRepo.updateSubUserProfileCreateUpdateState(false)
            MainActivity.adminDBRepo.updateSubUserProfileCreateUpdateState(null)

        }
        false -> {
            MainActivity.adminDBRepo.updateSubUserProfileCreateUpdateState(null)


        }
        null ->{

        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    BoldTextView(title, fontSize = 20)
                },
                navigationIcon = {
                    IconButton(onClick = {
//                        Log.i("Options1  : ",listOfOptions.value.toString())
//                        Log.i("Options2 : ",listOfOptions1.value.toString())
                        val modifiedString1 = listOfOptions.value.toString().replace("Options", "")
                        val modifiedString2 = listOfOptions1.value.toString().replace("Options1", "")
//                        Log.i("Options1  : ",modifiedString1)
//                        Log.i("Options2 : ",modifiedString1)
                        if(modifiedString1!=modifiedString2 || onOtherTextEdited.value)
                            onDonePressed.value=true
                        else
                            navHostController.popBackStack()
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
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                PopBtnDouble(btnName1 = "Save", btnName2 = "Done", onBtnClick1 = {
                    isEdited.value=false
                    if(listOfOptions.value.last()?.isSelected == "1" && otherText!!.isBlank())
                    {
                        otherTextError.value= true
                    }
                    else{
                        onOtherTextEdited.value=false
                        if (listOfOptions.value.last()?.isSelected == "1") {
                            listOfOptions.value.last()?.value = otherText!!
                            MainActivity.subUserRepo.updateOptionList(listOfOptions.value.toString())
                        }
                        if(listOfOptions.value.last()?.isSelected == "0")
                        {
                            listOfOptions.value.last()?.value=""
                            MainActivity.subUserRepo.updateOptionList(listOfOptions.value.toString())
                        }
                        if (title == "Family History") {
                            user.FamilyHistory = listOfOptions.value.toString()
                            MainActivity.adminDBRepo.setNewSubUserprofile(user.copy())
                            MainActivity.adminDBRepo.setNewSubUserprofileCopy(user.copy())
                            isSaving.value = true
                            MainActivity.adminDBRepo.adminUpdateSubUser(user = user)
                            MainActivity.subUserRepo.updateOptionList(user.FamilyHistory)
                            MainActivity.subUserRepo.updateOptionList1(user.FamilyHistory)
                        } else {
                            user.SocialHistory = listOfOptions.value.toString()
                            MainActivity.adminDBRepo.setNewSubUserprofile(user.copy())
                            MainActivity.adminDBRepo.setNewSubUserprofileCopy(user.copy())
                            isSaving.value = true
                            MainActivity.adminDBRepo.adminUpdateSubUser(user = user)
                            MainActivity.subUserRepo.updateOptionList(user.SocialHistory)
                            MainActivity.subUserRepo.updateOptionList1(user.SocialHistory)
                        }
                    }
                },
                    {
                        val modifiedString1 = listOfOptions.value.toString().replace("Options", "")
                        val modifiedString2 = listOfOptions1.value.toString().replace("Options1", "")
                        if(modifiedString1!=modifiedString2 || onOtherTextEdited.value)
                            onDonePressed.value=true
                        else
                            navHostController.popBackStack()
                    },
                    enable = isEdited.value

                )
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
                                        val modifiedString1 = listOfOptions.value.toString().replace("Options", "")
                                        val modifiedString2 = listOfOptions1.value.toString().replace("Options1", "")
                                        isEdited.value = modifiedString1!=modifiedString2 || onOtherTextEdited.value
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
//                                        .clickable {
//                                            if (option.isSelected == "1") option.isSelected =
//                                                "0" else option.isSelected = "1"
//                                            MainActivity.subUserRepo.updateOptionList(listOfOptions.value.toString())
//                                        }
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
                                    onOtherTextEdited.value=true
                                    otherTextError.value=false
                                    MainActivity.subUserRepo.updateOptionList(listOfOptions.value.toString())
                                    isEdited.value=true
                                },
                                isError = otherTextError.value,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                                    .padding(horizontal = 16.dp)
                                    .heightIn(min = 48.dp),
                                textStyle = TextStyle(fontSize = 16.sp),
                                colors = TextFieldDefaults.textFieldColors(containerColor = Color(0xffdae3f3)),
                            )
                            if(otherTextError.value){
                                Text(
                                    modifier = Modifier.padding(start = 16.dp),
                                    text = "Other Option Cannot Be Empty",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
    if(isSaving.value) showProgress()
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

fun parseOptions1(optionList : String) : MutableList<SubUserDBRepository.Options1>{
    val reminderRegex = """Options\(([^)]+)\)""".toRegex()
    val reminderMatches = reminderRegex.findAll(optionList)
    val optionsList = ArrayList<SubUserDBRepository.Options1>()
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
        val reminder = SubUserDBRepository.Options1(name, isSelected, value)
        if(name.isNotEmpty()){
            optionsList.add(reminder)
        }
    }
    return optionsList
}
