package com.ganainy.gymmasterscompose.ui.screens.create_workout.composables

import ExerciseInWorkoutCreateItem
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ganainy.gymmasterscompose.ui.models.Exercise
import com.ganainy.gymmasterscompose.ui.models.StaticExercise
import com.ganainy.gymmasterscompose.ui.models.workout.WorkoutExercise

//The WorkoutExercisesSection.kt 文件是一个 Jetpack Compose UI 组件，
// 用于渲染显示锻炼练习的章节，并具有添加和删除练习的功能。

//主可组合函数 WorkoutExercisesSection 被声明为内部函数，并接受三个参数
//exerciseList：要显示的 WorkoutExercise 对象列表
//onDeleteExercise：删除练习时触发的回调函数
//toggleExerciseWorkoutListShow：显示/隐藏练习选择界面的回调函数

@Composable
internal fun WorkoutExercisesSection(
    exerciseList: List<WorkoutExercise>,
    onDeleteExercise: (WorkoutExercise) -> Unit,
    toggleExerciseWorkoutListShow: () -> Unit,
) {

    //该组件使用一个 Column 作为其根容器，具有全宽和垂直填充
    // 这为所有子组件提供了垂直布局。
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // 头部包含一个行 ，采用 space-between 排列，显示章节标题和一个添加按钮
        // Section title + Add exercise icon
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 标题“练习”使用 Material3 字体样式和自定义样式，应用了半粗体字重和表面颜色主题。
            Text(
                text = "Exercises",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            // 添加按钮实现为一个可点击的图标，使用 Material3 的添加图标。
            // 它具有圆角、自定义尺寸，并在点击时调用 toggleExerciseWorkoutListShow 回调函数。
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add exercise",
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(
                        onClick = toggleExerciseWorkoutListShow
                    )
                    .padding(4.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        //间距和条件内容
        //一个填充物在标题和内容之间提供垂直分隔
        Spacer(modifier = Modifier.height(8.dp))

        // Empty state
        // 空状态处理
        // 该组件根据锻炼列表是否为空进行条件渲染
        if (exerciseList.isEmpty()) {
            // 当为空时，它在一个 Box 容器中显示居中的消息
            // 空状态文本使用柔和的样式和降低的不透明度，以实现微妙的外观。
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No exercises added yet",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
            }
        } else {
            // Exercise list
            // 当存在练习时，它们以水平填充和垂直间距的形式呈现在一个列中
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 组件通过 forEach 遍历每个练习，并为每个练习渲染一个 ExerciseInWorkoutCreateItem
                // 每个项目接收锻炼练习数据和删除回调函数。
                exerciseList.forEach { exercise ->
                    ExerciseInWorkoutCreateItem(
                        workoutExercise = exercise,
                        onDeleteExercise = onDeleteExercise
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WorkoutExercisesSectionPreview() {
    MaterialTheme {
        WorkoutExercisesSection(
            exerciseList = listOf(
                WorkoutExercise(
//                    exercise = Exercise(name = "Bench Press"),
                    exercise = StaticExercise(name = "Bench Press"),
                    sets = 3,
                    reps = 10,
                    order = 1
                ),
                WorkoutExercise(
//                    exercise = Exercise(name = "Squats"),
                    exercise = StaticExercise(name = "Squats"),
                    sets = 4,
                    reps = 12,
                    order = 2
                )
            ),
            onDeleteExercise = { /* Handle delete */ },
            toggleExerciseWorkoutListShow = { /* Handle toggle */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WorkoutExercisesSectionEmptyPreview() {
    MaterialTheme {
        WorkoutExercisesSection(
            exerciseList = emptyList(),
            onDeleteExercise = { /* Handle delete */ },
            toggleExerciseWorkoutListShow = { /* Handle toggle */ }
        )
    }
}
