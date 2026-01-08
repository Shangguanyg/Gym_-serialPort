package com.ganainy.gymmasterscompose.ui.screens.signin


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ganainy.gymmasterscompose.R
import com.ganainy.gymmasterscompose.ui.shared_components.CustomPasswordTextField
import com.ganainy.gymmasterscompose.ui.shared_components.CustomTextField
import com.ganainy.gymmasterscompose.ui.shared_components.LoadingIndicator
import com.ganainy.gymmasterscompose.utils.Utils.showToast

//@Composable: 将此函数标记为可组合函数，允许使用 Compose 构建 UI。
//参数:
//navigateToSignUp: () -> Unit: 函数类型的参数，用于处理导航到注册屏幕的逻辑。
//onSignInSuccess: () -> Unit: 函数类型的参数，在用户成功登录后调用此函数。

// 这段代码定义了一个完整的登录界面的 UI 组件，遵循 MVVM 架构，使得视图和状态管理相分离。
// 通过 Jetpack Compose，这是一个响应式、流畅且用户友好的界面设计。它依靠 ViewModel 来处理用户输入和验证，提升了可维护性和扩展性。
@Composable
fun SignInScreen(
    navigateToSignUp: () -> Unit,
    onSignInSuccess: () -> Unit,
) {
    //val viewModel: SignInViewModel = hiltViewModel(): 获取 SignInViewModel 的实例，使用 Hilt 提供的 hiltViewModel 函数进行注入。
    //val uiState by viewModel.uiState.collectAsState(): 从 ViewModel 中收集 UI 状态，自动更新界面以反应状态变化。
    //val formData by viewModel.formData.collectAsState(): 收集登录表单的数据，包括用户输入的电子邮件和密码。
    val viewModel: SignInViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val formData by viewModel.formData.collectAsState()

    //val context = LocalContext.current: 获取当前的 Android 上下文，方便在需要的地方使用。
    val context = LocalContext.current

    // Show sign-in form with current input and validation states

    //SignInScreenContent(...): 调用可组合的内容组件，并传递表单数据及其他操作函数。
    //onLogIntoAccount = { -> viewModel.logIntoAccount() }: 当用户点击登录按钮时调用 ViewModel 的 logIntoAccount 方法进行登录操作。
    //onNavigateToSignUp = navigateToSignUp: 导航到注册页面的动作。
    //onEmailChange 和 onPasswordChange: 分别用于更新输入电子邮件和密码的函数，调用 ViewModel 的对应更新方法。
    SignInScreenContent(
        formData,
        onLogIntoAccount = { -> viewModel.logIntoAccount() },
        onNavigateToSignUp = navigateToSignUp,
        onEmailChange = { email -> viewModel.updateEmail(email) },
        onPasswordChange = { password -> viewModel.updatePassword(password) },
    )


    // when (uiState): 根据 UI 状态的不同渲染不同的内容。
    //is SignInUiState.Initial: 如果状态是初始状态，此部分可以留空，通常表示空白表单。
    //is SignInUiState.Loading: 表示正在加载，调用 LoadingIndicator() 显示加载动画。
    //is SignInUiState.Error: 如果登录过程出现错误，使用 stringResource 获取错误消息的字符串资源，并通过 showToast 显示在界面上。
    //is SignInUiState.Success: 当登录成功后，使用 LaunchedEffect 自动执行 onSignInSuccess() 回调，可能会导航到应用的主界面。
    when (uiState) {
        is SignInUiState.Initial -> {
            // Show initial state (empty form)
        }

        is SignInUiState.Loading -> {
            // Show loading indicator
            LoadingIndicator()
        }

        is SignInUiState.Error -> {
            // Show error message
            val errorMessage= stringResource((uiState as SignInUiState.Error).messageStringResource)
            showToast(context,
                errorMessage,
            )
        }

        is SignInUiState.Success -> {
            LaunchedEffect(Unit) {
                onSignInSuccess()
            }
        }
    }
}

//@Composable: 将该函数标记为可组合。
//参数:
//formData: SignInFormData: 用于传递登录表单中的数据（电子邮件和密码等）。
//onLogIntoAccount, onNavigateToSignUp, onEmailChange, onPasswordChange: 对应的操作函数。
@Composable
fun SignInScreenContent(
    formData: SignInFormData,
    onLogIntoAccount: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
    //Surface(...): 定义背景布局，设置为填满屏幕并应用主题的背景色。
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Column(...): 使用 Column 来垂直排列子组件。
        // horizontalAlignment 和 verticalArrangement: 设置文本的对齐方式和垂直排列位置。
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            // Text(...): 显示标题为 “Sign In”，使用主题字体大小和加粗样式。
            Text(
                text = stringResource(R.string.sign_in),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            //Spacer(...): 留出垂直空间。此处用来在标题及欢迎文本之间留出间距，使得界面更美观。
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.welcome_back_to_gym_masters),
                color = Color.Black,
                fontSize = 40.sp,
            )
            Spacer(modifier = Modifier.height(32.dp))

            // CustomTextField(...): 使用自定义的输入框组件来收集用户输入的电子邮件：
            //text: 传入当前的电子邮件。
            //label: 输入框的标签。
            //isError: 通过数据验证状态确定是否为错误状态，允许对于输入有效性进行直观反馈。
            //errorText: 提供错误提示信息。
            //onValueChange: 输入发生变化时的回调，更新 ViewModel 中的电子邮件。
            CustomTextField(
                text = formData.email, label = stringResource(R.string.email),
                //isError = !formData.isEmailValid,
                errorText = stringResource(R.string.email_invalid_format),
                options = KeyboardOptions(keyboardType = KeyboardType.Email),
                onValueChange = onEmailChange
            )

            //CustomPasswordTextField(...): 设定包含密码的自定义输入框组件，具有与电子邮件字段相似的参数配置。
            CustomPasswordTextField(
                text = formData.password, label = stringResource(R.string.password),
                //isError = !formData.isPasswordValid,
                errorText = stringResource(R.string.password_must_be_atleast_six_characters_long),
                onValueChange = onPasswordChange
            )


            //Row(...): 用于在屏幕上水平排列“想要创建账户”与“注册”按钮。
            //TextButton(...): 点击时将触发注册导航。
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.want_to_create_account),
                    fontSize = 14.sp,
                )
                TextButton(onClick = onNavigateToSignUp) {
                    Text(text = stringResource(R.string.sign_up_here))
                }
            }
            //Button(...): 定义一个按钮组件，点击后触发登录操作。
            //onClick: 连接到 ViewModel 的 logIntoAccount() 方法。
            //modifier: 使用填满宽度及一些内边距。
            //Text(...): 按钮内显示的文字。
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onLogIntoAccount,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.sign_in),
                    fontSize = 24.sp
                )
            }
        }
    }
}

