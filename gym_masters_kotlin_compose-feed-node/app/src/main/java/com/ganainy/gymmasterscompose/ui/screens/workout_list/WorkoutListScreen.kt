package com.ganainy.gymmasterscompose.ui.screens.workout_list

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ganainy.gymmasterscompose.R
import com.ganainy.gymmasterscompose.ui.models.workout.Workout
import com.ganainy.gymmasterscompose.ui.shared_components.CustomTopAppBar
import com.ganainy.gymmasterscompose.ui.shared_components.LoadingIndicator
import com.ganainy.gymmasterscompose.utils.MockData

//@Composable: 该注解说明这是一个可组合函数，意味着可以用于构建用户界面。
//参数:
//navigateToWorkoutDetails: 函数类型参数，用于处理导航到锻炼详情页面的事件，传递锻炼对象及其状态（是否被喜欢、是否被保存）。
@Composable
fun WorkoutListScreen(
    navigateToWorkoutDetails: (Workout, isLiked: Boolean, isSaved: Boolean) -> Unit,
    navigateToRacingGame: () -> Unit
) {


    //从 Hilt 获取 ViewModel: 通过 hiltViewModel() 获取 WorkoutListViewModel 实例，Dagger/Hilt 会自动处理依赖注入。
    val viewModel: WorkoutListViewModel = hiltViewModel()

    //收集 UI 状态: 使用 collectAsState() 从 ViewModel 中收集 UI 数据，确保 UI 根据状态变化自动更新。
    val uiData by viewModel.uiData.collectAsState()

    //调用内容组件: 通过 WorkoutListContent 将 UI 数据和操作回调传递下去，具体负责呈现锻炼内容和处理用户的交互动作。
    //onAction 参数: 收集不同的用户操作，如选择排序偏好、修改搜索查询、点击锻炼、点赞、保存等。每个操作都会调用 ViewModel 的对应方法来处理业务逻辑。
    WorkoutListContent(
        uiData = uiData,
        onAction = { action ->
            when (action) {
                is WorkoutListScreenAction.OnSortPreferenceSelected -> viewModel.updateSortPreference(
                    action.sortType
                )

                is WorkoutListScreenAction.OnQueryChange -> viewModel.updateSearchQuery(action.query)
                is WorkoutListScreenAction.OnWorkoutClick -> {
                    Log.d("workout", "WorkoutListScreenAction.OnWorkoutClick in WorkoutListContent")
                    navigateToWorkoutDetails(
                        action.workout,
                        action.isLiked,
                        action.isSaved
                    )
//                    navigateToRacingGame()
                }

                is WorkoutListScreenAction.OnWorkoutLike ->
                    {   Log.d("workout", "WorkoutListScreenAction.OnWorkoutLike in WorkoutListContent")
                    viewModel.toggleWorkoutLike(action.workout)
                }
                is WorkoutListScreenAction.OnWorkoutSave -> {
                    Log.d("workout", "WorkoutListScreenAction.OnWorkoutSave in WorkoutListContent")
                    viewModel.toggleWorkoutSave(action.workout)
                }
                WorkoutListScreenAction.OnRetry -> viewModel.retry()
            }
        },
    )
}

