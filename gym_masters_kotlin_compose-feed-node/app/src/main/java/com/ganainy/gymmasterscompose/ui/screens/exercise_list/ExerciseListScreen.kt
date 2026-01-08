package com.ganainy.gymmasterscompose.ui.screens.exercise_list

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ganainy.gymmasterscompose.ui.models.BodyPart
import com.ganainy.gymmasterscompose.ui.models.Equipment
import com.ganainy.gymmasterscompose.ui.models.Exercise
import com.ganainy.gymmasterscompose.ui.models.StaticExercise
import com.ganainy.gymmasterscompose.ui.models.TargetMuscle
import com.ganainy.gymmasterscompose.ui.shared_components.CustomTopAppBar
import com.ganainy.gymmasterscompose.ui.shared_components.ExerciseListItem
import com.ganainy.gymmasterscompose.ui.shared_components.ExerciseListItemData
import com.ganainy.gymmasterscompose.ui.shared_components.ExerciseListItemType
import com.ganainy.gymmasterscompose.ui.shared_components.LoadingIndicator
import kotlinx.coroutines.launch
// todo fix downloading all exercises on first startup takes too long (at least show progress and make cancelable)
// 这是一个 TODO 注释，提示开发者需要优化首次启动时下载所有锻炼的过程，建议至少提供进度指示并允许用户取消下载。

// @OptIn(ExperimentalMaterial3Api::class)：表示使用了实验性的 Material3 API 功能，表示开发者同意使用这些不稳定的 API。
// @Composable：标记此函数为可组合函数，意味着它可以与 Compose 结合使用。
@OptIn(ExperimentalMaterial3Api::class)
@Composable
// fun ExerciseListScreen(...)：定义名为 ExerciseListScreen 的可组合函数。该函数接受三个参数：
// navigateToExercise: (Exercise) -> Unit：一个函数类型参数，用于导航到单个锻炼的详细页面。
// navigateBack: () -> Unit：无参数的函数，用于处理返回操作。
// viewModel: ExerciseListViewModel = hiltViewModel()：接受一个 ViewModel 实例，默认从 Hilt 获取 ExerciseListViewModel。
fun ExerciseListScreen(
    navigateToExercise: (StaticExercise) -> Unit ,
   // navigateToExercise: (Exercise) -> Unit,
    navigateBack: () -> Unit,
    viewModel: ExerciseListViewModel = hiltViewModel()
) {
    Log.d("ExerciseListScreen", "begin of ExerciseListScreen")
    //val uiState by viewModel.uiState.collectAsState()：
    // 使用 collectAsState() 从 ViewModel 中获取 UI 状态，并将结果赋值给 uiState。
    //val filteredExercises by viewModel.filteredExercises.collectAsState()：
    // 同样地，从 ViewModel 获取过滤后的锻炼列表并赋值给 filteredExercises。通过 by 语法，自动监视状态。
    val uiState by viewModel.uiState.collectAsState()
    val filteredExercises by viewModel.filteredExercises.collectAsState()


    //val coroutineScope = rememberCoroutineScope()：创建一个协程作用域，用于在界面中启动协程。
    //val sheetState = rememberModalBottomSheetState()：创建一个用于管理模态底部表单的状态。
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    //var showBottomSheet by remember { mutableStateOf(false) }：声明一个用于控制模态底部表单显示状态的可变状态，初始为 false。
    //if (showBottomSheet) { ... }：如果 showBottomSheet 为 true，表示需要显示模态底部表单。
    //ModalBottomSheet(...)：创建一个模态底部表单，接受诸多参数。
    //onDismissRequest = { showBottomSheet = false }：当底部表单被关闭时，将 showBottomSheet 设置为 false。
    //sheetState = sheetState：传递底部表单的状态。
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            //FilterSearchUI 是一个可组合函数，用于显示过滤和搜索界面。
            //uiState = uiState：传递当前 UI 状态。
            //onUpdateFilters = viewModel::updateFilters：将过滤器更新函数传递给子组件。
            //onFiltersCleared = { ... }：当用户清除过滤器时，调用 ViewModel 的 clearFilters 方法，并隐藏底部表单。
            FilterSearchUI(
                uiState = uiState,
                onUpdateFilters = viewModel::updateFilters,
                onFiltersCleared = {
                    viewModel.clearFilters()
                    coroutineScope.launch { sheetState.hide() }
                    showBottomSheet = false
                }
            )
        }
    }

    //这是另一个可组合函数，用于显示具体的锻炼列表和相关信息。
    //传递了多个参数，包括当前的 UI 状态、过滤后的锻炼列表、导航函数和某些动作的处理函数。
    ExercisesContent(
        uiState = uiState,
        filteredExercises = filteredExercises,
       // filteredStaticExercise = filteredStaticExercises,
        onNavigateBack = navigateBack,
        //onNavigateBack = navigateToExercise,
        onShowFilters = { showBottomSheet = true },
        onQueryChange = viewModel::updateSearchQuery,
        onExerciseClick = {
            Log.d("ExerciseListScreen", "onExerciseClick in ExercisesContent")
            navigateToExercise
            },
        onRetry = viewModel::retry

    )
}

