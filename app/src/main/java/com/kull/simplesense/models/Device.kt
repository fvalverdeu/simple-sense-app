package com.kull.simplesense.models

import com.google.gson.annotations.SerializedName

class Device (
    @SerializedName("name") val name: String? = null,
    @SerializedName("mac") val mac: String,
    @SerializedName("battery") val battery: String,
    @SerializedName("lastupdate") val lastupdate: String,
){
    override fun toString(): String {
        return "Device(name=$name, mac='$mac', battery='$battery', lastupdate='$lastupdate')"
    }
}