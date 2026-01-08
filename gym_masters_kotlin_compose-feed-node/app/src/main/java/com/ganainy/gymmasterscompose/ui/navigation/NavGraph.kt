
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ganainy.gymmasterscompose.AuthUiState
import com.ganainy.gymmasterscompose.R
import com.ganainy.gymmasterscompose.ui.models.StaticExercise
import com.ganainy.gymmasterscompose.ui.models.post.FeedPost
import com.ganainy.gymmasterscompose.ui.models.workout.Workout
import com.ganainy.gymmasterscompose.ui.screens.create_post.CreatePostScreen
import com.ganainy.gymmasterscompose.ui.screens.static_test.StaticTestScreen
import com.ganainy.gymmasterscompose.ui.screens.static_test.StaticTestResultScreen
import com.ganainy.gymmasterscompose.ui.screens.create_workout.WorkoutSetupScreen
import com.ganainy.gymmasterscompose.ui.screens.discover.DiscoverScreen
import com.ganainy.gymmasterscompose.ui.screens.exercise.ExerciseScreen
import com.ganainy.gymmasterscompose.ui.screens.exercise_list.ExerciseListScreen
import com.ganainy.gymmasterscompose.ui.screens.feed.FeedScreen
import com.ganainy.gymmasterscompose.ui.screens.game.GameScreen
import com.ganainy.gymmasterscompose.ui.screens.post_details.PostDetailsScreen
import com.ganainy.gymmasterscompose.ui.screens.profile.ProfileScreen
import com.ganainy.gymmasterscompose.ui.screens.signin.SignInScreen
import com.ganainy.gymmasterscompose.ui.screens.signup.SignUpScreen
import com.ganainy.gymmasterscompose.ui.screens.workout_details.WorkoutDetailsScreen
import com.ganainy.gymmasterscompose.ui.screens.workout_list.WorkoutListScreen
//import com.ganainy.racingcar.ui.Destinations
import com.google.gson.Gson
import javax.inject.Inject

// NavigationRoutes.kt

//这一段代码展示了应用的主视图结构，在用户身份验证状态变更时能够合理响应并显示相应的界面。
//主要用到的技术有 Jetpack Compose 进行界面构建，以及通过 NavHostController 结合导航进行屏幕间跳转。

//object NavigationRoutes: 定义一个单例对象，专门用于管理应用中的导航路由。
//const val AUTH_GRAPH = "auth": 定义了一个常量字符串，表示身份验证相关的导航图。
//const val MAIN_GRAPH = "main": 定义了主功能相关的导航图。
object NavigationRoutes {
    const val AUTH_GRAPH = "auth"
    const val MAIN_GRAPH = "main"

    //object Auth: 包含与身份验证相关的导航路由。
    //SIGN_IN: 构建了一个路由用于 "sign_in"，完整路径为 "auth/sign_in"。
    //SIGN_UP: 构建了一个路由用于 "sign_up"，完整路径为 "auth/sign_up"。
    object Auth {
        const val SIGN_IN = "$AUTH_GRAPH/sign_in"
        const val SIGN_UP = "$AUTH_GRAPH/sign_up"
    }

    //object Main: 包含与主功能相关的导航路由。
    //各常量分别代表不同的页面路由，构建时将 MAIN_GRAPH 前缀与具体的路由拼接。
    object Main {
        const val FEED = "$MAIN_GRAPH/feed"
        const val DISCOVER = "$MAIN_GRAPH/discover"
        const val WORKOUT_LIST = "$MAIN_GRAPH/workout_list"
        const val EXERCISE_LIST = "$MAIN_GRAPH/exercise_list"
        const val PROFILE = "$MAIN_GRAPH/profile"

