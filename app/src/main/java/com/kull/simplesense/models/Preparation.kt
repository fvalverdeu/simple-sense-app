package com.kull.simplesense.models

import com.google.gson.annotations.SerializedName

class Preparation (
    @SerializedName("id") val id: String? = null,
    @SerializedName("description") val description: String,
    @SerializedName("min") val min: String,
    @SerializedName("max") val max: String
) {
    override fun toString(): String {
        return "Preparation(id=$id, description='$description', min='$min', max='$max')"
    }
}
