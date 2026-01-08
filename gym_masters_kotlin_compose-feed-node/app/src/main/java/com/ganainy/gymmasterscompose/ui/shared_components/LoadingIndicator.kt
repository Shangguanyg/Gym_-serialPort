package com.ganainy.gymmasterscompose.ui.shared_components
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay


//这段代码定义了一个可组合函数 LoadingIndicator，用于在应用加载时显示一个环形进度指示器和可选消息。
// 我们将逐行详细分析这段代码的实现，以便理解其功能和用法。
//这段注释提供了函数的简要描述、参数的用途和行为。
//它说明了该函数是一个可组合函数，用于显示加载指示器，并指出消息如何更新。
/**
 * A composable function that displays a loading indicator. It displays a circular
 * progress indicator and optional message. The message is updated every 500ms
 * to create a "..." effect.
 *
 * @param message the message to display while loading. If null, no message is displayed.
 * @param modifier the modifier to apply to the indicator.
 */

//@Composable：标记该函数为可组合函数，表示可以在 Jetpack Compose 中用于构建 UI。
//fun LoadingIndicator(...)：定义 LoadingIndicator 函数。
//参数：
//message: String? = null：可选参数，表示在加载时显示的消息，如果不提供，则默认为 null。
//modifier: Modifier = Modifier.testTag("loading_indicator")：用于修饰加载指示器的样式，默认是一个带有测试标记的 Modifier。
@Composable
fun LoadingIndicator(message: String? = null, modifier: Modifier = Modifier.testTag("loading_indicator")) {
    //状态声明：使用 remember 和 mutableStateOf 声明一个可变状态 loadingMessage，它用于跟踪要显示的加载消息。
    //message ?: ""：如果 message 为 null，则初始化为空字符串。
    var loadingMessage by remember { mutableStateOf(message ?: "") }

    // Coroutine to update the dots in the message dynamically

    //LaunchedEffect：一个用于在 Composable 内部 запуска 协程的 API。这里它依赖于 message，以便在 message 变化时重启协程。
    //条件检查：仅在 message 不为 null 时开始动画，以避免在没有消息时进行无意义的更新。
    //无限循环：while (true) 创建一个无限循环。
    //delay(500)：延迟 500 毫秒，表示每半秒更新一次消息。
    //loadingMessage = when (loadingMessage) { ... }：根据当前的 loadingMessage 更新内容，以创建“...”的效果。
    //每次迭代中，消息在状态 $message. 和 $message... 之间循环切换。
    LaunchedEffect(message) {
        if (message != null) { // Only start the animation if a message is provided
            while (true) {
                delay(500)
                loadingMessage = when (loadingMessage) {
                    "$message." -> "$message.."
                    "$message.." -> "$message..."
                    else -> "$message."
                }
            }
        }
    }

    //Box：创建一个可组合的 Box，用于包裹内部内容。
    //modifier = modifier.fillMaxSize()：应用外部传入的修饰符，同时使其填满可用大小。
    //contentAlignment = Alignment.Center：居中对齐 Box 内的所有内容。
    Box(
        modifier = modifier
            .fillMaxSize()
            ,
        contentAlignment = Alignment.Center
    ) {
        // Centered content
        //        Column：创建一个垂直布局容器。
        //        horizontalAlignment = Alignment.CenterHorizontally：水平居中对齐列内的内容。
        //        verticalArrangement = Arrangement.Center：在垂直方向上居中对齐内容。

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //条件判断：检查 loadingMessage 是否为空或空白。
            //CircularProgressIndicator：如果没有消息，则显示一个圆形旋转进度条。
            //modifier = Modifier.size(50.dp)：设置进度条的大小为 50dp。
            if (loadingMessage.isNullOrBlank()) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp)
            )
            }

            // Show the text only if a message is provided
            // 条件判断：如果 loadingMessage 不为空或空白，则显示该消息。
            //Spacer(...)：在进度条和文本之间添加 8dp 的空白空间。
            //Text(...)：显示当前加载消息。
            //text = loadingMessage：用于显示的文本内容。
            //modifier = Modifier.padding(16.dp)：设置文本周围的内边距。
            //textAlign = TextAlign.Center：文本居中对齐。
            if (!loadingMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = loadingMessage,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


//@Preview：提供了预览功能，使得开发者可以在设计视图中看到组件效果。
//LoadingIndicatorPreview()：第一个预览函数，加载内容为 "Loading" 的加载指示器。
//LoadingIndicatorPreview2()：第二个预览函数，用于展示没有消息的加载指示器。
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,showSystemUi = true)
@Composable
fun LoadingIndicatorPreview() {
    LoadingIndicator(message = "Loading")
}

@Preview(showSystemUi = true)
@Composable
fun LoadingIndicatorPreview2() {
    LoadingIndicator()
}