        const val GAME = "$MAIN_GRAPH/game"
        const val CREATE_POST = "$MAIN_GRAPH/create_post"
        const val EXERCISE_DETAILS = "$MAIN_GRAPH/exercise_details"
        const val WORKOUT_SETUP = "$MAIN_GRAPH/workout_setup"
        const val WORKOUT_DETAILS = "$MAIN_GRAPH/workout_details"
        const val POST_DETAILS = "$MAIN_GRAPH/post_details"
        const val Racing_Game =  "$MAIN_GRAPH/racing_game"
        const val Space_Game =  "$MAIN_GRAPH/space_game"
        const val Balance_Board_Control =  "$MAIN_GRAPH/balance_board_control"
        const val Static_Test =  "$MAIN_GRAPH/static_test"

        const val Static_Test_Result =  "$MAIN_GRAPH/static_test_result"




    }
}

// NavigationArgs.kt
// object NavigationArgs: 定义导航参数的单例对象。
// 常量用于在不同的页面之间传递数据，便于路由参数提取。
object NavigationArgs {
    const val USER_ID = "userId"
    const val WORKOUT = "workout"
    const val EXERCISE = "exercise"
    const val POST = "post"
    const val IS_LIKED = "isLiked"
    const val IS_SAVED = "isSaved"
}

// Screen.kt
// sealed class Screen: 定义一个密封类来表示所有的应用界面（屏幕）。
// 参数: 每个屏幕都有传入的路径 route，图标 icon 和标签 label。
sealed class Screen(
    val route: String,
    val icon: Int? = null,
    val label: String? = null
) {
    //sealed class Auth: 定义与身份验证相关的屏幕子类。
    //SignIn 和 SignUp: 对应登录和注册屏幕，分别使用之前定义的路由。
    sealed class Auth(route: String) : Screen(route) {
        object SignIn : Auth(NavigationRoutes.Auth.SIGN_IN)
        object SignUp : Auth(NavigationRoutes.Auth.SIGN_UP)
    }

    //sealed class Main: 定义与主功能相关的不同屏幕。
    //每个屏幕对象（如 Feed, Discover, WorkoutList, ExerciseList, 和 Profile）都持有其对应的路由、图标和标签。
    sealed class Main(route: String, icon: Int? = null, label: String? = null) :
        Screen(route, icon, label) {

        object Feed : Main(NavigationRoutes.Main.FEED, R.drawable.home, "Feed")
        object Discover : Main(NavigationRoutes.Main.DISCOVER, R.drawable.search, "Discover")
        object WorkoutList :
            Main(NavigationRoutes.Main.WORKOUT_LIST, R.drawable.dumbbells, "Workouts")

        object ExerciseList :
            Main(NavigationRoutes.Main.EXERCISE_LIST, R.drawable.dumbbell, "Exercises")

        object Profile : Main(NavigationRoutes.Main.PROFILE, R.drawable.profile, "个人资料")

        object GAME : Main(NavigationRoutes.Main.GAME, R.drawable.profile, "日常训练")
    }

    //定义其他独立或特定的屏幕，例如创建帖子、锻炼、锻炼设置等，方便路由管理。
    object CreatePost : Screen(NavigationRoutes.Main.CREATE_POST)
    object Exercise : Screen(NavigationRoutes.Main.EXERCISE_DETAILS)
    object WorkoutSetup : Screen(NavigationRoutes.Main.WORKOUT_SETUP)
    object WorkoutDetails : Screen(NavigationRoutes.Main.WORKOUT_DETAILS)
    object PostDetails : Screen(NavigationRoutes.Main.POST_DETAILS)
    object RacingGame : Screen( NavigationRoutes.Main.Racing_Game)
    object SpaceGame : Screen( NavigationRoutes.Main.Space_Game)
    object BalanceBoardControl : Screen( NavigationRoutes.Main.Balance_Board_Control)
    object StaticTest : Screen( NavigationRoutes.Main.Static_Test)

    object StaticTestResult : Screen( NavigationRoutes.Main.Static_Test_Result)
}

// AppNavigator.kt
// class AppNavigator: 定义一个用于管理导航的类。使用 Dagger/Hilt 进行依赖注入。
// 构造函数: 构造函数注入 NavHostController 实例，以进行导航操作。
class AppNavigator @Inject constructor(private val navController: NavHostController) {

