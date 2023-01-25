package com.kull.simplesense.models

import com.google.gson.annotations.SerializedName

class User (
    @SerializedName("id") val id: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("lastname") val lastname: String? = null,
    @SerializedName("rol") val rol: String? = null
) {
    override fun toString(): String {
        return "User(id=$id, email=$email, name=$name, lastname=$lastname, rol=$rol)"
    }
}
