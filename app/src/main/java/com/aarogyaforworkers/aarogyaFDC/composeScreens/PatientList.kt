@file:OptIn(ExperimentalMaterial3Api::class)

package com.aarogyaforworkers.aarogyaFDC.composeScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity

@Composable
fun PatientList(navHostController: NavHostController){
    Column(Modifier.fillMaxSize()) {
        TopRow(navHostController)
        LazyColumn(){
            itemsIndexed(MainActivity.adminDBRepo.subUserSearchProfileListState.value.filter { it.user_id.isNotEmpty() }){ index, patient ->
                SearchResultUserCard(userProfile = patient)
            }
        }
    }
}


@Composable
fun TopRow(navHostController: NavHostController){
    Row(
        Modifier
            .fillMaxWidth()
            .width(40.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { navHostController.navigate(Destination.Home.routes) }) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "BackIcon")
        }
        
        BoldTextView(title = "Patient List")
        
        IconButton(onClick = { /*TODO*/ }) {
            Icon(imageVector = Icons.Default.Sort, contentDescription = "FilterIcon")
        }
        
    }
}