    // navigate(route: String): 让导航控制器导航到指定的路由。
    // 使用 launchSingleTop = true，确保如果目标已经位于栈顶，则不会重新创建。
    fun navigate(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
        }
    }

    //navigateToAuth(): 导航到身份验证图，使用 popUpTo(0) 清空回退栈（例如返回到启动屏）。
    fun navigateToAuth() {
        navController.navigate(NavigationRoutes.AUTH_GRAPH) {
            popUpTo(0) { inclusive = true }
        }
    }

    //navigateToMain(): 导航到主界面，并清空任何先前的栈。
    fun navigateToMain() {
        navController.navigate(NavigationRoutes.MAIN_GRAPH) {
            popUpTo(0) { inclusive = true }
        }
    }

    //navigateToProfile(userId: String?): 导航到用户个人资料页面，并且传递用户 ID。使用 URI 参数传递数据。
    fun navigateToProfile(userId: String?) {
        val route = "${NavigationRoutes.Main.PROFILE}?${NavigationArgs.USER_ID}=${userId ?: ""}"
        navController.navigate(route)
    }

    //navigateToWorkoutDetails(...): 导航到锻炼细节页面，同时传递锻炼对象及其状态。使用 Gson 将对象转换为 JSON 字符串。
    fun navigateToWorkoutDetails(workout: Workout, isLiked: Boolean, isSaved: Boolean) {
        Log.d("NavGraph", "begin of navigateToWorkoutDetails")
        val workoutJson = Uri.encode(Gson().toJson(workout))
        val route = "${NavigationRoutes.Main.WORKOUT_DETAILS}?" +
                "${NavigationArgs.WORKOUT}=$workoutJson&" +
                "${NavigationArgs.IS_LIKED}=$isLiked&" +
                "${NavigationArgs.IS_SAVED}=$isSaved"
        navController.navigate(route)
    }

    //导航到锻炼详情页面。
//    fun navigateToExerciseDetails(exercise: Exercise) {
//        Log.d("NavGraph", "begin of navigateToExerciseDetails")
//        val exerciseJson = Uri.encode(Gson().toJson(exercise))
//        val route =
//            "${NavigationRoutes.Main.EXERCISE_DETAILS}?${NavigationArgs.EXERCISE}=$exerciseJson"
//        navController.navigate(route)
//    }
    fun navigateToExerciseDetails(exercise: StaticExercise) {
        Log.d("NavGraph", "begin of navigateToExerciseDetails")
        val exerciseJson = Uri.encode(Gson().toJson(exercise))
        val route =
            "${NavigationRoutes.Main.EXERCISE_DETAILS}?${NavigationArgs.EXERCISE}=$exerciseJson"
        navController.navigate(route)
    }

    //导航到帖子详情页面。
    fun navigateToPostDetails(post: FeedPost, isLiked: Boolean) {
        val postJson = Uri.encode(Gson().toJson(post))
        val route = "${NavigationRoutes.Main.POST_DETAILS}?" +
                "${NavigationArgs.POST}=$postJson&" +
                "${NavigationArgs.IS_LIKED}=$isLiked"
        navController.navigate(route)
    }

    fun navigateToRacingGame() {
        Log.d("NavGraph", "begin of navigateToRacingCar")
        navController.navigate(Screen.RacingGame.route)
    }

    fun  navigateToSpaceGame() {
        Log.d("NavGraph", "begin of navigateToSpaceGame")
        navController.navigate(Screen.SpaceGame.route)
    }

    fun  navigateToBalanceBoardControl() {
        Log.d("NavGraph", "begin of navigateToBalanceBoardControl")
        navController.navigate(Screen.BalanceBoardControl.route)
    }


    fun  navigateToStaticTest() {
        Log.d("NavGraph", "begin of navigateToStaticTest")
        navController.navigate(Screen.StaticTest.route)
    }


    fun  navigateToStaticTestResult() {
        Log.d("NavGraph", "begin of navigateToStaticTestResult")
        navController.navigate(Screen.StaticTestResult.route)
    }



    //处理返回操作，从当前导航栈中弹出屏幕。
    fun navigateBack() {
        Log.d("NavGraph", "begin of navigateBack")
        navController.popBackStack()
    }

    fun navigateToCreatePost() {
        navController.navigate(Screen.CreatePost.route)
    }

    fun navigateToFeed() {
        navController.navigate(Screen.Main.Feed.route)
    }

}

