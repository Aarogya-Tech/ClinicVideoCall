package com.aarogyaforworkers.aarogyaFDC.composeScreens

import Commons.AddEditUserPageTags
import Commons.HomePageTags
import Commons.LoginTags
import Commons.UserHomePageTags
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.aarogyaforworkers.aarogya.R
import com.aarogyaforworkers.aarogya.composeScreens.isFromVital
import com.aarogyaforworkers.aarogyaFDC.Commons.timestamp
import com.aarogyaforworkers.aarogyaFDC.Commons.timestamp
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.SubUser.SubUserDBRepository
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.CardExpansionState
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.Device
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.VisitSummaryViewModel
import com.aarogyaforworkers.aarogyaFDC.ui.theme.defCardDark
import com.aarogyaforworkers.aarogyaFDC.ui.theme.defDark
import com.aarogyaforworkers.aarogyaFDC.ui.theme.defLight
import com.aarogyaforworkers.awsapi.models.Session
import com.aarogyaforworkers.awsapi.models.SubUserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//Back btn
@Composable
fun BackBtn(onBackBtnPressed : () -> Unit){
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
            contentAlignment = Alignment.CenterStart) {
            IconButton(onClick = {
                onBackBtnPressed()
            }) {
                Icon(imageVector = ImageVector.vectorResource(id =  R.drawable.back_btn_icon),
                    contentDescription = "BackBtn")
            }
        }
    }
}


//#Change16May
//For authentication error message
@Composable
fun ErrorMessage(errorMessage: String, errorTestTag: String) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .testTag(errorTestTag)) {
        ItalicTextView(title = errorMessage, textColor = Color.Red)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEntry(
    input: String,
    onChangeInput: ((String) -> Unit),
    editInput: Boolean,
    keyboardType: KeyboardType,
    placeholderText: String,
    isError: Boolean
) {
    TextField(
        value = input,
        onValueChange = onChangeInput,
        placeholder = { RegularTextView(title = placeholderText) },
        isError = isError,
        enabled = editInput,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done),
        colors = TextFieldDefaults.textFieldColors
            (Color.Black,
            cursorColor = Color.Black,
            focusedIndicatorColor = Color.Black,
            unfocusedIndicatorColor = Color.Black,
            errorIndicatorColor = Color.Red),
        singleLine = true,
        textStyle = TextStyle(fontFamily = FontFamily(Font(R.font.roboto_regular)), fontSize = 14.sp )
    )

}

@Composable
fun InputView(title:String,
              textIp: String,
              onChangeIp: (String) -> Unit,
              textIp1: String? = null,
              onChangeIp1: ((String) -> Unit)? = null,
              tag: String,
              keyboard: KeyboardType,
              placeholderText: String,
              isEdit: Boolean? = null,
              isError: Boolean? = null,
              placeholderText1: String? = null,
){
    Row(modifier = Modifier
        .testTag(tag + 1), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier
            .width(75.dp)
            .testTag(tag)){
            BoldTextView(title = title)
        }
        Box(modifier = Modifier
            .weight(1f)
            .testTag(tag)) {
            ProfileEntry(
                input = textIp,
                onChangeInput = onChangeIp,
                editInput = isEdit?: false,
                keyboardType = keyboard,
                placeholderText = placeholderText,
                isError = isError?:false
            )
        }

        when(textIp1 != null && onChangeIp1 != null){
            true -> {
                Spacer(modifier = Modifier.width(10.dp))
                Box(modifier = Modifier.weight(1f)) {
                    ProfileEntry(
                        input = textIp1,
                        onChangeInput = onChangeIp1,
                        editInput = isEdit?: false,
                        keyboardType = keyboard,
                        placeholderText = placeholderText1.toString(),
                        isError = isError?:false
                    )
                }
            }
            false-> null
        }
    }
}

// connectionCard
@Composable
fun ConnectionCard(device : Device, tag: String, onConnectionBtnClicked : (Boolean) -> Unit){
    Card(modifier = Modifier
        .clickable {
            onConnectionBtnClicked(device.isConnected)
        }
        .fillMaxWidth()
        .testTag(tag)
        .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(10.dp)
    ){
        Row(modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Column{
                Text(text = device.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(15.dp))
                Text(text = device.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(15.dp))
                Text(text = device.address,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold)
            }
            Column{
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            if (device.isConnected) defLight else defDark
                        ),
                    contentAlignment = Alignment.Center
                ){
                    IconButton(onClick = {
                        onConnectionBtnClicked(device.isConnected)
                    }) {
                        if(device.isConnected){
                            Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ble_dis),
                                contentDescription = "DisconnectBtn",
                                tint = Color.White)
                        }else{
                            Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ble_con),
                                contentDescription = "ConnectBtn",
                                tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Disableback(){
    // Use LocalOnBackPressedDispatcherOwner to get the onBackPressedDispatcher
    val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val backPressedCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing to disable the back button
            }
        }
    }
    DisposableEffect(dispatcher) {
        dispatcher?.addCallback(backPressedCallback)
        onDispose {
            backPressedCallback.remove()
        }
    }
}

//HomeScreenItems

@Composable
fun ConnectionBtnView(isConnected : Boolean, size: Dp, onIconClick : () -> Unit){
    when(isConnected){
        true -> ActionIconBtn(size = size, borderColor = defLight, icon = ImageVector.vectorResource(id = R.drawable.ble_connected,), desc = "BleDiscBtn") {
            onIconClick()
        }
        false -> ActionIconBtn(size = size, borderColor = Color.Red,icon = ImageVector.vectorResource(id = R.drawable.ble_disconnected), desc = "BleContBtn") {
            onIconClick()
        }
    }
}

@Composable
fun SignOutBtnView(onIconClick : () -> Unit){
    ActionIconBtn(size = 36.dp, borderColor = defDark, icon = ImageVector.vectorResource(id = R.drawable.signout), desc = "LogoutBtn") {
        onIconClick()
    }
}

