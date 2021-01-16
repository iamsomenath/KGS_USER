package com.app.kgs_user.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SessionManager @SuppressLint("CommitPrefEdits")

constructor(internal var _context: Context) {

    // Shared Preferences
    internal var pref: SharedPreferences

    internal var editor: Editor

    // Shared pref mode
    internal var PRIVATE_MODE = 0

    val isLoggedIn: Boolean
        get() = pref.getBoolean(KEY_IS_LOGGED_IN, false)

    val latitude: String?
        get() = pref.getString(KEY_LAT, "")

    val longitude: String?
        get() = pref.getString(KEY_LANG, "")

    val email: String?
        get() = pref.getString(KEY_EMAIL, "")

    val name: String?
        get() = pref.getString(KEY_NAME, "")

    val username: String?
        get() = pref.getString(KEY_UNAME, "")

    val mobile: String?
        get() = pref.getString(KEY_PHONE, "")

    val address: String?
        get() = pref.getString(KEY_ADDR, "")

    val getLocalAddress: String?
        get() = pref.getString(FULL_ADDR, "")

    val getLocation: String?
        get() = pref.getString(LOCATION, "")

    val getPincode: String?
        get() = pref.getString(PINCODE, "")

    val getId: String?
        get() = pref.getString(KEY_ID, "")

    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    fun setLogin(
        isLoggedIn: Boolean, id: String, name: String, email: String, phone: String
    ) {

        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.putString(KEY_ID, id)
        editor.putString(KEY_NAME, name)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_PHONE, phone)
        // commit changes

        editor.commit()
    }

    fun destroyLoginSession() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false)
        editor.clear()
        // commit changes
        editor.commit()
    }

    fun setAddress(address: String, lat: String, lan: String){
        editor.putString(KEY_ADDR, address)
        editor.putString(KEY_LANG, lan)
        editor.putString(KEY_LAT, lat)
        editor.commit()
    }

    fun saveArrayList(list: ArrayList<String>) {
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString("SAMPLE_ID", json)
        editor.apply()
    }

    fun getArrayList(): ArrayList<String> {
        val gson = Gson()
        val json = pref.getString("SAMPLE_ID", null)
        val type = object : TypeToken<ArrayList<String>>() {}.type
        return gson.fromJson(json, type)
    }

    fun setLocalAddress(addr: String, loc: String, pin: String) {
        editor.putString(FULL_ADDR, addr)
        editor.putString(LOCATION, loc)
        editor.putString(PINCODE, pin)
        editor.apply()
    }

    companion object {
        // LogCat tag
        private val TAG = SessionManager::class.java.simpleName

        // Shared preferences file dCode
        private val PREF_NAME = "Instrument"
        private val KEY_IS_LOGGED_IN = "isLoggedIn"
        private val KEY_ID = "id"
        private val KEY_NAME = "name"
        private val KEY_EMAIL = "email"
        private val KEY_UNAME = "uname"
        private val KEY_PHONE = "phone"
        private val KEY_TOKEN = "APItoken"
        private val KEY_PIC = "ProfilePicture"
        private val KEY_ADDR = "address"
        private val KEY_LANG = "state"
        private val KEY_CITY = "city"
        private val KEY_LAT = "country"
        private val USER_TYPE = "type"

        private val FULL_ADDR = "addr"
        private val LOCATION = "location"
        private val PINCODE = "pincode"
    }
}