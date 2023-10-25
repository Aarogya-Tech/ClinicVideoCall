@file:OptIn(ExperimentalMaterial3Api::class)

package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.aarogyaforworkers.aarogyaFDC.R
import com.aarogyaforworkers.aarogyaFDC.Commons.ifIsExitAndSave
import com.aarogyaforworkers.aarogyaFDC.Commons.isOnUserHomeScreen
import com.aarogyaforworkers.aarogyaFDC.Commons.isReadyForWeight
import com.aarogyaforworkers.aarogyaFDC.Commons.isSetRequestSent
import com.aarogyaforworkers.aarogyaFDC.Commons.lastFailed
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.SubUser.SessionStates
import com.aarogyaforworkers.awsapi.models.SubUserProfile
import net.huray.omronsdk.utility.Handler

var isFromPatientList = false

// Enum definition
enum class SortState {
    NONE,
    ASCENDING,
    DESCENDING
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PatientList(navHostController: NavHostController){

    Disableback()

    val handler = Handler()

    var nameSortState = remember { mutableStateOf(SortState.NONE) }
    var idSortState = remember { mutableStateOf(SortState.NONE) }


    var nameFilter = MainActivity.adminDBRepo.subUserSearchProfileListState.value.filter { it.user_id.isNotEmpty()  }

    var filterList = when {
        nameSortState.value == SortState.ASCENDING -> nameFilter.sortedBy { it.first_name + it.last_name }
        nameSortState.value == SortState.DESCENDING -> nameFilter.sortedByDescending { it.first_name + it.last_name }
        idSortState.value == SortState.ASCENDING -> nameFilter.sortedBy { it.user_id }
        idSortState.value == SortState.DESCENDING -> nameFilter.sortedByDescending { it.user_id }
        else -> nameFilter
    }

    when(MainActivity.adminDBRepo.searchDoneStatus.value){
        true -> {
            handler.postDelayed({
                MainActivity.adminDBRepo.isSearching.value = false
            }, 500)
            MainActivity.adminDBRepo.updateSearchedState(null)
        }

        false -> {
            handler.postDelayed({
                MainActivity.adminDBRepo.isSearching.value = false
            }, 500)
            MainActivity.adminDBRepo.updateSearchedState(null)
        }

        null -> {

        }

    }

    Column(Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))

        TopRow(navHostController)
        Spacer(modifier = Modifier.height(20.dp))

        ButtonRow(onNameSort = {
            when(nameSortState.value) {
                SortState.NONE -> nameSortState.value = SortState.ASCENDING
                SortState.ASCENDING -> nameSortState.value = SortState.DESCENDING
                SortState.DESCENDING -> nameSortState.value = SortState.ASCENDING
            }
            idSortState.value = SortState.NONE
        },
            onIdSort = {
                when(idSortState.value) {
                    SortState.NONE -> idSortState.value = SortState.ASCENDING
                    SortState.ASCENDING -> idSortState.value = SortState.DESCENDING
                    SortState.DESCENDING -> idSortState.value = SortState.ASCENDING
                }
                nameSortState.value = SortState.NONE
            },
            isNameSort = nameSortState.value != SortState.NONE,
            isIdSort = idSortState.value != SortState.NONE)

//        ButtonRow(onNameSort = {
//            isNameSort.value = !isNameSort.value
//
//            isIdSort.value = false },
//            onIdSort = {
//                isIdSort.value = !isIdSort.value
//
//                isNameSort.value = false },
//            isNameSort = isNameSort.value,
//            isIdSort = isIdSort.value)
        Spacer(modifier = Modifier.height(15.dp))
        LazyColumn(Modifier.padding(horizontal = 16.dp)){
            itemsIndexed(filterList){ index, patient ->
                Box(modifier = Modifier
                    .clickable(onClick = {
                        if (!subUserSelected) {
                            isFromPatientList = true
                            MainActivity.pc300Repo.clearSessionValues()
                            isSetRequestSent = false
                            lastFailed = false
                            isReadyForWeight = false
                            // if different user goes then reset omron sync status
                            if (MainActivity.adminDBRepo.getSelectedSubUserProfile().user_id != patient.user_id) {
                                MainActivity.omronRepo.isReadyForFetch = false
                                MainActivity.subUserRepo.isResetQuestion.value = true
                            }

                            MainActivity.subUserRepo.clearSessionList()
                            MainActivity.sessionRepo.updateSessionFetch(true)
                            MainActivity.sessionRepo.updateSessionFetchStatus(null)
                            MainActivity.subUserRepo.getSessionsByUserID(userId = patient.user_id)
                            MainActivity.pc300Repo.isShowEcgRealtimeAlert.value = false
                            isShown = false
                            MainActivity.adminDBRepo.setNewSubUserprofile(patient.copy())
                            MainActivity.adminDBRepo.setNewSubUserprofileCopy(patient.copy())
                            MainActivity.subUserRepo.isResetQuestion.value = true
                            MainActivity.subUserRepo.updateSessionState(
                                SessionStates(
                                    false,
                                    false,
                                    false,
                                    false,
                                    false
                                )
                            )
                            MainActivity.subUserRepo.resetStates()
                            ifIsExitAndSave = false
                            MainActivity.subUserRepo.lastSavedSession = null
                            MainActivity.subUserRepo.createNewSession()
//                  MainActivity.localDBRepo.createNewSession()
                            navHostController.navigate(Destination.UserHome.routes)
                            isOnUserHomeScreen = true
                        }
                    })
                ){
                    SearchResultUserCard(userProfile = patient)
                }
            }
        }
    }
    if(MainActivity.adminDBRepo.isSearching.value) showProgress()
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