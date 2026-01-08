package com.ganainy.gymmasterscompose.ui.screens.static_test

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganainy.gymmasterscompose.R
import com.ganainy.gymmasterscompose.ui.models.User
import com.ganainy.gymmasterscompose.ui.models.post.FeedPost
import com.ganainy.gymmasterscompose.ui.repository.IPostRepository
import com.ganainy.gymmasterscompose.ui.repository.IUserRepository
import com.ganainy.gymmasterscompose.ui.repository.ResultWrapper
import com.ganainy.gymmasterscompose.ui.screens.create_post.CreatePostUiState
import com.ganainy.gymmasterscompose.utils.Utils.extractHashtags
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StaticTestResultUiState(
    val isLoading: Boolean = false,
    val error: Int? = null,
    val isSuccess: Boolean = false,
    val user: User? = null,
    val feedPost: FeedPost = FeedPost(),
    val isPostButtonEnabled: Boolean = false,
    val selectedImages: List<Uri> = emptyList(),
)

@HiltViewModel
class StaticTestResultViewModel @Inject constructor(
    private val userRepository: IUserRepository,
    //private val postRepository: IPostRepository
) : ViewModel() {

    var  Name: MutableState<String> = mutableStateOf("测试")

    var TotalWeight: MutableState<Int> = mutableStateOf(0)

    var LeftWeight: MutableState<Int> = mutableStateOf(0)

    var RightWeight: MutableState<Int> = mutableStateOf(0)

    var TestScore: MutableState<Int> = mutableStateOf(0)

    val CoPPoint: MutableState<Int> =   mutableStateOf(0)

    val EnclosingArea: MutableState<Int> =  mutableStateOf(0)

    var TotalLength: MutableState<Int> = mutableStateOf(0)

    var HorizontalLength: MutableState<Int> =  mutableStateOf(0)

    var VerticalLength: MutableState<Int> = mutableStateOf(0)

    var TotalSpeed: MutableState<Int> =  mutableStateOf(0)

    var HorizontalSpeed: MutableState<Int> =  mutableStateOf(0)

    var VerticalSpeed: MutableState<Int> =  mutableStateOf(0)

    var TotalMSD: MutableState<Int> =  mutableStateOf(0)

    var HorizontalMSD: MutableState<Int> =  mutableStateOf(0)

    var VerticalMSD: MutableState<Int> =  mutableStateOf(0)

    var HorizontalFrequency: MutableState<Int> =  mutableStateOf(0)

    var VerticalFrequency: MutableState<Int> =  mutableStateOf(0)


    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState = _uiState.asStateFlow()

}