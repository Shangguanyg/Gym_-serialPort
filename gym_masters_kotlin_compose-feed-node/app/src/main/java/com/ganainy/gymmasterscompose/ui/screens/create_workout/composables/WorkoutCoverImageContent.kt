//文件位于创建锻炼屏幕的组件包中，包含了必要的 Android 和 Compose 导入。
package com.ganainy.gymmasterscompose.ui.screens.create_workout.composables

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ganainy.gymmasterscompose.BuildConfig
import com.ganainy.gymmasterscompose.R
import com.ganainy.gymmasterscompose.ui.models.workout.Workout
import com.ganainy.gymmasterscompose.utils.MockData.sampleWorkout
import kotlinx.coroutines.launch

//这个组件是一个完整的图片选择和管理界面，具有以下特点：
//
//响应式设计：根据是否选择图片显示不同的UI状态
//流畅动画：使用缩放和透明度动画提供流畅的用户体验
//Material Design：遵循Material Design规范，使用主题颜色和图标
//功能完整：支持图片选择、预览、删除等完整功能
//错误处理：提供占位符和错误状态的图片显示
//可复用性：通过回调函数实现与父组件的解耦
//该组件专门用于锻炼封面图片的管理，是创建锻炼流程中的重要组成部分。

@Composable
//主函数 WorkoutCoverImageContent 是一个内部可组合函数，接受两个参数：
internal fun WorkoutCoverImageContent(
    //workout: 当前的锻炼对象
    workout: Workout,
    //onEditWorkout: 编辑锻炼的回调函数
    onEditWorkout: (Workout) -> Unit,
) {
    //创建协程作用域用于异步操作
    val coroutineScope = rememberCoroutineScope()

    // Animation for image scaling and fade-in
    // 设置两个动画属性：
    //
    //缩放动画：当有图片时缩放为1.0，无图片时为0.95
    //两个动画都使用300毫秒持续时间和快出慢入缓动效果。
    val animatedScale by animateFloatAsState(
        targetValue = if (workout.imagePath.isNotEmpty()) 1f else 0.95f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )

    //透明度动画：当有图片时透明度为1.0，无图片时为0.7
    val animatedAlpha by animateFloatAsState(
        targetValue = if (workout.imagePath.isNotEmpty()) 1f else 0.7f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )

    // Image picker launcher
    //使用 rememberLauncherForActivityResult 创建图片选择器：
    //使用 ActivityResultContracts.GetContent() 合约
    //当选择图片后，将 URI 转换为字符串并更新锻炼对象的 imagePath 属性
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                onEditWorkout(workout.copy(imagePath = uri.toString()))
            }
        }
    )

    //主容器布局

    //创建一个垂直居中的列布局
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Image preview box with shadow and rounded corners
        // 内部包含一个正方形的 Box（300dp x 300dp），具有以下特性
        //应用动画缩放和透明度效果
        //圆角16dp和8dp阴影效果
        //点击时启动图片选择器
        Box(
            modifier = Modifier
                .size(300.dp) // Fixed size for 1:1 aspect ratio
                .scale(animatedScale)
                .alpha(animatedAlpha)
                .clip(RoundedCornerShape(16.dp))
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable{
                    launcher.launch("image/*") // Launch image picker on click
                },
            contentAlignment = Alignment.Center
        ) {
            //当 workout.imagePath 不为空时
            if (workout.imagePath.isNotEmpty()) {
                // Display selected image with crossfade animation
                // 使用 AsyncImage 显示图片
                //填满整个父容器
                //使用 ContentScale.Crop 裁剪适配
                //设置占位符和错误图片为哑铃图标
                AsyncImage(
                    model = workout.imagePath,
                    contentDescription = "Workout cover image",
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop, // Crop to fill the box
                    placeholder = painterResource(R.drawable.dumbbell),
                    error = painterResource(R.drawable.dumbbell)
                )

                // Remove button
                // 在右上角添加删除按钮
                // 圆形半透明背景
                // 点击时清空 imagePath
                // 使用关闭图标
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            onEditWorkout(workout.copy(imagePath = ""))
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(24.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove image",
                        tint = MaterialTheme.colorScheme.onError
                    )
                }
            }
            //无图片时的占位符
            //当没有选择图片时显示占位符
            //半透明背景的 Box

            else {
                // Placeholder with hint for user
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        //相机图标（48dp 大小）
                        Icon(
                            imageVector = Icons.Default.Camera,
                            contentDescription = "Tap to select image",
                            modifier = Modifier
                                .size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        //"点击添加图片" 提示文本
                        Text(
                            text = "Tap to add image",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

//Preview 函数提供两个预览函数： WorkoutCoverImageContent.kt:161-175
@Preview(showBackground = true)
@Composable
//无图片状态的预览
private fun WorkoutCoverImageContentNoImageSelected_Preview() {
    WorkoutCoverImageContent(
        workout = sampleWorkout, onEditWorkout = { }
    )
}

//有图片状态的预览（使用示例图片URL）
@Preview
@Composable
private fun WorkoutCoverImageContentImageSelected_Preview() {
    WorkoutCoverImageContent(
        //workout = sampleWorkout.copy(imagePath = BuildConfig.SAMPLE_WORKOUT_IMAGE_URL),
        workout = sampleWorkout.copy(imagePath = "https://example.com/workout_image.jpg"),
        onEditWorkout = { }
    )
}