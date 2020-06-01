package com.centosquare.devatease.goappdriver.utils

import android.content.Context

class SharedPrefsManager {

    private var context: Context? = null
    private val PREFS_NAME = "USER_PREFERENCE"

    constructor(context: Context){

        this.context = context
    }


    fun setInt(key: String, value: Int) {
        val sharedPref = context!!.getSharedPreferences(PREFS_NAME, 0)
        val editor = sharedPref.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(key: String): Int {
        val prefs = context!!.getSharedPreferences(PREFS_NAME, 0)
        return prefs.getInt(key, 0)
    }

    fun setStr(key: String, value: String) {
        val sharedPref = context!!.getSharedPreferences(PREFS_NAME, 0)
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun setFloatValues(key: String, value: Float?) {
        val sharedPref = context!!.getSharedPreferences(PREFS_NAME, 0)
        val editor = sharedPref.edit()
        editor.putFloat(key, value!!)
        editor.apply()
    }

    fun getFloatValues(key: String, value: Float?) {
        val sharedPref = context!!.getSharedPreferences(PREFS_NAME, 0)
        val editor = sharedPref.edit()
        editor.putFloat(key, value!!)
        editor.apply()
    }

    fun getStr(key: String): String {
        val prefs = context!!.getSharedPreferences(PREFS_NAME, 0)
        return prefs.getString(key, "DNF")!!
    }

    fun setBool(key: String, value: Boolean) {
        val sharedPref = context!!.getSharedPreferences(PREFS_NAME, 0)
        val editor = sharedPref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBool(key: String): Boolean {
        val prefs = context!!.getSharedPreferences(PREFS_NAME, 0)
        return prefs.getBoolean(key, false)
    }

    fun removeFromPrefs() {

        val sharedPref = context!!.getSharedPreferences(PREFS_NAME, 0)
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()

    }



}