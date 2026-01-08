package com.ganainy.gymmasterscompose.ui.shared_components

import CustomChip
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ganainy.gymmasterscompose.R
import com.ganainy.gymmasterscompose.ui.AppTheme
import com.ganainy.gymmasterscompose.ui.models.Exercise
import com.ganainy.gymmasterscompose.ui.models.StaticExercise
import com.ganainy.gymmasterscompose.ui.models.workout.WorkoutExercise
import com.ganainy.gymmasterscompose.utils.MockData.sampleExercise
import com.ganainy.gymmasterscompose.utils.MockData.sampleWorkoutExercise

// Enum for item types

// enum class ExerciseListItemType：定义了一个枚举类 ExerciseListItemType，用于描述不同类型的列表项。
//包含 3 种类型：
//EXERCISE：表示常规锻炼项。
//WORKOUT_ADDED_TO_EXERCISE：表示已添加到锻炼的项目。
//WORKOUT_NOT_ADDED_TO_EXERCISE：表示未添加到锻炼的项目。
enum class ExerciseListItemType {
    EXERCISE,
    WORKOUT_ADDED_TO_EXERCISE,
    WORKOUT_NOT_ADDED_TO_EXERCISE
}

// Sealed class to encapsulate exercise or workout exercise data
// sealed class ExerciseListItemData：定义一个密封类，用于封装锻炼和锻炼项目的数据。


sealed class ExerciseListItemData {
    // data class ExerciseData(val exercise: Exercise)：

    // 包含一个 Exercise 对象，表示单个锻炼项的数据。
    data class ExerciseData(val exercise: Exercise) : ExerciseListItemData()
    data class StaticExerciseData(val exercise: StaticExercise) : ExerciseListItemData()

    // data class WorkoutExerciseData(val workoutExercise: WorkoutExercise)：
    // 包含一个 WorkoutExercise 对象，表示与锻炼相关的锻炼项目的数据。
    data class WorkoutExerciseData(val workoutExercise: WorkoutExercise) : ExerciseListItemData()
}

