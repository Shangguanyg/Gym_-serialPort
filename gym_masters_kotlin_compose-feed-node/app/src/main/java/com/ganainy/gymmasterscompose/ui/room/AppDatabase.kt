package com.ganainy.gymmasterscompose.ui.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ganainy.gymmasterscompose.ui.models.BodyPart
import com.ganainy.gymmasterscompose.ui.models.Equipment
import com.ganainy.gymmasterscompose.ui.models.Exercise
import com.ganainy.gymmasterscompose.ui.models.StaticExercise
import com.ganainy.gymmasterscompose.ui.models.TargetMuscle
import com.ganainy.gymmasterscompose.ui.models.comment.CommentLikeDao
import com.ganainy.gymmasterscompose.ui.models.comment.CommentLikeEntity
import com.ganainy.gymmasterscompose.ui.models.workout.WorkoutEntity

@Database(entities = [Exercise::class,StaticExercise::class,  BodyPart::class, Equipment::class, TargetMuscle::class,
    WorkoutEntity::class,  CommentLikeEntity::class, CachedLike::class], version = 12)
//@TypeConverters 注解: 指定一个或多个类型转换器，对于将不支持的类型（如自定义对象，日期等）转换为可以存储在 SQLite 数据库中的类型（如字符串和数字）。
//Converters::class: Converters 类包含转换逻辑的实现。Room 需要在数据库操作时使用这些转换器来处理未内置的数据类型。
@TypeConverters(Converters::class)
//ExerciseDao: 专门用于操作 Exercise 表的 DAO。
//BodyPartListDao: 专门用于操作 BodyPart 表的 DAO。
//EquipmentDao: 专门用于操作 Equipment 表的 DAO。
//TargetDao: 专门用于操作 TargetMuscle 表的 DAO。
//WorkoutDao: 专门用于操作 WorkoutEntity 表的 DAO。
//CommentLikeDao: 专门用于操作 CommentLikeEntity 表的 DAO。
//CachedLikeDao: 专门用于操作 CachedLike 表的 DAO。
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun bodyPartListDao(): BodyPartListDao
    abstract fun equipmentDao(): EquipmentDao
    abstract fun targetDao(): TargetDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun commentLikeDao(): CommentLikeDao
    abstract fun cachedLikeDao(): CachedLikeDao
}