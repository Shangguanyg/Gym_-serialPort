package com.ganainy.gymmasterscompose.ui.retrofit

import com.ganainy.gymmasterscompose.ui.models.Exercise
import com.ganainy.gymmasterscompose.ui.models.StaticExercise
import com.ganainy.gymmasterscompose.ui.models.ApiResponse
import com.ganainy.gymmasterscompose.ui.models.ExerciseRecordResponse

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

//这是一个 Retrofit 接口文件，定义了与 ExerciseDB API 的所有网络请求方法。
interface ExerciseApi {
    @GET("exercises")
    //getExercises(): 支持分页获取练习列表，可通过 limit 和 offset 参数控制
    suspend fun getExercises(
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): List<StaticExercise>?

//    @GET("/api/training/static/list")
//    suspend fun getStaticExercisesList(
//    ): List<StaticExercise>?

    @GET("/api/training/static/list")
    suspend fun getStaticExercisesList(): ApiResponse?

    @GET("/api/training/dynamics/list")
    suspend fun getDynamicsExercisesRecordList(): ExerciseRecordResponse?

    //getExercisesByBodyPart(): 按身体部位筛选练习
    @GET("exercises/bodyPart/{bodyPart}")
    suspend fun getExercisesByBodyPart(@Path("bodyPart") bodyPart: String): List<StaticExercise>


    @GET("/api/training/static/list")
    suspend fun getStaticExercisesRecord(): ApiResponse?

    //getBodyPartList(): 获取所有身体部位列表
    @GET("exercises/bodyPartList")
    suspend fun getBodyPartList(): List<String>

    //getEquipmentList(): 获取所有器械类型列表
    @GET("exercises/equipmentList")
    suspend fun getEquipmentList(): List<String>

    //getTargetList(): 获取所有目标肌肉列表
    @GET("exercises/targetList")
    suspend fun getTargetList(): List<String>

    //getExercisesByEquipment(): 按器械类型筛选练习
    @GET("exercises/equipment/{type}")
    suspend fun getExercisesByEquipment(@Path("type") type: String): List<StaticExercise>

    //getExercisesByTarget(): 按目标肌肉筛选练习
    @GET("exercises/target/{target}")
    suspend fun getExercisesByTarget(@Path("target") target: String): List<StaticExercise>

    //getExerciseById(): 根据ID获取特定练习详情
    @GET("exercises/exercise/{id}")
    suspend fun getExerciseById(@Path("id") id: String): StaticExercise

    //getExercisesByName(): 按名称搜索练习
    @GET("exercises/name/{name}")
    suspend fun getExercisesByName(@Path("name") name: String): List<StaticExercise>


}