package com.kull.simplesense.utils

import android.app.Activity
import com.google.gson.Gson
import com.kull.simplesense.models.Client
import com.kull.simplesense.models.User

public class SessionHandler {
    public fun getMacFromSession(activity: Activity): String? {
        val sharedPref = SharedPref(activity)
        if(!sharedPref.getData("mac").isNullOrBlank()) {
            val mac = sharedPref.getData("mac")
            return mac
        }
        return ""
    }
    public fun getUserFromSession(activity: Activity): User {
        val sharedPref = SharedPref(activity)
        val gson = Gson()
        var user = User()
        if (!sharedPref.getData("user").isNullOrBlank()) {
            user = gson.fromJson(sharedPref.getData("user"), User::class.java)
            return user
        }
        return user
    }
}