package com.ganainy.gymmasterscompose.ui.room


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ganainy.gymmasterscompose.ui.models.Exercise
import com.ganainy.gymmasterscompose.ui.models.StaticExercise
import kotlinx.coroutines.flow.Flow

// 这段代码定义了一个 DAO（数据访问对象）接口 ExerciseDao，
// 用于管理与 Exercise 相关的数据库操作这是在使用 Room 进行本地数据持久化时的常见模式。
// @Dao 注解：用于标记该接口为 Room 的数据访问对象（DAO），即用于定义与数据库的交互方法。
// interface ExerciseDao：定义了一个名为 ExerciseDao 的接口，这个接口将包括各种与 Exercise 模型相关的数据库操作。
@Dao
interface ExerciseDao {
    // 获取所有锻炼的查询
    // @Query("SELECT * FROM exercise")：定义一个 SQL 查询，获取 exercise 表中的所有记录。
    //suspend fun getAllExercises()：声明为挂起函数，以便在协程中调用。返回一个 List<Exercise>，即数据库中所有锻炼的列表。
    @Query("SELECT * FROM exercise")
    suspend fun getAllExercises(): List<StaticExercise>



    @Query("SELECT * FROM staticExercise")
    suspend fun getStaticExercises(): List<StaticExercise>

    //@Insert(onConflict = OnConflictStrategy.REPLACE)：定义一个插入操作，如果插入的记录与现有记录发生冲突，将会替换现有记录。
    //suspend fun insertAll(exercises: List<Exercise>)：挂起函数，接受一个 List<Exercise> 作为参数，用于批量插入锻炼记录。
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<StaticExercise>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStaticExercise(exercises: List<StaticExercise>)


    //@Query("SELECT * FROM exercise WHERE equipment LIKE :type")：定义一个 SQL 查询，通过 equipment 字段查找满足条件的锻炼。
    //abstract fun getExercisesByEquipment(type: String): List<Exercise>?：返回一个 List<Exercise>（可以为 null），获取满足条件的锻炼列表。
    @Query("SELECT * FROM exercise WHERE equipment LIKE :type")
    abstract fun getExercisesByEquipment(type: String): List<StaticExercise>?

    //使用类似的方法定义一个查询，通过 target 字段查找锻炼。
    //abstract fun getExercisesByTarget(target: String): List<Exercise>?：返回一个 List<Exercise>（可以为 null），获取满足条件的锻炼列表。
    @Query("SELECT * FROM exercise WHERE target LIKE :target")
    abstract fun getExercisesByTarget(target: String): List<StaticExercise>?

    //这行定义一个按 name 字段查找锻炼的查询。
    //abstract fun getExercisesByName(name: String): List<Exercise>?：返回一个 List<Exercise>（可以为 null），获取满足条件的锻炼列表。
    @Query("SELECT * FROM exercise WHERE name LIKE :name")
    abstract fun getExercisesByName(name: String): List<StaticExercise>?

    //定义一个按 id 查找单个锻炼的查询。
    //abstract fun getExerciseById(id: String): Exercise?：返回一个 Exercise（可以为 null），获取匹配的锻炼记录。
    @Query("SELECT * FROM exercise WHERE id LIKE :id")
    abstract fun getExerciseById(id: String): StaticExercise?

    //这行和上一行类似，但返回一个 Flow<Exercise?>，用于观察锻炼记录的变化。
    //当数据库中与该 id 相对应的锻炼数据发生变化时，流会发出新值，适用于 UI 的响应式更新。
    @Query("SELECT * FROM exercise WHERE id LIKE :id")
    abstract fun observeExerciseById(id: String): Flow<StaticExercise?>

    //按 bodyPart 字段查找锻炼的查询。
    //abstract fun getExercisesByBodyPart(bodyPart: String): List<Exercise>?：返回一个 List<Exercise>（可以为 null），获取匹配的锻炼列表。
    @Query("SELECT * FROM exercise WHERE bodyPart LIKE :bodyPart")
    abstract fun getExercisesByBodyPart(bodyPart: String): List<StaticExercise>?

    //插入单个锻炼的操作，类似于上面的插入操作。
    //suspend fun insert(exercise: Exercise)：挂起函数，接受一个 Exercise 作为参数，插入到数据库中。
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: StaticExercise)

    //@Update 注解：定义一个更新操作。
    //suspend fun update(exercise: Exercise)：接受一个 Exercise 对象作为参数，更新对应的锻炼记录。
    @Update
    suspend fun update(exercise: StaticExercise)

    //查询以获取所有 isSavedLocally 标记为 1 的锻炼，这表示这些锻炼已在本地保存。
    //返回一个 Flow<List<Exercise>?>，用于观察已保存锻炼的变化。
    @Query("SELECT * FROM exercise WHERE isSavedLocally = 1")
    abstract fun observeSavedExercises(): Flow<List<StaticExercise>?>

    /**
     * Fetches a specific page of exercises.
     * Useful for paginated loading from the local cache.
     * ORDER BY id is important for consistent paging.
     */
    ///** ... */：注释解释该方法用于获取特定的锻炼页面，适合本地缓存的分页加载。
    //@Query("SELECT ...")：定义一个分页的 SQL 查询，按 id 排序，限制返回的记录数，并指定偏移量以便于分页。
    //suspend fun getExercisesPage(limit: Int, offset: Int): List<Exercise>：挂起函数，返回一个 List<Exercise>，包含查询的结果。

    @Query("SELECT * FROM exercise ORDER BY id ASC LIMIT :limit OFFSET :offset") // Order by ID for consistency
    suspend fun getExercisesPage(limit: Int, offset: Int): List<StaticExercise>

}