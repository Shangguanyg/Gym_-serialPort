package com.ganainy.gymmasterscompose.ui.screens.signup


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


data class SignUpFormData(
    val uid: String = "", // id of the user in the database
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val isUsernameValid: Boolean = false,
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val username: String? = null
) {
}

sealed class SignUpUiState {
    object Initial : SignUpUiState()
    object Loading : SignUpUiState()
    data class Error(val messageStringResource: Int) : SignUpUiState()
    object Success : SignUpUiState() // this state means either user signed up successfully or
    // account already exists on device
}

//@HiltViewModel: 注解表示这是一个可以通过 Hilt 进行依赖注入的 ViewModel 类。
//class SignUpViewModel: 定义视图模型的类名。
//构造函数:
//@Inject constructor(...): 使用 Hilt 自动注入依赖的构造函数。
//private val authRepository: AuthRepository: 注入身份验证仓库，用于处理用户的注册和身份验证操作。
@HiltViewModel
class SignUpViewModel @Inject constructor(private val authRepository: AuthRepository) :
    ViewModel() {

    //_uiState: 使用 MutableStateFlow 定义内部状态流。初始值为 SignUpUiState.Initial，表示注册界面处于初始状态。
    //uiState: 将内部状态流暴露为只读的 StateFlow，允许外部组件观察状态变化。
    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Initial)
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    //_signUpFormData: 使用 MutableStateFlow 存储用户注册表单数据，初始值为 SignUpFormData() 实例。
    //signUpFormData: 暴露表单数据的只读状态流，供 UI 组件观察。
    private val _signUpFormData = MutableStateFlow(SignUpFormData())
    val signUpFormData: StateFlow<SignUpFormData> = _signUpFormData.asStateFlow()

    //createAccount(): 处理用户创建账户的逻辑。
    //_uiState.value = SignUpUiState.Loading: 立即更新 UI 状态为加载中，表示正在进行注册操作。
    //viewModelScope.launch: 在 ViewModel 的协程作用域内启动一个新的协程以处理异步操作。
    fun createAccount() {
        _uiState.value = SignUpUiState.Loading
        viewModelScope.launch {
            //when {}: 根据表单数据验证的结果进行不同的处理。
            //条件检查:
            //_signUpFormData.value.isUsernameValid: 如果用户名无效，更新状态为错误，并显示相应的错误信息（资源 ID）。
            //_signUpFormData.value.isEmailValid: 如果电子邮件无效，更新状态为错误。
            //_signUpFormData.value.isPasswordValid: 如果密码无效，更新状态为错误。
            //else: 如果所有字段有效，则调用 authenticateWithFirebase() 方法进行身份验证。
            when {
                !_signUpFormData.value.isUsernameValid ->
                    _uiState.value = SignUpUiState.Error(R.string.invalid_username)

                !_signUpFormData.value.isEmailValid ->
                    _uiState.value = SignUpUiState.Error(R.string.invalid_email)

                !_signUpFormData.value.isPasswordValid ->
                    _uiState.value = SignUpUiState.Error(R.string.invalid_password)

                else -> authenticateWithFirebase()
            }
        }
    }

    //authenticateWithFirebase(): 用于与 Firebase 进行用户注册。
    //调用仓库的创建用户方法: 传递电子邮件和密码并异步执行。
    //onSuccess: 如果创建用户成功，继续进行下一步操作。
    //获取当前用户 ID: 使用 authRepository.getCurrentUserId() 获取新创建用户的 ID。如果返回的 ID 为 null，就抛出异常。
    private suspend fun authenticateWithFirebase() {

        authRepository.createUserAuth(
            _signUpFormData.value.email,
            _signUpFormData.value.password
        ).onSuccess {
            // createUser success, save user data in db and update ui
            val userId: String = authRepository.getCurrentUserId()
                ?: throw Exception("authenticateWithFirebase: com.ganainy.gymmasterscompose.ui.theme.models.User ID is null")

            _signUpFormData.update {
                it.copy(
                    uid = userId
                )
            }
            saveUserInfo()
        }.onFailure {
            //  sign up fails
            _uiState.value = SignUpUiState.Error((R.string.authentication_failed))
        }
    }

    private suspend fun saveUserInfo(): ResultWrapper<Unit> {
        return authRepository.createUserProfile(
            email = _signUpFormData.value.email,
            displayName = _signUpFormData.value.displayName
        ).let {
            if (it is ResultWrapper.Success) {
                _uiState.value = SignUpUiState.Success
                ResultWrapper.Success(Unit)
            } else {
                _uiState.value = SignUpUiState.Error((R.string.create_account_failed))
                ResultWrapper.Error(Exception("saveUserInfo: failed to save user info"))
            }
        }
    }

    fun updatePassword(password: String) {
        _signUpFormData.update {
            it.copy(
                password = password, isPasswordValid =
                Utils.isValidFieldLength(password, Constants.MINIMUM_PASSWORD_LENGTH)
            )
        }
    }


    fun updateDisplayName(displayName: String) {
        _signUpFormData.update {
            it.copy(
                displayName = displayName,
                isUsernameValid = Utils.isValidFieldLength(
                    displayName,
                    Constants.MINIMUM_NAME_LENGTH
                )
            )
        }
    }

    fun updateEmail(email: String) {
        _signUpFormData.update {
            it.copy(
                email = email,
                isEmailValid = Utils.isValidEmail(email)
            )
        }
    }


}


