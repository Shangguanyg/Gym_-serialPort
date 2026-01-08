package com.ganainy.gymmasterscompose.ui.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlin.String
import com.ganainy.gymmasterscompose.ui.models.workout.Workout



// @Entity(tableName = "exercise")：这个注解表示 Exercise 类将与数据库中的 exercise 表对应。Room 会使用这个注解来创建和管理该表。
@Entity(tableName = "exercise")
// @Serializable：这是一个来自 Kotlinx Serialization 的注解，表示该类可以序列化，并允许将对象转换为 JSON 或其他格式。这对于网络请求或缓存持久化很有用。
@Serializable
// @Parcelize：这是一个来自 Android Parcelable 插件的注解，它自动生成实现 Parcelable 接口的代码。
// 这使得 Exercise 实例可以在 Android 组件之间（如通过 Intent）进行传递。
@Parcelize

//data class Exercise：定义了一个数据类 Exercise，用于表示锻炼的信息。
//val bodyPart: String = ""：表示锻炼涉及的身体部位，默认为空字符串。
//val equipment: String = ""：表示进行此锻炼所需的设备，默认为空字符串。
//val gifUrl: String = ""：存储锻炼 GIF 动画的 URL，默认为空字符串。
//@PrimaryKey val id: String = ""：表示锻炼的唯一标识符，使用 @PrimaryKey 注解标识这是该表的主键，默认为空字符串。
//val name: String = ""：表示锻炼的名称，默认为空字符串。
//val target: String = ""：表示锻炼的目标肌肉，默认为空字符串。
//val secondaryMuscles: List<String> = emptyList()：表示辅助肌肉的列表，默认是一个空列表。
//val instructions: List<String> = emptyList()：表示锻炼的步骤说明列表，默认是一个空列表。
//var screenshotPath: String? = null：可变属性，表示锻炼 GIF 的截图路径，默认为 null。还提供了注释说明为什么会需要这个属性。
//var isSavedLocally: Boolean = false：可变属性，表示用户是否已将此锻炼保存到本地，默认为 false。

data class Exercise(
    val bodyPart: String = "",
    val equipment: String = "",
    val gifUrl: String = "",
    @PrimaryKey val id: String = "",
    val name: String = "",
    val target: String = "",
    val secondaryMuscles: List<String> = emptyList(),
    val instructions: List<String> = emptyList(),
    var screenshotPath: String? = null, // the path of a screenshot of the gif on the device,
    // api doesn't provide an exercise image, so we take the exercise gif and extract an image from it
    var isSavedLocally: Boolean = false, // if the user saved this exercise locally

    //val id: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val trainingDate: String = "",
    val trainingTime: String = "",
    val trainingScore: String = "",
    val userId: String = "",

) : Parcelable

data class ApiResponse(
    val code: Int,
    val message: String,
    val data: List<StaticExercise>
)

@Entity(tableName = "exerciseRecord")
@Serializable
@Parcelize
data class ExerciseRecord(
    val id: String = "",
    val userId: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val trainingDate: String = "",
    val trainingTime: String = "",
    val trainingScore: String = "",
) : Parcelable

data class ExerciseRecordResponse(
    val code: Int,
    val message: String,
//    val data: List<ExerciseRecord>
    val data: List<Workout>

)

@Entity(tableName = "staticExercise")
@Serializable
@Parcelize
data class StaticExercise(
    var isSavedLocally: Boolean = false, // if the user saved this exercise locally
    @PrimaryKey val id: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val trainingDate: String = "",
    val trainingTime: String = "",
    val trainingScore: String = "",
    val userId: String = "",
    var gifUrl: String = "",
    val bodyPart: String = "testBodyPart",
    val equipment: String = "testEquipment",
    val target: String = "",
    val name: String = "testStaticExercise",
    var screenshotPath: String? = null, // the path of a screenshot of the gif on the device,
    val secondaryMuscles: List<String> = emptyList(),
    val instructions: List<String> = emptyList(),
) : Parcelable



// @Entity(tableName = "body_part_list")：定义了一个与数据库表 body_part_list 对应的实体类。
@Entity(tableName = "body_part_list")

//data class BodyPart：定义了一个数据类 BodyPart，表示身体部位的名称。
//@PrimaryKey val name: String：使用 name 作为主键，确保每个身体部位的名称是唯一的。
data class BodyPart(
    @PrimaryKey val name: String
)

//@Entity(tableName = "equipment")：定义了一个与数据库表 equipment 对应的实体类。
@Entity(tableName = "equipment")
data class Equipment(
    @PrimaryKey val name: String
)

//@Entity(tableName = "targets")：定义了一个与数据库表 targets 对应的实体类。
@Entity(tableName = "targets")
//data class TargetMuscle：定义了一个数据类 TargetMuscle，表示目标肌肉的名称。
//@PrimaryKey val name: String：使用 name 作为主键，确保目标肌肉的名称是唯一的。
data class TargetMuscle(
    @PrimaryKey val name: String
)

