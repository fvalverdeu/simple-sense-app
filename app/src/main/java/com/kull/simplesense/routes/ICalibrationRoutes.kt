package com.kull.simplesense.routes

import com.kull.simplesense.models.Calibration
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ICalibrationRoutes {
    @POST("calibrations")
    fun register(@Body calibration: Calibration): Call<Calibration>
}