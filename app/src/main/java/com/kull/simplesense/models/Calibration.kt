package com.kull.simplesense.models

import com.google.gson.annotations.SerializedName

class Calibration (
    @SerializedName("id") val id: String? = null,
    @SerializedName("value") val value: String,
    @SerializedName("date") val date: String,
    @SerializedName("user_id") val iduser: String,
    @SerializedName("client_id") val idclient: String
){
    override fun toString(): String {
        return "Calibration(id=$id, value='$value', date='$date', iduser='$iduser', idclient='$idclient')"
    }
}