// AppNavigation.kt
//@Composable: 声明这是一个可组合函数。
//AppNavigation: 用于设置整个应用程序的导航结构。
//参数:
//modifier: 用于自定义布局或样式的可选参数。
//navController: 通过 rememberNavController() 创建一个新的 NavHostController 实例，用于管理导航。
//navigator: 创建应用程序导航器实例。
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    navigator: AppNavigator = remember { AppNavigator(navController) }
) {
    //NavHost: 容器，用于定义应用中的所有导航路由和起始目的地。起始目的地设置为 "loading"。
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = "loading"
    ) {
        // Loading Screen
        // composable(...): 定义可组合项。在这里，加载屏幕的路由进行定义。
        composable(route = "loading") {
            LoadingScreen()
        }

        // Auth Graph
        //authGraph(navigator): 该方法设置与身份验证相关的路由。
        authGraph(navigator)

        // Main Graph
        // mainGraph(navigator): 该方法设置主应用的路由。
        mainGraph(navigator)
    }
}

// AuthNavigation.kt
// 这些部分负责定义身份验证和主导航图的具体屏幕。
// authGraph(navigator): 用于构建身份验证相关的导航图，定义初始地点为 SignIn.screen.route。
private fun NavGraphBuilder.authGraph(navigator: AppNavigator) {
    navigation(
        startDestination = Screen.Auth.SignIn.route,
        route = NavigationRoutes.AUTH_GRAPH
    ) {
        //composable(...): 为登录界面定义路由并指定对应的处理逻辑
        composable(route = Screen.Auth.SignIn.route) {
            SignInScreen(
                onSignInSuccess = navigator::navigateToMain,
                navigateToSignUp = { navigator.navigate(Screen.Auth.SignUp.route) }
            )
        }

        //注册界面定义路由并指定对应的处理逻辑
        composable(route = Screen.Auth.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = navigator::navigateToMain,
                navigateToSignIn = { navigator.navigate(Screen.Auth.SignIn.route) }
            )
        }
    }
}

// MainNavigation.kt
// 函数定义: 声明了主屏幕的 Composable 函数并接受身份验证状态。
// rememberNavController(): 创建一个 NavHostController 实例用于管理导航。
// AppNavigator: 创建应用导航器实例以简化屏幕间的导航。

