package com.ganainy.gymmasterscompose.ui.screens.game

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.ganainy.gymmasterscompose.R
import com.ganainy.gymmasterscompose.ui.screens.profile.ProfileScreenAction
import com.ganainy.gymmasterscompose.ui.shared_components.CustomTopAppBar


@Composable
fun GameScreen(
    navigateBack: () -> Unit
) {
    Column(
//        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
    ) {
        CustomTopAppBar(
            title = "个人设置",
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            //onNavigationClick = { onAction(ProfileScreenAction.NavigateBack) },
        )

        // 示例：使用 GameComponent
        GameComponent(
            imageResId = R.drawable.alienbullet, // 替换为您的图片资源ID
            gameName = "示例游戏",
            gameDescription = "这是一个游戏的介绍内容，您可以描述游戏的玩法、特色等信息。",
            onClick = {
                // 点击事件处理逻辑
            }
        )

    }
}


@Composable
fun GameComponent(
    imageResId: Int, // 图片资源ID
    gameName: String,
    gameDescription: String,
    onClick: () -> Unit
) {
    // 外部盒子用来包裹整个组件，并添加边框和背景
    Box(
        modifier = Modifier
            .width(360.dp)
            .height(360.dp)
            .border(BorderStroke(1.dp, Color(0xFFB3E5FC)), RoundedCornerShape(8.dp))
            .background(Color(0xFFE0E0E0))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 第一部分：游戏图片
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                modifier = Modifier
                    .width(350.dp)
                    .height(200.dp)
            )
            // 第二部分：游戏名称
            Text(
                text = gameName,
                fontSize = 28.sp,
                modifier = Modifier
                    .padding(8.dp)
                    .width(330.dp),
                textAlign = TextAlign.Center
            )
            // 第三部分：游戏介绍
            Text(
                text = gameDescription,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(8.dp)
                    .width(330.dp)
                    .height(200.dp),
                textAlign = TextAlign.Start
            )
        }
    }
}
