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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.awsapi.models.AdminProfile
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupVideoCallingScreen(navHostController:NavHostController)
{
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(horizontal = 15.dp,),
                title = {
                    BoldTextView("Group Call", fontSize = 25)
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

            Divider(modifier=Modifier.padding(horizontal = 16.dp,))

            Row(
                modifier= Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NormalTextView(title = "Choose Up to 15 people. They'll be able to join the call at any time after it starts")
            }

            Divider(modifier=Modifier.padding(horizontal = 16.dp,))

            Spacer(modifier = Modifier.height(3.dp))
            
            LazyColumn(modifier= Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)){
                items(adminList){admin->
                    GroupAdminCard(admin = admin)
                }
            }
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