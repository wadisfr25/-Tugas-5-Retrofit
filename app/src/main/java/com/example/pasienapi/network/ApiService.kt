package com.example.pasienapi.network

import com.example.pasienapi.model.LoginRequest
import com.example.pasienapi.model.LoginResponse
import com.example.pasienapi.model.PatientsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/pasien")
    suspend fun getPatients(
        @Header("Authorization") authorization: String
    ): Response<PatientsResponse>
}