//这个函数接收多个参数以控制显示的内容：
//uiState: ExerciseListUiState：当前的 UI 状态。
//filteredExercises: List<Exercise>：用户过滤后的锻炼列表。
//其他参数用于控制行为，如导航、显示过滤器、搜索查询和重试。
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExercisesContent(
    uiState: ExerciseListUiState,
    filteredExercises: List<StaticExercise>,
    //filteredStaticExercise: List<StaticExercise>,
    onNavigateBack: () -> Unit,
    onShowFilters: () -> Unit,
    onQueryChange: (String) -> Unit,
    onExerciseClick: (StaticExercise) ->  Unit,
    //navigateToExercise: (StaticExercise) -> Unit,
   // onExerciseClick: (Exercise) ->  Unit ,
    onRetry: () -> Unit
) {
    Log.d("ExerciseListScreen", "begin of ExercisesContent")

    //Column 可组合函数用于垂直排列子组件，设置修饰符为 Modifier.fillMaxSize() 以填满可用空间。
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {


            //CustomTopAppBar 是定制的应用程序工具栏组件，用于显示标题和导航图标。
            //title = "Exercises"：标题文本为 "Exercises"。
            //navigationIcon = Icons.AutoMirrored.Filled.ArrowBack：返回按钮图标。
            //onNavigationClick = onNavigateBack：当点击返回图标时调用的函数。
            //actionIcons：包含需要在工具栏上显示的操作图标（例如过滤图标）。
            //onActionClicks：当点击操作图标时调用的函数。
            CustomTopAppBar(
                title =  "训练项目",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                exerciseIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick =  onNavigateBack,
                actionIcons = listOf(Icons.Default.FilterList),
                onActionClicks = listOf { onShowFilters() },
            )


            //SearchBar 是搜索功能的可组合组件，用于输入锻炼名称。
            //searchQuery = uiState.searchQuery：将当前的搜索查询传递到搜索栏。
            //onQueryChange = onQueryChange：在搜索查询变化时调用的函数。
            //modifier = Modifier.padding(16.dp).fillMaxWidth()：设置搜索栏的内边距并占满宽度。
            SearchBar(
                searchQuery = uiState.searchQuery,
                onQueryChange = onQueryChange,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )

            //使用 when 表达式检查当前的 UI 数据状态，以执行相应的 UI 操作。
            //is DataState.Loading：如果处于加载状态，显示加载指示器。
            when (uiState.dataState) {
                is DataState.Loading -> LoadingIndicator() // Show loading indicator

                //is DataState.Error：如果发生错误，调用 ErrorContent 显示错误信息。
                //提供错误消息并设置不显示重试按钮。
                is DataState.Error -> ErrorContent( // Error loading *cached* data
                    message = uiState.dataState.message,
                    showRetryButton = false, //  don't show retry for cache errors
                    onRetry = onRetry,
                )

                //is DataState.InitialDownloadRequired：当需要初始下载时调用 InitialDownloadContent，显示相应的消息和重试逻辑。
                is DataState.InitialDownloadRequired -> InitialDownloadContent(
                    message = uiState.dataState.message,
                    onRetry = onRetry // Use the same retry function (VM logic handles it)
                )

                //is DataState.Success：表示成功，显示锻炼列表。
                //if (filteredExercises.isEmpty())：检查过滤后的锻炼列表是否为空。
                //如果为空，调用 EmptyContent 显示没有锻炼的消息。
                //否则，调用 ExerciseList 显示结果。

//                is DataState.Success -> { // Success - display list or empty message
//                    if (filteredExercises.isEmpty()) {
//                        Log.d("ExerciseListScreen", "filteredExercises.isEmpty() in ExerciseListScreen")
//                        EmptyContent(
//                            hasActiveFilters = uiState.filters.activeFilters != ActiveFilters()
//                        )
//                    } else {
//                        Log.d("ExerciseListScreen", "filteredExercises is Not Empty in ExerciseListScreen")
//                        ExerciseList(
//                            exercises = filteredExercises,
//                            onExerciseClick = onExerciseClick
//                          //  navigateToExercise = navigateToExercise
//                        )
//                    }
//                }

                is DataState.Success -> {
                    Log.d("ExerciseListScreen", "run into SuccessStaticExercises in ExerciseListScreen")
                    if (filteredExercises.isEmpty()) {
                        Log.d("ExerciseListScreen", "filteredExercises.isEmpty() in SuccessStaticExercises of ExerciseListScreen")
                        EmptyContent(
                            hasActiveFilters = uiState.filters.activeFilters != ActiveFilters()
                        )
                    } else {
                        Log.d("ExerciseListScreen", "filteredExercises is Not Empty in SuccessStaticExercises of ExerciseListScreen")
                        ExerciseList(
                            exercises = filteredExercises,
                            onExerciseClick = onExerciseClick
                           // navigateToExercise = navigateToExercise
                        )
                    }
                }
                }
        }
}

