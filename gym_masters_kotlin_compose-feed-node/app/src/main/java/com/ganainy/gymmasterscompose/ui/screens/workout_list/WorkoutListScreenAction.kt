package com.ganainy.gymmasterscompose.ui.screens.workout_list

import com.ganainy.gymmasterscompose.ui.models.workout.Workout
// 这段代码定义了一个密封接口 WorkoutListScreenAction，用于表示在应用的锻炼列表屏幕上发生的各种用户操作。
// 密封类和密封接口常用于状态和事件处理，使得代码的可读性和可维护性更高。
sealed interface WorkoutListScreenAction {
    data class OnQueryChange(val query: String) : WorkoutListScreenAction
    data class OnSortPreferenceSelected(val sortType: SortType) : WorkoutListScreenAction
    data class OnWorkoutClick(val workout: Workout, val isLiked: Boolean, val isSaved: Boolean) :
        WorkoutListScreenAction
    data object OnRetry : WorkoutListScreenAction
    data class OnWorkoutLike(val workout: Workout) : WorkoutListScreenAction
    data class OnWorkoutSave(val workout: Workout) : WorkoutListScreenAction
}