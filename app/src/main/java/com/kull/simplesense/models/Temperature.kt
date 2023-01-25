package com.kull.simplesense.models

import com.google.gson.annotations.SerializedName

class Temperature (
    @SerializedName("id") val id: String? = null,
    @SerializedName("value") val value: String,
    @SerializedName("date") val date: String,
    @SerializedName("location") val location: String,
    @SerializedName("observation") val observation: String,
    @SerializedName("preparation_id") val idpreparation: String,
    @SerializedName("user_id") val iduser: String,
    @SerializedName("client_id") val idclient: String,
){
    override fun toString(): String {
        return "Temperature(id=$id, value='$value', date='$date', location='$location', observation='$observation', idpreparation='$idpreparation', iduser='$iduser', idclient='$idclient')"
    }
}