//@Composable fun ExerciseList(...)：定义一个可组合函数用于显示锻炼列表。
//LazyColumn：用于高效地显示滚动列表，通过惰性加载可见项。
//modifier = Modifier.fillMaxSize()：设置为填满可用空间。
//contentPadding = PaddingValues(16.dp)：给列表内容添加填充，避免直接接触屏幕边缘。
//verticalArrangement = Arrangement.spacedBy(8.dp)：设置列表项间的垂直间隔为 8dp。
@Composable
fun ExerciseList(
    //exercises: List<Exercise>,
    exercises: List<StaticExercise>,
    //onExerciseClick: (Exercise) -> Unit ,
    onExerciseClick: (StaticExercise) -> Unit,
    //navigateToExercise: (StaticExercise) -> Unit,
) {
    Log.d("ExerciseListScreen", "begin of ExerciseList")
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        //items 方法
        //items(items = exercises, key = { it.id }):
        //items 是 LazyColumn 的一个扩展函数，用于插入可滚动的列表项。
        //items = exercises：将传入的 exercises 列表作为显示内容。
        //key = { it.id }：使用每个 Exercise 对象的 id 作为键，以提高性能。确保在列表中可以唯一标识每个项目，有助于进行高效的增量更新。
        items(
            items = exercises,
            key = { it.id } // Use exercise.id as key for performance
        ) { exercise ->
            Log.d("ExerciseListScreen", "run to ExerciseListItem of ExerciseList")
            //ExerciseListItem(...)：通过建立锻炼项目的行来展示每个锻炼的详细信息。
            //data = ExerciseListItemData.ExerciseData(exercise)：将当前的 Exercise 对象封装成 ExerciseData 传递给 ExerciseListItem 组件。
            //type = ExerciseListItemType.EXERCISE：设置项目类型，表示这是一个锻炼项目。
            //onClick = onExerciseClick：指定点击该锻炼项目时的回调函数，表示导航到锻炼的详细页面。
            //onAddToWorkout, onModify, onDelete：都为 No-op（无操作），这是因为此类型的项目不涉及添加到锻炼、修改或删除的操作，这些操作可能在不同的上下文中更合适。
            ExerciseListItem(
                //data = ExerciseListItemData.ExerciseData(exercise),
                data = ExerciseListItemData.StaticExerciseData(exercise),
                type = ExerciseListItemType.EXERCISE,
                onClick = {
                    Log.d("ExerciseListScreen", "onExerciseClick in ExerciseListItem")
                    onExerciseClick
                },
                onAddToWorkout = { /* No-op for EXERCISE type */ },
                onModify = { /* No-op for EXERCISE type */ },
                onDelete = { /* No-op for EXERCISE type */ }
            )
            //if (exercises.last() != exercise)：检查当前的 exercise 是否是列表中的最后一个项。如果不是，则添加一个分隔线。
            //HorizontalDivider()：是一个可组合函数，用于在项目之间提供视觉上的分隔。这里的目的是在相邻的锻炼项目间增加清晰的视觉效果。
            if (exercises.last() != exercise) {
                HorizontalDivider()
        }
        }
    }
}

