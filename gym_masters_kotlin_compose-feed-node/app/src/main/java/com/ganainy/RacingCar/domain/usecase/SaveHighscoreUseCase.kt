package com.ganainy.racingcar.domain.usecase

import com.ganainy.racingcar.domain.repo.HighscoreRepository
import com.ganainy.racingcar.ui.viewmodel.MainViewModel
import com.ganainy.racingcar.utils.SoundRepository
import kotlinx.coroutines.flow.first

class SaveHighscoreUseCase(
    private val highscoreRepository: HighscoreRepository,
    private val soundRepository: SoundRepository
) {

    suspend fun execute(score: Int) {
        val currentHighscore = highscoreRepository.getHighScore().first()
        if (score > currentHighscore) {
            highscoreRepository.saveHighScore(score)
            soundRepository.playSound(MainViewModel.NEW_HIGHSCORE_SOUND_ID)
        }
    }
}