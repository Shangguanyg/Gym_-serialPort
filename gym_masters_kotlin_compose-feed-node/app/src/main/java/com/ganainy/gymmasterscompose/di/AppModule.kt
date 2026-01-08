package com.ganainy.gymmasterscompose.di


import android.content.Context
import androidx.room.Room
import com.ganainy.gymmasterscompose.BuildConfig
import com.ganainy.gymmasterscompose.Constants.FIREBASE_DATABASE_NAME
import com.ganainy.gymmasterscompose.prefs.ExerciseDownloadPrefs
import com.google.firebase.firestore.FirebaseFirestore
import com.ganainy.gymmasterscompose.ui.repository.AuthRepository
import com.ganainy.gymmasterscompose.ui.repository.CommentsRepository
import com.ganainy.gymmasterscompose.ui.repository.ExerciseRepository
import com.ganainy.gymmasterscompose.ui.repository.FeedRepository
import com.ganainy.gymmasterscompose.ui.repository.HashtagRepository
import com.ganainy.gymmasterscompose.ui.repository.IAuthRepository
import com.ganainy.gymmasterscompose.ui.repository.ICommentsRepository
import com.ganainy.gymmasterscompose.ui.repository.IExerciseRepository
import com.ganainy.gymmasterscompose.ui.repository.IFeedRepository
import com.ganainy.gymmasterscompose.ui.repository.IHashtagRepository
import com.ganainy.gymmasterscompose.ui.repository.ILikeRepository
import com.ganainy.gymmasterscompose.ui.repository.IPostRepository
import com.ganainy.gymmasterscompose.ui.repository.ISocialRepository
import com.ganainy.gymmasterscompose.ui.repository.IUserRepository
import com.ganainy.gymmasterscompose.ui.repository.IUsersRepository
import com.ganainy.gymmasterscompose.ui.repository.IWorkoutRepository
import com.ganainy.gymmasterscompose.ui.repository.LikeRepository
import com.ganainy.gymmasterscompose.ui.repository.PostRepository
import com.ganainy.gymmasterscompose.ui.repository.SocialRepository
import com.ganainy.gymmasterscompose.ui.repository.UserRepository
import com.ganainy.gymmasterscompose.ui.repository.UsersRepository
import com.ganainy.gymmasterscompose.ui.repository.WorkoutRepository
import com.ganainy.gymmasterscompose.ui.retrofit.ExerciseApi
import com.ganainy.gymmasterscompose.ui.retrofit.AuthApi
import com.ganainy.gymmasterscompose.ui.room.AppDatabase
import com.ganainy.gymmasterscompose.utils.ExerciseDataManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

// Dagger Hilt 模块注解将其声明为依赖注入模块，安装到 SingletonComponent 中
// AppModule.kt 文件是 Gym Masters Android 应用程序使用 Dagger Hilt 的中央依赖注入配置。
// 它为应用程序中的仓库、Firebase 服务、网络组件和实用工具提供单例实例。
// 该模块通过通过仓库接口和实现来分离关注点，遵循了清洁架构模式。
// 文件似乎以一个不完整的自定义限定符注解定义结束，可能是用于限定 IO CoroutineDispatcher 依赖的 @IoDispatcher 注解。
//

// @Module: 这个注释标识此类是一个 Dagger/Hilt 模块，表示它将提供依赖项。
// @InstallIn(SingletonComponent::class): 指明该模块的作用域为应用的 SingletonComponent，这意味着模块中的提供的方法生成的对象在整个应用的生命周期内只有一个实例。
// object AppModule: 定义了一个 Kotlin 单例对象，用于容纳所有提供的依赖项。
@Module
@InstallIn(SingletonComponent::class)
// 创建 AppModule 作为单例对象的声明
object AppModule {

    //ExerciseDB API 端点的基 URL 常量
    //private const val BASE_URL = "https://exercisedb.p.rapidapi.com/"

