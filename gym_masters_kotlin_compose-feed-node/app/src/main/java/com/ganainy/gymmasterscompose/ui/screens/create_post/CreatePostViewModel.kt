package com.ganainy.gymmasterscompose.ui.screens.create_post

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganainy.gymmasterscompose.R
import com.ganainy.gymmasterscompose.ui.models.User
import com.ganainy.gymmasterscompose.ui.models.post.FeedPost
import com.ganainy.gymmasterscompose.ui.repository.IPostRepository
import com.ganainy.gymmasterscompose.ui.repository.IUserRepository
import com.ganainy.gymmasterscompose.ui.repository.ResultWrapper
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
// - 负责业务逻辑和状态管理

//状态管理
//CreatePostViewModel 使用 CreatePostUiState 数据类管理界面状态，包含加载状态、错误信息、用户信息、帖子内容和选中的图片。
data class CreatePostUiState(
    val isLoading: Boolean = false,
    val error: Int? = null,
    val isSuccess: Boolean = false,
    val user: User? = null,
    val feedPost: FeedPost = FeedPost(),
    val isPostButtonEnabled: Boolean = false,
    val selectedImages: List<Uri> = emptyList(),
)

//* CreatePost 屏幕的 ViewModel。
//*
//* 此 ViewModel 执行以下操作：
//* 1. 在屏幕初始化时获取当前用户。
//* 2. 监控帖子内容的变化并相应地更新 UI 状态。
//* 3. 在帖子内容不为空时启用发布按钮。
//* 4. 点击发布按钮时发布帖子。
//*
//* @property userRepository 用于获取当前用户的用户存储库。
//* @property postRepository 用于发布帖子的帖子存储库。
/**
 * ViewModel for the CreatePost screen.
 *
 * This ViewModel does the following:
 * 1. Fetches the current user when the screen is initialized.
 * 2. Monitors the post content for changes and updates the UI state accordingly.
 * 3. Enables the post button when the post content is not empty.
 * 4. Publishes the post when the post button is clicked.
 *
 * @property userRepository The user repository for fetching the current user.
 * @property postRepository The post repository for publishing the post.
 */

//依赖注入
//ViewModel 通过 Hilt 注入 IUserRepository 和 IPostRepository 两个核心依赖。
@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val userRepository: IUserRepository,
    private val postRepository: IPostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState = _uiState.asStateFlow()


    //初始化流程
    //在初始化时执行两个关键操作：获取当前用户信息和监控帖子内容变化。
    init {
        _uiState.update { it.copy(isLoading = true) }
        fetchCurrentUser()
        monitorPostContentChanges()
    }

    //    //* 监控帖子内容的变化并相应地更新 UI 状态。
    //    //
    //    //* 当内容发生变化时，它会使用新内容和提取的标签更新 `feedPost`。
    //    //* 它还会根据内容是否为空来启用或禁用帖子按钮。
    /**
     * Monitors changes in the post content and updates the UI state accordingly.
     *
     * This function observes changes in the `feedPost.content` field of the UI state.
     * When the content changes, it updates the `feedPost` with the new content and extracted hashtags.
     * It also enables or disables the post button based on whether the content is not empty.
     */

    //
    private fun monitorPostContentChanges() {
        _uiState
            .map { it.feedPost.content }
            .distinctUntilChanged()
            .onEach { content ->
                _uiState.update { currentState ->
                    currentState.copy(
                        feedPost = currentState.feedPost.copy(
                            content = content,
                            tags = extractHashtags(content)
                        ),
                        isPostButtonEnabled = content.isNotEmpty()
                    )
                }
            }
            .launchIn(viewModelScope)
    }


    /**
     * Fetches the current user and updates the UI state.
     *
     * This function launches a coroutine in the ViewModel scope to fetch the current user from the user repository.
     * It updates the UI state to indicate loading while the user is being fetched. If the fetch is successful,
     * it updates the UI state with the fetched user data. If there is an error, it updates the UI state with an error message.
     */
    private fun fetchCurrentUser() {
        viewModelScope.launch {
            try {
                when (val result = userRepository.getUser(userId = null)) {
                    is ResultWrapper.Success -> {
                        _uiState.update {
                            it.copy(
                                user = result.data,
                                isLoading = false
                            )
                        }
                    }

                    is ResultWrapper.Error -> {
                        _uiState.update {
                            it.copy(
                                error = R.string.error_fetching_user,
                                isLoading = false
                            )
                        }
                    }

                    is ResultWrapper.Loading -> _uiState.update { it.copy(isLoading = true) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = R.string.error_fetching_user,
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Publishes a post to the repository.
     *
     * This function retrieves the current `feedPost` and `user` from the UI state and launches a coroutine
     * to create the post using the `postRepository`. It updates the UI state based on the result of the post creation.
     * If the post creation is successful, it sets `isLoading` to false and `isSuccess` to true.
     * If there is an error, it sets `isLoading` to false and updates the `error` field with an appropriate message.
     */
    fun publishPost(onNavigateBack: () -> Unit) {

        val feedPost = _uiState.value.feedPost
        val postAuthor = _uiState.value.user
        val selectedImages = _uiState.value.selectedImages
        viewModelScope.launch {
            val result = postRepository.createPost(feedPost, postAuthor, selectedImages)
            when (result) {
                is ResultWrapper.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                    onNavigateBack()
                }

                is ResultWrapper.Error -> {
                    // Handle the error
                    _uiState.update {
                        it.copy(
                            error = R.string.error_creating_post,
                            isLoading = false
                        )
                    }
                }

                is ResultWrapper.Loading -> _uiState.update {
                    it.copy(
                        isLoading = true,
                    )
                }
            }
        }
    }


    fun updatePostContent(newPostContent: String) {
        _uiState.update { it.copy(feedPost = it.feedPost.copy(content = newPostContent)) }
    }

    fun onImagePicked(uris: List<Uri>) {
        _uiState.update { it.copy(selectedImages = uris) }
    }

    fun onImageRemoved(uri: Uri) {
        _uiState.update { it.copy(selectedImages = _uiState.value.selectedImages - uri) }
    }

    fun onImageSelected(uris: List<Uri>) {
        val newImages = (_uiState.value.selectedImages + uris).take(5) // Limit to 5 images
        _uiState.update { it.copy(selectedImages = newImages) }
    }

}