@Composable
fun ActionBtn(title : String ,onBtnClick : () -> Unit){
    Button(onClick = { onBtnClick() }, modifier = Modifier.fillMaxWidth()) { TitleView(title = title) }
}

@Composable
fun ItalicTextView(title : String, fontSize: Int = 14, textColor: Color = Color.Black  ){
    Text(text = title,fontFamily = FontFamily(Font(R.font.roboto_italic)),fontSize = fontSize.sp, color = textColor)
}

@Composable
fun ActionIconBtn(size : Dp, icon : ImageVector, borderColor: Color, desc : String, onIconClick : () -> Unit){
    IconButton(onClick = { onIconClick() }, modifier = Modifier
        .size(size)
        .testTag(desc)
        .border(3.dp, borderColor, CircleShape)) {
        Icon(imageVector = icon, contentDescription = desc)
    }
}

@Composable
fun ActionBtn(size : Dp, icon : ImageVector, onIconClick : () -> Unit){
    Box( modifier = Modifier
        .background(defCardDark, shape = RoundedCornerShape(5.dp))
        .size(size + 12.dp), contentAlignment = Alignment.Center) {
        IconButton(onClick = { onIconClick() }, modifier = Modifier
            .size(size)) {
            Icon(imageVector = icon, contentDescription = "icon")
        }
    }
}

@Composable
fun ActionBtn(btnName:String = "",size : Dp, icon : ImageVector, onIconClick : () -> Unit){
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box( modifier = Modifier
            .background(defCardDark, shape = RoundedCornerShape(5.dp))
            .size(size + 12.dp), contentAlignment = Alignment.Center) {
            IconButton(onClick = { onIconClick() }, modifier = Modifier
                .size(size)
                .testTag(btnName)) {
                Icon(imageVector = icon, contentDescription = "icon")
            }
        }
        RegularTextView(title = btnName, fontSize = 10)
    }
}

@Composable
fun ConnectionActionBtn(isConnected: Boolean, size : Dp, onIconClick : () -> Unit){
    Box( modifier = Modifier
        .border(2.dp, if (isConnected) defLight else Color.Red, CircleShape),
        contentAlignment = Alignment.Center) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(10.dp)) {
            IconButton(onClick = { onIconClick() }) {
                Icon(imageVector = if(isConnected) ImageVector.vectorResource(id = R.drawable.ble_connected) else ImageVector.vectorResource(
                    id = R.drawable.ble_disconnected
                ), contentDescription = UserHomePageTags.shared.connectionBtn, Modifier.size(size))
            }
        }
    }
}

//@Composable
//fun ConnectionActionBtn(isConnected: Boolean, size : Dp, onIconClick : () -> Unit){
//    Box( modifier = Modifier
//        .background(defCardDark, shape = RoundedCornerShape(5.dp))
//        .border(
//            width = 2.dp,
//            color = if (isConnected) defLight else Color.Red,
//            shape = RoundedCornerShape(5.dp)
//        )
//        .size(size + 12.dp), contentAlignment = Alignment.Center) {
//        IconButton(onClick = { onIconClick() }, modifier = Modifier
//            .size(size)) {
//            Icon(imageVector = if(isConnected) ImageVector.vectorResource(id = R.drawable.ble_connected) else ImageVector.vectorResource(
//                id = R.drawable.ble_disconnected
//            ), contentDescription = UserHomePageTags.shared.connectionBtn)
//        }
//    }
//}

