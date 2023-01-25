package com.kull.simplesense.models

import com.google.gson.annotations.SerializedName

class LoginResponse (
    @SerializedName("token") val token: String? = null,
){
    override fun toString(): String {
        return "LoginResponse(token=$token)"
    }
}