// FilterSearchUI 函数签名
//@Composable private fun FilterSearchUI(...)：定义一个用于过滤和搜索界面的可组合函数。它接受以下参数：
//uiState: ExerciseListUiState：当前的 UI 状态。
//onUpdateFilters: (ActiveFilters) -> Unit：功能接口，用于更新过滤器。
//onFiltersCleared: () -> Unit：清除过滤器的回调。
@Composable
private fun FilterSearchUI(
    uiState: ExerciseListUiState,
    onUpdateFilters: (ActiveFilters) -> Unit,
    onFiltersCleared: () -> Unit
) {
    Log.d("ExerciseListScreen", "begin of FilterSearchUI")
    //Column(...)：使用 Column 布局，设置内边距为 16dp 并使其内容可以垂直滚动。
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        //Text(...)：在 UI 中显示过滤器标题“Filter Exercises”。
        //style = MaterialTheme.typography.headlineSmall：设置文本样式为小标题风格。
        //modifier = Modifier.padding(bottom = 16.dp)：在标题与后续内容之间添加下边距。
        Text(
            text = "训练项目筛选",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        //Body Part 过滤器
        //FilterSection(...)：用来显示身体部位的过滤器部分。
        //title = "Body Part"：设置标题为“Body Part”。
        //options = uiState.filters.bodyPartList：从 UI 状态中获取可选择的身体部位列表。
        //selectedOption = uiState.filters.activeFilters.bodyPart：当前被选中的身体部位。
        //onOptionSelected = { bodyPart -> ... }：当用户选择一个身体部位时更新过滤器的逻辑。
        FilterSection(
            title = "身体部位",
            options = uiState.filters.bodyPartList,
            selectedOption = uiState.filters.activeFilters.bodyPart,
            onOptionSelected = { bodyPart ->
                onUpdateFilters(uiState.filters.activeFilters.copy(bodyPart = bodyPart))
            }
        )

        //同样的方式为设备过滤器创建 UI：
        //过滤器标题为“Equipment”。
        //选中的值和选项列表都是从 uiState 获取。
        FilterSection(
            title = "设备",
            options = uiState.filters.equipmentList,
            selectedOption = uiState.filters.activeFilters.equipment,
            onOptionSelected = { equipment ->
                onUpdateFilters(uiState.filters.activeFilters.copy(equipment = equipment))
            }
        )

        //与设备过滤器类似，目标肌肉的过滤器创建方式相同。
        //适当更新在选选项时与视图模型中的活动过滤器保持同步。
        FilterSection(
            title = "目标部位",
            options = uiState.filters.targetList,
            selectedOption = uiState.filters.activeFilters.targetMuscle,
            onOptionSelected = { target ->
                onUpdateFilters(uiState.filters.activeFilters.copy(targetMuscle = target))
            }
        )

        //使用 Spacer 在过滤器和按钮之间增加一些空间。
        //按钮用于清除过滤器，点击时调用传递进来的 onFiltersCleared 方法，表示用户希望清除过滤条件。
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                    Log.d("ExerciseListScreen", "run to onClick of FilterSearchUI")
                    onFiltersCleared
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("清除筛选")
        }
    }
}

//FilterSection 方法签名
//@OptIn(ExperimentalLayoutApi::class)：表示使用实验性布局 API。
//@Composable private fun <T> FilterSection(...)：定义一个泛型可组合函数，用于展示选项。
//参数包括：
//title: String：过滤器的标题。
//options: List<T>：选项列表。
//selectedOption: T?：当前选中的选项。
//onOptionSelected: (T?) -> Unit：选择某一选项的回调。
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> FilterSection(
    title: String,
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T?) -> Unit
) {
    Log.d("ExerciseListScreen", "begin of FilterSection")
    //Column 用于垂直排列元素，并设置上下填充。
    //Text 显示当前过滤器的标题，应用中等标题样式，并设置下边距。
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        //FlowRow(...)：使用 FlowRow 布局将选项水平排列，设置填充和间隔。
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            //options.forEach { option ->：遍历每个选项。
            //FilterChip(...)：为每个选项创建一个过滤器芯片。
            //selected = option == selectedOption：判断当前选项是否为选中的选项。
            //onClick = { ... }：定义点击行为，如果当前选项已选中，则传入 null，否则传入选中的选项。
            options.forEach { option ->
                FilterChip(
                    selected = option == selectedOption,
                    onClick = {
                        Log.d("ExerciseListScreen", "run to onClick in FilterSection")
                        if (option == selectedOption) {
                            onOptionSelected(null)
                        } else {
                            onOptionSelected(option)
                        }
                    },
                    //芯片标签
                    //label = { ... }：定义每个芯片的标签。根据选项的类型显示其名称。
                    label = {
                        Log.d("ExerciseListScreen", "run to label in FilterSection")
                        Text(
                            when (option) {
                                is BodyPart -> option.name
                                is Equipment -> option.name
                                is TargetMuscle -> option.name
                                else -> option.toString()
                            }
                        )
                    }
                )
            }
        }
    }
}