@ExperimentalMaterial3Api
@Composable
fun SearchView(searchText : String, isSearching: Boolean, onValueChange : (String) -> Unit){
    TextField(
        value = searchText,
        onValueChange = {
            onValueChange(it)
        },
        placeholder = { Text(text = "Search user by name or phone...",  style = TextStyle.Default) },
        leadingIcon = { Icon(Icons.Filled.Search, null) },
        trailingIcon = {
            if (isSearching) {
                // Display searching indicator (e.g. a progress spinner) as trailing icon
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .testTag(HomePageTags.shared.searchView)
            .background(defCardDark, shape = RoundedCornerShape(8.dp)),
        singleLine = true,
    )
}

@Composable
fun SearchResultView(searchResults : List<SubUserProfile>?, onResultFound : () -> Unit, onSelectingPatient : (SubUserProfile) -> Unit, onAddNewUserClicked: () -> Unit){

    when{

        searchResults.isNullOrEmpty() -> {
            LaunchedEffect(Unit) {
                delay(1000)
                onResultFound()
            }
            ShowAddNewUser { onAddNewUserClicked() }
        }

        searchResults.isNotEmpty() -> {
            LaunchedEffect(Unit) {
                delay(1000)
                onResultFound()
            }
            LazyColumn(
                modifier = Modifier.fillMaxHeight(),
                contentPadding = PaddingValues(4.dp),
            ) {
                items(searchResults) { result ->
                    Box(modifier = Modifier.clickable(onClick = { onSelectingPatient(result) })
                    ){
                        SearchResultUserCard(userProfile = result)
                    }
                }
            }
        }
    }
}


@Composable
fun HeaderRow(title1: String, title2: String, title3: String, title4: String){
    Row(modifier = Modifier
        .background(Color(0xffe7492b))
        .fillMaxWidth()
        .height(25.dp),Arrangement.Center, Alignment.CenterVertically) {
//        Spacer(modifier = Modifier.width(5.dp))

        Box(Modifier.width(100.dp), contentAlignment = Alignment.Center) {
            BoldTextView(title = title1, fontSize = 14, textColor = Color.White)
        }
        Spacer(modifier = Modifier.width(5.dp))

        Box(Modifier.width(70.dp), contentAlignment = Alignment.Center) {
            BoldTextView(title = title2, fontSize = 14, textColor = Color.White)
        }
        Spacer(modifier = Modifier.width(5.dp))

        Box(Modifier.width(50.dp), contentAlignment = Alignment.Center) {
            BoldTextView(title = title3, fontSize = 14, textColor = Color.White)
        }
        Spacer(modifier = Modifier.width(5.dp))

        Box(Modifier.width(90.dp), contentAlignment = Alignment.Center) {
            BoldTextView(title = title4, fontSize = 14, textColor = Color.White)
        }
    }
}

@Composable
fun DataRow(rowColor: Color,title: String, unit:String, value:String, avg:String, range:String, validRange: ClosedRange<Double>?= null){
    var inRange = 1

    if(value.contains("/")){
        val bpData = value.split("/")
        val sys = bpData[0]
        val dia = bpData[1]
        inRange = when{

            sys.isEmpty() || dia.isEmpty() -> {
                3
            }
            
            sys.toDouble() < 120 && dia.toDouble() < 80-> {
                1
            }
            else -> {
                2
            }
        }
    }else{
        inRange = when {
            value.isEmpty() -> 3
            validRange == null -> 1 // if validRange is null, then data is in range
            value.toDoubleOrNull() == null -> 1 // if data cannot be converted to double, return 0
            else -> if (value.toDoubleOrNull()!! in validRange) 1 else 2 // check if data is in validRange
        }
    }

    Row(modifier = Modifier
        .background(rowColor)
        .fillMaxWidth()
        .height(25.dp),Arrangement.Center,Alignment.CenterVertically) {
//        Spacer(modifier = Modifier.width(5.dp))

        Box(Modifier.width(60.dp), contentAlignment = Alignment.CenterStart) {
            BoldTextView(title = title, fontSize = 14)
        }

        Box(Modifier.width(40.dp), contentAlignment = Alignment.Center) {
            RegularTextView(title = unit, fontSize = 12)
        }
        Spacer(modifier = Modifier.width(5.dp))

        Box(Modifier.width(70.dp), contentAlignment = Alignment.Center) {
            when(inRange){
                1 -> RegularTextView(title = value, fontSize = 14)
                2 -> BoldTextView(title = value, fontSize = 14, textColor = Color.Red)
                3 -> RegularTextView(title = "-", fontSize = 14)
            }
        }
        Spacer(modifier = Modifier.width(5.dp))

        Box(Modifier.width(50.dp), contentAlignment = Alignment.Center) {
            if(avg.isEmpty() || avg == "0.0" || avg == "0/0"){
                RegularTextView(title = "-", fontSize = 12)
            }else{
                RegularTextView(title = avg, fontSize = 12)
            }
        }
        Spacer(modifier = Modifier.width(5.dp))

        Box(Modifier.width(90.dp), contentAlignment = Alignment.Center) {
            RegularTextView(title = range, fontSize = 14)
        }
    }
}

@Composable
fun ShowAddNewUser(onAddNewUserClicked : () -> Unit){
    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .background(defCardDark, shape = RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = "No user found", modifier = Modifier.weight(1f))
            TextButton(onClick = { onAddNewUserClicked() }
            ) {
                TitleView(title = "Add as new user")
            }
        }
    }
}

private fun userGenderShort(userProfile: SubUserProfile): String {
    return when(userProfile.gender?.toUpperCase()) {
        "MALE" -> "M"
        "FEMALE" -> "F"
        "OTHER" -> "O"
        else -> ""
    }
}

private fun dobChanged(userProfile: SubUserProfile): String {
    val monthShort = listOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")
    val dob = userProfile.dob.split("/")
    val monthIndex = dob[0].toInt()
    val year = dob[1].takeLast(2)
    return monthShort[monthIndex] + ", " + year
}


@Composable
fun SearchResultUserCard(userProfile: SubUserProfile){
    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .background(defCardDark, shape = RoundedCornerShape(8.dp))
    ) {
        Row(
            Modifier
                .padding(8.dp)
                .testTag(HomePageTags.shared.getUserTag(userProfile)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column{
                UserImageView(imageUrl = userProfile.profile_pic_url, size = 55.dp){}
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Row{
                    LabelWithoutIconView(title = userProfile.first_name.capitalize(Locale.ROOT))
                    Spacer(modifier = Modifier.width(5.dp))
                    LabelWithoutIconView(title = userProfile.last_name.capitalize(Locale.ROOT))
                }
                Row{
                    LabelWithIconView(title = userGenderShort(userProfile),icon = if(checkIsMale(userProfile.gender)) Icons.Default.Male else Icons.Default.Female)
                    Spacer(modifier = Modifier.width(5.dp))
                    LabelWithIconView(title = dobChanged(userProfile), icon = Icons.Default.Cake)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                when(userProfile.phone.isEmpty()){
                    true-> ""
                    false -> LabelWithIconView(title = "+91"+userProfile.phone, icon = Icons.Default.Phone )
                }
                Spacer(modifier = Modifier.width(5.dp))
                LabelWithIconView(title = MainActivity.adminDBRepo.getHeightBasedOnUnitSet(userProfile.height.toDouble()), icon = Icons.Default.Height)
            }
        }
    }
}

fun performSearch(query: String): List<SubUserProfile> {
    if (query.isEmpty()) return emptyList()
    val searchResult = MainActivity.adminDBRepo.subUserSearchProfileListState.value
    val userList : ArrayList<SubUserProfile> = ArrayList()
    for (profile in searchResult) if(profile.first_name.isNotEmpty()) userList.add(profile)
    if(userList.isEmpty()) {
        CoroutineScope(Dispatchers.Default).launch {
            MainActivity.adminDBRepo.searchUserByQuery(query.first().toString())
        }
        return emptyList()
    }else{
        var refetch = false
        for (profile in searchResult){
            if(!profile.first_name.first().equals(query) || !profile.phone.first().equals(query)){
                refetch = true
            }
        }
        if(refetch) CoroutineScope(Dispatchers.Default).launch {
            MainActivity.adminDBRepo.searchUserByQuery(query.first().toString())
        }
    }

    return userList.filter { user ->
        val fullname = user.first_name + user.last_name
        user.first_name.startsWith(query, ignoreCase = true) || user.phone.startsWith(query, ignoreCase = true) || fullname.removePrefix("").startsWith(query, ignoreCase = true)
    }
}

//TextViews
@Composable
fun TitleView(title : String){
    Text(text = title, fontFamily = FontFamily(Font(R.font.roboto_bold)))
}



@Composable
fun TitleViewWithCancelBtn(title: String, onCancelClick : () -> Unit){
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween){
        BoldTextView(title = title, fontSize = 18)//update font size
        Box(modifier = Modifier.size(30.dp)) {//change size of icon
            IconButton(onClick = { onCancelClick() }){
                Icon(
                    imageVector = Icons.Outlined.Cancel,
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = "cancelIcon"
                )
            }
        }
    }
}