    // BASE_URL: 预定义一个常量，用于指定网络请求的基本 URL，方便在需要的地方使用。
    private const val BASE_URL = "http://49.234.193.180:7030/"

    // 网络依赖
    // OkHttpClient 提供方法，带有自定义拦截器以进行 API 身份验证和日志记录
    // @Provides: Dagger 注解，表示此方法用于提供 OkHttpClient 的实例。
    // @Singleton: 注明此实例是单例的，保证在应用生命周期内只存在一个 OkHttpClient 实例。
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        // Interceptor: 创建一个自定义拦截器，用于在每个请求中添加自定义的请求头，例如 API 主机和密钥，支持请求鉴权。
        val interceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
//                .addHeader("x-rapidapi-host", "exercisedb.p.rapidapi.com")
//                .addHeader("x-rapidapi-key", BuildConfig.RAPID_API_KEY)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Connection", "keep-alive")
               // .addHeader("Cookie", "token=b8e213b1-000f-4997-9302-fbd6696266e1;token=125c3d21-933c-4888-9c10-6d79696e872d;health_token=a1cbaebf-ccaa-4b6b-8cae-be1cc68340c6")
                .addHeader("Cookie", "health_token=a1cbaebf-ccaa-4b6b-8cae-be1cc68340c6")
                .build()
            chain.proceed(request)
        }

        // HttpLoggingInterceptor: 创建一个日志拦截器，用于记录 HTTP 请求和响应的详细内容，设置其日志级别为 BODY，表示记录所有请求和响应的详细信息。
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        //OkHttpClient.Builder(): 创建 OkHttpClient 的构建器，并将两个拦截器添加到客户端配置中。
        //build(): 完成构建并返回 OkHttpClient 实例。
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // Room 数据库提供方法，配置本地 SQLite 数据库，并使用破坏性迁移回退

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "exercise-database"
        ).fallbackToDestructiveMigration().build()
    }

    // Retrofit 提供方法，配置 HTTP 客户端，使用 Gson 转换器进行 JSON 序列化
    // 方法的作用: 提供一个 Retrofit 实例，以便应用中的其他部分进行网络操作。
    //baseUrl(BASE_URL): 设置基本 URL 为 BASE_URL，使得所有请求基于此 URL 进行。
    //client(okHttpClient): 指定使用的 OKHttpClient，使得 Retrofit 能够使用已配置的拦截器。
    //addConverterFactory(GsonConverterFactory.create()): 添加 Gson 作为 JSON 数据的解析器，允许 Retrofit 将 HTTP 请求和响应转换为 Kotlin 对象。
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ExerciseApi 提供方法，创建 Retrofit 服务接口
    // provideExerciseApi(...): 提供 ExerciseApi 的方法，利用 Retrofit 来生成接口实例。
    // retrofit.create(...): 使用 Retrofit 创建指定接口的实例，允许调用通过 Retrofit 定义的 API 方法。
    @Provides
    @Singleton
    fun provideExerciseApi(retrofit: Retrofit): ExerciseApi {
        return retrofit.create(ExerciseApi::class.java)
    }


    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    // Firebase 依赖
    // Firebase 身份验证提供者方法
    // 方法作用: 提供 Firebase 身份验证的单例实例，允许在应用中检索当前身份用户。
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    // currently unused but is left for future use with realtime chat feature
    // Firebase 实时数据库提供者方法，注释表明未来聊天功能的使用
    // 方法作用: 为 Firebase 数据库提供实例，后续提供数据库访问功能。
    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance(FIREBASE_DATABASE_NAME)
    }

    // Firebase Firestore 提供者方法，用于 NoSQL 文档数据库
    // 方法作用: 提供 Firestore 的单例实例，允许访问 Firestore 文档和集合。
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }


    // 仓库依赖
    // AuthRepository 提供者方法，实现身份验证功能
    // 提供身份验证仓库: 通过注入 FirebaseAuth 和 FirebaseFirestore 的实例，创建并提供 AuthRepository 的实例。
    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        database: FirebaseFirestore,
        authApi: AuthApi
    ): IAuthRepository {
        return AuthRepository(auth, database, authApi)
    }


    // PostRepository 提供方法，包含用户管理、数据库、标签和存储的依赖
    // 提供贴文仓库: 使用从其他仓库注入的实例来创建和返回 PostRepository 实例。
    @Provides
    @Singleton
    fun providePostRepository(
        userRepository: IUserRepository,
        database: FirebaseFirestore,
        hashtagRepository: IHashtagRepository,
        storage: FirebaseStorage

    ): IPostRepository {
        return PostRepository(userRepository, database, hashtagRepository, storage)
    }

    // SocialRepository 提供方法，用于社交功能
    // 提供社交仓库: 包括处理与社交相关的数据和操作的仓库。
    @Provides
    @Singleton
    fun provideSocialRepository(
        auth: FirebaseAuth,
        database: FirebaseFirestore,
    ): ISocialRepository {
        return SocialRepository(auth, database)
    }

    // HashtagRepository 提供方法，用于标签功能
    // 提供标签仓库: 提供用于管理标签数据和相关操作的仓库实例。
    @Provides
    @Singleton
    fun provideHashtagRepository(
        database: FirebaseFirestore
    ): IHashtagRepository {
        return HashtagRepository(database)
    }


    // UserRepository 提供方法，包含认证、数据库和存储依赖
    // 提供用户仓库: 创建 UserRepository 的实例以处理与用户相关的操作。
    @Provides
    @Singleton
    fun provideUserRepository(
        auth: FirebaseAuth,
        database: FirebaseFirestore,
        storage: FirebaseStorage
    ): IUserRepository {
        return UserRepository(auth, database, storage)
    }


    // 用户仓库管理多个用户的提供者方法
    // 提供用户管理仓库: 提供与多个用户相关操作的实例。
    @Provides
    @Singleton
    fun provideUsersRepository(
        auth: FirebaseAuth,
        database: FirebaseFirestore,
    ): IUsersRepository {
        return UsersRepository(auth, database)
    }


    // 健身仓库提供者方法，具有数据库、存储、标签和本地数据库依赖项
    // 提供锻炼仓库: 返回用于处理锻炼信息的仓库实例，由 Firebase Firestore 和 Storage 及 Room 数据库支持。
    @Provides
    @Singleton
    fun provideWorkoutRepository(
        database: FirebaseFirestore,
        storage: FirebaseStorage,
        hashtagRepository: IHashtagRepository,
        appDatabase: AppDatabase,
        exerciseApi: ExerciseApi
    ): IWorkoutRepository {
        return WorkoutRepository(database, storage, appDatabase, hashtagRepository, exerciseApi)
    }

    // 应用上下文提供者方法
    // 提供应用程序上下文: 避免在组件中多次注入上下文，通过此方法提供单例上下文以供全局使用。
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }


    // 社交动态仓库提供者方法，用于社交动态功能
    // 提供动态数据仓库: 为帖子和动态提供数据的管理和存取功能。
    @Provides
    @Singleton
    fun provideFeedRepository(
        database: FirebaseFirestore,
    ): IFeedRepository {
        return FeedRepository(database,)
    }

    // 练习下载偏好提供者方法，用于管理练习下载偏好
    // 提供用于管理锻炼下载偏好的实例: 注入上下文以创建偏好对象。
    @Provides
    @Singleton
    fun provideExerciseDownloadPrefs(
        @ApplicationContext context: Context
    ): ExerciseDownloadPrefs {
        return ExerciseDownloadPrefs(context)
    }

    // 练习存储库提供者方法，包含 API、数据库、调度器、上下文和偏好等多个依赖

    //@Provides 注解: 该注解标记该方法为一个提供器，Dagger/Hilt 会调用这个方法以获取对应类型的实例。在这里是 IExerciseRepository 的实例。
    //@Singleton 注解: 指定返回的实例是单例的，确保在整个应用程序生命周期内只存在一个 IExerciseRepository 的实例。这在管理状态和资源时非常有用，避免不必要的重复加载和开销。
    @Provides
    @Singleton

    //方法签名: provideExerciseRepository 是该提供器的方法名称。它接收多个参数，返回一个 IExerciseRepository 接口的实现。
    //接收的参数:
    //exerciseApi: ExerciseApi: 这是一个接口，负责与后端 API 进行交互。Dagger/Hilt 会自动提供该实例（假设它已被正确声明为可注入的）。
    //appDatabase: AppDatabase: Room 数据库实例，用于直接与本地数据进行交互。
    //@IoDispatcher ioDispatcher: CoroutineDispatcher: 这是一个调度器实例，通常用于处理 IO 操作，以避免阻塞主线程。
    //context: Context: Android 上下文，提供应用程序的环境信息和资源访问。
    //exerciseDownloadPrefs: ExerciseDownloadPrefs: 包含下载偏好的对象，用于跟踪用户的下载状态或偏好设置。
    fun provideExerciseRepository(
        exerciseApi: ExerciseApi,
        appDatabase: AppDatabase,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        context: Context,
        exerciseDownloadPrefs: ExerciseDownloadPrefs
    ): IExerciseRepository {
        //实例化 ExerciseRepository: 方法的返回值是通过调用 ExerciseRepository 的构造函数创建的实例。
        //参数传递: 将在方法参数中提供的依赖注入到 ExerciseRepository 的构造函数中。
        //appDatabase: 传入数据库实例，以便 ExerciseRepository 可以访问数据。
        //exerciseApi: 传入用于 API 交互的接口。
        //ioDispatcher: 传入以处理协程任务所用的调度器，以确保数据库操作不会阻塞 UI 线程。
        //context: 传入 Android 上下文以进行操作，尤其是涉及 Android 资源访问的部分。
        //exerciseDownloadPrefs: 传入用户的下载偏好设置，以记录和管理下载状态。
        return ExerciseRepository(
            appDatabase, exerciseApi,
            ioDispatcher = ioDispatcher,
            context = context,
            exerciseDownloadPrefs = exerciseDownloadPrefs
        )
    }

    // Firebase 存储提供者方法
    // 提供 Firebase 存储: 为 Firebase 存储服务提供单例实例，以访问图片和文件数据。
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    // IO 调度器提供者方法，返回 Dispatchers.IO 以进行后台操作
    // 提供 IO 调度器: 返回一个用于处理输入输出操作的调度器，确保这类操作不会阻塞主线程。
    @Provides
    @IoDispatcher
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO


    // ExerciseDataManager 提供方法用于管理锻炼数据操作
    // 提供锻炼数据管理器: 创建数据管理类，用于处理与 Exercise 相关的逻辑和操作。
    @Provides
    @Singleton
    fun provideExerciseDataManager(
        exerciseRepository: IExerciseRepository,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): ExerciseDataManager = ExerciseDataManager(
        exerciseRepository,
        ioDispatcher
    )


    // CommentsRepository 提供方法用于管理帖子评论
    // 提供评论管理仓库: 返回评论相关的仓库实例。
    @Provides
    @Singleton
    fun provideCommentsRepository(
        firestore: FirebaseFirestore,
    ): ICommentsRepository {
        return CommentsRepository(
            firestore = firestore,
        )
    }


    // LikeRepository 提供方法，包含 Firestore、用户仓库和本地数据库依赖
    // 提供点赞管理仓库: 返回用于处理点赞逻辑和操作的仓库实例。
    @Provides
    @Singleton
    fun provideLikeRepository(
        firestore: FirebaseFirestore,
        userRepository: IUserRepository,
        appDatabase: AppDatabase
    ): ILikeRepository {
        return LikeRepository(
            firestore = firestore,
            userRepository = userRepository,
            appDatabase = appDatabase
        )
    }

}

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher