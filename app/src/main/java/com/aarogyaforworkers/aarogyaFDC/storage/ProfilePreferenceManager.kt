package com.aarogyaforworkers.aarogyaFDC.storage

import android.content.Context
import androidx.compose.foundation.rememberBasicTooltipState
import androidx.compose.runtime.mutableStateOf

class ProfilePreferenceManager private constructor(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("tempProfile", Context.MODE_PRIVATE)
    private val fname = "fname"
    private val lname = "lname"
    private val month = "month"
    private val monthInt = "monthInt"
    private val year = "year"
    private val gender = "gender"
    private val height = "height"
    private val phone = "phone"
    private val address = "address"
    private val selectedCountry = "selectedCountry"


    fun saveFname(value : String){
        // Use the edit() method of the shared preferences object to create a new editor and store the selected temperature unit using the putInt() method
        // Apply the changes using the apply() method to save the selected temperature unit to the app's shared preferences
        sharedPreferences.edit().putString(fname, value).apply()
    }

    fun getFname() : String{
        // Use the getInt() method of the shared preferences object to retrieve the selected temperature unit, or 0 if no unit has been selected
        return sharedPreferences.getString(fname, "").toString()
    }

    fun saveLname(value : String){
        // Use the edit() method of the shared preferences object to create a new editor and store the selected temperature unit using the putInt() method
        // Apply the changes using the apply() method to save the selected temperature unit to the app's shared preferences
        sharedPreferences.edit().putString(lname, value).apply()
    }

    fun getLname() : String{
        // Use the getInt() method of the shared preferences object to retrieve the selected temperature unit, or 0 if no unit has been selected
        return sharedPreferences.getString(lname, "").toString()
    }

    fun saveMonth(value : String){
        // Use the edit() method of the shared preferences object to create a new editor and store the selected temperature unit using the putInt() method
        // Apply the changes using the apply() method to save the selected temperature unit to the app's shared preferences
        sharedPreferences.edit().putString(month, value).apply()
    }

    fun getmonth() : String{
        // Use the getInt() method of the shared preferences object to retrieve the selected temperature unit, or 0 if no unit has been selected
        return sharedPreferences.getString(month, "Month").toString()
    }

    fun saveMonthInt(value : String){
        // Use the edit() method of the shared preferences object to create a new editor and store the selected temperature unit using the putInt() method
        // Apply the changes using the apply() method to save the selected temperature unit to the app's shared preferences
        sharedPreferences.edit().putString(monthInt, value).apply()
    }

    fun getmonthInt() : String{
        // Use the getInt() method of the shared preferences object to retrieve the selected temperature unit, or 0 if no unit has been selected
        return sharedPreferences.getString(monthInt, "0").toString()
    }


    fun saveYear(value : String){
        // Use the edit() method of the shared preferences object to create a new editor and store the selected temperature unit using the putInt() method
        // Apply the changes using the apply() method to save the selected temperature unit to the app's shared preferences
        sharedPreferences.edit().putString(year, value).apply()
    }

    fun getyear() : String{
        // Use the getInt() method of the shared preferences object to retrieve the selected temperature unit, or 0 if no unit has been selected
        return sharedPreferences.getString(year, "Year").toString()
    }

    fun savegender(value : String){
        // Use the edit() method of the shared preferences object to create a new editor and store the selected temperature unit using the putInt() method
        // Apply the changes using the apply() method to save the selected temperature unit to the app's shared preferences
        sharedPreferences.edit().putString(gender, value).apply()
    }

    fun getgender() : String{
        // Use the getInt() method of the shared preferences object to retrieve the selected temperature unit, or 0 if no unit has been selected
        return sharedPreferences.getString(gender, "").toString()
    }

    fun saveheight(value : String){
        // Use the edit() method of the shared preferences object to create a new editor and store the selected temperature unit using the putInt() method
        // Apply the changes using the apply() method to save the selected temperature unit to the app's shared preferences
        sharedPreferences.edit().putString(height, value).apply()
    }

    fun getheight() : String{
        // Use the getInt() method of the shared preferences object to retrieve the selected temperature unit, or 0 if no unit has been selected
        return sharedPreferences.getString(height, "").toString()
    }

    fun savePhone(value : String){
        // Use the edit() method of the shared preferences object to create a new editor and store the selected temperature unit using the putInt() method
        // Apply the changes using the apply() method to save the selected temperature unit to the app's shared preferences
        sharedPreferences.edit().putString(phone, value).apply()
    }

    fun getPhone() : String{
        // Use the getInt() method of the shared preferences object to retrieve the selected temperature unit, or 0 if no unit has been selected
        return sharedPreferences.getString(phone, "").toString()
    }

    fun saveaddress(value : String){
        // Use the edit() method of the shared preferences object to create a new editor and store the selected temperature unit using the putInt() method
        // Apply the changes using the apply() method to save the selected temperature unit to the app's shared preferences
        sharedPreferences.edit().putString(address, value).apply()
    }

    fun getaddress() : String{
        // Use the getInt() method of the shared preferences object to retrieve the selected temperature unit, or 0 if no unit has been selected
        return sharedPreferences.getString(address, "").toString()
    }

    fun saveSelectedCountry(value : String){
        // Use the edit() method of the shared preferences object to create a new editor and store the selected temperature unit using the putInt() method
        // Apply the changes using the apply() method to save the selected temperature unit to the app's shared preferences
        sharedPreferences.edit().putString(selectedCountry, value).apply()
    }

    fun getselectedCountry() : String{
        // Use the getInt() method of the shared preferences object to retrieve the selected temperature unit, or 0 if no unit has been selected
        return sharedPreferences.getString(selectedCountry, "91").toString()
    }

    fun reset(){
        saveFname("")
        saveLname("")
        savePhone("")
        saveMonth("Month")
        saveMonthInt("0")
        saveYear("Year")
        saveheight("")
        saveaddress("")
        saveSelectedCountry("91")
        savegender("")
        isProfileFilled.value = false
    }

    private val isProfileFilled = mutableStateOf(false)

    fun getisProfileFilled(): Boolean {
        isProfileFilled.value = getFname().isNotEmpty() ||
                getLname().isNotEmpty() ||
                getPhone().isNotEmpty() ||
                getmonth() != "Month" ||
                getmonthInt() != "0" ||
                getyear() != "Year" ||
                getheight().isNotEmpty() ||
                getaddress().isNotEmpty() ||
                getgender().isNotEmpty()
        return isProfileFilled.value
    }


    companion object {

        @Volatile
        private var instance: ProfilePreferenceManager? = null

        // Get instance of SettingPreferenceManager to access the functions
        fun getInstance(context: Context): ProfilePreferenceManager =
            instance ?: synchronized(this) {
                instance ?: ProfilePreferenceManager(context).also { instance = it }
            }
    }



}