@Composable
fun NameTitleView(title : String, color: Color){
    Text(text = title.uppercase(Locale.ROOT), color = color ,fontFamily = FontFamily(Font(R.font.roboto_bold)))
}

@Composable
fun SubTitleView(title : String){
    Text(text = title, fontFamily = FontFamily(Font(R.font.roboto_medium)))
}

@Composable
fun NormalTextView(title : String){
    Text(text = title, fontFamily = FontFamily(Font(R.font.roboto_regular)))
}

@Composable
fun BoldTextView(title : String, fontSize: Int = 14, textColor: Color = Color.Black  ){
    Text(text = title,fontFamily = FontFamily(Font(R.font.roboto_bold)),fontSize = fontSize.sp, color = textColor)
}

@Composable
fun MediumTextView(title : String, fontSize: Int){
    Text(text = title, fontFamily = FontFamily(Font(R.font.roboto_medium)),fontSize = fontSize.sp)
}

@Composable
fun RegularTextView(title : String, fontSize: Int = 14, textColor: Color = Color.Black, textDecoration: TextDecoration? = null, modifier: Modifier= Modifier){
    Text(text = title, fontFamily = FontFamily(Font(R.font.roboto_regular)), fontSize = fontSize.sp, color = textColor, textDecoration = textDecoration, modifier=modifier)
}
@Composable
fun RegularTextView(title : String, fontSize: Int = 14, textColor: Color = Color.Black, textDecoration: TextDecoration? = null){
    Text(text = title, fontFamily = FontFamily(Font(R.font.roboto_regular)), fontSize = fontSize.sp, color = textColor, textDecoration = textDecoration)
}

@Composable
fun LabelWithoutIconView(title: String,textSize: Int = 14){
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        MediumTextView(title = title, fontSize = textSize)
    }
}

@Composable
fun LabelWithIconView(title: String, textSize: Int = 14, icon: ImageVector){
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "nameIcon",
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        RegularTextView(title = title, fontSize = textSize)
    }
}

//ImageViews

@Composable
fun UserImageView(imageUrl : String?, size : Dp, onImageClick : () -> Unit){

    when(imageUrl){

        "", null-> {
            MainActivity.adminDBRepo.getProfile(MainActivity.authRepo.getAdminUID())
            DefProfileIcon(onImageClick = { onImageClick() }, size = size)
        }

        "Not-given", "Not-Given" -> DefProfileIcon(onImageClick = { onImageClick() }, size = size)

        else -> ProfileIconWithUrl(imageUrl = imageUrl, size = size) { onImageClick() }

    }
}

@Composable
fun ProfileIconWithUrl(imageUrl : String?, size : Dp, onImageClick : () -> Unit){
    val profileUrlWithTimestamp = "$imageUrl?t=$timestamp"
    val painter = rememberImagePainter(data = profileUrlWithTimestamp)
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
            .size(size)
//            .rotate(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) 90f else 0f)
            .border(2.dp, defDark, CircleShape)
            .clip(CircleShape)
            .clickable {
                onImageClick()
            },
        contentScale = ContentScale.FillHeight
    )
}

@Composable
fun DefProfileIcon(onImageClick : () -> Unit, size : Dp){
    Image(
        painter = painterResource(R.drawable.profile_icon),
        contentDescription = "profilePic",
        modifier = Modifier
            .size(size)
            .border(2.dp, defDark, CircleShape)
            .clip(CircleShape)
            .clickable {
                onImageClick()
            }
    )
}

// UserHomeScreen Items
@Composable
fun VitalCard(title: String, icon: ImageVector, background: Color, columnScope: (ColumnScope) -> Unit){
    Card(modifier = Modifier
        .size(width = 180.dp, height = 110.dp)
        .background(defCardDark, shape = RoundedCornerShape(8.dp)), shape = RoundedCornerShape(15.dp))
    {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(background)){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(imageVector = icon, contentDescription ="vital-icon",Modifier.size(15.dp) )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    columnScope(this)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTextField(
    textInput: String,
    onChangeInput: (String) -> Unit,
    labelText: String,
    keyboard: KeyboardType,
    error: Boolean,
    enable: Boolean? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    iconAction: (() -> Unit)? = null,
    iconName: String? = null,
    iconImage: ImageVector? = null,
    TestTag: String
) {
    OutlinedTextField(
        value = textInput,
        onValueChange = { newValue -> onChangeInput(newValue) },
        label = { RegularTextView(title = labelText) },

        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboard,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(TestTag),
        shape = RoundedCornerShape(5.dp),
        isError = error,
        singleLine = true,
        enabled = enable ?: true,
        trailingIcon= {
            iconAction?.let {
                IconButton(onClick = it) {
                    if (iconImage != null) {
                        Icon(imageVector = iconImage, contentDescription = iconName )
                    }
                }
            }
        },
        visualTransformation = visualTransformation,
        textStyle = TextStyle(fontFamily = FontFamily(Font(R.font.roboto_regular)), fontSize = 14.sp )
    )
}

//TopBar
@Composable
fun TopBarWithBackBtn(onBackBtnPressed: () -> Unit){
    Row(
        Modifier
            .height(55.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { onBackBtnPressed() }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.back_btn_icon),
                contentDescription = "BackBtn"
            )
        }
    }
}