//private fun NavGraphBuilder.mainGraph(navigator: AppNavigator): 这是一个扩展函数，为 NavGraphBuilder 定义了一个方法，用于构建针对主应用的导航图。
//startDestination: 将主图的起始目的地设置为 Feed 屏幕，这个屏幕是应用启动时用户首先看到的内容。
//route = NavigationRoutes.MAIN_GRAPH: 设置整个导航图的路由，方便导航管理。
private fun NavGraphBuilder.mainGraph(navigator: AppNavigator) {
    navigation(
        startDestination = Screen.Main.Feed.route,
        route = NavigationRoutes.MAIN_GRAPH
    ) {
        // Feed Screen
        // composable(...): 定义了一个可组合的项，将其与特定的路由关联。在这里，定义了 FeedScreen，它是主屏幕的一个部分。
        // navigateToProfile = navigator::navigateToProfile: 将导航到用户个人资料的功能传递给 FeedScreen，通过 AppNavigator 实现。
        // navigateToCreatePost = navigator::navigateToCreatePost: 类似地，传入创建帖子的方法。
        // navigateToDetailedPost: 这是一个 Lambda 函数，它接受 post 和 isLiked 参数，用于导航到帖子的详细视图。
        composable(route = Screen.Main.Feed.route) {
            FeedScreen(
                navigateToProfile = navigator::navigateToProfile,
                navigateToCreatePost = navigator::navigateToCreatePost,
                navigateToDetailedPost = { post, isLiked ->
                    navigator.navigateToPostDetails(post, isLiked)
                }
            )
        }

        // Discover Screen
        // composable(...): 定义了 DiscoverScreen 作为主要功能之一，传递用户资料的导航。跳转至DiscoverScreen
        composable(route = Screen.Main.Discover.route) {
            DiscoverScreen(
                navigateToProfile = navigator::navigateToProfile
            )
        }

        // Exercise List Screen
        // ExerciseListScreen: 为锻炼列表定义了一个可组合函数，传递锻炼详情的导航和返回操作。
        composable(route = Screen.Main.ExerciseList.route) {
            ExerciseListScreen(
                navigateToExercise = navigator::navigateToExerciseDetails,
                navigateBack = navigator::navigateBack

            )
        }

        // Profile Screen
        // Profile Screen: 这个路由定义了用户个人资料页面。
        //路由格式: 通过查询参数传递用户 ID，为参数设置相应的默认值和数据类型，方便在后续检索。
        //ProfileScreen: 在这里调用 ProfileScreen 并传入多种导航回调，允许该组件能够通过 AppNavigator 进行多种操作。
        composable(
            route = "${Screen.Main.Profile.route}?${NavigationArgs.USER_ID}={${NavigationArgs.USER_ID}}",
            arguments = listOf(
                navArgument(NavigationArgs.USER_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString(NavigationArgs.USER_ID)
            ProfileScreen(
                userId = userId,
                navigateBack = navigator::navigateBack,
                navigateToLogin = navigator::navigateToAuth,
                navigateToCreatePost = { navigator.navigate(Screen.CreatePost.route) },
                navigateToWorkoutSetup = {
                    Log.d("NavGraph", "navigateToWorkoutSetup in ProfileScreen")
                    navigator.navigate(Screen.WorkoutSetup.route) },
                navigateToWorkoutDetails = {
                    workoutWithStatus ->
                    navigator.navigateToWorkoutDetails(
                        workoutWithStatus.workout,
                        workoutWithStatus.isLiked,
                        workoutWithStatus.isSaved
                    )
                },
                navigateToExercisesList = { navigator.navigate(Screen.Main.ExerciseList.route) },

                navigateToExerciseDetails = {
                    Log.d("NavGraph", "navigateToExerciseDetails in ProfileScreen")
                    navigator::navigateToExerciseDetails
                }
            )
        }

        // Additional screens with similar pattern...
        // Exercise Details Screen
        // composable(...): 定义锻炼详情页面，接受锻炼对象的 JSON 字符串作为参数。
        // 参数传递: 将传入的锻炼 JSON 字符串转换为 Exercise 对象，并将其传递给 ExerciseScreen。
        composable(
            route = "${Screen.Exercise.route}?${NavigationArgs.EXERCISE}={${NavigationArgs.EXERCISE}}",
            arguments = listOf(
                navArgument(NavigationArgs.EXERCISE) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val exerciseJson = backStackEntry.arguments?.getString(NavigationArgs.EXERCISE)
            val exercise = Gson().fromJson(exerciseJson, StaticExercise::class.java)
            ExerciseScreen(
                exercise = exercise,
                navigateBack = navigator::navigateBack
                //navigateTo
            )
        }

        // Post Details Screen
        // 帖子详情页面: 类似于锻炼详情，定义了帖子详情路由，转换帖子的 JSON 数据并传递给 PostDetailsScreen。
        composable(
            route = "${Screen.PostDetails.route}?" +
                    "${NavigationArgs.POST}={${NavigationArgs.POST}}",
            arguments = listOf(
                navArgument(NavigationArgs.POST) { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val postJson = backStackEntry.arguments?.getString(NavigationArgs.POST)
            val post = Gson().fromJson(postJson, FeedPost::class.java)
            PostDetailsScreen(
                navigateBack = navigator::navigateBack,
                post = post,
            )
        }

        // ExerciseList Screen
        // 锻炼列表页面: 定义一个锻炼列表的屏幕，传递必要的回调。
        composable(
            route = Screen.Main.ExerciseList.route,
        ) { backStackEntry ->
            ExerciseListScreen(
                navigateToExercise = navigator::navigateToExerciseDetails,
                navigateBack = navigator::navigateBack
            )
        }

        composable(
            route = Screen.Main.GAME.route
        ) {
            GameScreen(
                navigateBack = navigator::navigateBack,
            )
        }

        // Workout list screen
        // 锻炼列表屏幕: 为锻炼列表添加导航功能。界面中的功能按钮直接连接相应的导航逻辑。
        composable(
            route = Screen.Main.WorkoutList.route,
        ) { backStackEntry ->
            WorkoutListScreen(
                navigateToWorkoutDetails = navigator::navigateToWorkoutDetails,
                navigateToRacingGame = navigator::navigateToRacingGame
            )

    }

        // Workout list screen
        // 锻炼列表屏幕: 为锻炼列表添加导航功能。界面中的功能按钮直接连接相应的导航逻辑。
        composable(
            route = Screen.StaticTest.route,
        ) { backStackEntry ->
            val exerciseJson = backStackEntry.arguments?.getString(NavigationArgs.EXERCISE)
            val exercise = Gson().fromJson(exerciseJson, StaticExercise::class.java)
            StaticTestScreen(
                exercise = exercise,
                navigateBack = navigator::navigateBack
            )
        }


        // Static test result screen
        // 锻炼列表屏幕: 为锻炼列表添加导航功能。界面中的功能按钮直接连接相应的导航逻辑。
//        composable(
//            route = Screen.StaticTestResult.route,
//        ) { backStackEntry ->
//            val exerciseJson = backStackEntry.arguments?.getString(NavigationArgs.EXERCISE)
//            val exercise = Gson().fromJson(exerciseJson, StaticExercise::class.java)
//            StaticTestResultScreen(
//                exercise = exercise,
//                navigateBack = navigator::navigateBack
//            )
//        }


        // Workout Details Screen
        // 锻炼详情页面: 提供一个锻炼详情的路由，解析传入的参数，并为 WorkoutDetailsScreen 提供所需数据。
        // 传入三个参数：workout isLiked isSaved
        composable(
            route = "${Screen.WorkoutDetails.route}?" +
                    "${NavigationArgs.WORKOUT}={${NavigationArgs.WORKOUT}}&" +
                    "${NavigationArgs.IS_LIKED}={${NavigationArgs.IS_LIKED}}&" +
                    "${NavigationArgs.IS_SAVED}={${NavigationArgs.IS_SAVED}}",
            arguments = listOf(
                navArgument(NavigationArgs.WORKOUT) { type = NavType.StringType },
                navArgument(NavigationArgs.IS_LIKED) { type = NavType.BoolType },
                navArgument(NavigationArgs.IS_SAVED) { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val workoutJson = backStackEntry.arguments?.getString(NavigationArgs.WORKOUT)
            val isLiked = backStackEntry.arguments?.getBoolean(NavigationArgs.IS_LIKED) ?: false
            val isSaved = backStackEntry.arguments?.getBoolean(NavigationArgs.IS_SAVED) ?: false
            val workout = Gson().fromJson(workoutJson, Workout::class.java)
            WorkoutDetailsScreen(
                workout = workout,
                isLiked = isLiked,
                isSaved = isSaved,
                navigateBack = navigator::navigateBack,
                navigateToExerciseDetails = navigator::navigateToExerciseDetails,
                navigateToRacingGame = navigator::navigateToRacingGame,
                navigateToSpaceGame =  navigator::navigateToSpaceGame,
                navigateToBalanceBoardControl = navigator::navigateToBalanceBoardControl,
                navigateToStaticTest =  navigator::navigateToStaticTest,
                navigateToStaticTestResult = navigator::navigateToStaticTestResult
            )
        }

        // Workout Setup Screen
        // 锻炼设置页面: 在导航图中定义锻炼设置的组合界面。多个导航参数被传入。
        composable(route = Screen.WorkoutSetup.route) { backStackEntry ->
            WorkoutSetupScreen(
                navigateToFeed = navigator::navigateToFeed,
                navigateBack = navigator::navigateBack,
                navigateToExercise = navigator::navigateToExerciseDetails
            )
        }

        // Create post screen
        // 帖子创建页面: 定义了一个用于创建新帖子的屏幕，传入返回的导航逻辑。
        composable(route = Screen.CreatePost.route) {
            CreatePostScreen(
                onNavigateBack = navigator::navigateBack,
            )
        }


//        // Racing Car Screen
//        composable(route = Screen.RacingGame.route) {
//            RacingGameRoute(modifier = Modifier.fillMaxSize())
//        }
//
//        composable(route = Screen.SpaceGame.route) {
//            SpaceGameRoute(modifier = Modifier.fillMaxSize())
//        }

//        composable(route = Screen.StaticTest.route) {
//            RacingGameRoute(modifier = Modifier.fillMaxSize())
//        }

        // Workout list screen
        // 锻炼列表屏幕: 为锻炼列表添加导航功能。界面中的功能按钮直接连接相应的导航逻辑。
//        composable(
//            route = Screen.StaticTest.route,
//        ) { backStackEntry ->
//            val exerciseJson = backStackEntry.arguments?.getString(NavigationArgs.EXERCISE)
//            val exercise = Gson().fromJson(exerciseJson, StaticExercise::class.java)
//            StaticTestScreen(
//                exercise = exercise,
//                navigateBack = navigator::navigateBack
//            )
//        }

        // Static test result screen
        // 锻炼列表屏幕: 为锻炼列表添加导航功能。界面中的功能按钮直接连接相应的导航逻辑。
        composable(
            route = Screen.StaticTestResult.route,
        ) { backStackEntry ->
            val exerciseJson = backStackEntry.arguments?.getString(NavigationArgs.EXERCISE)
            val exercise = Gson().fromJson(exerciseJson, StaticExercise::class.java)
            StaticTestResultScreen(
                exercise = exercise,
                navigateBack = navigator::navigateBack
            )
        }

    }
    }

// MainScreen.kt
@Composable
//@Composable: 注解表明这是一个可组合函数，用于构建 UI。
//fun MainScreen(authState: AuthUiState): 函数签名，接收一个 AuthUiState 状态参数，表示用户的身份验证状态。
fun MainScreen(authState: AuthUiState) {

    //val navController = rememberNavController(): 使用 rememberNavController 创建一个 NavHostController 实例，用于管理应用内的导航。
    //val navigator = remember { AppNavigator(navController) }: 创建一个 AppNavigator 实例，以便通过其简化导航操作，保持导航控制器的引用。
    val navController = rememberNavController()
    val navigator = remember { AppNavigator(navController) }

    // Use LaunchedEffect with currentDestination to delay navigation
    // LaunchedEffect(authState, navController.currentDestination): 该效果会在 authState 或当前导航目的地变化时执行，可以用于处理复杂的副作用，如导航。
    //if (navController.currentDestination != null): 确保导航控制器的当前目的地不为 null，只有在控制器已准备就绪时才进行导航操作。
    //when (authState): 根据当前的身份验证状态进行不同的导航：
    //is AuthUiState.Authenticated: 如果用户已认证，则导航到主界面。
    //is AuthUiState.Unauthenticated: 如果用户未认证，则导航到认证界面（如登录或者注册）。
    //is AuthUiState.Loading: 如果状态为加载中，则导航到主界面（通常是一个加载指示器）。
    //else { Log.e(...) }: 如果导航控制器的当前目的地为 null，记录错误信息表示导航图尚未准备好，这有助于调试。

    LaunchedEffect(authState, navController.currentDestination) {
        if (navController.currentDestination != null) {
            when (authState) {
                is AuthUiState.Authenticated -> {
                    navigator.navigateToMain()
                }
                is AuthUiState.Unauthenticated -> {
                    navigator.navigateToAuth()
                }
                is AuthUiState.Loading -> {
                    // Show loading screen
                    navigator.navigateToMain()
                }
            }
        } else {
            Log.e("MainScreen", "Navigation graph not ready, skipping navigation")
        }
    }

    // Render AppNavigation based on authState
    // when (authState): 根据用户身份验证状态渲染不同的界面。
    //is AuthUiState.Loading: 如果身份验证状态是加载中，调用 LoadingScreen()，显示加载指示器。
    //else: 其他状态（已认证或未认证）都调用 AppNavigation，进行导航控制。

    when (authState) {
        is AuthUiState.Loading -> {
            LoadingScreen()
        }
        else -> {
            AppNavigation(navController = navController, navigator = navigator)
        }
    }

    // Scaffold layout with a bottom bar
    // Scaffold: 提供应用的基本布局结构，它内置了处理典型 UI 组成部分的功能，如顶部应用栏和底部导航栏。
    //bottomBar: 在该参数内定义了底部导航栏的显示逻辑。
    //val currentRoute = ...: 获取当前路由的名称，以便识别当前处于哪个界面。
    //if (currentRoute?.startsWith("main/") == true): 只有在当前路由为主图的情况下才显示底部导航栏，确保用户是在主要功能界面。
    Scaffold(
        bottomBar = {
            // Only show bottom bar when in main graph
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute?.startsWith("main/") == true) {
                BottomNavBar(navController)
            }
        }
    ) { paddingValues ->
        // Box layout to handle padding values
        // Box: 使用 Box 布局来包裹主要的 UI 部分并处理 padding。
        // AppNavigation(...): 在 Box 内部调用了 AppNavigation，传入 navController，以设置主要的导航效果。
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = paddingValues.calculateBottomPadding())) {
            // App navigation setup
            AppNavigation(
                navController = navController
            )
        }
    }

}


// BottomNavBar.kt
// BottomNavBar(navController: NavHostController): 定义底部导航栏的可组合函数，接收用于控制导航的 NavHostController。
// currentRoute: 获取当前菜单项，确保底部导航仅在主图下显示。
// if (currentRoute?.startsWith(NavigationRoutes.MAIN_GRAPH) == true): 仅在当前路由属于主图时创建导航菜单。
@Composable
fun BottomNavBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    if (currentRoute?.startsWith(NavigationRoutes.MAIN_GRAPH) == true) {
        NavigationBar {
            val items = listOf(
                Screen.Main.Feed,
                Screen.Main.Discover,
                Screen.Main.WorkoutList,
                Screen.Main.ExerciseList,
                Screen.Main.Profile,
                Screen.Main.GAME
            )

            //遍历屏幕项: 通过 items 列表生成底部导航项。
            //NavigationBarItem(...): 为每个屏幕创建一个底部导航项，包括图标和标签。
            //onClick: 点击某项时，使用 navController.navigate() 进行导航。
            //设置 popUpTo 将栈中先前的项目弹出并进行状态保存，launchSingleTop 确保不重复创建已存在的目的地，restoreState 自动恢复上次保存的状态。
            items.forEach { screen ->
                NavigationBarItem(
                    icon = {
                        screen.icon?.let {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(it),
                                contentDescription = null
                            )
                        }
                    },
                    label = { Text(screen.label ?: "") },
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
    }

@Composable
//LoadingScreen(): 定义一个负责展示加载状态的函数。
//Box 布局: 使用 Box 将加载指示器居中显示，Modifier.fillMaxSize() 确保其占满整个可用空间。
//CircularProgressIndicator(): 显示系统的圆形加载指示器，表示任务正在进行中。
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}