//ErrorContent 方法签名
//@Composable private fun ErrorContent(...)：定义一个可组合函数，用于显示错误信息。
//参数包括：
//message: String：错误信息。
//showRetryButton: Boolean = true：确定是否显示重试按钮。
//onRetry: () -> Unit：重试函数的回调。
@Composable
private fun ErrorContent(
    message: String,
    showRetryButton: Boolean = true,
    onRetry: () -> Unit
) {
    //Column(...) 布局，用于垂直排列错误信息和按钮，设置为屏幕中心。
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //Text(...)：显示错误消息，使用较大字体样式，并居中对齐。
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        //Spacer(...)：添加一些空间使项目之间不会紧贴在一起。
        //条件性显示重试按钮，点击时调用 onRetry 方法以重新尝试相关操作。
        Spacer(modifier = Modifier.height(16.dp))
        if (showRetryButton) {
            Button(onClick = onRetry) {
            Text("Retry")
        }
        }
    }
}

//InitialDownloadContent 方法签名
//@Composable private fun InitialDownloadContent(...)：定义一个可组合函数，提示用户进行初始下载。
//参数包括：
//message: String：显示的消息，用于提示用户。
//onRetry: () -> Unit：重试下载的回调。
@Composable
private fun InitialDownloadContent(
    message: String,
    onRetry: () -> Unit // This triggers the download via VM.retry()
) {
    //Column 垂直排列提示信息和按钮，并设置为居中。
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //显示关于初始下载的消息，使用较大字体，底部添加一些 padding。
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "An internet connection is required for the initial download.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        //Button(...)：显示下载按钮，点击时调用 onRetry 以触发下载逻辑。
        Button(onClick = onRetry) {
            // Button text can indicate download action
            Text("Download Exercises")
        }
    }
}

//EmptyContent 方法签名
//@Composable private fun EmptyContent(...)：定义一个可组合函数，显示空内容的消息。
//参数包含：
//hasActiveFilters: Boolean：指示是否存在活动过滤器的布尔值。
@Composable
private fun EmptyContent(hasActiveFilters: Boolean) {
    //Column 样式与上面相似，用于垂直排列文本并居中对齐。
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //使用条件语句，根据是否存在活动过滤器选择显示不同的消息。
        Text(
            text = if (hasActiveFilters) {
                "No exercises match your filters"
            } else {
                "No exercises found"
            },
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

//LoadingIndicator 方法
//@Composable private fun LoadingIndicator()：定义一个可组合函数，用于显示加载指示器。
@Composable
private fun LoadingIndicator() {
    //Box 用于在可用空间中居中显示内容。
    //modifier = Modifier.fillMaxSize()：使 Box 填满整个可用大小。
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        //创建一个圆形进度指示器，向用户显示正在加载的状态。
        CircularProgressIndicator()
    }
}

//SearchBar 方法签名
//@OptIn(ExperimentalMaterial3Api::class)：表示使用了实验性的 Material 3 API。
//@Composable private fun SearchBar(...)：定义一个可组合的搜索栏，接受以下参数：
//searchQuery: String：当前的搜索查询。
//onQueryChange: (String) -> Unit：当搜索查询变化时的回调。
//modifier: Modifier = Modifier：用于修饰该组件的参数，默认值为空。
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    //OutlinedTextField(...)：用于创建带边框的文本输入框，提供用户搜索功能。
    //value = searchQuery：文本框显示的内容为当前搜索查询。
    //onValueChange = onQueryChange：用户输入内容变化时调用的函数。
    //modifier = modifier：该参数允许外部传入修饰符。
    //placeholder = { Text("Search exercises") }：当文本框为空时，显示的占位符文本。
    //leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }：文本框左侧的图标，表示搜索功能。
    //singleLine = true：限制为单行输入框。
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("查找运动项目") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        singleLine = true,
    )
}