@Composable
fun TopBarWithCancelBtn(onCancelClick: () -> Unit){
    Row(
        Modifier
            .height(55.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(onClick = { onCancelClick() }) {
            Icon(
                imageVector = Icons.Default.Cancel,
                contentDescription = "BackBtn"
            )
        }
    }
}

@Composable
fun TopBarWithBackEditBtn(user: SubUserProfile,onBackBtnPressed: () -> Unit, onStartBtnPressed: () -> Unit, onEditBtnClicked : () -> Unit, onConnectionBtnClicked: () -> Unit, onExitBtnClicked : () -> Unit){
    Row(
        Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(Color.LightGray)
        ) {
            UserImageView(imageUrl = user.profile_pic_url, size = 60.dp){}
        }

        Column(
            modifier = Modifier
                .padding(horizontal=10.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.Start
        )
        {
            LabelWithoutIconView(title = formatTitle(user.first_name, user.last_name))
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                LabelWithoutIconView(title = getAge(user))
                Spacer(modifier = Modifier.padding(3.dp))
                LabelWithoutIconView(title = user.gender)
            }
        }

//        Box(modifier = Modifier.weight(1f).testTag(UserHomePageTags.shared.backBtn)){
//            IconButton(onClick = { onBackBtnPressed() }) {
//                Icon(
//                    imageVector = ImageVector.vectorResource(id = R.drawable.back_btn_icon),
//                    contentDescription = "BackBtn"
//                )
//            }
//        }
        Spacer(modifier = Modifier.weight(1f))

        Box(
            Modifier
                .size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            ActionBtnUser(size = 48.dp, icon = Icons.Default.PlayArrow) {
                onStartBtnPressed()
            }
        }


        Spacer(modifier = Modifier.width(16.dp))

        Box(
            Modifier
                .size(48.dp)
                .testTag(UserHomePageTags.shared.connectionBtn),
            contentAlignment = Alignment.Center
        ) {
            ConnectionActionBtn(isConnected = MainActivity.pc300Repo.connectionStatus.value, 48.dp) {
                onConnectionBtnClicked()
            }
        }

//        Spacer(modifier = Modifier.width(15.dp))
//
//        Box(
//            Modifier.testTag(UserHomePageTags.shared.editBtn)
//                .size(30.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            ActionBtn(size = 22.dp, icon = Icons.Default.Edit) {
//                onEditBtnClicked()
//            }
//        }
//
        Spacer(modifier = Modifier.width(16.dp))

        Box(
            Modifier
                .size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            ActionBtnUser(size = 48.dp, icon = Icons.Default.ExitToApp) {
                onBackBtnPressed()
            }
        }
    }
    Divider(
        color = Color.LightGray,
    )
}

@Composable
fun ActionBtnUser( size : Dp,icon: ImageVector, onIconClick : () -> Unit){
    Box( modifier = Modifier
        .border(2.dp, Color.Black, CircleShape),
        contentAlignment = Alignment.Center) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(10.dp)) {
            IconButton(onClick = { onIconClick() },
            ) {
                Icon(imageVector = icon, contentDescription = "icon", Modifier.size(size))
            }
        }
    }
}

data class VisitCard(val date: String, val place: String)


@Composable
fun VisitSummaryCards(navHostController: NavHostController,user:SubUserProfile, onBtnClick: (SubUserProfile) -> Unit) {

    var visitSummaryList= remember {
        mutableStateListOf<VisitCard>()
    }
    val sessionsList = MainActivity.subUserRepo.sessions.value.filter { it.sessionId.isNotEmpty() }

    val sessionsList1 = MainActivity.subUserRepo.sessions1.value.filter { it.sessionId.isNotEmpty() }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            RegularTextView(title = "Visits Summary",fontSize=18)
            IconButton(onClick = {
                onBtnClick(user)
            }) {
                Icon(
                    imageVector = Icons.Filled.AddCircleOutline,
                    contentDescription = "Add Button",
                    modifier=Modifier.size(30.dp),
                )
            }
        }

        sessionsList.map { item ->
            sessionsList1.map {
                if(item.sessionId == it.sessionId)
                {
                    VisitSummaryCard(navHostController = navHostController,item, it, {index ->
                     // on expand clicked ->
                        MainActivity.sessionRepo.scrollToIndex.value = index
                    }, sessionsList1.indexOf(it))
                }
            }
        }
    }
}

@Composable
fun VisitSummaryCard(
    navHostController: NavHostController,
    session: Session,
    cardExpansionState:SubUserDBRepository.Session1,
    onExpandClick : (Int) -> Unit,
    index : Int
) {
    Log.i("expand", cardExpansionState.isExpanded.toString())
    val expandState= remember { mutableStateOf(cardExpansionState.isExpanded) }

    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                cardExpansionState.isExpanded = !cardExpansionState.isExpanded
                expandState.value = cardExpansionState.isExpanded
                onExpandClick(index)
//                Log.i("expand", cardExpansionState.isExpanded.toString())
            },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(Color(0xffdae3f3))
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val postalCodeParsed = session.location.split(",")
                var pc = ""
                if (postalCodeParsed.size > 2) {
                    pc = postalCodeParsed[1]
                }
                Text(
                    text = "${session.date} ${session.time} $pc",
                    fontFamily = FontFamily(Font(R.font.roboto_regular)),
                    fontSize = 16.sp,
                    maxLines= if(cardExpansionState.isExpanded) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis,
                    color=Color.Black,
                    modifier= Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (cardExpansionState.isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Expand",
//                    modifier = Modifier.clickable {
////                        expanded = !expanded
////                        visitSummaryViewModel.expandedStateMap[session.sessionId]=expanded
//                        cardExpansionState.isExpanded=!cardExpansionState.isExpanded
//                        expandState.value = cardExpansionState.isExpanded
//                    }
                )
            }
        }
    }

