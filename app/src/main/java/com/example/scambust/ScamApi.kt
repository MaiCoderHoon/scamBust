package com.example.scambust

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class SmsRequest(
    val sender: String,
    val message: String
)

data class ScamResponse(
    val verdict: String, // "SCAM" | "SUSPICIOUS" | "SAFE"
    val confidence: Float,
    val risk_level: Int
)

interface ScamApiService {
    @POST("/analyze")
    suspend fun analyzeSms(@Body request: SmsRequest): ScamResponse
}

object RetrofitClient {
    // Note: 10.0.2.2 is used to access localhost from Android emulator
    // Change to actual IP if using a physical device
    private const val BASE_URL = "http://51.20.191.217:8000/"

    val instance: ScamApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ScamApiService::class.java)
    }
}
