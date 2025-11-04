package com.bright.patientcareapp.data.remote

import com.bright.patientcareapp.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API Service - Endpoints from PDF Page 7
 * Base URL: https://patientvisitapis.intellisoftkenya.com/api/
 */
interface PatientApiService {

    @POST("patients/register")
    suspend fun registerPatient(
        @Body request: PatientRegistrationRequest
    ): Response<ApiResponse<Any>>

    @POST("vitals/add")
    suspend fun addVitals(
        @Body request: VitalsRequest
    ): Response<ApiResponse<Any>>

    @POST("visits/add")
    suspend fun addGeneralAssessment(
        @Body request: GeneralAssessmentRequest
    ): Response<ApiResponse<Any>>

    @POST("visits/add")
    suspend fun addOverweightAssessment(
        @Body request: OverweightAssessmentRequest
    ): Response<ApiResponse<Any>>

    @GET("patients/list")
    suspend fun getPatients(): Response<ApiResponse<List<Any>>>
}