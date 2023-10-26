package com.aarogyaforworkers.aarogyaFDC.composeScreens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.awsapi.models.AdminProfile
import com.github.mikephil.charting.renderer.scatter.SquareShapeRenderer
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupVideoCallingScreen(navHostController:NavHostController) {
    var doctor = MainActivity.adminDBRepo.adminProfileState.value
    var isSelected = remember { mutableStateOf(false) }
    var selectedIndex = remember { mutableStateOf(-1) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    BoldTextView(doctor.hospitalName, fontSize = 20)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navHostController.navigate(Destination.Home.routes)
                    }) {
                        Icon(imageVector = Icons.Filled.ArrowBack,contentDescription = "Back Button")
                    }
                },
            )
        },
    ){
        Column(modifier= Modifier
            .fillMaxSize()
            .padding(it)) {

            Row() {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 23.dp)
                        .selectable(selected = isSelected.value,
                            onClick = {
                                isSelected.value = !isSelected.value

                            })
                        ) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(32.dp)
                                .background(
                                    color = if (isSelected.value) Color(0xFF2f5597) else Color(
                                        0xffdae3f3
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                ), contentAlignment = Alignment.Center) {
                            if (isSelected.value){
                                Icon(imageVector = Icons.Default.Check, contentDescription = "checkIcon", Modifier.size(15.dp), tint = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.width(15.dp))
                        RegularTextView(title = "Select All", fontSize = 22)
                    }
                }
            }
            
            LazyColumn(modifier= Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)){
                itemsIndexed(adminList){index, admin->
                    GroupCard(firstName = admin.first_name, lastName = admin.last_name,
                        isSelected = if (isSelected.value) true else index == selectedIndex.value) {
                        if (selectedIndex.value == index) {
                            // Deselect
                            selectedIndex.value = -1
                        } else {
                            // Select
                            selectedIndex.value = index
                        }

                        if (isSelected.value && (selectedIndex.value == index)){
                            isSelected.value = false
                            selectedIndex.value = -1
                        }
                    }
                }
            }
//            FloatingActionButton(onClick = { /*TODO*/ }, modifier = Modifier.align(Alignment.Horizontal(Alignment.BottomEnd)) ) {
//                BoldTextView(title = "Video Call")
//            }
        }
    }
}



@Composable
fun GroupCard(firstName: String, lastName: String, isSelected: Boolean, onSelect: () -> Unit ){
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .selectable(selected = isSelected, onClick = { onSelect() })
            .background(Color(0x80DAE3F3), RoundedCornerShape(100.dp))) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(45.dp)
                    .background(
                        color = if (isSelected) Color(0xFF2f5597) else Color(0xffdae3f3),
                        shape = CircleShape
                    ), contentAlignment = Alignment.Center) {
                if (isSelected){
                    Icon(imageVector = Icons.Default.Check, contentDescription = "checkIcon", Modifier.size(25.dp), tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.width(15.dp))

            RegularTextView(title = "$firstName $lastName", fontSize = 22)
        }
    }
}


@Composable
fun GroupAdminCard(admin: AdminProfile)
{
    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .background(Color(0xBFE2D2FD), shape = RoundedCornerShape(8.dp))
    ) {
        Row(
            Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                UserImageView(imageUrl = admin.profile_pic_url, size = 55.dp) {}
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Row {
                    LabelWithoutIconView(title = admin.first_name.capitalize(Locale.ROOT))
                    Spacer(modifier = Modifier.width(5.dp))
                    LabelWithoutIconView(title = admin.last_name.capitalize(Locale.ROOT))
                }
                Row {
                    LabelWithIconView(title = adminGenderShort(admin),icon = if(checkIsMale(admin.gender)) Icons.Default.Male else Icons.Default.Female)
                    Spacer(modifier = Modifier.width(5.dp))
                    LabelWithIconView(title = admin.age, icon = Icons.Default.Cake)
                }
            }
        }
    }
}