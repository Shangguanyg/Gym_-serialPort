package com.ganainy.gymmasterscompose.ui.screens.exercise

import CustomChip
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.ganainy.gymmasterscompose.ui.models.Exercise
import com.ganainy.gymmasterscompose.ui.models.StaticExercise

// @OptIn(ExperimentalLayoutApi::class)：这是一个 Opt-In 注解，表示这个函数使用了实验性 API。
// 使用实验性 API 意味着可能会在未来的版本中发生不兼容的更改。
// @Composable：标记函数为 Composable，表示它可以被 Compose 框架调用用于构建 UI。
// internal fun ExerciseContent(exercise: Exercise)：定义了一个名为 ExerciseContent 的内部函数，接受一个 Exercise 对象。

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ExerciseContent(exercise: StaticExercise) {
    //Column：用于垂直排列子 Composable。
    //modifier = Modifier.fillMaxSize()：让 Column 充满可用空间。
    //.verticalScroll(rememberScrollState())：使 Column 内容可垂直滚动，使用 rememberScrollState() 保存滚动状态。
    //.padding(16.dp)：给 Column 添加边距 16dp。
    //verticalArrangement = Arrangement.spacedBy(16.dp)：在垂直方向上设置子项之间的间隔为 16dp。
    //horizontalAlignment = Alignment.CenterHorizontally：中心对齐子项。
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //通过调用其他 Composable 函数为 Column 添加子项。
        //ExerciseGif：展示锻炼相关的 GIF 动画。
        //ExerciseTitle：显示锻炼名称。
        //ExerciseDetails：显示锻炼的身体部位、设备和目标。
        //SecondaryMusclesSection：显示辅助肌肉群。
        //InstructionsSection：显示锻炼步骤或说明。
        ExerciseGif(gifUrl = exercise.gifUrl)
        ExerciseTitle(name = exercise.name)
        ExerciseDetails(
            bodyPart = exercise.bodyPart,
            equipment = exercise.equipment,
            target = exercise.target
        )
        SecondaryMusclesSection(secondaryMuscles = exercise.secondaryMuscles)
        InstructionsSection(instructions = exercise.instructions)
    }
}

//@Composable private fun ExerciseGif(gifUrl: String)：定义一个私有的 Composable 函数，接受 GIF 的 URL。
//AndroidView：用于在 Jetpack Compose 中使用原生 Android 视图（这里是 ImageView）。
//factory：创建 ImageView 的工厂方法，通过 Glide 加载 GIF。
//scaleType = ImageView.ScaleType.FIT_CENTER：设置图片缩放方式。
//Glide：用于加载和显示 GIF 动画的库。
//modifier：配置 ImageView 的布局属性：
//.fillMaxWidth()：填充最大宽度。
//.height(300.dp)：设置高度为 300dp。
//.clip(RoundedCornerShape(12.dp))：使用圆角形状裁剪视图。
//.background(MaterialTheme.colorScheme.surfaceVariant)：设置视图背景，使用主题的颜色方案。
@Composable
private fun ExerciseGif(gifUrl: String) {
    AndroidView(
        factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
                Glide.with(context)
                    .asGif()
                    .load(gifUrl)
                    .into(this)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    )
}

//@Composable private fun ExerciseTitle(name: String)：定义一个私有的 Composable 函数显示锻炼名称。
//Text：用来显示文本。
//text = name：显示名称。
//style = MaterialTheme.typography.headlineMedium：使用主题中的中等标题样式。
//textAlign = TextAlign.Center：文本居中对齐。
//color = MaterialTheme.colorScheme.onBackground：文本颜色使用主题定义的背景色。
//modifier：设置布局属性，填充最大宽度和上下内边距（8dp）。
@Composable
private fun ExerciseTitle(name: String) {
    Text(
        text = name,
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

//@OptIn(ExperimentalLayoutApi::class)：表示该函数使用实验性布局 API。
//FlowRow：用于将子项（这里是自定义的 Chip）按流布局。
//modifier：设置填充最大宽度和内边距。
//horizontalArrangement = Arrangement.spacedBy(8.dp)：设置子项的水平间隔为 8dp。
//verticalArrangement = Arrangement.spacedBy(8.dp)：设置子项的垂直间隔为 8dp。
//CustomChip：每个标签（身体部位、设备、目标）都显示为一个 Chip 组件。
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExerciseDetails(bodyPart: String, equipment: String, target: String) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomChip(label = bodyPart)
        CustomChip(label = equipment)
        CustomChip(label = target)
    }
}

//先检查 secondaryMuscles 列表是否为空，只有在不空的情况下才显示此部分。
//使用 Column 来垂直显示内容，包括标题和肌肉标签。
//Text：显示“Secondary Muscles:”的标题，使用主题中的标题样式。
//FlowRow：按流布局显示所有辅助肌肉，使用 CustomChip 显示每个肌肉的标签。
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SecondaryMusclesSection(secondaryMuscles: List<String>) {
    if (secondaryMuscles.isNotEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Secondary Muscles:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                secondaryMuscles.forEach { muscle ->
                    CustomChip(label = muscle)
                }
            }
        }
    }
}

//检查 instructions 列表是否为空，若不为空则展示此部分。
//使用 Column 来垂直排列说明内容。
//Text：首先显示“Instructions:”的标题，使用主题中的标题样式。
//使用 forEachIndexed 遍历说明列表，为每个说明添加一个序号并显示，使用主题中对应样式的文本。
@Composable
private fun InstructionsSection(instructions: List<String>) {
    if (instructions.isNotEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Instructions:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            instructions.forEachIndexed { index, instruction ->
                Text(
                    text = "${index + 1}. $instruction",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

//@Preview(showBackground = true)：提供一个 UI 预览用于 Android Studio 的设计视图。
//@Composable private fun ExerciseContentPreview()：定义一个私有的预览 Composable。
//ExerciseContent(...)：调用 ExerciseContent，并传入一个示例 Exercise 对象，以确保预览能正常显示。
@Preview(showBackground = true)
@Composable
private fun ExerciseContentPreview() {
    ExerciseContent(
        StaticExercise(
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
        )
    )
}