//    Log.i("expand", cardExpansionState.isExpanded.toString())
    if (expandState.value) {
        VisitDetails(navHostController,session)
    }
}

@Composable
fun VisitDetails(navHostController: NavHostController,session: Session){

    Column {

        VitalBox(session)

        Spacer(modifier = Modifier.height(8.dp))

        val parsedTextPE = session.PhysicalExamination.split("-:-")

        val parsedTextLR = session.LabotryRadiology.split("-:-")

        val parsedTextIP = session.ImpressionPlan.split("-:-")

        val parsedPFList = MainActivity.sessionRepo.parseImageList(parsedTextPE.last())

        val parsedLRList = MainActivity.sessionRepo.parseImageList(parsedTextLR.last())

        val parsedIPList = MainActivity.sessionRepo.parseImageList(parsedTextIP.last())

        CardWithHeadingContentAndAttachment(
            navHostController = navHostController,
            title = "Physical Examination",
            value = if(parsedTextPE.isNotEmpty()) parsedTextPE.first() else "",
            onClick = {

                val selectedSession = MainActivity.sessionRepo.selectedsession

                val parsedText = selectedSession?.PhysicalExamination?.split("-:-")

                MainActivity.subUserRepo.updateTempPopUpText(parsedText?.first() ?: "")

                MainActivity.sessionRepo.clearImageList()
                MainActivity.sessionRepo.selectedsession = session
                isPESetUpDone = false
                isFromVital = false
                navHostController.navigate(Destination.PhysicalExaminationScreen.routes)
            },
            isAttachment = parsedPFList.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(6.dp))

        CardWithHeadingContentAndAttachment(
            navHostController = navHostController,
            title = "Laboratory & Radiology",
            value = if(parsedTextLR.isNotEmpty()) parsedTextLR.first() else "",
            onClick = {

                val selectedSession = MainActivity.sessionRepo.selectedsession

                val parsedText = selectedSession?.LabotryRadiology?.split("-:-")

                MainActivity.subUserRepo.updateTempPopUpText(parsedText?.first() ?: "")

                MainActivity.sessionRepo.clearImageList()
                MainActivity.sessionRepo.selectedsession = session
                isLRSetUpDone = false
                isFromVital = false
                navHostController.navigate(Destination.LaboratoryRadiologyScreen.routes)
            },
            isAttachment = parsedLRList.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(6.dp))

        CardWithHeadingContentAndAttachment(
            navHostController = navHostController,
            title = "Impression & Plan",
            value = if(parsedTextIP.isNotEmpty()) parsedTextIP.first() else "",
            onClick = {

                val selectedSession = MainActivity.sessionRepo.selectedsession

                val parsedText = selectedSession?.ImpressionPlan?.split("-:-")

                MainActivity.subUserRepo.updateTempPopUpText(parsedText?.first() ?: "")

                MainActivity.sessionRepo.clearImageList()
                MainActivity.sessionRepo.selectedsession = session
                isIPSetUpDone = false
                isFromVital = false
                navHostController.navigate(Destination.ImpressionPlanScreen.routes) },
            isAttachment = parsedIPList.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(6.dp))
        Divider(thickness = 2.dp, color = Color.LightGray, modifier = Modifier
            .padding(16.dp))
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun SessionBox(title: String, value : String, iconId : Int, unit: String){

    Card(modifier = Modifier
        .size(width = 95.dp, height = 75.dp)
//        .width(100.dp)
        ,
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            if(value.isNullOrEmpty()) Color(0x40DAE3F3) else Color(0xFFDAE3F3)
        )
    )
    {
        Box(modifier = Modifier
            .fillMaxSize()){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 5.dp, vertical = 7.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween)
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    RegularTextView(title = title, fontSize = 16, textColor = if(value.isNullOrEmpty())Color(0x80000000) else Color.Black)

//                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = iconId),
                        contentDescription ="weightIcon",Modifier.size(15.dp) )
                }
//                Spacer(modifier = Modifier.height(7.dp))
                RegularTextView(title = value.ifEmpty { "" }, fontSize = 18)
//                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(end = 5.dp), horizontalArrangement = Arrangement.End) {
                    RegularTextView(title = if(value.isNotEmpty()) unit else "", fontSize = 10)
                }
            }
        }
    }
}

