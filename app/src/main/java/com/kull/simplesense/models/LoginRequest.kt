package com.kull.simplesense.models

import com.google.gson.annotations.SerializedName

class LoginRequest (
    @SerializedName("email") val email: String? = null,
    @SerializedName("password") val password: String,
){
    override fun toString(): String {
        return "LoginRequest(email=$email, password='$password')"
    }
}