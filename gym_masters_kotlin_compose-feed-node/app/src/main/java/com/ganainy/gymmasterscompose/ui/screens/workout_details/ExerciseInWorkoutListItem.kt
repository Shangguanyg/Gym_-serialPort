package com.ganainy.gymmasterscompose.ui.screens.workout_details


import CustomChip
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.ganainy.gymmasterscompose.R
import com.ganainy.gymmasterscompose.ui.AppTheme
import com.ganainy.gymmasterscompose.ui.models.Exercise
import com.ganainy.gymmasterscompose.ui.models.StaticExercise
import com.ganainy.gymmasterscompose.ui.models.workout.WorkoutExercise

//@OptIn(ExperimentalLayoutApi::class): 该注解表示代码中使用了 Jetpack Compose 的实验性布局 API。必须符合使用实验性特性的要求。
//@Composable: 表明该函数是一个可组合函数，用于构建 UI。

//这段代码展示了移动应用程序中关于锻炼项的详细信息的 UI 实现。通过使用 Jetpack Compose，构建灵活、响应式的界面，能够根据用户操作实时更新展示内容。
//模块化设计: 通过将界面细分为多个可组合函数，增强了可维护性和可读性。每个组件都有清晰的责任分离。
//状态管理: 通过 ViewModel 管理 UI 状态，极大地提高了应对复杂交互场景的能力。
//良好的用户体验: 利用 Surface、Card 和其他 Material Design 组件，确保了应用的一致性和现代化。

//参数:
//workoutExercise: WorkoutExercise: 接收一个表示锻炼信息的对象。
//onClick: (Exercise) -> Unit: 当用户点击该项时触发的回调，传递与锻炼相关的 Exercise 对象。
//modifier: Modifier = Modifier: 用于自定义修饰符的可选参数，默认为不做修饰。
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExerciseInWorkoutListItem(
    workoutExercise: WorkoutExercise,
    onClick: (StaticExercise) -> Unit,
    modifier: Modifier = Modifier,
) {
    //ListItem(...): 使用 Material Design 的 ListItem 组件来呈现一个列表项。
    //headlineContent: 设置主标题的内容，这里显示锻炼的名称。
    //workoutExercise.exercise?.name?.let { Text(it) }: 安全调用，如果 exercise 对象和 name 不为 null，则显示锻炼名称。
    ListItem(
        headlineContent = {
            workoutExercise.exercise?.name?.let { Text(it) }
        },

        //supportingContent: 该部分用来展示辅助文本或相关信息。
        //Column { ... }: 使用列布局以竖直方向排列子组件。
        //FlowRow(...): 这是一个流式布局，允许其子组件在水平和垂直方向上根据可用空间自动换行。包括参数：
        //modifier: 将 modifier 传递给 FlowRow 以处理样式。
        //horizontalArrangement 和 verticalArrangement: 分别在水平和垂直方向上设置组件间的间距。
        supportingContent = {
            Column {
                FlowRow(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    //CustomChip(...): 对锻炼的身体部位、设备和目标等信息使用自定义的小组件（Chip）进行展示。
                    //安全调用: 通过可选链（?.）确保在 exercise 对象存在时才渲染相应的信息。
                    workoutExercise.exercise?.bodyPart?.let { CustomChip(it) }

                    workoutExercise.exercise?.equipment?.let { CustomChip(it) }

                    workoutExercise.exercise?.target?.let { CustomChip(it) }
                }

                //Spacer(...): 增加竖直方向的间隔，改善布局的视觉效果。
                Spacer(modifier = Modifier.height(4.dp))
            //todo
            }
        },
        //
        leadingContent = {
            // If an imageBitmap is provided, use it. Otherwise, show placeholder
            // 条件图像显示: 如果锻炼对象具有有效的 screenshotPath，则显示相应的图片。
            // rememberImagePainter(...): 使用 Image Koin 中的 rememberImagePainter 加载图片。
            // 默认图标: 如果没有可用的图片，显示占位符图标（如哑铃图标）。
            if (workoutExercise.exercise?.screenshotPath != null) {
                Image(
                    painter = rememberImagePainter(data = workoutExercise.exercise.screenshotPath),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.dumbbell), // Placeholder icon
                    contentDescription = "Placeholder",
                    modifier = Modifier.size(56.dp)
                )
            }
        },
        // trailingContent: 在列表项的右边添加一个图标，用于指示用户可以进一步查看锻炼详情。
        trailingContent = {
            Icon(Icons.Default.ChevronRight, "View details")

        },

        //modifier: 开启点击操作。只有在 exercise 不为 null 时才调用 onClick，将锻炼信息传递给回调函数。
        //fillMaxWidth(): 确保列表项填满父容器的宽度，使 UI 布局整齐且美观。
        modifier = Modifier
            .clickable {
                if (workoutExercise.exercise != null) {
                    onClick(workoutExercise.exercise)
                }
            }
            .fillMaxWidth()
    )
}

