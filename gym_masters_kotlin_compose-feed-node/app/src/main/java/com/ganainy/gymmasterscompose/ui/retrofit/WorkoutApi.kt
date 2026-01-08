package com.ganainy.gymmasterscompose.ui.retrofit

import com.ganainy.gymmasterscompose.ui.models.Exercise
import com.ganainy.gymmasterscompose.ui.models.StaticExercise
import com.ganainy.gymmasterscompose.ui.models.ApiResponse
import com.ganainy.gymmasterscompose.ui.models.ExerciseRecordResponse

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

//这是一个 Retrofit 接口文件，定义了与 ExerciseDB API 的所有网络请求方法。
interface WorkoutApi {
    @GET("exercises")
    //getExercises(): 支持分页获取练习列表，可通过 limit 和 offset 参数控制
    suspend fun getExercises(
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): List<StaticExercise>?

//    @GET("/api/training/static/list")
//    suspend fun getStaticExercisesList(
//    ): List<StaticExercise>?

//    @GET("/api/training/dynamics/list")
//    suspend fun getDynamicsExercisesRecordList(): ExerciseRecordResponse?



}