package com.ganainy.gymmasterscompose.ui.models.workout

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import kotlin.String

// Workout: 这是一个数据类，通常用于业务逻辑和用户界面（UI）中的表示。
// 它包含了所有用户在应用程序中需要用到的属性，如标题、描述、时间戳等。由于它实现了Parcelable接口，它可以方便地在不同的Android组件之间传递。
// WorkoutEntity: 这是一个映射到数据库表（在这里是SQLite）的类。
// 它使用了@Entity注释，表明该类的实例将被持久化到名为“workouts”的表中。它的结构可能会更符合数据库的设计模式，方便与数据库的交互。

/**
 * Data class representing a workout.
 *
 * @property id The unique identifier for the workout.
 * @property creatorId The unique identifier of the user who created the workout.
 * @property title The title of the workout.
 * @property description A description of the workout.
 * @property difficulty The difficulty level of the workout.
 * @property workoutDuration The duration of the workout in minutes.
 * @property dateCreated The timestamp when the workout was created.
 * @property imageUrl The URL of the workout image stored in Firebase Storage.
 * @property imagePath The local path of the workout image.
 * @property tags A list of tags associated with the workout.
 * @property isPublic A boolean indicating whether the workout is public.
 * @property workoutExerciseList A list of exercises included in the workout.
 * @property workoutMetrics The social statistics of the workout (upvotes, downvotes, save count).
 */

@Parcelize
data class Workout(
    val id: String = "",
    val creatorId: String = "",
    val title: String = "",
    val description: String = "",
    val difficulty: String = "",
    val workoutDuration: String = "", // mins
    val dateCreated: Timestamp = Timestamp.now(), // Timestamp
    val imageUrl: String = "", // Firebase Storage URL
    var imagePath: String = "", // Local image path
    val tags: List<String> = emptyList(),
    val isPublic: Boolean = false,
    var workoutExerciseList: List<WorkoutExercise> = emptyList(),
    val workoutMetrics: WorkoutMetrics = WorkoutMetrics(),

//    val id: String = "",
    val userId: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val trainingDate: String = "",
    val trainingTime: String = "",
    val trainingScore: String = "",

) : Parcelable


@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey
    val id: String,
    val creatorId: String,
    val title: String,
    val description: String,
    val difficulty: String,
//    val workoutDuration: String,
    val trainingTime: String,
    val trainingDate: String,
    val dateCreated: Timestamp,
    val imageUrl: String,
    val imagePath: String,
    val tags: List<String>,
    val isPublic: Boolean,
    val workoutExerciseList: List<WorkoutExercise>,
    @Embedded // Embed WorkoutMetrics directly in the table
    val workoutMetrics: WorkoutMetrics
) {
    companion object {
        const val WORKOUTS_COLLECTION = "workouts"
        const val WORKOUT_LIKES_COUNT = "likesCount"
        const val WORKOUT_SAVE_COUNT = "saveCount"
        const val WORKOUTS_METRICS = "workoutMetrics"
        const val WORKOUTS_TIMESTAMP = "dateCreated"
    }
}




// Extension functions to convert between Workout and WorkoutEntity
fun Workout.toWorkoutEntity(): WorkoutEntity {
    return WorkoutEntity(
        id = id,
        creatorId = creatorId,
        title = title,
        description = description,
        difficulty = difficulty,
        trainingTime = trainingTime,
        trainingDate = trainingDate,
        dateCreated = dateCreated,
        imageUrl = imageUrl,
        imagePath = imagePath,
        tags = tags,
        isPublic = isPublic,
        workoutExerciseList = workoutExerciseList,
        workoutMetrics = workoutMetrics
    )
}

fun WorkoutEntity.toWorkout(): Workout {
    return Workout(
        id = id,
        creatorId = creatorId,
        title = title,
        description = description,
        difficulty = difficulty,
//        workoutDuration = workoutDuration,
        trainingTime = trainingTime,
        trainingDate = trainingDate,
        dateCreated = dateCreated,
        imageUrl = imageUrl,
        imagePath = imagePath,
        tags = tags,
        isPublic = isPublic,
        workoutExerciseList = workoutExerciseList,
        workoutMetrics = workoutMetrics
    )
}