@Composable
fun VitalBox(sess: Session){
    Row() {
        Column(modifier=Modifier.padding(8.dp)) {
            RegularTextView(title = "Vitals", fontSize = 18)

            Spacer(modifier = Modifier.padding(4.dp))

            val tempInC = sess.temp.substringBefore("°C").toDoubleOrNull()
            val sysValue = sess.sys?.replace(Regex("[^\\d.]"), "")?.toIntOrNull()
            val diaValue = sess.dia?.replace(Regex("[^\\d.]"), "")?.toIntOrNull()
            val hrValue = sess.heartRate?.replace(Regex("[^\\d.]"), "")?.toIntOrNull()
            val spo2Value = sess.spO2?.replace(Regex("[^\\d.]"), "")?.toIntOrNull()
//            val bmiValue = sess.weight?.replace(Regex("[^\\d.]"), "")?.toDoubleOrNull()
//            val bodyFatValue = sess.bodyFat?.replace(Regex("[^\\d.]"), "")?.toDoubleOrNull()
//            val tempValue = sess.temp?.replace(Regex("[^\\d.]"), "")?.toDoubleOrNull()

            val sys = sysValue?.toString() ?: ""
            val dia = diaValue?.toString() ?: ""
            val hr = hrValue?.toString() ?: ""
            val spo2 = spo2Value?.toString() ?: ""
//            val bmi = bmiValue?.toString() ?: ""
//            val bodyFat = bodyFatValue?.toString() ?: ""
//            val temp = tempValue?.toString() ?: ""

            Column(
                Modifier
                    .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SessionBox(
                        title = "BP",
                        value = if ((sys.isNullOrEmpty() && dia.isNullOrEmpty())) "${sys}${dia}" else "${sys}/${dia}",
                        iconId = R.drawable.bp,
                        unit = "mmHg"
                    )

                    SessionBox(
                        title = "HR",
                        value = hr,
                        iconId = R.drawable.hr,
                        unit = "bpm"
                    )

                    SessionBox(
                        title = "SpO2",
                        value = spo2,
                        iconId = R.drawable.userspo,
                        unit = "%"
                    )


                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SessionBox(
                        title = "Temp",
                        value = MainActivity.adminDBRepo.getTempBasedOnUnit(tempInC),
                        iconId = R.drawable.temp,
                        unit = "°F"
                    )

                    val selectedUser = MainActivity.adminDBRepo.getSelectedSubUserProfile()
                    SessionBox(
                        title = "Weight",
                        value = sess.weight,
                        //MainActivity.doctorAddPatientRepo.updateKgToLbs(sess.weight),
                        iconId = R.drawable.weightuser,
                        unit = "lbs"
                    )

                    val result = sess.ecgFileLink.split("_")
                    if (result.size == 6) {
                        SessionBox(
                            title = "ECG",
                            value = result.last(),
                            iconId = R.drawable.ecg,
                            unit = ""
                        )
                    } else {
                        SessionBox(
                            title = "ECG",
                            value = "",
                            iconId = R.drawable.ecg,
                            unit = ""
                        )
                    }
                }
            }
        }}
}

@Composable
fun CardWithHeadingContentAndAttachment(navHostController: NavHostController,title:String, value: String, onClick: () -> Unit, isAttachment: Boolean) {
    Column(
        horizontalAlignment=Alignment.Start
    ) {
        RegularTextView(title = title, fontSize = 18,modifier=Modifier.padding(horizontal=8.dp))
        Surface(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    onClick()
                },
            shape = RoundedCornerShape(16.dp),
            color = Color(0xffdae3f3),
//            color = Color(0xFFBFEFFF),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = value,
                        fontFamily = FontFamily(Font(R.font.roboto_regular)),
                        fontSize = 16.sp,
                        color = Color.Black,
                        maxLines = 3, // Set the maximum number of lines
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(50.dp),
                        contentAlignment = Alignment.Center
                    ){
                        if(isAttachment){
                            Icon(imageVector = Icons.Filled.AttachFile, contentDescription = "Attachment Icon")
                        }
                    }
                }
            }
        }
    }
}

//@Composable
//fun TopBarWithBackEditBtn(user: SubUserProfile,onBackBtnPressed: () -> Unit, onEditBtnClicked : () -> Unit, onConnectionBtnClicked: () -> Unit, onExitBtnClicked : () -> Unit){
//    Row(
//        Modifier
//            .height(55.dp)
//            .padding(horizontal = 5.dp)
//            .fillMaxWidth(),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Box(modifier = Modifier
//            .size(48.dp)
//            .clip(CircleShape)
//            .background(Color.LightGray)
//        ) {
//            UserImageView(imageUrl = user.profile_pic_url, size = 48.dp
//            ){}
//        }
//
//        Column(modifier = Modifier
//            .padding(5.dp)
//            .weight(1f)) {
//            LabelWithoutIconView(title = formatTitle(user.first_name, user.last_name))
//            Spacer(modifier = Modifier.height(5.dp))
//            Row {
//                LabelWithoutIconView(title = getAge(user))
//                Spacer(modifier = Modifier.width(2.dp))
//                LabelWithoutIconView(title = user.gender)
//            }
//        }
//
////        Box(modifier = Modifier.weight(1f).testTag(UserHomePageTags.shared.backBtn)){
////            IconButton(onClick = { onBackBtnPressed() }) {
////                Icon(
////                    imageVector = ImageVector.vectorResource(id = R.drawable.back_btn_icon),
////                    contentDescription = "BackBtn"
////                )
////            }
////        }
//
//        Spacer(modifier = Modifier.width(15.dp))
//
//        Box(
//            Modifier
//                .size(48.dp).testTag(UserHomePageTags.shared.connectionBtn),
//            contentAlignment = Alignment.Center
//        ) {
//            ConnectionActionBtn(isConnected = MainActivity.pc300Repo.connectionStatus.value, 48.dp) {
//                onConnectionBtnClicked()
//            }
//        }
//
////        Spacer(modifier = Modifier.width(15.dp))
////
////        Box(
////            Modifier.testTag(UserHomePageTags.shared.editBtn)
////                .size(30.dp),
////            contentAlignment = Alignment.Center
////        ) {
////            ActionBtn(size = 22.dp, icon = Icons.Default.Edit) {
////                onEditBtnClicked()
////            }
////        }
////
////        Spacer(modifier = Modifier.width(15.dp))
////
////        Box(
////            Modifier
////                .size(30.dp),
////            contentAlignment = Alignment.Center
////        ) {
////            ActionBtn(size = 22.dp, icon = Icons.Default.ExitToApp) {
////                onBackBtnPressed()
////            }
////        }
//        Spacer(modifier = Modifier.width(10.dp))
//    }
//}

