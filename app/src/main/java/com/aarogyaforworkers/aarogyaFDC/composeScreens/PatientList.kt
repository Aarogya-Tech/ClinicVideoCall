@file:OptIn(ExperimentalMaterial3Api::class)

package com.aarogyaforworkers.aarogyaFDC.composeScreens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogya.R
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity

@Composable
fun PatientList(navHostController: NavHostController){

    var isNameSort = remember { mutableStateOf(false) }
    var isIdSort = remember { mutableStateOf(false) }

    val nameFilter = MainActivity.adminDBRepo.subUserSearchProfileListState.value.filter { it.user_id.isNotEmpty()  }

    val filterList =
        when{
            isNameSort.value -> nameFilter.sortedBy { it.first_name }
            isIdSort.value -> nameFilter.sortedBy { it.user_id }
            else -> nameFilter
        }
//        if(isNameSort.value){
//        nameFilter.sortedBy { it.first_name }
//    }

    Column(Modifier.fillMaxSize()) {
        TopRow(navHostController)
        ButtonRow(onNameSort = { isNameSort.value = !isNameSort.value }, onIdSort = { isIdSort.value = !isIdSort.value }, isNameSort = isNameSort.value, isIdSort = isIdSort.value)
        LazyColumn(){
            itemsIndexed(filterList){ index, patient ->
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
            .width(40.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { navHostController.navigate(Destination.Home.routes) }) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "BackIcon")
        }
        
        BoldTextView(title = "Patient List", fontSize = 25)
    }
}

@Composable
fun ButtonRow(onNameSort: () -> Unit, onIdSort: () -> Unit, isNameSort: Boolean, isIdSort: Boolean ){
    var isSortClicked = remember { mutableStateOf(false) }
    
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Box(Modifier.weight(1f)) {
            SortBtn(text = "Name", onSortClick = { onNameSort() }, isSortClicked = isNameSort)
        }
        Spacer(modifier = Modifier.width(20.dp))
        Box(Modifier.weight(1f)) {
            SortBtn(text = "Patient Id", onSortClick = { onIdSort() }, isSortClicked = isIdSort)
        }
    }
}

@Composable
fun SortBtn(text: String, onSortClick: () -> Unit , isSortClicked: Boolean){
    Row(
        Modifier
            .fillMaxWidth()
            .height(35.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
            .clickable {
                onSortClick()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround) {
            RegularTextView(title = text, fontSize = 16)
            Spacer(modifier = Modifier.width(24.dp))
            Icon(imageVector = ImageVector.vectorResource(id = if(isSortClicked) R.drawable.sort_from_top_to_bottom  else R.drawable.sort_from_bottom_to_top), contentDescription = "sort Icon", Modifier.size(16.dp))

    }
}