//@OptIn(ExperimentalMaterial3Api::class): 该注解指示使用了 Material 3 的实验性 API。
//参数:
//uiData: WorkoutListUiData: UI 状态数据，包含锻炼的信息和当前状态。
//onAction: 操作回调，用于处理用户交互。
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutListContent(
    uiData: WorkoutListUiData,
    onAction: (WorkoutListScreenAction) -> Unit
) {
    //Scaffold(...): 使用 Material Design 提供的 Scaffold 布局，它便于设置基本 UI 结构，如顶部应用栏、底部导航等。
    //topBar: 为 Scaffold 添加自定义顶部应用栏 (CustomTopAppBar)，为锻炼列表设置标题。
    Scaffold(
        topBar = {
            CustomTopAppBar(
               title = stringResource(R.string.workouts),
                         )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        //Column(...): 使用垂直方向排列组件的布局。
        //修饰符:
        //fillMaxSize(): 使列占据整个可用空间。
        //padding(innerPadding): 使用 Scaffold 提供的内边距避免与状态栏或其他组件重叠。
        //background(...): 设置背景色为当前主题的背景色。
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {

            // Search and Filter Section
            // SearchBar(...): 自定义搜索栏组件，允许用户输入搜索查询和选择排序选项。
            //searchQuery: 将 UI 状态中的搜索查询字符串传入搜索栏。
            //onQueryChange: 当用户修改搜索栏内容时，发出更新查询的操作。
            //修饰符:
            //fillMaxWidth(): 使搜索栏宽度填满。
            //过滤选项:
            //filterOptionList: 获取所有排序类型的名称并传递给搜索栏。
            //selectedFilterOption: 传入当前选择的排序类型。
            //onOptionSelected: 当选择排序选项时，发出相应的操作以更新排序的偏好设置。
            //背景色和形状: 使用 Material Theme 设置背景颜色及边角的圆形。
            SearchBar(
                searchQuery = uiData.searchQuery,
                onQueryChange = {
                    onAction(WorkoutListScreenAction.OnQueryChange(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .animateContentSize(),
                filterOptionList = SortType.entries.map { it.name },
                selectedFilterOption = uiData.sortType.name,
                onOptionSelected = {
                    onAction(WorkoutListScreenAction.OnSortPreferenceSelected(SortType.valueOf(it)))
                },
                shape = RoundedCornerShape(12.dp),
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    inputFieldColors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            )

            // Content Section
            // Box(...): 用于包裹内容区，设置权重使其填满整个可用空间。
            // Crossfade(...): 使用渐变效果显示加载状态的转换，结合当前状态（isLoading）。
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Crossfade(
                    targetState = uiData.isLoading,
                    animationSpec = tween(300)
                ) { isLoading ->
                    when {
                        //加载指示器: 若正在加载中，展示圆形加载指示器。
                        isLoading -> {
                            LoadingIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        //错误内容处理: 如果发生错误且 UI 数据中存在错误信息，则展示错误组件。
                        //onRetry: 提供重试操作的回调。
                        uiData.error != null -> {
                            ErrorContent(
                                message = uiData.error,
                                onRetry = {
                                    onAction(WorkoutListScreenAction.OnRetry)
                                },
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp)
                            )
                        }

                        //显示空内容: 如果锻炼列表为空，提示没有找到锻炼。
                        else -> {
                            when {
                                uiData.workoutWithStatusList.isEmpty() -> {
                                    EmptyContent(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(16.dp)
                                    )
                                }
                                //显示锻炼列表: 当状态正常且数据存在时，调用 WorkoutList 显示锻炼项。
                                //动作处理:
                                //onWorkoutClick: 点击某个锻炼项时，发出点击事件。
                                //onWorkoutLike和onWorkoutSave: 分别处理点赞和保存操作。
                                else -> {
                                    WorkoutList(
                                        workoutList = uiData.workoutWithStatusList,
                                        onWorkoutClick = { workout, isLiked, isSaved ->
                                            Log.d("Workout", "onWorkoutClick in WorkoutListContent")
                                            onAction(
                                                WorkoutListScreenAction.OnWorkoutClick(
                                                    workout = workout,
                                                    isLiked = isLiked,
                                                    isSaved = isSaved
                                                )
                                            )
                                        },
                                        onWorkoutLike = {
                                            Log.d("Workout", "onWorkoutLike in WorkoutListContent")
                                            onAction(WorkoutListScreenAction.OnWorkoutLike(it))
                                        },
                                        onWorkoutSave = {
                                            Log.d("Workout", "onWorkoutSave in WorkoutListContent")
                                            onAction(WorkoutListScreenAction.OnWorkoutSave(it))
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


//@OptIn(ExperimentalFoundationApi::class): 使用此注解表示代码中使用了 Jetpack Compose 的实验性基础设施 API。
//@Composable: 指明这是一个可组合函数，用于构建 UI。
//参数:
//workoutList: List<WorkoutWithStatus>: 传入锻炼与状态的列表，为每个锻炼项目提供数据。
//onWorkoutClick: (Workout, Boolean, Boolean) -> Unit: 点击锻炼项时的操作回调。
//onWorkoutLike: (Workout) -> Unit: 点赞锻炼的操作回调。
//onWorkoutSave: (Workout) -> Unit: 保存锻炼的操作回调。
//modifier: Modifier = Modifier: 可变修饰符，可用于定制该组件的外观。
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WorkoutList(
    workoutList: List<WorkoutWithStatus>,
    onWorkoutClick: (Workout, Boolean, Boolean) -> Unit,
    onWorkoutLike: (Workout) -> Unit,
    onWorkoutSave: (Workout) -> Unit,
    modifier: Modifier = Modifier
) {
    //LazyColumn(...): 使用 LazyColumn 来展示滚动的锻炼项列表，提供高效的项渲染。
    //修饰符:
    //modifier: 将外部传入的修饰符应用于该组件。
    //contentPadding: 在纵向上设置组件的上下填充，以便给外部提供额外的空间。
    //verticalArrangement: 设置垂直方向的项之间的间距。
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        //items(...): 遍历传入的 workoutList 列表，动态生成每个项目的 UI。
        //key: 提供项的唯一标识符，允许 Compose 更好地处理变化。这是用锻炼的 ID 来唯一确定每个项。
        items(
            items = workoutList,
            key = { it.workout.id }
        ) { workout ->
            //WorkoutPreviewComposable(...): 调用另一个可组合函数，显示每个锻炼的详细信息和状态。
            //workoutWithStatus = workout: 将当前遍历到的锻炼对象传递给组件。
            WorkoutPreviewComposable(
                workoutWithStatus = workout,
                //previewOnlyParams: 将点赞、保存和点击事件处理回调传递给锻炼预览组件，方便它在用户界面中相应操作。
                previewOnlyParams = PreviewOnlyParams(
                    onWorkoutLike = onWorkoutLike,
                    onWorkoutSave = onWorkoutSave,
                    onWorkoutClick = onWorkoutClick,
                ),
                modifier = Modifier
                    //.animateItemPlacement()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable {
                        onWorkoutClick(
                            workout.workout,
                            workout.isLiked,
                            workout.isSaved
                        )
                    }
            )
        }
    }
}

//可组合函数：定义用于展示错误信息的内容。
//参数:
//message: String: 错误信息文本。
//onRetry: () -> Unit: 重试操作的回调函数。
//modifier: Modifier = Modifier: 可选修饰符，默认为无修饰。
@Composable
private fun ErrorContent(
    message: String, onRetry: () -> Unit, modifier: Modifier = Modifier
) {
    //Column 布局: 使用列布局，将错误内容垂直排列。
    //    修饰符:
    //    fillMaxSize(): 填满整个可用空间。
    //    padding(16.dp): 为列内容添加 16dp 的内边距。
    //    对齐属性:
    //    horizontalAlignment: 水平居中对齐。
    //    verticalArrangement: 垂直方向上居中。
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //错误信息: 使用 Text 组件显示传入的错误信息。应用主题中的文本样式。
        Text(
            text = message, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center
        )
        //Spacer: 一种布局组件，创建 16dp 的垂直间隔。
        //重试按钮: 定义一个按钮，点击时调用 onRetry，以便用户可以尝试重新加载数据。
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("重新尝试")
        }
    }
}

//EmptyContent(modifier: Modifier = Modifier): 定义一个可组合函数，用于处理没有可用锻炼时的 UI。
//Column 布局: 水平和垂直居中显示内容。
@Composable
private fun EmptyContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //提示消息: 显示 "No workouts found" 提示文本，告知用户没有找到锻炼。使用当前主题的主要文本样式。
        Text(
            "No workouts found",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}



//OutlinedTextField(...): 创建一个轮廓文本输入框，用于输入搜索查询。
//参数:
//value: 当前输入框的值。
//onValueChange: 当输入框内容变化时的回调。
//placeholder: 占位字符串提示用户输入内容。
//leadingIcon: 文本框前的图标，使用默认的搜索图标。
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    filterOptionList: List<String>,
    selectedFilterOption: String,
    onOptionSelected: (String) -> Unit,
    shape: RoundedCornerShape,
    colors: SearchBarColors

) {
    Row(
        modifier = modifier, verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("搜索训练记录") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            singleLine = true,

        )
        //Spacer: 创建水平间隔确保美观。
        //FilterMenu(...): 调用过滤菜单组件，提供可选择的过滤选项。
        Spacer(modifier = Modifier.width(8.dp))
        FilterMenu(
            options = filterOptionList,
            selectedOption = selectedFilterOption,
            onOptionSelected = onOptionSelected
        )
    }
}

//@Composable: 将该方法标记为可组合。
//状态管理:
//var expanded by remember { mutableStateOf(false) }: 管理下拉菜单的展开状态。
//Box 布局: 用于包裹图标，处理下拉菜单行为。
@Composable
fun FilterMenu(
    options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter")
        }

        //DropdownMenu(...): 创建下拉菜单，参数确定其展开与关闭状态。
        //DropdownMenuItem(...): 遍历所有过滤选项，创建可选择的菜单项。点击时调用回调并关闭菜单。
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onOptionSelected(option)
                    expanded = false
                })
            }
        }
    }
}

//@Preview: 生成一个预览，显示该组件的 UI。
//构造示例数据: 通过 WorkoutListUiData 和 WorkoutWithStatus 创建一组样本数据。
@Preview(showBackground = true)
@Composable
fun PreviewWorkoutListScreen() {
    WorkoutListContent(
        uiData = WorkoutListUiData(
            workoutWithStatusList = listOf(
                WorkoutWithStatus(
                    workout = Workout(
                        id = "1",
                        title = "Workout 1",
                        description = "This is a workout",
                        imageUrl = "https://firebasestorage.googleapis.com/v0/b/gym-masters.appspot.com/o/workouts%2F-Myv9QdZ0nK7WYV3JpQJk%2Fimage?alt=media&token=0e0f9f5b-9d3e-4e1e-94f1-6c9e0d7a5f0c",
                        workoutExerciseList = MockData.sampleWorkoutExerciseList
                    ),
                    isLiked = false,
                    isSaved = false
                ),
                WorkoutWithStatus(
                    workout = Workout(
                        id = "2",
                        title = "Workout 2",
                        description = "This is another workout",
                        imageUrl = "",
                        workoutExerciseList = emptyList()
                    ),
                    isLiked = false,
                    isSaved = false
                )
            ),
            isLoading = false,
            error = null,
            searchQuery = ""
        ),
        onAction = {}
    )
}

//另一个预览函数: 提供一个空列表的情况，以展示界面在无锻炼数据时的表现。
@Preview(showBackground = true)
@Composable
fun PreviewWorkoutListEmptyScreen() {
    WorkoutListContent(
        uiData = WorkoutListUiData(
            workoutWithStatusList = emptyList(),
            isLoading = false,
            error = null,
            searchQuery = "",
            sortType = SortType.NEWEST
        ),
        onAction = {}
    )
}