//@Composable
//fun TopBarWithBackEditBtn(onBackBtnPressed: () -> Unit, onEditBtnClicked : () -> Unit, onConnectionBtnClicked: () -> Unit, onExitBtnClicked : () -> Unit){
//    Row(
//        Modifier
//            .height(55.dp)
//            .padding(horizontal = 5.dp)
//            .fillMaxWidth(),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//
//        Box(modifier = Modifier.weight(1f).testTag(UserHomePageTags.shared.backBtn)){
//            IconButton(onClick = { onBackBtnPressed() }) {
//                Icon(
//                    imageVector = ImageVector.vectorResource(id = R.drawable.back_btn_icon),
//                    contentDescription = "BackBtn"
//                )
//            }
//        }
//
//        Box(
//            Modifier
//                .size(30.dp).testTag(UserHomePageTags.shared.connectionBtn),
//            contentAlignment = Alignment.Center
//        ) {
//            ConnectionActionBtn(isConnected = MainActivity.pc300Repo.connectionStatus.value, 22.dp) {
//                onConnectionBtnClicked()
//            }
//        }
//
//        Spacer(modifier = Modifier.width(15.dp))
//
//        Box(
//            Modifier.testTag(UserHomePageTags.shared.editBtn)
//                .size(30.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            ActionBtn(size = 22.dp, icon = Icons.Default.Edit) {
//                onEditBtnClicked()
//            }
//        }
//
//        Spacer(modifier = Modifier.width(15.dp))
//
//        Box(
//            Modifier
//                .size(30.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            ActionBtn(size = 22.dp, icon = Icons.Default.ExitToApp) {
//                onBackBtnPressed()
//            }
//        }
//        Spacer(modifier = Modifier.width(10.dp))
//    }
//}

@Composable
fun TopBarWithBackSaveBtn(onSaveVisible : Boolean, onBackBtnPressed: () -> Unit, onSaveBtnClicked : () -> Unit){
    Row(
        Modifier
            .height(55.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { onBackBtnPressed() }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.back_btn_icon),
                contentDescription = "BackBtn"
            )
        }
        if(onSaveVisible){
            Box(
                Modifier
                    .width(60.dp)
                    .testTag(AddEditUserPageTags.shared.saveBtn)
                    .height(40.dp)
                    .background(
                        color = defCardDark,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = { onSaveBtnClicked() }) {
                    Text(
                        text = "Save",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF397EF5)
                    )
                }
            }
        }
    }
}

@Composable
fun CheckInternet(context: Context) {
    var isConnected by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isConnected = withContext(Dispatchers.IO) {
            isConnectedToInternet(context).single()
        }
    }

    if (!isConnected) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(text = "No Internet Connection") },
            text = { Text(text = "Please check your internet connection and try again.") },
            confirmButton = {
                Button(onClick = {
                    GlobalScope.launch {
                        isConnected = withContext(Dispatchers.IO) {
                            isConnectedToInternet(context).single()
                        }
                    }
                }) {
                    Text(text = "Check Connection")
                }
            }
        )
    }
}



fun isConnectedToInternet(context: Context): Flow<Boolean> = callbackFlow {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val isInternetAvailable = connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true
    if (isInternetAvailable) {
        CoroutineScope(Dispatchers.IO).launch {
            val hasInternetAccess = try {
                val timeoutMs = 1500 // Timeout for the server ping
                val socket = Socket()
                val socketAddress = InetSocketAddress("8.8.8.8", 53) // Google DNS
                socket.connect(socketAddress, timeoutMs)
                socket.close()
                true
            } catch (e: IOException) {
                false
            }
            send(hasInternetAccess)
        }
    } else {
        send(false)
    }
    awaitClose()
}

@Composable
fun splashLogo(){
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Row(modifier = Modifier
            .height(40.dp)
            .fillMaxWidth()
            .testTag(LoginTags.shared.splashScreen),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.applogo),
                contentDescription = "logo"
            )
        }
    }
}

@Composable
fun ConfirmAdminSignInScreen(navHostController: NavHostController) {
    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        Column {
            BackClickView(navHostController = navHostController)
            Spacer(modifier = Modifier.height(15.dp))
            OTPView()
        }
    }
}

@Composable
fun BackClickView(navHostController: NavHostController){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            navHostController.navigate(Destination.Login.routes)
        }, modifier = Modifier.size(36.dp)) {
            Icon(imageVector = ImageVector.vectorResource(id = R.drawable.back_btn), contentDescription = "Logout")
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OTPView() {
    val numFields = 6
    val otp = remember { mutableStateListOf<String>(*Array(numFields) { "" }) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Enter OTP", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(numFields) { i ->
                OTPDigitField(
                    value = otp[i],
                    onValueChange = {
                        if (it.length <= 1) {
                            otp[i] = it
                            if (i < numFields - 1 && it.isNotEmpty()) {
                                // Move focus to the next field
                                val nextFieldRequester = otpTextFieldRequesters[i + 1]
                                nextFieldRequester?.requestFocus()
                            } else if (i == numFields - 1 && otp.all { it.length == 1 }) {
                                // Check if all fields are filled
//                                onOTPCompleted(otp.joinToString(separator = ""))
//                                keyboardController?.hideSoftwareKeyboard()
                            }
                        }
                    },
                    imeAction = if (i < numFields - 1) ImeAction.Next else ImeAction.Done,
                    focusRequester = otpTextFieldRequesters[i]
                )
            }
        }
    }
}

val otpTextFieldRequesters = Array(6) { FocusRequester() }

@Composable
fun OTPDigitField(
    value: String,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction,
    focusRequester: FocusRequester
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = imeAction),
        maxLines = 1,
        modifier = Modifier
            .width(64.dp)
            .height(64.dp),
        textStyle = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
        visualTransformation = PasswordVisualTransformation()
    )
}

