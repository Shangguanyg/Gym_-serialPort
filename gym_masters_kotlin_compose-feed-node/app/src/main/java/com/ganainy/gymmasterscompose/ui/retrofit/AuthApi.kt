package com.ganainy.gymmasterscompose.ui.retrofit


import com.ganainy.gymmasterscompose.ui.models.Exercise
import com.ganainy.gymmasterscompose.ui.models.UserCreationResponse
import com.ganainy.gymmasterscompose.ui.models.UserCreationRequest
import com.ganainy.gymmasterscompose.ui.models.UserLoginRequest
import com.ganainy.gymmasterscompose.ui.models.UserLoginResponse

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.Response

interface AuthApi {
    @POST("api/user/create")
    suspend fun createUserInfo(@Body userCreationRequest: UserCreationRequest): Response<UserCreationResponse>

    @POST("api/user/login")
    suspend fun userLogin(@Body userLoginRequest: UserLoginRequest): Response<UserLoginResponse>
}
