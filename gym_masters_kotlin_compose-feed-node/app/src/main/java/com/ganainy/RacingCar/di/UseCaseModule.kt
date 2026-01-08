package com.ganainy.racingcar.di

import com.ganainy.racingcar.domain.repo.HighscoreRepository
import com.ganainy.racingcar.domain.usecase.GetHighscoreUseCase
import com.ganainy.racingcar.domain.usecase.SaveHighscoreUseCase
import com.ganainy.racingcar.utils.SoundRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun providesGetHighscoreUseCase(
        highscoreRepository: HighscoreRepository
    ): GetHighscoreUseCase {
        return GetHighscoreUseCase(highscoreRepository)
    }

    @Provides
    @Singleton
    fun providesSaveHighscoreUseCase(
        highscoreRepository: HighscoreRepository,
        soundRepository: SoundRepository,
    ): SaveHighscoreUseCase {
        return SaveHighscoreUseCase(highscoreRepository, soundRepository)
    }
}