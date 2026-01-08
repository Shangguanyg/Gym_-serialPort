package com.ganainy.gymmasterscompose.ui.screens.signin


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganainy.gymmasterscompose.Constants
import com.ganainy.gymmasterscompose.R
import com.ganainy.gymmasterscompose.ui.repository.AuthRepository
import com.ganainy.gymmasterscompose.ui.repository.ResultWrapper
import com.ganainy.gymmasterscompose.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
//这段代码定义了一个用于处理用户登录功能的 ViewModel，包括表单数据、UI 状态以及与身份验证相关的操作。代码中使用了 Kotlin 的数据类、密封类、协程等特性。
//SignInViewModel 是一个负责管理用户登录状态的 ViewModel，封装了用户输入、表单验证和身份验证的逻辑。
// 通过使用 Kotlin 协程、Room 和其他 Jetpack 组件，设计了一个结构清晰、易于维护的登录机制。使用 StateFlow 管理 UI 状态和表单数据，使得响应式编程变得简洁高效。

//data class SignInFormData: 定义了一个数据类，用于封装用户输入的登录表单信息。
//属性:
//email: String = "": 用户的电子邮件地址，默认为空字符串。
//password: String = "": 用户的密码，默认为空字符串。
//isEmailValid: Boolean = false: 用于存储邮箱的有效性状态，默认为 false。
//isPasswordValid: Boolean = false: 用于存储密码的有效性状态，默认为 false。
data class SignInFormData(
    val email: String = "",
    val password: String = "",
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false
)

//sealed class SignInUiState: 定义一个密封类，表示用户身份验证界面的不同状态。
//状态对象:
//object Initial: 初始状态，表示界面尚未进行任何操作。
//object Loading: 表示正在进行登录操作时的状态。
//data class Error(val messageStringResource: Int): 表示出现错误的状态，包含一个整型的字符串资源 ID，用于指向用户界面上的错误信息。
//object Success: 表示成功登录的状态。

sealed class SignInUiState {
    object Initial : SignInUiState()
    object Loading : SignInUiState()
    data class Error(val messageStringResource: Int) : SignInUiState()
    object Success : SignInUiState()
}

//@HiltViewModel: 注解表明这是一个 Hilt 支持的 ViewModel，为 Hilt 提供的依赖注入准备。
//构造函数:
//@Inject constructor(private val repository: AuthRepository): 通过依赖注入将 AuthRepository 实例注入到 ViewModel 中，用于处理身份验证的实际操作。
@HiltViewModel
class SignInViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {

    //私有变量 _uiState: 使用 MutableStateFlow 来存储 UI 状态，初始值设为 SignInUiState.Initial，以指示初始状态。
    //公开变量 uiState: 使用 asStateFlow() 方法将可变状态流转换为不可变状态流，供外部观察。
    private val _uiState = MutableStateFlow<SignInUiState>(SignInUiState.Initial)
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    //私有变量 _formData: 使用 MutableStateFlow 来存储表单数据，初始值为一个空的 SignInFormData 对象。
    //公开变量 formData: 公开的状态流，允许外部观察表单数据的变化。
    private val _formData = MutableStateFlow(SignInFormData())
    val formData: StateFlow<SignInFormData> = _formData.asStateFlow()

    init {
    }

    //updatePassword(password: String): 更新用户输入的密码并验证其有效性。
    //使用 update {} 方法: 以当前的表单数据为基础更新其状态。
    //it.copy(...): 使用 copy() 方法创建新的 SignInFormData 对象，并更新其中的 password 和 isPasswordValid。
    //Utils.isValidFieldLength(...): 调用工具方法验证密码长度是否符合要求。
    fun updatePassword(password: String) {
        _formData.update {
            it.copy(
                password = password, isPasswordValid =
                Utils.isValidFieldLength(password, Constants.MINIMUM_PASSWORD_LENGTH)
            )
        }
    }


    //updateEmail(email: String): 更新用户输入的电子邮件并验证其有效性。
    //类似于更新密码，it.copy(...) 方法用于创建一个包含新电子邮件和有效性状态的新表单数据对象。
    //Utils.isValidEmail(email): 调用工具方法验证电子邮件的格式是否有效。
    fun updateEmail(email: String) {
        _formData.update { it.copy(email = email, isEmailValid = Utils.isValidEmail(email)) }
    }

    //logIntoAccount(): 触发用户登录操作的函数。
    //viewModelScope.launch {...}: 在 ViewModel 的生命周期范围内启动一个新的协程。确保 UI 层不会因为长时间的操作而被阻塞。
    //validateForm(): 调用该方法来验证表单数据，如果有验证失败的情况，会将结果赋值给 validationResult。
    fun logIntoAccount() {
        viewModelScope.launch {

//            val validationResult = validateForm()
//            if (validationResult != null) {
//                _uiState.value = SignInUiState.Error(validationResult)
//                return@launch
//            }

            //repository.signInUser(...): 向 Authentication Repository 发送登录请求，传递电子邮件和密码。
            //处理登录结果:
            //when (result): 根据返回的结果类型更新 uiState。
            //is ResultWrapper.Success: 登录成功，设置 UI 状态为成功。
            //is ResultWrapper.Error: 登录失败，设置 UI 状态为错误，同时传入失败的错误信息资源 ID。
            //is ResultWrapper.Loading: 当请求处于加载中状态时，更新 UI 为加载状态。
            //val result = repository.signInUser(_formData.value.email, _formData.value.password)
            val result = repository.UserSignIn(_formData.value.email, _formData.value.password)
            _uiState.value = when (result) {
                is ResultWrapper.Success -> SignInUiState.Success
                is ResultWrapper.Error -> SignInUiState.Error(R.string.authentication_failed)
                is ResultWrapper.Loading -> SignInUiState.Loading
            }
        }
    }

    //validateForm(): 私有函数用于验证表单数据的有效性。
    //返回值类型: 整型的可空值，返回相应的错误信息资源 ID，或者在没有错误时返回 null。
    //when {}: 检查电子邮件和密码的有效性：
    //如果电子邮件无效返回错误消息。
    //如果密码无效返回错误消息。
    //否则返回 null，表示验证通过。
    private fun validateForm(): Int? {
        return when {
            !_formData.value.isEmailValid -> R.string.invalid_email
            !_formData.value.isPasswordValid -> R.string.invalid_password
            else -> null
        }
    }

}