//@OptIn(ExperimentalLayoutApi::class)：表示使用了实验性布局 API。
//@Composable fun ExerciseListItem(...)：定义一个可组合函数 ExerciseListItem，用于显示锻炼列表中的单个项目。
//接收以下参数：
//data: ExerciseListItemData：一类密封数据类型，包含锻炼或锻炼项目数据。
//type: ExerciseListItemType：项的类型，指示该项的种类（锻炼、已添加或未添加的锻炼）。
//onClick: (Exercise) -> Unit：点击时触发的回调，传入 Exercise 对象。
//onAddToWorkout: () -> Unit = {}：可选参数，点击后添加到锻炼的回调，默认无操作。
//onModify: (WorkoutExercise) -> Unit = {}：可选参数，修改锻炼的方法，默认无操作。
//onDelete: (WorkoutExercise) -> Unit = {}：可选参数，删除锻炼的方法，默认无操作。
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExerciseListItem(
    data: ExerciseListItemData,
    type: ExerciseListItemType,
//    onClick: (Exercise) -> Unit = {
//        Log.d("ExerciseListItem", "Click in onClick")
//    },
    onClick: (StaticExercise) -> Unit = {
        Log.d("ExerciseListItem", "Click in onClick")
    },
    onAddToWorkout: () -> Unit = {
        Log.d("ExerciseListItem", "Click in onAddToWorkout")
    },
    onModify: (WorkoutExercise) -> Unit = {},
    onDelete: (WorkoutExercise) -> Unit = {},
) {
    //    Animation for press effect
    //    使用 animateFloatAsState 创建一个动画效果，animatedScale 变量用于响应点击时的缩放效果。
    //    targetValue = 1f：目标值为 1，表示正常大小。
    //    animationSpec = tween(durationMillis = 200)：为缩放效果设置动画规格，持续 200 毫秒。
    val animatedScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 200)
    )

    //ListItem(...)：构建一个列表项组件，显示单个锻炼项及其详情。
    //modifier = Modifier.fillMaxWidth()：设置此组件填满可用的宽度。
    //.scale(animatedScale)：将缩放应用于动画效果。
    //.clip(RoundedCornerShape(12.dp))：为列表项设置圆角形状。
    //.shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))：为列表项添加阴影效果。
    //.background(MaterialTheme.colorScheme.surface)：设置背景颜色为主题色。
    //.clickable(...)：设置点击事件，仅在 data 是 ExerciseData 时启用点击。
    //onClick = { ... }：如果 data 为 ExerciseData，通过 onClick 回调传递锻炼项。
    ListItem(
             modifier = Modifier
            .fillMaxWidth()
            .scale(animatedScale)
            .clip(RoundedCornerShape(12.dp))
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
               // enabled = data is ExerciseListItemData.ExerciseData,
                enabled = data is ExerciseListItemData.StaticExerciseData,
                onClick = {
                    Log.d("ExerciseListItem", "onClick of ExerciseList")
//                    if (data is ExerciseListItemData.ExerciseData) {
//                        onClick(data.exercise)
//                    }
                    if (data is ExerciseListItemData.StaticExerciseData) {
                        Log.d("ExerciseListItem", "onClick1 of ExerciseList")
                        onClick(data.exercise)
                        Log.d("ExerciseListItem", "onClick2 of ExerciseList")
                    }
                }
            ),
        //colors = ListItemDefaults.colors(...)：设置列表项的配色方案，包括背景和文本颜色。
        //containerColor = MaterialTheme.colorScheme.surface：设置背景色为主题的表面颜色。
        //headlineColor = MaterialTheme.colorScheme.onSurface：设置主标题颜色为表面上的文本颜色。
        //supportingColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)：设置支持文本颜色，稍微透明。
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface,
            headlineColor = MaterialTheme.colorScheme.onSurface,
            supportingColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        ),
        //headlineContent = { ... }：定义列表项的主标题内容。
        //Text(...)：根据 data 的类型显示锻炼名称。
        //如果是 ExerciseData，则直接获取 exercise.name；如果是 WorkoutExerciseData，则获取与锻炼相关的名称，并使用 orEmpty() 处理可能的 null。
        //设置文本样式为 titleSmall ，并将字体加粗设置为中等大小，字号为 14sp。
        headlineContent = {
            Text(
                text = when (data) {
                    is ExerciseListItemData.ExerciseData -> data.exercise.name
                    is ExerciseListItemData.WorkoutExerciseData -> data.workoutExercise.exercise?.name.orEmpty()
                    is ExerciseListItemData.StaticExerciseData -> data.exercise.name
                },
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            )
        },
        //supportingContent = { ... }：定义列表项的支持文本或额外信息内容。
        //使用 Column 垂直排列内容。
        //FlowRow(...)：一个可组合的组件，用于将标签或附加信息水平排列，并支持换行。
        supportingContent = {

            Column {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    maxLines = 2,
                    overflow = FlowRowOverflow.Clip
                ) {
                    val bodyPart = when (data) {
                        is ExerciseListItemData.ExerciseData -> data.exercise.bodyPart
                        is ExerciseListItemData.WorkoutExerciseData -> data.workoutExercise.exercise?.bodyPart.orEmpty()
                        is ExerciseListItemData.StaticExerciseData -> data.exercise.bodyPart
                    }
                    val equipment = when (data) {
                        is ExerciseListItemData.ExerciseData -> data.exercise.equipment
                        is ExerciseListItemData.WorkoutExerciseData -> data.workoutExercise.exercise?.equipment.orEmpty()
                        is ExerciseListItemData.StaticExerciseData -> data.exercise.equipment
                    }
                    val target = when (data) {
                        is ExerciseListItemData.ExerciseData -> data.exercise.target
                        is ExerciseListItemData.WorkoutExerciseData -> data.workoutExercise.exercise?.target.orEmpty()
                        is ExerciseListItemData.StaticExerciseData -> data.exercise.target
                    }

                    if (bodyPart.isNotEmpty()) CustomChip(bodyPart, modifier = Modifier.padding(vertical = 2.dp))
                    if (equipment.isNotEmpty()) CustomChip(equipment, modifier = Modifier.padding(vertical = 2.dp))
                    if (target.isNotEmpty()) CustomChip(target, modifier = Modifier.padding(vertical = 2.dp))
                }
                Log.d("ExerciseListItem", "supportingContent1 of ExerciseList")
                if (type == ExerciseListItemType.WORKOUT_ADDED_TO_EXERCISE && data is ExerciseListItemData.WorkoutExerciseData) {
                    Log.d("ExerciseListItem", "supportingContent2 of ExerciseList")
                    WorkoutDetails(workoutExercise = data.workoutExercise)
                }
            }
        },
        //
        leadingContent = {
            ExerciseImage(data = data)
        },
        trailingContent = {
            when (type) {
                ExerciseListItemType.EXERCISE -> {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "View exercise details",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                ExerciseListItemType.WORKOUT_NOT_ADDED_TO_EXERCISE -> {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add exercise to workout",
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(
                                onClick = onAddToWorkout
                            )
                            .padding(4.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                ExerciseListItemType.WORKOUT_ADDED_TO_EXERCISE -> {
                    if (data is ExerciseListItemData.WorkoutExerciseData) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Modify exercise in workout",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(
                                        onClick = { onModify(data.workoutExercise) }
                                    )
                                    .padding(4.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete exercise from workout",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(
                                        onClick = { onDelete(data.workoutExercise) }
                                    )
                                    .padding(4.dp),
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    )
}

//ExerciseImage 方法
//@Composable private fun ExerciseImage(data: ExerciseListItemData)：定义一个可组合函数 ExerciseImage，用于显示锻炼或锻炼项目的图像。
//获取图像路径
//使用 when 表达式获取合适的 screenshotPath。
//如果是 ExerciseData，取 exercise.screenshotPath；如果是 WorkoutExerciseData，从 workoutExercise 中获取。
//图像处理逻辑
@Composable
private fun ExerciseImage(data: ExerciseListItemData) {
    val imagePath = when (data) {
        is ExerciseListItemData.ExerciseData -> data.exercise.screenshotPath
        is ExerciseListItemData.WorkoutExerciseData -> data.workoutExercise.exercise?.screenshotPath
        is ExerciseListItemData.StaticExerciseData -> data.exercise.screenshotPath
    }

    ////检查 imagePath 是否为 null，以决定如何显示图像。
    ////如果存在有效的路径，使用 AsyncImage 组件加载图像：
    ////model = imagePath：加载图像的 URL。
    ////contentDescription = "Exercise image"：用于无障碍描述。
    ////modifier：设置图像的大小和圆角裁剪。
    ////contentScale = ContentScale.Crop：裁剪图像以适应容器大小。
    ////placeholder 和 error：在加载图像时显示的占位符和错误图像资源。
    if (imagePath != null) {
        AsyncImage(
            model = imagePath,
            contentDescription = "Exercise image",
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.error),
            error = painterResource(id = R.drawable.error)
        )
    }
    //如果 imagePath 为 null，则使用 Icon 显示一个错误图像，指示锻炼图像不可用。
    else {
        Icon(
            painter = painterResource(id = R.drawable.error),
            contentDescription = "Exercise image not available",
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp)),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

// WorkoutDetails 方法
//@Composable private fun WorkoutDetails(workoutExercise: WorkoutExercise)：定义一个可组合的函数，用于显示与锻炼项目相关的详细信息。
@Composable
private fun WorkoutDetails(workoutExercise: WorkoutExercise) {
    // 使用 Row 布局，水平方向排列内容，设置为填满可用宽度，顶部内边距为 4dp。
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //显示锻炼项目的不同详细信息：
        //第一个 Text 显示锻炼项目的序号。
        //第二个 Text 显示锻炼中设置数和重复次数。
        //第三个 Text 显示设置之间的休息时间。
            Text(
                text = "Order: ${workoutExercise.order}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
            Text(
                text = "${workoutExercise.sets} sets, ${workoutExercise.reps} reps",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
            Text(
                text = "Rest: ${workoutExercise.restBetweenSets}s",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
        }
    }

    // Preview for EXERCISE type
//@Preview(...)：使用这个注解为 Compose 函数创建一个预览，以便在设计视图中查看效果。
//fun ExerciseListItemExercisePreview()：一个预览函数，用于展示 ExerciseListItem 的效果。
//在 AppTheme 下调用 ExerciseListItem(...)，传入示例锻炼数据 sampleExercise 进行渲染。
    @Preview(showBackground = true, name = "Exercise Item")
    @Composable
    fun ExerciseListItemExercisePreview() {
        AppTheme {
            ExerciseListItem(
               // data = ExerciseListItemData.ExerciseData(sampleExercise),
                data = ExerciseListItemData.StaticExerciseData(sampleExercise),
                type = ExerciseListItemType.EXERCISE,
                onClick = { /* No-op for preview */ },
                onAddToWorkout = { /* No-op for preview */ },
                onModify = { /* No-op for preview */ },
                onDelete = { /* No-op for preview */ }
            )
        }
    }

    // Preview for WORKOUT_ADDED_TO_EXERCISE type
    // 预览不同的锻炼项目类型
    //用于预览已添加到锻炼中的项目的效果，设置数据为 WorkoutExerciseData 类型。
    @Preview(showBackground = true, name = "Workout Exercise Item (Added)")
    @Composable
    fun ExerciseListItemWorkoutAddedPreview() {
        AppTheme {
            ExerciseListItem(
                data = ExerciseListItemData.WorkoutExerciseData(sampleWorkoutExercise),
                type = ExerciseListItemType.WORKOUT_ADDED_TO_EXERCISE,
                onClick = { /* No-op for preview */ },
                onAddToWorkout = { /* No-op for preview */ },
                onModify = { /* No-op for preview */ },
                onDelete = { /* No-op for preview */ }
            )
        }
    }

    // Preview for WORKOUT_NOT_ADDED_TO_EXERCISE type
    // 预览未添加的锻炼项目
    // 用于预览未添加到锻炼中的项目效果，数据为普通的 ExerciseData 类型。
    @Preview(showBackground = true, name = "Workout Exercise Item (Not Added)")
    @Composable
    fun ExerciseListItemWorkoutNotAddedPreview() {
        AppTheme {
            ExerciseListItem(
//                data = ExerciseListItemData.ExerciseData(sampleExercise),
                data = ExerciseListItemData.StaticExerciseData(sampleExercise),
                type = ExerciseListItemType.WORKOUT_NOT_ADDED_TO_EXERCISE,
                onClick = { /* No-op for preview */ },
                onAddToWorkout = { /* No-op for preview */ },
                onModify = { /* No-op for preview */ },
                onDelete = { /* No-op for preview */ }
            )
        }
    }