package com.bright.patientcareapp.data.auth

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenStore: SecureTokenStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip auth for login/signup endpoints
        val url = originalRequest.url.toString()
        if (url.contains("/user/signin") || url.contains("/user/signup")) {
            return chain.proceed(originalRequest)
        }

        // Get token and add to request
        val token = runBlocking { tokenStore.getToken() }

        val authenticatedRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer ${token.token}")
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(authenticatedRequest)

        // Handle 401 Unauthorized - clear token
        if (response.code == 401 && token != null) {
            runBlocking { tokenStore.clearToken() }
        }

        return response
    }
}