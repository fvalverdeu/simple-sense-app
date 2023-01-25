package com.kull.simplesense.models

import com.google.gson.annotations.SerializedName

class Client (
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("contact") val contact: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("user_id") val userid: String? = null
){
    override fun toString(): String {
        return "Client(id=$id, name=$name, contact=$contact, status=$status, userid=$userid)"
    }
}