//@Composable: 指明这是一个可组合函数。
//参数:
//workoutExercise: WorkoutExercise: 接收的锻炼对象，用于显示锻炼动画。
//modifier: Modifier = Modifier: 可选参数，默认为没有修饰的状态。
@Composable
fun ExerciseDetailsCard(
    workoutExercise: WorkoutExercise,
    modifier: Modifier = Modifier
) {
    //Card(...): 定义了一个 Material Design 卡片组件，设置样式。
    //modifier: 确保卡片宽度填满并设置适当的上下边距。
    //shape: 将卡片的形状定义为具有圆角的 12dp。
    //colors: 设置卡片的容器颜色为主题的变体并稍微增加透明度。
    //elevation: 设置卡片的阴影深度，给用户提供层次感。
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        //Column(...): 使用垂直布局展现细节信息。
        //padding: 为集合内容提供内边距。
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // First row: Order, Sets, and Reps
            // Row(...): 用于水平方向的排列。设置内部组件的对齐方式和间隔。
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //ExerciseDetailItem(...): 自定义组件，用于展示详细的锻炼项目，比如顺序、套数、重复次数等。
                //参数: 各种信息（图标、值、标签）都会传递到该组件中。
                ExerciseDetailItem(
                    icon = Icons.Rounded.FormatListNumbered,
                    value = workoutExercise.order.toString(),
                    label = "Order",
                    color = MaterialTheme.colorScheme.primary
                )

                //重复三次: 重复以上步骤来展示设置和重复次数的信息，以确保用户可以一目了然地了解锻炼细节。
                ExerciseDetailItem(
                    icon = Icons.Rounded.Repeat,
                    value = workoutExercise.sets.toString(),
                    label = "Sets",
                    color = MaterialTheme.colorScheme.secondary
                )

                ExerciseDetailItem(
                    icon = Icons.Rounded.FitnessCenter,
                    value = workoutExercise.reps.toString(),
                    label = "Reps",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            // Second row: Rest time
            // 展示休息时间: 使用行布局显示用户每个锻炼项目间的休息时间。
            // 背景: 设置背景色和形状，使其在视觉上与其他内容区分开来。
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExerciseDetailItem(
                    icon = Icons.Rounded.Timer,
                    value = "${workoutExercise.restBetweenSets}s",
                    label = "Rest",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

//声明: 这是用于展示锻炼细节的子组件。接收各种参数（图标、值、标签、颜色和修饰符）。
@Composable
private fun ExerciseDetailItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    //行布局: 设置细节项内容的排列方式。
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        //Icon(...): 使用 Jetpack Compose 的图标组件，显示所需的图标。
        //contentDescription: 提供访问性文本，告知用户当前图标表示什么。
        Icon(
            imageVector = icon,
            contentDescription = "$label icon",
            tint = color,
            modifier = Modifier
                .size(20.dp)
                .padding(end = 4.dp)
        )
        //Column(...): 垂直布局，渲染值和标签。
        //Text(...): 用于设置具体可见内容，运用 Material 主题样式和颜色进行格式化。
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            )
        }
    }
}

//@Preview: 提供了在 Android Studio 中预览功能，方便设计和调试 UI。
//构造示例数据: 创建一个样本的 WorkoutExercise 对象用于预览。
//AppTheme: 将预览组件包裹在 App 的主题中，确保匹配整个应用的视觉样式。
@Preview(showBackground = true)
@Composable
fun ExerciseDetailsCardPreview() {
    // Create a sample WorkoutExercise for preview
    val sampleExercise = WorkoutExercise(
        order = 1,
        sets = 3,
        reps = 12,
        restBetweenSets = 60,
    )

    AppTheme {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ExerciseDetailsCard(
                workoutExercise = sampleExercise
            )
        }
    }
}


//@Preview: 定义一个预览的可组合函数。
//构造 WorkoutExercise 对象: 创建一个示例的锻炼对象和相关的属性信息以用于测试 UI 组件。
//onClick = {}: 传入空的点击处理函数，作为占位符，允许界面入预览中表现正常。
@Preview
@Composable
private fun ExerciseInWorkoutListItemPreview() {
    ExerciseInWorkoutListItem(
        workoutExercise = WorkoutExercise(
            exercise = StaticExercise(
                bodyPart = "Legs",
                equipment = "Machine",
                gifUrl = "url_to_squat_machine_gif",
                screenshotPath = "path_to_squat_machine_screenshot",
                id = "1",
                name = "Squat (Machine)",
                target = "Quadriceps",
                secondaryMuscles = listOf("Glutes", "Hamstrings"),
                instructions = listOf(
                    "Set the machine to your height.",
                    "Place your shoulders under the pads.",
                    "Push through your heels to lift."
                )
            ),
            order = 1,
            sets = 3,
            reps = 8,
            restBetweenSets = 60
        ),
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    )
}


